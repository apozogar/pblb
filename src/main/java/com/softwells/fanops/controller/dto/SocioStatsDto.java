package com.softwells.fanops.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SocioStatsDto {
  private long totalSocios;
  private long nuevosSocios;
  private long totalSociosJovenes;
  private long edadMayoria;
  private long totalSociosJubilados;
  private int edadJubilacion;
  private long totalImpagados;
}
