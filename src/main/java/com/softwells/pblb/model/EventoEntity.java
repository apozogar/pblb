package com.softwells.pblb.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import lombok.Data;

@Entity
@Table(name = "eventos")
@Data
public class EventoEntity {

  @Id
  @GeneratedValue
  private UUID uid;

  @Column(nullable = false)
  private String nombreEvento;

  @Column(nullable = false)
  private LocalDate fechaEvento;

  private String ubicacion;

  @Column(length = 1000)
  private String descripcion;

  private BigDecimal costeTotalEstimado;
  private BigDecimal costeTotalReal;

  @OneToMany(mappedBy = "evento", cascade = CascadeType.ALL)
  private Set<ParticipacionEventoEntity> participaciones = new HashSet<>();

  @OneToMany(mappedBy = "evento", cascade = CascadeType.ALL)
  private Set<GastoEntity> gastos = new HashSet<>();

  @OneToMany(mappedBy = "evento", cascade = CascadeType.ALL)
  private Set<IngresoEntity> ingresos = new HashSet<>();

}
