#!/usr/bin/env bash
set -euo pipefail

# End-to-end flow for:
# - ms-clients
# - ms-accounts
# - ms-reportes
#
# Usage:
#   chmod +x infra/scripts/e2e-full-flow.sh
#   ./infra/scripts/e2e-full-flow.sh
#
# Optional env vars:
#   CLIENTS_BASE_URL=http://localhost:8082
#   ACCOUNTS_BASE_URL=http://localhost:8081
#   REPORTES_BASE_URL=http://localhost:8080

CLIENTS_BASE_URL="${CLIENTS_BASE_URL:-http://localhost:8082}"
ACCOUNTS_BASE_URL="${ACCOUNTS_BASE_URL:-http://localhost:8081}"
REPORTES_BASE_URL="${REPORTES_BASE_URL:-http://localhost:8080}"

LAST_BODY=""

log() {
  printf '\n[%s] %s\n' "$(date '+%H:%M:%S')" "$1"
}

fail() {
  echo "ERROR: $1" >&2
  exit 1
}

request() {
  local name="$1"
  local method="$2"
  local url="$3"
  local expected_status="$4"
  local data="${5:-}"
  shift 5 || true

  local response
  if [[ -n "${data}" ]]; then
    response="$(curl -sS -X "${method}" "${url}" -H "Content-Type: application/json" "$@" -d "${data}" -w $'\n%{http_code}')"
  else
    response="$(curl -sS -X "${method}" "${url}" "$@" -w $'\n%{http_code}')"
  fi

  local status="${response##*$'\n'}"
  local body="${response%$'\n'*}"
  LAST_BODY="${body}"

  if [[ "${status}" != "${expected_status}" ]]; then
    echo "---- ${name} failed ----" >&2
    echo "Expected status: ${expected_status}" >&2
    echo "Actual status:   ${status}" >&2
    echo "URL: ${method} ${url}" >&2
    echo "Body: ${body}" >&2
    exit 1
  fi

  echo "OK ${name} -> ${status}"
}

assert_body_contains() {
  local needle="$1"
  if [[ "${LAST_BODY}" != *"${needle}"* ]]; then
    echo "---- Body assertion failed ----" >&2
    echo "Expected to find: ${needle}" >&2
    echo "Body: ${LAST_BODY}" >&2
    exit 1
  fi
}

wait_for_report() {
  local cliente_id="$1"
  local cuenta_id="$2"
  local fecha_inicio="$3"
  local fecha_fin="$4"

  local max_attempts=20
  local attempt=1
  local url="${REPORTES_BASE_URL}/api/reportes?clienteId=${cliente_id}&fechaInicio=${fecha_inicio}&fechaFin=${fecha_fin}&page=0&size=20"

  while [[ "${attempt}" -le "${max_attempts}" ]]; do
    local response
    response="$(curl -sS "${url}" -H "X-Correlation-ID: e2e-${cliente_id}" -w $'\n%{http_code}')"
    local status="${response##*$'\n'}"
    local body="${response%$'\n'*}"

    if [[ "${status}" == "200" && "${body}" == *"${cliente_id}"* && "${body}" == *"${cuenta_id}"* ]]; then
      echo "OK reporte listo -> 200 (intento ${attempt})"
      LAST_BODY="${body}"
      return 0
    fi

    echo "Esperando reporte... intento ${attempt}/${max_attempts} (status=${status})"
    sleep 2
    attempt=$((attempt + 1))
  done

  fail "ms-reportes no devolvio el flujo esperado para cliente=${cliente_id} cuenta=${cuenta_id}"
}

