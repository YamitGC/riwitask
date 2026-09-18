package com.springboot.riwitask.service;

import com.springboot.riwitask.dto.TareaRequestDTO;
import com.springboot.riwitask.entity.Usuario;
import com.springboot.riwitask.entity.Tarea;
import com.springboot.riwitask.enums.Prioridad;
import com.springboot.riwitask.enums.EstadoTarea;
import com.springboot.riwitask.repository.TareaRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TareaServiceTest {

    @Mock
    private TareaRepository tareaRepository;

    @Mock
    private UsuarioService usuarioService;

    @InjectMocks
    private TareaService tareaService;

    @Test
    void crear_UsuarioExistente_GuardaTarea() {
        Usuario usuario = new Usuario(1L, "Ana", "ana@riwitask.com", "1234", true);
        TareaRequestDTO dto = new TareaRequestDTO();
        dto.setTitulo("Diseñar API");
        dto.setEstado(EstadoTarea.PENDIENTE);
        dto.setPrioridad(Prioridad.ALTA);
        dto.setUsuarioId(1L);

        Tarea guardada = new Tarea(1L, "Diseñar API", null, EstadoTarea.PENDIENTE,
                Prioridad.ALTA, LocalDateTime.now(), usuario);

        when(usuarioService.obtenerUsuarioOrThrow(1L)).thenReturn(usuario);
        when(tareaRepository.save(any(Tarea.class))).thenReturn(guardada);

        var resultado = tareaService.crear(dto);

        assertNotNull(resultado);
        assertEquals("Diseñar API", resultado.getTitulo());
        verify(tareaRepository, times(1)).save(any(Tarea.class));
    }

    @Test
    void listarPendientesRecientesPorUsuario_RetornaListaOrdenada() {
        Usuario usuario = new Usuario(1L, "Ana", "ana@riwitask.com", "1234", true);
        Tarea t1 = new Tarea(2L, "Reciente", null, EstadoTarea.PENDIENTE, Prioridad.MEDIA,
                LocalDateTime.now(), usuario);

        when(usuarioService.obtenerUsuarioOrThrow(1L)).thenReturn(usuario);
        when(tareaRepository.findByUsuarioIdAndEstadoOrderByFechaCreacionDesc(1L, EstadoTarea.PENDIENTE))
                .thenReturn(List.of(t1));

        var resultado = tareaService.listarPendientesRecientesPorUsuario(1L);

        assertEquals(1, resultado.size());
        verify(tareaRepository, times(1))
                .findByUsuarioIdAndEstadoOrderByFechaCreacionDesc(1L, EstadoTarea.PENDIENTE);
    }
}