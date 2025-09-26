package com.softwells.fanops.repository;

import com.softwells.fanops.model.EventoEntity;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventoRepository extends JpaRepository<EventoEntity, UUID> {

}