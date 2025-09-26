package com.softwells.fanops.mapper;

import com.softwells.fanops.controller.dto.EventoInscripcionDTO;
import com.softwells.fanops.model.EventoEntity;

public class EventoMapper {

  public static EventoInscripcionDTO toInscripcionDTO(EventoEntity evento) {
    return EventoInscripcionDTO.builder()
        .uid(evento.getUid())
        .nombreEvento(evento.getNombreEvento())
        .fechaEvento(evento.getFechaEvento())
        .ubicacion(evento.getUbicacion())
        .isCurrentUserInscrito(evento.isCurrentUserInscrito())
        .build();
  }
}