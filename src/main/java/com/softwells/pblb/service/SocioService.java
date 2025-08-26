package com.softwells.pblb.service;

import com.softwells.pblb.model.SocioEntity;
import com.softwells.pblb.repository.SocioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class SocioService {
    private final SocioRepository socioRepository;

    @Autowired
    public SocioService(SocioRepository socioRepository) {
        this.socioRepository = socioRepository;
    }

    public SocioEntity crear(SocioEntity socio) {
        if (socioRepository.existsByDni(socio.getDni())) {
            throw new IllegalArgumentException("Ya existe un socio con ese DNI");
        }
        return socioRepository.save(socio);
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
        return socioRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Socio no encontrado"));
    }

    public List<SocioEntity> obtenerTodos() {
        return socioRepository.findAll();
    }

    public List<SocioEntity> obtenerSociosActivos() {
        return socioRepository.findByActivo(true);
    }
}
