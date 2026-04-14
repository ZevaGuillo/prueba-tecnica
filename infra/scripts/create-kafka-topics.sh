#!/bin/sh
set -eu

BOOTSTRAP_SERVERS="${KAFKA_BOOTSTRAP_SERVERS:-kafka:9092}"

TOPICS="
cuenta-creada
cuenta-actualizada
movimiento-registrado
cliente-events
cuenta-events
movimiento-events
"

echo "Waiting for Kafka broker at ${BOOTSTRAP_SERVERS}..."
until timeout 5 kafka-topics --bootstrap-server "${BOOTSTRAP_SERVERS}" --list >/dev/null 2>&1; do
  echo "Kafka not ready yet, retrying..."
  sleep 2
done

echo "Kafka is ready. Creating topics if missing..."
echo "${TOPICS}" | while IFS= read -r topic; do
  [ -z "${topic}" ] && continue
  kafka-topics \
    --bootstrap-server "${BOOTSTRAP_SERVERS}" \
    --create \
    --if-not-exists \
    --topic "${topic}" \
    --partitions 3 \
    --replication-factor 1
done

echo "Topic initialization complete."
