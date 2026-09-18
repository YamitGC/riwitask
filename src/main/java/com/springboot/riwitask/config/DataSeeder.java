package com.springboot.riwitask.config;

import com.springboot.riwitask.entity.Tarea;
import com.springboot.riwitask.entity.Usuario;
import com.springboot.riwitask.enums.EstadoTarea;
import com.springboot.riwitask.enums.Prioridad;
import com.springboot.riwitask.repository.TareaRepository;
import com.springboot.riwitask.repository.UsuarioRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class DataSeeder implements CommandLineRunner {

    private final UsuarioRepository usuarioRepository;
    private final TareaRepository tareaRepository;

    public DataSeeder(UsuarioRepository usuarioRepository, TareaRepository tareaRepository) {
        this.usuarioRepository = usuarioRepository;
        this.tareaRepository = tareaRepository;
    }

    @Override
    public void run(String... args) {
        // Solo sembrar datos si la tabla de usuarios está vacía
        if (usuarioRepository.count() > 0) {
            return;
        }

        // 1. Usuarios iniciales
        Usuario u1 = new Usuario(null, "Ana Torres", "ana.torres@riwitask.com", "pass1234", true);
        Usuario u2 = new Usuario(null, "Carlos Mendoza", "carlos.m@riwitask.com", "pass1234", true);
        Usuario u3 = new Usuario(null, "Sofia Ramirez", "sofia.r@riwitask.com", "pass1234", false);

        usuarioRepository.saveAll(List.of(u1, u2, u3));

        // 2. Tareas iniciales (variando estados, fechas y prioridades para probar las derived queries)
        LocalDateTime ahora = LocalDateTime.now();

        Tarea t1 = new Tarea(null, "Diseñar arquitectura REST", "Definir endpoints y DTOs principales",
                EstadoTarea.COMPLETADA, Prioridad.ALTA, ahora.minusDays(3), u1);

        Tarea t2 = new Tarea(null, "Configurar base de datos PostgreSQL", "Crear tablas e índices requeridos",
                EstadoTarea.EN_PROCESO, Prioridad.MEDIA, ahora.minusDays(2), u1);

        // Tareas pendientes para probar la consulta especial de pendientes recientes
        Tarea t3 = new Tarea(null, "Implementar autenticación JWT", "Preparar seguridad para siguiente sprint",
                EstadoTarea.PENDIENTE, Prioridad.ALTA, ahora.minusHours(4), u1);

        Tarea t4 = new Tarea(null, "Crear documentación Swagger", "Detallar respuestas y esquemas OpenAPI",
                EstadoTarea.PENDIENTE, Prioridad.MEDIA, ahora.minusHours(1), u1);

        Tarea t5 = new Tarea(null, "Diseñar interfaz de usuario", "Bocetos de vistas principales",
                EstadoTarea.PENDIENTE, Prioridad.BAJA, ahora.minusDays(1), u2);

        tareaRepository.saveAll(List.of(t1, t2, t3, t4, t5));

        System.out.println(">>> [DataSeeder] Datos iniciales cargados con éxito para pruebas.");
    }
}