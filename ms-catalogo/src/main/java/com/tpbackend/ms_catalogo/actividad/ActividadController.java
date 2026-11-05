package com.tpbackend.ms_catalogo.actividad;

import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.tpbackend.ms_catalogo.actividad.dto.ActividadCreateDTO;
import com.tpbackend.ms_catalogo.actividad.dto.ActividadResponseDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@RestController 
@RequestMapping("/api/v1/actividades")
@RequiredArgsConstructor 
public class ActividadController {

    private final ActividadService actividadService;

    @PostMapping
    public ResponseEntity<ActividadResponseDTO> crearActividad(@Valid @RequestBody ActividadCreateDTO createDTO, @AuthenticationPrincipal Jwt jwt){
        String prestadorSub = jwt.getSubject();
        ActividadResponseDTO response = actividadService.crearActividad(createDTO, prestadorSub);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping 
    public ResponseEntity<Page<ActividadResponseDTO>> listar(Pageable pageable) {  
        
        Page<ActividadResponseDTO> resultado = actividadService.listarActividades(pageable);
        return ResponseEntity.ok(resultado);
    }


}
