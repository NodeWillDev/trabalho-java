package br.com.biblioteca;
import javax.swing.*;
import br.com.biblioteca.view.MenuView;
import br.com.biblioteca.dao.Conexao;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                Conexao.inicializar();
                new MenuView().setVisible(true);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null,
                    "Não foi possível iniciar. Extraia o ZIP inteiro e confira se pode gravar arquivos nesta pasta.\n" + e.getMessage(),
                    "Biblioteca", JOptionPane.ERROR_MESSAGE);
            }
        });
    }
}
