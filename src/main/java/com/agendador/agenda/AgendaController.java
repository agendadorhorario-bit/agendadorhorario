package com.agendador.agenda;

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
@RequestMapping("/agendas")
@Tag(name = "Agendas", description = "Operacoes de insercao, consulta, edicao e delecao da tabela agenda")
public class AgendaController {
    private final AgendaService agendaService;

    public AgendaController(AgendaService agendaService) {
        this.agendaService = agendaService;
    }

    @PostMapping
    @Operation(summary = "Inserir agenda")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Agenda inserida com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados invalidos")
    })
    public ResponseEntity<Agenda> inserir(@Valid @RequestBody AgendaRequest request) {
        Agenda agenda = agendaService.inserir(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(agenda);
    }

    @GetMapping
    @Operation(summary = "Listar agendas")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso")
    })
    public List<Agenda> listar() {
        return agendaService.listar();
    }

    @GetMapping("/{idAgenda}")
    @Operation(summary = "Buscar agenda por id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Agenda encontrada"),
            @ApiResponse(responseCode = "404", description = "Agenda nao encontrada")
    })
    public Agenda buscarPorId(@PathVariable Long idAgenda) {
        return agendaService.buscarPorId(idAgenda);
    }

    @PutMapping("/{idAgenda}")
    @Operation(summary = "Editar agenda por id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Agenda atualizada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados invalidos"),
            @ApiResponse(responseCode = "404", description = "Agenda nao encontrada")
    })
    public Agenda atualizar(@PathVariable Long idAgenda, @Valid @RequestBody AgendaRequest request) {
        return agendaService.atualizar(idAgenda, request);
    }

    @DeleteMapping("/{idAgenda}")
    @Operation(summary = "Deletar agenda por id")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Agenda removida com sucesso"),
            @ApiResponse(responseCode = "404", description = "Agenda nao encontrada")
    })
    public ResponseEntity<Void> deletar(@PathVariable Long idAgenda) {
        agendaService.deletar(idAgenda);
        return ResponseEntity.noContent().build();
    }
}

