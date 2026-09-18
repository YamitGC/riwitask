package com.springboot.riwitask.controller;

import com.springboot.riwitask.dto.UsuarioRequestDTO;
import com.springboot.riwitask.dto.UsuarioResponseDTO;
import com.springboot.riwitask.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UsuarioResponseDTO crear(@Valid @RequestBody UsuarioRequestDTO dto){
        return usuarioService.crear(dto);
    }

    @GetMapping
    public List<UsuarioResponseDTO> listar(){
        return usuarioService.listarTodos();
    }

    @GetMapping("/{id}")
    public UsuarioResponseDTO buscarPorId(@PathVariable Long id){
        return usuarioService.buscarPorId(id);
    }

    @PutMapping("/{id}")
    public UsuarioResponseDTO actualizar(@PathVariable Long id, @Valid @RequestBody UsuarioRequestDTO dto) {
        return usuarioService.actualizar(id, dto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable Long id){
        usuarioService.eliminar(id);
    }
}
