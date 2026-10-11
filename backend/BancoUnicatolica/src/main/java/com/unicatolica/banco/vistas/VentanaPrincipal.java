package com.unicatolica.banco.vistas;

import javax.swing.*;
import java.awt.*;

public class VentanaPrincipal extends JFrame {

    private JTabbedPane tabbedPane;
    private VistaCliente panelClientes; // Declarado directamente como VistaCliente
    private VistaCuenta panelCuentas;
    private VistaTransaccion panelTransacciones;

    public VentanaPrincipal() {
        // 1. Configuración de la ventana principal
        setTitle("Banco Unicatólica - Sistema de Gestión Bancaria");
        setSize(950, 650);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Centra la ventana en pantalla
        setLayout(new BorderLayout());

        // 2. Encabezado principal (Header)
        JPanel panelHeader = new JPanel();
        panelHeader.setBackground(new Color(26, 54, 93)); // Azul institucional
        JLabel lblTitulo = new JLabel("SISTEMA DE GESTIÓN BANCARIA");
        lblTitulo.setForeground(Color.WHITE);
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 18));
        panelHeader.add(lblTitulo);
        add(panelHeader, BorderLayout.NORTH);

        // 3. Crear el contenedor de pestañas
        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Arial", Font.PLAIN, 14));

        // 4. Instanciar los paneles modulares de cada pestaña
        panelClientes = new VistaCliente(); 
        panelCuentas = new VistaCuenta();
        panelTransacciones = new VistaTransaccion();

        // 5. Agregar las pestañas
        tabbedPane.addTab("Clientes", panelClientes);
        tabbedPane.addTab("Cuentas", panelCuentas);
        tabbedPane.addTab("Transacciones", panelTransacciones);

        // 6. Agregar el TabbedPane al centro
        add(tabbedPane, BorderLayout.CENTER);

        // 7. Pie de página (Barra de estado)
        JPanel panelFooter = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel lblStatus = new JLabel("  Estado: Listo | Conectado a Banco Unicatólica");
        lblStatus.setFont(new Font("Arial", Font.ITALIC, 11));
        panelFooter.add(lblStatus);
        add(panelFooter, BorderLayout.SOUTH);
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(() -> {
            new VentanaPrincipal().setVisible(true);
        });
    }
}