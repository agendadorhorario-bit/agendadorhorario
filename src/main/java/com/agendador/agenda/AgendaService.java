package com.agendador.agenda;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class AgendaService {
    private final AgendaRepository agendaRepository;

    public AgendaService(AgendaRepository agendaRepository) {
        this.agendaRepository = agendaRepository;
    }

    public Agenda inserir(AgendaRequest request) {
        return agendaRepository.inserir(request);
    }

    public List<Agenda> listar() {
        return agendaRepository.listar();
    }

    public List<Agenda> listarPorData(String dataAgenda) {
        return agendaRepository.listarPorData(dataAgenda);
    }

    public Agenda buscarPorId(Long idAgenda) {
        Agenda agenda = agendaRepository.buscarPorId(idAgenda);
        if (agenda == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Agenda nao encontrada");
        }
        return agenda;
    }

    public Agenda atualizar(Long idAgenda, AgendaRequest request) {
        Agenda agendaAtualizada = agendaRepository.atualizar(idAgenda, request);
        if (agendaAtualizada == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Agenda nao encontrada");
        }
        return agendaAtualizada;
    }

    public void deletar(Long idAgenda) {
        boolean deletado = agendaRepository.deletar(idAgenda);
        if (!deletado) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Agenda nao encontrada");
        }
    }

    public void validarDisponibilidade(String dataAgenda, String horarioAgenda) {
        if (agendaRepository.existePorDataEHorario(dataAgenda, horarioAgenda)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Horario ja esta agendado.");
        }
    }

    public Agenda confirmarEmpreendedor(Long idAgenda, Long idEmpreendedor) {
        Agenda agenda = buscarPorId(idAgenda);
        AgendaRequest request = toRequest(agenda);
        request.setIdEmpreendedor(idEmpreendedor);
        request.setAgendadoEmpreendedor(true);
        return atualizar(idAgenda, request);
    }

    public Agenda cancelarEmpreendedor(Long idAgenda, Long idEmpreendedor) {
        Agenda agenda = buscarPorId(idAgenda);
        AgendaRequest request = toRequest(agenda);
        request.setIdEmpreendedor(idEmpreendedor);
        request.setAgendadoEmpreendedor(false);
        return atualizar(idAgenda, request);
    }

    public Agenda desmarcarHorario(Long idAgenda, Long idEmpreendedor) {
        Agenda agenda = buscarPorId(idAgenda);
        AgendaRequest request = toRequest(agenda);
        request.setIdEmpreendedor(idEmpreendedor);
        request.setAgendadoUsuario(false);
        request.setAgendadoEmpreendedor(false);
        return atualizar(idAgenda, request);
    }

    public Set<String> listarDiasDesabilitados() {
        return new HashSet<String>(agendaRepository.listarDiasDesabilitados());
    }

    public void desabilitarDia(String dia) {
        agendaRepository.desabilitarDia(dia);
    }

    public void habilitarDia(String dia) {
        agendaRepository.habilitarDia(dia);
    }

    private AgendaRequest toRequest(Agenda agenda) {
        AgendaRequest request = new AgendaRequest();
        request.setIdUsuario(agenda.getIdUsuario());
        request.setIdEmpreendedor(agenda.getIdEmpreendedor());
        request.setHorarioAgenda(agenda.getHorarioAgenda());
        request.setDataAgenda(agenda.getDataAgenda());
        request.setDiaDesabilitado(agenda.getDiaDesabilitado());
        request.setAgendadoUsuario(agenda.getAgendadoUsuario());
        request.setAgendadoEmpreendedor(agenda.getAgendadoEmpreendedor());
        return request;
    }
}
