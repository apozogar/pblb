package com.softwells.pblb.repository;

import com.softwells.pblb.model.EventoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Repository
public interface EventoRepository extends JpaRepository<EventoEntity, UUID> {
    List<EventoEntity> findByFechaEventoAfter(LocalDate fecha);
    List<EventoEntity> findByFechaEventoBetween(LocalDate fechaInicio, LocalDate fechaFin);
    List<EventoEntity> findByNombreEventoContainingIgnoreCase(String nombre);
}
