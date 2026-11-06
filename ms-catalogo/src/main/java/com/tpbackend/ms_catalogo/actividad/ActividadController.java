package com.tpbackend.ms_catalogo.actividad;

import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.tpbackend.ms_catalogo.actividad.dto.ActividadCreateDTO;
import com.tpbackend.ms_catalogo.actividad.dto.ActividadResponseDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.web.bind.annotation.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.UUID;

import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@RestController 
@RequestMapping("/api/v1/actividades")
@RequiredArgsConstructor 
@Tag(name = "Actividades", description = "Gestión de actividades del catálogo")
public class ActividadController {

    private final ActividadService actividadService;


    @Operation(summary = "Crear actividad", description = "Crea una nueva actividad en el catálogo")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Actividad creada"),
        @ApiResponse(responseCode = "400", description = "Datos inválidos"),
        @ApiResponse(responseCode = "401", description = "No autenticado")
    })
    @PostMapping
    public ResponseEntity<ActividadResponseDTO> crearActividad(@Valid @RequestBody ActividadCreateDTO createDTO, @AuthenticationPrincipal Jwt jwt){
        String prestadorSub = jwt.getSubject();
        ActividadResponseDTO response = actividadService.crearActividad(createDTO, prestadorSub);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }


    @Operation(summary = "Listar actividades", description = "Lista todas las actividades (paginado)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Listado exitoso"),
        @ApiResponse(responseCode = "401", description = "No autenticado")
    })
    @GetMapping 
    public ResponseEntity<Page<ActividadResponseDTO>> listar(@ParameterObject Pageable pageable) {  
        
        Page<ActividadResponseDTO> resultado = actividadService.listarActividades(pageable);
        return ResponseEntity.ok(resultado);
    }

    
    @Operation(summary = "Obtener actividad por ID", description = "Devuelve una actividad si existe")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Actividad encontrada"),
        @ApiResponse(responseCode = "404", description = "No se encontro la actividad"),
        @ApiResponse(responseCode = "401", description = "No autenticado")
    })
    @GetMapping("/{id}")
    public ResponseEntity<ActividadResponseDTO> obtenerPorId(@PathVariable UUID id) {
        ActividadResponseDTO response = actividadService.obtenerPorId(id);
        return ResponseEntity.ok(response);
    }


}
