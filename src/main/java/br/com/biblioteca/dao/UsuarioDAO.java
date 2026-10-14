package br.com.biblioteca.dao;
import br.com.biblioteca.model.Usuario;
import java.sql.*;
import java.util.*;

public class UsuarioDAO {
    public List<Usuario> listar() throws SQLException {
        List<Usuario> lista = new ArrayList<>();
        try (Connection c = Conexao.abrir();
             PreparedStatement p = c.prepareStatement("SELECT * FROM usuario ORDER BY id");
             ResultSet r = p.executeQuery()) {
            while (r.next()) {
                Usuario item = new Usuario();
                item.setId(r.getInt("id"));
                item.setNome(r.getString("nome"));
                item.setEmail(r.getString("email"));
                item.setTelefone(r.getString("telefone"));
                item.setCpf(r.getString("cpf"));
                lista.add(item);
            }
        }
        return lista;
    }
    public void salvar(Usuario item) throws SQLException {
        String sql = item.getId() == 0
            ? "INSERT INTO usuario (nome, email, telefone, cpf) VALUES (?, ?, ?, ?)"
            : "UPDATE usuario SET nome = ?, email = ?, telefone = ?, cpf = ? WHERE id = ?";
        try (Connection c = Conexao.abrir(); PreparedStatement p = c.prepareStatement(sql)) {
            p.setString(1, item.getNome());
            p.setString(2, item.getEmail());
            p.setString(3, item.getTelefone());
            p.setString(4, item.getCpf());
            if (item.getId() != 0) p.setInt(5, item.getId());
            if (p.executeUpdate() != 1) throw new SQLException("Registro não encontrado.");
        }
    }
    // Apaga os vínculos primeiro. Se uma etapa falhar, tudo é desfeito.
    public void excluir(int id) throws SQLException {
        try (Connection c = Conexao.abrir()) {
            c.setAutoCommit(false);
            try {
                try (PreparedStatement p = c.prepareStatement("DELETE FROM emprestimo WHERE usuario_id = ?")) {
                    p.setInt(1, id); p.executeUpdate();
                }
                try (PreparedStatement p = c.prepareStatement("DELETE FROM reserva WHERE usuario_id = ?")) {
                    p.setInt(1, id); p.executeUpdate();
                }
                try (PreparedStatement p = c.prepareStatement("DELETE FROM usuario WHERE id = ?")) {
                    p.setInt(1, id);
                    if (p.executeUpdate() != 1) throw new SQLException("Registro não encontrado.");
                }
                c.commit();
            } catch (SQLException e) { c.rollback(); throw e; }
        }
    }
}
