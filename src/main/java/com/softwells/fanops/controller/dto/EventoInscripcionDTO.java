package com.softwells.fanops.controller.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDate;
import java.util.UUID;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class EventoInscripcionDTO {

  private UUID uid;
  private String nombreEvento;
  private LocalDate fechaEvento;
  private String ubicacion;

  @JsonProperty("isCurrentUserInscrito")
  private boolean isCurrentUserInscrito;
}