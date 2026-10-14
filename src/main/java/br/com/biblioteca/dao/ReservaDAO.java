package br.com.biblioteca.dao;
import br.com.biblioteca.model.Reserva;
import java.sql.*;
import java.util.*;
import java.time.LocalDate;

public class ReservaDAO {
    public List<Reserva> listar() throws SQLException {
        List<Reserva> lista = new ArrayList<>();
        try (Connection c = Conexao.abrir(); PreparedStatement p = c.prepareStatement("SELECT * FROM reserva ORDER BY id DESC"); ResultSet r = p.executeQuery()) {
            while (r.next()) {
                Reserva item = new Reserva();
                item.setId(r.getInt("id")); item.setUsuarioId(r.getInt("usuario_id")); item.setLivroId(r.getInt("livro_id"));
                item.setDataReserva(LocalDate.parse(r.getString("data_reserva"))); item.setStatus(r.getString("status")); lista.add(item);
            }
        }
        return lista;
    }
    public void registrar(int usuarioId, int livroId, LocalDate data) throws SQLException {
        if (data == null || data.isAfter(LocalDate.now())) throw new SQLException("Informe uma data de reserva válida, até hoje.");
        try (Connection c = Conexao.abrir()) {
            c.setAutoCommit(false);
            try {
                EmprestimoDAO.consultarQuantidade(c, livroId);
                try (PreparedStatement p = c.prepareStatement("SELECT id FROM reserva WHERE usuario_id = ? AND livro_id = ? AND status = 'Ativa'")) {
                    p.setInt(1, usuarioId); p.setInt(2, livroId);
                    try (ResultSet r = p.executeQuery()) {
                        if (r.next()) throw new SQLException("Este usuário já tem uma reserva ativa para este livro.");
                    }
                }
                try (PreparedStatement p = c.prepareStatement("INSERT INTO reserva (usuario_id, livro_id, data_reserva, status) VALUES (?, ?, ?, 'Ativa')")) {
                    p.setInt(1, usuarioId); p.setInt(2, livroId); p.setString(3, data.toString()); p.executeUpdate();
                }
                c.commit();
            } catch (SQLException e) { c.rollback(); throw e; }
        }
    }
    public void cancelar(int id) throws SQLException {
        try (Connection c = Conexao.abrir(); PreparedStatement p = c.prepareStatement("UPDATE reserva SET status = 'Cancelada' WHERE id = ? AND status = 'Ativa'")) {
            p.setInt(1, id);
            if (p.executeUpdate() != 1) throw new SQLException("Selecione uma reserva ativa.");
        }
    }
    // Atender cria um empréstimo e encerra a reserva na mesma transação.
    public void atender(int id, int livroId) throws SQLException {
        try (Connection c = Conexao.abrir()) {
            c.setAutoCommit(false);
            try {
                EmprestimoDAO.consultarQuantidade(c, livroId);
                int usuarioId;
                try (PreparedStatement p = c.prepareStatement("SELECT usuario_id, livro_id, status FROM reserva WHERE id = ?")) {
                    p.setInt(1, id);
                    try (ResultSet r = p.executeQuery()) {
                        if (!r.next() || !"Ativa".equals(r.getString("status")) || r.getInt("livro_id") != livroId)
                            throw new SQLException("Selecione uma reserva ativa.");
                        usuarioId = r.getInt("usuario_id");
                    }
                }
                EmprestimoDAO.inserir(c, usuarioId, livroId, LocalDate.now());
                try (PreparedStatement p = c.prepareStatement("UPDATE reserva SET status = 'Atendida' WHERE id = ?")) {
                    p.setInt(1, id); p.executeUpdate();
                }
                c.commit();
            } catch (SQLException e) { c.rollback(); throw e; }
        }
    }
    public void excluir(int id) throws SQLException {
        try (Connection c = Conexao.abrir(); PreparedStatement p = c.prepareStatement("DELETE FROM reserva WHERE id = ?")) {
            p.setInt(1, id);
            if (p.executeUpdate() != 1) throw new SQLException("Registro não encontrado.");
        }
    }
}
