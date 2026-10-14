CREATE DATABASE IF NOT EXISTS banco_unicatolica
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE banco_unicatolica;

CREATE TABLE Cliente (
    idCliente INT AUTO_INCREMENT PRIMARY KEY,
    cedula VARCHAR(30) NOT NULL UNIQUE,
    nombreCliente VARCHAR(120) NOT NULL,
    telefono VARCHAR(30),
    email VARCHAR(120),
    direccion VARCHAR(200),
    fechaNacimiento DATE NOT NULL,
    activo BOOLEAN NOT NULL DEFAULT TRUE
);

CREATE TABLE Cuenta (
    idCuenta INT AUTO_INCREMENT PRIMARY KEY,
    numeroCuenta VARCHAR(30) NOT NULL UNIQUE,
    idCliente INT NOT NULL,
    tipoCuenta VARCHAR(20) NOT NULL,
    estado VARCHAR(20) NOT NULL DEFAULT 'ACTIVA',
    saldo DECIMAL(15,2) NOT NULL DEFAULT 0.00,
    fechaHoraCreacion DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_Cuenta_Cliente
        FOREIGN KEY (idCliente)
        REFERENCES Cliente(idCliente),

    CONSTRAINT chk_Cuenta_tipo
        CHECK (tipoCuenta IN ('AHORROS', 'CORRIENTE')),

    CONSTRAINT chk_Cuenta_estado
        CHECK (estado IN ('ACTIVA', 'INACTIVA')),

    CONSTRAINT chk_Cuenta_saldo
        CHECK (saldo >= 0)
);

CREATE TABLE Transaccion (
    idTransaccion INT AUTO_INCREMENT PRIMARY KEY,
    idCuenta INT NOT NULL,
    tipo VARCHAR(20) NOT NULL,
    monto DECIMAL(15,2) NOT NULL,
    fechaHora DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_Transaccion_Cuenta
        FOREIGN KEY (idCuenta)
        REFERENCES Cuenta(idCuenta),

    CONSTRAINT chk_Transaccion_tipo
        CHECK (tipo IN ('DEPOSITO', 'RETIRO')),

    CONSTRAINT chk_Transaccion_monto
        CHECK (monto > 0)
);