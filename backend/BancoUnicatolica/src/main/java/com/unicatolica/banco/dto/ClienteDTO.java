package com.unicatolica.banco.dto;

import java.io.Serializable;

public class ClienteDTO implements Serializable {
    private Long idCliente;
    private String cedula;
    private String nombreCliente;
    private String telefono;
    private String email;
    private String direccion;
    private String fechaNacimiento;

    public ClienteDTO() {}

    public ClienteDTO(Long idCliente, String cedula, String nombreCliente, String telefono, String email, String direccion, String fechaNacimiento) {
        this.idCliente = idCliente;
        this.cedula = cedula;
        this.nombreCliente = nombreCliente;
        this.telefono = telefono;
        this.email = email;
        this.direccion = direccion;
        this.fechaNacimiento = fechaNacimiento;
    }

    // Getters y Setters
    public Long getIdCliente() { return idCliente; }
    public void setIdCliente(Long idCliente) { this.idCliente = idCliente; }

    public String getCedula() { return cedula; }
    public void setCedula(String cedula) { this.cedula = cedula; }

    public String getNombreCliente() { return nombreCliente; }
    public void setNombreCliente(String nombreCliente) { this.nombreCliente = nombreCliente; }

    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) { this.direccion = direccion; }

    public String getFechaNacimiento() { return fechaNacimiento; }
    public void setFechaNacimiento(String fechaNacimiento) { this.fechaNacimiento = fechaNacimiento; }
}