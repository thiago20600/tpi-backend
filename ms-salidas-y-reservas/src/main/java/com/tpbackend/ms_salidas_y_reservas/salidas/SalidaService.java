package com.tpbackend.ms_salidas_y_reservas.salidas;

import com.tpbackend.BusinessException;
import com.tpbackend.ms_salidas_y_reservas.exception.FechaInvalidaException;
import com.tpbackend.ms_salidas_y_reservas.exception.PropiedadNoAutorizadaException;
import com.tpbackend.ms_salidas_y_reservas.exception.SalidaNoEncontradaException;
import com.tpbackend.ms_salidas_y_reservas.exception.SalidaNoIniciadaException;
import com.tpbackend.ms_salidas_y_reservas.exception.SalidaNoProgramadaException;
import com.tpbackend.ms_salidas_y_reservas.reservas.ReservaService;
import com.tpbackend.ms_salidas_y_reservas.salidas.dto.SalidaCreateDTO;
import com.tpbackend.ms_salidas_y_reservas.salidas.dto.SalidaResponseDTO;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import com.tpbackend.ms_salidas_y_reservas.salidas.client.ActividadClient;
import com.tpbackend.ms_salidas_y_reservas.salidas.client.EvaluacionClient;
import com.tpbackend.ms_salidas_y_reservas.salidas.client.dto.ActividadDTO;
import com.tpbackend.ms_salidas_y_reservas.salidas.client.dto.EvaluacionResponseDTO;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SalidaService {

    private static final Logger log = LoggerFactory.getLogger(SalidaService.class);

    private final SalidaRepository salidaRepository;

    private final ActividadClient actividadClient;

    private final EvaluacionClient evaluacionClient;

    private final ReservaService reservaService;

    public SalidaResponseDTO crear(SalidaCreateDTO dto, String prestadorSub, String token) {
        log.info("Creando salida para actividad: {} - prestador: {}", dto.getActividadId(), prestadorSub);

        if (dto.getInicio().isBefore(LocalDateTime.now())) {
            log.warn("La fecha de inicio debe ser futura: {}", dto.getInicio());
            throw new FechaInvalidaException("La salida debe comenzar en el futuro");
        }
        if (dto.getFin().isBefore(dto.getInicio())) {
            log.warn("La fecha de fin debe ser posterior al inicio: {} - {}", dto.getInicio(), dto.getFin());
            throw new FechaInvalidaException("La fecha de fin debe ser posterior al inicio");
        }

        ActividadDTO actividad = actividadClient.obtenerActividad(dto.getActividadId(), token);

        if (actividad == null || !actividad.getPrestadorSub().equals(prestadorSub)) {
            log.warn("La actividad {} no existe o no pertenece al prestador {}", dto.getActividadId(), prestadorSub);
            throw new BusinessException("La actividad no existe o no te pertenece", 404);
        }

        Salida salida = Salida.builder()
                .actividadId(dto.getActividadId())
                .prestadorSub(prestadorSub)
                .inicio(dto.getInicio())
                .fin(dto.getFin())
                .capacidad(dto.getCapacidad())
                .estado(EstadoSalida.PROGRAMADA)        
                .precio(dto.getPrecio())
                .build();

        Salida guardada = salidaRepository.save(salida);
        log.debug("Salida creada con ID: {}", guardada.getId());
        return mapToResponse(guardada);
    }

    public Page<SalidaResponseDTO> listarPorPrestador(String prestadorSub, UUID actividadId, Pageable pageable) {
        log.info("Listando salidas del prestador: {} - actividadId: {} - page: {}, size: {}", prestadorSub, actividadId, pageable.getPageNumber(), pageable.getPageSize());

        Page<Salida> salidas;
        if (actividadId != null) {
            salidas = salidaRepository.findByPrestadorSubAndActividadId(prestadorSub, actividadId, pageable);
        } else {
            salidas = salidaRepository.findByPrestadorSub(prestadorSub, pageable);
        }

        return salidas.map(this::mapToResponse);
    }

    public Page<SalidaResponseDTO> listarProgramadas(UUID actividadId, Pageable pageable) {
        log.info("Listando salidas programadas - actividadId: {} - page: {}, size: {}", actividadId, pageable.getPageNumber(), pageable.getPageSize());

        Page<Salida> salidas;
        if (actividadId != null) {
            salidas = salidaRepository.findByEstadoAndActividadId(EstadoSalida.PROGRAMADA, actividadId, pageable);
        } else {
            salidas = salidaRepository.findByEstado(EstadoSalida.PROGRAMADA, pageable);
        }

        return salidas.map(this::mapToResponse);
    }

    public SalidaResponseDTO obtenerPorId(UUID id) {
        log.info("Buscando salida por ID: {}", id);
        return salidaRepository.findById(id)
                .map(this::mapToResponse)
                .orElseThrow(() -> {
                    log.warn("Salida no encontrada: {}", id);
                    return new SalidaNoEncontradaException(id);
                });
    }

    public SalidaResponseDTO suspender(UUID id, String prestadorSub) {
        log.info("Suspendiendo salida: {} - prestador: {}", id, prestadorSub);

        Salida salida = salidaRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Salida no encontrada: {}", id);
                    return new SalidaNoEncontradaException(id);
                });

        if (!salida.getPrestadorSub().equals(prestadorSub)) {
            log.warn("El prestador {} no tiene permiso para suspender la salida {}", prestadorSub, id);
            throw new PropiedadNoAutorizadaException();
        }

        if (salida.getEstado() != EstadoSalida.PROGRAMADA) {
            log.warn("La salida {} no esta programada (estado: {})", id, salida.getEstado());
            throw new SalidaNoProgramadaException();
        }

        salida.setEstado(EstadoSalida.SUSPENDIDA);
        Salida guardada = salidaRepository.save(salida);
        log.debug("Salida suspendida: {}", id);
        return mapToResponse(guardada);
    }

    public SalidaResponseDTO iniciarSalida(UUID id, String prestadorSub) {
        log.info("Iniciando salida: {} - prestador: {}", id, prestadorSub);

        Salida salida = salidaRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Salida no encontrada: {}", id);
                    return new SalidaNoEncontradaException(id);
                });

        if (!salida.getPrestadorSub().equals(prestadorSub)) {
            log.warn("El prestador {} no tiene permiso para iniciar la salida {}", prestadorSub, id);
            throw new PropiedadNoAutorizadaException();
        }

        if (salida.getEstado() != EstadoSalida.PROGRAMADA) {
            log.warn("La salida {} no esta programada (estado: {})", id, salida.getEstado());
            throw new SalidaNoProgramadaException();
        }

        if (salida.getInicio().isAfter(LocalDateTime.now())) {
            log.warn("La salida {} aun no puede iniciarse (inicio: {})", id, salida.getInicio());
            throw new FechaInvalidaException("La salida aun no puede iniciarse");
        }

        salida.setEstado(EstadoSalida.INICIADA);
        Salida guardada = salidaRepository.save(salida);
        log.debug("Salida iniciada: {}", id);
        return mapToResponse(guardada);
    }

    public SalidaResponseDTO finalizarSalida(UUID id, String prestadorSub) {
        log.info("Finalizando salida: {} - prestador: {}", id, prestadorSub);

        Salida salida = salidaRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Salida no encontrada: {}", id);
                    return new SalidaNoEncontradaException(id);
                });

        if (!salida.getPrestadorSub().equals(prestadorSub)) {
            log.warn("El prestador {} no tiene permiso para finalizar la salida {}", prestadorSub, id);
            throw new PropiedadNoAutorizadaException();
        }

        if (salida.getEstado() != EstadoSalida.INICIADA) {
            log.warn("La salida {} no esta iniciada (estado: {})", id, salida.getEstado());
            throw new SalidaNoIniciadaException();
        }

        salida.setEstado(EstadoSalida.FINALIZADA);
        Salida guardada = salidaRepository.save(salida);
        log.debug("Salida finalizada: {}", id);

        reservaService.finalizarReservasPorSalida(id);

        return mapToResponse(guardada);
    }

    public SalidaResponseDTO revisarViento(UUID id, String prestadorSub, String token) {
        log.info("Revisando viento de la salida: {} - prestador: {}", id, prestadorSub);

        Salida salida = salidaRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Salida no encontrada: {}", id);
                    return new SalidaNoEncontradaException(id);
                });

        if (!salida.getPrestadorSub().equals(prestadorSub)) {
            log.warn("El prestador {} no tiene permiso para revisar la salida {}", prestadorSub, id);
            throw new PropiedadNoAutorizadaException();
        }

        if (salida.getEstado() != EstadoSalida.PROGRAMADA) {
            log.warn("La salida {} no esta programada (estado: {})", id, salida.getEstado());
            throw new SalidaNoProgramadaException();
        }

        EvaluacionResponseDTO evaluacion = evaluacionClient.evaluarViento(
                salida.getActividadId(), salida.getInicio(), salida.getFin(), token);

        if ("SUPERA_UMBRAL".equals(evaluacion.getResultado())) {
            salida.setEstado(EstadoSalida.SUSPENDIDA);
            salidaRepository.save(salida);
            log.warn("La salida {} fue suspendida por viento", id);
        }

        return mapToResponse(salida);
    }

    private SalidaResponseDTO mapToResponse(Salida salida) {
        SalidaResponseDTO response = new SalidaResponseDTO();
        response.setId(salida.getId());
        response.setActividadId(salida.getActividadId());
        response.setPrestadorSub(salida.getPrestadorSub());
        response.setInicio(salida.getInicio());
        response.setFin(salida.getFin());
        response.setCapacidad(salida.getCapacidad());
        response.setEstado(salida.getEstado());
        response.setPrecio(salida.getPrecio());
        return response;
    }
}