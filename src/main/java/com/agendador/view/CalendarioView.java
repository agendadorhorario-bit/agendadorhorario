package com.agendador.view;

import com.agendador.agenda.Agenda;
import com.agendador.agenda.AgendaRequest;
import com.agendador.agenda.AgendaService;
import com.agendador.configuracao.ConfiguracaoEmpreendedor;
import com.agendador.configuracao.ConfiguracaoEmpreendedorService;
import com.agendador.usuario.Usuario;
import com.agendador.usuario.UsuarioService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Route("calendario")
@PageTitle("Calendario")
public class CalendarioView extends VerticalLayout implements BeforeEnterObserver {

    private static final Locale LOCALE_PT_BR = new Locale("pt", "BR");
    private static final Pattern PATTERN_NUMERO = Pattern.compile("(\\d+)");
    private static final DateTimeFormatter HORA_HH_MM = DateTimeFormatter.ofPattern("HH:mm");

    private final ConfiguracaoEmpreendedorService configuracaoEmpreendedorService;
    private final UsuarioService usuarioService;
    private final AgendaService agendaService;

    public CalendarioView(
            ConfiguracaoEmpreendedorService configuracaoEmpreendedorService,
            UsuarioService usuarioService,
            AgendaService agendaService
    ) {
        this.configuracaoEmpreendedorService = configuracaoEmpreendedorService;
        this.usuarioService = usuarioService;
        this.agendaService = agendaService;

        LocalDate hoje = LocalDate.now();
        YearMonth mesAtual = YearMonth.from(hoje);
        boolean adminAutenticado = isAdminAutenticado();

        setSizeFull();
        setPadding(false);
        setSpacing(false);
        getStyle().set("background", "linear-gradient(180deg, #f4f8ff 0%, #ffffff 100%)");

        HorizontalLayout layout = new HorizontalLayout();
        layout.setSizeFull();
        layout.setPadding(false);
        layout.setSpacing(false);

        Div calendarioCard = buildCalendarioCard(mesAtual, hoje, adminAutenticado);
        if (adminAutenticado) {
            layout.add(buildSidebar(), calendarioCard);
        } else {
            layout.add(calendarioCard);
        }
        layout.expand(calendarioCard);

        add(layout);
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        if (!isAdminAutenticado() && !isAcessoDiretoCalendario()) {
            event.rerouteTo("login");
        }
    }

    private boolean isAdminAutenticado() {
        Object autenticado = VaadinSession.getCurrent().getAttribute("adminAutenticado");
        return autenticado instanceof Boolean && (Boolean) autenticado;
    }

    private boolean isAcessoDiretoCalendario() {
        Object acessoDireto = VaadinSession.getCurrent().getAttribute("acessoDiretoCalendario");
        return acessoDireto instanceof Boolean && (Boolean) acessoDireto;
    }

    private Div buildSidebar() {
        Div sidebar = new Div();
        sidebar.getStyle().set("width", "220px");
        sidebar.getStyle().set("background", "linear-gradient(160deg,#0b4ebd,#073683)");
        sidebar.getStyle().set("color", "#ffffff");
        sidebar.getStyle().set("padding", "24px 16px");
        sidebar.getStyle().set("box-sizing", "border-box");

        H3 title = new H3("Agenda");
        title.getStyle().set("margin", "0 0 20px 0");

        sidebar.add(title,
                menuItem("Calendario", true, "calendario"),
                menuItem("Tarefas", false, null),
                menuItem("Contatos", false, null),
                menuItem("Configuracoes", false, "configuracoes"));
        return sidebar;
    }

    private Div menuItem(String label, boolean ativo, String rota) {
        Div item = new Div();
        item.setText(label);
        item.getStyle().set("padding", "10px 12px");
        item.getStyle().set("border-radius", "10px");
        item.getStyle().set("margin-bottom", "8px");
        item.getStyle().set("font-weight", "600");
        if (rota != null) {
            item.getStyle().set("cursor", "pointer");
            item.addClickListener(event -> getUI().ifPresent(ui -> ui.navigate(rota)));
        }
        if (ativo) {
            item.getStyle().set("background", "rgba(255,255,255,0.14)");
        }
        return item;
    }

