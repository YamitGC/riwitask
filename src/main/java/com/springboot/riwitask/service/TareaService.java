package com.springboot.riwitask.service;

import com.springboot.riwitask.entity.*;
import com.springboot.riwitask.dto.TareaRequestDTO;
import com.springboot.riwitask.dto.TareaResponseDTO;
import com.springboot.riwitask.enums.Prioridad;
import com.springboot.riwitask.enums.EstadoTarea;
import com.springboot.riwitask.exception.ResourceNotFoundException;
import com.springboot.riwitask.repository.TareaRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class TareaService {

    private final TareaRepository tareaRepository;
    private final UsuarioService usuarioService;

    public TareaService(TareaRepository tareaRepository, UsuarioService usuarioService) {
        this.tareaRepository = tareaRepository;
        this.usuarioService = usuarioService;
    }

    public TareaResponseDTO crear(TareaRequestDTO dto){
        Usuario usuario = usuarioService.obtenerUsuarioOrThrow(dto.getUsuarioId());

        Tarea tarea = new Tarea();
        tarea.setTitulo(dto.getTitulo());
        tarea.setDescripcion(dto.getDescripcion());
        tarea.setEstado(dto.getEstado());
        tarea.setPrioridad(dto.getPrioridad());
        tarea.setFechaCreacion(LocalDateTime.now());
        tarea.setUsuario(usuario);

        return toResponseDTO(tareaRepository.save(tarea));
    }

    public List<TareaResponseDTO> listarTodas(){
        return tareaRepository.findAll().stream().map(this::toResponseDTO).toList();
    }

    public TareaResponseDTO buscarPorId(Long id){
        return toResponseDTO(obtenerTareaOrThrow(id));
    }

    public TareaResponseDTO actualizar(Long id, TareaRequestDTO dto){
        Tarea tarea = obtenerTareaOrThrow(id);
        Usuario usuario = usuarioService.obtenerUsuarioOrThrow(dto.getUsuarioId());

        tarea.setTitulo(dto.getTitulo());
        tarea.setDescripcion(dto.getDescripcion());
        tarea.setEstado(dto.getEstado());
        tarea.setPrioridad(dto.getPrioridad());
        tarea.setUsuario(usuario);

        return toResponseDTO(tareaRepository.save(tarea));
    }

    public void eliminar(Long id){
        Tarea tarea = obtenerTareaOrThrow(id);
        tareaRepository.delete(tarea);
    }

    public List<TareaResponseDTO> listarPorUsuario(Long usuarioId) {
        usuarioService.obtenerUsuarioOrThrow(usuarioId);
        return tareaRepository.findByUsuarioId(usuarioId).stream().map(this::toResponseDTO).toList();
    }

    public List<TareaResponseDTO> listarPorEstado(EstadoTarea estado) {
        return tareaRepository.findByEstado(estado).stream().map(this::toResponseDTO).toList();
    }

    public List<TareaResponseDTO> listarPorPrioridad(Prioridad prioridad) {
        return tareaRepository.findByPrioridad(prioridad).stream().map(this::toResponseDTO).toList();
    }

    public List<TareaResponseDTO> listarPorUsuarioYEstado(Long usuarioId, EstadoTarea estado) {
        usuarioService.obtenerUsuarioOrThrow(usuarioId);
        return tareaRepository.findByUsuarioIdAndEstado(usuarioId, estado)
                .stream().map(this::toResponseDTO).toList();
    }


    // Requerimiento especial: pendientes de un usuario, más recientes primero
    public List<TareaResponseDTO> listarPendientesRecientesPorUsuario(Long usuarioId){
        usuarioService.obtenerUsuarioOrThrow(usuarioId);
        return tareaRepository
                .findByUsuarioIdAndEstadoOrderByFechaCreacionDesc(usuarioId, EstadoTarea.PENDIENTE)
                .stream().map(this::toResponseDTO).toList();
    }

    private Tarea obtenerTareaOrThrow(Long id){
        return tareaRepository.findById(id)
                .orElseThrow(()-> new ResourceNotFoundException("Tarea no encontrada con id: "+ id));
    }


    private TareaResponseDTO toResponseDTO(Tarea tarea){
        return new TareaResponseDTO(
                tarea.getId(), tarea.getTitulo(), tarea.getDescripcion(),
                tarea.getEstado(), tarea.getPrioridad(), tarea.getFechaCreacion(),
                tarea.getUsuario().getId(), tarea.getUsuario().getNombre());
    }
}
