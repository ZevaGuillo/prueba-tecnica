package com.zevaguillo.infrastructure.persistence.entity;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface CuentaRepository extends JpaRepository<CuentaEntity, String> {
    boolean existsByNumeroCuenta(String numeroCuenta);
    List<CuentaEntity> findByClienteId(String clienteId);
    List<CuentaEntity> findByEstado(String estado);
    Optional<CuentaEntity> findByNumeroCuenta(String numeroCuenta);
}