package com.softwells.pblb.controller;

import com.softwells.pblb.dto.ApiResponse;
import com.softwells.pblb.model.SocioEntity;
import com.softwells.pblb.service.SocioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/socios")
public class SocioController {
    private final SocioService socioService;

    @Autowired
    public SocioController(SocioService socioService) {
        this.socioService = socioService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<SocioEntity>> crear(@RequestBody SocioEntity socio) {
        return ResponseEntity.ok(new ApiResponse<>(true, "Socio creado exitosamente", 
            socioService.crear(socio)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<SocioEntity>> actualizar(@PathVariable UUID id, 
                                                             @RequestBody SocioEntity socio) {
        return ResponseEntity.ok(new ApiResponse<>(true, "Socio actualizado exitosamente", 
            socioService.actualizar(id, socio)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> eliminar(@PathVariable UUID id) {
        socioService.eliminar(id);
        return ResponseEntity.ok(new ApiResponse<>(true, "Socio eliminado exitosamente", null));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<SocioEntity>> obtenerPorId(@PathVariable UUID id) {
        return ResponseEntity.ok(new ApiResponse<>(true, "Socio encontrado", 
            socioService.obtenerPorId(id)));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<SocioEntity>>> obtenerTodos() {
        return ResponseEntity.ok(new ApiResponse<>(true, "Lista de socios", 
            socioService.obtenerTodos()));
    }

    @GetMapping("/activos")
    public ResponseEntity<ApiResponse<List<SocioEntity>>> obtenerSociosActivos() {
        return ResponseEntity.ok(new ApiResponse<>(true, "Lista de socios activos", 
            socioService.obtenerSociosActivos()));
    }
}
