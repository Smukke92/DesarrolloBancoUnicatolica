
package com.unicatolica.banco.dao;

import com.unicatolica.banco.config.ConexionBD;
import com.unicatolica.banco.model.Transaccion;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.time.LocalDateTime;

public class TransaccionDAO {

    public List<Transaccion> listarPorNumeroCuenta(String numeroCuenta)
            throws SQLException, IOException {

        String sql = """
            SELECT t.idTransaccion, t.idCuenta, t.tipo,
                   t.monto, t.fechaHora
            FROM Transaccion t
            INNER JOIN Cuenta c ON c.idCuenta = t.idCuenta
            WHERE c.numeroCuenta = ?
            ORDER BY t.fechaHora DESC, t.idTransaccion DESC
            """;

        List<Transaccion> transacciones = new ArrayList<>();

        try (Connection conexion = ConexionBD.conectar();
             PreparedStatement sentencia =
                     conexion.prepareStatement(sql)) {

            sentencia.setString(1, numeroCuenta);

            try (ResultSet resultado = sentencia.executeQuery()) {
                while (resultado.next()) {
                    transacciones.add(mapearTransaccion(resultado));
                }
            }
        }

        return transacciones;
    }

    public Transaccion registrarDeposito(
            String numeroCuenta, BigDecimal monto)
            throws SQLException, IOException {

        return ejecutarOperacion(numeroCuenta, monto, "DEPOSITO");
    }

    public Transaccion registrarRetiro(
            String numeroCuenta, BigDecimal monto)
            throws SQLException, IOException {

        return ejecutarOperacion(numeroCuenta, monto, "RETIRO");
    }

    private Transaccion ejecutarOperacion(
            String numeroCuenta, BigDecimal monto, String tipo)
            throws SQLException, IOException {

        if (monto == null || monto.compareTo(BigDecimal.ZERO) <= 0) {
            throw new SQLException("MONTO_INVALIDO");
        }

        try (Connection conexion = ConexionBD.conectar()) {

            conexion.setAutoCommit(false);

            try {
                Integer idCuenta;
                BigDecimal saldoActual;
                String estado;

                String sqlCuenta = """
                    SELECT idCuenta, saldo, estado
                    FROM Cuenta
                    WHERE numeroCuenta = ?
                    FOR UPDATE
                    """;

                try (PreparedStatement sentencia =
                             conexion.prepareStatement(sqlCuenta)) {

                    sentencia.setString(1, numeroCuenta);

                    try (ResultSet resultado = sentencia.executeQuery()) {
                        if (!resultado.next()) {
                            throw new SQLException(
                                    "CUENTA_NO_ENCONTRADA");
                        }

                        idCuenta = resultado.getInt("idCuenta");
                        saldoActual = resultado.getBigDecimal("saldo");
                        estado = resultado.getString("estado");
                    }
                }

                if (!"ACTIVA".equals(estado)) {
                    throw new SQLException("CUENTA_INACTIVA");
                }

                BigDecimal nuevoSaldo;

                if ("DEPOSITO".equals(tipo)) {
                    nuevoSaldo = saldoActual.add(monto);
                } else if ("RETIRO".equals(tipo)) {

                    if (saldoActual.compareTo(monto) < 0) {
                        throw new SQLException(
                                "FONDOS_INSUFICIENTES");
                    }

                    nuevoSaldo = saldoActual.subtract(monto);
                } else {
                    throw new SQLException(
                            "TIPO_TRANSACCION_NO_VALIDO");
                }

                String sqlTransaccion = """
                    INSERT INTO Transaccion (idCuenta, tipo, monto)
                    VALUES (?, ?, ?)
                    """;

                Integer idTransaccion;

                try (PreparedStatement sentencia =
                             conexion.prepareStatement(
                                     sqlTransaccion,
                                     Statement.RETURN_GENERATED_KEYS)) {

                    sentencia.setInt(1, idCuenta);
                    sentencia.setString(2, tipo);
                    sentencia.setBigDecimal(3, monto);

                    if (sentencia.executeUpdate() == 0) {
                        throw new SQLException(
                                "ERROR_REGISTRANDO_TRANSACCION");
                    }

                    try (ResultSet claves =
                                 sentencia.getGeneratedKeys()) {

                        if (!claves.next()) {
                            throw new SQLException(
                                    "ERROR_REGISTRANDO_TRANSACCION");
                        }

                        idTransaccion = claves.getInt(1);
                    }
                }

                String sqlSaldo = """
                    UPDATE Cuenta
                    SET saldo = ?
                    WHERE idCuenta = ?
                    """;

                try (PreparedStatement sentencia =
                             conexion.prepareStatement(sqlSaldo)) {

                    sentencia.setBigDecimal(1, nuevoSaldo);
                    sentencia.setInt(2, idCuenta);

                    if (sentencia.executeUpdate() != 1) {
                        throw new SQLException(
                                "ERROR_ACTUALIZANDO_SALDO");
                    }
                }

                conexion.commit();

                return new Transaccion(
                        idTransaccion,
                        idCuenta,
                        tipo,
                        monto,
                        LocalDateTime.now()
                );

            } catch (SQLException | RuntimeException e) {
                try {
                    conexion.rollback();
                } catch (SQLException errorRollback) {
                    e.addSuppressed(errorRollback);
                }

                throw e;

            } finally {
                try {
                    conexion.setAutoCommit(true);
                } catch (SQLException ignored) {
                    // La conexión se cerrará al salir del try-with-resources.
                }
            }
        }
    }

    private Transaccion mapearTransaccion(ResultSet resultado)
            throws SQLException {

        Transaccion transaccion = new Transaccion();

        transaccion.setIdTransaccion(
                resultado.getInt("idTransaccion"));
        transaccion.setIdCuenta(resultado.getInt("idCuenta"));
        transaccion.setTipo(resultado.getString("tipo"));
        transaccion.setMonto(resultado.getBigDecimal("monto"));

        Timestamp fecha = resultado.getTimestamp("fechaHora");

        if (fecha != null) {
            transaccion.setFechaHora(fecha.toLocalDateTime());
        }

        return transaccion;
    }
}
