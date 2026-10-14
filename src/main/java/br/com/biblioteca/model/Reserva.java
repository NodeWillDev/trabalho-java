package br.com.biblioteca.model;
import java.time.LocalDate;

public class Reserva {
    private int id;
    private LocalDate dataReserva;
    private String status;
    private int usuarioId;
    private int livroId;
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public LocalDate getDataReserva() { return dataReserva; }
    public void setDataReserva(LocalDate dataReserva) { this.dataReserva = dataReserva; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public int getUsuarioId() { return usuarioId; }
    public void setUsuarioId(int usuarioId) { this.usuarioId = usuarioId; }
    public int getLivroId() { return livroId; }
    public void setLivroId(int livroId) { this.livroId = livroId; }
}
