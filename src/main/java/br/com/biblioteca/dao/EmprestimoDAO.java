package br.com.biblioteca.dao;
import br.com.biblioteca.model.Emprestimo;
import java.sql.*;
import java.util.*;
import java.time.LocalDate;

public class EmprestimoDAO {
    public List<Emprestimo> listar() throws SQLException {
        List<Emprestimo> lista = new ArrayList<>();
        try (Connection c = Conexao.abrir(); PreparedStatement p = c.prepareStatement("SELECT * FROM emprestimo ORDER BY id DESC"); ResultSet r = p.executeQuery()) {
            while (r.next()) {
                Emprestimo e = new Emprestimo();
                e.setId(r.getInt("id")); e.setUsuarioId(r.getInt("usuario_id")); e.setLivroId(r.getInt("livro_id"));
                e.setDataEmprestimo(LocalDate.parse(r.getString("data_emprestimo")));
                String devolucao = r.getString("data_devolucao");
                e.setDataDevolucao(devolucao == null ? null : LocalDate.parse(devolucao));
                e.setStatus(r.getString("status")); lista.add(e);
            }
        }
        return lista;
    }

    // A conexão usa transações IMMEDIATE do SQLite para proteger o estoque.
    public static int consultarQuantidade(Connection c, int livroId) throws SQLException {
        try (PreparedStatement p = c.prepareStatement("SELECT quantidade FROM livro WHERE id = ?")) {
            p.setInt(1, livroId);
            try (ResultSet r = p.executeQuery()) {
                if (!r.next()) throw new SQLException("Livro não encontrado.");
                return r.getInt("quantidade");
            }
        }
    }
    public static int contarAbertos(Connection c, int livroId) throws SQLException {
        try (PreparedStatement p = c.prepareStatement("SELECT COUNT(*) FROM emprestimo WHERE livro_id = ? AND status = 'Aberto'")) {
            p.setInt(1, livroId);
            try (ResultSet r = p.executeQuery()) { r.next(); return r.getInt(1); }
        }
    }
    public static void inserir(Connection c, int usuarioId, int livroId, LocalDate data) throws SQLException {
        if (data == null || data.isAfter(LocalDate.now())) throw new SQLException("Informe uma data de empréstimo válida, até hoje.");
        int total = consultarQuantidade(c, livroId);
        if (contarAbertos(c, livroId) >= total) throw new SQLException("Este livro não tem cópias disponíveis.");
        try (PreparedStatement p = c.prepareStatement("INSERT INTO emprestimo (usuario_id, livro_id, data_emprestimo, status) VALUES (?, ?, ?, 'Aberto')")) {
            p.setInt(1, usuarioId); p.setInt(2, livroId); p.setString(3, data.toString()); p.executeUpdate();
        }
    }
    public void registrar(int usuarioId, int livroId, LocalDate data) throws SQLException {
        try (Connection c = Conexao.abrir()) {
            c.setAutoCommit(false);
            try { inserir(c, usuarioId, livroId, data); c.commit(); }
            catch (SQLException e) { c.rollback(); throw e; }
        }
    }
    public void devolver(int id) throws SQLException {
        try (Connection c = Conexao.abrir(); PreparedStatement p = c.prepareStatement(
            "UPDATE emprestimo SET status = 'Devolvido', data_devolucao = ? WHERE id = ? AND status = 'Aberto'")) {
            p.setString(1, LocalDate.now().toString()); p.setInt(2, id);
            if (p.executeUpdate() != 1) throw new SQLException("Selecione um empréstimo aberto.");
        }
    }
    public void excluir(int id) throws SQLException {
        try (Connection c = Conexao.abrir(); PreparedStatement p = c.prepareStatement("DELETE FROM emprestimo WHERE id = ?")) {
            p.setInt(1, id);
            if (p.executeUpdate() != 1) throw new SQLException("Registro não encontrado.");
        }
    }
}
