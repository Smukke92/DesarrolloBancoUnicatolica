
package com.unicatolica.banco.dao;

import com.unicatolica.banco.config.ConexionBD;
import com.unicatolica.banco.model.Cliente;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

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
}

