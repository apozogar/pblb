package com.softwells.pblb.controller;

import com.softwells.pblb.controller.dto.ApiResponse;
import com.softwells.pblb.controller.dto.SocioStatsDto;
import com.softwells.pblb.model.SocioEntity;
import com.softwells.pblb.model.CuotaEntity;
import com.softwells.pblb.service.SepaService;
import com.softwells.pblb.service.SocioService;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/socios")
@RequiredArgsConstructor
public class SocioController {

  private final SocioService socioService;
  private final SepaService sepaService;

  @PostMapping
  public ResponseEntity<ApiResponse<SocioEntity>> crear(@RequestBody SocioEntity socio) {
    return ResponseEntity.ok(new ApiResponse<>(true, "Socio creado exitosamente",
        socioService.crear(socio)));
  }

  @PutMapping("/{id}")
  public ResponseEntity<ApiResponse<SocioEntity>> actualizar(@PathVariable UUID id,
      @RequestBody SocioEntity socio) {
    return ResponseEntity.ok(new ApiResponse<>(true, "Socio actualizado exitosamente",
        socioService.actualizar(id, socio)));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<ApiResponse<Void>> eliminar(@PathVariable UUID id) {
    socioService.eliminar(id);
    return ResponseEntity.ok(new ApiResponse<>(true, "Socio eliminado exitosamente", null));
  }

  @GetMapping("/{id}")
  public ResponseEntity<ApiResponse<SocioEntity>> obtenerPorId(@PathVariable UUID id) {
    return ResponseEntity.ok(new ApiResponse<>(true, "Socio encontrado",
        socioService.obtenerPorId(id)));
  }

  @GetMapping
  public ResponseEntity<ApiResponse<List<SocioEntity>>> obtenerTodos() {
    return ResponseEntity.ok(new ApiResponse<>(true, "Lista de socios",
        socioService.obtenerTodos()));
  }

  @GetMapping("/activos")
  public ResponseEntity<ApiResponse<List<SocioEntity>>> obtenerSociosActivos() {
    return ResponseEntity.ok(new ApiResponse<>(true, "Lista de socios activos",
        socioService.obtenerSociosActivos()));
  }

  @GetMapping("/{id}/cuotas")
  public ResponseEntity<ApiResponse<List<CuotaEntity>>> obtenerCuotasDeSocio(@PathVariable UUID id) {
    return ResponseEntity.ok(new ApiResponse<>(true, "Cuotas del socio",
        socioService.obtenerCuotasDeSocio(id)));
  }

  @GetMapping("/estadisticas")
  public ResponseEntity<ApiResponse<SocioStatsDto>> obtenerEstadisticas() {
    LocalDate fechaDesde = LocalDate.now().minusMonths(1);
    SocioStatsDto estadisticas = socioService.obtenerEstadisticas(fechaDesde);
    return ResponseEntity.ok(
        new ApiResponse<>(true, "Estadísticas de socios recuperadas", estadisticas));
  }


  @PostMapping("/importar")
  public ResponseEntity<ApiResponse<String>> importarSociosDesdeExcel(
      @RequestParam("file") MultipartFile file) {
    if (file.isEmpty()) {
      return ResponseEntity.badRequest()
          .body(new ApiResponse<>(false, "El fichero está vacío", null));
    }

    socioService.importarSocios(file);

    return ResponseEntity.ok(new ApiResponse<>(true, "Socios importados correctamente", null));
  }

  @GetMapping(value = "/generar-sepa", produces = MediaType.APPLICATION_XML_VALUE)
  public ResponseEntity<byte[]> generarFicheroSepa() {
    try {
      LocalDateTime fechaDesde = LocalDateTime.now().minusMonths(1);
      String sepaFile = sepaService.generarFicheroSepa(fechaDesde);
      byte[] fileContent = sepaFile.getBytes(StandardCharsets.UTF_8);

      HttpHeaders headers = new HttpHeaders();
      headers.setContentType(MediaType.APPLICATION_XML);
      headers.setContentDisposition(ContentDisposition.attachment().filename("sepa.xml").build());
      headers.setContentLength(fileContent.length);

      return ResponseEntity.ok()
          .headers(headers)
          .body(fileContent);

    } catch (Exception e) {
      // Manejar otros errores generales
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body("Error inesperado".getBytes(StandardCharsets.UTF_8));
    }
  }

}
