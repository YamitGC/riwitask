package com.springboot.riwitask.service;

import com.springboot.riwitask.dto.UsuarioRequestDTO;
import com.springboot.riwitask.dto.UsuarioResponseDTO;
import com.springboot.riwitask.entity.Usuario;
import com.springboot.riwitask.exception.DupicateEmailException;
import com.springboot.riwitask.exception.ResourceNotFoundException;
import com.springboot.riwitask.repository.UsuarioRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;

    public UsuarioService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    public UsuarioResponseDTO crear(UsuarioRequestDTO dto){
        if (usuarioRepository.existsByEmail(dto.getEmail())){
            throw new DupicateEmailException("El correo ya se encuentra registrado: "+ dto.getEmail());
        }
        Usuario usuario = new Usuario();
        usuario.setNombre(dto.getNombre());
        usuario.setEmail(dto.getEmail());
        usuario.setPassword(dto.getPassword());
        usuario.setActivo(dto.getActivo() != null ? dto.getActivo() : true);

        Usuario guardado = usuarioRepository.save(usuario);
        return toResponseDTO(guardado);
    }

    public List<UsuarioResponseDTO> listarTodos(){
        return usuarioRepository.findAll().stream()
                .map(this::toResponseDTO)
                .toList();
    }

    public UsuarioResponseDTO buscarPorId(Long id){
        Usuario usuario = obtenerUsuarioOrThrow(id);
        return toResponseDTO(usuario);
    }

    public UsuarioResponseDTO actualizar(Long id, UsuarioRequestDTO dto){
        Usuario usuario = obtenerUsuarioOrThrow(id);

        if (!usuario.getEmail().equals(dto.getEmail())
            && usuarioRepository.existsByEmail(dto.getEmail())){
            throw new DupicateEmailException("El correo ya se encuentra registrado: "+ dto.getEmail());
        }

        usuario.setNombre(dto.getNombre());
        usuario.setEmail(dto.getEmail());
        usuario.setPassword(dto.getPassword());
        if(dto.getActivo() != null){
            usuario.setActivo(dto.getActivo());
        }

        return toResponseDTO(usuarioRepository.save(usuario));
    }

    public void eliminar(Long id){
        Usuario usuario = obtenerUsuarioOrThrow(id);
        usuarioRepository.delete(usuario);
    }

    // Método interno reutilizado por TareaService para validar el usuario responsable
    public Usuario obtenerUsuarioOrThrow(Long id){
        return usuarioRepository.findById(id)
                .orElseThrow(()-> new ResourceNotFoundException("Usuario no encontrado con id: "+ id));
    }

    private UsuarioResponseDTO toResponseDTO(Usuario usuario){
        return new UsuarioResponseDTO(
                usuario.getId(), usuario.getNombre(), usuario.getEmail(), usuario.getActivo());
    }
}
