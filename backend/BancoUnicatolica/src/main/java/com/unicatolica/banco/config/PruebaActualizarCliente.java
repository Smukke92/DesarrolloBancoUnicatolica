
package com.unicatolica.banco.config;

import com.unicatolica.banco.dao.ClienteDAO;
import com.unicatolica.banco.model.Cliente;
import java.time.LocalDate;

public class PruebaActualizarCliente {

    public static void main(String[] args) {

        ClienteDAO clienteDAO = new ClienteDAO();

        try {
            // Buscar el cliente que vamos a actualizar.
            Cliente cliente =
                    clienteDAO.buscarPorCedula("123456789");

            if (cliente == null) {
                System.out.println("El cliente no existe.");
                return;
            }

            // Modificar sus datos personales.
            cliente.setNombreCliente("Juan Perez Actualizado");
            cliente.setTelefono("3109876543");
            cliente.setEmail("juan.actualizado@example.com");
            cliente.setDireccion("Bogota");
            cliente.setFechaNacimiento(LocalDate.of(2005, 8, 17));

            // Guardar los cambios.
            boolean actualizado =
                    clienteDAO.actualizarCliente(cliente);

            if (actualizado) {
                System.out.println("Cliente actualizado correctamente.");
            } else {
                System.out.println(
                        "No se realizaron cambios en el cliente.");
            }

        } catch (Exception e) {
            System.out.println("Error al actualizar el cliente:");
            e.printStackTrace();
        }
    }
}
