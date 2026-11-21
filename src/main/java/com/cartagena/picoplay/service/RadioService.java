package com.cartagena.picoplay.service;

import com.cartagena.picoplay.model.Cancion;
import com.cartagena.picoplay.model.HistorialReproduccion;
import com.cartagena.picoplay.repository.CancionRepository;
import com.cartagena.picoplay.repository.HistorialRepository;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class RadioService {

    private final CancionRepository cancionRepo;
    private final HistorialRepository historialRepo;

    private final List<Cancion> parrilla = new LinkedList<>();
    private final LinkedList<Cancion> anilloLocal = new LinkedList<>();
    private final Queue<Cancion> colaCabina = new LinkedList<>();
    private int indiceRotacionLocal = 0;
    private String franjaActiva = "Franja Mañana (08:00 - 12:00)";

    public RadioService(CancionRepository cancionRepo, HistorialRepository historialRepo) {
        this.cancionRepo = cancionRepo;
        this.historialRepo = historialRepo;
        sincronizarEstructurasDesdeBD();
    }

    public void sincronizarEstructurasDesdeBD() {
        List<Cancion> todas = cancionRepo.findAll();
        anilloLocal.clear();
        for (Cancion c : todas) {
            if (c.isArtistaLocal()) {
                anilloLocal.add(c);
            }
        }
        if (!anilloLocal.isEmpty() && indiceRotacionLocal >= anilloLocal.size()) {
            indiceRotacionLocal = 0;
        }
    }

    public Cancion registrarCancion(String titulo, String artista, String genero, boolean artistaLocal) {
        Cancion nueva = new Cancion(titulo, artista, genero, artistaLocal);
        Cancion guardada = cancionRepo.save(nueva);
        sincronizarEstructurasDesdeBD();
        return guardada;
    }

    public void actualizarRutaAudio(Long id, String rutaAudio) {
        cancionRepo.findById(id).ifPresent(c -> {
            c.setRutaAudio(rutaAudio);
            cancionRepo.save(c);
        });
        sincronizarEstructurasDesdeBD();
    }

    public List<Cancion> obtenerCatalogoOrdenado() {
        List<Cancion> lista = cancionRepo.findAll();
        lista.sort(Comparator.comparing(Cancion::getTitulo, String.CASE_INSENSITIVE_ORDER));
        return lista;
    }

    public Cancion buscarEnBST(String titulo) {
        if (titulo == null) return null;
        return cancionRepo.findAll().stream()
                .filter(c -> c.getTitulo().equalsIgnoreCase(titulo.trim()))
                .findFirst()
                .orElse(null);
    }

    public void agregarAParrilla(Long idCancion) {
        cancionRepo.findById(idCancion).ifPresent(parrilla::add);
    }

    public void subirEnParrilla(Long idCancion) {
        for (int i = 1; i < parrilla.size(); i++) {
            if (parrilla.get(i).getId().equals(idCancion)) {
                Cancion temp = parrilla.get(i - 1);
                parrilla.set(i - 1, parrilla.get(i));
                parrilla.set(i, temp);
                break;
            }
        }
    }

    public void eliminarDeParrilla(Long idCancion) {
        parrilla.removeIf(c -> c.getId().equals(idCancion));
    }

    public List<Cancion> obtenerParrilla() {
        return new ArrayList<>(parrilla);
    }

    public List<Cancion> obtenerAnilloLocal() {
        return new ArrayList<>(anilloLocal);
    }

    public Cancion verSiguienteArtistaLocal() {
        if (anilloLocal.isEmpty()) return null;
        return anilloLocal.get(indiceRotacionLocal % anilloLocal.size());
    }

    public void inyectarRotacionLocalAParrilla() {
        Cancion siguiente = verSiguienteArtistaLocal();
        if (siguiente != null) {
            parrilla.add(siguiente);
            indiceRotacionLocal = (indiceRotacionLocal + 1) % anilloLocal.size();
        }
    }

    public void enviarParrillaACabina() {
        while (!parrilla.isEmpty()) {
            colaCabina.add(parrilla.remove(0));
        }
    }

    public List<Cancion> obtenerColaCabina() {
        return new ArrayList<>(colaCabina);
    }

    public HistorialReproduccion reproducirSiguienteEnCabina() {
        Cancion actual = colaCabina.poll();
        if (actual == null) return null;

        cancionRepo.findById(actual.getId()).ifPresent(c -> {
            c.setVecesReproducida(c.getVecesReproducida() + 1);
            cancionRepo.save(c);
        });

        HistorialReproduccion reg = new HistorialReproduccion();
        reg.setCancion(actual);

        HistorialReproduccion guardado = historialRepo.save(reg);
        sincronizarEstructurasDesdeBD();
        return guardado;
    }

    public List<HistorialReproduccion> obtenerHistorial() {
        List<HistorialReproduccion> lista = historialRepo.findAll();
        lista.sort((a, b) -> Long.compare(
                b.getId() != null ? b.getId() : 0L,
                a.getId() != null ? a.getId() : 0L
        ));
        return lista;
    }

    public String getFranjaActiva() {
        return franjaActiva;
    }

    public void setFranjaActiva(String franjaActiva) {
        this.franjaActiva = franjaActiva;
    }
}