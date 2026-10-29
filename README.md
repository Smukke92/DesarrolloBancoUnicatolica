# Banco Unicatólica - Sistema de Gestión Bancaria

Aplicación de escritorio en Java para la gestión integral de clientes, cuentas bancarias y transacciones financieras, con arquitectura en capas y persistencia en MySQL.

## Estructura del Proyecto

```text
DesarrolloBancoUnicatolica/
├── README.md
├── backend/
│   └── BancoUnicatolica/
│       ├── database/
│       │   └── 01-crear-base-datos.sql
│       ├── pom.xml
│       ├── src/main/java/com/unicatolica/banco/
│       │   ├── BancoUnicatolica.java
│       │   ├── config/ConexionBD.java
│       │   ├── dao/
│       │   ├── dto/
│       │   ├── model/
│       │   ├── service/
│       │   └── vistas/
│       └── src/main/resources/db.properties
└── .git/

## Prerrequisitos
JDK: Versión 26 (Ajustable en el pom.xml si se requiere compatibilidad con versiones anteriores).

Gestor de Dependencias: Apache Maven 3.x.

Base de Datos: MySQL Server 8.0 o superior.

## Configuración del Entorno

1.  Crear el archivo de configuración backend/BancoUnicatolica/src/main/resources/db.properties con las credenciales locales:
db.url=jdbc:mysql://localhost:3306/banco_unicatolica
db.user=tu_usuario
db.password=tu_password

2   Inicializar la base de datos ejecutando el script SQL:
mysql -u tu_usuario -p < backend/BancoUnicatolica/database/01-crear-base-datos.sql


## Compilación y Ejecución
Desde la ruta backend/BancoUnicatolica:
# Compilar y empaquetar
mvn clean package

# Ejecutar el archivo ejecutable
java -jar target/BancoUnicatolica-1.0-SNAPSHOT.jar