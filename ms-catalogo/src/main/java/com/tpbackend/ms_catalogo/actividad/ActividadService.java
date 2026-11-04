package com.tpbackend.ms_catalogo.actividad;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.tpbackend.ms_catalogo.actividad.dto.ActividadCreateDTO;
import com.tpbackend.ms_catalogo.actividad.dto.ActividadResponseDTO;
import lombok.RequiredArgsConstructor;

@Service 
@RequiredArgsConstructor 
public class ActividadService {
    
    private final ActividadRepository actividadRepository;
    

    public ActividadResponseDTO crearActividad(ActividadCreateDTO actividadCreate, String prestadorSub){
        if (actividadCreate.getLimiteRafagasKmh() <= 0){
            /* las excepciones hay que cambiarlas  */
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "el limite debe ser mayor a 0");
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
