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
                fechaNacimiento
            )
            VALUES (?, ?, ?, ?, ?, ?)
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
                   email, direccion, fechaNacimiento
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
                   email, direccion, fechaNacimiento
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

    public List<Cliente> listarClientes() throws SQLException, IOException {

        List<Cliente> clientes = new ArrayList<>();

        String sql = """
            SELECT idCliente, cedula, nombreCliente, telefono,
                   email, direccion, fechaNacimiento
            FROM Cliente
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

    public List<Cliente> buscarClientes(String criterio) throws SQLException, IOException {

        List<Cliente> clientes = new ArrayList<>();

        String sql = """
            SELECT idCliente, cedula, nombreCliente, telefono,
                   email, direccion, fechaNacimiento
            FROM Cliente
            WHERE cedula LIKE ? OR nombreCliente LIKE ?
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
                fechaNacimiento = ?
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

            sentencia.setInt(6, cliente.getIdCliente());

            return sentencia.executeUpdate() > 0;
        }
    }

    public boolean eliminarCliente(Integer clienteId) throws SQLException, IOException {

        String sqlVerificar = "SELECT COUNT(*) FROM Cuenta WHERE idCliente = ?";
        String sqlEliminar = "DELETE FROM Cliente WHERE idCliente = ?";

        try (Connection conexion = ConexionBD.conectar()) {

            // Verificar si el cliente tiene cuentas asociadas.
            try (PreparedStatement sentencia = conexion.prepareStatement(sqlVerificar)) {

                sentencia.setInt(1, clienteId);

                try (ResultSet resultado = sentencia.executeQuery()) {
                    if (resultado.next() && resultado.getInt(1) > 0) {
                        // Mensaje estandarizado acorde al contrato
                        throw new SQLException("CLIENTE_TIENE_CUENTAS");
                    }
                }
            }

            // Eliminar el cliente si no tiene cuentas.
            try (PreparedStatement sentencia = conexion.prepareStatement(sqlEliminar)) {
                sentencia.setInt(1, clienteId);
                return sentencia.executeUpdate() > 0;
            }
        }
    }

    // Método auxiliar privado para reducir duplicación de código al leer del ResultSet
    private Cliente mapearCliente(ResultSet resultado) throws SQLException {
        Cliente cliente = new Cliente();
        cliente.setIdCliente(resultado.getInt("idCliente"));
        cliente.setCedula(resultado.getString("cedula"));
        cliente.setNombreCliente(resultado.getString("nombreCliente"));
        cliente.setTelefono(resultado.getString("telefono"));
        cliente.setEmail(resultado.getString("email"));
        cliente.setDireccion(resultado.getString("direccion"));

        Date fecha = resultado.getDate("fechaNacimiento");
        if (fecha != null) {
            cliente.setFechaNacimiento(fecha.toLocalDate());
        }

        return cliente;
    }
}