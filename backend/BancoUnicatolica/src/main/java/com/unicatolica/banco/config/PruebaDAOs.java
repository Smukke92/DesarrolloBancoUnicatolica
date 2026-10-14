package com.unicatolica.banco.config;

import com.unicatolica.banco.dao.ClienteDAO;
import com.unicatolica.banco.dao.CuentaDAO;
import com.unicatolica.banco.dao.TransaccionDAO;
import com.unicatolica.banco.model.Cliente;
import com.unicatolica.banco.model.Cuenta;
import com.unicatolica.banco.model.Transaccion;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class PruebaDAOs {

    public static void main(String[] args) {

        ClienteDAO clienteDAO = new ClienteDAO();
        CuentaDAO cuentaDAO = new CuentaDAO();
        TransaccionDAO transaccionDAO = new TransaccionDAO();

        String cedulaPrueba = "1098765432";
        String numCuentaPrueba = "AHORROS-9999";

        try {
            System.out.println("==========================================");
            System.out.println("   1. PRUEBAS DE CLIENTE DAO");
            System.out.println("==========================================");

            // 1.1 Registrar Cliente
            Cliente nuevoCliente = new Cliente();
            nuevoCliente.setCedula(cedulaPrueba);
            nuevoCliente.setNombreCliente("Carlos Mendoza");
            nuevoCliente.setTelefono("3001234567");
            nuevoCliente.setEmail("carlos.mendoza@email.com");
            nuevoCliente.setDireccion("Calle 10 # 5-20");
            nuevoCliente.setFechaNacimiento(LocalDate.of(1995, 8, 15));
            nuevoCliente.setActivo(true);

            boolean registrado = clienteDAO.registrar(nuevoCliente);
            System.out.println("✔ Registrar Cliente: " + registrado + " (ID: " + nuevoCliente.getIdCliente() + ")");

            // 1.2 Existe por Cédula
            boolean existeCedula = clienteDAO.existePorCedula(cedulaPrueba);
            System.out.println("✔ Existe por Cédula (" + cedulaPrueba + "): " + existeCedula);

            // 1.3 Buscar por Cédula y Buscar por ID
            Cliente clientePorCedula = clienteDAO.buscarPorCedula(cedulaPrueba);
            Cliente clientePorId = clienteDAO.buscarPorId(nuevoCliente.getIdCliente());
            System.out.println("✔ Encontrado por Cédula: " + (clientePorCedula != null ? clientePorCedula.getNombreCliente() : "No"));
            System.out.println("✔ Encontrado por ID: " + (clientePorId != null ? clientePorId.getNombreCliente() : "No"));

            // 1.4 Actualizar Cliente
            nuevoCliente.setTelefono("3119876543");
            nuevoCliente.setDireccion("Carrera 15 # 20-30");
            boolean actualizado = clienteDAO.actualizarCliente(nuevoCliente);
            System.out.println("✔ Actualizar Cliente: " + actualizado);

            // 1.5 Listar y Buscar Clientes
            List<Cliente> activosListados = clienteDAO.listarClientes();
            List<Cliente> buscandoCoincidencia = clienteDAO.buscarClientes("Carlos");
            System.out.println("✔ Total Clientes Activos: " + activosListados.size());
            System.out.println("✔ Coincidencias con 'Carlos': " + buscandoCoincidencia.size());


            System.out.println("\n==========================================");
            System.out.println("   2. PRUEBAS DE CUENTA DAO");
            System.out.println("==========================================");

            // 2.1 Verificar Cliente Activo en CuentaDAO
            boolean esActivo = cuentaDAO.esClienteActivo(nuevoCliente.getIdCliente());
            System.out.println("✔ Cliente Activo validado desde CuentaDAO: " + esActivo);

            // 2.2 Registrar Cuenta
            Cuenta nuevaCuenta = new Cuenta();
            nuevaCuenta.setNumeroCuenta(numCuentaPrueba);
            nuevaCuenta.setIdCliente(nuevoCliente.getIdCliente());
            nuevaCuenta.setTipoCuenta("AHORROS");

            boolean cuentaRegistrada = cuentaDAO.registrar(nuevaCuenta);
            System.out.println("✔ Registrar Cuenta: " + cuentaRegistrada + " (ID Cuenta: " + nuevaCuenta.getIdCuenta() + ")");

            // 2.3 Existe por Número de Cuenta
            boolean existeCuenta = cuentaDAO.existePorNumeroCuenta(numCuentaPrueba);
            System.out.println("✔ Existe Cuenta (" + numCuentaPrueba + "): " + existeCuenta);

            // 2.4 Buscar por Número e ID
            Cuenta cuentaPorNumero = cuentaDAO.buscarPorNumero(numCuentaPrueba);
            Cuenta cuentaPorId = cuentaDAO.buscarPorId(nuevaCuenta.getIdCuenta());
            System.out.println("✔ Encontrada por Número: " + (cuentaPorNumero != null ? cuentaPorNumero.getTipoCuenta() : "No"));
            System.out.println("✔ Encontrada por ID: " + (cuentaPorId != null ? cuentaPorId.getNumeroCuenta() : "No"));

            // 2.5 Actualizar Tipo de Cuenta
            nuevaCuenta.setTipoCuenta("CORRIENTE");
            boolean cuentaActualizada = cuentaDAO.actualizar(nuevaCuenta);
            System.out.println("✔ Actualizar Tipo de Cuenta a CORRIENTE: " + cuentaActualizada);

            // 2.6 Cambiar Estado de Cuenta
            boolean estadoCambiado = cuentaDAO.cambiarEstado(nuevaCuenta.getIdCuenta(), "ACTIVA");
            System.out.println("✔ Cambiar Estado de Cuenta: " + estadoCambiado);

            // 2.7 Listar y Buscar Cuentas
            List<Cuenta> listaCuentas = cuentaDAO.listar();
            List<Cuenta> busquedaCuenta = cuentaDAO.buscar(numCuentaPrueba, "CORRIENTE", nuevoCliente.getIdCliente());
            System.out.println("✔ Total Cuentas de Clientes Activos: " + listaCuentas.size());
            System.out.println("✔ Búsqueda con Filtros: " + busquedaCuenta.size());


            System.out.println("\n==========================================");
            System.out.println("   3. PRUEBAS DE TRANSACCION DAO");
            System.out.println("==========================================");

            // 3.1 Registrar Depósito
            BigDecimal montoDeposito = new BigDecimal("150000.00");
            Transaccion deposito = transaccionDAO.registrarDeposito(numCuentaPrueba, montoDeposito);
            System.out.println("✔ Depósito Exitoso ID: " + deposito.getIdTransaccion() + " | Monto: $" + deposito.getMonto());

            // 3.2 Registrar Retiro
            BigDecimal montoRetiro = new BigDecimal("50000.00");
            Transaccion retiro = transaccionDAO.registrarRetiro(numCuentaPrueba, montoRetiro);
            System.out.println("✔ Retiro Exitoso ID: " + retiro.getIdTransaccion() + " | Monto: $" + retiro.getMonto());

            // 3.3 Verificar nuevo saldo en la cuenta
            Cuenta cuentaActualizadaSaldo = cuentaDAO.buscarPorNumero(numCuentaPrueba);
            System.out.println("✔ Saldo Actualizado en Cuenta: $" + cuentaActualizadaSaldo.getSaldo());

            // 3.4 Listar Transacciones
            List<Transaccion> historial = transaccionDAO.listarPorNumeroCuenta(numCuentaPrueba);
            System.out.println("✔ Historial de Transacciones Registradas: " + historial.size());


            System.out.println("\n==========================================");
            System.out.println("   4. PRUEBAS DE ELIMINACIÓN Y BORRADO LÓGICO");
            System.out.println("==========================================");

            // 4.1 Verificar si la cuenta tiene transacciones asociadas
            boolean tieneTx = cuentaDAO.tieneTransaccionesAsociadas(nuevaCuenta.getIdCuenta());
            System.out.println("✔ ¿La cuenta tiene transacciones asociadas?: " + tieneTx);

            // 4.2 Probar restricción de eliminación de cuenta con transacciones
            try {
                cuentaDAO.eliminar(nuevaCuenta.getIdCuenta());
            } catch (Exception e) {
                System.out.println("✔ Excepción capturada correctamente al intentar eliminar cuenta con TX: " + e.getMessage());
            }

            // 4.3 Borrado lógico del cliente (Soft Delete)
            boolean clienteInactivado = clienteDAO.eliminarCliente(nuevoCliente.getIdCliente());
            System.out.println("✔ Inactivar Cliente (Borrado Lógico): " + clienteInactivado);

            // 4.4 Verificar que las cuentas asociadas al cliente inactivo ya NO aparecen en búsquedas
            Cuenta cuentaInactivaHidden = cuentaDAO.buscarPorNumero(numCuentaPrueba);
            System.out.println("✔ Búsqueda de cuenta asociada a cliente inactivo (debe ser null): " + (cuentaInactivaHidden == null ? "Oculta (Correcto)" : "Visible"));

            // 4.5 Reactivar Cliente
            boolean clienteReactivado = clienteDAO.reactivarCliente(nuevoCliente.getIdCliente());
            System.out.println("✔ Reactivar Cliente: " + clienteReactivado);

            // 4.6 Verificar visibilidad restaurada
            Cuenta cuentaRestaurada = cuentaDAO.buscarPorNumero(numCuentaPrueba);
            System.out.println("✔ Búsqueda de cuenta tras reactivar cliente: " + (cuentaRestaurada != null ? "Visibilidad Restaurada (Correcto)" : "Fallo"));

            System.out.println("\n==========================================");
            System.out.println("   ¡TODAS LAS PRUEBAS FINALIZARON EXITOSAMENTE!");
            System.out.println("==========================================");

        } catch (Exception e) {
            System.err.println("❌ Ocurrió un error durante las pruebas:");
            e.printStackTrace();
        }
    }
}
