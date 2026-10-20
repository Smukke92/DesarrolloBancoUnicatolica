package com.unicatolica.banco.vistas;

import com.unicatolica.banco.dto.CuentaDTO;
import com.unicatolica.banco.dto.ResultadoTransaccionDTO;
import com.unicatolica.banco.dto.TransaccionDTO;
import com.unicatolica.banco.service.CuentaService;
import com.unicatolica.banco.service.TransaccionService;
import com.unicatolica.banco.service.impl.CuentaServiceImpl;
import com.unicatolica.banco.service.impl.TransaccionServiceImpl;
import com.unicatolica.banco.vistas.eventos.TransaccionListener;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import java.awt.*;
import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class VistaTransaccion extends JPanel {

    // Capa de Servicios
    private final TransaccionService transaccionService;
    private final CuentaService cuentaService;
    
    // Lista de observadores que reaccionarán a las transacciones
    private final List<TransaccionListener> listeners = new ArrayList<>();
    
    // Componentes de búsqueda y datos de la cuenta
    private JTextField txtNumeroCuenta;
    private JButton btnBuscarCuenta;
    private JTextField txtTitularDisplay;
    private JTextField txtTipoDisplay;
    private JTextField txtEstadoDisplay;
    private JTextField txtSaldoDisplay;

    // Componentes para registrar transacción
    private JComboBox<String> cbTipoTransaccion;
    private JTextField txtMonto;
    private JButton btnProcesar;
    private JButton btnLimpiar;

    // Tabla de Historial
    private JTable tablaHistorial;
    private DefaultTableModel modeloTabla;

    // Formateador de fecha/hora para la tabla
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
    
    public void addTransaccionListener(TransaccionListener listener) {
        if (listener != null) {
            listeners.add(listener);
        }
    }

    private void notificarTransaccionRealizada() {
        for (TransaccionListener listener : listeners) {
            listener.onTransaccionRealizada();
        }
    }
    
    public VistaTransaccion() {
        this.transaccionService = new TransaccionServiceImpl();
        this.cuentaService = new CuentaServiceImpl();

        setLayout(new BorderLayout(10, 10));
        setBackground(new Color(247, 250, 252));

        // 1. Crear Paneles
        JPanel panelIzquierdo = new JPanel(new BorderLayout(5, 5));
        panelIzquierdo.add(crearPanelCuenta(), BorderLayout.NORTH);
        panelIzquierdo.add(crearPanelOperaciones(), BorderLayout.CENTER);

        JPanel panelHistorial = crearPanelHistorial();

        // 2. Configurar Validaciones y Eventos
        configurarValidacionesCampos();
        asignarEventos();

        // 3. JSplitPane para dividir operaciones e historial
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, panelIzquierdo, panelHistorial);
        splitPane.setDividerLocation(420);
        splitPane.setResizeWeight(0.45);

        add(splitPane, BorderLayout.CENTER);
    }

    private JPanel crearPanelCuenta() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(26, 54, 93)),
                "Consulta de Cuenta",
                TitledBorder.LEFT,
                TitledBorder.TOP,
                new Font("Arial", Font.BOLD, 14),
                new Color(26, 54, 93)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        txtNumeroCuenta = new JTextField(12);
        btnBuscarCuenta = new JButton("🔍 Consultar");

        txtTitularDisplay = crearCampoLectura();
        txtTipoDisplay = crearCampoLectura();
        txtEstadoDisplay = crearCampoLectura();
        txtSaldoDisplay = crearCampoLectura();

        // Búsqueda
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("N° Cuenta*:"), gbc);
        gbc.gridx = 1;
        
        JPanel pnlBusqueda = new JPanel(new BorderLayout(5, 0));
        pnlBusqueda.add(txtNumeroCuenta, BorderLayout.CENTER);
        pnlBusqueda.add(btnBuscarCuenta, BorderLayout.EAST);
        panel.add(pnlBusqueda, gbc);

        // Información de la Cuenta
        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("Titular:"), gbc);
        gbc.gridx = 1;
        panel.add(txtTitularDisplay, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(new JLabel("Tipo Cuenta:"), gbc);
        gbc.gridx = 1;
        panel.add(txtTipoDisplay, gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        panel.add(new JLabel("Estado:"), gbc);
        gbc.gridx = 1;
        panel.add(txtEstadoDisplay, gbc);

        gbc.gridx = 0; gbc.gridy = 4;
        panel.add(new JLabel("Saldo Disponible:"), gbc);
        gbc.gridx = 1;
        panel.add(txtSaldoDisplay, gbc);

        return panel;
    }

    private JPanel crearPanelOperaciones() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(26, 54, 93)),
                "Registrar Transacción",
                TitledBorder.LEFT,
                TitledBorder.TOP,
                new Font("Arial", Font.BOLD, 14),
                new Color(26, 54, 93)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 5, 8, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        cbTipoTransaccion = new JComboBox<>(new String[]{"DEPOSITO", "RETIRO"});
        txtMonto = new JTextField(12);

        btnProcesar = new JButton("💳 Realizar Transacción");
        btnLimpiar = new JButton("Limpiar");

        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("Tipo Transacción*:"), gbc);
        gbc.gridx = 1;
        panel.add(cbTipoTransaccion, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("Monto ($)*:"), gbc);
        gbc.gridx = 1;
        panel.add(txtMonto, gbc);

        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
        panelBotones.add(btnProcesar);
        panelBotones.add(btnLimpiar);

        gbc.gridx = 0; gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(15, 5, 5, 5);
        panel.add(panelBotones, gbc);

        return panel;
    }

    private JPanel crearPanelHistorial() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(26, 54, 93)),
                "Historial de Movimientos",
                TitledBorder.LEFT,
                TitledBorder.TOP,
                new Font("Arial", Font.BOLD, 14),
                new Color(26, 54, 93)
        ));

        String[] columnas = {"ID Tx", "Tipo", "Monto ($)", "Fecha y Hora"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tablaHistorial = new JTable(modeloTabla);
        JScrollPane scrollPane = new JScrollPane(tablaHistorial);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private void asignarEventos() {
        btnBuscarCuenta.addActionListener(e -> accionBuscarCuenta());
        txtNumeroCuenta.addActionListener(e -> accionBuscarCuenta());
        btnProcesar.addActionListener(e -> accionProcesarTransaccion());
        btnLimpiar.addActionListener(e -> limpiarCampos());
    }

    private void accionBuscarCuenta() {
        String numCuenta = txtNumeroCuenta.getText().trim();
        if (numCuenta.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Ingrese un número de cuenta para consultar.",
                    "Campo Vacío", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            CuentaDTO cuenta = transaccionService.consultarCuentaParaTransaccion(numCuenta);

            txtTitularDisplay.setText(cuenta.getNombreCliente());
            txtTipoDisplay.setText(cuenta.getTipoCuenta());
            txtEstadoDisplay.setText(cuenta.getEstado());
            txtSaldoDisplay.setText("$ " + String.format("%.2f", cuenta.getSaldo()));

            cargarHistorial(numCuenta);

        } catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(this,
                    traducirError(e.getMessage()),
                    "Cuenta No Encontrada", JOptionPane.WARNING_MESSAGE);
            limpiarDetallesCuenta();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Error al buscar la cuenta: " + e.getMessage(),
                    "Error Interno", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void accionProcesarTransaccion() {
        String numCuenta = txtNumeroCuenta.getText().trim();
        String tipoTransaccion = cbTipoTransaccion.getSelectedItem().toString();
        String montoTexto = txtMonto.getText().trim();

        if (numCuenta.isEmpty() || txtTitularDisplay.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Debe consultar una cuenta válida antes de registrar una transacción.",
                    "Validación Requerida", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (montoTexto.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Ingrese el monto de la transacción.",
                    "Campo Vacío", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            BigDecimal monto = new BigDecimal(montoTexto);
            if (monto.compareTo(BigDecimal.ZERO) <= 0) {
                JOptionPane.showMessageDialog(this,
                        "El monto debe ser un valor mayor a cero.",
                        "Monto Inválido", JOptionPane.WARNING_MESSAGE);
                return;
            }

            ResultadoTransaccionDTO resultado;
            if ("DEPOSITO".equalsIgnoreCase(tipoTransaccion)) {
                resultado = transaccionService.registrarDeposito(numCuenta, monto);
            } else {
                resultado = transaccionService.registrarRetiro(numCuenta, monto);
            }

            // Actualizar interfaz con el nuevo saldo
            txtSaldoDisplay.setText("$ " + String.format("%.2f", resultado.getSaldoNuevo()));
            cargarHistorial(numCuenta);
            txtMonto.setText("");
            
            // NOTIFICAR A OTRAS VISTAS (Como VistaCuenta) QUE HUBO UN CAMBIO DE SALDO
            notificarTransaccionRealizada();
            
            // Abrir comprobante de transacción emergente
            Frame parentFrame = (Frame) SwingUtilities.getWindowAncestor(this);
            ComprobanteDialog dialog = new ComprobanteDialog(parentFrame, resultado);
            dialog.setVisible(true);

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this,
                    "El monto ingresado no es una cantidad numérica válida.",
                    "Formato Inválido", JOptionPane.WARNING_MESSAGE);
        } catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(this,
                    traducirError(e.getMessage()),
                    "Error de Transacción", JOptionPane.WARNING_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Error al procesar la transacción: " + e.getMessage(),
                    "Error Interno", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void cargarHistorial(String numCuenta) {
        try {
            List<TransaccionDTO> historial = transaccionService.listarTransaccionesPorCuenta(numCuenta);
            modeloTabla.setRowCount(0);

            for (TransaccionDTO t : historial) {
                String fechaFormateada = t.getFechaHora() != null 
                        ? t.getFechaHora().format(formatter) 
                        : "";

                Object[] fila = new Object[]{
                    t.getIdTransaccion(),
                    t.getTipoTransaccion(),
                    String.format("%.2f", t.getMonto()),
                    fechaFormateada
                };
                modeloTabla.addRow(fila);
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Error al cargar historial: " + e.getMessage(),
                    "Error de Carga", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void limpiarCampos() {
        txtNumeroCuenta.setText("");
        txtMonto.setText("");
        cbTipoTransaccion.setSelectedIndex(0);
        limpiarDetallesCuenta();
    }

    private void limpiarDetallesCuenta() {
        txtTitularDisplay.setText("");
        txtTipoDisplay.setText("");
        txtEstadoDisplay.setText("");
        txtSaldoDisplay.setText("$ 0.00");
        modeloTabla.setRowCount(0);
    }

    private JTextField crearCampoLectura() {
        JTextField campo = new JTextField(15);
        campo.setEditable(false);
        campo.setBackground(new Color(230, 235, 240));
        return campo;
    }

    private String traducirError(String codigo) {
        if (codigo == null) return "Ocurrió un error inesperado.";
        return switch (codigo) {
            case "CUENTA_NO_ENCONTRADA" -> "La cuenta especificada no existe en el sistema.";
            case "CUENTA_INACTIVA" -> "No se pueden realizar operaciones en una cuenta INACTIVA.";
            case "MONTO_INVALIDO" -> "El monto de la transacción debe ser mayor a cero.";
            case "FONDOS_INSUFICIENTES" -> "Saldo insuficiente para realizar el retiro solicitado.";
            case "NUMERO_CUENTA_INVALIDO" -> "El número de cuenta ingresado no es válido.";
            default -> codigo;
        };
    }

    private void configurarValidacionesCampos() {
        aplicarFiltroTexto(txtNumeroCuenta, "[0-9]+", 12);
        aplicarFiltroMonto(txtMonto, 10);
    }

    private void aplicarFiltroTexto(JTextField campo, String regex, int maxCaracteres) {
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

    private void aplicarFiltroMonto(JTextField campo, int maxCaracteres) {
        ((AbstractDocument) campo.getDocument()).setDocumentFilter(new DocumentFilter() {
            @Override
            public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr) throws BadLocationException {
                if (string == null) return;
                String textoFuturo = fb.getDocument().getText(0, fb.getDocument().getLength()) + string;
                if (textoFuturo.length() <= maxCaracteres && textoFuturo.matches("^[0-9]*\\.?[0-9]{0,2}$")) {
                    super.insertString(fb, offset, string, attr);
                }
            }

            @Override
            public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
                if (text == null) return;
                String textoActual = fb.getDocument().getText(0, fb.getDocument().getLength());
                String textoFuturo = textoActual.substring(0, offset) + text + textoActual.substring(offset + length);
                if (textoFuturo.isEmpty() || (textoFuturo.length() <= maxCaracteres && textoFuturo.matches("^[0-9]*\\.?[0-9]{0,2}$"))) {
                    super.replace(fb, offset, length, text, attrs);
                }
            }
        });
    }
}