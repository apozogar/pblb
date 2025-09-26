package com.softwells.fanops.controller.dto;

import com.softwells.fanops.model.CuotaEntity;
import com.softwells.fanops.model.SocioEntity;
import java.util.List;
import java.util.UUID;
import lombok.Data;

@Data
public class SocioDto {

  private UUID uid;
  private Integer numeroSocio;
  private String nombre;
  private String dni;
  private String email;
  private String telefono;
  private List<CuotaEntity> cuotas;

  public static SocioDto fromEntity(SocioEntity entity) {
    SocioDto dto = new SocioDto();
    dto.setUid(entity.getUid());
    dto.setNumeroSocio(entity.getNumeroSocio());
    dto.setNombre(entity.getNombre());
    dto.setDni(entity.getDni());
    dto.setEmail(entity.getEmail());
    dto.setTelefono(entity.getTelefono());
    dto.setCuotas(entity.getCuotas().stream().toList()); // Asumiendo que la relación está cargada
    return dto;
  }
}