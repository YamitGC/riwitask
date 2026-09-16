package com.springboot.riwitask.dto;

import com.springboot.riwitask.enums.EstadoTarea;
import com.springboot.riwitask.enums.Prioridad;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
public class TareaResponseDTO {
    private Long id;
    private String titulo;
    private String descripcion;
    private EstadoTarea estado;
    private Prioridad prioridad;
    private LocalDateTime fechaCreacion;
    private Long usuarioId;
    private String usuarioNombre;
}