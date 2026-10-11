package com.unicatolica.banco.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class TransaccionDTO {
    private Integer idTransaccion;
    private LocalDateTime fechaHora;
    private String tipoTransaccion;
    private BigDecimal monto;
    private Integer idCuenta;

    public TransaccionDTO() {}

    // Getters y Setters
    public Integer getIdTransaccion() { return idTransaccion; }
    public void setIdTransaccion(Integer idTransaccion) { this.idTransaccion = idTransaccion; }

    public LocalDateTime getFechaHora() { return fechaHora; }
    public void setFechaHora(LocalDateTime fechaHora) { this.fechaHora = fechaHora; }

    public String getTipoTransaccion() { return tipoTransaccion; }
    public void setTipoTransaccion(String tipoTransaccion) { this.tipoTransaccion = tipoTransaccion; }

    public BigDecimal getMonto() { return monto; }
    public void setMonto(BigDecimal monto) { this.monto = monto; }

    public Integer getIdCuenta() { return idCuenta; }
    public void setIdCuenta(Integer idCuenta) { this.idCuenta = idCuenta; }
}