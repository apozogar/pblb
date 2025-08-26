package com.softwells.pblb.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;
import lombok.Data;

@Entity
@Table(name = "participaciones_eventos")
@Data
public class ParticipacionEventoEntity {

  @EmbeddedId
  private ParticipacionEventoId id;

  @ManyToOne
  @MapsId("socioUid")
  @JoinColumn(name = "socio_uid")
  private SocioEntity socio;

  @ManyToOne
  @MapsId("eventoUid")
  @JoinColumn(name = "evento_uid")
  private EventoEntity evento;

  @Column(nullable = false)
  private LocalDate fechaInscripcion;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private EstadoParticipacion estadoParticipacion;

  private BigDecimal montoPagadoEvento;

  public enum EstadoParticipacion {
    CONFIRMADO, PENDIENTE, CANCELADO
  }

}
