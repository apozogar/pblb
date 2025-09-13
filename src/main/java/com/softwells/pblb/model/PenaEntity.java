package com.softwells.pblb.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "pena")
public class PenaEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  private String nombre;
  private String iniciadorId;
  private String direccion1;
  private String direccion2;
  private String cuentaIban;
  private String cuentaBic;
  private Double cuotaAdulto;
  private Double cuotaMenor;
  private Integer edadMayoria;
  private Integer edadJubilacion;
}