    private Div buildCalendarioCard(YearMonth mesAtual, LocalDate hoje, boolean adminAutenticado) {
        Div card = new Div();
        card.getStyle().set("flex", "1");
        card.getStyle().set("margin", "12px");
        card.getStyle().set("background", "#ffffff");
        card.getStyle().set("border-radius", "18px");
        card.getStyle().set("padding", "20px");
        card.getStyle().set("box-shadow", "0 10px 35px rgba(24,56,120,0.12)");

        H2 tituloPagina = new H2("Calendario");
        tituloPagina.getStyle().set("margin", "0 0 8px 0");

        Button sair = new Button("Sair");
        sair.addThemeVariants(ButtonVariant.LUMO_TERTIARY_INLINE);
        sair.addClickListener(event -> {
            boolean admin = isAdminAutenticado();
            VaadinSession.getCurrent().setAttribute("adminAutenticado", false);
            VaadinSession.getCurrent().setAttribute("acessoDiretoCalendario", false);
            VaadinSession.getCurrent().setAttribute("idEmpreendedorLogado", null);
            getUI().ifPresent(ui -> ui.navigate(admin ? "login" : ""));
        });

        HorizontalLayout topo = new HorizontalLayout(tituloPagina, sair);
        topo.setWidthFull();
        topo.setPadding(false);
        topo.setSpacing(true);
        topo.setJustifyContentMode(JustifyContentMode.BETWEEN);
        topo.setAlignItems(Alignment.CENTER);

        H3 tituloMes = new H3(formatarMesAno(mesAtual));
        tituloMes.getStyle().set("margin", "0 0 16px 0");

        HorizontalLayout cabecalhoDias = new HorizontalLayout();
        cabecalhoDias.setWidthFull();
        cabecalhoDias.setPadding(false);
        cabecalhoDias.setSpacing(false);
        String[] dias = {"Dom", "Seg", "Ter", "Qua", "Qui", "Sex", "Sab"};
        for (String dia : dias) {
            Div d = new Div();
            d.setText(dia);
            d.getStyle().set("flex", "1");
            d.getStyle().set("text-align", "center");
            d.getStyle().set("font-weight", "600");
            d.getStyle().set("color", "#5f6f91");
            d.getStyle().set("padding", "6px 0");
            cabecalhoDias.add(d);
        }

        Div grade = new Div();
        grade.getStyle().set("display", "grid");
        grade.getStyle().set("grid-template-columns", "repeat(7, minmax(0, 1fr))");
        grade.getStyle().set("gap", "8px");
        Set<LocalDate> diasDesabilitados = carregarDiasDesabilitados();

        int deslocamento = converterParaInicioDomingo(mesAtual.atDay(1).getDayOfWeek());
        int diasNoMes = mesAtual.lengthOfMonth();
        int diasMesAnterior = mesAtual.minusMonths(1).lengthOfMonth();

        for (int i = 0; i < 42; i++) {
            Div celula = new Div();
            celula.getStyle().set("min-height", "64px");
            celula.getStyle().set("border", "1px solid #e7ecf7");
            celula.getStyle().set("border-radius", "10px");
            celula.getStyle().set("display", "flex");
            celula.getStyle().set("align-items", "flex-start");
            celula.getStyle().set("justify-content", "flex-start");
            celula.getStyle().set("padding", "10px");

            int numeroDia;
            boolean outroMes = false;
            if (i < deslocamento) {
                numeroDia = (diasMesAnterior - deslocamento + 1) + i;
                outroMes = true;
            } else if (i >= deslocamento + diasNoMes) {
                numeroDia = i - (deslocamento + diasNoMes) + 1;
                outroMes = true;
            } else {
                numeroDia = i - deslocamento + 1;
            }

            Div textoDia = new Div();
            textoDia.setText(String.valueOf(numeroDia));
            textoDia.getStyle().set("font-weight", "600");
            textoDia.getStyle().set("color", outroMes ? "#b5c1d8" : "#0d2149");

            if (!outroMes && numeroDia == hoje.getDayOfMonth()) {
                textoDia.getStyle().set("background", "#0b5fff");
                textoDia.getStyle().set("color", "#ffffff");
                textoDia.getStyle().set("width", "32px");
                textoDia.getStyle().set("height", "32px");
                textoDia.getStyle().set("border-radius", "50%");
                textoDia.getStyle().set("display", "inline-flex");
                textoDia.getStyle().set("align-items", "center");
                textoDia.getStyle().set("justify-content", "center");
            }

            if (!outroMes) {
                LocalDate dataSelecionada = mesAtual.atDay(numeroDia);
                boolean diaDesabilitado = diasDesabilitados.contains(dataSelecionada);
                if (diaDesabilitado) {
                    celula.getStyle().set("background", "#edf1f7");
                    celula.getStyle().set("border-color", "#d8e0ee");
                    textoDia.getStyle().set("color", "#95a3bb");
                }
                if (adminAutenticado || !diaDesabilitado) {
                    celula.getStyle().set("cursor", "pointer");
                    celula.addClickListener(event -> exibirAgendaDoDiaModal(dataSelecionada, adminAutenticado));
                } else {
                    celula.getStyle().set("cursor", "not-allowed");
                }
            }

            celula.add(textoDia);
            grade.add(celula);
        }

        card.add(topo, tituloMes, cabecalhoDias, grade);
        return card;
    }

