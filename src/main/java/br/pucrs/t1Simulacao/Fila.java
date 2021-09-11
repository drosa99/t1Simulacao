package br.pucrs.t1Simulacao;

public class Fila {
    //Dados carregados do arquivo de entrada .yml
    private double chegadaInicial;
    private int servidores;
    private int capacidade;
    private double chegadaMinima;
    private double chegadaMaxima;
    private double saidaMinima;
    private double saidaMaxima;

    //Dados de controle
    private int populacaoAtual;
    private int perdidos;

    public Fila() {}

    public double getChegadaInicial() {
        return chegadaInicial;
    }

    public void setChegadaInicial(double chegadaInicial) {
        this.chegadaInicial = chegadaInicial;
    }

    public int getServidores() {
        return servidores;
    }

    public void setServidores(int servidores) {
        this.servidores = servidores;
    }

    public int getCapacidade() {
        return capacidade;
    }

    public void setCapacidade(int capacidade) {
        this.capacidade = capacidade;
    }

    public double getChegadaMinima() {
        return chegadaMinima;
    }

    public void setChegadaMinima(double chegadaMinima) {
        this.chegadaMinima = chegadaMinima;
    }

    public double getChegadaMaxima() {
        return chegadaMaxima;
    }

    public void setChegadaMaxima(double chegadaMaxima) {
        this.chegadaMaxima = chegadaMaxima;
    }

    public double getSaidaMinima() {
        return saidaMinima;
    }

    public void setSaidaMinima(double saidaMinima) {
        this.saidaMinima = saidaMinima;
    }

    public double getSaidaMaxima() {
        return saidaMaxima;
    }

    public void setSaidaMaxima(double saidaMaxima) {
        this.saidaMaxima = saidaMaxima;
    }

    public int getPopulacaoAtual() {
        return populacaoAtual;
    }

    public void setPopulacaoAtual(int populacaoAtual) {
        this.populacaoAtual = populacaoAtual;
    }

    public int getPerdidos() {
        return perdidos;
    }

    public void setPerdidos(int perdidos) {
        this.perdidos = perdidos;
    }
}