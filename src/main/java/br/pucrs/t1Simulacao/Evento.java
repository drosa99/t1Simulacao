package br.pucrs.t1Simulacao;

public class Evento {
    private TipoEnum tipo;
    private double tempo; //Tempo do evento

    public Evento(TipoEnum tipo, double tempo){
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

    public enum TipoEnum {
        CHEGADA, SAIDA;
    }

}
