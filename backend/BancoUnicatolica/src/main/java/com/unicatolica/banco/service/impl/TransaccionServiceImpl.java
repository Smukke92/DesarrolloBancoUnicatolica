package com.unicatolica.banco.service.impl;

import com.unicatolica.banco.dao.ClienteDAO;
import com.unicatolica.banco.dao.CuentaDAO;
import com.unicatolica.banco.dao.TransaccionDAO;
import com.unicatolica.banco.dto.*;
import com.unicatolica.banco.model.Cliente;
import com.unicatolica.banco.model.Cuenta;
import com.unicatolica.banco.model.Transaccion;
import com.unicatolica.banco.service.TransaccionService;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class TransaccionServiceImpl implements TransaccionService {

    private final TransaccionDAO transaccionDAO;
    private final CuentaDAO cuentaDAO;
    private final ClienteDAO clienteDAO;

    public TransaccionServiceImpl(TransaccionDAO transaccionDAO, CuentaDAO cuentaDAO, ClienteDAO clienteDAO) {
        this.transaccionDAO = transaccionDAO;
        this.cuentaDAO = cuentaDAO;
        this.clienteDAO = clienteDAO;
    }

    public TransaccionServiceImpl() {
        this.transaccionDAO = new TransaccionDAO();
        this.cuentaDAO = new CuentaDAO();
        this.clienteDAO = new ClienteDAO();
    }

    @Override
    public CuentaDTO consultarCuentaParaTransaccion(String numeroCuenta) {
        if (esNuloOVacio(numeroCuenta)) {
            throw new IllegalArgumentException("NUMERO_CUENTA_INVALIDO");
        }

        try {
            Cuenta cuenta = cuentaDAO.buscarPorNumero(numeroCuenta.trim());
            if (cuenta == null) {
                throw new IllegalArgumentException("CUENTA_NO_ENCONTRADA");
            }

            Cliente cliente = clienteDAO.buscarPorId(cuenta.getIdCliente());
            String nombreCliente = (cliente != null) ? cliente.getNombreCliente() : "Desconocido";

            CuentaDTO dto = new CuentaDTO();
            dto.setIdCuenta(cuenta.getIdCuenta());
            dto.setNumeroCuenta(cuenta.getNumeroCuenta());
            dto.setTipoCuenta(cuenta.getTipoCuenta());
            dto.setEstado(cuenta.getEstado());
            dto.setSaldo(cuenta.getSaldo());
            dto.setIdCliente(cuenta.getIdCliente());
            dto.setNombreCliente(nombreCliente);

            return dto;

        } catch (SQLException | java.io.IOException e) {
            throw new RuntimeException("ERROR_BASE_DATOS: " + e.getMessage(), e);
        }
    }

    @Override
    public ResultadoTransaccionDTO registrarDeposito(String numeroCuenta, BigDecimal monto) {
        return ejecutarOperacion("DEPOSITO", numeroCuenta, monto);
    }

    @Override
    public ResultadoTransaccionDTO registrarRetiro(String numeroCuenta, BigDecimal monto) {
        return ejecutarOperacion("RETIRO", numeroCuenta, monto);
    }

    private ResultadoTransaccionDTO ejecutarOperacion(String tipo, String numeroCuenta, BigDecimal monto) {
        if (esNuloOVacio(numeroCuenta)) {
            throw new IllegalArgumentException("CUENTA_NO_ENCONTRADA");
        }

        if (monto == null || monto.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("MONTO_INVALIDO");
        }

        try {
            // Consultar saldo inicial previo al depósito/retiro
            Cuenta cuentaAntes = cuentaDAO.buscarPorNumero(numeroCuenta.trim());
            if (cuentaAntes == null) {
                throw new IllegalArgumentException("CUENTA_NO_ENCONTRADA");
            }

            BigDecimal saldoAnterior = cuentaAntes.getSaldo();

            // Ejecutar la operación atómica (bloqueo FOR UPDATE, validación ACTIVA, inserción y update de saldo)
            Transaccion transaccion;
            if ("DEPOSITO".equalsIgnoreCase(tipo)) {
                transaccion = transaccionDAO.registrarDeposito(numeroCuenta.trim(), monto);
            } else {
                transaccion = transaccionDAO.registrarRetiro(numeroCuenta.trim(), monto);
            }

            // Consultar el nuevo saldo actualizado
            Cuenta cuentaDespues = cuentaDAO.buscarPorNumero(numeroCuenta.trim());
            BigDecimal saldoNuevo = cuentaDespues.getSaldo();

            ResultadoTransaccionDTO resultado = new ResultadoTransaccionDTO();

            // Linea 107: Conversión explicita a Integer para evitar incompatibilidad de tipos
            resultado.setIdTransaccion(transaccion.getIdTransaccion());

            resultado.setNumeroCuenta(numeroCuenta.trim());
            resultado.setTipoTransaccion(transaccion.getTipo());
            resultado.setMonto(transaccion.getMonto());
            resultado.setSaldoAnterior(saldoAnterior);
            resultado.setSaldoNuevo(saldoNuevo);

            // Linea 113: Garantizar asignación de LocalDateTime
            if (transaccion.getFechaHora() != null) {
                resultado.setFechaHora(transaccion.getFechaHora());
            } else {
                resultado.setFechaHora(java.time.LocalDateTime.now());
            }

            return resultado;

        } catch (SQLException e) {
            // Mapear excepciones enviadas por el DAO
            String msg = e.getMessage();
            if ("CUENTA_NO_ENCONTRADA".equals(msg) ||
                "CUENTA_INACTIVA".equals(msg) ||
                "MONTO_INVALIDO".equals(msg) ||
                "FONDOS_INSUFICIENTES".equals(msg)) {
                throw new IllegalArgumentException(msg);
            }
            throw new RuntimeException("ERROR_BASE_DATOS: " + msg, e);
        } catch (java.io.IOException e) {
            throw new RuntimeException("ERROR_BASE_DATOS: " + e.getMessage(), e);
        }
    }

    @Override
    public List<TransaccionDTO> listarTransaccionesPorCuenta(String numeroCuenta) {
        if (esNuloOVacio(numeroCuenta)) {
            throw new IllegalArgumentException("NUMERO_CUENTA_INVALIDO");
        }

        try {
            Cuenta cuenta = cuentaDAO.buscarPorNumero(numeroCuenta.trim());
            if (cuenta == null) {
                throw new IllegalArgumentException("CUENTA_NO_ENCONTRADA");
            }

            List<Transaccion> transacciones = transaccionDAO.listarPorNumeroCuenta(numeroCuenta.trim());
            List<TransaccionDTO> resultado = new ArrayList<>();

            // REEMPLAZA EL BUCLE EN listarTransaccionesPorCuenta POR ESTE:
            for (Transaccion t : transacciones) {
                TransaccionDTO dto = new TransaccionDTO();
                dto.setIdTransaccion(t.getIdTransaccion());
                dto.setFechaHora(t.getFechaHora()); // Directo LocalDateTime a LocalDateTime
                dto.setTipoTransaccion(t.getTipo());
                dto.setMonto(t.getMonto());
                dto.setIdCuenta(t.getIdCuenta());
                resultado.add(dto);
            }

            return resultado;

        } catch (SQLException | java.io.IOException e) {
            throw new RuntimeException("ERROR_BASE_DATOS: " + e.getMessage(), e);
        }
    }

    @Override
    public EstadoCuentaDTO consultarEstadoCuenta(String numeroCuenta) {
        if (esNuloOVacio(numeroCuenta)) {
            throw new IllegalArgumentException("NUMERO_CUENTA_INVALIDO");
        }

        try {
            Cuenta cuenta = cuentaDAO.buscarPorNumero(numeroCuenta.trim());
            if (cuenta == null) {
                throw new IllegalArgumentException("CUENTA_NO_ENCONTRADA");
            }

            Cliente cliente = clienteDAO.buscarPorId(cuenta.getIdCliente());

            EstadoCuentaDTO estadoDTO = new EstadoCuentaDTO();
            estadoDTO.setNumeroCuenta(cuenta.getNumeroCuenta());
            estadoDTO.setTipoCuenta(cuenta.getTipoCuenta());
            estadoDTO.setEstado(cuenta.getEstado());
            estadoDTO.setSaldo(cuenta.getSaldo());

            if (cliente != null) {
                estadoDTO.setNombreCliente(cliente.getNombreCliente());
                estadoDTO.setCedulaCliente(cliente.getCedula());
            } else {
                estadoDTO.setNombreCliente("Desconocido");
                estadoDTO.setCedulaCliente("Desconocido");
            }

            List<TransaccionDTO> historial = listarTransaccionesPorCuenta(numeroCuenta.trim());
            estadoDTO.setListaTransacciones(historial);

            return estadoDTO;

        } catch (SQLException | java.io.IOException e) {
            throw new RuntimeException("ERROR_BASE_DATOS: " + e.getMessage(), e);
        }
    }

    private boolean esNuloOVacio(String str) {
        return str == null || str.trim().isEmpty();
    }
}
