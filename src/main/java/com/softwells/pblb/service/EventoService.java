package com.softwells.pblb.service;

import com.softwells.pblb.model.EventoEntity;
import com.softwells.pblb.repository.EventoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityNotFoundException;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class EventoService {
    private final EventoRepository eventoRepository;

    @Autowired
    public EventoService(EventoRepository eventoRepository) {
        this.eventoRepository = eventoRepository;
    }

    public EventoEntity crear(EventoEntity evento) {
        return eventoRepository.save(evento);
    }

    public EventoEntity actualizar(UUID id, EventoEntity evento) {
        EventoEntity existente = obtenerPorId(id);
        evento.setUid(existente.getUid());
        return eventoRepository.save(evento);
    }

    public void eliminar(UUID id) {
        if (!eventoRepository.existsById(id)) {
            throw new EntityNotFoundException("Evento no encontrado");
        }
        eventoRepository.deleteById(id);
    }

    public EventoEntity obtenerPorId(UUID id) {
        return eventoRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Evento no encontrado"));
    }

    public List<EventoEntity> obtenerTodos() {
        return eventoRepository.findAll();
    }

    public List<EventoEntity> obtenerEventosFuturos() {
        return eventoRepository.findByFechaEventoAfter(LocalDate.now());
    }
}
