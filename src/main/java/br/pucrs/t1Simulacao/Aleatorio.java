package br.pucrs.t1Simulacao;

public class Aleatorio {
    private int a;
    private int c;
    private double mod;
    private int semente;
    private int size;
    private double ultimoAleatorio;
    private double qtAleatorios;

    public Aleatorio(int size) {
        this.setA(54564);
        this.setC(31);
        this.setMod(Math.pow(2,39)-5);
        this.setSemente(7);
        this.setSize(size);
        this.setUltimoAleatorio(getSemente());
        this.setQtAleatorios(0);
    }

    /* Método para testar com outros aleatórios
    public Aleatorio(int size, double[] numerosAleatorios) {
        this.a = 54564;
        this.c = 31;
        this.mod = Math.pow(2,39)-5;
        this.semente = 7;
        this.size = size;
        this.numerosAleatorios = numerosAleatorios;
    } */

    /*
    public void geraPseudoAleatorio(){
        numerosAleatorios = new double[size];
        numerosAleatorios[0] = semente;
        for(int i = 1; i<size; i++){
            numerosAleatorios[i] = ((a*numerosAleatorios[i-1] + c) % mod);
        }
    }
     */

    public double geraProximoAleatorio(){
        setUltimoAleatorio(((getA() * getUltimoAleatorio() + getC()) % getMod())/ getMod());
        setQtAleatorios(getQtAleatorios() + 1);
        return getUltimoAleatorio();
    }

    /**
     * c = constante usada para maior variação dos números gerados
     * a = número
     * mod = número grande
     */
    public int getA() {
        return a;
    }

    public void setA(int a) {
        this.a = a;
    }

    public int getC() {
        return c;
    }

    public void setC(int c) {
        this.c = c;
    }

    public double getMod() {
        return mod;
    }

    public void setMod(double mod) {
        this.mod = mod;
    }

    public int getSemente() {
        return semente;
    }

    public void setSemente(int semente) {
        this.semente = semente;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public double getUltimoAleatorio() {
        return ultimoAleatorio;
    }

    public void setUltimoAleatorio(double ultimoAleatorio) {
        this.ultimoAleatorio = ultimoAleatorio;
    }

    public double getQtAleatorios() {
        return qtAleatorios;
    }

    public void setQtAleatorios(double qtAleatorios) {
        this.qtAleatorios = qtAleatorios;
    }
}
