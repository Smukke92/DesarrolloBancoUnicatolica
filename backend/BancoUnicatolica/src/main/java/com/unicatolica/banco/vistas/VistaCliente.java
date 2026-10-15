package com.unicatolica.banco.vistas;

import com.unicatolica.banco.dto.ClienteDTO;
import com.unicatolica.banco.service.ClienteService;
import com.unicatolica.banco.service.impl.ClienteServiceImpl;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import java.awt.*;
import java.time.LocalDate;
import java.util.List;

public class VistaCliente extends JPanel {

    // Componentes del formulario
    private JTextField txtCedula;
    private JTextField txtNombre;
    private JTextField txtTelefono;
    private JTextField txtEmail;
    private JTextField txtDireccion;
    private JTextField txtFechaNacimiento;

    private JButton btnGuardar;
    private JButton btnModificar;
    private JButton btnEliminar;
    private JButton btnLimpiar;

    // Componentes de la tabla de consulta
    private JTextField txtBuscar;
    private JButton btnBuscar;
    private JTable tablaClientes;
    private DefaultTableModel modeloTabla;

    private ClienteService clienteService;
    private Integer idClienteSeleccionado = null;

    // Constructor por defecto (crea la instancia del servicio)
    public VistaCliente() {
        this(new ClienteServiceImpl());
    }

    // Constructor con Inyección de Dependencia
    public VistaCliente(ClienteService clienteService) {
        this.clienteService = clienteService;

        setLayout(new BorderLayout(10, 10));
        setBackground(new Color(247, 250, 252));

        // 1. Crear Paneles
        JPanel panelFormulario = crearPanelFormulario();
        JPanel panelTabla = crearPanelTabla();

        // 2. Aplicar Filtros de entrada
        configurarValidacionesCampos();

        // 3. Asignar Eventos/Listeners a los componentes
        asignarEventos();

        // 4. Organizar Layout
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, panelFormulario, panelTabla);
        splitPane.setDividerLocation(380);
        splitPane.setResizeWeight(0.4);

        add(splitPane, BorderLayout.CENTER);

