package org.tusalud.ui;

import org.tusalud.model.Medicion;
import org.tusalud.repository.MedicionDAO;

import javax.swing.*;
import java.awt.*;

public class EditarMedicionDialog extends JDialog {
    private final Medicion medicion;
    private final MedicionDAO medicionDAO = new MedicionDAO();
    private boolean actualizado = false;

    private JTextField txtPeso;
    private JTextField txtAltura;
    private JTextField txtFrecuencia;
    private JTextField txtGlucosa;

    public EditarMedicionDialog(Dialog parent, Medicion medicion) {
        super(parent, "Editar Medición #" + medicion.getIdMedicion(), true);
        this.medicion = medicion;
        initComponents();
    }

    public EditarMedicionDialog(Frame parent, Medicion medicion) {
        super(parent, "Editar Medición #" + medicion.getIdMedicion(), true);
        this.medicion = medicion;
        initComponents();
    }

    private void initComponents() {
        setSize(400, 320);
        setLocationRelativeTo(getParent());
        setResizable(false);
        setLayout(new BorderLayout(15, 15));

        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(new Color(52, 152, 219));
        JLabel title = new JLabel("Modificar Registro Biométrico");
        title.setFont(new Font("SansSerif", Font.BOLD, 16));
        title.setForeground(Color.WHITE);
        title.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        headerPanel.add(title);
        add(headerPanel, BorderLayout.NORTH);

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 10, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(6, 6, 6, 6);

        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("Peso Corporal (kg):"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        txtPeso = new JTextField(medicion.getPeso() != null ? String.valueOf(medicion.getPeso()) : "");
        formPanel.add(txtPeso, gbc);

        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0;
        formPanel.add(new JLabel("Altura (m):"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        txtAltura = new JTextField(medicion.getAltura() != null ? String.valueOf(medicion.getAltura()) : "");
        formPanel.add(txtAltura, gbc);

        gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0;
        formPanel.add(new JLabel("FC (bpm):"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        txtFrecuencia = new JTextField(medicion.getFrecuenciaCardiaca() != null ? String.valueOf(medicion.getFrecuenciaCardiaca()) : "");
        formPanel.add(txtFrecuencia, gbc);

        gbc.gridx = 0; gbc.gridy = 3; gbc.weightx = 0;
        formPanel.add(new JLabel("Glucosa (mg/dL):"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        txtGlucosa = new JTextField(medicion.getGlucosaSangre() != null ? String.valueOf(medicion.getGlucosaSangre()) : "");
        formPanel.add(txtGlucosa, gbc);

        add(formPanel, BorderLayout.CENTER);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        JButton btnCancelar = new JButton("Cancelar");
        JButton btnGuardar = new JButton("Actualizar");

        btnGuardar.setBackground(new Color(52, 152, 219));
        btnGuardar.setForeground(Color.WHITE);

        btnCancelar.addActionListener(e -> dispose());
        btnGuardar.addActionListener(e -> guardarCambios());

        btnPanel.add(btnCancelar);
        btnPanel.add(btnGuardar);
        add(btnPanel, BorderLayout.SOUTH);
    }

    private void guardarCambios() {
        try {
            Double peso = txtPeso.getText().trim().isEmpty() ? null : Double.parseDouble(txtPeso.getText().trim());
            Double altura = txtAltura.getText().trim().isEmpty() ? null : Double.parseDouble(txtAltura.getText().trim());
            Integer fc = txtFrecuencia.getText().trim().isEmpty() ? null : Integer.parseInt(txtFrecuencia.getText().trim());
            Double glucosa = txtGlucosa.getText().trim().isEmpty() ? null : Double.parseDouble(txtGlucosa.getText().trim());

            if (peso == null && altura == null && fc == null && glucosa == null) {
                JOptionPane.showMessageDialog(this, "Debe existir al menos un dato biométrico.", "Validación", JOptionPane.WARNING_MESSAGE);
                return;
            }

            medicion.setPeso(peso);
            medicion.setAltura(altura);
            medicion.setFrecuenciaCardiaca(fc);
            medicion.setGlucosaSangre(glucosa);

            if (medicionDAO.actualizar(medicion)) {
                JOptionPane.showMessageDialog(this, "Medición actualizada exitosamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                this.actualizado = true;
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "No se pudo actualizar el registro.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Formato numérico no válido.", "Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error de base de datos:\n" + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public boolean isActualizado() {
        return actualizado;
    }
}
