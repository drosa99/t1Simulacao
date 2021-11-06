package br.pucrs.t1Simulacao;

public class Evento {
    private TipoEnum tipo;
    private double tempo; //Tempo do evento
    private Integer idOrigem;
    private Integer idDestino;

    public Evento(TipoEnum tipo, double tempo, int idOrigem) {
        this.idOrigem = idOrigem;
        this.setTipo(tipo);
        this.setTempo(tempo);
    }

    public Evento(TipoEnum tipo, double tempo, int idOrigem, int idDestino) {
        this.tipo = tipo;
        this.tempo = tempo;
        //this.idFila = idFila;
        this.idOrigem = idOrigem;
        this.idDestino = idDestino;
    }

    public TipoEnum getTipo() {
        return tipo;
    }

    public void setTipo(TipoEnum tipo) {
        this.tipo = tipo;
    }

    public double getTempo() {
        return tempo;
    }

    public void setTempo(double tempo) {
        this.tempo = tempo;
    }


    public Integer getIdOrigem() {
        return idOrigem;
    }

    public void setIdOrigem(int idOrigem) {
        this.idOrigem = idOrigem;
    }

    public Integer getIdDestino() {
        return idDestino;
    }

    public void setIdDestino(Integer idDestino) {
        this.idDestino = idDestino;
    }

    public enum TipoEnum {
        CHEGADA, SAIDA, PASSAGEM;
    }

}
