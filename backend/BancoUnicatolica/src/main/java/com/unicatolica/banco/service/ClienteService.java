package com.unicatolica.banco.service;

import com.unicatolica.banco.dto.ClienteDTO;
import java.util.List;

public interface ClienteService {

    ClienteDTO registrarCliente(ClienteDTO cliente);

    ClienteDTO buscarPorId(Integer idCliente);

    ClienteDTO buscarPorCedula(String cedula);

    List<ClienteDTO> listarClientes();

    List<ClienteDTO> buscarClientes(String criterio);

    ClienteDTO actualizarCliente(ClienteDTO cliente);

    void eliminarCliente(Integer idCliente);

    void reactivarCliente(Integer idCliente);
}