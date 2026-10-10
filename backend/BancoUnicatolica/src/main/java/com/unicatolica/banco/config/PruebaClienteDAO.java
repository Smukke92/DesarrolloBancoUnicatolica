
package com.unicatolica.banco.config;

import com.unicatolica.banco.dao.ClienteDAO;
import com.unicatolica.banco.model.Cliente;
import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;

public class PruebaClienteDAO {

    public static void main(String[] args) {

        Cliente cliente = new Cliente(
            null,
            "123456789",
            "Juan Perez",
            "3001234567",
            "juan@example.com",
            "Cali",
            LocalDate.of(2005, 8, 17)
        );

        ClienteDAO clienteDAO = new ClienteDAO();

        try {
            boolean registrado = clienteDAO.registrar(cliente);

            if (registrado) {
                System.out.println("Cliente registrado correctamente.");
                System.out.println("ID generado: " + cliente.getIdCliente());
            } else {
                System.out.println("No se pudo registrar el cliente.");
            }

        } catch (SQLException | IOException e) {
            System.out.println("Error al registrar el cliente:");
            e.printStackTrace();
        }
    }
}
