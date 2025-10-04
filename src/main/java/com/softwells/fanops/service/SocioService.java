package com.softwells.fanops.service;

import com.softwells.fanops.controller.dto.CarnetDto;
import com.softwells.fanops.controller.dto.RegisterRequest;
import com.softwells.fanops.controller.dto.SocioDto;
import com.softwells.fanops.controller.dto.SocioStatsDto;
import com.softwells.fanops.enums.EstadoCuota;
import com.softwells.fanops.exception.EmailAlreadyExistsException;
import com.softwells.fanops.model.CuotaEntity;
import com.softwells.fanops.model.PenaEntity;
import com.softwells.fanops.model.RoleEntity;
import com.softwells.fanops.model.SocioEntity;
import com.softwells.fanops.model.UsuarioEntity;
import com.softwells.fanops.repository.CuotaRepository;
import com.softwells.fanops.repository.PenaRepository;
import com.softwells.fanops.repository.RoleRepository;
import com.softwells.fanops.repository.SocioRepository;
import com.softwells.fanops.repository.UsuarioRepository;
import jakarta.persistence.EntityNotFoundException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.commons.text.WordUtils;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
public class SocioService {

  // Constantes para los índices de las columnas del Excel
  private static final int COL_NOMBRE_COMPLETO = 0;
  private static final int COL_DNI = 1;
  private static final int COL_FECHA_NACIMIENTO = 2;
  private static final int COL_DIRECCION = 3;
  private static final int COL_POBLACION = 4;
  private static final int COL_PROVINCIA = 5;
  private static final int COL_CODIGO_POSTAL = 6;
  private static final int COL_TELEFONO = 7;
  private static final int COL_EMAIL = 8;
  private static final int COL_ABONADO_BETIS = 9;
  private static final int COL_ACCIONISTA_BETIS = 10;
  private static final int COL_DOMICILIACION = 12;

  private final SocioRepository socioRepository;
  private final CuotaRepository cuotaRepository;
  private final PasswordEncoder passwordEncoder;
  private final UsuarioRepository usuarioRepository;
  private final RoleRepository roleRepository;
  private final PenaRepository penaRepository;

  public SocioEntity crear(SocioEntity socio) {
    if (socioRepository.existsByDni(socio.getDni())) {
      throw new IllegalArgumentException("Ya existe un socio con ese DNI");
    }
    return socioRepository.save(socio);
  }

