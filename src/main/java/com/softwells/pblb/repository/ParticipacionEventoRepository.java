package com.softwells.pblb.repository;

import com.softwells.pblb.model.ParticipacionEventoEntity;
import com.softwells.pblb.model.EventoEntity;
import com.softwells.pblb.model.SocioEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ParticipacionEventoRepository extends JpaRepository<ParticipacionEventoEntity, UUID> {
    List<ParticipacionEventoEntity> findByEvento(EventoEntity evento);
    List<ParticipacionEventoEntity> findBySocio(SocioEntity socio);
    Optional<ParticipacionEventoEntity> findByEventoAndSocio(EventoEntity evento, SocioEntity socio);
}
