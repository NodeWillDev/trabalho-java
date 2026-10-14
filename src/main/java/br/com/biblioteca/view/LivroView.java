package br.com.biblioteca.view;
import javax.swing.*;
import br.com.biblioteca.model.Categoria;
public class LivroView extends TelaBase {
    public JTextField titulo = new JTextField(25);
    public JTextField autor = new JTextField(25);
    public JTextField isbn = new JTextField(25);
    public JTextField anoPublicacao = new JTextField(25);
    public JTextField quantidade = new JTextField(25);
    public JComboBox<Categoria> categoria = new JComboBox<>();
    public JButton salvar = new JButton("Salvar");
    public JButton editar = new JButton("Editar selecionado");
    public JButton excluir = new JButton("Excluir selecionado");
    public LivroView() {
        super("Cadastro de livros", new String[] {"ID", "Título", "Autor", "ISBN", "Ano", "Quantidade", "Categoria"});
        campo("Título", titulo);
        campo("Autor", autor);
        campo("ISBN", isbn);
        campo("Ano de publicação", anoPublicacao);
        campo("Quantidade", quantidade);
        campo("Categoria", categoria);
        botoes.add(salvar); botoes.add(limpar); botoes.add(editar); botoes.add(excluir); botoes.add(atualizar);
    }
}
