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
            Evento eventoAtual = eventosAgendados.remove(0);             //Remove o evento dos agendados, pois já está sendo executado
            eventosAcontecendo.add(eventoAtual);          //Adiciona no evento que está acontecendo

            //Variável tempoAnterior é utilizada para o cálculo de probabilidade
            tempoAnterior = tempo;
            tempo = eventoAtual.getTempo();

            Fila filaAtual = escalonadorDeFilas.getFilas().get(eventoAtual.getIdOrigem());

            Fila filaDestino = eventoAtual.getIdDestino() != null ? escalonadorDeFilas.getFilas().get(eventoAtual.getIdDestino()) : null;

            switch (eventoAtual.getTipo()) {
                case CHEGADA:
                    chegada(filaAtual);
                    break;
                case SAIDA:
                    saida(filaAtual);
                    break;
                case PASSAGEM:
                    passagem(filaAtual, filaDestino);
                    break;
            }

        }

        //Exibir probabilidades
        this.exibirProbabilidade();
    }

    private void passagem(Fila origem, Fila destino) {
        this.ajustarProbabilidade();
        origem.setPopulacaoAtual(origem.getPopulacaoAtual() - 1);

        if (origem.getPopulacaoAtual() >= origem.getServidores()) {
            Fila destinoProxFilaOrigem = sorteio(origem);
            if (destinoProxFilaOrigem != null) {
                agendaPassagem(origem, destinoProxFilaOrigem);
            } else {
                agendaSaida(origem);
            }
        }

        if (destino != null) {
            if (filaPodeAtender(destino)) {
                destino.setPopulacaoAtual(destino.getPopulacaoAtual() + 1);
                if (destino.getPopulacaoAtual() <= destino.getServidores()) {
                    Fila destino2 = sorteio(destino);
                    if (destino2 != null) { // se for para outra fila
                        agendaPassagem(destino, destino2);
                    } else {
                        agendaSaida(destino);
                    }
                }
            } else {
                destino.setPerdidos(destino.getPerdidos() + 1);
            }
        }
    }

    private void chegada(Fila filaAtual) {
        this.ajustarProbabilidade();
        //Se ainda tempo espaço na fila
        if (filaPodeAtender(filaAtual)) {
            filaAtual.setPopulacaoAtual(filaAtual.getPopulacaoAtual() + 1);

            //Se só tem uma pessoa na fila ou nenhuma, essa pessoa já é atendida
            if (filaAtual.getPopulacaoAtual() <= filaAtual.getServidores()) {
                //System.out.println("EXECUTADO |" + eventoAtual.getTipo() + " | " + eventoAtual.getTempo());

                Fila destino = sorteio(filaAtual);
                if (destino != null) {
                    agendaPassagem(filaAtual, destino);
                } else {
                    agendaSaida(filaAtual);
                }
            }
        } else {
            //Não conseguiu entrar na fila pois estava cheia. E contabilizada como uma pessoa perdida
            filaAtual.setPerdidos(filaAtual.getPerdidos() + 1);
        }

        agendaChegada(filaAtual);
    }

    private boolean filaPodeAtender(Fila filaAtual) {
        return filaAtual.getCapacidade() == -1 || filaAtual.getPopulacaoAtual() < filaAtual.getCapacidade();
    }

    private void saida(Fila filaAtual) {
        //System.out.println("EXECUTADO |" + eventoAtual.getTipo() + " | " + eventoAtual.getTempo());
        this.ajustarProbabilidade();
        filaAtual.setPopulacaoAtual(filaAtual.getPopulacaoAtual() - 1);

        //Se tem gente na espera pra ficar de frente para o servidor
        if (filaAtual.getPopulacaoAtual() >= filaAtual.getServidores()) {
            Fila destino = sorteio(filaAtual);
            if (destino != null) {
                agendaPassagem(filaAtual, destino);
            } else {
                agendaSaida(filaAtual);
            }
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
            probabilidades.put(f.getId(), new double[f.getCapacidade() != -1 ? f.getCapacidade() + 1 : 10]);
        });

        //Agenda o primeiro evento
        Evento primeiroEvento = new Evento(Evento.TipoEnum.CHEGADA, escalonadorDeFilas.getFilas().get(0).getChegadaInicial(), escalonadorDeFilas.getFilas().get(0).getId());
        eventosAgendados.add(primeiroEvento);
        //System.out.println("AGENDADO |" + primeiroEvento.getTipo() + " | " + primeiroEvento.getTempo());
    }

    public void agendaSaida(Fila filaAtual) {
        double aleatorio = aleatorios.geraProximoAleatorio();
        // t = ((B-A) * aleatorio + A)
        double tempoSaida = (filaAtual.getSaidaMaxima() - filaAtual.getSaidaMinima()) * aleatorio + filaAtual.getSaidaMinima();
        // t + tempo atual
        double tempoRealSaida = tempoSaida + tempo;

        Evento novaSaida = new Evento(Evento.TipoEnum.SAIDA, tempoRealSaida, filaAtual.getId());
        eventosAgendados.add(novaSaida);
        eventosAgendados.sort(Comparator.comparingDouble(Evento::getTempo));

        //System.out.println("AGENDADO |" + novaSaida.getTipo() + " | " + tempoRealSaida);
    }

    public void agendaChegada(Fila filaAtual) {
        double aleatorio = aleatorios.geraProximoAleatorio();
        // t = ((B-A) * aleatorio + A)
        double tempoChegada = (filaAtual.getChegadaMaxima() - filaAtual.getChegadaMinima()) * aleatorio + filaAtual.getChegadaMinima();
        // t + tempo atual
        double tempoRealChegada = tempoChegada + tempo;

        Evento novaChegada = new Evento(Evento.TipoEnum.CHEGADA, tempoRealChegada, filaAtual.getId());
        eventosAgendados.add(novaChegada);
        eventosAgendados.sort(Comparator.comparingDouble(Evento::getTempo));

        //System.out.println("AGENDADO |" + novaChegada.getTipo() + " | " + tempoRealChegada);
    }

    private void agendaPassagem(Fila filaOrigem, Fila filaDestino) {
        double aleatorio = aleatorios.geraProximoAleatorio();
        // t = ((B-A) * aleatorio + A)
        double tempoSaida = (filaOrigem.getSaidaMaxima() - filaOrigem.getSaidaMinima()) * aleatorio + filaOrigem.getSaidaMinima();
        // t + tempo atual
        double tempoRealSaida = tempoSaida + tempo;

        Evento novaSaida = new Evento(Evento.TipoEnum.PASSAGEM, tempoRealSaida, filaOrigem.getId(), filaDestino.getId());
        eventosAgendados.add(novaSaida);
        eventosAgendados.sort(Comparator.comparingDouble(Evento::getTempo));
    }

    private Fila sorteio(final Fila origem) {
        double intervalo = 0.0;
        final double aleatorio = aleatorios.geraProximoAleatorio();

        // faz o sort do hahmap da menor probabilidade para a maior, afim de calcular o intervalo
        Map<Integer, Double> sortedMap = origem.getProbabilidades().entrySet().stream()
                .sorted(Comparator.comparingDouble(Map.Entry::getValue))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (a, b) -> {
                            throw new AssertionError();
                        },
                        LinkedHashMap::new
                ));

        Fila filaDestino = null;

        for (Integer fila : sortedMap.keySet()) {
            intervalo += sortedMap.get(fila);
            if (aleatorio <= intervalo) { // se o numero aleatorio for menor que o do intervalo
                filaDestino = escalonadorDeFilas.getFilas().get(fila); // adiciona a fila destino
                break; // para iteração, pois ja achou o resultado
            }
        }
        return filaDestino;
    }

    public void ajustarProbabilidade() {
        escalonadorDeFilas.getFilas().forEach(fila -> {
            probabilidades.get(fila.getId())[fila.getPopulacaoAtual()] += this.tempo - this.tempoAnterior;
        });
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

