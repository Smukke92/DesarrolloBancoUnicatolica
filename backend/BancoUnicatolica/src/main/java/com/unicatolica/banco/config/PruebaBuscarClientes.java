
package com.unicatolica.banco.config;

import com.unicatolica.banco.dao.ClienteDAO;
import com.unicatolica.banco.model.Cliente;
import java.util.List;

public class PruebaBuscarClientes {

    public static void main(String[] args) {

        ClienteDAO clienteDAO = new ClienteDAO();

        try {
            String criterio = "Juan";

            List<Cliente> clientes =
                    clienteDAO.buscarClientes(criterio);

            if (clientes.isEmpty()) {
                System.out.println(
                        "No se encontraron clientes con: " + criterio);
                return;
            }

            System.out.println(
                    "Resultados encontrados: " + clientes.size());

            for (Cliente cliente : clientes) {
                System.out.println("-------------------------");
                System.out.println("ID: " + cliente.getIdCliente());
                System.out.println("Cédula: " + cliente.getCedula());
                System.out.println(
                        "Nombre: " + cliente.getNombreCliente());
            }

        } catch (Exception e) {
            System.out.println("Error al buscar clientes:");
            e.printStackTrace();
        }
    }
}
