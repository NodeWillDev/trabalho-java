package br.com.biblioteca.controller;
import br.com.biblioteca.dao.*;
import br.com.biblioteca.model.*;
import br.com.biblioteca.view.UsuarioView;
import java.util.*;

public class UsuarioController {
    private final UsuarioView tela;
    private final UsuarioDAO dao = new UsuarioDAO();
    private List<Usuario> registros = new ArrayList<>();
    private int id = 0;

    public UsuarioController(UsuarioView tela) {
        this.tela = tela;
        tela.salvar.addActionListener(e -> salvar());
        tela.editar.addActionListener(e -> editar());
        tela.excluir.addActionListener(e -> excluir());
        tela.limpar.addActionListener(e -> limpar());
        tela.atualizar.addActionListener(e -> { limpar(); carregar(); });
        carregar();
    }
    public void carregar() {
        try {
            registros = dao.listar();
            tela.dadosTabela.setRowCount(0);
            for (Usuario item : registros) tela.dadosTabela.addRow(new Object[] { item.getId(), item.getNome(), item.getEmail(), item.getTelefone(), item.getCpf() });
        } catch (Exception e) { tela.erro(e); }
    }
    private void salvar() {
        try {
            Usuario item = new Usuario(); item.setId(id);
            item.setNome(Validacao.obrigatorio(tela.nome.getText(), "Nome", 120));
            String email = Validacao.obrigatorio(tela.email.getText(), "E-mail", 160);
            if (!email.matches("[^\\s@]+@[^\\s@]+\\.[^\\s@]+")) throw new IllegalArgumentException("Informe um e-mail válido.");
            item.setEmail(email);
            String telefone = tela.telefone.getText().trim();
            if (telefone.length() > 30) throw new IllegalArgumentException("Telefone deve ter até 30 caracteres.");
            item.setTelefone(telefone);
            String cpf = tela.cpf.getText().trim().replace(".", "").replace("-", "");
            if (!cpf.matches("[0-9]{11}")) throw new IllegalArgumentException("O CPF deve conter 11 números.");
            item.setCpf(cpf);
            dao.salvar(item); limpar(); carregar(); tela.mensagem("Cadastro salvo.");
        } catch (Exception e) { tela.erro(e); }
    }
    private void editar() {
        try {
            Usuario item = registros.get(tela.linhaSelecionada()); id = item.getId();
            tela.nome.setText(item.getNome());
            tela.email.setText(item.getEmail());
            tela.telefone.setText(item.getTelefone());
            tela.cpf.setText(item.getCpf());
            tela.salvar.setText("Salvar alterações");
        } catch (Exception e) { tela.erro(e); }
    }
    private void excluir() {
        try {
            Usuario item = registros.get(tela.linhaSelecionada());
            int escolhido = item.getId();
            if (tela.confirmar("Excluir o usuário: " + item.getNome() + "?\nOs empréstimos e reservas deste usuário também serão apagados.\nEsta exclusão é permanente. Deseja continuar?")) {
                dao.excluir(escolhido); limpar(); carregar(); tela.mensagem("Registro excluído.");
            }
        } catch (Exception e) { tela.erro(e); }
    }
    private void limpar() {
        id = 0; tela.tabela.clearSelection(); tela.salvar.setText("Salvar");
        tela.nome.setText("");
        tela.email.setText("");
        tela.telefone.setText("");
        tela.cpf.setText("");
    }
}
