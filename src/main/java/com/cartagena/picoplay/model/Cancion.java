package com.cartagena.picoplay.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "canciones")
public class Cancion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String titulo;

    @Column(nullable = false)
    private String artista;

    @Column(nullable = false)
    private String genero; // Champeta, Salsa, Vallenato, etc.

    @Column(nullable = false)
    private boolean artistaLocal; // true = talento emergente de Cartagena

    private int vecesReproducida;

    public Cancion() {}

    public Cancion(String titulo, String artista, String genero, boolean artistaLocal) {
        this.titulo = titulo;
        this.artista = artista;
        this.genero = genero;
        this.artistaLocal = artistaLocal;
        this.vecesReproducida = 0;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }
    public String getArtista() { return artista; }
    public void setArtista(String artista) { this.artista = artista; }
    public String getGenero() { return genero; }
    public void setGenero(String genero) { this.genero = genero; }
    public boolean isArtistaLocal() { return artistaLocal; }
    public void setArtistaLocal(boolean artistaLocal) { this.artistaLocal = artistaLocal; }
    public int getVecesReproducida() { return vecesReproducida; }
    public void setVecesReproducida(int vecesReproducida) { this.vecesReproducida = vecesReproducida; }
}