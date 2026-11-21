package com.cartagena.picoplay.structures;

import com.cartagena.picoplay.model.Cancion;

public class NodoListaDoble {
    public Cancion cancion;
    public NodoListaDoble anterior;
    public NodoListaDoble siguiente;

    public NodoListaDoble(Cancion cancion) {
        this.cancion = cancion;
    }
}