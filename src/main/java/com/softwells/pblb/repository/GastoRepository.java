package com.softwells.pblb.repository;

import com.softwells.pblb.model.GastoEntity;
import com.softwells.pblb.model.EventoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Repository
public interface GastoRepository extends JpaRepository<GastoEntity, UUID> {
    List<GastoEntity> findByEvento(EventoEntity evento);
    List<GastoEntity> findByFechaGastoBetween(LocalDate fechaInicio, LocalDate fechaFin);
    List<GastoEntity> findByTipoGasto(String tipoGasto);
    List<GastoEntity> findByProveedor(String proveedor);
}
