package com.softwells.fanops.controller;

import com.softwells.fanops.controller.dto.ApiResponse;
import com.softwells.fanops.controller.dto.EventoInscripcionDTO;
import com.softwells.fanops.model.EventoEntity;
import com.softwells.fanops.model.PenaEntity;
import com.softwells.fanops.service.EventoService;
import com.softwells.fanops.service.PenaService;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/pena")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')") // Proteger todo el controlador para administradores
public class PenaController {

  private final PenaService service;

  @GetMapping("/{id}")
  @PreAuthorize("isAuthenticated()") // Permitir a cualquier usuario autenticado ver los eventos
  public ResponseEntity<ApiResponse<PenaEntity>> findById(@PathVariable("id") Long id) {
    PenaEntity pena = service.findById(id);
    return ResponseEntity.ok(new ApiResponse<>(true, "Eventos recuperados", pena));
  }

}