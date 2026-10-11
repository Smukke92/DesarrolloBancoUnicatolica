package com.unicatolica.banco.service.impl;

import com.unicatolica.banco.dao.ClienteDAO;
import com.unicatolica.banco.dao.CuentaDAO;
import com.unicatolica.banco.dto.CuentaDTO;
import com.unicatolica.banco.dto.FiltroCuentaDTO;
import com.unicatolica.banco.model.Cliente;
import com.unicatolica.banco.model.Cuenta;
import com.unicatolica.banco.model.enums.EstadoCuenta;
import com.unicatolica.banco.service.CuentaService;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class CuentaServiceImpl implements CuentaService {

    private final CuentaDAO cuentaDAO;
    private final ClienteDAO clienteDAO;

    public CuentaServiceImpl(CuentaDAO cuentaDAO, ClienteDAO clienteDAO) {
        this.cuentaDAO = cuentaDAO;
        this.clienteDAO = clienteDAO;
    }

    public CuentaServiceImpl() {
        this.cuentaDAO = new CuentaDAO();
        this.clienteDAO = new ClienteDAO();
    }

    @Override
    public CuentaDTO registrarCuenta(CuentaDTO cuentaDTO) {
        if (cuentaDTO == null) {
            throw new IllegalArgumentException("CUENTA_NO_VALIDA");
        }

        if (cuentaDTO.getIdCliente() == null || cuentaDTO.getIdCliente() <= 0) {
            throw new IllegalArgumentException("CLIENTE_NO_ENCONTRADO");
        }

        try {
            Cliente cliente = clienteDAO.buscarPorId(cuentaDTO.getIdCliente());
            if (cliente == null) {
                throw new IllegalArgumentException("CLIENTE_NO_ENCONTRADO");
            }

            if (esNuloOVacio(cuentaDTO.getNumeroCuenta())) {
                throw new IllegalArgumentException("NUMERO_CUENTA_INVALIDO");
            }

            if (cuentaDAO.existePorNumeroCuenta(cuentaDTO.getNumeroCuenta().trim())) {
                throw new IllegalArgumentException("NUMERO_CUENTA_DUPLICADO");
            }

            if (esNuloOVacio(cuentaDTO.getTipoCuenta()) || 
               (!cuentaDTO.getTipoCuenta().equalsIgnoreCase("AHORROS") && 
                !cuentaDTO.getTipoCuenta().equalsIgnoreCase("CORRIENTE"))) {
                throw new IllegalArgumentException("TIPO_CUENTA_NO_VALIDO");
            }

            Cuenta cuenta = new Cuenta();
            cuenta.setNumeroCuenta(cuentaDTO.getNumeroCuenta().trim());
            cuenta.setIdCliente(cuentaDTO.getIdCliente());
            cuenta.setTipoCuenta(cuentaDTO.getTipoCuenta().toUpperCase().trim());
            cuenta.setSaldo(BigDecimal.ZERO);
            cuenta.setEstado(EstadoCuenta.ACTIVA.name());
            cuenta.setFechaHoraCreacion(LocalDateTime.now());

            boolean guardado = cuentaDAO.registrar(cuenta);
            if (!guardado) {
                throw new RuntimeException("ERROR_REGISTRANDO_CUENTA");
            }

            return mapearADTO(cuenta, cliente.getNombreCliente());

        } catch (SQLException | java.io.IOException e) {
            throw new RuntimeException("ERROR_BASE_DATOS: " + e.getMessage(), e);
        }
    }

    @Override
    public List<CuentaDTO> listarCuentas() {
        try {
            List<Cuenta> cuentas = cuentaDAO.listar(); // Usa listar() de tu DAO
            List<CuentaDTO> resultado = new ArrayList<>();

            for (Cuenta c : cuentas) {
                Cliente cliente = clienteDAO.buscarPorId(c.getIdCliente());
                String nombreCliente = (cliente != null) ? cliente.getNombreCliente() : "Desconocido";
                resultado.add(mapearADTO(c, nombreCliente));
            }

            return resultado;

        } catch (SQLException | java.io.IOException e) {
            throw new RuntimeException("ERROR_BASE_DATOS: " + e.getMessage(), e);
        }
    }

    @Override
    public List<CuentaDTO> buscarCuentas(FiltroCuentaDTO filtro) {
        try {
            String numCuenta = (filtro != null) ? filtro.getNumeroCuenta() : null;
            String tipoCuenta = (filtro != null) ? filtro.getTipoCuenta() : null;
            Integer idCliente = (filtro != null) ? filtro.getIdCliente() : null;

            List<Cuenta> cuentas = cuentaDAO.buscar(numCuenta, tipoCuenta, idCliente); // Usa buscar() de tu DAO
            List<CuentaDTO> resultado = new ArrayList<>();

            for (Cuenta c : cuentas) {
                Cliente cliente = clienteDAO.buscarPorId(c.getIdCliente());
                String nombreCliente = (cliente != null) ? cliente.getNombreCliente() : "Desconocido";
                resultado.add(mapearADTO(c, nombreCliente));
            }

            return resultado;

        } catch (SQLException | java.io.IOException e) {
            throw new RuntimeException("ERROR_BASE_DATOS: " + e.getMessage(), e);
        }
    }

    @Override
    public CuentaDTO obtenerCuentaPorNumero(String numeroCuenta) {
        if (esNuloOVacio(numeroCuenta)) {
            throw new IllegalArgumentException("NUMERO_CUENTA_INVALIDO");
        }

        try {
            Cuenta cuenta = cuentaDAO.buscarPorNumero(numeroCuenta.trim()); // Usa buscarPorNumero() de tu DAO
            if (cuenta == null) {
                throw new IllegalArgumentException("CUENTA_NO_ENCONTRADA");
            }

            Cliente cliente = clienteDAO.buscarPorId(cuenta.getIdCliente());
            String nombreCliente = (cliente != null) ? cliente.getNombreCliente() : "Desconocido";

            return mapearADTO(cuenta, nombreCliente);

        } catch (SQLException | java.io.IOException e) {
            throw new RuntimeException("ERROR_BASE_DATOS: " + e.getMessage(), e);
        }
    }

    @Override
    public CuentaDTO actualizarCuenta(CuentaDTO cuentaDTO) {
        if (cuentaDTO == null || cuentaDTO.getIdCuenta() == null || cuentaDTO.getIdCuenta() <= 0) {
            throw new IllegalArgumentException("CUENTA_NO_ENCONTRADA");
        }

        try {
            Cuenta cuentaExistente = cuentaDAO.buscarPorId(cuentaDTO.getIdCuenta());
            if (cuentaExistente == null) {
                throw new IllegalArgumentException("CUENTA_NO_ENCONTRADA");
            }

            if (!cuentaExistente.getNumeroCuenta().equalsIgnoreCase(cuentaDTO.getNumeroCuenta())) {
                throw new IllegalArgumentException("EL_NUMERO_DE_CUENTA_NO_SE_PUEDE_MODIFICAR");
            }

            if (esNuloOVacio(cuentaDTO.getTipoCuenta()) || 
               (!cuentaDTO.getTipoCuenta().equalsIgnoreCase("AHORROS") && 
                !cuentaDTO.getTipoCuenta().equalsIgnoreCase("CORRIENTE"))) {
                throw new IllegalArgumentException("TIPO_CUENTA_NO_VALIDO");
            }

            cuentaExistente.setTipoCuenta(cuentaDTO.getTipoCuenta().toUpperCase().trim());

            boolean actualizado = cuentaDAO.actualizar(cuentaExistente); // Usa actualizar() de tu DAO
            if (!actualizado) {
                throw new RuntimeException("ERROR_ACTUALIZANDO_CUENTA");
            }

            Cliente cliente = clienteDAO.buscarPorId(cuentaExistente.getIdCliente());
            String nombreCliente = (cliente != null) ? cliente.getNombreCliente() : "Desconocido";

            return mapearADTO(cuentaExistente, nombreCliente);

        } catch (SQLException | java.io.IOException e) {
            throw new RuntimeException("ERROR_BASE_DATOS: " + e.getMessage(), e);
        }
    }

    @Override
    public void cambiarEstadoCuenta(int cuentaId, EstadoCuenta estado) {
        if (cuentaId <= 0) {
            throw new IllegalArgumentException("CUENTA_NO_ENCONTRADA");
        }
        if (estado == null) {
            throw new IllegalArgumentException("ESTADO_CUENTA_NO_VALIDO");
        }

        try {
            Cuenta cuenta = cuentaDAO.buscarPorId(cuentaId);
            if (cuenta == null) {
                throw new IllegalArgumentException("CUENTA_NO_ENCONTRADA");
            }

            boolean actualizado = cuentaDAO.cambiarEstado(cuentaId, estado.name()); // Usa cambiarEstado() de tu DAO
            if (!actualizado) {
                throw new RuntimeException("ERROR_ACTUALIZANDO_ESTADO_CUENTA");
            }

        } catch (SQLException | java.io.IOException e) {
            throw new RuntimeException("ERROR_BASE_DATOS: " + e.getMessage(), e);
        }
    }

    @Override
    public void eliminarCuenta(int cuentaId) {
        if (cuentaId <= 0) {
            throw new IllegalArgumentException("CUENTA_NO_ENCONTRADA");
        }

        try {
            Cuenta cuenta = cuentaDAO.buscarPorId(cuentaId);
            if (cuenta == null) {
                throw new IllegalArgumentException("CUENTA_NO_ENCONTRADA");
            }

            if (cuenta.getSaldo() != null && cuenta.getSaldo().compareTo(BigDecimal.ZERO) > 0) {
                throw new IllegalArgumentException("CUENTA_NO_PUEDE_ELIMINARSE");
            }

            boolean eliminado = cuentaDAO.eliminar(cuentaId); // Usa eliminar() de tu DAO
            if (!eliminado) {
                throw new RuntimeException("ERROR_ELIMINANDO_CUENTA");
            }

        } catch (SQLException e) {
            if (e.getMessage() != null && e.getMessage().contains("CUENTA_NO_PUEDE_ELIMINARSE")) {
                throw new IllegalArgumentException("CUENTA_NO_PUEDE_ELIMINARSE");
            }
            throw new RuntimeException("ERROR_BASE_DATOS: " + e.getMessage(), e);
        } catch (java.io.IOException e) {
            throw new RuntimeException("ERROR_BASE_DATOS: " + e.getMessage(), e);
        }
    }

    private boolean esNuloOVacio(String str) {
        return str == null || str.trim().isEmpty();
    }

    private CuentaDTO mapearADTO(Cuenta cuenta, String nombreCliente) {
        CuentaDTO dto = new CuentaDTO();
        dto.setIdCuenta(cuenta.getIdCuenta());
        dto.setNumeroCuenta(cuenta.getNumeroCuenta());
        dto.setTipoCuenta(cuenta.getTipoCuenta());
        dto.setEstado(cuenta.getEstado());
        dto.setSaldo(cuenta.getSaldo());
        dto.setIdCliente(cuenta.getIdCliente());
        dto.setNombreCliente(nombreCliente);
        return dto;
    }
}