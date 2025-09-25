package com.softwells.pblb.service;

import com.softwells.pblb.model.EventoEntity;
import com.softwells.pblb.repository.EventoRepository;
import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class EventoService {

  private final EventoRepository eventoRepository;

  public List<EventoEntity> findAll() {
    return eventoRepository.findAll();
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
    // ... actualizar otros campos según sea necesario

    return eventoRepository.save(eventoExistente);
  }

  public void delete(UUID id) {
    eventoRepository.deleteById(id);
  }
}