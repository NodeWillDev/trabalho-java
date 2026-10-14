package br.com.biblioteca.view;
import br.com.biblioteca.controller.*;
import javax.swing.*;
import java.awt.*;

public class MenuView extends JFrame {
    public MenuView() {
        super("Sistema de Gerenciamento de Biblioteca");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(560, 380); setLocationRelativeTo(null);
        JPanel painel = new JPanel(new BorderLayout(15, 15));
        painel.setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));
        JLabel titulo = new JLabel("Biblioteca", SwingConstants.CENTER);
        titulo.setFont(titulo.getFont().deriveFont(Font.BOLD, 26f));
        painel.add(titulo, BorderLayout.NORTH);
        JPanel opcoes = new JPanel(new GridLayout(3, 2, 15, 15));
        JButton usuarios = new JButton("Usuários"); JButton livros = new JButton("Livros");
        JButton categorias = new JButton("Categorias"); JButton emprestimos = new JButton("Empréstimos");
        JButton reservas = new JButton("Reservas"); JButton sair = new JButton("Sair");
        for (JButton botao : new JButton[]{usuarios, livros, categorias, emprestimos, reservas, sair}) opcoes.add(botao);
        painel.add(opcoes, BorderLayout.CENTER); setContentPane(painel);
        usuarios.addActionListener(e -> { UsuarioView v = new UsuarioView(); new UsuarioController(v); abrir("Usuários", v); });
        livros.addActionListener(e -> { LivroView v = new LivroView(); new LivroController(v); abrir("Livros", v); });
        categorias.addActionListener(e -> { CategoriaView v = new CategoriaView(); new CategoriaController(v); abrir("Categorias", v); });
        emprestimos.addActionListener(e -> { EmprestimoView v = new EmprestimoView(); new EmprestimoController(v); abrir("Empréstimos", v); });
        reservas.addActionListener(e -> { ReservaView v = new ReservaView(); new ReservaController(v); abrir("Reservas", v); });
        sair.addActionListener(e -> dispose());
    }
    private void abrir(String titulo, JPanel tela) {
        JDialog janela = new JDialog(this, titulo, true);
        janela.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        janela.add(tela); janela.setSize(1050, 620); janela.setLocationRelativeTo(this); janela.setVisible(true);
    }
}
