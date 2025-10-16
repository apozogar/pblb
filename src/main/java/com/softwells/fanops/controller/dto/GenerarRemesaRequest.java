package com.softwells.fanops.controller.dto;

import java.time.LocalDate;
import lombok.Data;

@Data
public class GenerarRemesaRequest {

  private String concepto;
  private LocalDate fechaCobro;

}