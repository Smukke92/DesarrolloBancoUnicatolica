package com.unicatolica.banco.dao;

import com.unicatolica.banco.config.ConexionBD;
import com.unicatolica.banco.model.Cuenta;

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CuentaDAO {

    public boolean registrar(Cuenta cuenta) throws SQLException, IOException {

        // Validar primero si el cliente está activo antes de insertar
        if (!esClienteActivo(cuenta.getIdCliente())) {
            throw new SQLException("CLIENTE_INACTIVO: No se puede registrar una cuenta para un cliente inactivo.");
        }

        String sql = """
            INSERT INTO Cuenta
                (numeroCuenta, idCliente, tipoCuenta, estado, saldo)
            VALUES (?, ?, ?, 'ACTIVA', 0.00)
            """;

        try (Connection conexion = ConexionBD.conectar();
             PreparedStatement sentencia = conexion.prepareStatement(
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

    // Lista únicamente las cuentas asociadas a un cliente activo
    public List<Cuenta> listar() throws SQLException, IOException {

        String sql = """
            SELECT c.idCuenta, c.numeroCuenta, c.idCliente, c.tipoCuenta,
                   c.estado, c.saldo, c.fechaHoraCreacion
            FROM Cuenta c
            INNER JOIN Cliente cl ON c.idCliente = cl.idCliente
            WHERE cl.activo = TRUE
            ORDER BY c.idCuenta
            """;

        List<Cuenta> cuentas = new ArrayList<>();

        try (Connection conexion = ConexionBD.conectar();
             PreparedStatement sentencia = conexion.prepareStatement(sql);
             ResultSet resultado = sentencia.executeQuery()) {

            while (resultado.next()) {
                cuentas.add(mapearCuenta(resultado));
            }
        }

        return cuentas;
    }

    // Filtra las cuentas asegurando que el cliente esté activo
    public List<Cuenta> buscar(String numeroCuenta, String tipoCuenta, Integer idCliente)
            throws SQLException, IOException {

        StringBuilder sql = new StringBuilder("""
            SELECT c.idCuenta, c.numeroCuenta, c.idCliente, c.tipoCuenta,
                   c.estado, c.saldo, c.fechaHoraCreacion
            FROM Cuenta c
            INNER JOIN Cliente cl ON c.idCliente = cl.idCliente
            WHERE cl.activo = TRUE
            """);

        if (numeroCuenta != null && !numeroCuenta.isBlank()) {
            sql.append(" AND c.numeroCuenta LIKE ?");
        }

        if (tipoCuenta != null && !tipoCuenta.isBlank()) {
            sql.append(" AND c.tipoCuenta = ?");
        }

        if (idCliente != null) {
            sql.append(" AND c.idCliente = ?");
        }

        sql.append(" ORDER BY c.idCuenta");

        List<Cuenta> cuentas = new ArrayList<>();

        try (Connection conexion = ConexionBD.conectar();
             PreparedStatement sentencia = conexion.prepareStatement(sql.toString())) {

            int parametro = 1;

            if (numeroCuenta != null && !numeroCuenta.isBlank()) {
                sentencia.setString(parametro++, "%" + numeroCuenta.trim() + "%");
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

    // Busca por número de cuenta asegurando que pertenezca a un cliente activo
    public Cuenta buscarPorNumero(String numeroCuenta) throws SQLException, IOException {

        String sql = """
            SELECT c.idCuenta, c.numeroCuenta, c.idCliente, c.tipoCuenta,
                   c.estado, c.saldo, c.fechaHoraCreacion
            FROM Cuenta c
            INNER JOIN Cliente cl ON c.idCliente = cl.idCliente
            WHERE c.numeroCuenta = ? AND cl.activo = TRUE
            """;

        try (Connection conexion = ConexionBD.conectar();
             PreparedStatement sentencia = conexion.prepareStatement(sql)) {

            sentencia.setString(1, numeroCuenta);

            try (ResultSet resultado = sentencia.executeQuery()) {
                if (resultado.next()) {
                    return mapearCuenta(resultado);
                }
            }
        }

        return null;
    }

    // Actualiza únicamente si la cuenta pertenece a un cliente activo
    public boolean actualizar(Cuenta cuenta) throws SQLException, IOException {

        String sql = """
            UPDATE Cuenta c
            INNER JOIN Cliente cl ON c.idCliente = cl.idCliente
            SET c.tipoCuenta = ?
            WHERE c.idCuenta = ? AND cl.activo = TRUE
            """;

        try (Connection conexion = ConexionBD.conectar();
             PreparedStatement sentencia = conexion.prepareStatement(sql)) {

            sentencia.setString(1, cuenta.getTipoCuenta());
            sentencia.setInt(2, cuenta.getIdCuenta());

            return sentencia.executeUpdate() > 0;
        }
    }

    // Cambia el estado de la cuenta solo si el cliente asociado está activo
    public boolean cambiarEstado(Integer idCuenta, String estado) throws SQLException, IOException {

        String sql = """
            UPDATE Cuenta c
            INNER JOIN Cliente cl ON c.idCliente = cl.idCliente
            SET c.estado = ?
            WHERE c.idCuenta = ? AND cl.activo = TRUE
            """;

        try (Connection conexion = ConexionBD.conectar();
             PreparedStatement sentencia = conexion.prepareStatement(sql)) {

            sentencia.setString(1, estado);
            sentencia.setInt(2, idCuenta);

            return sentencia.executeUpdate() > 0;
        }
    }

    // Elimina la cuenta solo si no tiene transacciones y pertenece a un cliente activo
    public boolean eliminar(Integer idCuenta) throws SQLException, IOException {

        // Validar si la cuenta está asociada a un cliente activo
        Cuenta cuenta = buscarPorId(idCuenta);
        if (cuenta == null) {
            return false; // No existe o el cliente está inactivo
        }

        String verificarTransacciones = """
            SELECT COUNT(*)
            FROM Transaccion
            WHERE idCuenta = ?
            """;

        String eliminarSql = """
            DELETE c FROM Cuenta c
            INNER JOIN Cliente cl ON c.idCliente = cl.idCliente
            WHERE c.idCuenta = ? AND cl.activo = TRUE
            """;

        try (Connection conexion = ConexionBD.conectar()) {

            try (PreparedStatement sentencia = conexion.prepareStatement(verificarTransacciones)) {
                sentencia.setInt(1, idCuenta);

                try (ResultSet resultado = sentencia.executeQuery()) {
                    if (resultado.next() && resultado.getInt(1) > 0) {
                        throw new SQLException(
                                "CUENTA_NO_PUEDE_ELIMINARSE: la cuenta tiene transacciones.");
                    }
                }
            }

            try (PreparedStatement sentencia = conexion.prepareStatement(eliminarSql)) {
                sentencia.setInt(1, idCuenta);
                return sentencia.executeUpdate() > 0;
            }
        }
    }

    // Busca la cuenta por su ID asegurando que el cliente esté activo
    public Cuenta buscarPorId(Integer idCuenta) throws SQLException, IOException {

        String sql = """
            SELECT c.idCuenta, c.numeroCuenta, c.idCliente, c.tipoCuenta,
                   c.estado, c.saldo, c.fechaHoraCreacion
            FROM Cuenta c
            INNER JOIN Cliente cl ON c.idCliente = cl.idCliente
            WHERE c.idCuenta = ? AND cl.activo = TRUE
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

    public boolean existePorNumeroCuenta(String numeroCuenta) throws SQLException, IOException {

        String sql = """
            SELECT COUNT(*)
            FROM Cuenta c
            INNER JOIN Cliente cl ON c.idCliente = cl.idCliente
            WHERE c.numeroCuenta = ? AND cl.activo = TRUE
            """;

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

    // Método auxiliar privado para verificar el estado activo del cliente por ID
    public boolean esClienteActivo(Integer idCliente) throws SQLException, IOException {

        String sql = "SELECT activo FROM Cliente WHERE idCliente = ?";

        try (Connection conexion = ConexionBD.conectar();
             PreparedStatement sentencia = conexion.prepareStatement(sql)) {

            sentencia.setInt(1, idCliente);

            try (ResultSet resultado = sentencia.executeQuery()) {
                if (resultado.next()) {
                    return resultado.getBoolean("activo");
                }
            }
        }
        return false;
    }

    private Cuenta mapearCuenta(ResultSet resultado) throws SQLException {

        Cuenta cuenta = new Cuenta();

        cuenta.setIdCuenta(resultado.getInt("idCuenta"));
        cuenta.setNumeroCuenta(resultado.getString("numeroCuenta"));
        cuenta.setIdCliente(resultado.getInt("idCliente"));
        cuenta.setTipoCuenta(resultado.getString("tipoCuenta"));
        cuenta.setEstado(resultado.getString("estado"));
        cuenta.setSaldo(resultado.getBigDecimal("saldo"));

        Timestamp fecha = resultado.getTimestamp("fechaHoraCreacion");

        if (fecha != null) {
            cuenta.setFechaHoraCreacion(fecha.toLocalDateTime());
        }

        return cuenta;
    }
}