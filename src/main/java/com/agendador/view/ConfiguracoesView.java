package com.agendador.view;

import com.agendador.configuracao.ConfiguracaoEmpreendedor;
import com.agendador.configuracao.ConfiguracaoEmpreendedorService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.timepicker.TimePicker;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

@Route("configuracoes")
@PageTitle("Configuracoes")
public class ConfiguracoesView extends VerticalLayout implements BeforeEnterObserver {

    private static final DateTimeFormatter HORA_HH_MM = DateTimeFormatter.ofPattern("HH:mm");
    private final ConfiguracaoEmpreendedorService configuracaoEmpreendedorService;

    public ConfiguracoesView(ConfiguracaoEmpreendedorService configuracaoEmpreendedorService) {
        this.configuracaoEmpreendedorService = configuracaoEmpreendedorService;

        setSizeFull();
        setPadding(false);
        setSpacing(false);
        getStyle().set("background", "linear-gradient(180deg, #f4f8ff 0%, #ffffff 100%)");

        HorizontalLayout layout = new HorizontalLayout();
        layout.setSizeFull();
        layout.setPadding(false);
        layout.setSpacing(false);

        Div configuracoesCard = buildConfiguracoesCard();
        layout.add(buildSidebar(), configuracoesCard);
        layout.expand(configuracoesCard);

        add(layout);
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        if (!isAdminAutenticado()) {
            event.rerouteTo("login");
        }
    }