    private void exibirAgendaDoDiaModal(LocalDate dataSelecionada, boolean adminAutenticado) {
        try {
            ConfiguracaoEmpreendedor configuracao = configuracaoEmpreendedorService.buscarConfiguracao();
            LocalTime horaInicio = parseHora(configuracao.getHoraInicio());
            LocalTime horaFinal = parseHora(configuracao.getHoraFinal());
            int intervaloMinutos = extrairIntervaloEmMinutos(configuracao.getIntervaloAtendimento());
            List<LocalTime> horarios = gerarHorarios(horaInicio, horaFinal, intervaloMinutos);
            boolean diaDesabilitado = carregarDiasDesabilitados().contains(dataSelecionada);

            Dialog modal = new Dialog();
            modal.setHeaderTitle("Horarios do dia");
            modal.setWidth("min(1250px, 96vw)");
            modal.setMaxWidth("96vw");
            modal.setModal(true);
            modal.setCloseOnEsc(true);
            modal.setCloseOnOutsideClick(true);
            modal.add(buildAgendaDiaCard(dataSelecionada, horarios, configuracao.getIdEmpreendedor(), adminAutenticado, diaDesabilitado, modal));
            modal.open();
        } catch (ResponseStatusException ex) {
            Notification notification = Notification.show(ex.getReason() != null
                    ? ex.getReason()
                    : "Erro ao carregar configuracao de atendimento.");
            notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
        }
    }

