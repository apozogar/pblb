package com.softwells.fanops.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.softwells.fanops.enums.EstadoCuota;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.UUID;
import lombok.Data;

@Entity
@Table(name = "cuotas")
@Data
public class CuotaEntity {

  @Id
  @GeneratedValue
  private UUID uid;

  private String concepto;

  private Double importe;

  private LocalDate fechaEmision;

  private LocalDate fechaPago;

  @Enumerated(EnumType.STRING)
  private EstadoCuota estado; // Ej: PAGADA, PENDIENTE, VENCIDA

  private Integer mes; // 1-12

  private Integer anio;

  @JsonIgnore
  @ManyToOne
  @JoinColumn(name = "socio_uid", nullable = false)
  private SocioEntity socio;
}
