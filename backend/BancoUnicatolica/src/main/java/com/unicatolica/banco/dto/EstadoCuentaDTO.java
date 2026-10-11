package com.unicatolica.banco.dto;

import java.math.BigDecimal;
import java.util.List;

public class EstadoCuentaDTO {
    private String numeroCuenta;
    private String tipoCuenta;
    private String estado;
    private BigDecimal saldo;
    private String nombreCliente;
    private String cedulaCliente;
    private List<TransaccionDTO> listaTransacciones;

    public EstadoCuentaDTO() {}

    // Getters y Setters
    public String getNumeroCuenta() { return numeroCuenta; }
    public void setNumeroCuenta(String numeroCuenta) { this.numeroCuenta = numeroCuenta; }

    public String getTipoCuenta() { return tipoCuenta; }
    public void setTipoCuenta(String tipoCuenta) { this.tipoCuenta = tipoCuenta; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public BigDecimal getSaldo() { return saldo; }
    public void setSaldo(BigDecimal saldo) { this.saldo = saldo; }

    public String getNombreCliente() { return nombreCliente; }
    public void setNombreCliente(String nombreCliente) { this.nombreCliente = nombreCliente; }

    public String getCedulaCliente() { return cedulaCliente; }
    public void setCedulaCliente(String cedulaCliente) { this.cedulaCliente = cedulaCliente; }

    public List<TransaccionDTO> getListaTransacciones() { return listaTransacciones; }
    public void setListaTransacciones(List<TransaccionDTO> listaTransacciones) { this.listaTransacciones = listaTransacciones; }
}