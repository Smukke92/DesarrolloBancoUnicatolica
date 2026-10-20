package com.unicatolica.banco.vistas;

import com.unicatolica.banco.dto.ClienteDTO;
import com.unicatolica.banco.dto.CuentaDTO;
import com.unicatolica.banco.dto.FiltroCuentaDTO;
import com.unicatolica.banco.model.enums.EstadoCuenta;
import com.unicatolica.banco.service.ClienteService;
import com.unicatolica.banco.service.CuentaService;
import com.unicatolica.banco.service.impl.ClienteServiceImpl;
import com.unicatolica.banco.service.impl.CuentaServiceImpl;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import java.awt.*;
import java.util.List;

public class VistaCuenta extends JPanel {

    // Capa de Servicios
    private final CuentaService cuentaService;
    private final ClienteService clienteService;

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

    // Variable de apoyo para guardar el ID de la cuenta seleccionada
    private Integer idCuentaSeleccionada = null;

    public VistaCuenta() {
        this.cuentaService = new CuentaServiceImpl();
        this.clienteService = new ClienteServiceImpl();

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

        // 5. Cargar datos iniciales
        cargarTablaCuentas();
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

        // Fila 0: ID / Cédula Titular
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

        String[] columnas = {"ID", "N° Cuenta", "ID Titular", "Nombre Titular", "Tipo", "Estado", "Saldo ($)"};
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

        // Evento de Selección en Tabla
        tablaCuentas.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && tablaCuentas.getSelectedRow() != -1) {
                cargarDatosSeleccionados();
            }
        });
    }

    private void cargarTablaCuentas() {
        try {
            List<CuentaDTO> lista = cuentaService.listarCuentas();
            actualizarModeloTabla(lista);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Error al cargar la lista de cuentas: " + e.getMessage(),
                    "Error de Carga", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void actualizarModeloTabla(List<CuentaDTO> cuentas) {
        modeloTabla.setRowCount(0);
        for (CuentaDTO c : cuentas) {
            Object[] fila = new Object[]{
                c.getIdCuenta(),
                c.getNumeroCuenta(),
                c.getIdCliente(),
                c.getNombreCliente() != null ? c.getNombreCliente() : "Sin Nombre",
                c.getTipoCuenta(),
                c.getEstado(),
                String.format("%.2f", c.getSaldo())
            };
            modeloTabla.addRow(fila);
        }
    }

    private void accionRegistrarCuenta() {
        if (!validarCamposObligatorios()) {
            JOptionPane.showMessageDialog(this,
                    "Debe ingresar la cédula/ID del titular y el número de cuenta.",
                    "Campos Incompletos", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            Integer idClienteInput = obtenerIdClienteDesdeInput(txtIdCliente.getText().trim());

            CuentaDTO dto = new CuentaDTO();
            dto.setIdCliente(idClienteInput);
            dto.setNumeroCuenta(txtNumeroCuenta.getText().trim());
            dto.setTipoCuenta(cbTipoCuenta.getSelectedItem().toString());

            cuentaService.registrarCuenta(dto);

            JOptionPane.showMessageDialog(this,
                    "Cuenta registrada exitosamente.",
                    "Registro Exitoso", JOptionPane.INFORMATION_MESSAGE);

            limpiarFormulario();
            cargarTablaCuentas();

        } catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(this,
                    traducirError(e.getMessage()),
                    "Error de Validación", JOptionPane.WARNING_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Error al registrar cuenta: " + e.getMessage(),
                    "Error Interno", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void accionModificarCuenta() {
        if (idCuentaSeleccionada == null) {
            JOptionPane.showMessageDialog(this,
                    "Debe seleccionar una cuenta de la tabla para modificar su tipo o estado.",
                    "Selección Requerida", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            CuentaDTO dto = new CuentaDTO();
            dto.setIdCuenta(idCuentaSeleccionada);
            dto.setNumeroCuenta(txtNumeroCuenta.getText().trim());
            dto.setTipoCuenta(cbTipoCuenta.getSelectedItem().toString());
            dto.setEstado(cbEstadoCuenta.getSelectedItem().toString());

            cuentaService.actualizarCuenta(dto);

            JOptionPane.showMessageDialog(this,
                    "Información de la cuenta actualizada correctamente.",
                    "Actualización Exitosa", JOptionPane.INFORMATION_MESSAGE);

            limpiarFormulario();
            cargarTablaCuentas();

        } catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(this,
                    traducirError(e.getMessage()),
                    "Error de Validación", JOptionPane.WARNING_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Error al actualizar la cuenta: " + e.getMessage(),
                    "Error Interno", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void accionInactivarCuenta() {
        if (idCuentaSeleccionada == null) {
            JOptionPane.showMessageDialog(this,
                    "Debe seleccionar una cuenta de la tabla para inactivarla.",
                    "Selección Requerida", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirmacion = JOptionPane.showConfirmDialog(this,
                "¿Está seguro de que desea cambiar el estado de la cuenta N° " + txtNumeroCuenta.getText() + " a INACTIVA?",
                "Confirmar Inactivación", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (confirmacion == JOptionPane.YES_OPTION) {
            try {
                cuentaService.cambiarEstadoCuenta(idCuentaSeleccionada, EstadoCuenta.INACTIVA);

                JOptionPane.showMessageDialog(this,
                        "El estado de la cuenta ha sido cambiado a INACTIVA.",
                        "Operación Completada", JOptionPane.INFORMATION_MESSAGE);

                limpiarFormulario();
                cargarTablaCuentas();

            } catch (IllegalArgumentException e) {
                JOptionPane.showMessageDialog(this,
                        traducirError(e.getMessage()),
                        "Error de Operación", JOptionPane.WARNING_MESSAGE);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this,
                        "Error al inactivar la cuenta: " + e.getMessage(),
                        "Error Interno", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void accionBuscarCuenta() {
        String criterio = txtBuscarCuenta.getText().trim();
        if (criterio.isEmpty()) {
            cargarTablaCuentas();
            return;
        }

        try {
            FiltroCuentaDTO filtro = new FiltroCuentaDTO();
            filtro.setNumeroCuenta(criterio);

            try {
                Integer idCliente = Integer.parseInt(criterio);
                filtro.setIdCliente(idCliente);
            } catch (NumberFormatException ignored) {
                // Si el criterio no es numérico entero simple, busca como número de cuenta
            }

            List<CuentaDTO> resultados = cuentaService.buscarCuentas(filtro);
            actualizarModeloTabla(resultados);

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Error al buscar cuentas: " + e.getMessage(),
                    "Error de Búsqueda", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void cargarDatosSeleccionados() {
        int fila = tablaCuentas.getSelectedRow();
        if (fila >= 0) {
            idCuentaSeleccionada = Integer.parseInt(modeloTabla.getValueAt(fila, 0).toString());
            txtNumeroCuenta.setText(modeloTabla.getValueAt(fila, 1).toString());
            txtIdCliente.setText(modeloTabla.getValueAt(fila, 2).toString());
            cbTipoCuenta.setSelectedItem(modeloTabla.getValueAt(fila, 4).toString());
            cbEstadoCuenta.setSelectedItem(modeloTabla.getValueAt(fila, 5).toString());
            txtSaldoDisplay.setText("$ " + modeloTabla.getValueAt(fila, 6).toString());

            txtNumeroCuenta.setEnabled(false);
            txtNumeroCuenta.setBackground(new Color(235, 235, 235));
            txtIdCliente.setEnabled(false);
            txtIdCliente.setBackground(new Color(235, 235, 235));
        }
    }

    private void limpiarFormulario() {
        idCuentaSeleccionada = null;
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

    private Integer obtenerIdClienteDesdeInput(String input) {
        Integer idParsed = Integer.parseInt(input);
        
        // Si no existe directamente por ID numérico, valida si ingresaron una Cédula
        try {
            ClienteDTO cliente = clienteService.buscarClientes(input).stream()
                    .filter(c -> input.equals(c.getCedula()))
                    .findFirst()
                    .orElse(null);

            if (cliente != null) {
                return cliente.getIdCliente();
            }
        } catch (Exception ignored) {
        }

        return idParsed;
    }

    private boolean validarCamposObligatorios() {
        return !txtNumeroCuenta.getText().trim().isEmpty() && !txtIdCliente.getText().trim().isEmpty();
    }

    private String traducirError(String codigo) {
        if (codigo == null) return "Ocurrió un error inesperado.";
        return switch (codigo) {
            case "CLIENTE_NO_ENCONTRADO" -> "El titular / cliente especificado no existe.";
            case "NUMERO_CUENTA_DUPLICADO" -> "El número de cuenta ingresado ya se encuentra registrado.";
            case "NUMERO_CUENTA_INVALIDO" -> "El número de cuenta ingresado no es válido.";
            case "TIPO_CUENTA_NO_VALIDO" -> "El tipo de cuenta no es válido (solo AHORROS o CORRIENTE).";
            case "ESTADO_CUENTA_NO_VALIDO" -> "El estado especificado no es válido.";
            case "EL_NUMERO_DE_CUENTA_NO_SE_PUEDE_MODIFICAR" -> "El número de cuenta no se puede modificar.";
            case "CUENTA_NO_PUEDE_ELIMINARSE" -> "La cuenta no puede eliminarse ni inactivarse si tiene saldo o movimientos.";
            default -> codigo;
        };
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
    
    public void recargarDatos() {
        cargarTablaCuentas();
    }
}