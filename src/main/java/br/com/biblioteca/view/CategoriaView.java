package br.com.biblioteca.view;
import javax.swing.*;
public class CategoriaView extends TelaBase {
    public JTextField nome = new JTextField(25);
    public JTextField descricao = new JTextField(25);
    public JButton salvar = new JButton("Salvar");
    public JButton editar = new JButton("Editar selecionado");
    public JButton excluir = new JButton("Excluir selecionado");
    public CategoriaView() {
        super("Cadastro de categorias", new String[] {"ID", "Nome", "Descrição"});
        campo("Nome", nome);
        campo("Descrição", descricao);
        botoes.add(salvar); botoes.add(limpar); botoes.add(editar); botoes.add(excluir); botoes.add(atualizar);
    }
}
