package com.springboot.riwitask.service;

import com.springboot.riwitask.dto.UsuarioRequestDTO;
import com.springboot.riwitask.entity.Usuario;
import com.springboot.riwitask.exception.DupicateEmailException;
import com.springboot.riwitask.exception.ResourceNotFoundException;
import com.springboot.riwitask.repository.UsuarioRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private UsuarioService usuarioService;

    @Test
    void crear_EmailDuplicado_LanzaExcepcion() {
        UsuarioRequestDTO dto = new UsuarioRequestDTO();
        dto.setNombre("Ana");
        dto.setEmail("ana@riwitask.com");
        dto.setPassword("1234");

        when(usuarioRepository.existsByEmail("ana@riwitask.com")).thenReturn(true);

        assertThrows(DupicateEmailException.class, () -> usuarioService.crear(dto));
        verify(usuarioRepository, never()).save(any());
    }

    @Test
    void obtenerUsuarioOrThrow_UsuarioInexistente_LanzaExcepcion() {
        when(usuarioRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> usuarioService.obtenerUsuarioOrThrow(99L));
    }

    @Test
    void crear_DatosValidos_RetornaUsuarioGuardado() {
        UsuarioRequestDTO dto = new UsuarioRequestDTO();
        dto.setNombre("Ana");
        dto.setEmail("ana@riwitask.com");
        dto.setPassword("1234");

        Usuario guardado = new Usuario(1L, "Ana", "ana@riwitask.com", "1234", true);

        when(usuarioRepository.existsByEmail("ana@riwitask.com")).thenReturn(false);
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(guardado);

        var resultado = usuarioService.crear(dto);

        assertNotNull(resultado);
        assertEquals("Ana", resultado.getNombre());
        verify(usuarioRepository, times(1)).save(any(Usuario.class));
    }
}
