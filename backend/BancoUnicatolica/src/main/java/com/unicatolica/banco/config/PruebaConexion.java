
package com.unicatolica.banco.config;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public class PruebaConexion {

    public static void main(String[] args) {

        try (Connection conexion = ConexionBD.conectar();
             Statement sentencia = conexion.createStatement();
             ResultSet resultado =
                     sentencia.executeQuery("SELECT 1")) {

            if (resultado.next()) {
                System.out.println("¡Conexión exitosa con MySQL!");
                System.out.println("Resultado: " + resultado.getInt(1));
            }

        } catch (Exception e) {
            System.out.println("Error de conexión:");
            e.printStackTrace();
        }
    }
}

