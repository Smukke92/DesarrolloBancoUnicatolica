
package com.unicatolica.banco.config;

import com.unicatolica.banco.dao.ClienteDAO;
import com.unicatolica.banco.model.Cliente;

public class PruebaBuscarCliente {

    public static void main(String[] args) {

        ClienteDAO clienteDAO = new ClienteDAO();

        try {
            Cliente cliente = clienteDAO.buscarPorCedula("123456789");

            if (cliente != null) {
                System.out.println("Cliente encontrado:");
                System.out.println("ID: " + cliente.getIdCliente());
                System.out.println("Cédula: " + cliente.getCedula());
                System.out.println("Nombre: " + cliente.getNombreCliente());
                System.out.println("Teléfono: " + cliente.getTelefono());
                System.out.println("Email: " + cliente.getEmail());
                System.out.println("Dirección: " + cliente.getDireccion());
                System.out.println("Fecha de nacimiento: "
                        + cliente.getFechaNacimiento());
            } else {
                System.out.println("No existe un cliente con esa cédula.");
            }

        } catch (Exception e) {
            System.out.println("Error al buscar el cliente:");
            e.printStackTrace();
        }
    }
}
