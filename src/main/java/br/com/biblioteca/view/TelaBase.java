package br.com.biblioteca.view;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

// Parte comum das telas: título, formulário, botões e tabela.
public class TelaBase extends JPanel {
    protected JPanel formulario = new JPanel(new GridLayout(0, 2, 10, 8));
    protected JPanel botoes = new JPanel(new FlowLayout(FlowLayout.LEFT));
    public JTable tabela;
    public DefaultTableModel dadosTabela;
    public JButton limpar = new JButton("Limpar");
    public JButton atualizar = new JButton("Atualizar lista");

    public TelaBase(String titulo, String[] colunas) {
        super(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        JLabel cabecalho = new JLabel(titulo);
        cabecalho.setFont(cabecalho.getFont().deriveFont(Font.BOLD, 22f));
        JPanel superior = new JPanel(new BorderLayout(10, 15));
        superior.add(cabecalho, BorderLayout.NORTH);
        superior.add(formulario, BorderLayout.CENTER);
        superior.add(botoes, BorderLayout.SOUTH);
        add(superior, BorderLayout.NORTH);
        dadosTabela = new DefaultTableModel(colunas, 0) {
            @Override public boolean isCellEditable(int linha, int coluna) { return false; }
        };
        tabela = new JTable(dadosTabela);
        tabela.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabela.setRowHeight(25);
        JScrollPane rolagem = new JScrollPane(tabela);
        rolagem.setColumnHeaderView(tabela.getTableHeader());
        add(rolagem, BorderLayout.CENTER);
    }
    protected void campo(String nome, JComponent componente) {
        formulario.add(new JLabel(nome)); formulario.add(componente);
    }
    public void mensagem(String texto) { JOptionPane.showMessageDialog(this, texto); }
    public boolean confirmar(String texto) {
        return JOptionPane.showConfirmDialog(this, texto, "Confirmar", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION;
    }
    public void erro(Exception e) {
        String texto = e.getMessage();
        if (e instanceof java.sql.SQLException) {
            java.sql.SQLException sql = (java.sql.SQLException)e;
            if (sql.getErrorCode() == 19 || (sql.getErrorCode() & 255) == 19 || "23000".equals(sql.getSQLState()) || "23503".equals(sql.getSQLState()) || "23505".equals(sql.getSQLState()))
                texto = "Não foi possível salvar ou excluir. Confira CPF/ISBN repetidos e registros ligados a outras tabelas.";
        }
        JOptionPane.showMessageDialog(this, texto, "Atenção", JOptionPane.WARNING_MESSAGE);
    }
    public int linhaSelecionada() {
        int linha = tabela.getSelectedRow();
        if (linha < 0) throw new IllegalArgumentException("Selecione uma linha da tabela.");
        return linha;
    }
}
