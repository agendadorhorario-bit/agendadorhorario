package com.agendador.empreendedor;

import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class EmpreendedorService {
    private final EmpreendedorRepository empreendedorRepository;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public EmpreendedorService(EmpreendedorRepository empreendedorRepository) {
        this.empreendedorRepository = empreendedorRepository;
    }

    public Empreendedor inserir(EmpreendedorRequest request) {
        validarCamposObrigatorios(request);
        request.setSenhaEmpreendedor(passwordEncoder.encode(request.getSenhaEmpreendedor()));
        return empreendedorRepository.inserir(request);
    }

    public List<Empreendedor> listar() {
        return empreendedorRepository.listar();
    }

    public Empreendedor buscarPorId(Long idEmpreendedor) {
        Empreendedor empreendedor = empreendedorRepository.buscarPorId(idEmpreendedor);
        if (empreendedor == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Empreendedor nao encontrado");
        }
        return empreendedor;
    }

    public Empreendedor atualizar(Long idEmpreendedor, EmpreendedorRequest request) {
        validarCamposObrigatorios(request);
        request.setSenhaEmpreendedor(passwordEncoder.encode(request.getSenhaEmpreendedor()));
        Empreendedor empreendedorAtualizado = empreendedorRepository.atualizar(idEmpreendedor, request);
        if (empreendedorAtualizado == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Empreendedor nao encontrado");
        }
        return empreendedorAtualizado;
    }

    public void deletar(Long idEmpreendedor) {
        boolean deletado = empreendedorRepository.deletar(idEmpreendedor);
        if (!deletado) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Empreendedor nao encontrado");
        }
    }

    public Empreendedor autenticarLogin(String usuario, String senha) {
        if (isBlank(usuario) || isBlank(senha)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Usuario e senha sao obrigatorios");
        }

        Empreendedor empreendedor = empreendedorRepository.buscarPorUsuario(usuario.trim());
        if (empreendedor == null || !passwordEncoder.matches(senha, empreendedor.getSenhaEmpreendedor())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Usuario ou senha invalidos");
        }
        return empreendedor;
    }

    private void validarCamposObrigatorios(EmpreendedorRequest request) {
        if (request == null
                || isBlank(request.getNomeEmpreendedor())
                || isBlank(request.getEnderecoEmpreendedor())
                || isBlank(request.getCelularEmpreendedor())
                || isBlank(request.getUsuarioEmpreendedor())
                || isBlank(request.getSenhaEmpreendedor())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "nomeEmpreendedor, enderecoEmpreendedor, celularEmpreendedor, usuarioEmpreendedor e senhaEmpreendedor sao obrigatorios");
        }
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}


