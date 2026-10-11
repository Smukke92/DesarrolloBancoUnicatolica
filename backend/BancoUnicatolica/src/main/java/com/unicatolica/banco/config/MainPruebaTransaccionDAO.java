package com.unicatolica.banco.config;

import com.unicatolica.banco.dao.TransaccionDAO;
import com.unicatolica.banco.model.Transaccion;

import java.math.BigDecimal;
import java.util.List;

public class MainPruebaTransaccionDAO {

    public static void main(String[] args) {
        TransaccionDAO transaccionDAO = new TransaccionDAO();
        String numeroCuenta = "1002003004";

        try {
            System.out.println("--- 1. PROBANDO DEPÓSITO ---");
            BigDecimal montoDeposito = new BigDecimal("150000.00");
            Transaccion deposito = transaccionDAO.registrarDeposito(numeroCuenta, montoDeposito);
            System.out.println("Depósito exitoso:");
            System.out.println("ID Transacción: " + deposito.getIdTransaccion());
            System.out.println("ID Cuenta: " + deposito.getIdCuenta());
            System.out.println("Tipo: " + deposito.getTipo());
            System.out.println("Monto: $" + deposito.getMonto());
            System.out.println("Fecha/Hora: " + deposito.getFechaHora());

            System.out.println("\n--- 2. PROBANDO RETIRO ---");
            BigDecimal montoRetiro = new BigDecimal("50000.00");
            Transaccion retiro = transaccionDAO.registrarRetiro(numeroCuenta, montoRetiro);
            System.out.println("Retiro exitoso:");
            System.out.println("ID Transacción: " + retiro.getIdTransaccion());
            System.out.println("ID Cuenta: " + retiro.getIdCuenta());
            System.out.println("Tipo: " + retiro.getTipo());
            System.out.println("Monto: $" + retiro.getMonto());
            System.out.println("Fecha/Hora: " + retiro.getFechaHora());

            System.out.println("\n--- 3. LISTANDO HISTORIAL DE TRANSACCIONES ---");
            List<Transaccion> historial = transaccionDAO.listarPorNumeroCuenta(numeroCuenta);

            System.out.println("Total transacciones encontradas: " + historial.size());
            for (Transaccion t : historial) {
                System.out.println(String.format("ID: %d | Tipo: %s | Monto: $%.2f | Fecha: %s",
                        t.getIdTransaccion(),
                        t.getTipo(),
                        t.getMonto(),
                        t.getFechaHora()));
            }

        } catch (Exception e) {
            System.err.println("Error durante la prueba del DAO: " + e.getMessage());
            e.printStackTrace();
        }
    }
}