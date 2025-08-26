package com.softwells.pblb.repository;

import com.softwells.pblb.model.CuotaEntity;
import com.softwells.pblb.model.SocioEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Repository
public interface CuotaRepository extends JpaRepository<CuotaEntity, UUID> {
    List<CuotaEntity> findBySocio(SocioEntity socio);
    List<CuotaEntity> findByEstado(CuotaEntity.EstadoCuota estado);
    List<CuotaEntity> findByFechaVencimientoBefore(LocalDate fecha);
    List<CuotaEntity> findByPeriodo(CuotaEntity.PeriodoCuota periodo);
}