        // 5. Cargar lista inicial
        cargarTodosLosClientes();
    }

    private JPanel crearPanelFormulario() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(26, 54, 93)),
                "Datos del Cliente",
                TitledBorder.LEFT,
                TitledBorder.TOP,
                new Font("Arial", Font.BOLD, 14),
                new Color(26, 54, 93)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        txtCedula = new JTextField(15);
        txtNombre = new JTextField(15);
        txtTelefono = new JTextField(15);
        txtEmail = new JTextField(15);
        txtDireccion = new JTextField(15);
        txtFechaNacimiento = new JTextField(15);

        // Fila 0: Cédula
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("Cédula / ID*:"), gbc);
        gbc.gridx = 1;
        panel.add(txtCedula, gbc);

        // Fila 1: Nombre
        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("Nombre Completo*:"), gbc);
        gbc.gridx = 1;
        panel.add(txtNombre, gbc);

        // Fila 2: Teléfono
        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(new JLabel("Teléfono*:"), gbc);
        gbc.gridx = 1;
        panel.add(txtTelefono, gbc);

        // Fila 3: Email
        gbc.gridx = 0; gbc.gridy = 3;
        panel.add(new JLabel("Correo Electrónico*:"), gbc);
        gbc.gridx = 1;
        panel.add(txtEmail, gbc);

        // Fila 4: Dirección
        gbc.gridx = 0; gbc.gridy = 4;
        panel.add(new JLabel("Dirección*:"), gbc);
        gbc.gridx = 1;
        panel.add(txtDireccion, gbc);

        // Fila 5: Fecha Nacimiento
        gbc.gridx = 0; gbc.gridy = 5;
        panel.add(new JLabel("Fecha Nac. (AAAA-MM-DD)*:"), gbc);
        gbc.gridx = 1;
        panel.add(txtFechaNacimiento, gbc);

        // Fila 6: Botones
        JPanel panelBotones = new JPanel(new GridLayout(2, 2, 5, 5));
        btnGuardar = new JButton("Guardar");
        btnModificar = new JButton("Modificar");
        btnEliminar = new JButton("Eliminar");
        btnLimpiar = new JButton("Limpiar");

        panelBotones.add(btnGuardar);
        panelBotones.add(btnModificar);
        panelBotones.add(btnEliminar);
        panelBotones.add(btnLimpiar);

        gbc.gridx = 0; gbc.gridy = 6;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(15, 5, 5, 5);
        panel.add(panelBotones, gbc);

        return panel;
    }

    private JPanel crearPanelTabla() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(26, 54, 93)),
                "Listado de Clientes",
                TitledBorder.LEFT,
                TitledBorder.TOP,
                new Font("Arial", Font.BOLD, 14),
                new Color(26, 54, 93)
        ));

        JPanel panelBusqueda = new JPanel(new FlowLayout(FlowLayout.LEFT));
        txtBuscar = new JTextField(20);
        btnBuscar = new JButton("🔍 Buscar");
        panelBusqueda.add(new JLabel("Buscar (Cédula/Nombre):"));
        panelBusqueda.add(txtBuscar);
        panelBusqueda.add(btnBuscar);

        panel.add(panelBusqueda, BorderLayout.NORTH);

        String[] columnas = {"ID", "Cédula", "Nombre", "Teléfono", "Correo", "Dirección", "F. Nacimiento"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tablaClientes = new JTable(modeloTabla);
        JScrollPane scrollPane = new JScrollPane(tablaClientes);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private void asignarEventos() {
        btnLimpiar.addActionListener(e -> limpiarFormulario());
        btnGuardar.addActionListener(e -> accionGuardar());
        btnModificar.addActionListener(e -> accionModificar());
        btnEliminar.addActionListener(e -> accionEliminar());
        btnBuscar.addActionListener(e -> accionBuscar());

        tablaClientes.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && tablaClientes.getSelectedRow() != -1) {
                cargarDatosSeleccionados();
            }
        });
    }

    private void cargarTodosLosClientes() {
        modeloTabla.setRowCount(0);
        try {
            List<ClienteDTO> lista = clienteService.listarClientes();
            for (ClienteDTO c : lista) {
                modeloTabla.addRow(new Object[]{
                        c.getIdCliente(), c.getCedula(), c.getNombreCliente(), c.getTelefono(),
                        c.getEmail(), c.getDireccion(), c.getFechaNacimiento()
                });
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error al cargar la lista de clientes: " + ex.getMessage(),
                    "Error DB", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void accionGuardar() {
        if (!validarCamposObligatorios()) {
            JOptionPane.showMessageDialog(this,
                    "Todos los campos marcados con (*) son obligatorios.",
                    "Campos Incompletos", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (!validarFormatoEmail(txtEmail.getText().trim())) {
            JOptionPane.showMessageDialog(this,
                    "El formato del correo electrónico no es válido (ejemplo: usuario@correo.com).",
                    "Correo Inválido", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            ClienteDTO dto = obtenerClienteDesdeFormulario();
            clienteService.registrarCliente(dto);

            JOptionPane.showMessageDialog(this,
                    "Cliente registrado exitosamente.",
                    "Registro Exitoso", JOptionPane.INFORMATION_MESSAGE);

            limpiarFormulario();
            cargarTodosLosClientes();

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error al registrar cliente: " + ex.getMessage(),
                    "Error de Operación", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void accionModificar() {
        if (idClienteSeleccionado == null) {
            JOptionPane.showMessageDialog(this,
                    "Debe seleccionar un cliente de la tabla para modificarlo.",
                    "Selección Requerida", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (!validarCamposObligatorios()) {
            JOptionPane.showMessageDialog(this,
                    "Todos los campos obligatorios deben estar diligenciados.",
                    "Campos Incompletos", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            ClienteDTO dto = obtenerClienteDesdeFormulario();
            dto.setIdCliente(idClienteSeleccionado);

            clienteService.actualizarCliente(dto);

            JOptionPane.showMessageDialog(this,
                    "Datos del cliente actualizados correctamente.",
                    "Actualización Exitosa", JOptionPane.INFORMATION_MESSAGE);

            limpiarFormulario();
            cargarTodosLosClientes();

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error al modificar cliente: " + ex.getMessage(),
                    "Error de Operación", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void accionEliminar() {
        if (idClienteSeleccionado == null) {
            JOptionPane.showMessageDialog(this,
                    "Debe seleccionar un cliente de la tabla para eliminarlo.",
                    "Selección Requerida", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirmacion = JOptionPane.showConfirmDialog(this,
                "¿Está seguro de que desea eliminar al cliente con cédula " + txtCedula.getText() + "?",
                "Confirmar Eliminación", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (confirmacion == JOptionPane.YES_OPTION) {
            try {
                clienteService.eliminarCliente(idClienteSeleccionado);

                JOptionPane.showMessageDialog(this,
                        "Cliente eliminado correctamente del sistema.",
                        "Operación Completada", JOptionPane.INFORMATION_MESSAGE);

                limpiarFormulario();
                cargarTodosLosClientes();

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error al eliminar cliente: " + ex.getMessage(),
                        "Error de Operación", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void accionBuscar() {
        String criterio = txtBuscar.getText().trim();
        if (criterio.isEmpty()) {
            cargarTodosLosClientes();
            return;
        }

        modeloTabla.setRowCount(0);
        try {
            List<ClienteDTO> resultados = clienteService.buscarClientes(criterio);
            for (ClienteDTO c : resultados) {
                modeloTabla.addRow(new Object[]{
                        c.getIdCliente(), c.getCedula(), c.getNombreCliente(), c.getTelefono(),
                        c.getEmail(), c.getDireccion(), c.getFechaNacimiento()
                });
            }
            if (resultados.isEmpty()) {
                JOptionPane.showMessageDialog(this, "No se encontraron clientes con el criterio ingresado.",
                        "Sin Coincidencias", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error al buscar clientes: " + ex.getMessage(),
                    "Error de Búsqueda", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void cargarDatosSeleccionados() {
        int fila = tablaClientes.getSelectedRow();
        if (fila >= 0) {
            idClienteSeleccionado = Integer.parseInt(modeloTabla.getValueAt(fila, 0).toString());
            txtCedula.setText(modeloTabla.getValueAt(fila, 1).toString());
            txtNombre.setText(modeloTabla.getValueAt(fila, 2).toString());
            txtTelefono.setText(modeloTabla.getValueAt(fila, 3).toString());
            txtEmail.setText(modeloTabla.getValueAt(fila, 4).toString());
            txtDireccion.setText(modeloTabla.getValueAt(fila, 5).toString());

            Object fechaObj = modeloTabla.getValueAt(fila, 6);
            txtFechaNacimiento.setText(fechaObj != null ? fechaObj.toString() : "");

            txtCedula.setEnabled(false);
            txtCedula.setBackground(new Color(235, 235, 235));
        }
    }

    private void limpiarFormulario() {
        idClienteSeleccionado = null;
        txtCedula.setText("");
        txtNombre.setText("");
        txtTelefono.setText("");
        txtEmail.setText("");
        txtDireccion.setText("");
        txtFechaNacimiento.setText("");
        txtBuscar.setText("");

        txtCedula.setEnabled(true);
        txtCedula.setBackground(Color.WHITE);
        tablaClientes.clearSelection();
    }

    private boolean validarCamposObligatorios() {
        return !txtCedula.getText().trim().isEmpty()
                && !txtNombre.getText().trim().isEmpty()
                && !txtTelefono.getText().trim().isEmpty()
                && !txtEmail.getText().trim().isEmpty()
                && !txtDireccion.getText().trim().isEmpty()
                && !txtFechaNacimiento.getText().trim().isEmpty();
    }

    private boolean validarFormatoEmail(String email) {
        return email.matches("^[A-Za-z0-9+_.-]+@(.+)$");
    }

    private ClienteDTO obtenerClienteDesdeFormulario() {
        ClienteDTO dto = new ClienteDTO();
        dto.setCedula(txtCedula.getText().trim());
        dto.setNombreCliente(txtNombre.getText().trim());
        dto.setTelefono(txtTelefono.getText().trim());
        dto.setEmail(txtEmail.getText().trim());
        dto.setDireccion(txtDireccion.getText().trim());
        String fechaTxt = txtFechaNacimiento.getText().trim();
        if (!fechaTxt.isEmpty()) {
            dto.setFechaNacimiento(LocalDate.parse(fechaTxt));
        } else {
            dto.setFechaNacimiento(null);
        }
        return dto;
    }

    private void configurarValidacionesCampos() {
        aplicarFiltro(txtCedula, "[0-9]+", 13);
        aplicarFiltro(txtNombre, "[a-zA-ZáéíóúÁÉÍÓÚñÑ\\s]+", 50);
        aplicarFiltro(txtTelefono, "[0-9]+", 10);
        aplicarFiltro(txtEmail, "[a-zA-Z0-9@\\._\\-]+", 50);
        aplicarFiltro(txtDireccion, "[a-zA-Z0-9#\\-\\.\\,\\s]+", 60);
        aplicarFiltro(txtFechaNacimiento, "[0-9\\-]+", 10);
        aplicarFiltro(txtBuscar, "[a-zA-Z0-9áéíóúÁÉÍÓÚñÑ\\s]+", 50);
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