package com.softwells.pblb.repository;

import com.softwells.pblb.model.SocioEntity;
import java.time.LocalDate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.UUID;
import java.util.Optional;
import java.util.List;

@Repository
public interface SocioRepository extends JpaRepository<SocioEntity, UUID> {

  Optional<SocioEntity> findByDni(String dni);

  Optional<SocioEntity> findByEmail(String email);

  Optional<SocioEntity> findByNumeroSocio(String numeroSocio);

  List<SocioEntity> findByActivo(boolean activo);

  boolean existsByDni(String dni);

  boolean existsByEmail(String email);

  boolean existsByNumeroSocio(String numeroSocio);

  long countByFechaAltaGreaterThanEqual(LocalDate fechaDesde);

  List<SocioEntity> findByUsuarioUid(UUID usuarioUid);

  List<SocioEntity> findFirstByUsuarioEmail(String email);
}
