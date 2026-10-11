package com.unicatolica.banco.vistas;

import com.unicatolica.banco.dto.CuentaDTO;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import java.awt.*;

public class VistaCuenta extends JPanel {

    // Componentes del formulario
    private JTextField txtNumeroCuenta;
    private JTextField txtIdCliente;
    private JComboBox<String> cbTipoCuenta;
    private JComboBox<String> cbEstadoCuenta;
    private JTextField txtSaldoDisplay;

    private JButton btnRegistrar;
    private JButton btnModificar;
    private JButton btnDesactivar;
    private JButton btnLimpiar;

    // Componentes de la tabla
    private JTextField txtBuscarCuenta;
    private JButton btnBuscar;
    private JTable tablaCuentas;
    private DefaultTableModel modeloTabla;

    public VistaCuenta() {
        setLayout(new BorderLayout(10, 10));
        setBackground(new Color(247, 250, 252));

        // 1. Crear paneles
        JPanel panelFormulario = crearPanelFormulario();
        JPanel panelTabla = crearPanelTabla();

        // 2. Aplicar validaciones de entrada
        configurarValidacionesCampos();

        // 3. Asignar Eventos/Listeners
        asignarEventos();

        // 4. Organizar layout con JSplitPane
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, panelFormulario, panelTabla);
        splitPane.setDividerLocation(380);
        splitPane.setResizeWeight(0.4);

