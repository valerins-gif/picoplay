package com.cartagena.picoplay.structures;

import java.util.ArrayList;
import java.util.List;

import com.cartagena.picoplay.model.Cancion;

public class ColaReproduccionFIFO {
    private static class NodoCola {
        Cancion cancion;
        NodoCola siguiente;
        NodoCola(Cancion c) { this.cancion = c; }
    }

    private NodoCola frente;
    private NodoCola fin;

    public void encolar(Cancion cancion) {
        NodoCola nuevo = new NodoCola(cancion);
        if (fin != null) {
            fin.siguiente = nuevo;
        }
        fin = nuevo;
        if (frente == null) {
            frente = nuevo;
        }
    }

    public Cancion desencolar() {
        if (frente == null) return null;
        Cancion sale = frente.cancion;
        frente = frente.siguiente;
        if (frente == null) {
            fin = null;
        }
        return sale;
    }

    public Cancion verActual() {
        return frente != null ? frente.cancion : null;
    }

    public List<Cancion> listarCola() {
        List<Cancion> lista = new ArrayList<>();
        NodoCola actual = frente;
        while (actual != null) {
            lista.add(actual.cancion);
            actual = actual.siguiente;
        }
        return lista;
    }
}