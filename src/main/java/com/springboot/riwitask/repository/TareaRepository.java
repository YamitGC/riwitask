package com.springboot.riwitask.repository;

import com.springboot.riwitask.enums.*;
import com.springboot.riwitask.entity.Tarea;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TareaRepository extends JpaRepository<Tarea, Long> {

    List<Tarea> findByUsuarioId(Long usuarioId);

    List<Tarea> findByEstado(EstadoTarea estado);

    List<Tarea> findByPrioridad(Prioridad prioridad);

    List<Tarea> findByUsuarioIdAndEstado(Long usuarioId, EstadoTarea estado);

    // Requerimiento especial: tareas pendientes de un usuario,
    // ordenadas por fecha de creación descendente (más recientes primero)
    List<Tarea> findByUsuarioIdAndEstadoOrderByFechaCreacionDesc(Long usuarioId, EstadoTarea estado);
}
