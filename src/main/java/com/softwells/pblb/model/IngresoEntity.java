package com.softwells.pblb.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;
import lombok.Data;

@Entity
@Table(name = "ingresos")
@Data
public class IngresoEntity {

  @Id
  @GeneratedValue
  private UUID uid;

  @Column(nullable = false)
  private String concepto;

  @Column(nullable = false)
  private BigDecimal monto;

  @Column(nullable = false)
  private LocalDate fechaIngreso;

  @Column(nullable = false)
  private String tipoIngreso;

  @Column(length = 1000)
  private String observaciones;

  @ManyToOne
  @JoinColumn(name = "evento_uid")
  private EventoEntity evento;

}
