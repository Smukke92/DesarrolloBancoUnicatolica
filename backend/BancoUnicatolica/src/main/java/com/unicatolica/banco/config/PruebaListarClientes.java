
package com.unicatolica.banco.config;

import com.unicatolica.banco.dao.ClienteDAO;
import com.unicatolica.banco.model.Cliente;
import java.util.List;

public class PruebaListarClientes {

    public static void main(String[] args) {

        ClienteDAO clienteDAO = new ClienteDAO();

        try {
            List<Cliente> clientes = clienteDAO.listarClientes();

            if (clientes.isEmpty()) {
                System.out.println("No hay clientes registrados.");
                return;
            }

            System.out.println("Clientes registrados: " + clientes.size());

            for (Cliente cliente : clientes) {
                System.out.println("-------------------------");
                System.out.println("ID: " + cliente.getIdCliente());
                System.out.println("Cédula: " + cliente.getCedula());
                System.out.println("Nombre: " + cliente.getNombreCliente());
                System.out.println("Teléfono: " + cliente.getTelefono());
                System.out.println("Email: " + cliente.getEmail());
            }

        } catch (Exception e) {
            System.out.println("Error al listar los clientes:");
            e.printStackTrace();
        }
    }
}
