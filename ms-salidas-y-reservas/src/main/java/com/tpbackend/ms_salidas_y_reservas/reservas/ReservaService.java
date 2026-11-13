package com.tpbackend.ms_salidas_y_reservas.reservas;



import com.tpbackend.ms_salidas_y_reservas.exception.CapacidadExcedidaException;
import com.tpbackend.ms_salidas_y_reservas.exception.DiaOcupadoException;
import com.tpbackend.ms_salidas_y_reservas.exception.FechaInvalidaException;
import com.tpbackend.ms_salidas_y_reservas.exception.PropiedadNoAutorizadaException;
import com.tpbackend.ms_salidas_y_reservas.exception.ReservaFinalizadaException;
import com.tpbackend.ms_salidas_y_reservas.exception.ReservaNoConfirmadaException;
import com.tpbackend.ms_salidas_y_reservas.exception.ReservaNoEncontradaException;
import com.tpbackend.ms_salidas_y_reservas.exception.SalidaNoEncontradaException;
import com.tpbackend.ms_salidas_y_reservas.exception.SalidaNoProgramadaException;
import com.tpbackend.ms_salidas_y_reservas.reservas.dto.ReservaCreateDTO;
import com.tpbackend.ms_salidas_y_reservas.reservas.dto.ReservaResponseDTO;
import com.tpbackend.ms_salidas_y_reservas.salidas.EstadoSalida;
import com.tpbackend.ms_salidas_y_reservas.salidas.Salida;
import com.tpbackend.ms_salidas_y_reservas.salidas.SalidaRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReservaService {

    private static final Logger log = LoggerFactory.getLogger(ReservaService.class);

    private final ReservaRepository reservaRepository;
    private final SalidaRepository salidaRepository;

    @Transactional
    public ReservaResponseDTO crear(ReservaCreateDTO dto, String titularSub) {
        log.info("Creando reserva para salida: {} - titular: {}", dto.getSalidaId(), titularSub);

        Salida salida = salidaRepository.findById(dto.getSalidaId())
                .orElseThrow(() -> {
                    log.warn("Salida no encontrada: {}", dto.getSalidaId());
                    return new SalidaNoEncontradaException(dto.getSalidaId());
                });

        if (salida.getEstado() != EstadoSalida.PROGRAMADA) {
            log.warn("La salida {} no esta programada", dto.getSalidaId());
            throw new SalidaNoProgramadaException();
        }

        if (salida.getInicio().isBefore(LocalDateTime.now())) {
            log.warn("La salida {} ya comenzo", dto.getSalidaId());
            throw new FechaInvalidaException("La salida ya comenzo");
        }

        List<Reserva> ocupadas = reservaRepository.findBySalidaIdAndEstadoIn(
                salida.getId(),
                List.of(EstadoReserva.CONFIRMADA, EstadoReserva.PENDIENTE_DE_PAGO)
        );
        int ocupados = ocupadas.stream().mapToInt(Reserva::getCantidad).sum();

        if (ocupados + dto.getCantidad() > salida.getCapacidad()) {
            log.warn("No hay suficientes lugares para la salida {}", dto.getSalidaId());
            throw new CapacidadExcedidaException("No hay suficientes lugares disponibles");
        }

        List<Reserva> reservasDelTurista = reservaRepository.findByTitularSubAndEstadoIn(titularSub, List.of(EstadoReserva.CONFIRMADA, EstadoReserva.PENDIENTE_DE_PAGO));

        for (Reserva r : reservasDelTurista) {
            Salida salidaExistente = salidaRepository.findById(r.getSalidaId()).orElse(null);
            if (salidaExistente != null && salidaExistente.getInicio().toLocalDate().equals(salida.getInicio().toLocalDate())) {
                log.warn("El turista {} ya tiene una reserva para el dia {}", titularSub, salida.getInicio().toLocalDate());
                throw new DiaOcupadoException();
            }
        }



        Reserva reserva = Reserva.builder()
                .salidaId(dto.getSalidaId())
                .titularSub(titularSub)
                .cantidad(dto.getCantidad())
                .fechaCreacion(LocalDateTime.now())
                .build();

        Reserva guardada = reservaRepository.save(reserva);
        log.debug("Reserva creada con ID: {}", guardada.getId());
        return mapToResponse(guardada);
    }

    public Page<ReservaResponseDTO> listarPorTitular(String titularSub, Pageable pageable) {
        log.info("Listando reservas del titular: {} - page: {}, size: {}", titularSub, pageable.getPageNumber(), pageable.getPageSize());
        return reservaRepository.findByTitularSub(titularSub, pageable)
                .map(this::mapToResponse);
    }

    public Page<ReservaResponseDTO> listarPorPrestador(String prestadorSub, Pageable pageable) {
        log.info("Listando reservas del prestador: {} - page: {}, size: {}", prestadorSub, pageable.getPageNumber(), pageable.getPageSize());

        List<Salida> salidas = salidaRepository.findByPrestadorSub(prestadorSub);
        List<UUID> salidaIds = salidas.stream().map(Salida::getId).collect(Collectors.toList());

        return reservaRepository.findBySalidaIdIn(salidaIds, pageable)
                .map(this::mapToResponse);
    }

    public ReservaResponseDTO confirmar(UUID id) {
        log.info("Confirmando reserva: {}", id);

        Reserva reserva = reservaRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Reserva no encontrada: {}", id);
                    return new ReservaNoEncontradaException(id);
                });

        if (reserva.getEstado() != EstadoReserva.PENDIENTE_DE_PAGO) {
            log.warn("La reserva {} no esta pendiente de pago", id);
            throw new ReservaFinalizadaException();
        }

        reserva.setEstado(EstadoReserva.CONFIRMADA);
        Reserva guardada = reservaRepository.save(reserva);
        log.debug("Reserva confirmada: {}", id);
        return mapToResponse(guardada);
    }

    public ReservaResponseDTO cancelar(UUID id, String titularSub) {
        log.info("Cancelando reserva: {} - titular: {}", id, titularSub);

        Reserva reserva = reservaRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Reserva no encontrada: {}", id);
                    return new ReservaNoEncontradaException(id);
                });

        if (!reserva.getTitularSub().equals(titularSub)) {
            log.warn("El titular {} no tiene permiso para cancelar la reserva {}", titularSub, id);
            throw new PropiedadNoAutorizadaException();
        }

        if (reserva.getEstado() != EstadoReserva.CONFIRMADA) {
            log.warn("La reserva {} no esta confirmada", id);
            throw new ReservaNoConfirmadaException();
        }

        Salida salida = salidaRepository.findById(reserva.getSalidaId())
                .orElseThrow(() -> new SalidaNoEncontradaException(reserva.getSalidaId()));

        if (salida.getInicio().isBefore(LocalDateTime.now())) {
            log.warn("La salida {} ya comenzo", reserva.getSalidaId());
            throw new FechaInvalidaException("La salida ya comenzo");
        }

        reserva.setEstado(EstadoReserva.CANCELADA);
        Reserva guardada = reservaRepository.save(reserva);
        log.debug("Reserva cancelada: {}", id);
        return mapToResponse(guardada);
    }

    @Transactional
    public void finalizarReservasPorSalida(UUID salidaId) {
        log.info("Finalizando reservas de la salida: {}", salidaId);

        List<Reserva> reservas = reservaRepository.findBySalidaIdAndEstado(salidaId, EstadoReserva.CONFIRMADA);
        for (Reserva reserva : reservas) {
            reserva.setEstado(EstadoReserva.FINALIZADA);
            reservaRepository.save(reserva);
            log.debug("Reserva finalizada: {}", reserva.getId());
        }

        log.info("Reservas finalizadas: {}", reservas.size());
    }


    private ReservaResponseDTO mapToResponse(Reserva reserva) {
        ReservaResponseDTO response = new ReservaResponseDTO();
        response.setId(reserva.getId());
        response.setSalidaId(reserva.getSalidaId());
        response.setTitularSub(reserva.getTitularSub());
        response.setCantidad(reserva.getCantidad());
        response.setFechaCreacion(reserva.getFechaCreacion());
        response.setEstado(reserva.getEstado());

        Salida salida = salidaRepository.findById(reserva.getSalidaId()).orElse(null);
        response.setAfectada(salida != null && salida.getEstado() == EstadoSalida.SUSPENDIDA);

        return response;
    }
}