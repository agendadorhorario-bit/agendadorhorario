package com.agendador.usuario;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class UsuarioService {
    private final UsuarioRepository usuarioRepository;

    public UsuarioService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    public Usuario inserir(UsuarioRequest request) {
        return usuarioRepository.inserir(request);
    }

    public Usuario atualizar(Long idUsuario, UsuarioRequest request) {
        Usuario usuarioAtualizado = usuarioRepository.atualizar(idUsuario, request);
        if (usuarioAtualizado == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario nao encontrado");
        }
        return usuarioAtualizado;
    }

    public void deletar(Long idUsuario) {
        boolean deletado = usuarioRepository.deletar(idUsuario);
        if (!deletado) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario nao encontrado");
        }
    }

    public boolean existePorCpf(String cpf) {
        if (cpf == null || cpf.trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "CPF obrigatorio");
        }
        String somenteDigitos = cpf.replaceAll("\\D", "");
        if (somenteDigitos.length() != 11) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "CPF invalido");
        }
        return usuarioRepository.buscarPorCpf(somenteDigitos) != null;
    }

    public Usuario buscarPorCpf(String cpf) {
        if (cpf == null || cpf.trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "CPF obrigatorio");
        }
        String somenteDigitos = cpf.replaceAll("\\D", "");
        if (somenteDigitos.length() != 11) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "CPF invalido");
        }
        Usuario usuario = usuarioRepository.buscarPorCpf(somenteDigitos);
        if (usuario == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario nao encontrado");
        }
        return usuario;
    }

    public Usuario buscarPorId(Long idUsuario) {
        Usuario usuario = usuarioRepository.buscarPorId(idUsuario);
        if (usuario == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario nao encontrado");
        }
        return usuario;
    }
}