    private boolean isAdminAutenticado() {
        Object autenticado = VaadinSession.getCurrent().getAttribute("adminAutenticado");
        return autenticado instanceof Boolean && (Boolean) autenticado;
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
                menuItem("Calendario", false, "calendario"),
                menuItem("Tarefas", false, null),
                menuItem("Contatos", false, null),
                menuItem("Configuracoes", true, "configuracoes"));
        return sidebar;
    }

    private Div menuItem(String label, boolean ativo, String rota) {
        Div item = new Div();
        item.setText(label);
        item.getStyle().set("padding", "10px 12px");
        item.getStyle().set("border-radius", "10px");
        item.getStyle().set("margin-bottom", "8px");
        item.getStyle().set("font-weight", "600");
        item.getStyle().set("color", "#ffffff");
        if (ativo) {
            item.getStyle().set("background", "rgba(255,255,255,0.14)");
        }
        if (rota != null) {
            item.getStyle().set("cursor", "pointer");
            item.addClickListener(event -> getUI().ifPresent(ui -> ui.navigate(rota)));
        }
        return item;
    }

    private Div buildConfiguracoesCard() {
        Div card = new Div();
        card.getStyle().set("flex", "1");
        card.getStyle().set("margin", "12px");
        card.getStyle().set("background", "#ffffff");
        card.getStyle().set("border-radius", "18px");
        card.getStyle().set("padding", "20px");
        card.getStyle().set("box-shadow", "0 10px 35px rgba(24,56,120,0.12)");

        H2 tituloPagina = new H2("Configuracao de Atendimento");
        tituloPagina.getStyle().set("margin", "0 0 18px 0");

        TimePicker horaInicio = new TimePicker("Hora de inicio");
        horaInicio.setStep(java.time.Duration.ofMinutes(15));
        horaInicio.setWidthFull();
        horaInicio.setMin(LocalTime.of(0, 0));
        horaInicio.setMax(LocalTime.of(23, 45));

        TimePicker horaFinal = new TimePicker("Hora final");
        horaFinal.setStep(java.time.Duration.ofMinutes(15));
        horaFinal.setWidthFull();
        horaFinal.setMin(LocalTime.of(0, 0));
        horaFinal.setMax(LocalTime.of(23, 45));

        ComboBox<String> intervalo = new ComboBox<>("Intervalo de atendimento");
        intervalo.setItems("15 minutos", "30 minutos", "45 minutos", "60 minutos");
        intervalo.setWidthFull();

        aplicarValoresPadrao(horaInicio, horaFinal, intervalo);

        HorizontalLayout campos = new HorizontalLayout(horaInicio, horaFinal, intervalo);
        campos.setWidthFull();
        campos.setSpacing(true);
        campos.setPadding(false);
        campos.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.END);
        campos.expand(horaInicio, horaFinal, intervalo);

        Button salvar = new Button("Salvar configuracao");
        salvar.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        salvar.getStyle().set("margin-top", "20px");
        salvar.getStyle().set("min-width", "260px");
        salvar.getStyle().set("height", "48px");
        salvar.getStyle().set("font-size", "1.1rem");

        Div tabelaUltimaConfiguracao = new Div();
        tabelaUltimaConfiguracao.getStyle().set("margin-top", "16px");
        preencherTabelaUltimaConfiguracao(tabelaUltimaConfiguracao);

        salvar.addClickListener(event -> {
            LocalTime inicioSelecionado = horaInicio.getValue();
            LocalTime finalSelecionado = horaFinal.getValue();
            String intervaloSelecionado = intervalo.getValue();
            if (inicioSelecionado == null || finalSelecionado == null || intervaloSelecionado == null || intervaloSelecionado.isBlank()) {
                Notification notification = Notification.show("Preencha todos os campos antes de salvar.");
                notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
                return;
            }
            if (!inicioSelecionado.isBefore(finalSelecionado)) {
                Notification notification = Notification.show("Hora final deve ser maior que hora de inicio.");
                notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
                return;
            }

            Long idEmpreendedor = obterIdEmpreendedorLogado();
            try {
                configuracaoEmpreendedorService.salvarConfiguracao(
                        inicioSelecionado.format(HORA_HH_MM),
                        finalSelecionado.format(HORA_HH_MM),
                        intervaloSelecionado,
                        idEmpreendedor
                );
                Notification notification = Notification.show("Configuracao salva com sucesso.");
                notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
                preencherTabelaUltimaConfiguracao(tabelaUltimaConfiguracao);
            } catch (ResponseStatusException ex) {
                Notification notification = Notification.show(ex.getReason() != null ? ex.getReason() : "Erro ao salvar configuracao.");
                notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
            }
        });

        card.add(tituloPagina, campos, salvar, tabelaUltimaConfiguracao);
        return card;
    }

    private void aplicarValoresPadrao(TimePicker horaInicio, TimePicker horaFinal, ComboBox<String> intervalo) {
        horaInicio.setValue(LocalTime.of(8, 0));
        horaFinal.setValue(LocalTime.of(18, 0));
        intervalo.setValue("30 minutos");

        try {
            ConfiguracaoEmpreendedor ultima = configuracaoEmpreendedorService.buscarConfiguracao();
            LocalTime horaInicioConfigurada = parseHora(ultima.getHoraInicio());
            LocalTime horaFinalConfigurada = parseHora(ultima.getHoraFinal());
            if (horaInicioConfigurada != null) {
                horaInicio.setValue(horaInicioConfigurada);
            }
            if (horaFinalConfigurada != null) {
                horaFinal.setValue(horaFinalConfigurada);
            }
            intervalo.setValue(ultima.getIntervaloAtendimento());
        } catch (ResponseStatusException ignored) {
            // Sem historico salvo: mantem valores padrao.
        } catch (RuntimeException ignored) {
            // Valor legado invalido na base: mantem valores padrao.
        }
    }

    private LocalTime parseHora(String valorHora) {
        if (valorHora == null || valorHora.isBlank()) {
            return null;
        }
        String valor = valorHora.trim();
        try {
            return LocalTime.parse(valor);
        } catch (DateTimeParseException ignored) {
            // tenta formato HH:mm abaixo
        }
        if (valor.length() >= 5) {
            return LocalTime.parse(valor.substring(0, 5), HORA_HH_MM);
        }
        return LocalTime.parse(valor, HORA_HH_MM);
    }

    private void preencherTabelaUltimaConfiguracao(Div container) {
        container.removeAll();

        H3 titulo = new H3("Ultima configuracao salva");
        titulo.getStyle().set("margin", "0 0 8px 0");

        Div tabela = new Div();
        tabela.getStyle().set("border", "1px solid #d9e7ff");
        tabela.getStyle().set("border-radius", "12px");
        tabela.getStyle().set("overflow", "hidden");

        tabela.add(buildLinhaTabela("Hora de inicio", "Hora final", "Intervalo de atendimento", true));
        try {
            ConfiguracaoEmpreendedor ultima = configuracaoEmpreendedorService.buscarConfiguracao();
            tabela.add(buildLinhaTabela(
                    valorOuTraco(ultima.getHoraInicio()),
                    valorOuTraco(ultima.getHoraFinal()),
                    valorOuTraco(ultima.getIntervaloAtendimento()),
                    false
            ));
        } catch (ResponseStatusException ignored) {
            tabela.add(buildLinhaTabela("-", "-", "-", false));
        }

        container.add(titulo, tabela);
    }

    private HorizontalLayout buildLinhaTabela(String c1Texto, String c2Texto, String c3Texto, boolean cabecalho) {
        Div c1 = new Div();
        c1.setText(c1Texto);
        c1.getStyle().set("flex", "1");
        c1.getStyle().set("padding", "12px 16px");
        c1.getStyle().set("border-right", "1px solid #d9e7ff");

        Div c2 = new Div();
        c2.setText(c2Texto);
        c2.getStyle().set("flex", "1");
        c2.getStyle().set("padding", "12px 16px");
        c2.getStyle().set("border-right", "1px solid #d9e7ff");

        Div c3 = new Div();
        c3.setText(c3Texto);
        c3.getStyle().set("flex", "1");
        c3.getStyle().set("padding", "12px 16px");

        HorizontalLayout linha = new HorizontalLayout(c1, c2, c3);
        linha.setWidthFull();
        linha.setPadding(false);
        linha.setSpacing(false);
        linha.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);
        if (cabecalho) {
            linha.getStyle().set("font-weight", "700");
            linha.getStyle().set("background", "#f7fbff");
            linha.getStyle().set("border-bottom", "1px solid #d9e7ff");
        } else {
            linha.getStyle().set("font-weight", "500");
        }
        return linha;
    }

    private String valorOuTraco(String valor) {
        return valor == null || valor.isBlank() ? "-" : valor;
    }

    private Long obterIdEmpreendedorLogado() {
        Object valor = VaadinSession.getCurrent().getAttribute("idEmpreendedorLogado");
        if (valor instanceof Long) {
            return (Long) valor;
        }
        if (valor instanceof Integer) {
            return ((Integer) valor).longValue();
        }
        return null;
    }
}
