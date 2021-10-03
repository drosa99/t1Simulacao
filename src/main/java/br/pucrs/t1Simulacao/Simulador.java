package br.pucrs.t1Simulacao;

import java.util.*;
import java.util.stream.Collectors;

public class Simulador {

    public final String ARQUIVO_YML = "application.yml"; //Arquivo com as configurações da fila

    public int qtdNumerosAleatorios;
    public EscalonadorDeFilas escalonadorDeFilas;
    public List<Evento> eventosAcontecendo = new ArrayList<>();
    public List<Evento> eventosAgendados = new ArrayList<>();
    public double tempo;
    public double tempoAnterior = 0;
    public Map<Integer, double[]> probabilidades = new HashMap<>();
    private int semente;
    private Aleatorio aleatorios;

    public Simulador() {
        this.escalonadorDeFilas = new EscalonadorDeFilas();
        mapearYamlParaPOJO();
        this.aleatorios = new Aleatorio(this.qtdNumerosAleatorios, this.semente);
    }

    public void simulacao () {
        while(aleatorios.getQtAleatorios() < this.qtdNumerosAleatorios) {
            Evento eventoAtual = eventosAgendados.get(0); //Pega o próximo evento a ocorrer
            eventosAgendados.remove(0);             //Remove o evento dos agendados, pois já está sendo executado
            eventosAcontecendo.add(eventoAtual);          //Adiciona no evento que está acontecendo

            //Variável tempoAnterior é utilizada para o cálculo de probabilidade
            tempoAnterior = tempo;
            tempo = eventoAtual.getTempo();

            Fila filaAtual = escalonadorDeFilas.getFilas().get(0);

            if (eventoAtual.getTipo() == Evento.TipoEnum.CHEGADA) {
                chegada(filaAtual, aleatorios.geraProximoAleatorio() );
            } else if (eventoAtual.getTipo() == Evento.TipoEnum.SAIDA) {
                saida(filaAtual, aleatorios.geraProximoAleatorio());
            }
        }

        //Exibir probabilidades
        this.exibirProbabilidade();
    }

    private void chegada(Fila filaAtual, double aleatorio){

        this.ajustarProbabilidade(filaAtual);

        //Se ainda tempo espaço na fila
        if (filaAtual.getPopulacaoAtual() < filaAtual.getCapacidade()) {
            filaAtual.setPopulacaoAtual(filaAtual.getPopulacaoAtual() + 1);

            //Se só tem uma pessoa na fila ou nenhuma, essa pessoa já é atendida
            if (filaAtual.getPopulacaoAtual() <= filaAtual.getServidores()) {
                //System.out.println("EXECUTADO |" + eventoAtual.getTipo() + " | " + eventoAtual.getTempo());
                agendaSaida(aleatorio, filaAtual);
            }
        } else {
            //Não conseguiu entrar na fila pois estava cheia. E contabilizada como uma pessoa perdida
            filaAtual.setPerdidos(filaAtual.getPerdidos() + 1);
        }

        agendaChegada(aleatorio, filaAtual);
    }

    private void saida(Fila filaAtual, double aleatorio){
        //System.out.println("EXECUTADO |" + eventoAtual.getTipo() + " | " + eventoAtual.getTempo());
        this.ajustarProbabilidade(filaAtual);
        filaAtual.setPopulacaoAtual(filaAtual.getPopulacaoAtual() - 1);

        //Se tem gente na espera pra ficar de frente para o servidor
        if (filaAtual.getPopulacaoAtual() >= filaAtual.getServidores()) {
            agendaSaida(aleatorio, filaAtual);
        }
    }

