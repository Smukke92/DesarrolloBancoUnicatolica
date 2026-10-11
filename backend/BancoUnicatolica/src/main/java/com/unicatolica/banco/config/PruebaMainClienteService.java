package com.unicatolica.banco.config;

import com.unicatolica.banco.dto.ClienteDTO;
import com.unicatolica.banco.service.ClienteService;
import com.unicatolica.banco.service.impl.ClienteServiceImpl;

import java.time.LocalDate;
import java.util.List;

public class PruebaMainClienteService {

    public static void main(String[] args) {
        ClienteService clienteService = new ClienteServiceImpl();

        try {
            System.out.println("--- 1. PROBANDO REGISTRO DE CLIENTE ---");
            ClienteDTO nuevoCliente = new ClienteDTO();
            nuevoCliente.setCedula("10987654321");
            nuevoCliente.setNombreCliente("Carlos Perez");
            nuevoCliente.setTelefono("3001234567");
            nuevoCliente.setEmail("carlos.perez@email.com");
            nuevoCliente.setDireccion("Calle 10 # 5-20");
            nuevoCliente.setFechaNacimiento(LocalDate.of(1995, 8, 15));

            ClienteDTO registrado = clienteService.registrarCliente(nuevoCliente);
            System.out.println("Cliente registrado con ID: " + registrado.getIdCliente());

            System.out.println("\n--- 2. PROBANDO BUSQUEDA DE CLIENTES ---");
            List<ClienteDTO> resultadoBusqueda = clienteService.buscarClientes("Carlos");
            System.out.println("Clientes encontrados: " + resultadoBusqueda.size());

            System.out.println("\n--- 3. PROBANDO ACTUALIZACIÓN DE CLIENTE ---");
            registrado.setTelefono("3119998877");
            registrado.setDireccion("Avenida Siempre Viva 123");
            ClienteDTO actualizado = clienteService.actualizarCliente(registrado);
            System.out.println("Cliente actualizado. Nuevo teléfono: " + actualizado.getTelefono());

            System.out.println("\n--- 4. PROBANDO ELIMINACIÓN DE CLIENTE ---");
            clienteService.eliminarCliente(registrado.getIdCliente());
            System.out.println("Cliente eliminado correctamente.");

        } catch (Exception e) {
            System.err.println("Error durante la prueba del Service: " + e.getMessage());
            e.printStackTrace();
        }
    }
}