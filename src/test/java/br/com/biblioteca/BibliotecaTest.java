package br.com.biblioteca;

import br.com.biblioteca.dao.*;
import br.com.biblioteca.model.*;
import br.com.biblioteca.controller.*;
import br.com.biblioteca.view.*;
import org.junit.*;
import static org.junit.Assert.*;
import java.nio.file.*;
import java.sql.*;
import java.time.LocalDate;
import java.util.concurrent.*;
import javax.swing.*;

// Os testes usam arquivos SQLite temporários e não alteram o banco do aluno.
public class BibliotecaTest {
    private Path configuracao;
    private int usuarioId, categoriaId, livroId;

    @Before public void preparar() throws Exception {
        configuracao = Files.createTempFile("biblioteca-teste-", ".db");
        System.setProperty("biblioteca.banco", configuracao.toString());
        Conexao.inicializar();
        Categoria cat = new Categoria(); cat.setNome("Literatura"); cat.setDescricao("Contos");
        new CategoriaDAO().salvar(cat); categoriaId = new CategoriaDAO().listar().get(0).getId();
        Usuario u = new Usuario(); u.setNome("Teste"); u.setEmail("teste@example.com"); u.setTelefone(""); u.setCpf("00000000000");
        new UsuarioDAO().salvar(u); usuarioId = new UsuarioDAO().listar().get(0).getId();
        Livro l = new Livro(); l.setTitulo("Livro teste"); l.setAutor("Autor"); l.setIsbn("ISBN-TESTE");
        l.setAnoPublicacao(2020); l.setQuantidade(1); l.setCategoriaId(categoriaId);
        new LivroDAO().salvar(l); livroId = new LivroDAO().listar().get(0).getId();
    }
    @After public void finalizar() throws Exception {
        System.clearProperty("biblioteca.banco");
        Files.deleteIfExists(configuracao);
    }


