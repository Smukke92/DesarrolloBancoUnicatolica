package com.unicatolica.banco.service;

import com.unicatolica.banco.dto.CuentaDTO;
import com.unicatolica.banco.dto.EstadoCuentaDTO;
import com.unicatolica.banco.dto.ResultadoTransaccionDTO;
import com.unicatolica.banco.dto.TransaccionDTO;

import java.math.BigDecimal;
import java.util.List;

public interface TransaccionService {
    CuentaDTO consultarCuentaParaTransaccion(String numeroCuenta);
    ResultadoTransaccionDTO registrarDeposito(String numeroCuenta, BigDecimal monto);
    ResultadoTransaccionDTO registrarRetiro(String numeroCuenta, BigDecimal monto);
    List<TransaccionDTO> listarTransaccionesPorCuenta(String numeroCuenta);
    EstadoCuentaDTO consultarEstadoCuenta(String numeroCuenta);
}