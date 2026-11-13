package com.tpbackend.ms_salidas_y_reservas.salidas;

import com.tpbackend.ms_salidas_y_reservas.salidas.dto.SalidaCreateDTO;
import com.tpbackend.ms_salidas_y_reservas.salidas.dto.SalidaResponseDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/salidas")
@RequiredArgsConstructor
public class SalidaController {

    private final SalidaService salidaService;

    @PostMapping
    public ResponseEntity<SalidaResponseDTO> crear(@Valid @RequestBody SalidaCreateDTO dto, @AuthenticationPrincipal Jwt jwt) {
        String prestadorSub = jwt.getSubject();
        String token = jwt.getTokenValue();
        SalidaResponseDTO response = salidaService.crear(dto, prestadorSub, token);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<Page<SalidaResponseDTO>> listar(@RequestParam(required = false) UUID actividadId, @AuthenticationPrincipal Jwt jwt, @ParameterObject Pageable pageable) {
        String sub = jwt.getSubject();
        List<String> roles = jwt.getClaimAsStringList("realm_access.roles");

        String rolPrincipal = (roles != null && !roles.isEmpty()) ? roles.get(0) : "TURISTA";

        Page<SalidaResponseDTO> response = switch (rolPrincipal) {
            case "PRESTADOR" -> salidaService.listarPorPrestador(sub, actividadId, pageable);
            case "TURISTA" -> salidaService.listarProgramadas(actividadId, pageable);
            default -> salidaService.listarProgramadas(actividadId, pageable);
        };

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<SalidaResponseDTO> obtenerPorId(@PathVariable UUID id) {
        SalidaResponseDTO response = salidaService.obtenerPorId(id);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{id}/suspension")
    public ResponseEntity<SalidaResponseDTO> suspender(@PathVariable UUID id, @AuthenticationPrincipal Jwt jwt) {
        String prestadorSub = jwt.getSubject();
        SalidaResponseDTO response = salidaService.suspender(id, prestadorSub);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{id}/inicio")
    public ResponseEntity<SalidaResponseDTO> iniciarSalida(@PathVariable UUID id, @AuthenticationPrincipal Jwt jwt) {
        String prestadorSub = jwt.getSubject();
        SalidaResponseDTO response = salidaService.iniciarSalida(id, prestadorSub);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{id}/finalizacion")
    public ResponseEntity<SalidaResponseDTO> finalizarSalida(@PathVariable UUID id, @AuthenticationPrincipal Jwt jwt) {
        String prestadorSub = jwt.getSubject();
        SalidaResponseDTO response = salidaService.finalizarSalida(id, prestadorSub);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{id}/revision-viento")
    public ResponseEntity<SalidaResponseDTO> revisarViento(@PathVariable UUID id, @AuthenticationPrincipal Jwt jwt) {
        String prestadorSub = jwt.getSubject();
        String token = jwt.getTokenValue();
        SalidaResponseDTO response = salidaService.revisarViento(id, prestadorSub, token);
        return ResponseEntity.ok(response);
    }


}