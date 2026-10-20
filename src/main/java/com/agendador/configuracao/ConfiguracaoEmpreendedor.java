package com.agendador.configuracao;

public class ConfiguracaoEmpreendedor {
    private final Long idEmpreendedor;
    private final String horaInicio;
    private final String horaFinal;
    private final String intervaloAtendimento;

    public ConfiguracaoEmpreendedor(Long idEmpreendedor, String horaInicio, String horaFinal, String intervaloAtendimento) {
        this.idEmpreendedor = idEmpreendedor;
        this.horaInicio = horaInicio;
        this.horaFinal = horaFinal;
        this.intervaloAtendimento = intervaloAtendimento;
    }

    public Long getIdEmpreendedor() {
        return idEmpreendedor;
    }

    public String getHoraInicio() {
        return horaInicio;
    }

    public String getHoraFinal() {
        return horaFinal;
    }

    public String getIntervaloAtendimento() {
        return intervaloAtendimento;
    }
}
