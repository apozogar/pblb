package com.softwells.fanops.service;

import com.softwells.fanops.controller.dto.RemesaExcelDto;
import com.softwells.fanops.controller.dto.RemesaResponse;
import com.softwells.fanops.enums.EstadoCuota;
import com.softwells.fanops.model.CuotaEntity;
import com.softwells.fanops.model.PenaEntity;
import com.softwells.fanops.model.SocioEntity;
import com.softwells.fanops.repository.CuotaRepository;
import com.softwells.fanops.repository.PenaRepository;
import jakarta.persistence.EntityNotFoundException;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
public class CobroService {

  private final CuotaRepository cuotaRepository;
  private final PenaRepository penaRepository;
  private final CuotaService cuotaService;
  private final SepaService sepaService;
  private final BicLookupService bicLookupService;

  //  public String generarRemesaMensual(String concepto, LocalDate fechaCobro) {
//    List<CuotaEntity> cuotasPendientes = cuotaRepository.findByEstado(EstadoCuota.PENDIENTE);
//    return sepaService.generarFicheroSepa(cuotasPendientes, fechaCobro.atStartOfDay(), concepto);
//  }
  public ByteArrayInputStream generarRemesaExcel(String concepto, LocalDate fechaCobro) {
    // 1. Obtener la información de la peña (para el BIC, etc.)
    PenaEntity pena = penaRepository.findById(1L)
        .orElseThrow(
            () -> new EntityNotFoundException("No se encontró la información de la peña."));

    // 2. Obtener las cuotas pendientes de pago
    List<CuotaEntity> cuotasPendientes = cuotaRepository.findByEstado(EstadoCuota.PENDIENTE);

    if (cuotasPendientes.isEmpty()) {
      cuotasPendientes = cuotaService.generarCuotas();
    }

    if (cuotasPendientes.isEmpty()) {
      log.info("No se encontraron cuotas pendientes para generar el Excel de remesa.");
      return new ByteArrayInputStream(new byte[0]);
    }

    // 3. Mapear a DTOs
    List<RemesaExcelDto> dtos = cuotasPendientes.stream()
        .map(cuota -> {
          SocioEntity socio = cuota.getSocio();
          String iban = socio.getNumeroCuenta();
          // Buscamos el BIC en nuestra nueva tabla. Si no lo encontramos, dejamos "NO ENCONTRADO".
          String bic = bicLookupService.getBicForIban(iban).orElse("NO ENCONTRADO");

          return RemesaExcelDto.builder()
              .referencia(socio.getNombre())
              .nombre(socio.getNombre())
              .refMandato(socio.getNombre())
              .iban(iban)
              .bic(bic) // Usamos el BIC que hemos encontrado
              .pais(
                  socio.getNumeroCuenta() != null ? socio.getNumeroCuenta().substring(0, 2) : "ES")
              .residente("S") // Asumimos que son residentes
              .refAdeudo("Cuota 2025")
              .fechaFirmaMandato(DateTimeFormatter.ofPattern("dd/MM/yyyy")
                  .format(LocalDate.now()))
              .importe(cuota.getImporte())
              .concepto(concepto)
              .tipoAdeudo("RCUR") // Asumimos Recurrente, podrías tenerlo en la cuota
              .fechaCobro(DateTimeFormatter.ofPattern("dd/MM/yyyy")
                  .format(fechaCobro)) // O una fecha de cobro específica
              .build();
        })
        .toList();

    // 4. Crear el fichero Excel en memoria
    try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
      Sheet sheet = workbook.createSheet("Remesa");

      // Cabecera
      String[] headers = {"REFERENCIA", "NOMBRE", "REFMANDATO", "IBAN", "BIC", "PAIS", "RESIDENTE",
          "FECHAFIRMAMANDATO", "REFADEUDO", "IMPORTE", "CONCEPTO", "TIPOADEUDO", "FECHACOBRO"};
      Row headerRow = sheet.createRow(0);
      for (int i = 0; i < headers.length; i++) {
        Cell cell = headerRow.createCell(i);
        cell.setCellValue(headers[i]);
      }

      // Datos
      int rowIdx = 1;
      for (RemesaExcelDto dto : dtos) {
        Row row = sheet.createRow(rowIdx++);
        row.createCell(0).setCellValue(dto.getReferencia());
        row.createCell(1).setCellValue(dto.getNombre());
        row.createCell(2).setCellValue(dto.getRefMandato());
        row.createCell(3).setCellValue(dto.getIban());
        row.createCell(4).setCellValue(dto.getBic());
        row.createCell(5).setCellValue(dto.getPais());
        row.createCell(6).setCellValue(dto.getResidente());
        row.createCell(7).setCellValue(
            dto.getFechaFirmaMandato() != null ? dto.getFechaFirmaMandato().toString() : "");
        row.createCell(8).setCellValue(dto.getRefAdeudo());
        row.createCell(9).setCellValue(dto.getImporte() != null ? dto.getImporte() : 0.0);
        row.createCell(10).setCellValue(dto.getConcepto());
        row.createCell(11).setCellValue(dto.getTipoAdeudo());
        row.createCell(12)
            .setCellValue(dto.getFechaCobro() != null ? dto.getFechaCobro().toString() : "");
      }

      // Autoajustar el tamaño de las columnas
      for (int i = 0; i < headers.length; i++) {
        sheet.autoSizeColumn(i);
      }

      workbook.write(out);
      return new ByteArrayInputStream(out.toByteArray());

    } catch (IOException e) {
      log.error("Error al generar el fichero Excel de remesa", e);
      throw new RuntimeException("Error al generar el fichero Excel: " + e.getMessage());
    }
  }

//  public String procesarFicheroRetorno(MultipartFile file) {
//    if (file.isEmpty()) {
//      throw new IllegalArgumentException("El fichero de retorno está vacío.");
//    }
//    try {
//      return retornoService.procesarFichero(file.getInputStream());
//    } catch (IOException e) {
//      log.error("Error al leer el fichero de retorno", e);
//      throw new RuntimeException("Error al leer el fichero de retorno: " + e.getMessage());
//    }
//  }
//
//  public String confirmarPagosPendientes() {
//    List<CuotaEntity> cuotasPendientes = cuotaRepository.findByEstado(EstadoCuota.PENDIENTE);
//
//    if (cuotasPendientes.isEmpty()) {
//      return "No hay cuotas pendientes de pago para confirmar.";
//    }
//
//    for (CuotaEntity cuota : cuotasPendientes) {
//      cuota.setEstado(EstadoCuota.PAGADA);
//      cuota.setFechaPago(LocalDate.now());
//    }
//
//    cuotaRepository.saveAll(cuotasPendientes);
//
//    log.info("Confirmados {} pagos. Las cuotas pendientes han sido marcadas como PAGADAS.", cuotasPendientes.size());
//    return String.format("%d cuotas han sido marcadas como pagadas correctamente.", cuotasPendientes.size());
//  }
}