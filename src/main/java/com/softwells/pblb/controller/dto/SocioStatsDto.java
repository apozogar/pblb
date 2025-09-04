package com.softwells.pblb.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SocioStatsDto {
  private long totalSocios;
  private long nuevosSocios;
}
