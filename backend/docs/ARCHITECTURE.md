# Especificación Técnica de Arquitectura - Banco Unicatólica

## Organización por Capas

El sistema aplica el patrón de arquitectura en capas para garantizar la separación de responsabilidades:

| Capa          | Paquete                           | Responsabilidad |

| **Config**    | `com.unicatolica.banco.config`    | Gestión de conexiones a base de datos mediante JDBC. |
| **Modelo**    | `com.unicatolica.banco.model`     | Entidades del dominio (`Cliente`, `Cuenta`, `Transaccion`). |
| **DTO**       | `com.unicatolica.banco.dto`       | Objetos para la transferencia de datos entre vistas y servicios. |
| **DAO**       | `com.unicatolica.banco.dao`       | Acceso a la base de datos (CRUD, sentencias preparadas y consultas). |
| **Servicio**  | `com.unicatolica.banco.service`   | Lógica de negocio, validaciones transaccionales e integridad. |
| **Vistas**    | `com.unicatolica.banco.vistas`    | Interfaz gráfica de usuario basada en Swing. |

---

## Modelo de Dominio
### Entidades Principales

* **Cliente:** `idCliente`, `cedula`, `nombreCliente`, `telefono`, `email`, `direccion`, `fechaNacimiento`, `activo`.
* **Cuenta:** `idCuenta`, `numeroCuenta`, `idCliente`, `tipoCuenta` (`AHORROS`, `CORRIENTE`), `estado` (`ACTIVA`, `INACTIVA`), `saldo`, `fechaHoraCreacion`.
* **Transaccion:** `idTransaccion`, `idCuenta`, `tipo` (`DEPOSITO`, `RETIRO`), `monto`, `fechaHora`.


## Reglas de Negocio y Lógica de Servicios
### `ClienteServiceImpl`
* Cédula, nombre, teléfono, dirección e email obligatorios.
* Validación de formato de email válido.
* Verificación de cédulas no duplicadas.
* Soporte para borrado lógico (*soft delete*) y reactivación de clientes.

### `CuentaServiceImpl`
* El cliente asociado debe estar registrado y activo.
* Número de cuenta único y no vacío.
* Tipos de cuenta válidos (`AHORROS`, `CORRIENTE`).
* Permite eliminación solo si el saldo es igual a `$0` y no registra transacciones asociadas.

### `TransaccionServiceImpl`
* Manejo de transacciones financieras atómicas (depósitos y retiros).
* Validación de montos estricta (`monto > 0`).
* Verificación de fondos suficientes antes de realizar un retiro.
* Confirmación de que la cuenta y el cliente se encuentren en estado `ACTIVO`.
* Cálculo automático del saldo anterior y el saldo actualizado.


## Diseño de Base de Datos

Base de datos: `banco_unicatolica` (Script: `01-crear-base-datos.sql`)

### Restricciones Clave
* Restricción `UNIQUE` sobre la cédula del cliente y número de cuenta.
* Claves foráneas con integridad referencial hacia `Cuenta` y `Cliente`.
* Validaciones `CHECK`: `saldo >= 0` y `monto > 0`.
* Listas de valores permitidos (`ENUM` / `CHECK`) para `tipoCuenta` y `estado`.