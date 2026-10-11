
package com.unicatolica.banco.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Cuenta {

    private Integer idCuenta;
    private String numeroCuenta;
    private Integer idCliente;
    private String tipoCuenta;
    private String estado;
    private BigDecimal saldo;
    private LocalDateTime fechaHoraCreacion;

    public Cuenta() {
    }

    public Cuenta(Integer idCuenta, String numeroCuenta,
                  Integer idCliente, String tipoCuenta,
                  String estado, BigDecimal saldo,
                  LocalDateTime fechaHoraCreacion) {
        this.idCuenta = idCuenta;
        this.numeroCuenta = numeroCuenta;
        this.idCliente = idCliente;
        this.tipoCuenta = tipoCuenta;
        this.estado = estado;
        this.saldo = saldo;
        this.fechaHoraCreacion = fechaHoraCreacion;
    }

    public Integer getIdCuenta() {
        return idCuenta;
    }

    public void setIdCuenta(Integer idCuenta) {
        this.idCuenta = idCuenta;
    }

    public String getNumeroCuenta() {
        return numeroCuenta;
    }

    public void setNumeroCuenta(String numeroCuenta) {
        this.numeroCuenta = numeroCuenta;
    }

    public Integer getIdCliente() {
        return idCliente;
    }

    public void setIdCliente(Integer idCliente) {
        this.idCliente = idCliente;
    }

    public String getTipoCuenta() {
        return tipoCuenta;
    }

    public void setTipoCuenta(String tipoCuenta) {
        this.tipoCuenta = tipoCuenta;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public BigDecimal getSaldo() {
        return saldo;
    }

    public void setSaldo(BigDecimal saldo) {
        this.saldo = saldo;
    }

    public LocalDateTime getFechaHoraCreacion() {
        return fechaHoraCreacion;
    }

    public void setFechaHoraCreacion(
            LocalDateTime fechaHoraCreacion) {
        this.fechaHoraCreacion = fechaHoraCreacion;
    }
}
