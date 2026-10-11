
package com.unicatolica.banco.dao;

import com.unicatolica.banco.config.ConexionBD;
import com.unicatolica.banco.model.Cuenta;

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CuentaDAO {

    public boolean registrar(Cuenta cuenta)
            throws SQLException, IOException {

        String sql = """
            INSERT INTO Cuenta
                (numeroCuenta, idCliente, tipoCuenta, estado, saldo)
            VALUES (?, ?, ?, 'ACTIVA', 0.00)
            """;

        try (Connection conexion = ConexionBD.conectar();
             PreparedStatement sentencia =
                     conexion.prepareStatement(
                             sql, Statement.RETURN_GENERATED_KEYS)) {

            sentencia.setString(1, cuenta.getNumeroCuenta());
            sentencia.setInt(2, cuenta.getIdCliente());
            sentencia.setString(3, cuenta.getTipoCuenta());

            int filas = sentencia.executeUpdate();

            if (filas == 0) {
                return false;
            }

            try (ResultSet claves = sentencia.getGeneratedKeys()) {
                if (claves.next()) {
                    cuenta.setIdCuenta(claves.getInt(1));
                }
            }

            return true;
        }
    }

    public List<Cuenta> listar()
            throws SQLException, IOException {

        String sql = """
            SELECT idCuenta, numeroCuenta, idCliente, tipoCuenta,
                   estado, saldo, fechaHoraCreacion
            FROM Cuenta
            ORDER BY idCuenta
            """;

        List<Cuenta> cuentas = new ArrayList<>();

        try (Connection conexion = ConexionBD.conectar();
             PreparedStatement sentencia =
                     conexion.prepareStatement(sql);
             ResultSet resultado = sentencia.executeQuery()) {

            while (resultado.next()) {
                cuentas.add(mapearCuenta(resultado));
            }
        }

        return cuentas;
    }

    public List<Cuenta> buscar(
            String numeroCuenta, String tipoCuenta, Integer idCliente)
            throws SQLException, IOException {

        StringBuilder sql = new StringBuilder("""
            SELECT idCuenta, numeroCuenta, idCliente, tipoCuenta,
                   estado, saldo, fechaHoraCreacion
            FROM Cuenta
            WHERE 1 = 1
            """);

        if (numeroCuenta != null && !numeroCuenta.isBlank()) {
            sql.append(" AND numeroCuenta LIKE ?");
        }

        if (tipoCuenta != null && !tipoCuenta.isBlank()) {
            sql.append(" AND tipoCuenta = ?");
        }

        if (idCliente != null) {
            sql.append(" AND idCliente = ?");
        }

        sql.append(" ORDER BY idCuenta");

        List<Cuenta> cuentas = new ArrayList<>();

        try (Connection conexion = ConexionBD.conectar();
             PreparedStatement sentencia =
                     conexion.prepareStatement(sql.toString())) {

            int parametro = 1;

            if (numeroCuenta != null && !numeroCuenta.isBlank()) {
                sentencia.setString(
                        parametro++, "%" + numeroCuenta.trim() + "%");
            }

            if (tipoCuenta != null && !tipoCuenta.isBlank()) {
                sentencia.setString(parametro++, tipoCuenta);
            }

            if (idCliente != null) {
                sentencia.setInt(parametro, idCliente);
            }

            try (ResultSet resultado = sentencia.executeQuery()) {
                while (resultado.next()) {
                    cuentas.add(mapearCuenta(resultado));
                }
            }
        }

        return cuentas;
    }

    public Cuenta buscarPorNumero(String numeroCuenta)
            throws SQLException, IOException {

        String sql = """
            SELECT idCuenta, numeroCuenta, idCliente, tipoCuenta,
                   estado, saldo, fechaHoraCreacion
            FROM Cuenta
            WHERE numeroCuenta = ?
            """;

        try (Connection conexion = ConexionBD.conectar();
             PreparedStatement sentencia =
                     conexion.prepareStatement(sql)) {

            sentencia.setString(1, numeroCuenta);

            try (ResultSet resultado = sentencia.executeQuery()) {
                if (resultado.next()) {
                    return mapearCuenta(resultado);
                }
            }
        }

        return null;
    }

    public boolean actualizar(Cuenta cuenta)
            throws SQLException, IOException {

        String sql = """
            UPDATE Cuenta
            SET tipoCuenta = ?
            WHERE idCuenta = ?
            """;

        try (Connection conexion = ConexionBD.conectar();
             PreparedStatement sentencia =
                     conexion.prepareStatement(sql)) {

            sentencia.setString(1, cuenta.getTipoCuenta());
            sentencia.setInt(2, cuenta.getIdCuenta());

            return sentencia.executeUpdate() > 0;
        }
    }

    public boolean cambiarEstado(Integer idCuenta, String estado)
            throws SQLException, IOException {

        String sql = """
            UPDATE Cuenta
            SET estado = ?
            WHERE idCuenta = ?
            """;

        try (Connection conexion = ConexionBD.conectar();
             PreparedStatement sentencia =
                     conexion.prepareStatement(sql)) {

            sentencia.setString(1, estado);
            sentencia.setInt(2, idCuenta);

            return sentencia.executeUpdate() > 0;
        }
    }

    public boolean eliminar(Integer idCuenta)
            throws SQLException, IOException {

        String verificar = """
            SELECT COUNT(*)
            FROM Transaccion
            WHERE idCuenta = ?
            """;

        String eliminar = """
            DELETE FROM Cuenta
            WHERE idCuenta = ?
            """;

        try (Connection conexion = ConexionBD.conectar()) {

            try (PreparedStatement sentencia =
                         conexion.prepareStatement(verificar)) {

                sentencia.setInt(1, idCuenta);

                try (ResultSet resultado = sentencia.executeQuery()) {
                    if (resultado.next() && resultado.getInt(1) > 0) {
                        throw new SQLException(
                                "CUENTA_NO_PUEDE_ELIMINARSE: "
                                + "la cuenta tiene transacciones.");
                    }
                }
            }

            try (PreparedStatement sentencia =
                         conexion.prepareStatement(eliminar)) {

                sentencia.setInt(1, idCuenta);
                return sentencia.executeUpdate() > 0;
            }
        }
    }

    private Cuenta mapearCuenta(ResultSet resultado)
            throws SQLException {

        Cuenta cuenta = new Cuenta();

        cuenta.setIdCuenta(resultado.getInt("idCuenta"));
        cuenta.setNumeroCuenta(resultado.getString("numeroCuenta"));
        cuenta.setIdCliente(resultado.getInt("idCliente"));
        cuenta.setTipoCuenta(resultado.getString("tipoCuenta"));
        cuenta.setEstado(resultado.getString("estado"));
        cuenta.setSaldo(resultado.getBigDecimal("saldo"));

        Timestamp fecha =
                resultado.getTimestamp("fechaHoraCreacion");

        if (fecha != null) {
            cuenta.setFechaHoraCreacion(fecha.toLocalDateTime());
        }

        return cuenta;
    }
    
    // 1. Necesario para buscar por ID numérico en actualizar/eliminar
    public Cuenta buscarPorId(Integer idCuenta) throws SQLException, IOException {
        String sql = """
            SELECT idCuenta, numeroCuenta, idCliente, tipoCuenta,
                   estado, saldo, fechaHoraCreacion
            FROM Cuenta
            WHERE idCuenta = ?
            """;

        try (Connection conexion = ConexionBD.conectar();
             PreparedStatement sentencia = conexion.prepareStatement(sql)) {

            sentencia.setInt(1, idCuenta);

            try (ResultSet resultado = sentencia.executeQuery()) {
                if (resultado.next()) {
                    return mapearCuenta(resultado);
                }
            }
        }
        return null;
    }

    // 2. Necesario para validar RN07 (número de cuenta único)
    public boolean existePorNumeroCuenta(String numeroCuenta) throws SQLException, IOException {
        String sql = "SELECT COUNT(*) FROM Cuenta WHERE numeroCuenta = ?";

        try (Connection conexion = ConexionBD.conectar();
             PreparedStatement sentencia = conexion.prepareStatement(sql)) {

            sentencia.setString(1, numeroCuenta);

            try (ResultSet resultado = sentencia.executeQuery()) {
                if (resultado.next()) {
                    return resultado.getInt(1) > 0;
                }
            }
        }
        return false;
    }

    // 3. Necesario para validar si se puede eliminar sin borrar historial
    public boolean tieneTransaccionesAsociadas(Integer idCuenta) throws SQLException, IOException {
        String sql = "SELECT COUNT(*) FROM Transaccion WHERE idCuenta = ?";

        try (Connection conexion = ConexionBD.conectar();
             PreparedStatement sentencia = conexion.prepareStatement(sql)) {

            sentencia.setInt(1, idCuenta);

            try (ResultSet resultado = sentencia.executeQuery()) {
                if (resultado.next()) {
                    return resultado.getInt(1) > 0;
                }
            }
        }
        return false;
    }
}

