package com.tpbackend.ms_catalogo.actividad;


import org.springframework.stereotype.Service;
import com.tpbackend.ms_catalogo.actividad.dto.ActividadCreateDTO;
import com.tpbackend.ms_catalogo.actividad.dto.ActividadResponseDTO;
import com.tpbackend.ms_catalogo.exception.ActividadNoEncontradaException;
import com.tpbackend.ms_catalogo.exception.LimiteRafagasInvalidoException;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Service 
@RequiredArgsConstructor 
public class ActividadService {

    private static final Logger log = LoggerFactory.getLogger(ActividadService.class);

    private final ActividadRepository actividadRepository;

    public ActividadResponseDTO crearActividad(ActividadCreateDTO actividadCreate, String prestadorSub) {
        log.info("Creando actividad: {}", actividadCreate.getNombre());

        if (actividadCreate.getLimiteRafagasKmh() <= 0) {
            log.warn("Limite de rafagas invalido: {}", actividadCreate.getLimiteRafagasKmh());
            throw new LimiteRafagasInvalidoException();
        }

        Actividad actividad = Actividad.builder()
                .nombre(actividadCreate.getNombre())
                .descripcion(actividadCreate.getDescripcion())
                .latitud(actividadCreate.getLatitud())
                .longitud(actividadCreate.getLongitud())
                .limiteRafagasKmh(actividadCreate.getLimiteRafagasKmh())
                .prestadorSub(prestadorSub)
                .build();

        Actividad actividadGuardada = actividadRepository.save(actividad);
        log.debug("Actividad creada con ID: {}", actividadGuardada.getId());
        return mapToResponse(actividadGuardada);
    }

    public Page<ActividadResponseDTO> listarActividades(Pageable pageable) {
        log.info("Listando actividades - page: {}, size: {}", pageable.getPageNumber(), pageable.getPageSize());
        Page<Actividad> actividades = actividadRepository.findAll(pageable);
        log.debug("Actividades encontradas: {}", actividades.getTotalElements());
        return actividades.map(this::mapToResponse);
    }

    public ActividadResponseDTO obtenerPorId(UUID id) {
        log.info("Buscando actividad por ID: {}", id);
        return actividadRepository.findById(id)
                .map(this::mapToResponse)
                .orElseThrow(() -> {
                    log.warn("Actividad no encontrada: {}", id);
                    return new ActividadNoEncontradaException(id);
                });
    }

    private ActividadResponseDTO mapToResponse(Actividad actividad) {
        ActividadResponseDTO response = new ActividadResponseDTO();
        response.setId(actividad.getId());
        response.setNombre(actividad.getNombre());
        response.setDescripcion(actividad.getDescripcion());
        response.setLatitud(actividad.getLatitud());
        response.setLongitud(actividad.getLongitud());
        response.setLimiteRafagasKmh(actividad.getLimiteRafagasKmh());
        response.setPrestadorSub(actividad.getPrestadorSub());
        return response;
    }

}
