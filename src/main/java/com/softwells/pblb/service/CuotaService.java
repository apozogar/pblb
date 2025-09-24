package com.softwells.pblb.service;

import com.softwells.pblb.enums.EstadoCuota;
import com.softwells.pblb.model.CuotaEntity;
import com.softwells.pblb.model.PenaEntity;
import com.softwells.pblb.model.SocioEntity;
import com.softwells.pblb.repository.CuotaRepository;
import com.softwells.pblb.repository.PenaRepository;
import com.softwells.pblb.repository.SocioRepository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CuotaService {

  private final SocioRepository socioRepository;
  private final PenaRepository penaRepository;
  private final CuotaRepository cuotaRepository;
  private final SepaService sepaService;

  @Transactional
  public String generarCuotasYRemesaMensual() {
    List<SocioEntity> sociosActivos = socioRepository.findByActivo(true);
    PenaEntity pena = penaRepository.findById(1L)
        .orElseThrow(() -> new IllegalStateException("Datos de la peña no encontrados."));
    List<CuotaEntity> nuevasCuotas = new ArrayList<>();
    LocalDate hoy = LocalDate.now();

    for (SocioEntity socio : sociosActivos) {
      // Lógica para determinar el importe de la cuota
      int edad =
          (socio.getFechaNacimiento() != null) ? Period.between(socio.getFechaNacimiento(), hoy)
              .getYears() : 30;
      double importe =
          (edad > pena.getEdadMayoria()) ? pena.getCuotaAdulto() : pena.getCuotaMenor();

      CuotaEntity cuota = new CuotaEntity();
      cuota.setSocio(socio);
      cuota.setImporte(importe);
      cuota.setMes(hoy.getMonthValue());
      cuota.setAnio(hoy.getYear());
      cuota.setEstado(EstadoCuota.PENDIENTE); // Estado inicial
      cuota.setFechaEmision(hoy);
      nuevasCuotas.add(cuota);
    }

    cuotaRepository.saveAll(nuevasCuotas);

    // Ahora que las cuotas están generadas, creamos el fichero SEPA
    sepaService.generarFicheroSepa(LocalDateTime.now());

    return "Se generaron " + nuevasCuotas.size()
        + " cuotas y se creó la remesa SEPA correctamente.";
  }
}