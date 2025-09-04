package com.softwells.pblb.model;

import com.softwells.pblb.enums.EstadoCuota;
import com.softwells.pblb.enums.PeriodoCuota;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.math.BigDecimal;
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
  private BigDecimal monto;

  @Column(nullable = false)
  private LocalDate fechaVencimiento;

  private LocalDate fechaPago;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private EstadoCuota estado;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private PeriodoCuota periodo;

  @ManyToOne
  @JoinColumn(name = "socio_uid", nullable = false)
  private SocioEntity socio;

}
