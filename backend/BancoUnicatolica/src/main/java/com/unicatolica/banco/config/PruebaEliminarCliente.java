
package com.unicatolica.banco.config;

import com.unicatolica.banco.dao.ClienteDAO;
import com.unicatolica.banco.model.Cliente;
import java.sql.SQLException;
public class PruebaEliminarCliente {

    public static void main(String[] args) {

        ClienteDAO clienteDAO = new ClienteDAO();

        try {
            // Buscar un cliente por su cédula.
            Cliente cliente =
                    clienteDAO.buscarPorCedula("123456789");

            if (cliente == null) {
                System.out.println("El cliente no existe.");
                return;
            }

            // Intentar eliminarlo usando su ID.
            boolean eliminado =
                    clienteDAO.eliminarCliente(cliente.getIdCliente());

            if (eliminado) {
                System.out.println(
                        "Cliente eliminado correctamente.");
            } else {
                System.out.println(
                        "No se pudo eliminar el cliente.");
            }

        } catch (SQLException e) {
            System.out.println("No se pudo eliminar el cliente:");
            System.out.println(e.getMessage());

        } catch (Exception e) {
            System.out.println("Error inesperado:");
            e.printStackTrace();
        }
    }
}
