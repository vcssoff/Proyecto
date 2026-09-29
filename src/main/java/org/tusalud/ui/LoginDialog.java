package org.tusalud.ui;

import org.tusalud.model.Usuario;
import org.tusalud.repository.UsuarioDAO;

import javax.swing.*;
import java.awt.*;
import java.sql.Date;

public class LoginDialog extends JDialog {
    private JTextField txtCorreo;
    private JPasswordField txtPassword;
    private Usuario usuarioAutenticado = null;
    private final UsuarioDAO usuarioDAO = new UsuarioDAO();

    public LoginDialog(Frame parent) {
        super(parent, "TuSalud - Iniciar Sesión", true);
        initComponents();
    }

    private void initComponents() {
        setSize(420, 280);
        setLocationRelativeTo(getParent());
        setResizable(false);
        setLayout(new BorderLayout(15, 15));

        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(new Color(41, 128, 185));
        JLabel title = new JLabel("Bienvenido a TuSalud");
        title.setFont(new Font("SansSerif", Font.BOLD, 18));
        title.setForeground(Color.WHITE);
        title.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        headerPanel.add(title);
        add(headerPanel, BorderLayout.NORTH);

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createEmptyBorder(15, 25, 10, 25));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(6, 6, 6, 6);

        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("Correo Electrónico:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        txtCorreo = new JTextField();
        formPanel.add(txtCorreo, gbc);

        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0;
        formPanel.add(new JLabel("Contraseña:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        txtPassword = new JPasswordField();
        formPanel.add(txtPassword, gbc);

        add(formPanel, BorderLayout.CENTER);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 12));
        JButton btnRegistrar = new JButton("Crear Cuenta");
        JButton btnLogin = new JButton("Iniciar Sesión");

        btnLogin.setBackground(new Color(41, 128, 185));
        btnLogin.setForeground(Color.WHITE);

        btnRegistrar.addActionListener(e -> abrirRegistro());
        btnLogin.addActionListener(e -> realizarLogin());

        btnPanel.add(btnRegistrar);
        btnPanel.add(btnLogin);
        add(btnPanel, BorderLayout.SOUTH);
    }

    private void realizarLogin() {
        String correo = txtCorreo.getText().trim();
        String pass = new String(txtPassword.getPassword());

        if (correo.isEmpty() || pass.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Completa todos los campos.", "Atención", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            Usuario u = usuarioDAO.iniciarSesion(correo, pass);
            if (u != null) {
                this.usuarioAutenticado = u;
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Credenciales incorrectas o usuario no encontrado.", "Error de autenticación", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error de conexión a la base de datos:\n" + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void abrirRegistro() {
        RegistroDialog regDialog = new RegistroDialog(this);
        regDialog.setVisible(true);
        if (regDialog.isRegistrado()) {
            txtCorreo.setText(regDialog.getCorreoRegistrado());
            txtPassword.requestFocus();
        }
    }

    public Usuario getUsuarioAutenticado() {
        return usuarioAutenticado;
    }
}
