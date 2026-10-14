package br.com.biblioteca.view;
import javax.swing.*;
import br.com.biblioteca.model.Usuario;
import br.com.biblioteca.model.Livro;
public class EmprestimoView extends TelaBase {
    public JComboBox<Usuario> usuario = new JComboBox<>();
    public JComboBox<Livro> livro = new JComboBox<>();
    public JTextField dataEmprestimo = new JTextField(20);
    public JButton registrar = new JButton("Registrar empréstimo");
    public JButton devolver = new JButton("Devolver selecionado");
    public JButton excluir = new JButton("Excluir selecionado");
    public EmprestimoView() {
        super("Controle de empréstimos", new String[] {"ID", "Usuário", "Livro", "Empréstimo", "Devolução", "Status"});
        campo("Usuário", usuario); campo("Livro", livro); campo("Data do empréstimo (dd/MM/aaaa)", dataEmprestimo);
        botoes.add(registrar);
        botoes.add(devolver);
        botoes.add(excluir);
        botoes.add(limpar); botoes.add(atualizar);
    }
}
