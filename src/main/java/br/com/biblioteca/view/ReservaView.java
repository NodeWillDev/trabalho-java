package br.com.biblioteca.view;
import javax.swing.*;
import br.com.biblioteca.model.Usuario;
import br.com.biblioteca.model.Livro;
public class ReservaView extends TelaBase {
    public JComboBox<Usuario> usuario = new JComboBox<>();
    public JComboBox<Livro> livro = new JComboBox<>();
    public JTextField dataReserva = new JTextField(20);
    public JButton registrar = new JButton("Registrar reserva");
    public JButton atender = new JButton("Atender selecionada");
    public JButton cancelar = new JButton("Cancelar selecionada");
    public JButton excluir = new JButton("Excluir selecionado");
    public ReservaView() {
        super("Controle de reservas", new String[] {"ID", "Usuário", "Livro", "Data", "Status"});
        campo("Usuário", usuario); campo("Livro", livro); campo("Data da reserva (dd/MM/aaaa)", dataReserva);
        botoes.add(registrar);
        botoes.add(atender);
        botoes.add(cancelar);
        botoes.add(excluir);
        botoes.add(limpar); botoes.add(atualizar);
    }
}
