package br.com.biblioteca.controller;
import java.time.*;
import java.time.format.*;

public class Validacao {
    public static String obrigatorio(String texto, String campo, int limite) {
        String valor = texto.trim();
        if (valor.isEmpty()) throw new IllegalArgumentException("Preencha: " + campo + ".");
        if (valor.length() > limite) throw new IllegalArgumentException(campo + " deve ter até " + limite + " caracteres.");
        return valor;
    }
    public static int inteiro(String texto, String campo, int minimo, int maximo) {
        try {
            int valor = Integer.parseInt(texto.trim());
            if (valor < minimo || valor > maximo) throw new NumberFormatException();
            return valor;
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(campo + " deve ser um número inteiro entre " + minimo + " e " + maximo + ".");
        }
    }
    public static LocalDate data(String texto) {
        try {
            LocalDate data = LocalDate.parse(texto.trim(), DateTimeFormatter.ofPattern("dd/MM/uuuu").withResolverStyle(ResolverStyle.STRICT));
            if (data.isAfter(LocalDate.now())) throw new IllegalArgumentException("A data não pode ser futura.");
            return data;
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Informe uma data válida no formato dd/MM/aaaa.");
        }
    }
    public static String formatar(LocalDate data) {
        return data == null ? "" : data.format(DateTimeFormatter.ofPattern("dd/MM/uuuu"));
    }
}
