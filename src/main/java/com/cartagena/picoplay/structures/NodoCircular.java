package com.cartagena.picoplay.structures;

import com.cartagena.picoplay.model.Cancion;

public class NodoCircular {
    public Cancion cancionLocal;
    public NodoCircular siguiente;

    public NodoCircular(Cancion cancionLocal) {
        this.cancionLocal = cancionLocal;
    }
}