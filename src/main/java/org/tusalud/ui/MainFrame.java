package org.tusalud.ui;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.tusalud.model.Medicion;
import org.tusalud.model.Recomendacion;
import org.tusalud.model.Usuario;
import org.tusalud.repository.MedicionDAO;
import org.tusalud.service.AnalisisService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.List;

public class MainFrame extends JFrame {
    private final Usuario usuario;
    private final MedicionDAO medicionDAO = new MedicionDAO();
    private final AnalisisService analisisService = new AnalisisService();

    private JTextField txtPeso;
    private JTextField txtAltura;
    private JTextField txtFrecuencia;
    private JTextField txtGlucosa;

    private JTable tblHistorial;
    private DefaultTableModel tableModel;

    private JLabel lblPromedioCardiaco;
    private JLabel lblPromedioGlucosa;
    private JLabel lblIMC;
    private JTextArea txtRecomendaciones;
    private JPanel panelGrafico;

    public MainFrame(Usuario usuario) {
        this.usuario = usuario;
        setTitle("TuSalud - Monitoreo Biométrico | Usuario: " + usuario.getNombre());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(950, 700);
        setLocationRelativeTo(null);
        initComponents();
        recargarDatos();
    }

    private void initComponents() {
        setLayout(new BorderLayout());

        // Header Superior
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(41, 128, 185));
        header.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));

        JLabel title = new JLabel("TuSalud — Panel de Control");
        title.setFont(new Font("SansSerif", Font.BOLD, 18));
        title.setForeground(Color.WHITE);

        JLabel userLabel = new JLabel("Sesión: " + usuario.getNombre() + " (" + usuario.getCorreo() + ")");
        userLabel.setFont(new Font("SansSerif", Font.PLAIN, 13));
        userLabel.setForeground(Color.WHITE);

        header.add(title, BorderLayout.WEST);
        header.add(userLabel, BorderLayout.EAST);
        add(header, BorderLayout.NORTH);

        // Pestañas principales
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("SansSerif", Font.PLAIN, 14));

        tabbedPane.addTab("➕ Nueva Medición", crearPanelNuevaMedicion());
        tabbedPane.addTab("📋 Historial", crearPanelHistorial());
        tabbedPane.addTab("📊 Estadísticas & Gráficos", crearPanelEstadisticas());
        tabbedPane.addTab("💡 Recomendaciones", crearPanelRecomendaciones());

        add(tabbedPane, BorderLayout.CENTER);
    }

    private JPanel crearPanelNuevaMedicion() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 10, 10, 10);

        JLabel desc = new JLabel("<html><b>Ingreso de Datos Biométricos</b><br><small>Ingresa uno o más valores para registrar tu medición actual:</small></html>");
        desc.setFont(new Font("SansSerif", Font.PLAIN, 14));
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        panel.add(desc, gbc);

        gbc.gridwidth = 1;
        gbc.gridy = 1; gbc.gridx = 0;
        panel.add(new JLabel("Peso Corporal (kg):"), gbc);
        gbc.gridx = 1;
        txtPeso = new JTextField(15);
        panel.add(txtPeso, gbc);

        gbc.gridy = 2; gbc.gridx = 0;
        panel.add(new JLabel("Altura (metros, ej. 1.75):"), gbc);
        gbc.gridx = 1;
        txtAltura = new JTextField(15);
        panel.add(txtAltura, gbc);

        gbc.gridy = 3; gbc.gridx = 0;
        panel.add(new JLabel("Frecuencia Cardíaca (bpm):"), gbc);
        gbc.gridx = 1;
        txtFrecuencia = new JTextField(15);
        panel.add(txtFrecuencia, gbc);

        gbc.gridy = 4; gbc.gridx = 0;
        panel.add(new JLabel("Glucosa en Sangre (mg/dL):"), gbc);
        gbc.gridx = 1;
        txtGlucosa = new JTextField(15);
        panel.add(txtGlucosa, gbc);

        JButton btnGuardar = new JButton("Guardar Medición");
        btnGuardar.setBackground(new Color(41, 128, 185));
        btnGuardar.setForeground(Color.WHITE);
        btnGuardar.setFont(new Font("SansSerif", Font.BOLD, 13));
        btnGuardar.addActionListener(e -> guardarMedicion());

        gbc.gridy = 5; gbc.gridx = 0; gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        panel.add(btnGuardar, gbc);

        return panel;
    }

    private JPanel crearPanelHistorial() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        String[] columnas = {"ID", "Fecha y Hora", "Peso (kg)", "Altura (m)", "FC (bpm)", "Glucosa (mg/dL)"};
        tableModel = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int col) { return false; }
        };
        tblHistorial = new JTable(tableModel);
        tblHistorial.setRowHeight(22);
        tblHistorial.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 12));

        panel.add(new JScrollPane(tblHistorial), BorderLayout.CENTER);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnRefrescar = new JButton("Actualizar");
        JButton btnEliminar = new JButton("Eliminar Seleccionada");

        btnRefrescar.addActionListener(e -> recargarDatos());
        btnEliminar.addActionListener(e -> eliminarSeleccionada());

        btnPanel.add(btnRefrescar);
        btnPanel.add(btnEliminar);
        panel.add(btnPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel crearPanelEstadisticas() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // Panel de métricas resumidas
        JPanel metricas = new JPanel(new GridLayout(1, 3, 15, 0));
        metricas.setBorder(BorderFactory.createTitledBorder("Promedios y Métricas"));

        lblPromedioCardiaco = new JLabel("FC Promedio: -- bpm", SwingConstants.CENTER);
        lblPromedioCardiaco.setFont(new Font("SansSerif", Font.BOLD, 14));

        lblPromedioGlucosa = new JLabel("Glucosa Promedio: -- mg/dL", SwingConstants.CENTER);
        lblPromedioGlucosa.setFont(new Font("SansSerif", Font.BOLD, 14));

        lblIMC = new JLabel("IMC Actual: --", SwingConstants.CENTER);
        lblIMC.setFont(new Font("SansSerif", Font.BOLD, 14));

        metricas.add(lblPromedioCardiaco);
        metricas.add(lblPromedioGlucosa);
        metricas.add(lblIMC);

        panel.add(metricas, BorderLayout.NORTH);

        panelGrafico = new JPanel(new BorderLayout());
        panelGrafico.setBorder(BorderFactory.createTitledBorder("Gráfica de Evolución Temporal"));
        panel.add(panelGrafico, BorderLayout.CENTER);

        return panel;
    }

    private JPanel crearPanelRecomendaciones() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JLabel title = new JLabel("Recomendaciones de Salud Personalizadas");
        title.setFont(new Font("SansSerif", Font.BOLD, 16));
        panel.add(title, BorderLayout.NORTH);

        txtRecomendaciones = new JTextArea();
        txtRecomendaciones.setEditable(false);
        txtRecomendaciones.setFont(new Font("SansSerif", Font.PLAIN, 14));
        txtRecomendaciones.setLineWrap(true);
        txtRecomendaciones.setWrapStyleWord(true);
        txtRecomendaciones.setMargin(new Insets(10, 10, 10, 10));

        panel.add(new JScrollPane(txtRecomendaciones), BorderLayout.CENTER);

        return panel;
    }

    private void guardarMedicion() {
        try {
            Double peso = txtPeso.getText().trim().isEmpty() ? null : Double.parseDouble(txtPeso.getText().trim());
            Double altura = txtAltura.getText().trim().isEmpty() ? null : Double.parseDouble(txtAltura.getText().trim());
            Integer fc = txtFrecuencia.getText().trim().isEmpty() ? null : Integer.parseInt(txtFrecuencia.getText().trim());
            Double glucosa = txtGlucosa.getText().trim().isEmpty() ? null : Double.parseDouble(txtGlucosa.getText().trim());

            if (peso == null && altura == null && fc == null && glucosa == null) {
                JOptionPane.showMessageDialog(this, "Debes ingresar al menos un dato biométrico.", "Validación", JOptionPane.WARNING_MESSAGE);
                return;
            }

            Medicion m = new Medicion(
                    usuario.getIdUsuario(),
                    new Timestamp(System.currentTimeMillis()),
                    peso,
                    altura,
                    fc,
                    glucosa
            );

            if (medicionDAO.agregar(m)) {
                JOptionPane.showMessageDialog(this, "Medición registrada exitosamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                txtPeso.setText("");
                txtAltura.setText("");
                txtFrecuencia.setText("");
                txtGlucosa.setText("");
                recargarDatos();
            } else {
                JOptionPane.showMessageDialog(this, "No se pudo registrar la medición.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Por favor ingresa números válidos (ej: Altura: 1.75, Peso: 70.5).", "Formato numérico inválido", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error al guardar en la base de datos:\n" + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void eliminarSeleccionada() {
        int row = tblHistorial.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Selecciona una fila para eliminar.", "Atención", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int idMedicion = (int) tableModel.getValueAt(row, 0);
        int confirm = JOptionPane.showConfirmDialog(this, "¿Estás seguro de eliminar la medición con ID #" + idMedicion + "?", "Confirmar eliminación", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                if (medicionDAO.eliminar(idMedicion, usuario.getIdUsuario())) {
                    JOptionPane.showMessageDialog(this, "Medición eliminada.");
                    recargarDatos();
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error al eliminar: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void recargarDatos() {
        try {
            List<Medicion> mediciones = medicionDAO.obtenerPorUsuario(usuario.getIdUsuario());

            // Actualizar tabla
            tableModel.setRowCount(0);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            for (Medicion m : mediciones) {
                tableModel.addRow(new Object[]{
                        m.getIdMedicion(),
                        sdf.format(m.getFechaHora()),
                        m.getPeso() != null ? m.getPeso() : "-",
                        m.getAltura() != null ? m.getAltura() : "-",
                        m.getFrecuenciaCardiaca() != null ? m.getFrecuenciaCardiaca() : "-",
                        m.getGlucosaSangre() != null ? m.getGlucosaSangre() : "-"
                });
            }

            // Actualizar Métricas
            double fcProm = analisisService.calcularCardiaco(mediciones);
            double gluProm = analisisService.calcularGlucosa(mediciones);
            double imc = analisisService.calcularIMC(mediciones);

            lblPromedioCardiaco.setText(String.format("FC Promedio: %.0f bpm", fcProm));
            lblPromedioGlucosa.setText(String.format("Glucosa Promedio: %.1f mg/dL", gluProm));
            lblIMC.setText(String.format("IMC Actual: %.1f", imc));

            // Actualizar Gráfica JFreeChart
            panelGrafico.removeAll();
            if (!mediciones.isEmpty()) {
                JFreeChart chart = analisisService.generarGraficaEvolucion(mediciones);
                ChartPanel chartPanel = new ChartPanel(chart);
                panelGrafico.add(chartPanel, BorderLayout.CENTER);
            } else {
                panelGrafico.add(new JLabel("No hay mediciones suficientes para graficar.", SwingConstants.CENTER), BorderLayout.CENTER);
            }
            panelGrafico.revalidate();
            panelGrafico.repaint();

            // Actualizar Recomendaciones
            List<Recomendacion> recomendaciones = analisisService.generarRecomendaciones(usuario.getIdUsuario(), mediciones);
            StringBuilder sb = new StringBuilder();
            for (Recomendacion r : recomendaciones) {
                sb.append("• [").append(r.getTipo()).append("]: ").append(r.getMensaje()).append("\n\n");
            }
            txtRecomendaciones.setText(sb.toString());

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error al sincronizar datos:\n" + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
