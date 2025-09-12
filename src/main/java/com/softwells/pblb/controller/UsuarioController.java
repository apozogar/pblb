package com.softwells.pblb.controller;

import com.softwells.pblb.controller.dto.ApiResponse;
import com.softwells.pblb.model.SocioEntity;
import com.softwells.pblb.model.UsuarioEntity;
import com.softwells.pblb.service.SocioService;
import com.softwells.pblb.service.UsuarioService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;

@RestController
@RequestMapping("/api/usuarios")
@RequiredArgsConstructor
public class UsuarioController {

  private final UsuarioService usuarioService;
  private final SocioService socioService;

  @GetMapping("/me/socios")
  @PreAuthorize("isAuthenticated()")
  public ResponseEntity<ApiResponse<Set<SocioEntity>>> obtenerMisFichasDeSocio() {
    UsuarioEntity usuario = usuarioService.obtenerUsuarioAutenticado();
    return ResponseEntity.ok(
        new ApiResponse<>(true, "Fichas de socio del usuario recuperadas", usuario.getSocios()));
  }


  @GetMapping("/me/socio-principal")
  @PreAuthorize("isAuthenticated()")
  @Transactional(readOnly = true)
  public ResponseEntity<ApiResponse<SocioEntity>> obtenerMiFichaPrincipal() {
    UsuarioEntity usuario = usuarioService.obtenerUsuarioAutenticado();
    // Lógica para obtener la primera o principal ficha de socio
    SocioEntity socioPrincipal = socioService.sociosByUsuario(usuario.getUid()).stream().findFirst()
        .orElseThrow(() -> new EntityNotFoundException(
            "No se encontró una ficha de socio para este usuario."));
    return ResponseEntity.ok(new ApiResponse<>(true, "Ficha de socio recuperada", socioPrincipal));
  }

}