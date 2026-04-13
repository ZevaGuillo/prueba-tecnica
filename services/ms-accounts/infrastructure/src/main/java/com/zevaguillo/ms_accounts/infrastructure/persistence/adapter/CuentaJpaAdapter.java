package com.zevaguillo.ms_accounts.infrastructure.persistence.adapter;

import com.zevaguillo.ms_accounts.application.port.out.CuentaPersistencePort;
import com.zevaguillo.ms_accounts.domain.model.Cuenta;
import com.zevaguillo.ms_accounts.infrastructure.persistence.entity.CuentaEntity;
import com.zevaguillo.ms_accounts.infrastructure.persistence.entity.CuentaRepository;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class CuentaJpaAdapter implements CuentaPersistencePort {

    private final CuentaRepository repository;

    public CuentaJpaAdapter(CuentaRepository repository) {
        this.repository = repository;
    }

    @Override
    public Cuenta save(Cuenta cuenta) {
        CuentaEntity entity = toEntity(cuenta);
        CuentaEntity saved = repository.save(entity);
        return toDomain(saved);
    }

    @Override
    public Optional<Cuenta> findById(String cuentaId) {
        return repository.findById(cuentaId).map(this::toDomain);
    }

    @Override
    public List<Cuenta> findAll() {
        return repository.findAll().stream().map(this::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<Cuenta> findByClienteId(String clienteId) {
        return repository.findByClienteId(clienteId).stream().map(this::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<Cuenta> findByEstado(String estado) {
        return repository.findByEstado(estado).stream().map(this::toDomain).collect(Collectors.toList());
    }

    @Override
    public boolean existsByNumeroCuenta(String numeroCuenta) {
        return repository.existsByNumeroCuenta(numeroCuenta);
    }

    @Override
    public boolean existsById(String cuentaId) {
        return repository.existsById(cuentaId);
    }

    @Override
    public void delete(String cuentaId) {
        repository.deleteById(cuentaId);
    }

    private CuentaEntity toEntity(Cuenta cuenta) {
        CuentaEntity entity = new CuentaEntity();
        entity.setCuentaId(cuenta.getCuentaId());
        entity.setNumeroCuenta(cuenta.getNumeroCuenta());
        entity.setTipoCuenta(cuenta.getTipoCuenta());
        entity.setSaldo(cuenta.getSaldo());
        entity.setEstado(cuenta.getEstado());
        entity.setClienteId(cuenta.getClienteId());
        entity.setVersion(cuenta.getVersion());
        return entity;
    }

    private Cuenta toDomain(CuentaEntity entity) {
        Cuenta cuenta = new Cuenta();
        cuenta.setCuentaId(entity.getCuentaId());
        cuenta.setNumeroCuenta(entity.getNumeroCuenta());
        cuenta.setTipoCuenta(entity.getTipoCuenta());
        cuenta.setSaldo(entity.getSaldo());
        cuenta.setEstado(entity.getEstado());
        cuenta.setClienteId(entity.getClienteId());
        cuenta.setVersion(entity.getVersion());
        return cuenta;
    }
}