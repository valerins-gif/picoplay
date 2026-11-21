package com.cartagena.picoplay.structures;

import com.cartagena.picoplay.model.Cancion;
import java.util.ArrayList;
import java.util.List;

public class ArbolCatalogoBST {
    private NodoBST raiz;

    public void limpiar() {
        this.raiz = null;
    }

    public void insertar(Cancion cancion) {
        raiz = insertarRec(raiz, cancion);
    }

    private NodoBST insertarRec(NodoBST actual, Cancion cancion) {
        if (actual == null) {
            return new NodoBST(cancion);
        }
        int comparacion = cancion.getTitulo().compareToIgnoreCase(actual.dato.getTitulo());
        if (comparacion < 0) {
            actual.izquierdo = insertarRec(actual.izquierdo, cancion);
        } else if (comparacion > 0) {
            actual.derecho = insertarRec(actual.derecho, cancion);
        } else {
            actual.dato = cancion; // Actualiza si ya existe el título
        }
        return actual;
    }

    public Cancion buscarPorTitulo(String titulo) {
        return buscarRec(raiz, titulo);
    }

    private Cancion buscarRec(NodoBST actual, String titulo) {
        if (actual == null || titulo == null) return null;
        int comparacion = titulo.compareToIgnoreCase(actual.dato.getTitulo());
        if (comparacion == 0) return actual.dato;
        return comparacion < 0
                ? buscarRec(actual.izquierdo, titulo)
                : buscarRec(actual.derecho, titulo);
    }

    // Recorrido In-Order: devuelve todo el catálogo ordenado alfabéticamente
    public List<Cancion> listarOrdenado() {
        List<Cancion> resultado = new ArrayList<>();
        inOrderRec(raiz, resultado);
        return resultado;
    }

    private void inOrderRec(NodoBST actual, List<Cancion> lista) {
        if (actual != null) {
            inOrderRec(actual.izquierdo, lista);
            lista.add(actual.dato);
            inOrderRec(actual.derecho, lista);
        }
    }
}