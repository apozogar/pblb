package com.softwells.pblb.repository;

import com.softwells.pblb.model.EventoEntity;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventoRepository extends JpaRepository<EventoEntity, UUID> {

}