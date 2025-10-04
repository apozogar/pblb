package com.softwells.fanops.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RemesaResponse {
  private String mensaje;
  private String xmlContent;
}