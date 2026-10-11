package com.unicatolica.banco.service;

import com.unicatolica.banco.dto.CuentaDTO;
import com.unicatolica.banco.dto.FiltroCuentaDTO;
import com.unicatolica.banco.model.enums.EstadoCuenta;

import java.util.List;

public interface CuentaService {
    CuentaDTO registrarCuenta(CuentaDTO cuenta);
    List<CuentaDTO> listarCuentas();
    List<CuentaDTO> buscarCuentas(FiltroCuentaDTO filtro);
    CuentaDTO obtenerCuentaPorNumero(String numeroCuenta);
    CuentaDTO actualizarCuenta(CuentaDTO cuenta);
    void eliminarCuenta(int cuentaId);
    void cambiarEstadoCuenta(int cuentaId, EstadoCuenta estado);
}
