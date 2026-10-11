package com.unicatolica.banco.dto;

public class FiltroCuentaDTO {

    private String numeroCuenta;
    private String tipoCuenta;
    private Integer idCliente;

    public FiltroCuentaDTO() {
    }

    public FiltroCuentaDTO(String numeroCuenta, String tipoCuenta, Integer idCliente) {
        this.numeroCuenta = numeroCuenta;
        this.tipoCuenta = tipoCuenta;
        this.idCliente = idCliente;
    }

    public String getNumeroCuenta() {
        return numeroCuenta;
    }

    public void setNumeroCuenta(String numeroCuenta) {
        this.numeroCuenta = numeroCuenta;
    }

    public String getTipoCuenta() {
        return tipoCuenta;
    }

    public void setTipoCuenta(String tipoCuenta) {
        this.tipoCuenta = tipoCuenta;
    }

    public Integer getIdCliente() {
        return idCliente;
    }

    public void setIdCliente(Integer idCliente) {
        this.idCliente = idCliente;
    }
}