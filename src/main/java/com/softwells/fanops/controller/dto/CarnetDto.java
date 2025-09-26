package com.softwells.fanops.controller.dto;

import com.softwells.fanops.model.PenaEntity;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CarnetDto {
  private PenaEntity penaInfo;
  private List<SocioDto> socios;
}