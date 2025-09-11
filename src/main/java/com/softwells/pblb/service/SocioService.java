package com.softwells.pblb.service;

import com.softwells.pblb.controller.dto.SocioStatsDto;
import com.softwells.pblb.model.CuotaEntity;
import com.softwells.pblb.model.RoleEntity;
import com.softwells.pblb.model.SocioEntity;
import com.softwells.pblb.model.UsuarioEntity;
import com.softwells.pblb.exception.EmailAlreadyExistsException;
import com.softwells.pblb.repository.CuotaRepository;
import com.softwells.pblb.controller.dto.RegisterRequest;
import com.softwells.pblb.repository.RoleRepository;
import com.softwells.pblb.repository.UsuarioRepository;
import com.softwells.pblb.repository.SocioRepository;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.ArrayList;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Set;
import java.util.Set;
import java.util.UUID;
import org.springframework.web.multipart.MultipartFile;

@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
public class SocioService {

  private final SocioRepository socioRepository;
  private final CuotaRepository cuotaRepository;
  private final PasswordEncoder passwordEncoder;
  private final UsuarioRepository usuarioRepository;
  private final RoleRepository roleRepository;

  public SocioEntity crear(SocioEntity socio) {
    if (socioRepository.existsByDni(socio.getDni())) {
      throw new IllegalArgumentException("Ya existe un socio con ese DNI");
    }
    return socioRepository.save(socio);
  }

  public SocioEntity registrarSocio(RegisterRequest request) {
    // 1. Verifica que el email no esté ya en uso para un Usuario.
    if (usuarioRepository.findByEmail(request.getEmail()).isPresent()) {
      throw new EmailAlreadyExistsException("El email ya está registrado.");
    }

    // 2. Crea el nuevo UsuarioEntity
    UsuarioEntity nuevoUsuario = new UsuarioEntity();
    nuevoUsuario.setEmail(request.getEmail());
    nuevoUsuario.setPassword(passwordEncoder.encode(request.getPassword()));
    nuevoUsuario.setActivo(true);

    // Asignamos el rol directamente al Usuario
    RoleEntity userRole = roleRepository.findByName("ROLE_USER")
        .orElseThrow(() -> new RuntimeException("Error: Rol ROLE_USER no encontrado."));
    nuevoUsuario.setRoles(Set.of(userRole));

    UsuarioEntity usuario = usuarioRepository.save(nuevoUsuario);

    // 3. Crea la nueva ficha de SocioEntity.
    SocioEntity nuevoSocio = new SocioEntity();
    nuevoSocio.setNombre(request.getNombre());
    nuevoSocio.setEmail(request.getEmail());
    nuevoSocio.setFechaAlta(LocalDate.now());
    nuevoSocio.setActivo(true);
    nuevoSocio.setAbonadoBetis(false);
    nuevoSocio.setAccionistaBetis(false);
    nuevoSocio.setExentoPago(false);
    nuevoSocio.setNumeroSocio(generarNumeroSocio());
    nuevoSocio.setUsuario(usuario);
    return socioRepository.save(nuevoSocio);
  }

  public SocioEntity actualizar(UUID id, SocioEntity socio) {
    SocioEntity existente = obtenerPorId(id);
    socio.setUid(existente.getUid());
    return socioRepository.save(socio);
  }

  public void eliminar(UUID id) {
    if (!socioRepository.existsById(id)) {
      throw new EntityNotFoundException("Socio no encontrado");
    }
    socioRepository.deleteById(id);
  }

  public SocioEntity obtenerPorId(UUID id) {
    SocioEntity socio = socioRepository.findById(id)
        .orElseThrow(() -> new EntityNotFoundException("Socio no encontrado"));

    // Lógica de autorización
    var authentication = SecurityContextHolder.getContext().getAuthentication();
    String currentUsername = authentication.getName();
    boolean isAdmin = authentication.getAuthorities().stream()
        .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

    if (!isAdmin && !socio.getEmail().equals(currentUsername)) {
      throw new AccessDeniedException("No tienes permiso para ver la información de este socio.");
    }
    return socio;
  }

  public List<SocioEntity> obtenerTodos() {
    return socioRepository.findAll();
  }

  public List<SocioEntity> obtenerSociosActivos() {
    return socioRepository.findByActivo(true);
  }

