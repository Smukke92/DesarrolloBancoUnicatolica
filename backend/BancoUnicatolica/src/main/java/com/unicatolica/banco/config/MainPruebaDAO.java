package com.unicatolica.banco.config;

import com.unicatolica.banco.dao.CuentaDAO;
import com.unicatolica.banco.model.Cuenta;

import java.util.List;

public class MainPruebaDAO {

    public static void main(String[] args) {
        CuentaDAO cuentaDAO = new CuentaDAO();
        
        System.out.println("=== INICIANDO PRUEBAS DE CuentaDAO ===\n");

        try {
            // 1. PROBAR REGISTRO DE UNA NUEVA CUENTA (Asociada al idCliente = 2)
            System.out.println("1. Probando método registrar()...");
            Cuenta nuevaCuenta = new Cuenta();
            nuevaCuenta.setNumeroCuenta("998877");
            nuevaCuenta.setIdCliente(3); // Usamos el idCliente 2 como solicitaste
            nuevaCuenta.setTipoCuenta("AHORROS");

            boolean registrado = cuentaDAO.registrar(nuevaCuenta);
            if (registrado) {
                System.out.println("-> Cuenta registrada exitosamente.");
                System.out.println("-> ID generado automáticamente en BD: " + nuevaCuenta.getIdCuenta());
            } else {
                System.out.println("-> No se pudo registrar la cuenta.");
            }

            // 2. PROBAR BUSCAR POR NÚMERO DE CUENTA
            System.out.println("\n2. Probando método buscarPorNumero()...");
            Cuenta cuentaBuscada = cuentaDAO.buscarPorNumero("998877");
            if (cuentaBuscada != null) {
                System.out.println("-> Cuenta encontrada: " + cuentaBuscada.getNumeroCuenta() 
                        + " | Tipo: " + cuentaBuscada.getTipoCuenta() 
                        + " | Estado: " + cuentaBuscada.getEstado() 
                        + " | Saldo: " + cuentaBuscada.getSaldo());
            } else {
                System.out.println("-> Cuenta no encontrada.");
            }

            // 3. PROBAR ACTUALIZAR (Cambiar tipo de cuenta)
            System.out.println("\n3. Probando método actualizar()...");
            if (cuentaBuscada != null) {
                cuentaBuscada.setTipoCuenta("CORRIENTE");
                boolean actualizado = cuentaDAO.actualizar(cuentaBuscada);
                System.out.println("-> ¿Tipo de cuenta actualizado a CORRIENTE? " + actualizado);
            }

            // 4. PROBAR CAMBIAR ESTADO (Ej: Pasar a INACTIVA o BLOQUEADA)
            System.out.println("\n4. Probando método cambiarEstado()...");
            if (cuentaBuscada != null) {
                boolean estadoCambiado = cuentaDAO.cambiarEstado(cuentaBuscada.getIdCuenta(), "INACTIVA");
                System.out.println("-> ¿Estado cambiado a INACTIVA? " + estadoCambiado);
            }

            // 5. PROBAR LISTAR TODAS LAS CUENTAS
            System.out.println("\n5. Probando método listar()...");
            List<Cuenta> listaCuentas = cuentaDAO.listar();
            System.out.println("-> Total de cuentas registradas en el sistema: " + listaCuentas.size());
            for (Cuenta c : listaCuentas) {
                System.out.println("   - ID: " + c.getIdCuenta() + " | Num: " + c.getNumeroCuenta() + " | Cliente ID: " + c.getIdCliente());
            }

            // 6. PROBAR BUSCAR CON FILTROS (método buscar)
            System.out.println("\n6. Probando método buscar() con filtros...");
            List<Cuenta> resultadosBusqueda = cuentaDAO.buscar("9988", null, 2);
            System.out.println("-> Cuentas encontradas con filtro ('9988' y cliente 2): " + resultadosBusqueda.size());

            // 7. PROBAR ELIMINAR CUENTA
            System.out.println("\n7. Probando método eliminar()...");
            if (cuentaBuscada != null) {
                try {
                    boolean eliminado = cuentaDAO.eliminar(cuentaBuscada.getIdCuenta());
                    System.out.println("-> ¿Cuenta eliminada exitosamente? " + eliminado);
                } catch (Exception e) {
                    System.out.println("-> No se pudo eliminar (esperado si hay restricciones o transacciones): " + e.getMessage());
                }
            }

        } catch (Exception e) {
            System.err.println("\n❌ ERROR DURANTE LA EJECUCIÓN DE LAS PRUEBAS:");
            e.printStackTrace();
        }
        
        System.out.println("\n=== FIN DE LAS PRUEBAS ===");
    }
}