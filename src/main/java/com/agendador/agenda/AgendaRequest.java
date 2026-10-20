package com.agendador.agenda;

public class AgendaRequest {
    private Long idUsuario;
    private Long idEmpreendedor;
    private String horarioAgenda;
    private String dataAgenda;
    private String diaDesabilitado;
    private Boolean agendadoUsuario;
    private Boolean agendadoEmpreendedor;

    public Long getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(Long idUsuario) {
        this.idUsuario = idUsuario;
    }

    public Long getIdEmpreendedor() {
        return idEmpreendedor;
    }

    public void setIdEmpreendedor(Long idEmpreendedor) {
        this.idEmpreendedor = idEmpreendedor;
    }

    public String getHorarioAgenda() {
        return horarioAgenda;
    }

    public void setHorarioAgenda(String horarioAgenda) {
        this.horarioAgenda = horarioAgenda;
    }

    public String getDataAgenda() {
        return dataAgenda;
    }

    public void setDataAgenda(String dataAgenda) {
        this.dataAgenda = dataAgenda;
    }

    public String getDiaDesabilitado() {
        return diaDesabilitado;
    }

    public void setDiaDesabilitado(String diaDesabilitado) {
        this.diaDesabilitado = diaDesabilitado;
    }

    public Boolean getAgendadoUsuario() {
        return agendadoUsuario;
    }

    public void setAgendadoUsuario(Boolean agendadoUsuario) {
        this.agendadoUsuario = agendadoUsuario;
    }

    public Boolean getAgendadoEmpreendedor() {
        return agendadoEmpreendedor;
    }

    public void setAgendadoEmpreendedor(Boolean agendadoEmpreendedor) {
        this.agendadoEmpreendedor = agendadoEmpreendedor;
    }
}
