# Manual de Usuario - Banco Unicatólica

La interfaz de la aplicación está construida sobre Swing y utiliza un contenedor principal (`VentanaPrincipal`) organizado mediante pestañas (`JTabbedPane`).

## Módulos del Sistema

### 1. Gestión de Clientes (`VistaCliente`)
* **Registro:** Permite el alta de nuevos clientes completando los datos personales.
* **Consulta:** Buscador por ID, número de cédula o criterio de texto general.
* **Modificación:** Actualización de datos de contacto (teléfono, email, dirección).
* **Estado:** Desactivación (*soft delete*) y reactivación de clientes del sistema.

### 2. Gestión de Cuentas (`VistaCuenta`)
* **Apertura:** Vinculación de nuevas cuentas a clientes activos, definiendo tipo de cuenta (`AHORROS` o `CORRIENTE`).
* **Filtros:** Búsqueda por número de cuenta, tipo de cuenta e ID del titular.
* **Cierre:** Eliminación de cuentas (requiere saldo en cero y sin historial de transacciones).

### 3. Transacciones Financieras (`VistaTransaccion`)
* **Operaciones:** Selección de cuenta y registro de **Depósitos** o **Retiros**.
* **Integración:** Actualización en tiempo real del saldo reflejado en el módulo de cuentas.
* **Historial:** Consulta del registro histórico de movimientos por número de cuenta.