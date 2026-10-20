package com.agendador.empreendedor;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/empreendedores")
@Tag(name = "Empreendedores", description = "Operacoes de insercao, consulta, edicao e delecao da tabela empreendedor")
public class EmpreendedorController {
    private final EmpreendedorService empreendedorService;

    public EmpreendedorController(EmpreendedorService empreendedorService) {
        this.empreendedorService = empreendedorService;
    }

    @PostMapping
    @Operation(summary = "Inserir empreendedor")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Empreendedor inserido com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados invalidos")
    })
    public ResponseEntity<Empreendedor> inserir(@Valid @RequestBody EmpreendedorRequest request) {
        Empreendedor empreendedor = empreendedorService.inserir(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(empreendedor);
    }

    @GetMapping
    @Operation(summary = "Listar empreendedores")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso")
    })
    public List<Empreendedor> listar() {
        return empreendedorService.listar();
    }

    @GetMapping("/{idEmpreendedor}")
    @Operation(summary = "Buscar empreendedor por id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Empreendedor encontrado"),
            @ApiResponse(responseCode = "404", description = "Empreendedor nao encontrado")
    })
    public Empreendedor buscarPorId(@PathVariable Long idEmpreendedor) {
        return empreendedorService.buscarPorId(idEmpreendedor);
    }

    @PutMapping("/{idEmpreendedor}")
    @Operation(summary = "Editar empreendedor por id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Empreendedor atualizado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados invalidos"),
            @ApiResponse(responseCode = "404", description = "Empreendedor nao encontrado")
    })
    public Empreendedor atualizar(@PathVariable Long idEmpreendedor, @Valid @RequestBody EmpreendedorRequest request) {
        return empreendedorService.atualizar(idEmpreendedor, request);
    }

    @DeleteMapping("/{idEmpreendedor}")
    @Operation(summary = "Deletar empreendedor por id")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Empreendedor removido com sucesso"),
            @ApiResponse(responseCode = "404", description = "Empreendedor nao encontrado")
    })
    public ResponseEntity<Void> deletar(@PathVariable Long idEmpreendedor) {
        empreendedorService.deletar(idEmpreendedor);
        return ResponseEntity.noContent().build();
    }
}

