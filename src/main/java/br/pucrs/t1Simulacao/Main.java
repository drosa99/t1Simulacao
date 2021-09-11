package br.pucrs.t1Simulacao;

public class Main {
    public static void main(String[] args) {
        Simulador simulador = new Simulador();
        simulador.mapearYamlParaPOJO();

        Aleatorio aleatorios = new Aleatorio(simulador.qtdNumerosAleatorios);

        simulador.simulacao(aleatorios);
    }
}
