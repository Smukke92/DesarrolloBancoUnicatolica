package com.unicatolica.banco.config;

import com.unicatolica.banco.dto.ClienteDTO;
import com.unicatolica.banco.dto.CuentaDTO;
import com.unicatolica.banco.dto.FiltroCuentaDTO;
import com.unicatolica.banco.model.enums.EstadoCuenta;
import com.unicatolica.banco.service.ClienteService;
import com.unicatolica.banco.service.CuentaService;
import com.unicatolica.banco.service.impl.ClienteServiceImpl;
import com.unicatolica.banco.service.impl.CuentaServiceImpl;

import java.time.LocalDate;
import java.util.List;

public class MainPruebaCuentaService {

    public static void main(String[] args) {
        ClienteService clienteService = new ClienteServiceImpl();
        CuentaService cuentaService = new CuentaServiceImpl();

        // Número de identificación aleatorio para evitar conflictos con registros existentes en BD
        String cedulaPrueba = "CED" + System.currentTimeMillis() % 1000000;
        String numeroCuentaPrueba = "100" + System.currentTimeMillis() % 100000;

        try {
            System.out.println("==================================================");
            System.out.println("--- 1. PASO 1: REGISTRAR UN NUEVO CLIENTE ---");
            System.out.println("==================================================");
            
            ClienteDTO nuevoCliente = new ClienteDTO();
            nuevoCliente.setCedula(cedulaPrueba);
            nuevoCliente.setNombreCliente("Maria Fernanda Gomez");
            nuevoCliente.setTelefono("3158889900");
            nuevoCliente.setEmail("maria.gomez@email.com");
            nuevoCliente.setDireccion("Carrera 15 # 45-30");
            nuevoCliente.setFechaNacimiento(LocalDate.of(1998, 5, 20));

            ClienteDTO clienteCreado = clienteService.registrarCliente(nuevoCliente);
            System.out.println("Cliente registrado exitosamente!");
            System.out.println("ID Cliente: " + clienteCreado.getIdCliente());
            System.out.println("Nombre: " + clienteCreado.getNombreCliente());
            System.out.println("Cédula: " + clienteCreado.getCedula());

            System.out.println("\n==================================================");
            System.out.println("--- 2. PASO 2: CREAR UNA CUENTA PARA EL CLIENTE ---");
            System.out.println("==================================================");

            CuentaDTO nuevaCuenta = new CuentaDTO();
            nuevaCuenta.setIdCliente(clienteCreado.getIdCliente());
            nuevaCuenta.setNumeroCuenta(numeroCuentaPrueba);
            nuevaCuenta.setTipoCuenta("AHORROS"); // Debe ser AHORROS o CORRIENTE

            CuentaDTO cuentaCreada = cuentaService.registrarCuenta(nuevaCuenta);
            System.out.println("Cuenta bancaria registrada exitosamente!");
            System.out.println("ID Cuenta: " + cuentaCreada.getIdCuenta());
            System.out.println("Número de Cuenta: " + cuentaCreada.getNumeroCuenta());
            System.out.println("Propietario: " + cuentaCreada.getNombreCliente());
            System.out.println("Tipo Cuenta: " + cuentaCreada.getTipoCuenta());
            System.out.println("Estado Inicial: " + cuentaCreada.getEstado());
            System.out.println("Saldo Inicial: $" + cuentaCreada.getSaldo());

            System.out.println("\n==================================================");
            System.out.println("--- 3. PASO 3: CONSULTAR CUENTA POR NÚMERO ---");
            System.out.println("==================================================");

            CuentaDTO cuentaConsultada = cuentaService.obtenerCuentaPorNumero(numeroCuentaPrueba);
            System.out.println("Cuenta encontrada por número:");
            System.out.println("Número: " + cuentaConsultada.getNumeroCuenta() + " | Saldo: $" + cuentaConsultada.getSaldo());

            System.out.println("\n==================================================");
            System.out.println("--- 4. PASO 4: FILTRAR Y BUSCAR CUENTAS ---");
            System.out.println("==================================================");

            FiltroCuentaDTO filtro = new FiltroCuentaDTO();
            filtro.setIdCliente(clienteCreado.getIdCliente());
            List<CuentaDTO> cuentasCliente = cuentaService.buscarCuentas(filtro);
            System.out.println("Total de cuentas pertenecientes al cliente " + clienteCreado.getNombreCliente() + ": " + cuentasCliente.size());

            System.out.println("\n==================================================");
            System.out.println("--- 5. PASO 5: PROBAR CAMBIO DE ESTADO (INACTIVAR) ---");
            System.out.println("==================================================");

            cuentaService.cambiarEstadoCuenta(cuentaCreada.getIdCuenta(), EstadoCuenta.INACTIVA);
            CuentaDTO cuentaInactiva = cuentaService.obtenerCuentaPorNumero(numeroCuentaPrueba);
            System.out.println("Nuevo estado de la cuenta: " + cuentaInactiva.getEstado());

            System.out.println("\n==================================================");
            System.out.println("--- 6. PASO 6: PROBAR VALIDACIONES DE NEGOCIO ---");
            System.out.println("==================================================");

            try {
                System.out.print("Intentando registrar número de cuenta duplicado... ");
                cuentaService.registrarCuenta(nuevaCuenta);
            } catch (IllegalArgumentException e) {
                System.out.println("Atrapado correctamente: " + e.getMessage());
            }

            try {
                System.out.print("Intentando crear cuenta con cliente inexistente... ");
                CuentaDTO cuentaInvalida = new CuentaDTO();
                cuentaInvalida.setIdCliente(999999);
                cuentaInvalida.setNumeroCuenta("999999999");
                cuentaInvalida.setTipoCuenta("CORRIENTE");
                cuentaService.registrarCuenta(cuentaInvalida);
            } catch (IllegalArgumentException e) {
                System.out.println("Atrapado correctamente: " + e.getMessage());
            }

            System.out.println("\n¡Todas las pruebas del CuentaService finalizaron con éxito!");

        } catch (Exception e) {
            System.err.println("Error durante la prueba de CuentaService: " + e.getMessage());
            e.printStackTrace();
        }
    }
}