package com.tpbackend.ms_salidas_y_reservas.reservas;

import com.tpbackend.ms_salidas_y_reservas.reservas.dto.ReservaCreateDTO;
import com.tpbackend.ms_salidas_y_reservas.reservas.dto.ReservaResponseDTO;
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
import org.springframework.web.bind.annotation.RestController;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;

import java.util.List;
import java.util.UUID;

@Tag(name = "Reservas")
@RestController
@RequestMapping("/api/v1/reservas")
@RequiredArgsConstructor
public class ReservaController {

    private final ReservaService reservaService;

    @Operation(summary = "Crear reserva", description = "Crea una nueva reserva para una salida")
    @PostMapping
    public ResponseEntity<ReservaResponseDTO> crear(@Valid @RequestBody ReservaCreateDTO dto, @AuthenticationPrincipal Jwt jwt) {
        String titularSub = jwt.getSubject();
        ReservaResponseDTO response = reservaService.crear(dto, titularSub);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Listar reservas", description = "Lista las reservas del usuario autenticado segun su rol")
    @GetMapping
    public ResponseEntity<Page<ReservaResponseDTO>> listarReservas(@AuthenticationPrincipal Jwt jwt, @ParameterObject Pageable pageable) {
        String sub = jwt.getSubject();
        List<String> roles = jwt.getClaimAsStringList("realm_access.roles");

        String rolPrincipal = (roles != null && !roles.isEmpty()) ? roles.get(0) : "TURISTA";

        Page<ReservaResponseDTO> response = switch (rolPrincipal) {
            case "PRESTADOR" -> reservaService.listarPorPrestador(sub, pageable);
            case "TURISTA" -> reservaService.listarPorTitular(sub, pageable);
            default -> reservaService.listarPorTitular(sub, pageable);
        };

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Obtener reserva por ID", description = "Obtiene una reserva por ID para consultas internas")
    @GetMapping("/{id}")
    public ResponseEntity<ReservaResponseDTO> obtenerPorId(@PathVariable UUID id) {
        return ResponseEntity.ok(reservaService.obtenerPorId(id));
    }

    @Operation(summary = "Cancelar reserva", description = "Cancela una reserva propia")
    @PostMapping("/{id}/cancelacion")
    public ResponseEntity<ReservaResponseDTO> cancelar(@PathVariable UUID id, @AuthenticationPrincipal Jwt jwt) {
        String titularSub = jwt.getSubject();
        ReservaResponseDTO response = reservaService.cancelar(id, titularSub);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Confirmar reserva", description = "Confirma una reserva")
    @PostMapping("/{id}/confirmacion")
    public ResponseEntity<ReservaResponseDTO> confirmar(@PathVariable UUID id) {
        ReservaResponseDTO response = reservaService.confirmar(id);
        return ResponseEntity.ok(response);
    }
}