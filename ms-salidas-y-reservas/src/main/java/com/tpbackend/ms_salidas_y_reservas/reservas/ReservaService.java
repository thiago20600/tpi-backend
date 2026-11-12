package com.tpbackend.ms_salidas_y_reservas.reservas;

import com.tpbackend.BusinessException;
import com.tpbackend.ms_salidas_y_reservas.exception.ReservaNoEncontradaException;
import com.tpbackend.ms_salidas_y_reservas.reservas.dto.ReservaCreateDTO;
import com.tpbackend.ms_salidas_y_reservas.reservas.dto.ReservaResponseDTO;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReservaService {

    private final ReservaRepository reservaRepository;

    public ReservaResponseDTO crear(ReservaCreateDTO dto, String titularSub) {
        Reserva reserva = Reserva.builder()
                .salidaId(dto.getSalidaId())
                .titularSub(titularSub)
                .cantidad(dto.getCantidad())
                .fechaCreacion(LocalDateTime.now())
                .build();

        Reserva guardada = reservaRepository.save(reserva);
        return mapToResponse(guardada);
    }

    public List<ReservaResponseDTO> listarPorTitular(String titularSub) {
        return reservaRepository.findByTitularSub(titularSub).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public ReservaResponseDTO confirmar(UUID id) {
        Reserva reserva = reservaRepository.findById(id)
                .orElseThrow(() -> new ReservaNoEncontradaException(id));

        if (reserva.getEstado() != EstadoReserva.PENDIENTE_DE_PAGO) {
            throw new BusinessException("La reserva no esta pendiente de pago", 400);
        }

        reserva.setEstado(EstadoReserva.CONFIRMADA);
        Reserva guardada = reservaRepository.save(reserva);
        return mapToResponse(guardada);
    }

    public ReservaResponseDTO cancelar(UUID id, String titularSub) {
        Reserva reserva = reservaRepository.findById(id)
                .orElseThrow(() -> new ReservaNoEncontradaException(id));

        if (!reserva.getTitularSub().equals(titularSub)) {
            throw new BusinessException("No tenes permiso para cancelar esta reserva", 403);
        }

        if (reserva.getEstado() == EstadoReserva.CANCELADA) {
            return mapToResponse(reserva);
        }

        reserva.setEstado(EstadoReserva.CANCELADA);
        Reserva guardada = reservaRepository.save(reserva);
        return mapToResponse(guardada);
    }

    private ReservaResponseDTO mapToResponse(Reserva reserva) {
        ReservaResponseDTO response = new ReservaResponseDTO();
        response.setId(reserva.getId());
        response.setSalidaId(reserva.getSalidaId());
        response.setTitularSub(reserva.getTitularSub());
        response.setCantidad(reserva.getCantidad());
        response.setFechaCreacion(reserva.getFechaCreacion());
        response.setEstado(reserva.getEstado());

        response.setAfectada(false);

        return response;
    }
}
