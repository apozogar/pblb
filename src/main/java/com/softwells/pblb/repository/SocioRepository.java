package com.softwells.pblb.repository;

import com.softwells.pblb.model.SocioEntity;
import java.time.LocalDate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.UUID;
import java.util.Optional;
import java.util.List;

@Repository
public interface SocioRepository extends JpaRepository<SocioEntity, UUID> {

  List<SocioEntity> findByActivo(boolean activo);

  boolean existsByDni(String dni);

  long countByFechaAltaGreaterThanEqual(LocalDate fechaDesde);

  List<SocioEntity> findByUsuarioUid(UUID usuarioUid);

  List<SocioEntity> findByUsuarioEmail(String email);

  long countByFechaNacimientoAfter(LocalDate fecha);

  long countByFechaNacimientoBeforeOrFechaNacimientoEquals(LocalDate fechaNacimiento,
      LocalDate fechaNacimiento2);

  @Query("SELECT MAX(s.numeroSocio) FROM SocioEntity s")
  Optional<Integer> findMaxNumeroSocio();
}
