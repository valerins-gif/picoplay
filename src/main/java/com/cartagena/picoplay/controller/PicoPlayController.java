package com.cartagena.picoplay.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.cartagena.picoplay.model.Cancion;
import com.cartagena.picoplay.service.RadioService;

@Controller
public class PicoPlayController {

    private final RadioService radioService;

    public PicoPlayController(RadioService radioService) {
        this.radioService = radioService;
    }

    @GetMapping("/")
    public String inicio(@RequestParam(required = false) String busqueda, Model model) {
        model.addAttribute("catalogo", radioService.obtenerCatalogoOrdenado());
        model.addAttribute("parrilla", radioService.obtenerParrilla());
        model.addAttribute("anilloLocal", radioService.obtenerAnilloLocal());
        model.addAttribute("siguienteLocal", radioService.verSiguienteArtistaLocal());
        model.addAttribute("colaCabina", radioService.obtenerColaCabina());
        model.addAttribute("historial", radioService.obtenerHistorial());
        model.addAttribute("franjaActiva", radioService.getFranjaActiva());

        if (busqueda != null && !busqueda.isBlank()) {
            Cancion encontrada = radioService.buscarEnBST(busqueda);
            model.addAttribute("resultadoBusqueda", encontrada);
            model.addAttribute("terminoBuscado", busqueda);
        }
        return "index";
    }

    @PostMapping("/canciones")
    public String agregarCancion(@RequestParam String titulo,
                                 @RequestParam String artista,
                                 @RequestParam String genero,
                                 @RequestParam(defaultValue = "false") boolean artistaLocal) {
        radioService.registrarCancion(titulo, artista, genero, artistaLocal);
        return "redirect:/";
    }

    @PostMapping("/franja")
    public String cambiarFranja(@RequestParam String franja) {
        radioService.setFranjaActiva(franja);
        return "redirect:/";
    }

    @PostMapping("/parrilla/agregar/{id}")
    public String agregarAParrilla(@PathVariable Long id) {
        radioService.agregarAParrilla(id);
        return "redirect:/";
    }

    @PostMapping("/parrilla/subir/{id}")
    public String subirEnParrilla(@PathVariable Long id) {
        radioService.subirEnParrilla(id);
        return "redirect:/";
    }

    @PostMapping("/parrilla/eliminar/{id}")
    public String eliminarDeParrilla(@PathVariable Long id) {
        radioService.eliminarDeParrilla(id);
        return "redirect:/";
    }

    @PostMapping("/rotacion/inyectar")
    public String inyectarTurnoLocal() {
        radioService.inyectarRotacionLocalAParrilla();
        return "redirect:/";
    }

    @PostMapping("/cabina/cargar")
    public String enviarACabina() {
        radioService.enviarParrillaACabina();
        return "redirect:/";
    }

    @PostMapping("/cabina/reproducir")
    public String reproducirSiguiente() {
        radioService.reproducirSiguienteEnCabina();
        return "redirect:/";
    }
}