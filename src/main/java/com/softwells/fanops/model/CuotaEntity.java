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

  @Column(nullable = false)
  private Double importe;

  @Column(nullable = false)
  private LocalDate fechaEmision;

  private LocalDate fechaPago;

  @Column(nullable = false)
  private EstadoCuota estado; // Ej: PAGADA, PENDIENTE, VENCIDA

  @Column(nullable = false)
  private Integer mes; // 1-12

  @Column(nullable = false)
  private Integer anio;

  @JsonIgnore
  @ManyToOne
  @JoinColumn(name = "socio_uid", nullable = false)
  private SocioEntity socio;
}
