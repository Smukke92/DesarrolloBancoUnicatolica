package com.unicatolica.banco.dao;

import com.unicatolica.banco.config.ConexionBD;
import com.unicatolica.banco.model.Cliente;

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ClienteDAO {

    public boolean registrar(Cliente cliente) throws SQLException, IOException {

        String sql = """
            INSERT INTO Cliente (
                cedula,
                nombreCliente,
                telefono,
                email,
                direccion,
                fechaNacimiento,
                activo
            )
            VALUES (?, ?, ?, ?, ?, ?, ?)
            """;

        try (Connection conexion = ConexionBD.conectar();
             PreparedStatement sentencia = conexion.prepareStatement(
                     sql, Statement.RETURN_GENERATED_KEYS)) {

            sentencia.setString(1, cliente.getCedula());
            sentencia.setString(2, cliente.getNombreCliente());
            sentencia.setString(3, cliente.getTelefono());
            sentencia.setString(4, cliente.getEmail());
            sentencia.setString(5, cliente.getDireccion());

            if (cliente.getFechaNacimiento() != null) {
                sentencia.setDate(
                    6,
                    Date.valueOf(cliente.getFechaNacimiento())
                );
            } else {
                sentencia.setNull(
                    6,
                    Types.DATE
                );
            }

            // Si el objeto no define activo explicitly, por defecto se registra activo (true)
            boolean estaActivo = cliente.getActivo() != null ? cliente.getActivo() : true;
            sentencia.setBoolean(7, estaActivo);

            int filasInsertadas = sentencia.executeUpdate();

            if (filasInsertadas > 0) {
                try (ResultSet resultado = sentencia.getGeneratedKeys()) {
                    if (resultado.next()) {
                        cliente.setIdCliente(resultado.getInt(1));
                    }
                }
                return true;
            }

            return false;
        }
    }

    public Cliente buscarPorId(Integer idCliente) throws SQLException, IOException {

        String sql = """
            SELECT idCliente, cedula, nombreCliente, telefono,
                   email, direccion, fechaNacimiento, activo
            FROM Cliente
            WHERE idCliente = ?
            """;

        try (Connection conexion = ConexionBD.conectar();
             PreparedStatement sentencia = conexion.prepareStatement(sql)) {

            sentencia.setInt(1, idCliente);

            try (ResultSet resultado = sentencia.executeQuery()) {
                if (resultado.next()) {
                    return mapearCliente(resultado);
                }
            }
        }
        return null;
    }

    public Cliente buscarPorCedula(String cedula) throws SQLException, IOException {

        String sql = """
            SELECT idCliente, cedula, nombreCliente, telefono,
                   email, direccion, fechaNacimiento, activo
            FROM Cliente
            WHERE cedula = ?
            """;

        try (Connection conexion = ConexionBD.conectar();
             PreparedStatement sentencia = conexion.prepareStatement(sql)) {

            sentencia.setString(1, cedula);

            try (ResultSet resultado = sentencia.executeQuery()) {
                if (resultado.next()) {
                    return mapearCliente(resultado);
                }
            }
        }
        return null;
    }

    public boolean existePorCedula(String cedula) throws SQLException, IOException {

        String sql = "SELECT COUNT(*) FROM Cliente WHERE cedula = ?";

        try (Connection conexion = ConexionBD.conectar();
             PreparedStatement sentencia = conexion.prepareStatement(sql)) {

            sentencia.setString(1, cedula);

            try (ResultSet resultado = sentencia.executeQuery()) {
                if (resultado.next()) {
                    return resultado.getInt(1) > 0;
                }
            }
        }
        return false;
    }

    // Devuelve por defecto solo los clientes activos
    public List<Cliente> listarClientes() throws SQLException, IOException {

        List<Cliente> clientes = new ArrayList<>();

        String sql = """
            SELECT idCliente, cedula, nombreCliente, telefono,
                   email, direccion, fechaNacimiento, activo
            FROM Cliente
            WHERE activo = TRUE
            ORDER BY idCliente
            """;

        try (Connection conexion = ConexionBD.conectar();
             PreparedStatement sentencia = conexion.prepareStatement(sql);
             ResultSet resultado = sentencia.executeQuery()) {

            while (resultado.next()) {
                clientes.add(mapearCliente(resultado));
            }
        }

        return clientes;
    }

    // Busca únicamente entre los clientes activos
    public List<Cliente> buscarClientes(String criterio) throws SQLException, IOException {

        List<Cliente> clientes = new ArrayList<>();

        String sql = """
            SELECT idCliente, cedula, nombreCliente, telefono,
                   email, direccion, fechaNacimiento, activo
            FROM Cliente
            WHERE activo = TRUE AND (cedula LIKE ? OR nombreCliente LIKE ?)
            ORDER BY idCliente
            """;

        try (Connection conexion = ConexionBD.conectar();
             PreparedStatement sentencia = conexion.prepareStatement(sql)) {

            String busqueda = "%" + criterio.trim() + "%";

            sentencia.setString(1, busqueda);
            sentencia.setString(2, busqueda);

            try (ResultSet resultado = sentencia.executeQuery()) {
                while (resultado.next()) {
                    clientes.add(mapearCliente(resultado));
                }
            }
        }
        return clientes;
    }

    public boolean actualizarCliente(Cliente cliente) throws SQLException, IOException {

        String sql = """
            UPDATE Cliente
            SET nombreCliente = ?,
                telefono = ?,
                email = ?,
                direccion = ?,
                fechaNacimiento = ?,
                activo = ?
            WHERE idCliente = ?
            """;

        try (Connection conexion = ConexionBD.conectar();
             PreparedStatement sentencia = conexion.prepareStatement(sql)) {

            sentencia.setString(1, cliente.getNombreCliente());
            sentencia.setString(2, cliente.getTelefono());
            sentencia.setString(3, cliente.getEmail());
            sentencia.setString(4, cliente.getDireccion());

            if (cliente.getFechaNacimiento() != null) {
                sentencia.setDate(5, Date.valueOf(cliente.getFechaNacimiento()));
            } else {
                sentencia.setNull(5, Types.DATE);
            }

            boolean estaActivo = cliente.getActivo() != null ? cliente.getActivo() : true;
            sentencia.setBoolean(6, estaActivo);
            sentencia.setInt(7, cliente.getIdCliente());

            return sentencia.executeUpdate() > 0;
        }
    }

    // Borrado lógico: inactiva al cliente en lugar de eliminarlo físicamente
    public boolean eliminarCliente(Integer clienteId) throws SQLException, IOException {

        String sqlInactivar = "UPDATE Cliente SET activo = FALSE WHERE idCliente = ? AND activo = TRUE";

        try (Connection conexion = ConexionBD.conectar();
             PreparedStatement sentencia = conexion.prepareStatement(sqlInactivar)) {

            sentencia.setInt(1, clienteId);
            return sentencia.executeUpdate() > 0;
        }
    }

    // Permite reactivar un cliente previamente inactivado
    public boolean reactivarCliente(Integer clienteId) throws SQLException, IOException {

        String sqlReactivar = "UPDATE Cliente SET activo = TRUE WHERE idCliente = ? AND activo = FALSE";

        try (Connection conexion = ConexionBD.conectar();
             PreparedStatement sentencia = conexion.prepareStatement(sqlReactivar)) {

            sentencia.setInt(1, clienteId);
            return sentencia.executeUpdate() > 0;
        }
    }

    private Cliente mapearCliente(ResultSet resultado) throws SQLException {
        Cliente cliente = new Cliente();
        cliente.setIdCliente(resultado.getInt("idCliente"));
        cliente.setCedula(resultado.getString("cedula"));
        cliente.setNombreCliente(resultado.getString("nombreCliente"));
        cliente.setTelefono(resultado.getString("telefono"));
        cliente.setEmail(resultado.getString("email"));
        cliente.setDireccion(resultado.getString("direccion"));
        cliente.setActivo(resultado.getBoolean("activo"));

        Date fecha = resultado.getDate("fechaNacimiento");
        if (fecha != null) {
            cliente.setFechaNacimiento(fecha.toLocalDate());
        }

        return cliente;
    }
}