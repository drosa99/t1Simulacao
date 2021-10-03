package br.pucrs.t1Simulacao;

public class Evento {
    private TipoEnum tipo;
    private double tempo; //Tempo do evento
    private int idFila;

    public Evento(TipoEnum tipo, double tempo, int idFila) {
        this.idFila = idFila;
        this.setTipo(tipo);
        this.setTempo(tempo);
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

    public int getIdFila() {
        return idFila;
    }

    public void setIdFila(int idFila) {
        this.idFila = idFila;
    }

    public enum TipoEnum {
        CHEGADA, SAIDA, PASSAGEM;
    }

}
