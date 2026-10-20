package com.agendador.usuario;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/usuarios")
@Tag(name = "Usuarios", description = "Operacoes de insercao, edicao e delecao da tabela usuario")
public class UsuarioController {
    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @PostMapping
    @Operation(summary = "Inserir usuario")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Usuario inserido com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados invalidos")
    })
    public ResponseEntity<Usuario> inserir(@Valid @RequestBody UsuarioRequest request) {
        Usuario usuario = usuarioService.inserir(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(usuario);
    }

    @PutMapping("/{idUsuario}")
    @Operation(summary = "Editar usuario por id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Usuario atualizado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados invalidos"),
            @ApiResponse(responseCode = "404", description = "Usuario nao encontrado")
    })
    public Usuario atualizar(@PathVariable Long idUsuario, @Valid @RequestBody UsuarioRequest request) {
        return usuarioService.atualizar(idUsuario, request);
    }

    @DeleteMapping("/{idUsuario}")
    @Operation(summary = "Deletar usuario por id")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Usuario removido com sucesso"),
            @ApiResponse(responseCode = "404", description = "Usuario nao encontrado")
    })
    public ResponseEntity<Void> deletar(@PathVariable Long idUsuario) {
        usuarioService.deletar(idUsuario);
        return ResponseEntity.noContent().build();
    }
}

