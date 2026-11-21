package com.cartagena.picoplay.structures;

import com.cartagena.picoplay.model.Cancion;

public class NodoBST {
    public Cancion dato;
    public NodoBST izquierdo;
    public NodoBST derecho;

    public NodoBST(Cancion dato) {
        this.dato = dato;
    }
}