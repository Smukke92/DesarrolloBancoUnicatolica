package com.unicatolica.banco.vistas;

import com.unicatolica.banco.dto.ResultadoTransaccionDTO;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.time.LocalDateTime;

public class ComprobanteDialog extends JDialog {

    public ComprobanteDialog(Window owner, ResultadoTransaccionDTO resultado) {
        super(owner, "Comprobante de Transacción - Banco Unicatólica", ModalityType.APPLICATION_MODAL);
        
        setSize(420, 520);
        setResizable(false);
        setLocationRelativeTo(owner);
        setLayout(new BorderLayout());

        // Panel Principal estilo Ticket / Comprobante
        JPanel panelContenido = new JPanel(new BorderLayout(10, 10));
        panelContenido.setBackground(Color.WHITE);
        panelContenido.setBorder(new EmptyBorder(15, 20, 15, 20));

        // 1. Encabezado del Banco
        JPanel panelHeader = new JPanel(new GridLayout(2, 1, 2, 2));
        panelHeader.setBackground(Color.WHITE);
        
        JLabel lblBanco = new JLabel("BANCO UNICATÓLICA", SwingConstants.CENTER);
        lblBanco.setFont(new Font("Arial", Font.BOLD, 18));
        lblBanco.setForeground(new Color(26, 54, 93));

        JLabel lblSubtitulo = new JLabel("Comprobante de Operación Bancaria", SwingConstants.CENTER);
        lblSubtitulo.setFont(new Font("Arial", Font.ITALIC, 12));
        lblSubtitulo.setForeground(Color.GRAY);

        panelHeader.add(lblBanco);
        panelHeader.add(lblSubtitulo);

        panelContenido.add(panelHeader, BorderLayout.NORTH);

        // 2. Cuerpo del Recibo con la información detallada
        JPanel panelDetalles = new JPanel(new GridBagLayout());
        panelDetalles.setBackground(new Color(248, 249, 250));
        panelDetalles.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 224, 230), 1),
                new EmptyBorder(15, 15, 15, 15)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Formato para mostrar los valores
        String idTx = (resultado.getIdTransaccion() != null) ? resultado.getIdTransaccion().toString() : "N/A";
        String numCuenta = (resultado.getNumeroCuenta() != null) ? resultado.getNumeroCuenta() : "-";
        String tipoTx = (resultado.getTipoTransaccion() != null) ? resultado.getTipoTransaccion() : "-";
        String montoTx = (resultado.getMonto() != null) ? "$ " + resultado.getMonto().toString() : "$ 0.00";
        String saldoAnt = (resultado.getSaldoAnterior() != null) ? "$ " + resultado.getSaldoAnterior().toString() : "$ 0.00";
        String saldoNue = (resultado.getSaldoNuevo() != null) ? "$ " + resultado.getSaldoNuevo().toString() : "$ 0.00";
        String fechaHora = (resultado.getFechaHora() != null ? resultado.getFechaHora().toString() : "");
        // Fila 0: N° Transacción
        agregarFilaDetalle(panelDetalles, gbc, 0, "N° Transacción:", idTx, true);
        
        // Fila 1: Fecha y Hora
        agregarFilaDetalle(panelDetalles, gbc, 1, "Fecha / Hora:", fechaHora, false);

        // Fila 2: N° Cuenta
        agregarFilaDetalle(panelDetalles, gbc, 2, "N° de Cuenta:", numCuenta, true);

        // Fila 3: Tipo de Operación
        agregarFilaDetalle(panelDetalles, gbc, 3, "Tipo Operación:", tipoTx, true);

        // Fila 4: Monto Operado
        agregarFilaDetalle(panelDetalles, gbc, 4, "Monto Procesa.:", montoTx, true);

        // Fila 5: Saldo Anterior
        agregarFilaDetalle(panelDetalles, gbc, 5, "Saldo Anterior:", saldoAnt, false);

        // Fila 6: Saldo Nuevo
        agregarFilaDetalle(panelDetalles, gbc, 6, "Saldo Nuevo:", saldoNue, true);

        panelContenido.add(panelDetalles, BorderLayout.CENTER);

        // 3. Pie de página y Botón para Aceptar / Cerrar
        JPanel panelFooter = new JPanel(new FlowLayout(FlowLayout.CENTER));
        panelFooter.setBackground(Color.WHITE);

        JButton btnAceptar = new JButton("✓ Aceptar y Cerrar");
        btnAceptar.setFont(new Font("Arial", Font.BOLD, 13));
        btnAceptar.setBackground(new Color(26, 54, 93));
        btnAceptar.setForeground(Color.WHITE);
        btnAceptar.setFocusPainted(false);
        btnAceptar.addActionListener(e -> dispose());

        panelFooter.add(btnAceptar);
        panelContenido.add(panelFooter, BorderLayout.SOUTH);

        add(panelContenido);
    }

    private void agregarFilaDetalle(JPanel panel, GridBagConstraints gbc, int fila, String etiqueta, String valor, boolean resaltado) {
        gbc.gridy = fila;

        gbc.gridx = 0;
        gbc.weightx = 0.4;
        JLabel lblEtiqueta = new JLabel(etiqueta);
        lblEtiqueta.setFont(new Font("Arial", Font.PLAIN, 12));
        lblEtiqueta.setForeground(Color.DARK_GRAY);
        panel.add(lblEtiqueta, gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.6;
        JLabel lblValor = new JLabel(valor);
        lblValor.setFont(new Font("Arial", resaltado ? Font.BOLD : Font.PLAIN, 12));
        lblValor.setForeground(resaltado ? new Color(26, 54, 93) : Color.BLACK);
        panel.add(lblValor, gbc);
    }
}