        add(splitPane, BorderLayout.CENTER);
    }

    private JPanel crearPanelFormulario() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(26, 54, 93)),
                "Gestión de Cuentas Bancarias",
                TitledBorder.LEFT,
                TitledBorder.TOP,
                new Font("Arial", Font.BOLD, 14),
                new Color(26, 54, 93)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        txtNumeroCuenta = new JTextField(15);
        txtIdCliente = new JTextField(15);
        cbTipoCuenta = new JComboBox<>(new String[]{"AHORROS", "CORRIENTE"});
        cbEstadoCuenta = new JComboBox<>(new String[]{"ACTIVA", "INACTIVA"});
        
        txtSaldoDisplay = new JTextField("$ 0.00", 15);
        txtSaldoDisplay.setEditable(false);
        txtSaldoDisplay.setBackground(new Color(230, 235, 240));

        // Fila 0: ID Cliente / Titular
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("Cédula / ID Titular*:"), gbc);
        gbc.gridx = 1;
        panel.add(txtIdCliente, gbc);

        // Fila 1: Número de Cuenta
        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("Número de Cuenta*:"), gbc);
        gbc.gridx = 1;
        panel.add(txtNumeroCuenta, gbc);

        // Fila 2: Tipo de Cuenta
        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(new JLabel("Tipo de Cuenta*:"), gbc);
        gbc.gridx = 1;
        panel.add(cbTipoCuenta, gbc);

        // Fila 3: Estado de la Cuenta
        gbc.gridx = 0; gbc.gridy = 3;
        panel.add(new JLabel("Estado*:"), gbc);
        gbc.gridx = 1;
        panel.add(cbEstadoCuenta, gbc);

        // Fila 4: Saldo Actual
        gbc.gridx = 0; gbc.gridy = 4;
        panel.add(new JLabel("Saldo Actual:"), gbc);
        gbc.gridx = 1;
        panel.add(txtSaldoDisplay, gbc);

        // Fila 5: Botones
        JPanel panelBotones = new JPanel(new GridLayout(2, 2, 5, 5));
        btnRegistrar = new JButton("Crear Cuenta");
        btnModificar = new JButton("Actualizar");
        btnDesactivar = new JButton("Inactivar");
        btnLimpiar = new JButton("Limpiar");

        panelBotones.add(btnRegistrar);
        panelBotones.add(btnModificar);
        panelBotones.add(btnDesactivar);
        panelBotones.add(btnLimpiar);

        gbc.gridx = 0; gbc.gridy = 5;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(15, 5, 5, 5);
        panel.add(panelBotones, gbc);

        return panel;
    }

    private JPanel crearPanelTabla() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(26, 54, 93)),
                "Listado de Cuentas Registradas",
                TitledBorder.LEFT,
                TitledBorder.TOP,
                new Font("Arial", Font.BOLD, 14),
                new Color(26, 54, 93)
        ));

        JPanel panelBusqueda = new JPanel(new FlowLayout(FlowLayout.LEFT));
        txtBuscarCuenta = new JTextField(18);
        btnBuscar = new JButton("🔍 Buscar");
        panelBusqueda.add(new JLabel("Buscar (N° Cta / Cédula):"));
        panelBusqueda.add(txtBuscarCuenta);
        panelBusqueda.add(btnBuscar);

        panel.add(panelBusqueda, BorderLayout.NORTH);

        String[] columnas = {"ID", "N° Cuenta", "Cédula Titular", "Nombre Titular", "Tipo", "Estado", "Saldo ($)"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tablaCuentas = new JTable(modeloTabla);
        JScrollPane scrollPane = new JScrollPane(tablaCuentas);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private void asignarEventos() {
        btnLimpiar.addActionListener(e -> limpiarFormulario());
        btnRegistrar.addActionListener(e -> accionRegistrarCuenta());
        btnModificar.addActionListener(e -> accionModificarCuenta());
        btnDesactivar.addActionListener(e -> accionInactivarCuenta());
        btnBuscar.addActionListener(e -> accionBuscarCuenta());

        // Evento de Selección en Tabla (Autocompletar formulario)
        tablaCuentas.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && tablaCuentas.getSelectedRow() != -1) {
                cargarDatosSeleccionados();
            }
        });
    }

    private void accionRegistrarCuenta() {
        if (!validarCamposObligatorios()) {
            JOptionPane.showMessageDialog(this,
                    "Debe ingresar la cédula del titular y el número de cuenta.",
                    "Campos Incompletos", JOptionPane.WARNING_MESSAGE);
            return;
        }

        CuentaDTO dto = new CuentaDTO();
        dto.setNumeroCuenta(txtNumeroCuenta.getText().trim());
        dto.setTipoCuenta(cbTipoCuenta.getSelectedItem().toString());
        dto.setEstado("ACTIVA"); // El contrato especifica estado inicial ACTIVA

        // AQUÍ SE INVOCARÁ EL SERVICE REAL:
        // cuentaService.registrarCuenta(dto);

        JOptionPane.showMessageDialog(this,
                "Cuenta registrada exitosamente.",
                "Registro Exitoso", JOptionPane.INFORMATION_MESSAGE);

        limpiarFormulario();
    }

    private void accionModificarCuenta() {
        if (txtNumeroCuenta.isEnabled()) {
            JOptionPane.showMessageDialog(this,
                    "Debe seleccionar una cuenta de la tabla para modificar su tipo o estado.",
                    "Selección Requerida", JOptionPane.WARNING_MESSAGE);
            return;
        }

        CuentaDTO dto = new CuentaDTO();
        dto.setNumeroCuenta(txtNumeroCuenta.getText().trim());
        dto.setTipoCuenta(cbTipoCuenta.getSelectedItem().toString());
        dto.setEstado(cbEstadoCuenta.getSelectedItem().toString());

        // AQUÍ SE INVOCARÁ EL SERVICE REAL:
        // cuentaService.actualizarCuenta(dto);

        JOptionPane.showMessageDialog(this,
                "Información de la cuenta actualizada correctamente.",
                "Actualización Exitosa", JOptionPane.INFORMATION_MESSAGE);

        limpiarFormulario();
    }

    private void accionInactivarCuenta() {
        if (txtNumeroCuenta.isEnabled() && tablaCuentas.getSelectedRow() == -1) {
            JOptionPane.showMessageDialog(this,
                    "Debe seleccionar una cuenta de la tabla para inactivarla.",
                    "Selección Requerida", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirmacion = JOptionPane.showConfirmDialog(this,
                "¿Está seguro de que desea cambiar el estado de la cuenta N° " + txtNumeroCuenta.getText() + " a INACTIVA?",
                "Confirmar Inactivación", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (confirmacion == JOptionPane.YES_OPTION) {
            // AQUÍ SE INVOCARÁ EL SERVICE REAL:
            // cuentaService.cambiarEstadoCuenta(idCuenta, EstadoCuenta.INACTIVA);

            JOptionPane.showMessageDialog(this,
                    "El estado de la cuenta ha sido cambiado a INACTIVA.",
                    "Operación Completada", JOptionPane.INFORMATION_MESSAGE);
            limpiarFormulario();
        }
    }

    private void accionBuscarCuenta() {
        String criterio = txtBuscarCuenta.getText().trim();
        if (criterio.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Ingrese un número de cuenta o cédula para realizar la búsqueda.",
                    "Criterio Vacío", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        // AQUÍ SE INVOCARÁ EL SERVICE REAL:
        // List<CuentaDTO> resultados = cuentaService.buscarCuentas(filtro);
    }

    private void cargarDatosSeleccionados() {
        int fila = tablaCuentas.getSelectedRow();
        if (fila >= 0) {
            txtNumeroCuenta.setText(modeloTabla.getValueAt(fila, 1).toString());
            txtIdCliente.setText(modeloTabla.getValueAt(fila, 2).toString());
            cbTipoCuenta.setSelectedItem(modeloTabla.getValueAt(fila, 4).toString());
            cbEstadoCuenta.setSelectedItem(modeloTabla.getValueAt(fila, 5).toString());
            txtSaldoDisplay.setText("$ " + modeloTabla.getValueAt(fila, 6).toString());

            // RF07: El número de cuenta y el titular no deben ser editables
            txtNumeroCuenta.setEnabled(false);
            txtNumeroCuenta.setBackground(new Color(235, 235, 235));
            txtIdCliente.setEnabled(false);
            txtIdCliente.setBackground(new Color(235, 235, 235));
        }
    }

    private void limpiarFormulario() {
        txtNumeroCuenta.setText("");
        txtIdCliente.setText("");
        cbTipoCuenta.setSelectedIndex(0);
        cbEstadoCuenta.setSelectedIndex(0);
        txtSaldoDisplay.setText("$ 0.00");
        txtBuscarCuenta.setText("");

        txtNumeroCuenta.setEnabled(true);
        txtNumeroCuenta.setBackground(Color.WHITE);
        txtIdCliente.setEnabled(true);
        txtIdCliente.setBackground(Color.WHITE);

        tablaCuentas.clearSelection();
    }

    private boolean validarCamposObligatorios() {
        return !txtNumeroCuenta.getText().trim().isEmpty() && !txtIdCliente.getText().trim().isEmpty();
    }

    private void configurarValidacionesCampos() {
        aplicarFiltro(txtNumeroCuenta, "[0-9]+", 12);
        aplicarFiltro(txtIdCliente, "[0-9]+", 13);
        aplicarFiltro(txtBuscarCuenta, "[0-9]+", 13);
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