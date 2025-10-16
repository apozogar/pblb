package com.softwells.fanops.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "iban_bic_mapping")
public class IbanBicMappingEntity {

  @Id
  @Column(length = 4, nullable = false)
  private String bankCode; // Los 4 dígitos del banco en el IBAN (ej: "2100")

  @Column(nullable = false)
  private String bic; // El código BIC correspondiente (ej: "CAIXESBBXXX")
}