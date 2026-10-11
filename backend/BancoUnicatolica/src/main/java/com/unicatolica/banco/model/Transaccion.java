
package com.unicatolica.banco.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Transaccion {

    private Integer idTransaccion;
    private Integer idCuenta;
    private String tipo;
    private BigDecimal monto;
    private LocalDateTime fechaHora;

    public Transaccion() {
    }

    public Transaccion(Integer idTransaccion, Integer idCuenta,
                       String tipo, BigDecimal monto,
                       LocalDateTime fechaHora) {
        this.idTransaccion = idTransaccion;
        this.idCuenta = idCuenta;
        this.tipo = tipo;
        this.monto = monto;
        this.fechaHora = fechaHora;
    }

    public Integer getIdTransaccion() {
        return idTransaccion;
    }

    public void setIdTransaccion(Integer idTransaccion) {
        this.idTransaccion = idTransaccion;
    }

    public Integer getIdCuenta() {
        return idCuenta;
    }

    public void setIdCuenta(Integer idCuenta) {
        this.idCuenta = idCuenta;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public BigDecimal getMonto() {
        return monto;
    }

    public void setMonto(BigDecimal monto) {
        this.monto = monto;
    }

    public LocalDateTime getFechaHora() {
        return fechaHora;
    }

    public void setFechaHora(LocalDateTime fechaHora) {
        this.fechaHora = fechaHora;
    }
}

