package com.tpbackend.ms_catalogo.actividad;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.tpbackend.BusinessException;
import com.tpbackend.ms_catalogo.actividad.dto.ActividadCreateDTO;
import com.tpbackend.ms_catalogo.actividad.dto.ActividadResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.UUID;

@Service 
@RequiredArgsConstructor 
public class ActividadService {
    
    private final ActividadRepository actividadRepository;
    

    public ActividadResponseDTO crearActividad(ActividadCreateDTO actividadCreate, String prestadorSub){

        if (prestadorSub == null || prestadorSub.isBlank()) {
            throw new BusinessException("El prestador no está autenticado", 401);
        }
        if (actividadCreate.getLimiteRafagasKmh() <= 0){
            throw new BusinessException("El limite debe ser mayor a 0", 400);
        }

        Actividad actividad = Actividad.builder()
                                        .nombre(actividadCreate.getNombre())
                                        .descripcion(actividadCreate.getDescripcion())
                                        .latitud(actividadCreate.getLatitud())
                                        .longitud(actividadCreate.getLongitud())
                                        .prestadorSub(prestadorSub)
                                        .build();
        
        Actividad actividadGuardada = actividadRepository.save(actividad);

        return mapToResponse(actividadGuardada);
    }

    public Page<ActividadResponseDTO> listarActividades(Pageable pageable){
        Page<Actividad> actividades = actividadRepository.findAll(pageable);
        return actividades.map(this::mapToResponse);
    }

    public ActividadResponseDTO obtenerPorId(UUID id) {
        return actividadRepository.findById(id)
                .map(this::mapToResponse)
                .orElseThrow(() -> new BusinessException("Actividad no encontrada: " + id, 404));
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
