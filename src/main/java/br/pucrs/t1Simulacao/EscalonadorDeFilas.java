package br.pucrs.t1Simulacao;

import java.util.ArrayList;
import java.util.List;

public class EscalonadorDeFilas {
    private List<Fila> filas;

    public EscalonadorDeFilas(){
        filas = new ArrayList<Fila>();
        filas.add(new Fila());
    }

    public List<Fila> getFilas() {
        return filas;
    }
}
