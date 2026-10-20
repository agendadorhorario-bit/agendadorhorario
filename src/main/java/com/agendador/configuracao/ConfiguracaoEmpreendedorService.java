package com.agendador.configuracao;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class ConfiguracaoEmpreendedorService {
    private final ConfiguracaoEmpreendedorRepository configuracaoEmpreendedorRepository;

    public ConfiguracaoEmpreendedorService(ConfiguracaoEmpreendedorRepository configuracaoEmpreendedorRepository) {
        this.configuracaoEmpreendedorRepository = configuracaoEmpreendedorRepository;
    }

    public ConfiguracaoEmpreendedor buscarConfiguracao() {
        ConfiguracaoEmpreendedor configuracao = configuracaoEmpreendedorRepository.buscarConfiguracao();
        if (configuracao == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Configuracao de atendimento nao encontrada.");
        }
        return configuracao;
    }

    public ConfiguracaoEmpreendedor salvarConfiguracao(String horaInicio, String horaFinal, String intervaloAtendimento, Long idEmpreendedor) {
        if (isBlank(horaInicio) || isBlank(horaFinal) || isBlank(intervaloAtendimento)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Preencha hora de inicio, hora final e intervalo.");
        }
        if (idEmpreendedor == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Sessao de administrador invalida. Faca login novamente.");
        }
        return configuracaoEmpreendedorRepository.inserir(
                horaInicio.trim(),
                horaFinal.trim(),
                intervaloAtendimento.trim(),
                idEmpreendedor
        );
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