  @Transactional(readOnly = true)
  public List<CuotaEntity> obtenerCuotasDeSocio(UUID socioId) {
    return cuotaRepository.findBySocioUid(socioId);
  }

  // En tu clase SocioService
  public SocioStatsDto obtenerEstadisticas(LocalDate fechaDesde) {
    // Suponiendo que tienes un SocioRepository inyectado
    long totalSocios = socioRepository.count();
    long nuevosSocios = socioRepository.countByFechaAltaGreaterThanEqual(fechaDesde);
    return new SocioStatsDto(totalSocios, nuevosSocios);
  }

  private String generarNumeroSocio() {
    // Lógica para generar un número de socio único.
    // Puede ser un correlativo o un UUID.
    return "SOC-" + UUID.randomUUID().toString().substring(0, 8);
  }

  public void importarSocios(MultipartFile file) {
    try (InputStream inputStream = file.getInputStream()) {
      Workbook workbook = new XSSFWorkbook(inputStream);
      Sheet sheet = workbook.getSheetAt(0);
      List<SocioEntity> socios = new ArrayList<>();
      // Creamos un formateador que acepta múltiples patrones de fecha
      DateTimeFormatter dateFormatter = new DateTimeFormatterBuilder()
          .appendOptional(DateTimeFormatter.ofPattern("dd-MM-yyyy"))
          .appendOptional(DateTimeFormatter.ofPattern("dd-MM-yy"))
          .appendOptional(DateTimeFormatter.ofPattern("dd MM yyyy"))
          .appendOptional(DateTimeFormatter.ofPattern("dd - MM - yyyy"))
          .appendOptional(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
          .appendOptional(DateTimeFormatter.ofPattern("dd /MM /yyyy"))
          .appendOptional(DateTimeFormatter.ofPattern("dd.MM.yyyy"))
          .toFormatter();

      // Saltamos la primera fila (cabecera)
      Integer numSocio = 100;
      for (int i = 1; i <= sheet.getLastRowNum(); i++) {
        Row row = sheet.getRow(i);
        if (row == null) {
          continue;
        }

        SocioEntity socio = new SocioEntity();

        // Asignamos los valores de las celdas al objeto SocioEntity
        // Ojo: los índices de las celdas empiezan en 0
        socio.setNombre(getCellValueAsString(row.getCell(1)));
        socio.setDni(getCellValueAsString(row.getCell(2)));

        String fechaNacimientoStr = getCellValueAsString(row.getCell(3));
        if (fechaNacimientoStr != null && !fechaNacimientoStr.isEmpty()) {
          try {
            socio.setFechaNacimiento(LocalDate.parse(fechaNacimientoStr, dateFormatter));
          } catch (Exception e) {
            log.debug("Error formateo {}", fechaNacimientoStr);
          }
        }

        socio.setDireccion(getCellValueAsString(row.getCell(4)));
        socio.setPoblacion(getCellValueAsString(row.getCell(5)));
        socio.setProvincia(getCellValueAsString(row.getCell(6)));
        socio.setCodigoPostal(getCellValueAsString(row.getCell(7)));
        socio.setTelefono(getCellValueAsString(row.getCell(8)));
        socio.setEmail(getCellValueAsString(row.getCell(9)));

        // Extraemos el IBAN del campo de domiciliación
        String domiciliacion = getCellValueAsString(row.getCell(13));
        socio.setNumeroCuenta(domiciliacion);

        socio.setActivo(true); // Por defecto, los nuevos socios están activos

        socio.setNumeroSocio((numSocio++).toString());
        socio.setFechaAlta(LocalDate.now());

        socios.add(socio);
      }

      socioRepository.saveAll(socios);

    } catch (Exception e) {
      log.error("Error al procesar el fichero Excel: {}", e.getMessage());
      throw new RuntimeException("Error al procesar el fichero Excel: " + e.getMessage());
    }
  }

  private String getCellValueAsString(Cell cell) {
    if (cell == null) {
      return "";
    }
    return switch (cell.getCellType()) {
      case STRING -> cell.getStringCellValue();
      case NUMERIC -> String.valueOf((long) cell.getNumericCellValue());
      case BOOLEAN -> String.valueOf(cell.getBooleanCellValue());
      default -> "";
    };
  }

}
