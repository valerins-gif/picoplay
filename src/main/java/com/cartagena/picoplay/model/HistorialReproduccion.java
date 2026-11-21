package com.cartagena.picoplay.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "historial_reproducciones")
public class HistorialReproduccion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String tituloCancion;
    private String artista;
    private String genero;
    private boolean artistaLocal;
    private String franjaHoraria;
    private LocalDateTime fechaHora;

    public HistorialReproduccion() {}

    public HistorialReproduccion(Cancion cancion, String franjaHoraria) {
        this.tituloCancion = cancion.getTitulo();
        this.artista = cancion.getArtista();
        this.genero = cancion.getGenero();
        this.artistaLocal = cancion.isArtistaLocal();
        this.franjaHoraria = franjaHoraria;
        this.fechaHora = LocalDateTime.now();
    }

    public String getFechaFormateada() {
        return fechaHora != null ? fechaHora.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")) : "";
    }

    public Long getId() { return id; }
    public String getTituloCancion() { return tituloCancion; }
    public String getArtista() { return artista; }
    public String getGenero() { return genero; }
    public boolean isArtistaLocal() { return artistaLocal; }
    public String getFranjaHoraria() { return franjaHoraria; }
    public LocalDateTime getFechaHora() { return fechaHora; }
}