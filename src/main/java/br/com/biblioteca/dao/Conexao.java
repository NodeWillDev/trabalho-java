package br.com.biblioteca.dao;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.sql.*;
import java.util.Properties;

public class Conexao {
    public static Connection abrir() throws SQLException {
        // Por padrão, o banco fica dentro da pasta dados do projeto.
        Path banco = Path.of(System.getProperty("biblioteca.banco", "dados/biblioteca.db"));
        try {
            Files.createDirectories(banco.toAbsolutePath().getParent());
        } catch (IOException e) {
            throw new SQLException("Não foi possível criar a pasta do banco de dados.", e);
        }
        Properties opcoes = new Properties();
        opcoes.setProperty("foreign_keys", "true");
        opcoes.setProperty("busy_timeout", "5000");
        // Uma transação de escrita por vez evita emprestar a última cópia duas vezes.
        opcoes.setProperty("transaction_mode", "IMMEDIATE");
        return DriverManager.getConnection("jdbc:sqlite:" + banco.toAbsolutePath(), opcoes);
    }

    public static void inicializar() throws SQLException {
        // Cria as cinco tabelas, sem apagar os dados que já foram cadastrados.
        try (InputStream arquivo = Conexao.class.getResourceAsStream("/banco.sql")) {
            if (arquivo == null) throw new SQLException("O arquivo banco.sql não foi incluído no programa.");
            String sql = new String(arquivo.readAllBytes(), StandardCharsets.UTF_8);
            try (Connection c = abrir(); Statement comando = c.createStatement()) {
                c.setAutoCommit(false);
                try {
                    for (String parte : sql.split(";")) if (!parte.isBlank()) comando.execute(parte);
                    c.commit();
                } catch (SQLException e) { c.rollback(); throw e; }
            }
        } catch (IOException e) {
            throw new SQLException("Não foi possível ler o script do banco.", e);
        }
    }
}