    @Test public void reiniciarMantemOsDados() throws Exception {
        Conexao.inicializar();
        assertEquals(1, new UsuarioDAO().listar().size());
        assertEquals(1, new CategoriaDAO().listar().size());
        assertEquals(1, new LivroDAO().listar().size());
        assertEquals("Livro teste", new LivroDAO().listar().get(0).getTitulo());
    }
    @Test public void cadastrosPodemSerEditadosEExcluidos() throws Exception {
        UsuarioDAO ud = new UsuarioDAO(); Usuario u = ud.listar().get(0);
        u.setNome("Nome alterado"); ud.salvar(u); assertEquals("Nome alterado", ud.listar().get(0).getNome());
        CategoriaDAO cd = new CategoriaDAO(); Categoria c = cd.listar().get(0);
        c.setDescricao("Descrição alterada"); cd.salvar(c); assertEquals("Descrição alterada", cd.listar().get(0).getDescricao());
        LivroDAO ld = new LivroDAO(); Livro l = ld.listar().get(0);
        l.setTitulo("Título alterado"); ld.salvar(l); assertEquals("Título alterado", ld.listar().get(0).getTitulo());
        ld.excluir(livroId); cd.excluir(categoriaId); ud.excluir(usuarioId);
        assertTrue(ld.listar().isEmpty()); assertTrue(cd.listar().isEmpty()); assertTrue(ud.listar().isEmpty());
    }
    @Test public void bancoImpedeDuplicadosEVinculosInvalidos() throws Exception {
        Usuario u = new UsuarioDAO().listar().get(0); u.setId(0);
        assertThrows(SQLException.class, () -> new UsuarioDAO().salvar(u));
        Livro l = new LivroDAO().listar().get(0); l.setId(0);
        assertThrows(SQLException.class, () -> new LivroDAO().salvar(l));
        new EmprestimoDAO().registrar(usuarioId, livroId, LocalDate.now());
        // A proteção do banco continua ativa: só o DAO apaga na ordem correta.
        try (Connection c = Conexao.abrir(); Statement s = c.createStatement()) {
            assertThrows(SQLException.class, () -> s.executeUpdate("DELETE FROM categoria WHERE id = " + categoriaId));
            assertThrows(SQLException.class, () -> s.executeUpdate("DELETE FROM usuario WHERE id = " + usuarioId));
            assertThrows(SQLException.class, () -> s.executeUpdate("DELETE FROM livro WHERE id = " + livroId));
        }
    }
    @Test public void emprestimoControlaCopiasEDevolucao() throws Exception {
        EmprestimoDAO dao = new EmprestimoDAO();
        dao.registrar(usuarioId, livroId, LocalDate.now());
        assertThrows(SQLException.class, () -> dao.registrar(usuarioId, livroId, LocalDate.now()));
        assertEquals(1, dao.listar().size());
        Livro l = new LivroDAO().listar().get(0); l.setQuantidade(0);
        assertThrows(SQLException.class, () -> new LivroDAO().salvar(l));
        assertEquals(1, new LivroDAO().listar().get(0).getQuantidade());
        int id = dao.listar().get(0).getId(); dao.devolver(id);
        assertEquals("Devolvido", dao.listar().get(0).getStatus());
        assertEquals(LocalDate.now(), dao.listar().get(0).getDataDevolucao());
        assertThrows(SQLException.class, () -> dao.devolver(id));
        dao.registrar(usuarioId, livroId, LocalDate.now()); assertEquals(2, dao.listar().size());
    }
    @Test public void reservaPodeSerCanceladaOuAtendida() throws Exception {
        ReservaDAO dao = new ReservaDAO();
        dao.registrar(usuarioId, livroId, LocalDate.now());
        assertThrows(SQLException.class, () -> dao.registrar(usuarioId, livroId, LocalDate.now()));
        int id = dao.listar().get(0).getId(); dao.cancelar(id);
        assertEquals("Cancelada", dao.listar().get(0).getStatus());
        final int cancelada = id;
        assertThrows(SQLException.class, () -> dao.atender(cancelada, livroId));
        dao.registrar(usuarioId, livroId, LocalDate.now());
        id = dao.listar().get(0).getId(); dao.atender(id, livroId);
        assertEquals("Atendida", dao.listar().get(0).getStatus());
        assertEquals(1, new EmprestimoDAO().listar().size());
        final int atendida = id;
        assertThrows(SQLException.class, () -> dao.atender(atendida, livroId));
        assertEquals(1, new EmprestimoDAO().listar().size());
    }
    @Test public void atenderSemEstoqueMantemReservaAtiva() throws Exception {
        new EmprestimoDAO().registrar(usuarioId, livroId, LocalDate.now());
        ReservaDAO dao = new ReservaDAO(); dao.registrar(usuarioId, livroId, LocalDate.now());
        int id = dao.listar().get(0).getId();
        assertThrows(SQLException.class, () -> dao.atender(id, livroId));
        assertEquals("Ativa", dao.listar().get(0).getStatus());
        assertEquals(1, new EmprestimoDAO().listar().size());
    }
    @Test public void datasInvalidasNaoSaoAceitas() throws Exception {
        assertThrows(IllegalArgumentException.class, () -> Validacao.data("31/02/2026"));
        assertThrows(IllegalArgumentException.class, () -> Validacao.data("abc"));
        assertThrows(SQLException.class, () -> new EmprestimoDAO().registrar(usuarioId, livroId, LocalDate.now().plusDays(1)));
        assertThrows(SQLException.class, () -> new ReservaDAO().registrar(usuarioId, livroId, LocalDate.now().plusDays(1)));
        assertTrue(new EmprestimoDAO().listar().isEmpty()); assertTrue(new ReservaDAO().listar().isEmpty());
    }
    @Test public void doisEmprestimosNaoUsamAUltimaCopia() throws Exception {
        ExecutorService pool = Executors.newFixedThreadPool(2);
        CountDownLatch inicio = new CountDownLatch(1);
        Callable<Boolean> trabalho = () -> {
            inicio.await();
            try { new EmprestimoDAO().registrar(usuarioId, livroId, LocalDate.now()); return true; }
            catch (SQLException e) { return false; }
        };
        try {
            Future<Boolean> a = pool.submit(trabalho), b = pool.submit(trabalho); inicio.countDown();
            int sucessos = (a.get(10, TimeUnit.SECONDS) ? 1 : 0) + (b.get(10, TimeUnit.SECONDS) ? 1 : 0);
            assertEquals(1, sucessos); assertEquals(1, new EmprestimoDAO().listar().size());
        } finally { pool.shutdownNow(); }
    }
    @Test public void telaDeUsuarioSalvaEditaEExcluiPelosBotoes() throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            UsuarioView tela = new UsuarioView() {
                @Override public void mensagem(String s) { }
                @Override public void erro(Exception e) { throw new AssertionError(e); }
                @Override public boolean confirmar(String s) { return true; }
            };
            new UsuarioController(tela);
            tela.nome.setText("Novo usuário"); tela.email.setText("novo@example.com"); tela.cpf.setText("111.111.111-11");
            tela.salvar.doClick(); assertEquals(2, tela.tabela.getRowCount());
            tela.tabela.setRowSelectionInterval(1, 1); tela.editar.doClick();
            assertEquals("Novo usuário", tela.nome.getText()); tela.nome.setText("Editado"); tela.salvar.doClick();
            assertEquals("Editado", tela.tabela.getValueAt(1, 1));
            tela.tabela.setRowSelectionInterval(1, 1); tela.excluir.doClick(); assertEquals(1, tela.tabela.getRowCount());
        });
    }

    @Test public void excluirLivroApagaSomenteSeusVinculos() throws Exception {
        new EmprestimoDAO().registrar(usuarioId, livroId, LocalDate.now());
        new ReservaDAO().registrar(usuarioId, livroId, LocalDate.now());
        Livro outro = new LivroDAO().listar().get(0); outro.setId(0); outro.setIsbn("OUTRO-ISBN");
        new LivroDAO().salvar(outro);
        int outroId = new LivroDAO().listar().get(1).getId();
        new EmprestimoDAO().registrar(usuarioId, outroId, LocalDate.now());
        new ReservaDAO().registrar(usuarioId, outroId, LocalDate.now());
        new LivroDAO().excluir(livroId);
        assertEquals(1, new LivroDAO().listar().size());
        assertEquals(outroId, new EmprestimoDAO().listar().get(0).getLivroId());
        assertEquals(1, new EmprestimoDAO().listar().size());
        assertEquals(outroId, new ReservaDAO().listar().get(0).getLivroId());
        assertEquals(1, new ReservaDAO().listar().size());
        assertEquals(1, new UsuarioDAO().listar().size()); assertEquals(1, new CategoriaDAO().listar().size());
    }
    @Test public void excluirUsuarioApagaSeusVinculosELiberaLivro() throws Exception {
        new EmprestimoDAO().registrar(usuarioId, livroId, LocalDate.now());
        new ReservaDAO().registrar(usuarioId, livroId, LocalDate.now());
        new UsuarioDAO().excluir(usuarioId);
        assertTrue(new UsuarioDAO().listar().isEmpty());
        assertTrue(new EmprestimoDAO().listar().isEmpty()); assertTrue(new ReservaDAO().listar().isEmpty());
        assertEquals(1, new LivroDAO().listar().size()); assertEquals(1, new CategoriaDAO().listar().size());
    }
    @Test public void excluirCategoriaApagaLivrosEVinculos() throws Exception {
        new EmprestimoDAO().registrar(usuarioId, livroId, LocalDate.now());
        new ReservaDAO().registrar(usuarioId, livroId, LocalDate.now());
        Categoria outra = new Categoria(); outra.setNome("Outra categoria"); outra.setDescricao(""); new CategoriaDAO().salvar(outra);
        new CategoriaDAO().excluir(categoriaId);
        assertEquals("Outra categoria", new CategoriaDAO().listar().get(0).getNome());
        assertEquals(1, new CategoriaDAO().listar().size());
        assertTrue(new LivroDAO().listar().isEmpty());
        assertTrue(new EmprestimoDAO().listar().isEmpty()); assertTrue(new ReservaDAO().listar().isEmpty());
        assertEquals(1, new UsuarioDAO().listar().size());
    }
    @Test public void excluirEmprestimoEReservaIndividualmente() throws Exception {
        EmprestimoDAO e = new EmprestimoDAO(); ReservaDAO r = new ReservaDAO();
        e.registrar(usuarioId, livroId, LocalDate.now()); r.registrar(usuarioId, livroId, LocalDate.now());
        e.excluir(e.listar().get(0).getId()); r.excluir(r.listar().get(0).getId());
        assertTrue(e.listar().isEmpty()); assertTrue(r.listar().isEmpty());
        e.registrar(usuarioId, livroId, LocalDate.now()); assertEquals(1, e.listar().size());
    }
    @Test public void falhaNaExclusaoDesfazRemocaoDosVinculos() throws Exception {
        new EmprestimoDAO().registrar(usuarioId, livroId, LocalDate.now());
        new ReservaDAO().registrar(usuarioId, livroId, LocalDate.now());
        try (Connection c = Conexao.abrir(); Statement s = c.createStatement()) {
            s.execute("CREATE TRIGGER impedir_exclusao BEFORE DELETE ON livro BEGIN SELECT RAISE(ABORT, 'Falha de teste'); END");
        }
        assertThrows(SQLException.class, () -> new LivroDAO().excluir(livroId));
        assertEquals(1, new LivroDAO().listar().size());
        assertEquals(1, new EmprestimoDAO().listar().size()); assertEquals(1, new ReservaDAO().listar().size());
    }
    @Test public void botaoDeExcluirLivroPermiteCancelarEConfirmar() throws Exception {
        new EmprestimoDAO().registrar(usuarioId, livroId, LocalDate.now());
        new ReservaDAO().registrar(usuarioId, livroId, LocalDate.now());
        SwingUtilities.invokeAndWait(() -> {
            boolean[] confirmar = {false};
            LivroView tela = new LivroView() {
                @Override public void mensagem(String s) { }
                @Override public void erro(Exception e) { throw new AssertionError(e); }
                @Override public boolean confirmar(String s) {
                    assertTrue(s.contains("empréstimos e reservas")); return confirmar[0];
                }
            };
            new LivroController(tela);
            tela.tabela.setRowSelectionInterval(0, 0); tela.excluir.doClick();
            assertEquals(1, tela.tabela.getRowCount());
            confirmar[0] = true; tela.excluir.doClick(); assertEquals(0, tela.tabela.getRowCount());
        });
        assertTrue(new EmprestimoDAO().listar().isEmpty()); assertTrue(new ReservaDAO().listar().isEmpty());
    }
}
