package com.softwells.pblb.controller;

import com.softwells.pblb.dto.ApiResponse;
import com.softwells.pblb.model.EventoEntity;
import com.softwells.pblb.service.EventoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/eventos")
public class EventoController {
    private final EventoService eventoService;

    @Autowired
    public EventoController(EventoService eventoService) {
        this.eventoService = eventoService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<EventoEntity>> crear(@RequestBody EventoEntity evento) {
        return ResponseEntity.ok(new ApiResponse<>(true, "Evento creado exitosamente", 
            eventoService.crear(evento)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<EventoEntity>> actualizar(@PathVariable UUID id, 
                                                              @RequestBody EventoEntity evento) {
        return ResponseEntity.ok(new ApiResponse<>(true, "Evento actualizado exitosamente", 
            eventoService.actualizar(id, evento)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> eliminar(@PathVariable UUID id) {
        eventoService.eliminar(id);
        return ResponseEntity.ok(new ApiResponse<>(true, "Evento eliminado exitosamente", null));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<EventoEntity>> obtenerPorId(@PathVariable UUID id) {
        return ResponseEntity.ok(new ApiResponse<>(true, "Evento encontrado", 
            eventoService.obtenerPorId(id)));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<EventoEntity>>> obtenerTodos() {
        return ResponseEntity.ok(new ApiResponse<>(true, "Lista de eventos", 
            eventoService.obtenerTodos()));
    }

    @GetMapping("/futuros")
    public ResponseEntity<ApiResponse<List<EventoEntity>>> obtenerEventosFuturos() {
        return ResponseEntity.ok(new ApiResponse<>(true, "Lista de eventos futuros", 
            eventoService.obtenerEventosFuturos()));
    }
}
