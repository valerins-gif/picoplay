package com.cartagena.picoplay.controller;

import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.cartagena.picoplay.model.Cancion;
import com.cartagena.picoplay.model.HistorialReproduccion;
import com.cartagena.picoplay.service.RadioService;

@RestController
@RequestMapping("/api")
public class PicoPlayApiController {

    private final RadioService radioService;

    public PicoPlayApiController(RadioService radioService) {
        this.radioService = radioService;
    }

    @GetMapping("/estado")
    public Map<String, Object> obtenerEstadoCompleto() {
        List<Cancion> catalogo = radioService.obtenerCatalogoOrdenado();
        List<HistorialReproduccion> historial = radioService.obtenerHistorial();

        String topArtista = catalogo.stream()
                .filter(c -> c.getVecesReproducida() > 0)
                .max(Comparator.comparingInt(Cancion::getVecesReproducida))
                .map(Cancion::getArtista)
                .orElse("-");

        Map<String, Object> respuesta = new LinkedHashMap<>();
        respuesta.put("catalogo", catalogo);
        respuesta.put("parrilla", radioService.obtenerParrilla());
        respuesta.put("anilloLocal", radioService.obtenerAnilloLocal());
        respuesta.put("siguienteLocal", radioService.verSiguienteArtistaLocal());
        respuesta.put("colaCabina", radioService.obtenerColaCabina());
        respuesta.put("historial", historial);
        respuesta.put("franjaActiva", radioService.getFranjaActiva());
        respuesta.put("totalEmisiones", historial.size());
        respuesta.put("topArtista", topArtista);
        return respuesta;
    }

    @PostMapping("/canciones")
    public Map<String, Object> crearCancion(@RequestBody Map<String, Object> payload) {
        String titulo = String.valueOf(payload.getOrDefault("titulo", "")).trim();
        String artista = String.valueOf(payload.getOrDefault("artista", "")).trim();
        String genero = String.valueOf(payload.getOrDefault("genero", "Champeta")).trim();
        boolean artistaLocal = Boolean.parseBoolean(String.valueOf(payload.getOrDefault("artistaLocal", false)));

        if (!titulo.isEmpty() && !artista.isEmpty()) {
            radioService.registrarCancion(titulo, artista, genero, artistaLocal);
        }
        return obtenerEstadoCompleto();
    }

    @GetMapping("/buscar")
    public Map<String, Object> buscarEnBST(@RequestParam String titulo) {
        Cancion encontrada = radioService.buscarEnBST(titulo);
        Map<String, Object> res = new HashMap<>();
        res.put("encontrada", encontrada != null);
        res.put("cancion", encontrada);
        return res;
    }

    @PostMapping("/parrilla/agregar/{id}")
    public Map<String, Object> agregarParrilla(@PathVariable Long id) {
        radioService.agregarAParrilla(id);
        return obtenerEstadoCompleto();
    }

    @PostMapping("/parrilla/subir/{id}")
    public Map<String, Object> subirParrilla(@PathVariable Long id) {
        radioService.subirEnParrilla(id);
        return obtenerEstadoCompleto();
    }

    @PostMapping("/parrilla/eliminar/{id}")
    public Map<String, Object> eliminarParrilla(@PathVariable Long id) {
        radioService.eliminarDeParrilla(id);
        return obtenerEstadoCompleto();
    }

    @PostMapping("/rotacion/inyectar")
    public Map<String, Object> inyectarTurnoLocal() {
        radioService.inyectarRotacionLocalAParrilla();
        return obtenerEstadoCompleto();
    }

    @PostMapping("/cabina/cargar")
    public Map<String, Object> cargarCabina() {
        radioService.enviarParrillaACabina();
        return obtenerEstadoCompleto();
    }

    @PostMapping("/cabina/siguiente")
    public Map<String, Object> reproducirSiguiente(@RequestParam(defaultValue = "CIRCULAR") String modo) {
        // Si la cola de cabina está vacía, despacha automáticamente según la estructura seleccionada
        if (radioService.obtenerColaCabina().isEmpty()) {
            if (!radioService.obtenerParrilla().isEmpty()) {
                radioService.enviarParrillaACabina();
            } else if ("CIRCULAR".equals(modo)) {
                radioService.inyectarRotacionLocalAParrilla();
                radioService.enviarParrillaACabina();
            } else if ("PRIORITY_TREE".equals(modo)) {
                radioService.obtenerCatalogoOrdenado().stream()
                        .min(Comparator.comparingInt(Cancion::getVecesReproducida))
                        .ifPresent(c -> {
                            radioService.agregarAParrilla(c.getId());
                            radioService.enviarParrillaACabina();
                        });
            } else {
                radioService.obtenerCatalogoOrdenado().stream()
                        .findFirst()
                        .ifPresent(c -> {
                            radioService.agregarAParrilla(c.getId());
                            radioService.enviarParrillaACabina();
                        });
            }
        }
        radioService.reproducirSiguienteEnCabina();
        return obtenerEstadoCompleto();
    }
}