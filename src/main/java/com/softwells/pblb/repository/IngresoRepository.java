package com.softwells.pblb.repository;

import com.softwells.pblb.model.IngresoEntity;
import com.softwells.pblb.model.EventoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Repository
public interface IngresoRepository extends JpaRepository<IngresoEntity, UUID> {
    List<IngresoEntity> findByEvento(EventoEntity evento);
    List<IngresoEntity> findByFechaIngresoBetween(LocalDate fechaInicio, LocalDate fechaFin);
    List<IngresoEntity> findByTipoIngreso(String tipoIngreso);
}
