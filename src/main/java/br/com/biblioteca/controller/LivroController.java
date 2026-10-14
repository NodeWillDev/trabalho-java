package br.com.biblioteca.controller;
import br.com.biblioteca.dao.*;
import br.com.biblioteca.model.*;
import br.com.biblioteca.view.LivroView;
import java.util.*;

public class LivroController {
    private final LivroView tela;
    private final LivroDAO dao = new LivroDAO();
    private List<Livro> registros = new ArrayList<>();
    private int id = 0;

    public LivroController(LivroView tela) {
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
            List<Categoria> categorias = new CategoriaDAO().listar();
            tela.categoria.removeAllItems();
            for (Categoria c : categorias) tela.categoria.addItem(c);
            registros = dao.listar();
            tela.dadosTabela.setRowCount(0);
            for (Livro item : registros) tela.dadosTabela.addRow(new Object[] { item.getId(), item.getTitulo(), item.getAutor(), item.getIsbn(), item.getAnoPublicacao(), item.getQuantidade(), nomeCategoria(item.getCategoriaId()) });
        } catch (Exception e) { tela.erro(e); }
    }
    private void salvar() {
        try {
            Livro item = new Livro(); item.setId(id);
            item.setTitulo(Validacao.obrigatorio(tela.titulo.getText(), "Título", 160));
            item.setAutor(Validacao.obrigatorio(tela.autor.getText(), "Autor", 120));
            item.setIsbn(Validacao.obrigatorio(tela.isbn.getText(), "ISBN", 20));
            item.setAnoPublicacao(Validacao.inteiro(tela.anoPublicacao.getText(), "Ano", 1, java.time.LocalDate.now().getYear()));
            item.setQuantidade(Validacao.inteiro(tela.quantidade.getText(), "Quantidade", 0, 1000000));
            Categoria categoria = (Categoria)tela.categoria.getSelectedItem();
            if (categoria == null) throw new IllegalArgumentException("Cadastre uma categoria primeiro.");
            item.setCategoriaId(categoria.getId());
            dao.salvar(item); limpar(); carregar(); tela.mensagem("Cadastro salvo.");
        } catch (Exception e) { tela.erro(e); }
    }
    private void editar() {
        try {
            Livro item = registros.get(tela.linhaSelecionada()); id = item.getId();
            tela.titulo.setText(item.getTitulo());
            tela.autor.setText(item.getAutor());
            tela.isbn.setText(item.getIsbn());
            tela.anoPublicacao.setText(String.valueOf(item.getAnoPublicacao()));
            tela.quantidade.setText(String.valueOf(item.getQuantidade()));
            for (int i = 0; i < tela.categoria.getItemCount(); i++)
                if (tela.categoria.getItemAt(i).getId() == item.getCategoriaId()) tela.categoria.setSelectedIndex(i);
            tela.salvar.setText("Salvar alterações");
        } catch (Exception e) { tela.erro(e); }
    }
    private void excluir() {
        try {
            Livro item = registros.get(tela.linhaSelecionada());
            int escolhido = item.getId();
            if (tela.confirmar("Excluir o livro: " + item.getTitulo() + "?\nOs empréstimos e reservas deste livro também serão apagados.\nEsta exclusão é permanente. Deseja continuar?")) {
                dao.excluir(escolhido); limpar(); carregar(); tela.mensagem("Registro excluído.");
            }
        } catch (Exception e) { tela.erro(e); }
    }
    private void limpar() {
        id = 0; tela.tabela.clearSelection(); tela.salvar.setText("Salvar");
        tela.titulo.setText("");
        tela.autor.setText("");
        tela.isbn.setText("");
        tela.anoPublicacao.setText("");
        tela.quantidade.setText("");
    }
    private String nomeCategoria(int id) {
        for (int i = 0; i < tela.categoria.getItemCount(); i++) {
            Categoria c = tela.categoria.getItemAt(i);
            if (c.getId() == id) return c.getNome();
        }
        return "Categoria " + id;
    }
}
