package com.unicatolica.banco.dto;

import java.io.Serializable;
import java.time.LocalDate;

public class ClienteDTO implements Serializable {
    
    private static final long serialVersionUID = 1L;

    private Integer idCliente;
    private String cedula;
    private String nombreCliente;
    private String telefono;
    private String email;
    private String direccion;
    private LocalDate fechaNacimiento;
    private Boolean activo;

    public ClienteDTO() {}

    public ClienteDTO(Integer idCliente, String cedula, String nombreCliente, String telefono, 
                      String email, String direccion, LocalDate fechaNacimiento, Boolean activo) {
        this.idCliente = idCliente;
        this.cedula = cedula;
        this.nombreCliente = nombreCliente;
        this.telefono = telefono;
        this.email = email;
        this.direccion = direccion;
        this.fechaNacimiento = fechaNacimiento;
        this.activo = activo;
    }

    // Getters y Setters
    public Integer getIdCliente() { return idCliente; }
    public void setIdCliente(Integer idCliente) { this.idCliente = idCliente; }

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

    public LocalDate getFechaNacimiento() { return fechaNacimiento; }
    public void setFechaNacimiento(LocalDate fechaNacimiento) { this.fechaNacimiento = fechaNacimiento; }

    public Boolean getActivo() { return activo; }
    
    // Método auxiliar común para tipos booleanos en Java / frameworks (JSON, JSF, Thymeleaf)
    public Boolean isActivo() { return activo; }
    
    public void setActivo(Boolean activo) { this.activo = activo; }
}