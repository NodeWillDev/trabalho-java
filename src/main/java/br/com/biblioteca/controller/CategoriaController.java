package br.com.biblioteca.controller;
import br.com.biblioteca.dao.*;
import br.com.biblioteca.model.*;
import br.com.biblioteca.view.CategoriaView;
import java.util.*;

public class CategoriaController {
    private final CategoriaView tela;
    private final CategoriaDAO dao = new CategoriaDAO();
    private List<Categoria> registros = new ArrayList<>();
    private int id = 0;

    public CategoriaController(CategoriaView tela) {
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
            for (Categoria item : registros) tela.dadosTabela.addRow(new Object[] { item.getId(), item.getNome(), item.getDescricao() });
        } catch (Exception e) { tela.erro(e); }
    }
    private void salvar() {
        try {
            Categoria item = new Categoria(); item.setId(id);
            item.setNome(Validacao.obrigatorio(tela.nome.getText(), "Nome", 80));
            String descricao = tela.descricao.getText().trim();
            if (descricao.length() > 255) throw new IllegalArgumentException("Descrição deve ter até 255 caracteres.");
            item.setDescricao(descricao);
            dao.salvar(item); limpar(); carregar(); tela.mensagem("Cadastro salvo.");
        } catch (Exception e) { tela.erro(e); }
    }
    private void editar() {
        try {
            Categoria item = registros.get(tela.linhaSelecionada()); id = item.getId();
            tela.nome.setText(item.getNome());
            tela.descricao.setText(item.getDescricao());
            tela.salvar.setText("Salvar alterações");
        } catch (Exception e) { tela.erro(e); }
    }
    private void excluir() {
        try {
            Categoria item = registros.get(tela.linhaSelecionada());
            int escolhido = item.getId();
            if (tela.confirmar("Excluir a categoria: " + item.getNome() + "?\nTodos os livros desta categoria, seus empréstimos e suas reservas também serão apagados.\nEsta exclusão é permanente. Deseja continuar?")) {
                dao.excluir(escolhido); limpar(); carregar(); tela.mensagem("Registro excluído.");
            }
        } catch (Exception e) { tela.erro(e); }
    }
    private void limpar() {
        id = 0; tela.tabela.clearSelection(); tela.salvar.setText("Salvar");
        tela.nome.setText("");
        tela.descricao.setText("");
    }
}
