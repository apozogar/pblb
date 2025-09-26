package com.softwells.fanops.service;

import com.softwells.fanops.controller.dto.EventoInscripcionDTO;
import com.softwells.fanops.mapper.EventoMapper;
import com.softwells.fanops.model.EventoEntity;
import com.softwells.fanops.model.SocioEntity;
import com.softwells.fanops.model.UsuarioEntity;
import com.softwells.fanops.repository.EventoRepository;
import com.softwells.fanops.repository.UsuarioRepository;
import jakarta.persistence.EntityNotFoundException;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class EventoService {

  private final EventoRepository eventoRepository;
  private final UsuarioRepository usuarioRepository;

  public List<EventoEntity> findAllForAdmin() {
    return eventoRepository.findAll().stream()
        .sorted(Comparator.comparing(EventoEntity::getFechaEvento).reversed())
        .collect(Collectors.toList());
  }


  public List<EventoInscripcionDTO> findAllForInscripcion() {
    String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
    UsuarioEntity usuario = usuarioRepository.findByEmailIgnoreCase(userEmail)
        .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));

    // Asumimos que el primer socio es el principal para la inscripción
    SocioEntity socioPrincipal = usuario.getSocios().stream().findFirst()
        .orElseThrow(() -> new IllegalStateException("El usuario no tiene un socio asociado."));

    List<EventoEntity> eventos = eventoRepository.findAll();

    // Para cada evento, comprobamos si el socio principal está en la lista de participantes
    eventos.forEach(evento -> {
      boolean inscrito = evento.getParticipantes().stream()
          .anyMatch(participante -> participante.getUid().equals(socioPrincipal.getUid()));
      evento.setCurrentUserInscrito(inscrito);
    });

    // Mapear a DTO y ordenar por fecha
    return eventos.stream()
        .map(EventoMapper::toInscripcionDTO)
        .sorted(Comparator.comparing(EventoInscripcionDTO::getFechaEvento))
        .collect(Collectors.toList());
  }

  public EventoEntity save(EventoEntity evento) {
    // Aquí se podrían añadir validaciones antes de guardar
    return eventoRepository.save(evento);
  }

  public EventoEntity update(UUID id, EventoEntity eventoDetails) {
    EventoEntity eventoExistente =
        eventoRepository
            .findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Evento no encontrado con ID: " + id));

    // Actualizar los campos
    eventoExistente.setNombreEvento(eventoDetails.getNombreEvento());
    eventoExistente.setFechaEvento(eventoDetails.getFechaEvento());
    eventoExistente.setUbicacion(eventoDetails.getUbicacion());
    eventoExistente.setDescripcion(eventoDetails.getDescripcion());
    eventoExistente.setNumeroPlazas(eventoDetails.getNumeroPlazas());
    eventoExistente.setCosteTotalEstimado(eventoDetails.getCosteTotalEstimado());
    eventoExistente.setCosteTotalReal(eventoDetails.getCosteTotalReal());

    return eventoRepository.save(eventoExistente);
  }

  public void delete(UUID id) {
    eventoRepository.deleteById(id);
  }


  @Transactional
  public void inscribirSocio(UUID eventoId) {
    String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
    UsuarioEntity usuario = usuarioRepository.findByEmailIgnoreCase(userEmail)
        .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));
    SocioEntity socio = usuario.getSocios().stream().findFirst()
        .orElseThrow(() -> new IllegalStateException("Usuario sin socio principal."));

    EventoEntity evento = eventoRepository.findById(eventoId)
        .orElseThrow(() -> new EntityNotFoundException("Evento no encontrado"));

    evento.getParticipantes().add(socio);
    eventoRepository.save(evento);
  }

  @Transactional
  public void anularInscripcionSocio(UUID eventoId) {
    String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
    SocioEntity socio = usuarioRepository.findByEmailIgnoreCase(userEmail)
        .flatMap(u -> u.getSocios().stream().findFirst())
        .orElseThrow(() -> new IllegalStateException("Usuario sin socio principal."));

    EventoEntity evento = eventoRepository.findById(eventoId)
        .orElseThrow(() -> new EntityNotFoundException("Evento no encontrado"));

    evento.getParticipantes().removeIf(p -> p.getUid().equals(socio.getUid()));
    eventoRepository.save(evento);
  }
}