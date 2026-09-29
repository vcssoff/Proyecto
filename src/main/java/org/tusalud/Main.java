package org.tusalud;

import com.formdev.flatlaf.FlatLightLaf;
import org.tusalud.model.Usuario;
import org.tusalud.ui.LoginDialog;
import org.tusalud.ui.MainFrame;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        // Look & Feel moderno y plano
        try {
            FlatLightLaf.setup();
        } catch (Exception ignored) {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored2) {}
        }

        SwingUtilities.invokeLater(() -> {
            LoginDialog login = new LoginDialog(null);
            login.setVisible(true);

            Usuario usuario = login.getUsuarioAutenticado();
            if (usuario != null) {
                MainFrame mainFrame = new MainFrame(usuario);
                mainFrame.setVisible(true);
            } else {
                System.exit(0);
            }
        });
    }
}
