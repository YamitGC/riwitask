package com.springboot.riwitask.dto;

import com.springboot.riwitask.enums.EstadoTarea;
import com.springboot.riwitask.enums.Prioridad;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TareaRequestDTO {

    @NotBlank(message = "El título es obligatorio")
    private String titulo;

    private String descripcion;

    @NotNull(message = "El estado es obligatorio")
    private EstadoTarea estado;

    @NotNull(message = "La prioridad es obligatoria")
    private Prioridad prioridad;

    @NotNull(message = "Debe indicar el usuario responsable")
    private Long usuarioId;
}