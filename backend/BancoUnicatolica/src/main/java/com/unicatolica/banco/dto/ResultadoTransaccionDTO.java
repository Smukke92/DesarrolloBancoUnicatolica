package com.unicatolica.banco.dto;

import java.io.Serializable;
import java.math.BigDecimal;

public class ResultadoTransaccionDTO implements Serializable {
    private Long idTransaccion;
    private String numeroCuenta;
    private String tipoTransaccion;
    private BigDecimal monto;
    private BigDecimal saldoAnterior;
    private BigDecimal saldoNuevo;
    private String fechaHora;

    public ResultadoTransaccionDTO() {}

    // Getters y Setters
    public Long getIdTransaccion() { return idTransaccion; }
    public void setIdTransaccion(Long idTransaccion) { this.idTransaccion = idTransaccion; }

    public String getNumeroCuenta() { return numeroCuenta; }
    public void setNumeroCuenta(String numeroCuenta) { this.numeroCuenta = numeroCuenta; }

    public String getTipoTransaccion() { return tipoTransaccion; }
    public void setTipoTransaccion(String tipoTransaccion) { this.tipoTransaccion = tipoTransaccion; }

    public BigDecimal getMonto() { return monto; }
    public void setMonto(BigDecimal monto) { this.monto = monto; }

    public BigDecimal getSaldoAnterior() { return saldoAnterior; }
    public void setSaldoAnterior(BigDecimal saldoAnterior) { this.saldoAnterior = saldoAnterior; }

    public BigDecimal getSaldoNuevo() { return saldoNuevo; }
    public void setSaldoNuevo(BigDecimal saldoNuevo) { this.saldoNuevo = saldoNuevo; }

    public String getFechaHora() { return fechaHora; }
    public void setFechaHora(String fechaHora) { this.fechaHora = fechaHora; }
}