  public SocioEntity registrarSocio(RegisterRequest request) {
    // 1. Verifica que el email no esté ya en uso para un Usuario.
    if (usuarioRepository.findByEmailIgnoreCase(request.getEmail()).isPresent()) {
      throw new EmailAlreadyExistsException("El email ya está registrado.");
    }

    // 2. Crea el nuevo UsuarioEntity
    UsuarioEntity nuevoUsuario = new UsuarioEntity();
    nuevoUsuario.setEmail(request.getEmail());
    nuevoUsuario.setPassword(passwordEncoder.encode(request.getPassword()));
    nuevoUsuario.setActivo(true);

    // Asignamos el rol directamente al Usuario
    RoleEntity userRole = roleRepository.findByName("ROLE_USER")
        .orElseGet(() -> roleRepository.save(new RoleEntity("ROLE_USER")));
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

  @Transactional
  public SocioEntity actualizar(UUID id, SocioEntity socioData) {
    SocioEntity existente = obtenerPorId(id);

    // Actualizamos los datos del socio
    existente.setNumeroSocio(socioData.getNumeroSocio());
    existente.setNombre(socioData.getNombre());
    existente.setDni(socioData.getDni());
    existente.setFechaNacimiento(socioData.getFechaNacimiento());
    existente.setEmail(socioData.getEmail());
    existente.setTelefono(socioData.getTelefono());
    existente.setDireccion(socioData.getDireccion());
    existente.setPoblacion(socioData.getPoblacion());
    existente.setProvincia(socioData.getProvincia());
    existente.setCodigoPostal(socioData.getCodigoPostal());
    existente.setActivo(socioData.isActivo());
    existente.setAbonadoBetis(socioData.isAbonadoBetis());
    existente.setAccionistaBetis(socioData.isAccionistaBetis());
    existente.setExentoPago(socioData.isExentoPago());
    existente.setNumeroCuenta(socioData.getNumeroCuenta());
    existente.setFechaAlta(socioData.getFechaAlta());
    existente.setObservaciones(socioData.getObservaciones());

    // Actualizamos los roles del usuario asociado
    existente.getUsuario().setRoles(socioData.getUsuario().getRoles());

    return socioRepository.save(existente);
  }


  @Transactional
  public SocioEntity actualizarMiSocio(UUID id, SocioEntity socioData) {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    assert authentication != null;
    String userEmail = authentication.getName();

    SocioEntity existente = socioRepository.findById(id)
        .orElseThrow(() -> new EntityNotFoundException("Socio no encontrado con ID: " + id));

    // Verificación de seguridad: el socio debe pertenecer al usuario autenticado
    if (!existente.getUsuario().getEmail().equals(userEmail)) {
      throw new AccessDeniedException("No tienes permiso para modificar este socio.");
    }

    // Actualizamos solo los campos permitidos
    existente.setFechaNacimiento(socioData.getFechaNacimiento());
    existente.setDni(socioData.getDni());
    existente.setDireccion(socioData.getDireccion());
    existente.setPoblacion(socioData.getPoblacion());
    existente.setProvincia(socioData.getProvincia());
    existente.setCodigoPostal(socioData.getCodigoPostal());
    existente.setTelefono(socioData.getTelefono());
    existente.setNumeroCuenta(socioData.getNumeroCuenta());

    return socioRepository.save(existente);
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
    assert authentication != null;
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

    // Obtenemos la configuración de la peña para la edad de mayoría
    PenaEntity pena = penaRepository.findById(1L)
        .orElseThrow(
            () -> new IllegalStateException("No se encontraron los datos de la peña con ID 1"));

    LocalDate fechaCorteJovenes = LocalDate.now().minusYears(pena.getEdadMayoria());
    long totalSociosJovenes = socioRepository.countByFechaNacimientoAfter(fechaCorteJovenes);

    // Calculamos la fecha de corte para ser jubilado
    LocalDate fechaCorteJubilados = LocalDate.now().minusYears(pena.getEdadJubilacion());
    long totalSociosJubilados = socioRepository.countByFechaNacimientoBeforeOrFechaNacimientoEquals(
        fechaCorteJubilados, fechaCorteJubilados);

    List<EstadoCuota> estadosImpagados = List.of(EstadoCuota.RECHAZADA, EstadoCuota.VENCIDA);
    int totalImpagados = cuotaRepository.countDistinctSociosByEstadoIn(estadosImpagados);

    return new SocioStatsDto(totalSocios, nuevosSocios, totalSociosJovenes, pena.getEdadMayoria(),
        totalSociosJubilados, pena.getEdadJubilacion(), totalImpagados);
  }

  private Integer generarNumeroSocio() {
    // Busca el número de socio máximo actual y le suma 1.
    // Si no hay socios, empieza en 1.
    return socioRepository.findMaxNumeroSocio().orElse(0) + 1;
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

      // Obtenemos el rol de usuario una sola vez para reutilizarlo
      RoleEntity userRole = roleRepository.findByName("ROLE_USER")
          .orElseThrow(() -> new RuntimeException("Error: Rol ROLE_USER no encontrado."));

      // Obtenemos el número de socio máximo actual para empezar a incrementar desde ahí
      Integer numSocio = socioRepository.findMaxNumeroSocio().orElse(0) + 1;
      // Saltamos la primera fila (cabecera)
      for (int i = 1; i <= sheet.getLastRowNum(); i++) {
        Row row = sheet.getRow(i);
        if (row == null) {
          continue;
        }

        // Verificamos si la fila está vacía comprobando la columna del nombre.
        String nombreCompleto = getCellValueAsString(row.getCell(COL_NOMBRE_COMPLETO));
        if (nombreCompleto == null || nombreCompleto.isBlank()) {
          log.warn("Saltando fila {} porque el nombre está vacío.", row.getRowNum() + 1);
          continue; // Si el nombre está vacío, ignoramos la fila completa.
        }

        SocioEntity socio = new SocioEntity();
        String email = getCellValueAsString(row.getCell(COL_EMAIL));

        // --- Lógica para crear o encontrar el Usuario ---
        UsuarioEntity usuario = usuarioRepository.findByEmailIgnoreCase(email)
            // Si el email está vacío, se creará un usuario con email nulo, lo que dará error.
            .orElseGet(() -> {
              UsuarioEntity nuevoUsuario = new UsuarioEntity();
              nuevoUsuario.setEmail(email);
              // Asignamos una contraseña temporal. El usuario deberá usar "olvidé mi contraseña".
              nuevoUsuario.setPassword(passwordEncoder.encode(UUID.randomUUID().toString()));
              nuevoUsuario.setActivo(true);
              nuevoUsuario.setRoles(Set.of(userRole));
              return usuarioRepository.save(nuevoUsuario);
            });
        // --- Fin de la lógica de Usuario ---

        // Asignamos los valores de las celdas al objeto SocioEntity usando los índices 0-based
        socio.setNombre(WordUtils.capitalizeFully(nombreCompleto));
        socio.setDni(getCellValueAsString(row.getCell(COL_DNI)));
        socio.setEmail(email);

        String fechaNacimientoStr = getCellValueAsString(row.getCell(COL_FECHA_NACIMIENTO));
        if (fechaNacimientoStr != null && !fechaNacimientoStr.isEmpty()) {
          try {
            socio.setFechaNacimiento(LocalDate.parse(fechaNacimientoStr, dateFormatter));
          } catch (Exception e) {
            log.warn("Error al formatear la fecha '{}' para el socio '{}'", fechaNacimientoStr,
                socio.getNombre());
          }
        }

        socio.setDireccion(getCellValueAsString(row.getCell(COL_DIRECCION)));
        socio.setPoblacion(getCellValueAsString(row.getCell(COL_POBLACION)));
        socio.setProvincia(getCellValueAsString(row.getCell(COL_PROVINCIA)));
        socio.setCodigoPostal(getCellValueAsString(row.getCell(COL_CODIGO_POSTAL)));
        socio.setTelefono(getCellValueAsString(row.getCell(COL_TELEFONO)));

        // Extraemos el IBAN del campo de domiciliación
        String domiciliacion = getCellValueAsString(row.getCell(COL_DOMICILIACION));
        socio.setNumeroCuenta(domiciliacion);

        String esAbonado = getCellValueAsString(row.getCell(COL_ABONADO_BETIS));
        socio.setAbonadoBetis("si".equalsIgnoreCase(esAbonado) || "sí".equalsIgnoreCase(esAbonado));

        String esAccionista = getCellValueAsString(row.getCell(COL_ACCIONISTA_BETIS));
        socio.setAccionistaBetis("si".equalsIgnoreCase(esAccionista) || "sí".equalsIgnoreCase(esAccionista));

        socio.setActivo(true); // Por defecto, los nuevos socios están activos
        socio.setNumeroSocio(numSocio++);
        socio.setFechaAlta(LocalDate.now());
        socio.setUsuario(usuario); // Asociamos el socio al usuario

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

  public List<SocioEntity> sociosByUsuario(UUID uid) {
    return socioRepository.findByUsuarioUid(uid);
  }

  public List<SocioEntity> obtenerSocioAutenticado() {
    String userEmail = Objects.requireNonNull(
        SecurityContextHolder.getContext().getAuthentication()).getName();
    // Asumimos que un usuario tiene al menos una ficha de socio.
    // Esta lógica busca la primera que encuentra asociada a su email.
    return socioRepository.findByUsuarioEmail(userEmail);
  }

  @Transactional(readOnly = true)
  public CarnetDto obtenerDatosCarnetUsuarioAutenticado() {
    String userEmail = Objects.requireNonNull(
        SecurityContextHolder.getContext().getAuthentication()).getName();

    // 1. Obtener la información de la peña
    PenaEntity penaInfo = penaRepository.findById(1L) // Asumiendo que el ID es 1
        .orElseThrow(
            () -> new EntityNotFoundException("No se encontró la información de la peña."));

    // 2. Obtener todos los socios del usuario y mapearlos a DTOs
    List<SocioDto> sociosDto = socioRepository.findByUsuarioEmail(userEmail).stream()
        .map(SocioDto::fromEntity).collect(Collectors.toList());

    return new CarnetDto(penaInfo, sociosDto);
  }

  public SocioEntity crearSocioAsociado(SocioEntity nuevoSocioData) {
    // 1. Obtener el email del usuario autenticado desde el contexto de seguridad
    String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();

    // 2. Buscar el usuario principal en la base de datos
    UsuarioEntity usuario = usuarioRepository.findByEmailIgnoreCase(userEmail)
        .orElseThrow(
            () -> new UsernameNotFoundException("Usuario no encontrado con email: " + userEmail));

    // 3. Crear y configurar la nueva entidad Socio
    SocioEntity nuevoSocio = new SocioEntity();
    nuevoSocio.setNumeroSocio(generarNumeroSocio());
    nuevoSocio.setNombre(nuevoSocioData.getNombre());
    nuevoSocio.setDni(nuevoSocioData.getDni());
    nuevoSocio.setFechaNacimiento(nuevoSocioData.getFechaNacimiento());
    nuevoSocio.setTelefono(nuevoSocioData.getTelefono());
    nuevoSocio.setDireccion(nuevoSocioData.getDireccion());
    nuevoSocio.setPoblacion(nuevoSocioData.getPoblacion());
    nuevoSocio.setProvincia(nuevoSocioData.getProvincia());
    nuevoSocio.setFechaAlta(LocalDate.now());
    nuevoSocio.setCodigoPostal(nuevoSocioData.getCodigoPostal());

    // Si no se proporciona un número de cuenta, hereda el del socio principal.
    if (nuevoSocioData.getNumeroCuenta() == null || nuevoSocioData.getNumeroCuenta().isBlank()) {
      SocioEntity socioPrincipal = usuario.getSocios().stream().findFirst()
          .orElseThrow(() -> new IllegalStateException(
              "El usuario no tiene un socio principal para heredar la cuenta."));
      nuevoSocio.setNumeroCuenta(socioPrincipal.getNumeroCuenta());
    } else {
      nuevoSocio.setNumeroCuenta(nuevoSocioData.getNumeroCuenta());
    }

    // 4. Asignar el usuario al nuevo socio
    nuevoSocio.setUsuario(usuario);
    nuevoSocio.setActivo(true); // Por defecto, el nuevo socio se crea como activo

    // 5. Guardar el nuevo socio en la base de datos
    return socioRepository.save(nuevoSocio);
  }
}
