package com.softwells.pblb.controller;

import com.softwells.pblb.controller.dto.ApiResponse;
import com.softwells.pblb.model.SocioEntity;
import com.softwells.pblb.model.UsuarioEntity;
import com.softwells.pblb.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;

@RestController
@RequestMapping("/api/usuarios")
@RequiredArgsConstructor
public class UsuarioController {

    private final UsuarioService usuarioService;

    @GetMapping("/me/socios")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<Set<SocioEntity>>> obtenerMisFichasDeSocio() {
        UsuarioEntity usuario = usuarioService.obtenerUsuarioAutenticado();
        return ResponseEntity.ok(new ApiResponse<>(true, "Fichas de socio del usuario recuperadas", usuario.getSocios()));
    }
}