package com.softwells.fanops.controller.dto;

import java.time.LocalDate;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RemesaExcelDto {

  private String referencia; // numeroSocio
  private String nombre;
  private String refMandato;
  private String iban;
  private String bic;
  private String pais;
  private String residente;
  private String fechaFirmaMandato;
  private String refAdeudo; // uid de la cuota
  private Double importe;
  private String concepto;
  private String tipoAdeudo; // RCUR, FRST...
  private String fechaCobro;

}