package com.softwells.pblb.mapper;

import com.softwells.pblb.controller.dto.EventoInscripcionDTO;
import com.softwells.pblb.model.EventoEntity;

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