package br.com.biblioteca.view;
import javax.swing.*;
public class UsuarioView extends TelaBase {
    public JTextField nome = new JTextField(25);
    public JTextField email = new JTextField(25);
    public JTextField telefone = new JTextField(25);
    public JTextField cpf = new JTextField(25);
    public JButton salvar = new JButton("Salvar");
    public JButton editar = new JButton("Editar selecionado");
    public JButton excluir = new JButton("Excluir selecionado");
    public UsuarioView() {
        super("Cadastro de usuários", new String[] {"ID", "Nome", "E-mail", "Telefone", "CPF"});
        campo("Nome", nome);
        campo("E-mail", email);
        campo("Telefone", telefone);
        campo("CPF", cpf);
        botoes.add(salvar); botoes.add(limpar); botoes.add(editar); botoes.add(excluir); botoes.add(atualizar);
    }
}