main() {
  local ts
  ts="$(date +%s)"

  local persona_id="per-${ts}"
  local cliente_id="cli-${ts}"
  local cuenta_id="acc-${ts}"
  local numero_cuenta="nc-${ts}"
  local mov_dep_id="mov-dep-${ts}"
  local mov_ret_id="mov-ret-${ts}"
  local tx_dep="tx-dep-${ts}"
  local tx_ret="tx-ret-${ts}"
  local fecha_inicio
  local fecha_fin
  fecha_inicio="$(date -d '-1 day' '+%Y-%m-%d')"
  fecha_fin="$(date -d '+1 day' '+%Y-%m-%d')"

  log "Smoke checks de servicios"
  request "ms-clients listar personas" "GET" "${CLIENTS_BASE_URL}/api/personas" "200" ""
  request "ms-clients listar clientes" "GET" "${CLIENTS_BASE_URL}/api/clientes" "200" ""
  request "ms-accounts listar cuentas" "GET" "${ACCOUNTS_BASE_URL}/api/cuentas" "200" ""

  log "Flujo personas/clientes"
  request "crear persona" "POST" "${CLIENTS_BASE_URL}/api/personas" "201" \
    "{\"identificacion\":\"${persona_id}\",\"nombre\":\"Juan E2E\",\"genero\":\"M\",\"edad\":30,\"direccion\":\"Calle E2E\",\"telefono\":\"099123456\"}"
  assert_body_contains "\"identificacion\":\"${persona_id}\""

  request "obtener persona" "GET" "${CLIENTS_BASE_URL}/api/personas/${persona_id}" "200" ""
  assert_body_contains "\"identificacion\":\"${persona_id}\""

  request "crear cliente" "POST" "${CLIENTS_BASE_URL}/api/clientes" "201" \
    "{\"clienteId\":\"${cliente_id}\",\"identificacion\":\"${persona_id}\",\"nombre\":\"Juan E2E\",\"genero\":\"M\",\"edad\":30,\"direccion\":\"Calle E2E\",\"telefono\":\"099123456\",\"contrasena\":\"password123\"}"
  assert_body_contains "\"clienteId\":\"${cliente_id}\""

  request "obtener cliente" "GET" "${CLIENTS_BASE_URL}/api/clientes/${cliente_id}" "200" ""
  assert_body_contains "\"clienteId\":\"${cliente_id}\""

  request "actualizar cliente" "PUT" "${CLIENTS_BASE_URL}/api/clientes/${cliente_id}" "200" \
    "{\"identificacion\":\"${persona_id}\",\"nombre\":\"Juan E2E Update\",\"genero\":\"M\",\"edad\":31,\"direccion\":\"Calle E2E 2\",\"telefono\":\"099123457\",\"contrasena\":\"password456\",\"estado\":\"ACTIVE\"}"
  assert_body_contains "\"nombre\":\"Juan E2E Update\""

  request "duplicado cliente debe fallar" "POST" "${CLIENTS_BASE_URL}/api/clientes" "409" \
    "{\"clienteId\":\"${cliente_id}\",\"identificacion\":\"${persona_id}\",\"nombre\":\"Juan E2E\",\"genero\":\"M\",\"edad\":30,\"direccion\":\"Calle E2E\",\"telefono\":\"099123456\",\"contrasena\":\"password123\"}"

  log "Validacion de read model cliente en ms-accounts"
  request "cuenta con cliente inexistente debe retornar 404" "POST" "${ACCOUNTS_BASE_URL}/api/cuentas" "404" \
    "{\"cuentaId\":\"acc-fantasma-${ts}\",\"numeroCuenta\":\"nc-fantasma-${ts}\",\"tipoCuenta\":\"AHORROS\",\"saldo\":0.0,\"clienteId\":\"cli-fantasma-${ts}\",\"estado\":\"ACTIVE\"}"
  assert_body_contains "Cliente no existe"

  echo "Esperando propagacion del cliente al cache de ms-accounts (outbox relay ~1s + consumer)..."
  sleep 3

  log "Flujo cuentas/movimientos"
  request "crear cuenta" "POST" "${ACCOUNTS_BASE_URL}/api/cuentas" "201" \
    "{\"cuentaId\":\"${cuenta_id}\",\"numeroCuenta\":\"${numero_cuenta}\",\"tipoCuenta\":\"AHORROS\",\"saldo\":0.0,\"clienteId\":\"${cliente_id}\",\"estado\":\"ACTIVE\"}"
  assert_body_contains "\"cuentaId\":\"${cuenta_id}\""

  request "obtener cuenta por id" "GET" "${ACCOUNTS_BASE_URL}/api/cuentas/${cuenta_id}" "200" ""
  assert_body_contains "\"cuentaId\":\"${cuenta_id}\""

  request "obtener cuentas por cliente" "GET" "${ACCOUNTS_BASE_URL}/api/cuentas/cliente/${cliente_id}" "200" ""
  assert_body_contains "\"cuentaId\":\"${cuenta_id}\""

  request "actualizar estado cuenta" "PATCH" "${ACCOUNTS_BASE_URL}/api/cuentas/${cuenta_id}" "200" \
    "{\"estado\":\"ACTIVE\"}"
  assert_body_contains "\"estado\":\"ACTIVE\""

  request "registrar deposito" "POST" "${ACCOUNTS_BASE_URL}/api/movimientos" "201" \
    "{\"movimientoId\":\"${mov_dep_id}\",\"cuentaId\":\"${cuenta_id}\",\"tipoMovimiento\":\"DEPOSITO\",\"valor\":200.0}" \
    -H "X-Transaction-Id: ${tx_dep}"
  assert_body_contains "\"movimientoId\":\"${mov_dep_id}\""

  request "registrar retiro" "POST" "${ACCOUNTS_BASE_URL}/api/movimientos" "201" \
    "{\"movimientoId\":\"${mov_ret_id}\",\"cuentaId\":\"${cuenta_id}\",\"tipoMovimiento\":\"RETIRO\",\"valor\":200.0}" \
    -H "X-Transaction-Id: ${tx_ret}"
  assert_body_contains "\"movimientoId\":\"${mov_ret_id}\""

  log "Validacion de reporte (espera por consumo Kafka)"
  wait_for_report "${cliente_id}" "${cuenta_id}" "${fecha_inicio}" "${fecha_fin}"

  log "Cierre de flujo"
  request "eliminar cuenta" "DELETE" "${ACCOUNTS_BASE_URL}/api/cuentas/${cuenta_id}" "204" ""
  request "eliminar cliente" "DELETE" "${CLIENTS_BASE_URL}/api/clientes/${cliente_id}" "204" ""

  log "E2E COMPLETADO OK"
}

main "$@"
