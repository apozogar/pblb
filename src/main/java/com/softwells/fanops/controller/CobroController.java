package com.softwells.fanops.controller;

import com.softwells.fanops.controller.dto.ApiResponse;
import com.softwells.fanops.service.CobroService;
import java.io.ByteArrayInputStream;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/cobros")
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('ROLE_ADMIN')")
public class CobroController {

  private final CobroService cobroService;

  @GetMapping(value = "/generar-remesa", produces = "application/xml")
  public ResponseEntity<InputStreamResource> generarRemesaMensual(@RequestParam String concepto,
      @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaCobro) {
    String xmlData = cobroService.generarRemesaMensual(concepto, fechaCobro);

    ByteArrayInputStream in = new ByteArrayInputStream(xmlData.getBytes());

    HttpHeaders headers = new HttpHeaders();
    headers.add("Content-Disposition", "attachment; filename=remesa.xml");

    return ResponseEntity.ok().headers(headers).contentType(MediaType.parseMediaType(
            "application/xml"))
        .body(new InputStreamResource(in));
  }

  @GetMapping(value = "/generar-remesa-excel", produces = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
  public ResponseEntity<InputStreamResource> generarRemesaExcel(@RequestParam String concepto,
      @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaCobro) {
    ByteArrayInputStream in = cobroService.generarRemesaExcel(concepto, fechaCobro);

    HttpHeaders headers = new HttpHeaders();
    headers.add("Content-Disposition", "attachment; filename=remesa.xlsx");

    return ResponseEntity.ok().headers(headers).contentType(MediaType.parseMediaType(
            "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
        .body(new InputStreamResource(in));
  }

  @PostMapping("/procesar-retorno")
  public ResponseEntity<ApiResponse<String>> procesarRetorno(
      @RequestParam("file") MultipartFile file) {
    String resultado = cobroService.procesarFicheroRetorno(file);
    return ResponseEntity.ok(new ApiResponse<>(true, resultado, null));
  }

  @PostMapping("/confirmar-pagos")
  public ResponseEntity<ApiResponse<String>> confirmarPagos() {
    String resultado = cobroService.confirmarPagosPendientes();
    return ResponseEntity.ok(new ApiResponse<>(true, resultado, null));
  }
}