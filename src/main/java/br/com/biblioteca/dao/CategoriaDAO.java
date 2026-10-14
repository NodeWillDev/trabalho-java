package br.com.biblioteca.dao;
import br.com.biblioteca.model.Categoria;
import java.sql.*;
import java.util.*;

public class CategoriaDAO {
    public List<Categoria> listar() throws SQLException {
        List<Categoria> lista = new ArrayList<>();
        try (Connection c = Conexao.abrir();
             PreparedStatement p = c.prepareStatement("SELECT * FROM categoria ORDER BY id");
             ResultSet r = p.executeQuery()) {
            while (r.next()) {
                Categoria item = new Categoria();
                item.setId(r.getInt("id"));
                item.setNome(r.getString("nome"));
                item.setDescricao(r.getString("descricao"));
                lista.add(item);
            }
        }
        return lista;
    }
    public void salvar(Categoria item) throws SQLException {
        String sql = item.getId() == 0
            ? "INSERT INTO categoria (nome, descricao) VALUES (?, ?)"
            : "UPDATE categoria SET nome = ?, descricao = ? WHERE id = ?";
        try (Connection c = Conexao.abrir(); PreparedStatement p = c.prepareStatement(sql)) {
            p.setString(1, item.getNome());
            p.setString(2, item.getDescricao());
            if (item.getId() != 0) p.setInt(3, item.getId());
            if (p.executeUpdate() != 1) throw new SQLException("Registro não encontrado.");
        }
    }
    // Apaga os vínculos primeiro. Se uma etapa falhar, tudo é desfeito.
    public void excluir(int id) throws SQLException {
        try (Connection c = Conexao.abrir()) {
            c.setAutoCommit(false);
            try {
                try (PreparedStatement p = c.prepareStatement("DELETE FROM emprestimo WHERE livro_id IN (SELECT id FROM livro WHERE categoria_id = ?)")) {
                    p.setInt(1, id); p.executeUpdate();
                }
                try (PreparedStatement p = c.prepareStatement("DELETE FROM reserva WHERE livro_id IN (SELECT id FROM livro WHERE categoria_id = ?)")) {
                    p.setInt(1, id); p.executeUpdate();
                }
                try (PreparedStatement p = c.prepareStatement("DELETE FROM livro WHERE categoria_id = ?")) {
                    p.setInt(1, id); p.executeUpdate();
                }
                try (PreparedStatement p = c.prepareStatement("DELETE FROM categoria WHERE id = ?")) {
                    p.setInt(1, id);
                    if (p.executeUpdate() != 1) throw new SQLException("Registro não encontrado.");
                }
                c.commit();
            } catch (SQLException e) { c.rollback(); throw e; }
        }
    }
}
