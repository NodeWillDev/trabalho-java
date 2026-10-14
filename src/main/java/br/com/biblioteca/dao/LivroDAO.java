package br.com.biblioteca.dao;
import br.com.biblioteca.model.Livro;
import java.sql.*;
import java.util.*;

public class LivroDAO {
    public List<Livro> listar() throws SQLException {
        List<Livro> lista = new ArrayList<>();
        try (Connection c = Conexao.abrir();
             PreparedStatement p = c.prepareStatement("SELECT * FROM livro ORDER BY id");
             ResultSet r = p.executeQuery()) {
            while (r.next()) {
                Livro item = new Livro();
                item.setId(r.getInt("id"));
                item.setTitulo(r.getString("titulo"));
                item.setAutor(r.getString("autor"));
                item.setIsbn(r.getString("isbn"));
                item.setAnoPublicacao(r.getInt("ano_publicacao"));
                item.setQuantidade(r.getInt("quantidade"));
                item.setCategoriaId(r.getInt("categoria_id"));
                lista.add(item);
            }
        }
        return lista;
    }
    public void salvar(Livro item) throws SQLException {
        String sql = item.getId() == 0
            ? "INSERT INTO livro (titulo, autor, isbn, ano_publicacao, quantidade, categoria_id) VALUES (?, ?, ?, ?, ?, ?)"
            : "UPDATE livro SET titulo = ?, autor = ?, isbn = ?, ano_publicacao = ?, quantidade = ?, categoria_id = ? WHERE id = ?";
        try (Connection c = Conexao.abrir()) {
            c.setAutoCommit(false);
            try {
                if (item.getId() != 0) {
                    EmprestimoDAO.consultarQuantidade(c, item.getId());
                    if (item.getQuantidade() < EmprestimoDAO.contarAbertos(c, item.getId()))
                        throw new SQLException("A quantidade não pode ser menor que o número de empréstimos abertos.");
                }
                try (PreparedStatement p = c.prepareStatement(sql)) {
            p.setString(1, item.getTitulo());
            p.setString(2, item.getAutor());
            p.setString(3, item.getIsbn());
            p.setInt(4, item.getAnoPublicacao());
            p.setInt(5, item.getQuantidade());
            p.setInt(6, item.getCategoriaId());
                    if (item.getId() != 0) p.setInt(7, item.getId());
                    if (p.executeUpdate() != 1) throw new SQLException("Livro não encontrado.");
                }
                c.commit();
            } catch (SQLException e) { c.rollback(); throw e; }
        }
    }
    // Apaga os vínculos primeiro. Se uma etapa falhar, tudo é desfeito.
    public void excluir(int id) throws SQLException {
        try (Connection c = Conexao.abrir()) {
            c.setAutoCommit(false);
            try {
                try (PreparedStatement p = c.prepareStatement("DELETE FROM emprestimo WHERE livro_id = ?")) {
                    p.setInt(1, id); p.executeUpdate();
                }
                try (PreparedStatement p = c.prepareStatement("DELETE FROM reserva WHERE livro_id = ?")) {
                    p.setInt(1, id); p.executeUpdate();
                }
                try (PreparedStatement p = c.prepareStatement("DELETE FROM livro WHERE id = ?")) {
                    p.setInt(1, id);
                    if (p.executeUpdate() != 1) throw new SQLException("Registro não encontrado.");
                }
                c.commit();
            } catch (SQLException e) { c.rollback(); throw e; }
        }
    }
}
