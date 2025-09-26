package com.softwells.fanops.service;

import com.softwells.fanops.model.UsuarioEntity;
import com.softwells.fanops.repository.UsuarioRepository;
import jakarta.persistence.EntityNotFoundException;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UsuarioService {

  private final UsuarioRepository usuarioRepository;

  public UsuarioEntity obtenerUsuarioAutenticado() {
    String userEmail = Objects.requireNonNull(
        SecurityContextHolder.getContext().getAuthentication()).getName();
    return usuarioRepository.findByEmailIgnoreCase(userEmail)
        .orElseThrow(() -> new EntityNotFoundException(
            "No se encontró un usuario con el email: " + userEmail));
  }
}