    private Div buildAgendaDiaCard(
            LocalDate dataSelecionada,
            List<LocalTime> horarios,
            Long idEmpreendedorConfigurado,
            boolean adminAutenticado,
            boolean diaDesabilitado,
            Dialog modalPai
    ) {
        Div card = new Div();
        card.getStyle().set("border", "1px solid #d9e7ff");
        card.getStyle().set("border-radius", "14px");
        card.getStyle().set("padding", "16px");
        card.getStyle().set("margin-top", "8px");

        Div iconeBox = new Div();
        iconeBox.getStyle().set("width", "56px");
        iconeBox.getStyle().set("height", "56px");
        iconeBox.getStyle().set("display", "inline-flex");
        iconeBox.getStyle().set("align-items", "center");
        iconeBox.getStyle().set("justify-content", "center");
        iconeBox.getStyle().set("border-radius", "12px");
        iconeBox.getStyle().set("background", "linear-gradient(180deg,#e9f2ff,#dceafe)");
        Icon calendarIcon = VaadinIcon.CALENDAR.create();
        calendarIcon.setSize("30px");
        calendarIcon.getStyle().set("color", "#2a62d8");
        iconeBox.add(calendarIcon);

        H3 tituloData = new H3(formatarDataCompleta(dataSelecionada));
        tituloData.getStyle().set("margin", "0");

        HorizontalLayout header = new HorizontalLayout(iconeBox, tituloData);
        header.setAlignItems(FlexComponent.Alignment.CENTER);
        header.setSpacing(true);
        header.getStyle().set("margin-bottom", "12px");
        if (adminAutenticado) {
            Button cancelarDia = new Button("Cancelar o dia");
            cancelarDia.addThemeVariants(ButtonVariant.LUMO_ERROR);
            cancelarDia.setEnabled(!diaDesabilitado);
            cancelarDia.addClickListener(event -> {
                agendaService.desabilitarDia(dataSelecionada.toString());
                Notification notification = Notification.show("Dia cancelado com sucesso.");
                notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
                modalPai.close();
                getUI().ifPresent(ui -> ui.navigate("calendario"));
            });

            Button habilitarDia = new Button("Habilitar o dia");
            habilitarDia.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
            habilitarDia.setEnabled(diaDesabilitado);
            habilitarDia.addClickListener(event -> {
                agendaService.habilitarDia(dataSelecionada.toString());
                Notification notification = Notification.show("Dia habilitado com sucesso.");
                notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
                modalPai.close();
                getUI().ifPresent(ui -> ui.navigate("calendario"));
            });

            header.add(cancelarDia, habilitarDia);
            header.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
            header.setWidthFull();
        }

        Div tabela = new Div();
        tabela.getStyle().set("border", "1px solid #d9e7ff");
        tabela.getStyle().set("border-radius", "12px");
        tabela.getStyle().set("overflow", "hidden");
        tabela.add(buildCabecalhoTabela(adminAutenticado));

        Map<String, Agenda> agendasPorHorario = carregarAgendasPorHorario(dataSelecionada);
        for (LocalTime horario : horarios) {
            String horarioTexto = horario.format(HORA_HH_MM);
            Agenda agenda = agendasPorHorario.get(horarioTexto);
            tabela.add(buildLinhaHorario(
                    dataSelecionada,
                    horarioTexto,
                    agenda,
                    idEmpreendedorConfigurado,
                    adminAutenticado,
                    diaDesabilitado,
                    modalPai
            ));
        }

        card.add(header, tabela);
        return card;
    }

    private Map<String, Agenda> carregarAgendasPorHorario(LocalDate dataSelecionada) {
        List<Agenda> agendas = agendaService.listarPorData(dataSelecionada.toString());
        Map<String, Agenda> porHorario = new HashMap<String, Agenda>();
        for (Agenda agenda : agendas) {
            if (agenda.getHorarioAgenda() == null) {
                continue;
            }
            Agenda existente = porHorario.get(agenda.getHorarioAgenda());
            if (existente == null) {
                porHorario.put(agenda.getHorarioAgenda(), agenda);
                continue;
            }

            boolean atualMarcado = Boolean.TRUE.equals(agenda.getAgendadoUsuario());
            boolean existenteMarcado = Boolean.TRUE.equals(existente.getAgendadoUsuario());
            if (atualMarcado && !existenteMarcado) {
                porHorario.put(agenda.getHorarioAgenda(), agenda);
                continue;
            }
            if (atualMarcado == existenteMarcado
                    && agenda.getIdAgenda() != null
                    && (existente.getIdAgenda() == null || agenda.getIdAgenda() > existente.getIdAgenda())) {
                porHorario.put(agenda.getHorarioAgenda(), agenda);
            }
        }
        return porHorario;
    }

    private HorizontalLayout buildCabecalhoTabela(boolean adminAutenticado) {
        List<String> colunas = new ArrayList<String>();
        colunas.add("Horario Agendado");
        colunas.add("Nome");
        colunas.add("Marcado");
        colunas.add("Confirmado");
        colunas.add(adminAutenticado ? "Caixa de acoes" : "Acoes");
        return buildLinhaTabela(colunas, true);
    }

