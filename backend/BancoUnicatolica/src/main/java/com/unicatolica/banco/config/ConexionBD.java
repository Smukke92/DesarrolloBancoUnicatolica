
package com.unicatolica.banco.config;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class ConexionBD {

    public static Connection conectar()
            throws SQLException, IOException {

        Properties propiedades = new Properties();

        try (InputStream archivo =
                ConexionBD.class.getClassLoader()
                    .getResourceAsStream("db.properties")) {

            if (archivo == null) {
                throw new IOException(
                    "No se encontró el archivo db.properties"
                );
            }

            propiedades.load(archivo);
        }

        String url = propiedades.getProperty("db.url");
        String usuario = propiedades.getProperty("db.user");
        String contrasena = propiedades.getProperty("db.password");

        return DriverManager.getConnection(
            url, usuario, contrasena
        );
    }
}
