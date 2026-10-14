package br.com.biblioteca.controller;
import br.com.biblioteca.dao.*;
import br.com.biblioteca.model.*;
import br.com.biblioteca.view.ReservaView;
import java.time.LocalDate;
import java.util.*;

public class ReservaController {
    private final ReservaView tela;
    private final ReservaDAO dao = new ReservaDAO();
    private List<Reserva> registros = new ArrayList<>();
    private List<Usuario> usuarios = new ArrayList<>();
    private List<Livro> livros = new ArrayList<>();

    public ReservaController(ReservaView tela) {
        this.tela = tela;
        tela.registrar.addActionListener(e -> registrar());
        tela.excluir.addActionListener(e -> excluir());
        tela.limpar.addActionListener(e -> limpar());
        tela.atualizar.addActionListener(e -> { limpar(); carregar(); });
        tela.cancelar.addActionListener(e -> cancelar());
        tela.atender.addActionListener(e -> atender());
        limpar(); carregar();
    }
    public void carregar() {
        try {
            usuarios = new UsuarioDAO().listar(); livros = new LivroDAO().listar();
            tela.usuario.removeAllItems(); tela.livro.removeAllItems();
            for (Usuario u : usuarios) tela.usuario.addItem(u);
            for (Livro l : livros) tela.livro.addItem(l);
            registros = dao.listar(); tela.dadosTabela.setRowCount(0);
            for (Reserva item : registros) tela.dadosTabela.addRow(new Object[] { item.getId(), nomeUsuario(item.getUsuarioId()), nomeLivro(item.getLivroId()), Validacao.formatar(item.getDataReserva()), item.getStatus() });
        } catch (Exception e) { tela.erro(e); }
    }
    private void registrar() {
        try {
            Usuario u = (Usuario)tela.usuario.getSelectedItem(); Livro l = (Livro)tela.livro.getSelectedItem();
            if (u == null || l == null) throw new IllegalArgumentException("Cadastre pelo menos um usuário e um livro primeiro.");
            dao.registrar(u.getId(), l.getId(), Validacao.data(tela.dataReserva.getText()));
            limpar(); carregar(); tela.mensagem("Registro salvo.");
        } catch (Exception e) { tela.erro(e); }
    }
    private void cancelar() {
        try {
            Reserva item = registros.get(tela.linhaSelecionada());
            if (tela.confirmar("Cancelar esta reserva?")) {
                dao.cancelar(item.getId()); carregar(); tela.mensagem("Reserva cancelada.");
            }
        } catch (Exception e) { tela.erro(e); }
    }
    private void atender() {
        try {
            Reserva item = registros.get(tela.linhaSelecionada());
            if (tela.confirmar("Criar um empréstimo hoje e marcar esta reserva como atendida?")) {
                dao.atender(item.getId(), item.getLivroId()); carregar(); tela.mensagem("Reserva atendida e empréstimo criado.");
            }
        } catch (Exception e) { tela.erro(e); }
    }
    private void excluir() {
        try {
            Reserva item = registros.get(tela.linhaSelecionada());
            if (tela.confirmar("Excluir reserva nº " + item.getId() + "?\nO registro da reserva será apagado permanentemente.\nDeseja continuar?")) {
                dao.excluir(item.getId()); limpar(); carregar(); tela.mensagem("Registro excluído.");
            }
        } catch (Exception e) { tela.erro(e); }
    }
    private void limpar() {
        tela.dataReserva.setText(Validacao.formatar(LocalDate.now()));
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
