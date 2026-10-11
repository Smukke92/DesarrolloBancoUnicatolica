package com.unicatolica.banco.vistas;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import java.awt.*;
import java.math.BigDecimal;

public class VistaTransaccion extends JPanel {

    // Componentes para la sección de Operaciones (Depósito / Retiro)
    private JTextField txtCuentaOp;
    private JButton btnConsultarOp;
    private JLabel lblInfoTitular;
    private JLabel lblInfoTipoEstado;
    private JLabel lblInfoSaldo;
    private JTextField txtMonto;
    private JButton btnDeposito;
    private JButton btnRetiro;

    // Componentes para la sección de Estado de Cuenta e Historial
    private JTextField txtCuentaHist;
    private JButton btnConsultarHist;
    private JLabel lblHistTitular;
    private JLabel lblHistCedula;
    private JLabel lblHistTipoEstado;
    private JLabel lblHistSaldo;
    private JTable tablaTransacciones;
    private DefaultTableModel modeloTabla;

    public VistaTransaccion() {
        setLayout(new BorderLayout(10, 10));
        setBackground(new Color(247, 250, 252));

        JTabbedPane subTabbedPane = new JTabbedPane();
        subTabbedPane.setFont(new Font("Arial", Font.PLAIN, 13));

        subTabbedPane.addTab("Realizar Depósito / Retiro", crearPanelOperaciones());
        subTabbedPane.addTab("Estado de Cuenta e Historial", crearPanelEstadoCuenta());

        configurarValidacionesCampos();
        asignarEventos();

        add(subTabbedPane, BorderLayout.CENTER);
    }

    private JPanel crearPanelOperaciones() {
        JPanel panelPrincipal = new JPanel(new GridBagLayout());
        panelPrincipal.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(26, 54, 93)),
                "Registro de Depósitos y Retiros",
                TitledBorder.LEFT,
                TitledBorder.TOP,
                new Font("Arial", Font.BOLD, 14),
                new Color(26, 54, 93)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        txtCuentaOp = new JTextField(15);
        btnConsultarOp = new JButton("Consultar Cuenta");

        lblInfoTitular = new JLabel("-");
        lblInfoTipoEstado = new JLabel("-");
        lblInfoSaldo = new JLabel("$ 0.00");
        lblInfoSaldo.setFont(new Font("Arial", Font.BOLD, 14));
        lblInfoSaldo.setForeground(new Color(26, 54, 93));

        txtMonto = new JTextField(15);
        btnDeposito = new JButton("Registrar Depósito");
        btnDeposito.setBackground(new Color(40, 167, 69));
        btnDeposito.setForeground(Color.WHITE);

        btnRetiro = new JButton("Registrar Retiro");
        btnRetiro.setBackground(new Color(220, 53, 69));
        btnRetiro.setForeground(Color.WHITE);

        // Fila 0: Búsqueda de Cuenta
        gbc.gridx = 0; gbc.gridy = 0;
        panelPrincipal.add(new JLabel("Número de Cuenta*:"), gbc);
        gbc.gridx = 1;
        panelPrincipal.add(txtCuentaOp, gbc);
        gbc.gridx = 2;
        panelPrincipal.add(btnConsultarOp, gbc);

        // Fila 1: Separador
        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 3;
        panelPrincipal.add(new JSeparator(), gbc);