    private HorizontalLayout buildLinhaHorario(
            LocalDate dataSelecionada,
            String horarioTexto,
            Agenda agenda,
            Long idEmpreendedorConfigurado,
            boolean adminAutenticado,
            boolean diaDesabilitado,
            Dialog modalPai
    ) {
        String nomeUsuario = "-";
        boolean possuiSolicitacaoUsuario = agenda != null && Boolean.TRUE.equals(agenda.getAgendadoUsuario());
        boolean marcado = possuiSolicitacaoUsuario;
        if (possuiSolicitacaoUsuario && agenda.getIdUsuario() != null) {
            try {
                Usuario usuario = usuarioService.buscarPorId(agenda.getIdUsuario());
                if (usuario.getNomeUsuario() != null && !usuario.getNomeUsuario().isBlank()) {
                    nomeUsuario = usuario.getNomeUsuario();
                }
            } catch (ResponseStatusException ignored) {
                nomeUsuario = "-";
            }
        }

        Div c1 = createCell(horarioTexto, "1.05", true);
        Div c2 = createCell(nomeUsuario, "1.2", true);

        Div c3 = createContainerCell("0.42");
        Checkbox marcadoCheck = new Checkbox();
        marcadoCheck.setEnabled(false);
        marcadoCheck.setValue(marcado);
        c3.add(marcadoCheck);

        Div c4 = createContainerCell("0.5");
        if (adminAutenticado) {
            Checkbox confirmadoCheck = new Checkbox();
            boolean confirmado = agenda != null && Boolean.TRUE.equals(agenda.getAgendadoEmpreendedor());
            confirmadoCheck.setValue(confirmado);
            confirmadoCheck.setEnabled(marcado);
            confirmadoCheck.addValueChangeListener(event -> {
                if (!event.isFromClient() || agenda == null) {
                    return;
                }
                Long idEmpreendedor = resolverIdEmpreendedor(idEmpreendedorConfigurado);
                try {
                    if (event.getValue()) {
                        agendaService.confirmarEmpreendedor(agenda.getIdAgenda(), idEmpreendedor);
                    } else {
                        agendaService.cancelarEmpreendedor(agenda.getIdAgenda(), idEmpreendedor);
                    }
                    modalPai.close();
                    exibirAgendaDoDiaModal(dataSelecionada, true);
                } catch (ResponseStatusException ex) {
                    Notification notification = Notification.show(ex.getReason() != null ? ex.getReason() : "Erro ao atualizar confirmacao.");
                    notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
                }
            });
            c4.add(confirmadoCheck);
        } else {
            String status = "-";
            if (possuiSolicitacaoUsuario) {
                Boolean confirmadoEmpreendedor = agenda.getAgendadoEmpreendedor();
                boolean possuiRespostaAdmin = agenda.getIdEmpreendedor() != null;
                if (Boolean.TRUE.equals(confirmadoEmpreendedor)) {
                    status = "✅";
                } else if (Boolean.FALSE.equals(confirmadoEmpreendedor) && possuiRespostaAdmin) {
                    status = "❌";
                } else {
                    status = "aguardando";
                }
            }
            Div confirmadoTexto = new Div(status);
            c4.add(confirmadoTexto);
        }

        Div c5 = createContainerCell("0.6");
        if (adminAutenticado) {
            Button acaoAdmin = new Button(VaadinIcon.ELLIPSIS_DOTS_H.create());
            acaoAdmin.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
            acaoAdmin.setEnabled(!diaDesabilitado && agenda != null);
            acaoAdmin.addClickListener(event -> abrirAcoesAdminModal(dataSelecionada, agenda, idEmpreendedorConfigurado, modalPai));
            c5.add(acaoAdmin);
        } else {
            Button acaoUsuario = new Button(VaadinIcon.EDIT.create());
            acaoUsuario.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
            acaoUsuario.getStyle().set("min-width", "44px");
            boolean horarioDesabilitadoPeloAdmin = agenda != null && Boolean.FALSE.equals(agenda.getAgendadoEmpreendedor());
            acaoUsuario.setEnabled(!diaDesabilitado && !marcado && !horarioDesabilitadoPeloAdmin);
            acaoUsuario.addClickListener(event -> abrirModalCpfUsuario(dataSelecionada, horarioTexto, c2, marcadoCheck, acaoUsuario, modalPai));
            c5.add(acaoUsuario);
        }

        HorizontalLayout linha = new HorizontalLayout(c1, c2, c3, c4, c5);
        linha.setWidthFull();
        linha.setPadding(false);
        linha.setSpacing(false);
        linha.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);
        linha.getStyle().set("border-bottom", "1px solid #eaf1ff");
        linha.getStyle().set("font-weight", "500");
        linha.getStyle().set("min-height", "56px");
        return linha;
    }

    private void abrirAcoesAdminModal(
            LocalDate dataSelecionada,
            Agenda agenda,
            Long idEmpreendedorConfigurado,
            Dialog modalPai
    ) {
        if (agenda == null) {
            Notification notification = Notification.show("Nao ha agendamento nesse horario.");
            notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
            return;
        }

        Dialog dialog = new Dialog();
        dialog.setHeaderTitle("Caixa de acoes");

        Button desabilitar = new Button("Desabilitar este horario");
        desabilitar.setWidthFull();
        desabilitar.addClickListener(event -> {
            try {
                Long idEmpreendedor = resolverIdEmpreendedor(idEmpreendedorConfigurado);
                agendaService.cancelarEmpreendedor(agenda.getIdAgenda(), idEmpreendedor);
                Notification notification = Notification.show("Horario desabilitado.");
                notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
                dialog.close();
                modalPai.close();
                exibirAgendaDoDiaModal(dataSelecionada, true);
            } catch (ResponseStatusException ex) {
                Notification notification = Notification.show(ex.getReason() != null ? ex.getReason() : "Erro ao desabilitar horario.");
                notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
            }
        });

        Button cancelarHorario = new Button("Cancelar horario");
        cancelarHorario.setWidthFull();
        cancelarHorario.addClickListener(event -> {
            try {
                Long idEmpreendedor = resolverIdEmpreendedor(idEmpreendedorConfigurado);
                agendaService.cancelarEmpreendedor(agenda.getIdAgenda(), idEmpreendedor);
                Notification notification = Notification.show("Horario cancelado.");
                notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
                dialog.close();
                modalPai.close();
                exibirAgendaDoDiaModal(dataSelecionada, true);
            } catch (ResponseStatusException ex) {
                Notification notification = Notification.show(ex.getReason() != null ? ex.getReason() : "Erro ao cancelar horario.");
                notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
            }
        });

        Button desmarcar = new Button("Desmarcar horario");
        desmarcar.setWidthFull();
        desmarcar.addClickListener(event -> {
            try {
                Long idEmpreendedor = resolverIdEmpreendedor(idEmpreendedorConfigurado);
                agendaService.desmarcarHorario(agenda.getIdAgenda(), idEmpreendedor);
                Notification notification = Notification.show("Horario desmarcado.");
                notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
                dialog.close();
                modalPai.close();
                exibirAgendaDoDiaModal(dataSelecionada, true);
            } catch (ResponseStatusException ex) {
                Notification notification = Notification.show(ex.getReason() != null ? ex.getReason() : "Erro ao desmarcar horario.");
                notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
            }
        });

        Button enviarMensagem = new Button("Enviar mensagem para cliente");
        enviarMensagem.setWidthFull();
        enviarMensagem.addClickListener(event -> {
            Notification notification = Notification.show("Mensagem enviada para cliente.");
            notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
            dialog.close();
        });

        VerticalLayout layout = new VerticalLayout(desabilitar, cancelarHorario, desmarcar, enviarMensagem);
        layout.setPadding(false);
        layout.setSpacing(true);
        layout.setWidth("320px");

        dialog.add(layout);
        dialog.open();
    }

    private void abrirModalCpfUsuario(
            LocalDate dataSelecionada,
            String horarioTexto,
            Div nomeCell,
            Checkbox marcadoCheck,
            Button acaoButton,
            Dialog modalPai
    ) {
        Dialog cpfDialog = new Dialog();
        cpfDialog.setHeaderTitle("Informar CPF");
        cpfDialog.setWidth("420px");
        cpfDialog.setCloseOnEsc(true);
        cpfDialog.setCloseOnOutsideClick(true);

        TextField cpfField = new TextField("CPF");
        cpfField.setPlaceholder("Digite o CPF");
        cpfField.setWidthFull();

        Button cancelar = new Button("Cancelar", event -> cpfDialog.close());
        cancelar.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

        Button confirmar = new Button("Confirmar");
        confirmar.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        confirmar.addClickListener(event -> {
            try {
                Usuario usuario = usuarioService.buscarPorCpf(cpfField.getValue());
                agendaService.validarDisponibilidade(dataSelecionada.toString(), horarioTexto);

                AgendaRequest request = new AgendaRequest();
                request.setIdUsuario(usuario.getIdUsuario());
                request.setIdEmpreendedor(null);
                request.setHorarioAgenda(horarioTexto);
                request.setDataAgenda(dataSelecionada.toString());
                request.setAgendadoUsuario(true);
                request.setAgendadoEmpreendedor(null);
                agendaService.inserir(request);

                nomeCell.setText(usuario.getNomeUsuario() != null && !usuario.getNomeUsuario().isBlank()
                        ? usuario.getNomeUsuario()
                        : "-");
                marcadoCheck.setValue(true);
                acaoButton.setEnabled(false);

                Notification notification = Notification.show("Horario agendado com sucesso.");
                notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
                cpfDialog.close();
                modalPai.close();
                exibirAgendaDoDiaModal(dataSelecionada, false);
            } catch (ResponseStatusException ex) {
                if (ex.getStatusCode().value() == HttpStatus.NOT_FOUND.value()) {
                    getUI().ifPresent(ui -> ui.navigate("usuario/cadastro"));
                    cpfDialog.close();
                    return;
                }
                Notification notification = Notification.show(ex.getReason() != null ? ex.getReason() : "Erro ao processar agendamento.");
                notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
            }
        });

        HorizontalLayout actions = new HorizontalLayout(cancelar, confirmar);
        actions.setJustifyContentMode(JustifyContentMode.END);
        actions.setWidthFull();

        VerticalLayout content = new VerticalLayout(cpfField, actions);
        content.setPadding(false);
        content.setSpacing(true);
        content.setWidthFull();

        cpfDialog.add(content);
        cpfDialog.open();
    }

    private Div createCell(String text, String flex, boolean rightBorder) {
        Div cell = new Div();
        cell.setText(text);
        cell.getStyle().set("flex", flex);
        cell.getStyle().set("padding", "12px 16px");
        if (rightBorder) {
            cell.getStyle().set("border-right", "1px solid #d9e7ff");
        }
        return cell;
    }

    private Div createContainerCell(String flex) {
        Div cell = new Div();
        cell.getStyle().set("flex", flex);
        cell.getStyle().set("padding", "8px 16px");
        cell.getStyle().set("display", "flex");
        cell.getStyle().set("align-items", "center");
        cell.getStyle().set("justify-content", "center");
        cell.getStyle().set("border-right", "1px solid #d9e7ff");
        return cell;
    }

    private HorizontalLayout buildLinhaTabela(List<String> colunas, boolean cabecalho) {
        Div c1 = createCell(colunas.get(0), "1.05", true);
        Div c2 = createCell(colunas.get(1), "1.2", true);
        Div c3 = createCell(colunas.get(2), "0.42", true);
        c3.getStyle().set("text-align", "center");
        Div c4 = createCell(colunas.get(3), "0.5", true);
        c4.getStyle().set("text-align", "center");
        Div c5 = createCell(colunas.get(4), "0.6", false);
        c5.getStyle().set("text-align", "center");

        HorizontalLayout linha = new HorizontalLayout(c1, c2, c3, c4, c5);
        linha.setWidthFull();
        linha.setPadding(false);
        linha.setSpacing(false);
        linha.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);
        if (cabecalho) {
            linha.getStyle().set("font-weight", "700");
            linha.getStyle().set("background", "#f7fbff");
            linha.getStyle().set("border-bottom", "1px solid #d9e7ff");
        }
        return linha;
    }

    private Set<LocalDate> carregarDiasDesabilitados() {
        Set<String> dias = agendaService.listarDiasDesabilitados();
        Set<LocalDate> datas = new HashSet<LocalDate>();
        for (String dia : dias) {
            if (dia == null || dia.isBlank()) {
                continue;
            }
            try {
                datas.add(LocalDate.parse(dia));
            } catch (DateTimeParseException ignored) {
                // Ignora valor invalido legado no banco.
            }
        }
        return datas;
    }

    private Long resolverIdEmpreendedor(Long idEmpreendedorConfigurado) {
        if (idEmpreendedorConfigurado != null) {
            return idEmpreendedorConfigurado;
        }
        Object valorSessao = VaadinSession.getCurrent().getAttribute("idEmpreendedorLogado");
        if (valorSessao instanceof Long) {
            return (Long) valorSessao;
        }
        if (valorSessao instanceof Integer) {
            return ((Integer) valorSessao).longValue();
        }
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Empreendedor nao definido para acao do admin.");
    }

    private LocalTime parseHora(String valorHora) {
        if (valorHora == null || valorHora.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Hora de atendimento invalida.");
        }
        String valor = valorHora.trim();
        try {
            return LocalTime.parse(valor);
        } catch (DateTimeParseException ignored) {
            // Tenta formatos alternativos abaixo.
        }
        try {
            return LocalTime.parse(valor, HORA_HH_MM);
        } catch (DateTimeParseException ignored) {
            // Tenta ler apenas HH:mm.
        }
        if (valor.length() >= 5) {
            try {
                return LocalTime.parse(valor.substring(0, 5), HORA_HH_MM);
            } catch (DateTimeParseException ignored) {
                // Continua para erro padrao.
            }
        }
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Formato de hora invalido na configuracao.");
    }

    private int extrairIntervaloEmMinutos(String intervaloAtendimento) {
        if (intervaloAtendimento == null || intervaloAtendimento.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Intervalo de atendimento invalido.");
        }
        Matcher matcher = PATTERN_NUMERO.matcher(intervaloAtendimento);
        if (!matcher.find()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Intervalo de atendimento invalido.");
        }
        int minutos = Integer.parseInt(matcher.group(1));
        if (minutos <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Intervalo de atendimento invalido.");
        }
        return minutos;
    }

    private List<LocalTime> gerarHorarios(LocalTime horaInicio, LocalTime horaFinal, int intervaloMinutos) {
        if (!horaInicio.isBefore(horaFinal)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Faixa de atendimento invalida. Ajuste a configuracao: hora final deve ser maior que hora de inicio.");
        }
        List<LocalTime> horarios = new ArrayList<LocalTime>();
        LocalTime atual = horaInicio;
        while (atual.isBefore(horaFinal)) {
            horarios.add(atual);
            atual = atual.plusMinutes(intervaloMinutos);
        }
        return horarios;
    }

    private String formatarDataCompleta(LocalDate dataSelecionada) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEEE, d 'de' MMMM 'de' yyyy", LOCALE_PT_BR);
        return dataSelecionada.format(formatter);
    }

    private String formatarMesAno(YearMonth mesAtual) {
        String mes = mesAtual.getMonth().getDisplayName(TextStyle.FULL, LOCALE_PT_BR);
        String mesCapitalizado = Character.toUpperCase(mes.charAt(0)) + mes.substring(1);
        return mesCapitalizado + " " + mesAtual.getYear();
    }

    private int converterParaInicioDomingo(DayOfWeek dayOfWeek) {
        return dayOfWeek.getValue() % 7;
    }
}
