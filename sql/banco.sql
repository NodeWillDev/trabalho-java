CREATE TABLE IF NOT EXISTS usuario (
  id INTEGER PRIMARY KEY AUTOINCREMENT,
  nome VARCHAR(120) NOT NULL,
  email VARCHAR(160) NOT NULL,
  telefone VARCHAR(30) NOT NULL,
  cpf VARCHAR(11) NOT NULL UNIQUE
);
CREATE TABLE IF NOT EXISTS categoria (
  id INTEGER PRIMARY KEY AUTOINCREMENT,
  nome VARCHAR(80) NOT NULL,
  descricao VARCHAR(255) NOT NULL
);
CREATE TABLE IF NOT EXISTS livro (
  id INTEGER PRIMARY KEY AUTOINCREMENT,
  titulo VARCHAR(160) NOT NULL,
  autor VARCHAR(120) NOT NULL,
  isbn VARCHAR(20) NOT NULL UNIQUE,
  ano_publicacao INTEGER NOT NULL,
  quantidade INTEGER NOT NULL CHECK (quantidade >= 0),
  categoria_id INTEGER NOT NULL,
  FOREIGN KEY (categoria_id) REFERENCES categoria(id) ON DELETE RESTRICT
);
CREATE TABLE IF NOT EXISTS emprestimo (
  id INTEGER PRIMARY KEY AUTOINCREMENT,
  data_emprestimo DATE NOT NULL,
  data_devolucao DATE,
  status VARCHAR(20) NOT NULL CHECK (status IN ('Aberto', 'Devolvido')),
  usuario_id INTEGER NOT NULL,
  livro_id INTEGER NOT NULL,
  FOREIGN KEY (usuario_id) REFERENCES usuario(id) ON DELETE RESTRICT,
  FOREIGN KEY (livro_id) REFERENCES livro(id) ON DELETE RESTRICT
);
CREATE TABLE IF NOT EXISTS reserva (
  id INTEGER PRIMARY KEY AUTOINCREMENT,
  data_reserva DATE NOT NULL,
  status VARCHAR(20) NOT NULL CHECK (status IN ('Ativa', 'Atendida', 'Cancelada')),
  usuario_id INTEGER NOT NULL,
  livro_id INTEGER NOT NULL,
  FOREIGN KEY (usuario_id) REFERENCES usuario(id) ON DELETE RESTRICT,
  FOREIGN KEY (livro_id) REFERENCES livro(id) ON DELETE RESTRICT
);
CREATE INDEX IF NOT EXISTS idx_emprestimo_livro_status ON emprestimo(livro_id, status);
CREATE INDEX IF NOT EXISTS idx_reserva_livro_status ON reserva(livro_id, status);