        // Fila 2: Información Titular
        gbc.gridwidth = 1;
        gbc.gridx = 0; gbc.gridy = 2;
        panelPrincipal.add(new JLabel("Titular:"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 2;
        panelPrincipal.add(lblInfoTitular, gbc);

        // Fila 3: Tipo y Estado
        gbc.gridwidth = 1;
        gbc.gridx = 0; gbc.gridy = 3;
        panelPrincipal.add(new JLabel("Tipo / Estado:"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 2;
        panelPrincipal.add(lblInfoTipoEstado, gbc);

        // Fila 4: Saldo Actual
        gbc.gridwidth = 1;
        gbc.gridx = 0; gbc.gridy = 4;
        panelPrincipal.add(new JLabel("Saldo Disponible:"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 2;
        panelPrincipal.add(lblInfoSaldo, gbc);

        // Fila 5: Separador
        gbc.gridx = 0; gbc.gridy = 5; gbc.gridwidth = 3;
        panelPrincipal.add(new JSeparator(), gbc);

        // Fila 6: Monto
        gbc.gridwidth = 1;
        gbc.gridx = 0; gbc.gridy = 6;
        panelPrincipal.add(new JLabel("Monto de la Operación ($)*:"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 2;
        panelPrincipal.add(txtMonto, gbc);

        // Fila 7: Botones
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 5));
        panelBotones.add(btnDeposito);
        panelBotones.add(btnRetiro);

        gbc.gridx = 0; gbc.gridy = 7; gbc.gridwidth = 3;
        gbc.insets = new Insets(15, 8, 8, 8);
        panelPrincipal.add(panelBotones, gbc);

        return panelPrincipal;
    }

    private JPanel crearPanelEstadoCuenta() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(26, 54, 93)),
                "Consulta Consolidada de Estado de Cuenta)",
                TitledBorder.LEFT,
                TitledBorder.TOP,
                new Font("Arial", Font.BOLD, 14),
                new Color(26, 54, 93)
        ));

        JPanel panelNorte = new JPanel(new BorderLayout(5, 5));
        
        JPanel panelBusqueda = new JPanel(new FlowLayout(FlowLayout.LEFT));
        txtCuentaHist = new JTextField(15);
        btnConsultarHist = new JButton("🔍 Ver Estado de Cuenta");
        panelBusqueda.add(new JLabel("Número de Cuenta:"));
        panelBusqueda.add(txtCuentaHist);
        panelBusqueda.add(btnConsultarHist);

        JPanel panelDatosConsolidados = new JPanel(new GridLayout(2, 2, 10, 5));
        panelDatosConsolidados.setBorder(BorderFactory.createEtchedBorder());
        lblHistTitular = new JLabel(" Titular: -");
        lblHistCedula = new JLabel(" Cédula: -");
        lblHistTipoEstado = new JLabel(" Tipo / Estado: -");
        lblHistSaldo = new JLabel(" Saldo Actual: $ 0.00");
        lblHistSaldo.setFont(new Font("Arial", Font.BOLD, 12));

        panelDatosConsolidados.add(lblHistTitular);
        panelDatosConsolidados.add(lblHistCedula);
        panelDatosConsolidados.add(lblHistTipoEstado);
        panelDatosConsolidados.add(lblHistSaldo);

        panelNorte.add(panelBusqueda, BorderLayout.NORTH);
        panelNorte.add(panelDatosConsolidados, BorderLayout.CENTER);

        panel.add(panelNorte, BorderLayout.NORTH);

        String[] columnas = {"Código Transacción", "Fecha / Hora", "Tipo Movimiento", "Valor ($)"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tablaTransacciones = new JTable(modeloTabla);
        JScrollPane scrollPane = new JScrollPane(tablaTransacciones);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private void asignarEventos() {
        btnConsultarOp.addActionListener(e -> accionConsultarCuentaOp());
        btnDeposito.addActionListener(e -> accionProcesarOperacion("DEPOSITO"));
        btnRetiro.addActionListener(e -> accionProcesarOperacion("RETIRO"));
        btnConsultarHist.addActionListener(e -> accionConsultarHistorial());
    }

    private void accionConsultarCuentaOp() {
        String numCuenta = txtCuentaOp.getText().trim();
        if (numCuenta.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Ingrese un número de cuenta para consultar.",
                    "Campo Requerido", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // AQUÍ SE INVOCARÁ EL SERVICE REAL:
        // CuentaDTO cuenta = transaccionService.consultarCuentaParaTransaccion(numCuenta);
    }

    private void accionProcesarOperacion(String tipoOperacion) {
        String numCuenta = txtCuentaOp.getText().trim();
        String montoTexto = txtMonto.getText().trim();

        if (numCuenta.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Debe ingresar y consultar una cuenta antes de realizar una operación.",
                    "Cuenta Requerida", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (montoTexto.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Ingrese el monto a " + (tipoOperacion.equals("DEPOSITO") ? "depositar." : "retirar."),
                    "Monto Requerido", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            BigDecimal monto = new BigDecimal(montoTexto);
            if (monto.compareTo(BigDecimal.ZERO) <= 0) {
                JOptionPane.showMessageDialog(this,
                        "El monto de la transacción debe ser mayor que cero ($ 0.00).",
                        "Monto Inválido", JOptionPane.WARNING_MESSAGE);
                return;
            }

            if (tipoOperacion.equals("DEPOSITO")) {
                // AQUÍ SE INVOCARÁ EL SERVICE REAL:
                // ResultadoTransaccionDTO res = transaccionService.registrarDeposito(numCuenta, monto);
            } else {
                // AQUÍ SE INVOCARÁ EL SERVICE REAL:
                // ResultadoTransaccionDTO res = transaccionService.registrarRetiro(numCuenta, monto);
            }

            JOptionPane.showMessageDialog(this,
                    "Operación de " + tipoOperacion + " procesada exitosamente.",
                    "Transacción Completada", JOptionPane.INFORMATION_MESSAGE);

            txtMonto.setText("");

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this,
                    "Ingrese un monto numérico válido.",
                    "Formato Incorrecto", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void accionConsultarHistorial() {
        String numCuenta = txtCuentaHist.getText().trim();
        if (numCuenta.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Ingrese un número de cuenta para consultar el estado de cuenta e historial.",
                    "Campo Requerido", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // AQUÍ SE INVOCARÁ EL SERVICE REAL:
        // EstadoCuentaDTO estado = transaccionService.consultarEstadoCuenta(numCuenta);
    }

    private void configurarValidacionesCampos() {
        aplicarFiltro(txtCuentaOp, "[0-9]+", 12);
        aplicarFiltro(txtCuentaHist, "[0-9]+", 12);
        aplicarFiltro(txtMonto, "[0-9\\.]+", 12);
    }

    private void aplicarFiltro(JTextField campo, String regex, int maxCaracteres) {
        ((AbstractDocument) campo.getDocument()).setDocumentFilter(new DocumentFilter() {
            @Override
            public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr) throws BadLocationException {
                if (string == null) return;
                int nuevoLargo = fb.getDocument().getLength() + string.length();
                if (nuevoLargo <= maxCaracteres && string.matches(regex)) {
                    super.insertString(fb, offset, string, attr);
                }
            }

            @Override
            public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
                if (text == null) return;
                int nuevoLargo = fb.getDocument().getLength() - length + text.length();
                if (text.isEmpty() || (nuevoLargo <= maxCaracteres && text.matches(regex))) {
                    super.replace(fb, offset, length, text, attrs);
                }
            }
        });
    }
}