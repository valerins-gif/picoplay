package com.cartagena.picoplay.structures;

import java.util.ArrayList;
import java.util.List;

import com.cartagena.picoplay.model.Cancion;

public class ParrillaListaDoble {
    private NodoListaDoble cabeza;
    private NodoListaDoble cola;
    private int tamano;

    public void agregarAlFinal(Cancion cancion) {
        NodoListaDoble nuevo = new NodoListaDoble(cancion);
        if (cabeza == null) {
            cabeza = cola = nuevo;
        } else {
            cola.siguiente = nuevo;
            nuevo.anterior = cola;
            cola = nuevo;
        }
        tamano++;
    }

    public boolean eliminarPorId(Long cancionId) {
        NodoListaDoble actual = cabeza;
        while (actual != null) {
            if (actual.cancion.getId().equals(cancionId)) {
                if (actual.anterior != null) actual.anterior.siguiente = actual.siguiente;
                else cabeza = actual.siguiente;

                if (actual.siguiente != null) actual.siguiente.anterior = actual.anterior;
                else cola = actual.anterior;

                tamano--;
                return true;
            }
            actual = actual.siguiente;
        }
        return false;
    }

    public void moverArriba(Long cancionId) {
        NodoListaDoble actual = cabeza;
        while (actual != null) {
            if (actual.cancion.getId().equals(cancionId) && actual.anterior != null) {
                Cancion temp = actual.cancion;
                actual.cancion = actual.anterior.cancion;
                actual.anterior.cancion = temp;
                return;
            }
            actual = actual.siguiente;
        }
    }

    public List<Cancion> obtenerParrilla() {
        List<Cancion> lista = new ArrayList<>();
        NodoListaDoble actual = cabeza;
        while (actual != null) {
            lista.add(actual.cancion);
            actual = actual.siguiente;
        }
        return lista;
    }

    public void vaciar() {
        cabeza = cola = null;
        tamano = 0;
    }

    public int getTamano() { return tamano; }
}