# Sistema de Gerenciamento de Biblioteca

## Sobre o projeto

Sistema desktop desenvolvido em Java Swing para gerenciamento
de uma biblioteca.

O sistema possui integração com banco de dados e utiliza o
padrão arquitetural MVC (Model-View-Controller).

## Tecnologias

- Java
- Java Swing
- JDBC
- MySQL
- MVC

## Funcionalidades

- Cadastro de usuários
- Cadastro de livros
- Cadastro de categorias
- Registro de empréstimos
- Registro de reservas
- Consulta de livros
- Controle de empréstimos
- Controle de reservas

---

## Diagrama de Classes

```mermaid
classDiagram

    class Usuario {
        +int id
        +String nome
        +String email
        +String telefone
        +String cpf
    }

    class Livro {
        +int id
        +String titulo
        +String autor
        +String isbn
        +int anoPublicacao
        +int quantidade
        +int categoriaId
    }

    class Categoria {
        +int id
        +String nome
        +String descricao
    }

    class Emprestimo {
        +int id
        +Date dataEmprestimo
        +Date dataDevolucao
        +String status
        +int usuarioId
        +int livroId
    }

    class Reserva {
        +int id
        +Date dataReserva
        +String status
        +int usuarioId
        +int livroId
    }

    Categoria "1" --> "N" Livro : possui
    Usuario "1" --> "N" Emprestimo : realiza
    Livro "1" --> "N" Emprestimo : participa
    Usuario "1" --> "N" Reserva : realiza
    Livro "1" --> "N" Reserva : possui
