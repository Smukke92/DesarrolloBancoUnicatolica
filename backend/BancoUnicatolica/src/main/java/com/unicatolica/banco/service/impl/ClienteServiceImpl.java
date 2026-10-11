package com.unicatolica.banco.service.impl;

import com.unicatolica.banco.dao.ClienteDAO;
import com.unicatolica.banco.dto.ClienteDTO;
import com.unicatolica.banco.model.Cliente;
import com.unicatolica.banco.service.ClienteService;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class ClienteServiceImpl implements ClienteService {

    private final ClienteDAO clienteDAO;

    private static final Pattern EMAIL_PATTERN = 
            Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");

    public ClienteServiceImpl(ClienteDAO clienteDAO) {
        this.clienteDAO = clienteDAO;
    }

    public ClienteServiceImpl() {
        this.clienteDAO = new ClienteDAO();
    }

    @Override
    public ClienteDTO registrarCliente(ClienteDTO clienteDTO) {
        validarDatosCliente(clienteDTO);

        try {
            if (clienteDAO.existePorCedula(clienteDTO.getCedula())) {
                throw new IllegalArgumentException("IDENTIFICACION_DUPLICADA");
            }

            Cliente cliente = mapearADominio(clienteDTO);
            boolean guardado = clienteDAO.registrar(cliente);

            if (!guardado) {
                throw new RuntimeException("ERROR_REGISTRANDO_CLIENTE");
            }

            return mapearADTO(cliente);

        } catch (SQLException | java.io.IOException e) {
            throw new RuntimeException("ERROR_BASE_DATOS: " + e.getMessage(), e);
        }
    }

    @Override
    public List<ClienteDTO> listarClientes() {
        try {
            List<Cliente> clientes = clienteDAO.listarClientes();
            List<ClienteDTO> resultado = new ArrayList<>();

            for (Cliente c : clientes) {
                resultado.add(mapearADTO(c));
            }

            return resultado;

        } catch (SQLException | java.io.IOException e) {
            throw new RuntimeException("ERROR_BASE_DATOS: " + e.getMessage(), e);
        }
    }

    @Override
    public List<ClienteDTO> buscarClientes(String criterio) {
        if (criterio == null || criterio.trim().isEmpty()) {
            return listarClientes();
        }

        try {
            List<Cliente> clientes = clienteDAO.buscarClientes(criterio.trim());
            List<ClienteDTO> resultado = new ArrayList<>();

            for (Cliente c : clientes) {
                resultado.add(mapearADTO(c));
            }

            return resultado;

        } catch (SQLException | java.io.IOException e) {
            throw new RuntimeException("ERROR_BASE_DATOS: " + e.getMessage(), e);
        }
    }

    @Override
    public ClienteDTO actualizarCliente(ClienteDTO clienteDTO) {
        if (clienteDTO == null || clienteDTO.getIdCliente() == null) {
            throw new IllegalArgumentException("CLIENTE_NO_VALIDO");
        }

        validarDatosCliente(clienteDTO);

        try {
            Cliente clienteExistente = clienteDAO.buscarPorId(clienteDTO.getIdCliente());
            if (clienteExistente == null) {
                throw new IllegalArgumentException("CLIENTE_NO_ENCONTRADO");
            }

            // Regla de Negocio RN02: La cédula no se puede modificar
            if (!clienteExistente.getCedula().equals(clienteDTO.getCedula())) {
                throw new IllegalArgumentException("LA_IDENTIFICACION_NO_SE_PUEDE_MODIFICAR");
            }

            Cliente clienteAActualizar = mapearADominio(clienteDTO);
            boolean actualizado = clienteDAO.actualizarCliente(clienteAActualizar);

            if (!actualizado) {
                throw new RuntimeException("ERROR_ACTUALIZANDO_CLIENTE");
            }

            return mapearADTO(clienteAActualizar);

        } catch (SQLException | java.io.IOException e) {
            throw new RuntimeException("ERROR_BASE_DATOS: " + e.getMessage(), e);
        }
    }

    @Override
    public void eliminarCliente(int clienteId) { // <--- Firma corregida con int
        if (clienteId <= 0) {
            throw new IllegalArgumentException("CLIENTE_NO_VALIDO");
        }

        try {
            Cliente cliente = clienteDAO.buscarPorId(clienteId);
            if (cliente == null) {
                throw new IllegalArgumentException("CLIENTE_NO_ENCONTRADO");
            }

            // El DAO se encarga de validar si tiene cuentas vinculadas
            boolean eliminado = clienteDAO.eliminarCliente(clienteId);
            if (!eliminado) {
                throw new RuntimeException("ERROR_ELIMINANDO_CLIENTE");
            }

        } catch (SQLException e) {
            // Re-lanzar las excepciones de negocio del DAO
            if ("CLIENTE_TIENE_CUENTAS".equals(e.getMessage())) {
                throw new IllegalArgumentException("CLIENTE_TIENE_CUENTAS");
            }
            throw new RuntimeException("ERROR_BASE_DATOS: " + e.getMessage(), e);
        } catch (java.io.IOException e) {
            throw new RuntimeException("ERROR_BASE_DATOS: " + e.getMessage(), e);
        }
    }

    // --- MÉTODOS AUXILIARES Y MAPEOS ---

    private void validarDatosCliente(ClienteDTO cliente) {
        if (cliente == null) {
            throw new IllegalArgumentException("CLIENTE_NO_VALIDO");
        }
        if (esNuloOVacio(cliente.getCedula())) {
            throw new IllegalArgumentException("CLIENTE_NO_VALIDO");
        }
        if (esNuloOVacio(cliente.getNombreCliente())) {
            throw new IllegalArgumentException("CLIENTE_NO_VALIDO");
        }
        if (esNuloOVacio(cliente.getTelefono())) {
            throw new IllegalArgumentException("CLIENTE_NO_VALIDO");
        }
        if (esNuloOVacio(cliente.getDireccion())) {
            throw new IllegalArgumentException("CLIENTE_NO_VALIDO");
        }
        if (esNuloOVacio(cliente.getEmail())) {
            throw new IllegalArgumentException("CLIENTE_NO_VALIDO");
        }
        if (!EMAIL_PATTERN.matcher(cliente.getEmail().trim()).matches()) {
            throw new IllegalArgumentException("CLIENTE_NO_VALIDO");
        }
    }

    private boolean esNuloOVacio(String str) {
        return str == null || str.trim().isEmpty();
    }

    private Cliente mapearADominio(ClienteDTO dto) {
        Cliente c = new Cliente();
        if (dto.getIdCliente() != null) {
            c.setIdCliente(dto.getIdCliente());
        }
        c.setCedula(dto.getCedula() != null ? dto.getCedula().trim() : null);
        c.setNombreCliente(dto.getNombreCliente() != null ? dto.getNombreCliente().trim() : null);
        c.setTelefono(dto.getTelefono() != null ? dto.getTelefono().trim() : null);
        c.setEmail(dto.getEmail() != null ? dto.getEmail().trim() : null);
        c.setDireccion(dto.getDireccion() != null ? dto.getDireccion().trim() : null);
        c.setFechaNacimiento(dto.getFechaNacimiento());
        return c;
    }

    private ClienteDTO mapearADTO(Cliente c) {
        ClienteDTO dto = new ClienteDTO();
        dto.setIdCliente(c.getIdCliente());
        dto.setCedula(c.getCedula());
        dto.setNombreCliente(c.getNombreCliente());
        dto.setTelefono(c.getTelefono());
        dto.setEmail(c.getEmail());
        dto.setDireccion(c.getDireccion());
        dto.setFechaNacimiento(c.getFechaNacimiento());
        return dto;
    }
}