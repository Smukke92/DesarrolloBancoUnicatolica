package com.unicatolica.banco.dto;

import java.io.Serializable;
import java.math.BigDecimal;

public class CuentaDTO implements Serializable {
    private Long idCuenta;
    private String numeroCuenta;
    private String tipoCuenta; // AHORROS o CORRIENTE
    private String estado;     // ACTIVA o INACTIVA
    private BigDecimal saldo;
    private Long idCliente;
    private String nombreCliente;

    public CuentaDTO() {}

    public CuentaDTO(Long idCuenta, String numeroCuenta, String tipoCuenta, String estado, BigDecimal saldo, Long idCliente, String nombreCliente) {
        this.idCuenta = idCuenta;
        this.numeroCuenta = numeroCuenta;
        this.tipoCuenta = tipoCuenta;
        this.estado = estado;
        this.saldo = saldo;
        this.idCliente = idCliente;
        this.nombreCliente = nombreCliente;
    }

    // Getters y Setters
    public Long getIdCuenta() { return idCuenta; }
    public void setIdCuenta(Long idCuenta) { this.idCuenta = idCuenta; }

    public String getNumeroCuenta() { return numeroCuenta; }
    public void setNumeroCuenta(String numeroCuenta) { this.numeroCuenta = numeroCuenta; }

    public String getTipoCuenta() { return tipoCuenta; }
    public void setTipoCuenta(String tipoCuenta) { this.tipoCuenta = tipoCuenta; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public BigDecimal getSaldo() { return saldo; }
    public void setSaldo(BigDecimal saldo) { this.saldo = saldo; }

    public Long getIdCliente() { return idCliente; }
    public void setIdCliente(Long idCliente) { this.idCliente = idCliente; }

    public String getNombreCliente() { return nombreCliente; }
    public void setNombreCliente(String nombreCliente) { this.nombreCliente = nombreCliente; }
}