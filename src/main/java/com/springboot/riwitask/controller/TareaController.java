package com.springboot.riwitask.controller;

import com.springboot.riwitask.dto.TareaRequestDTO;
import com.springboot.riwitask.dto.TareaResponseDTO;
import com.springboot.riwitask.enums.EstadoTarea;
import com.springboot.riwitask.enums.Prioridad;
import com.springboot.riwitask.service.TareaService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tareas")
public class TareaController {

    private TareaService tareaService;

    public TareaController(TareaService tareaService) {
        this.tareaService = tareaService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TareaResponseDTO crear(@Valid @RequestBody TareaRequestDTO dto){
        return tareaService.crear(dto);
    }

    @GetMapping
    public List<TareaResponseDTO> listar(){
        return tareaService.listarTodas();
    }

    @GetMapping("/{id}")
    public TareaResponseDTO buscarPorId(@PathVariable Long id){
        return tareaService.buscarPorId(id);
    }

    @PutMapping("/{id}")
    public TareaResponseDTO actualizar(@PathVariable Long id, @Valid @RequestBody TareaRequestDTO dto){
        return tareaService.actualizar(id, dto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable Long id){
        tareaService.eliminar(id);
    }

    // Consultas de seguimiento

    @GetMapping("/usuario/{usuarioId}")
    public List<TareaResponseDTO> porUsuario(@PathVariable Long usuarioId) {
        return tareaService.listarPorUsuario(usuarioId);
    }

    @GetMapping("/estado/{estado}")
    public List<TareaResponseDTO> porEstado(@PathVariable EstadoTarea estado) {
        return tareaService.listarPorEstado(estado);
    }

    @GetMapping("/prioridad/{prioridad}")
    public List<TareaResponseDTO> porPrioridad(@PathVariable Prioridad prioridad) {
        return tareaService.listarPorPrioridad(prioridad);
    }

    @GetMapping("/usuario/{usuarioId}/estado/{estado}")
    public List<TareaResponseDTO> porUsuarioYEstado(@PathVariable Long usuarioId,
                                                    @PathVariable EstadoTarea estado) {
        return tareaService.listarPorUsuarioYEstado(usuarioId, estado);
    }

    // Requerimiento especial
    @GetMapping("/usuario/{usuarioId}/pendientes-recientes")
    public List<TareaResponseDTO> pendientesRecientesPorUsuario(@PathVariable Long usuarioId) {
        return tareaService.listarPendientesRecientesPorUsuario(usuarioId);
    }
}
