package com.softwells.pblb.model;

import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.UUID;
import lombok.Data;

@Embeddable
@Data
public class ParticipacionEventoId implements Serializable {

  private UUID socioUid;
  private UUID eventoUid;

  // Constructor, equals, hashCode
  public ParticipacionEventoId() {
  }

  public ParticipacionEventoId(UUID socioUid, UUID eventoUid) {
    this.socioUid = socioUid;
    this.eventoUid = eventoUid;
  }

}
