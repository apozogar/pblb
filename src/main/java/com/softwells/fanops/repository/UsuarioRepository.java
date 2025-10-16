package com.softwells.fanops.repository;

import com.softwells.fanops.model.UsuarioEntity;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UsuarioRepository extends JpaRepository<UsuarioEntity, UUID> {
  @Query(
      "SELECT u FROM UsuarioEntity u LEFT JOIN FETCH u.socios s LEFT JOIN FETCH s.pena WHERE lower(u.email) = lower(:email)")
  Optional<UsuarioEntity> findByEmailIgnoreCase(@Param("email") String email);
}
