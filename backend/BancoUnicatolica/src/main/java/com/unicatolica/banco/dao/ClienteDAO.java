
package com.unicatolica.banco.dao;

import com.unicatolica.banco.config.ConexionBD;
import com.unicatolica.banco.model.Cliente;
import java.util.List;
import java.util.ArrayList;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.ResultSet;

public class ClienteDAO {

    public boolean registrar(Cliente cliente) throws SQLException, java.io.IOException {

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
                    java.sql.Date.valueOf(cliente.getFechaNacimiento())
                );
            } else {
                sentencia.setNull(
                    6,
                    java.sql.Types.DATE
                );
            }

            int filasInsertadas = sentencia.executeUpdate();

            if (filasInsertadas > 0) {
                try (var resultado = sentencia.getGeneratedKeys()) {
                    if (resultado.next()) {
                        cliente.setIdCliente(resultado.getInt(1));
                    }
                }

                return true;
            }

            return false;
        }
    }
    
    public Cliente buscarPorCedula(String cedula)
            throws SQLException, java.io.IOException {

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
                    Cliente cliente = new Cliente();

                    cliente.setIdCliente(resultado.getInt("idCliente"));
                    cliente.setCedula(resultado.getString("cedula"));
                    cliente.setNombreCliente(resultado.getString("nombreCliente"));
                    cliente.setTelefono(resultado.getString("telefono"));
                    cliente.setEmail(resultado.getString("email"));
                    cliente.setDireccion(resultado.getString("direccion"));

                    java.sql.Date fecha = resultado.getDate("fechaNacimiento");

                    if (fecha != null) {
                        cliente.setFechaNacimiento(fecha.toLocalDate());
                    }

                    return cliente;
                }
            }
        }

        return null;
    }
    
    public List<Cliente> listarClientes()
            throws SQLException, java.io.IOException {

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
                Cliente cliente = new Cliente();

                cliente.setIdCliente(resultado.getInt("idCliente"));
                cliente.setCedula(resultado.getString("cedula"));
                cliente.setNombreCliente(resultado.getString("nombreCliente"));
                cliente.setTelefono(resultado.getString("telefono"));
                cliente.setEmail(resultado.getString("email"));
                cliente.setDireccion(resultado.getString("direccion"));

                java.sql.Date fecha = resultado.getDate("fechaNacimiento");

                if (fecha != null) {
                    cliente.setFechaNacimiento(fecha.toLocalDate());
                }

                clientes.add(cliente);
            }
        }

        return clientes;
    }  
    public List<Cliente> buscarClientes(String criterio)
            throws SQLException, java.io.IOException {

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
                    Cliente cliente = new Cliente();

                    cliente.setIdCliente(resultado.getInt("idCliente"));
                    cliente.setCedula(resultado.getString("cedula"));
                    cliente.setNombreCliente(
                            resultado.getString("nombreCliente"));
                    cliente.setTelefono(resultado.getString("telefono"));
                    cliente.setEmail(resultado.getString("email"));
                    cliente.setDireccion(resultado.getString("direccion"));

                    java.sql.Date fecha =
                            resultado.getDate("fechaNacimiento");

                    if (fecha != null) {
                        cliente.setFechaNacimiento(fecha.toLocalDate());
                    }

                    clientes.add(cliente);
                }
            }
        }
        return clientes;
    }   
    public boolean actualizarCliente(Cliente cliente)
            throws SQLException, java.io.IOException {

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
                sentencia.setDate(
                        5,
                        java.sql.Date.valueOf(cliente.getFechaNacimiento())
                );
            } else {
                sentencia.setNull(5, java.sql.Types.DATE);
            }

            sentencia.setInt(6, cliente.getIdCliente());

            return sentencia.executeUpdate() > 0;
        }
    }
    public boolean eliminarCliente(Integer clienteId)
            throws SQLException, java.io.IOException {

        String sqlVerificar = """
            SELECT COUNT(*)
            FROM Cuenta
            WHERE idCliente = ?
            """;

        String sqlEliminar = """
            DELETE FROM Cliente
            WHERE idCliente = ?
            """;

        try (Connection conexion = ConexionBD.conectar()) {

            // Verificar si el cliente tiene cuentas asociadas.
            try (PreparedStatement sentencia =
                         conexion.prepareStatement(sqlVerificar)) {

                sentencia.setInt(1, clienteId);

                try (ResultSet resultado = sentencia.executeQuery()) {
                    if (resultado.next() && resultado.getInt(1) > 0) {
                        throw new SQLException(
                                "No se puede eliminar el cliente porque "
                                + "tiene cuentas asociadas."
                        );
                    }
                }
            }

            // Eliminar el cliente si no tiene cuentas.
            try (PreparedStatement sentencia =
                         conexion.prepareStatement(sqlEliminar)) {

                sentencia.setInt(1, clienteId);

                return sentencia.executeUpdate() > 0;
            }
        }
    }
}

