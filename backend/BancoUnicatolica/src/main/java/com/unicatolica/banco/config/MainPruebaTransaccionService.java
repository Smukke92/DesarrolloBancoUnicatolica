package com.unicatolica.banco.config;

import com.unicatolica.banco.dto.*;
import com.unicatolica.banco.service.ClienteService;
import com.unicatolica.banco.service.CuentaService;
import com.unicatolica.banco.service.TransaccionService;
import com.unicatolica.banco.service.impl.ClienteServiceImpl;
import com.unicatolica.banco.service.impl.CuentaServiceImpl;
import com.unicatolica.banco.service.impl.TransaccionServiceImpl;

import java.math.BigDecimal;
import java.time.LocalDate;

public class MainPruebaTransaccionService {

    public static void main(String[] args) {
        ClienteService clienteService = new ClienteServiceImpl();
        CuentaService cuentaService = new CuentaServiceImpl();
        TransaccionService transaccionService = new TransaccionServiceImpl();

        String cedula = "CED" + System.currentTimeMillis() % 1000000;
        String numeroCuenta = "100" + System.currentTimeMillis() % 100000;

        try {
            System.out.println("--- 1. PREPARANDO CLIENTE Y CUENTA DE PRUEBA ---");
            ClienteDTO cliente = new ClienteDTO();
            cliente.setCedula(cedula);
            cliente.setNombreCliente("Andres Lopez");
            cliente.setTelefono("3112223344");
            cliente.setEmail("andres@email.com");
            cliente.setDireccion("Calle 50 # 10-20");
            cliente.setFechaNacimiento(LocalDate.of(1990, 1, 1));
            ClienteDTO cCreado = clienteService.registrarCliente(cliente);

            CuentaDTO cuenta = new CuentaDTO();
            cuenta.setIdCliente(cCreado.getIdCliente());
            cuenta.setNumeroCuenta(numeroCuenta);
            cuenta.setTipoCuenta("AHORROS");
            cuentaService.registrarCuenta(cuenta);

            System.out.println("Cuenta creada exitosamente con número: " + numeroCuenta);

            System.out.println("\n--- 2. PROBANDO DEPÓSITO ($300.000) ---");
            ResultadoTransaccionDTO resDep = transaccionService.registrarDeposito(numeroCuenta, new BigDecimal("300000.00"));
            System.out.println("Depósito Exitoso!");
            System.out.println("Saldo Anterior: $" + resDep.getSaldoAnterior());
            System.out.println("Saldo Nuevo: $" + resDep.getSaldoNuevo());

            System.out.println("\n--- 3. PROBANDO RETIRO ($100.000) ---");
            ResultadoTransaccionDTO resRet = transaccionService.registrarRetiro(numeroCuenta, new BigDecimal("100000.00"));
            System.out.println("Retiro Exitoso!");
            System.out.println("Saldo Anterior: $" + resRet.getSaldoAnterior());
            System.out.println("Saldo Nuevo: $" + resRet.getSaldoNuevo());

            System.out.println("\n--- 4. PROBANDO VALIDACIÓN FONDOS INSUFICIENTES ($500.000) ---");
            try {
                transaccionService.registrarRetiro(numeroCuenta, new BigDecimal("500000.00"));
            } catch (IllegalArgumentException e) {
                System.out.println("Atrapado correctamente: " + e.getMessage());
            }

            System.out.println("\n--- 5. PROBANDO ESTADO DE CUENTA COMPLETO ---");
            EstadoCuentaDTO estado = transaccionService.consultarEstadoCuenta(numeroCuenta);
            System.out.println("Titular: " + estado.getNombreCliente() + " (Cédula: " + estado.getCedulaCliente() + ")");
            System.out.println("Número Cuenta: " + estado.getNumeroCuenta() + " | Saldo Actual: $" + estado.getSaldo());
            System.out.println("Movimientos registrados (" + estado.getListaTransacciones().size() + "):");
            for (TransaccionDTO t : estado.getListaTransacciones()) {
                System.out.println(" - ID: " + t.getIdTransaccion() + " | Tipo: " + t.getTipoTransaccion() + " | Monto: $" + t.getMonto() + " | Fecha: " + t.getFechaHora());
            }

            System.out.println("\n¡Todas las pruebas de TransaccionService finalizaron con éxito!");

        } catch (Exception e) {
            System.err.println("Error durante la prueba: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
