package com.cartagena.picoplay.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Comparator;
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
import org.springframework.web.multipart.MultipartFile;

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
        respuesta.put("totalEmisiones", historial.size());
        respuesta.put("topArtista", topArtista);
        return respuesta;
    }

    @PostMapping("/canciones")
    public Map<String, Object> crearCancion(@RequestBody Map<String, Object> payload) {
        String titulo = String.valueOf(payload.getOrDefault("titulo", "")).trim();
        String artista = String.valueOf(payload.getOrDefault("artista", "")).trim();
        String genero = String.valueOf(payload.getOrDefault("genero", "Champeta")).trim();
        boolean artistaLocal = Boolean.parseBoolean(String.valueOf(payload.getOrDefault("artistaLocal", true)));

        if (!titulo.isEmpty() && !artista.isEmpty()) {
            radioService.registrarCancion(titulo, artista, genero, artistaLocal);
        }
        return obtenerEstadoCompleto();
    }

    @PostMapping("/canciones/upload")
    public Map<String, Object> crearCancionConAudio(
            @RequestParam String titulo,
            @RequestParam String artista,
            @RequestParam String genero,
            @RequestParam(defaultValue = "true") boolean artistaLocal,
            @RequestParam(required = false) MultipartFile archivoAudio
    ) throws IOException {
        String rutaPublica = null;
        if (archivoAudio != null && !archivoAudio.isEmpty()) {
            Path carpeta = Paths.get("uploads/audio");
            Files.createDirectories(carpeta);
            String original = archivoAudio.getOriginalFilename() != null ? archivoAudio.getOriginalFilename() : "audio.mp3";
            String nombreLimpio = System.currentTimeMillis() + "_" + original.replaceAll("[^a-zA-Z0-9._-]", "_");
            Path destino = carpeta.resolve(nombreLimpio);
            Files.copy(archivoAudio.getInputStream(), destino, StandardCopyOption.REPLACE_EXISTING);
            rutaPublica = "/audio/" + nombreLimpio;
        }

        if (!titulo.trim().isEmpty() && !artista.trim().isEmpty()) {
            Cancion creada = radioService.registrarCancion(titulo.trim(), artista.trim(), genero.trim(), artistaLocal);
            if (rutaPublica != null && creada != null && creada.getId() != null) {
                radioService.actualizarRutaAudio(creada.getId(), rutaPublica);
            }
        }
        return obtenerEstadoCompleto();
    }

    @PostMapping("/parrilla/agregar/{id}")
    public Map<String, Object> agregarParrilla(@PathVariable Long id) {
        radioService.agregarAParrilla(id);
        return obtenerEstadoCompleto();
    }

    @PostMapping("/cabina/siguiente")
    public Map<String, Object> reproducirSiguiente(@RequestParam(defaultValue = "CIRCULAR") String modo) {
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