    public void mapearYamlParaPOJO() {

        final Map<String, Object> dados = PropertiesLoader.loadProperties(ARQUIVO_YML);

        this.qtdNumerosAleatorios = (int) dados.get("numeros-aleatorios");

        this.semente = (int) dados.get("semente");

        final List<HashMap<String, Object>> dadosFilas = (List<HashMap<String, Object>>) dados.get("filas");


        //Mapeia do .yml para uma instancia de Fila a representacao dos dados contidos no arquivo
        List<Fila> filas = dadosFilas.stream().map(fila -> {
            Fila novaFila = new Fila();
            novaFila.setId(fila.containsKey("id") ? (int) fila.get("id") : 1);
            novaFila.setCapacidade((int) fila.get("capacidade"));
            novaFila.setChegadaInicial((double) fila.getOrDefault("chegada-inicial", -1.0));
            novaFila.setChegadaMaxima((double) fila.getOrDefault("chegada-maxima", -1.0));
            novaFila.setChegadaMinima((double) fila.getOrDefault("chegada-minima", -1.0));
            novaFila.setSaidaMaxima((double) fila.getOrDefault("saida-maxima", -1.0));
            novaFila.setSaidaMinima((double) fila.getOrDefault("saida-minima", -1.0));
            novaFila.setServidores((int) fila.get("servidores"));
            return novaFila;
        }).collect(Collectors.toList());

        // montar topologia de rede
        // dados de rede contém todas a filas
        List<LinkedHashMap<String, Object>> dadosRedes = (List<LinkedHashMap<String, Object>>) dados.get("redes");

        // itera a estrutura para popular as filas
        for (HashMap<String, Object> rede : dadosRedes) {

            int origem = (int) rede.get("origem");
            int destino = (int) rede.get("destino");
            double probabilidade = (double) rede.get("probabilidade");

            Fila filaOrigem = filas.stream().filter(f -> f.getId() == origem).findFirst().get();
            Fila filaDestino = filas.stream().filter(f -> f.getId() == destino).findFirst().get();

            // relaciona o destino à origem
            filaOrigem.putToFilaDestino(destino, filaDestino);

            // propabilidade que é passada no arquivo yml
            filaOrigem.putToProbabilidades(destino, probabilidade);
        }


        //System.out.println("EVENTO   |" + "tipo    |" +  " tempo");
        escalonadorDeFilas.getFilas().addAll(filas); //Adiciona todas filas no escalonador
        escalonadorDeFilas.getFilas().remove(0); //Remove o primeiro item, que é vazio

        //Adiciona probabilidade % de chance de a fila estar com x pessoas em seu k de multiplas filas
        //probabilidade = new double[escalonadorDeFilas.getFilas().get(0).getCapacidade() + 1];
        escalonadorDeFilas.getFilas().forEach(f -> {
            probabilidades.put(f.getId(), new double[f.getCapacidade() + 1]);
        });

        //Agenda o primeiro evento
        Evento primeiroEvento = new Evento(Evento.TipoEnum.CHEGADA, escalonadorDeFilas.getFilas().get(0).getChegadaInicial());
        eventosAgendados.add(primeiroEvento);
        //System.out.println("AGENDADO |" + primeiroEvento.getTipo() + " | " + primeiroEvento.getTempo());
    }

    public void agendaSaida(double aleatorio, Fila filaAtual) {
        // t = ((B-A) * aleatorio + A)
        double tempoSaida = (filaAtual.getSaidaMaxima() - filaAtual.getSaidaMinima()) * aleatorio  + filaAtual.getSaidaMinima();
        // t + tempo atual
        double tempoRealSaida = tempoSaida + tempo;

        Evento novaSaida = new Evento(Evento.TipoEnum.SAIDA, tempoRealSaida);
        eventosAgendados.add(novaSaida);
        eventosAgendados.sort(Comparator.comparingDouble(Evento::getTempo));

        //System.out.println("AGENDADO |" + novaSaida.getTipo() + " | " + tempoRealSaida);
    }

    public void agendaChegada(double aleatorio, Fila filaAtual) {
        // t = ((B-A) * aleatorio + A)
        double tempoChegada = (filaAtual.getChegadaMaxima() - filaAtual.getChegadaMinima()) * aleatorio + filaAtual.getChegadaMinima();
        // t + tempo atual
        double tempoRealChegada = tempoChegada + tempo;

        Evento novaChegada = new Evento(Evento.TipoEnum.CHEGADA, tempoRealChegada);
        eventosAgendados.add(novaChegada);
        eventosAgendados.sort(Comparator.comparingDouble(Evento::getTempo));

        //System.out.println("AGENDADO |" + novaChegada.getTipo() + " | " + tempoRealChegada);
    }

    public void ajustarProbabilidade(Fila filaAtual) {
        probabilidades.get(filaAtual.getId())[filaAtual.getPopulacaoAtual()] += this.tempo - this.tempoAnterior;
    }

    public void exibirProbabilidade() {
        System.out.println(this.aleatorios.toString());


        probabilidades.forEach((id, pFilas) -> {
            System.out.println("- Fila: " + id);
            System.out.println("Probabilidades:");
            double porcentagem = 0;
            int i = 0;
            for (double item : pFilas) {
                porcentagem += (item / this.tempo);
                String result = String.format("Value %.4f", ((item / this.tempo) * 100));
                System.out.println("Posição " + i + " : " + result + "%");
                i++;
            }

            System.out.println(porcentagem * 100 + "%");
            System.out.println("Perdidos " + this.escalonadorDeFilas.getFilas().get(id).getPerdidos());
            System.out.println("Tempo total: " + tempo);
        });


    }
}

