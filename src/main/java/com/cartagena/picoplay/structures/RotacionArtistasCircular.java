package com.cartagena.picoplay.structures;

import java.util.ArrayList;
import java.util.List;

import com.cartagena.picoplay.model.Cancion;

public class RotacionArtistasCircular {
    private NodoCircular ultimo;
    private NodoCircular cursorRotacion;
    private int totalLocales;

    public void limpiar() {
        ultimo = null;
        cursorRotacion = null;
        totalLocales = 0;
    }

    public void agregarCancionLocal(Cancion cancion) {
        if (!cancion.isArtistaLocal()) return;
        NodoCircular nuevo = new NodoCircular(cancion);
        if (ultimo == null) {
            ultimo = nuevo;
            ultimo.siguiente = ultimo;
            cursorRotacion = ultimo;
        } else {
            nuevo.siguiente = ultimo.siguiente;
            ultimo.siguiente = nuevo;
            ultimo = nuevo;
        }
        totalLocales++;
    }

    // Entrega la siguiente canción local en el anillo y avanza el cursor circular
    public Cancion siguienteTurnoEquitativo() {
        if (cursorRotacion == null) return null;
        Cancion seleccionada = cursorRotacion.cancionLocal;
        cursorRotacion = cursorRotacion.siguiente;
        return seleccionada;
    }

    public Cancion verSiguienteEnTurno() {
        return cursorRotacion != null ? cursorRotacion.cancionLocal : null;
    }

    public List<Cancion> listarAnillo() {
        List<Cancion> lista = new ArrayList<>();
        if (ultimo == null) return lista;
        NodoCircular actual = ultimo.siguiente;
        for (int i = 0; i < totalLocales; i++) {
            lista.add(actual.cancionLocal);
            actual = actual.siguiente;
        }
        return lista;
    }

    public int getTotalLocales() { return totalLocales; }
}