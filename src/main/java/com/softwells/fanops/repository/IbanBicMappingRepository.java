package com.softwells.fanops.repository;

import com.softwells.fanops.model.IbanBicMappingEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IbanBicMappingRepository extends JpaRepository<IbanBicMappingEntity, String> {}