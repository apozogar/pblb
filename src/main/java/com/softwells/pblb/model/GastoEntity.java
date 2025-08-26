package com.softwells.pblb.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;
import lombok.Data;

@Entity
@Table(name = "gastos")
@Data
public class GastoEntity {

  @Id
  @GeneratedValue
  private UUID uid;

  @Column(nullable = false)
  private String concepto;

  @Column(nullable = false)
  private BigDecimal monto;

  @Column(nullable = false)
  private LocalDate fechaGasto;

  @Column(nullable = false)
  private String tipoGasto;

  private String proveedor;

  @Column(length = 1000)
  private String observaciones;

  @ManyToOne
  @JoinColumn(name = "evento_uid")
  private EventoEntity evento;

}
