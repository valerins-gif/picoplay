package com.cartagena.picoplay.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.cartagena.picoplay.model.Cancion;
import com.cartagena.picoplay.model.HistorialReproduccion;
import com.cartagena.picoplay.repository.CancionRepository;
import com.cartagena.picoplay.repository.HistorialRepository;
import com.cartagena.picoplay.structures.ArbolCatalogoBST;
import com.cartagena.picoplay.structures.ColaReproduccionFIFO;
import com.cartagena.picoplay.structures.ParrillaListaDoble;
import com.cartagena.picoplay.structures.RotacionArtistasCircular;

import jakarta.annotation.PostConstruct;

@Service
public class RadioService {

    private final CancionRepository cancionRepo;
    private final HistorialRepository historialRepo;

    private final ArbolCatalogoBST catalogoBST = new ArbolCatalogoBST();
    private final ParrillaListaDoble parrillaDoble = new ParrillaListaDoble();
    private final RotacionArtistasCircular rotacionCircular = new RotacionArtistasCircular();
    private final ColaReproduccionFIFO colaCabina = new ColaReproduccionFIFO();

    private String franjaActiva = "Franja Mañana (08:00 - 12:00)";

    public RadioService(CancionRepository cancionRepo, HistorialRepository historialRepo) {
        this.cancionRepo = cancionRepo;
        this.historialRepo = historialRepo;
    }

    @PostConstruct
    public void inicializarEstructuras() {
        if (cancionRepo.count() == 0) {
            cancionRepo.save(new Cancion("El Sebe Sebe", "Louis Tower", "Champeta", true));
            cancionRepo.save(new Cancion("La Espelucá", "Twister El Rey", "Champeta", true));
            cancionRepo.save(new Cancion("Rebelión", "Joe Arroyo", "Salsa", true));
            cancionRepo.save(new Cancion("La Piragua", "José Barros", "Cumbia", false));
            cancionRepo.save(new Cancion("Buscando tus Besos", "Koffe El Kafetero", "Champeta", true));
            cancionRepo.save(new Cancion("Matilde Lina", "Leandro Díaz", "Vallenato", false));
        }
        sincronizarEstructurasDesdeBD();
    }

    public void sincronizarEstructurasDesdeBD() {
        catalogoBST.limpiar();
        rotacionCircular.limpiar();
        List<Cancion> todas = cancionRepo.findAll();
        for (Cancion c : todas) {
            catalogoBST.insertar(c);
            if (c.isArtistaLocal()) {
                rotacionCircular.agregarCancionLocal(c);
            }
        }
    }

    public void registrarCancion(String titulo, String artista, String genero, boolean artistaLocal) {
        Cancion guardada = cancionRepo.save(new Cancion(titulo.trim(), artista.trim(), genero, artistaLocal));
        catalogoBST.insertar(guardada);
        if (guardada.isArtistaLocal()) {
            rotacionCircular.agregarCancionLocal(guardada);
        }
    }

    public Cancion buscarEnBST(String titulo) {
        return catalogoBST.buscarPorTitulo(titulo.trim());
    }

    public List<Cancion> obtenerCatalogoOrdenado() {
        return catalogoBST.listarOrdenado();
    }

    public void agregarAParrilla(Long id) {
        cancionRepo.findById(id).ifPresent(parrillaDoble::agregarAlFinal);
    }

    public void eliminarDeParrilla(Long id) {
        parrillaDoble.eliminarPorId(id);
    }

    public void subirEnParrilla(Long id) {
        parrillaDoble.moverArriba(id);
    }

    public List<Cancion> obtenerParrilla() {
        return parrillaDoble.obtenerParrilla();
    }

    public void inyectarRotacionLocalAParrilla() {
        Cancion turnoLocal = rotacionCircular.siguienteTurnoEquitativo();
        if (turnoLocal != null) {
            parrillaDoble.agregarAlFinal(turnoLocal);
        }
    }

    public Cancion verSiguienteArtistaLocal() {
        return rotacionCircular.verSiguienteEnTurno();
    }

    public List<Cancion> obtenerAnilloLocal() {
        return rotacionCircular.listarAnillo();
    }

    public void enviarParrillaACabina() {
        for (Cancion c : parrillaDoble.obtenerParrilla()) {
            colaCabina.encolar(c);
        }
        parrillaDoble.vaciar();
    }

    public void reproducirSiguienteEnCabina() {
        Cancion sonando = colaCabina.desencolar();
        if (sonando != null) {
            cancionRepo.findById(sonando.getId()).ifPresent(c -> {
                c.setVecesReproducida(c.getVecesReproducida() + 1);
                cancionRepo.save(c);
                historialRepo.save(new HistorialReproduccion(c, franjaActiva));
            });
            sincronizarEstructurasDesdeBD();
        }
    }

    public List<Cancion> obtenerColaCabina() {
        return colaCabina.listarCola();
    }

    public List<HistorialReproduccion> obtenerHistorial() {
        return historialRepo.findAllByOrderByFechaHoraDesc();
    }

    public String getFranjaActiva() { return franjaActiva; }
    public void setFranjaActiva(String franjaActiva) { this.franjaActiva = franjaActiva; }
}