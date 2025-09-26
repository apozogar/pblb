package com.softwells.fanops.repository;

import com.softwells.fanops.model.CuotaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.UUID;

@Repository
public interface CuotaRepository extends JpaRepository<CuotaEntity, UUID> {

  List<CuotaEntity> findBySocioUid(UUID uid);
}
