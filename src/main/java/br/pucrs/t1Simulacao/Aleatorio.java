package br.pucrs.t1Simulacao;

public class Aleatorio {
    private int a;
    private long c;
    private double mod;
    private int semente;
    private int tamanho;
    private double ultimoAleatorio;
    private int qtAleatorios;

    //public double[] array = new Random().doubles(100001).toArray(); //array com aleatorios para teste

    public Aleatorio(int tamanho, int semente) {
        this.setA(16807);
        this.setC(11);
        this.setMod(Math.pow(2,31) - 1);

        this.setSemente(semente);
        this.setTamanho(tamanho);
        this.setUltimoAleatorio(getSemente());
        this.setQtAleatorios(0);
    }


    public double geraProximoAleatorio(){
        setUltimoAleatorio(((getA() * getUltimoAleatorio() + getC()) % getMod()));
        setQtAleatorios(getQtAleatorios() + 1);
        return getUltimoAleatorio() / getMod();
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

    public long getC() {
        return c;
    }

    public void setC(long c) {
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

    public int getTamanho() {
        return tamanho;
    }

    public void setTamanho(int tamanho) {
        this.tamanho = tamanho;
    }

    public double getUltimoAleatorio() {
        return ultimoAleatorio;
    }

    public void setUltimoAleatorio(double ultimoAleatorio) {
        this.ultimoAleatorio = ultimoAleatorio;
    }

    public int getQtAleatorios() {
        return qtAleatorios;
    }

    public void setQtAleatorios(int qtAleatorios) {
        this.qtAleatorios = qtAleatorios;
    }

    @Override
    public String toString() {
        return "Gerador: \n" +
                " a=" + a +
                " \n c=" + c +
                " \n mod=" + mod +
                " \n semente=" + semente +
                " \n tamanho=" + tamanho +
                " \n ultimoAleatorio=" + ultimoAleatorio +
                " \n qtAleatorios=" + qtAleatorios;
    }
}
