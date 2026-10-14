package br.com.biblioteca.controller;
import br.com.biblioteca.dao.*;
import br.com.biblioteca.model.*;
import br.com.biblioteca.view.EmprestimoView;
import java.time.LocalDate;
import java.util.*;

public class EmprestimoController {
    private final EmprestimoView tela;
    private final EmprestimoDAO dao = new EmprestimoDAO();
    private List<Emprestimo> registros = new ArrayList<>();
    private List<Usuario> usuarios = new ArrayList<>();
    private List<Livro> livros = new ArrayList<>();

    public EmprestimoController(EmprestimoView tela) {
        this.tela = tela;
        tela.registrar.addActionListener(e -> registrar());
        tela.excluir.addActionListener(e -> excluir());
        tela.limpar.addActionListener(e -> limpar());
        tela.atualizar.addActionListener(e -> { limpar(); carregar(); });
        tela.devolver.addActionListener(e -> devolver());
        limpar(); carregar();
    }
    public void carregar() {
        try {
            usuarios = new UsuarioDAO().listar(); livros = new LivroDAO().listar();
            tela.usuario.removeAllItems(); tela.livro.removeAllItems();
            for (Usuario u : usuarios) tela.usuario.addItem(u);
            for (Livro l : livros) tela.livro.addItem(l);
            registros = dao.listar(); tela.dadosTabela.setRowCount(0);
            for (Emprestimo item : registros) tela.dadosTabela.addRow(new Object[] { item.getId(), nomeUsuario(item.getUsuarioId()), nomeLivro(item.getLivroId()), Validacao.formatar(item.getDataEmprestimo()), Validacao.formatar(item.getDataDevolucao()), item.getStatus() });
        } catch (Exception e) { tela.erro(e); }
    }
    private void registrar() {
        try {
            Usuario u = (Usuario)tela.usuario.getSelectedItem(); Livro l = (Livro)tela.livro.getSelectedItem();
            if (u == null || l == null) throw new IllegalArgumentException("Cadastre pelo menos um usuário e um livro primeiro.");
            dao.registrar(u.getId(), l.getId(), Validacao.data(tela.dataEmprestimo.getText()));
            limpar(); carregar(); tela.mensagem("Registro salvo.");
        } catch (Exception e) { tela.erro(e); }
    }
    private void devolver() {
        try {
            Emprestimo item = registros.get(tela.linhaSelecionada());
            if (tela.confirmar("Confirmar a devolução deste livro hoje?")) {
                dao.devolver(item.getId()); carregar(); tela.mensagem("Livro devolvido.");
            }
        } catch (Exception e) { tela.erro(e); }
    }
    private void excluir() {
        try {
            Emprestimo item = registros.get(tela.linhaSelecionada());
            if (tela.confirmar("Excluir empréstimo nº " + item.getId() + "?\nO histórico será apagado. Se estiver aberto, a cópia ficará disponível.\nDeseja continuar?")) {
                dao.excluir(item.getId()); limpar(); carregar(); tela.mensagem("Registro excluído.");
            }
        } catch (Exception e) { tela.erro(e); }
    }
    private void limpar() {
        tela.dataEmprestimo.setText(Validacao.formatar(LocalDate.now()));
        tela.tabela.clearSelection();
        if (tela.usuario.getItemCount() > 0) tela.usuario.setSelectedIndex(0);
        if (tela.livro.getItemCount() > 0) tela.livro.setSelectedIndex(0);
    }
    private String nomeUsuario(int id) {
        for (Usuario u : usuarios) if (u.getId() == id) return u.getNome();
        return "Usuário " + id;
    }
    private String nomeLivro(int id) {
        for (Livro l : livros) if (l.getId() == id) return l.getTitulo();
        return "Livro " + id;
    }
}
