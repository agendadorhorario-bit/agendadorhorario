package com.agendador.view;

import com.agendador.usuario.UsuarioRequest;
import com.agendador.usuario.UsuarioService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.springframework.web.server.ResponseStatusException;

@Route("usuario/cadastro")
@PageTitle("Cadastro de Usuario")
public class UsuarioView extends VerticalLayout {
    private final UsuarioService usuarioService;

    public UsuarioView(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
        setSizeFull();
        setPadding(true);
        setSpacing(true);
        setAlignItems(Alignment.CENTER);
        getStyle().set("background", "#f4f8ff");

        HorizontalLayout card = new HorizontalLayout();
        card.setWidthFull();
        card.setMaxWidth("1200px");
        card.setPadding(false);
        card.setSpacing(false);
        card.getStyle().set("background", "white");
        card.getStyle().set("border-radius", "20px");
        card.getStyle().set("box-shadow", "0 10px 40px rgba(24, 56, 120, 0.15)");
        card.getStyle().set("overflow", "hidden");
        card.getStyle().set("flex-wrap", "wrap");

        card.add(buildLeftPanel(), buildFormPanel());
        add(card);
    }

    private Div buildLeftPanel() {
        Div leftPanel = new Div();
        leftPanel.getStyle().set("background", "linear-gradient(160deg,#0050d7,#003d99)");
        leftPanel.getStyle().set("color", "white");
        leftPanel.getStyle().set("padding", "48px");
        leftPanel.getStyle().set("min-width", "320px");
        leftPanel.getStyle().set("flex", "1 1 320px");

        H2 title = new H2("Cadastre um novo usuario");
        title.getStyle().set("margin-top", "0");

        Paragraph subtitle = new Paragraph("Preencha os dados ao lado para criar um novo usuario no sistema.");
        subtitle.getStyle().set("opacity", "0.9");

        leftPanel.add(title, subtitle);
        return leftPanel;
    }

    private VerticalLayout buildFormPanel() {
        VerticalLayout formPanel = new VerticalLayout();
        formPanel.setSpacing(true);
        formPanel.setPadding(true);
        formPanel.getStyle().set("padding", "40px");
        formPanel.getStyle().set("min-width", "320px");
        formPanel.getStyle().set("flex", "2 1 560px");

        H4 section = new H4("USUARIOS");
        section.getStyle().set("color", "#0b5fff");
        section.getStyle().set("margin", "0");

        H2 title = new H2("Cadastro de Usuario");
        title.getStyle().set("margin", "0");

        Paragraph subtitle = new Paragraph("Informe os dados abaixo para cadastrar um novo usuario.");
        subtitle.getStyle().set("color", "#5f6f91");

        FormLayout form = new FormLayout();
        form.setWidthFull();
        form.setResponsiveSteps(
                new FormLayout.ResponsiveStep("0", 1),
                new FormLayout.ResponsiveStep("900px", 1)
        );

        TextField nomeUsuario = new TextField("Nome do Usuario");
        nomeUsuario.setPlaceholder("Digite o nome do usuario");
        nomeUsuario.setPrefixComponent(VaadinIcon.USER.create());
        nomeUsuario.setWidthFull();

        TextField endereco = new TextField("Endereco");
        endereco.setPlaceholder("Digite o endereco do usuario");
        endereco.setPrefixComponent(VaadinIcon.HOME.create());
        endereco.setWidthFull();

        TextField celular = new TextField("Celular");
        celular.setPlaceholder("Digite o celular do usuario");
        celular.setPrefixComponent(VaadinIcon.MOBILE.create());
        celular.setWidthFull();
        celular.setValueChangeMode(ValueChangeMode.EAGER);
        celular.addValueChangeListener(event -> aplicarMascaraCelular(celular));

        TextField cpfUsuario = new TextField("CPF");
        cpfUsuario.setPlaceholder("Digite o CPF do usuario");
        cpfUsuario.setPrefixComponent(VaadinIcon.USER.create());
        cpfUsuario.setWidthFull();
        cpfUsuario.setValueChangeMode(ValueChangeMode.EAGER);
        cpfUsuario.addValueChangeListener(event -> aplicarMascaraCpf(cpfUsuario));

        TextArea observacao = new TextArea("Observacao");
        observacao.setPlaceholder("Digite uma observacao (opcional)");
        observacao.setMinHeight("130px");
        observacao.setWidthFull();

        form.add(nomeUsuario, endereco, celular, cpfUsuario, observacao);

        Button salvar = new Button("Salvar Usuario", VaadinIcon.CHECK.create());
        salvar.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        salvar.setEnabled(false);
        cpfUsuario.addBlurListener(event -> validarCampoCpf(cpfUsuario, salvar));
        salvar.addClickListener(event -> salvarUsuario(nomeUsuario, endereco, celular, cpfUsuario, observacao, salvar));

        Button cancelar = new Button("Cancelar", VaadinIcon.CLOSE_SMALL.create());
        cancelar.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        cancelar.addClickListener(event -> {
            nomeUsuario.clear();
            endereco.clear();
            celular.clear();
            cpfUsuario.clear();
            cpfUsuario.setInvalid(false);
            cpfUsuario.setErrorMessage(null);
            observacao.clear();
            salvar.setEnabled(false);
        });

        HorizontalLayout actions = new HorizontalLayout(cancelar, salvar);
        actions.setWidthFull();
        actions.setJustifyContentMode(FlexComponent.JustifyContentMode.END);
        actions.getStyle().set("margin-top", "8px");

        formPanel.add(section, title, subtitle, form, actions);
        return formPanel;
    }

    private void validarCampoCpf(TextField cpfField, Button salvarButton) {
        String cpf = cpfField.getValue();
        if (cpf == null || cpf.isBlank()) {
            cpfField.setInvalid(true);
            cpfField.setErrorMessage("CPF e obrigatorio");
            salvarButton.setEnabled(false);
            return;
        }
        if (!isCpfValido(cpf)) {
            cpfField.setInvalid(true);
            cpfField.setErrorMessage("CPF invalido");
            salvarButton.setEnabled(false);
            return;
        }
        cpfField.setInvalid(false);
        cpfField.setErrorMessage(null);
        salvarButton.setEnabled(true);
    }

    private void aplicarMascaraCpf(TextField cpfField) {
        String somenteDigitos = cpfField.getValue().replaceAll("\\D", "");
        if (somenteDigitos.length() > 11) {
            somenteDigitos = somenteDigitos.substring(0, 11);
        }

        StringBuilder mascarado = new StringBuilder();
        for (int i = 0; i < somenteDigitos.length(); i++) {
            if (i == 3 || i == 6) {
                mascarado.append('.');
            }
            if (i == 9) {
                mascarado.append('-');
            }
            mascarado.append(somenteDigitos.charAt(i));
        }

        String valorFormatado = mascarado.toString();
        if (!valorFormatado.equals(cpfField.getValue())) {
            cpfField.setValue(valorFormatado);
        }
    }

    private void aplicarMascaraCelular(TextField celularField) {
        String somenteDigitos = celularField.getValue().replaceAll("\\D", "");
        if (somenteDigitos.length() > 11) {
            somenteDigitos = somenteDigitos.substring(0, 11);
        }

        StringBuilder mascarado = new StringBuilder();
        for (int i = 0; i < somenteDigitos.length(); i++) {
            if (i == 0) {
                mascarado.append('(');
            }
            if (i == 2) {
                mascarado.append(") ");
            }
            if (i == 7) {
                mascarado.append('-');
            }
            mascarado.append(somenteDigitos.charAt(i));
        }

        String valorFormatado = mascarado.toString();
        if (!valorFormatado.equals(celularField.getValue())) {
            celularField.setValue(valorFormatado);
        }
    }

    private boolean isCpfValido(String cpf) {
        String somenteDigitos = cpf.replaceAll("\\D", "");
        if (somenteDigitos.length() != 11) {
            return false;
        }
        if (somenteDigitos.chars().distinct().count() == 1) {
            return false;
        }

        int primeiroDigito = calcularDigitoVerificador(somenteDigitos.substring(0, 9), 10);
        int segundoDigito = calcularDigitoVerificador(somenteDigitos.substring(0, 9) + primeiroDigito, 11);
        return somenteDigitos.equals(somenteDigitos.substring(0, 9) + primeiroDigito + segundoDigito);
    }

    private int calcularDigitoVerificador(String base, int pesoInicial) {
        int soma = 0;
        int peso = pesoInicial;
        for (int i = 0; i < base.length(); i++) {
            soma += Character.getNumericValue(base.charAt(i)) * peso;
            peso--;
        }
        int resto = soma % 11;
        return (resto < 2) ? 0 : 11 - resto;
    }

    private void salvarUsuario(TextField nomeUsuario, TextField endereco, TextField celular,
                               TextField cpfUsuario, TextArea observacao, Button salvarButton) {
        validarCampoCpf(cpfUsuario, salvarButton);
        if (nomeUsuario.isEmpty() || endereco.isEmpty() || celular.isEmpty() || cpfUsuario.isInvalid()) {
            Notification notification = Notification.show("Preencha os campos obrigatorios corretamente.");
            notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
            return;
        }

        UsuarioRequest request = new UsuarioRequest();
        request.setNomeUsuario(nomeUsuario.getValue());
        request.setEnderecoUsuario(endereco.getValue());
        request.setCelularUsuario(celular.getValue().replaceAll("\\D", ""));
        request.setCpfUsuario(cpfUsuario.getValue().replaceAll("\\D", ""));
        request.setObsUsuario(observacao.getValue());

        try {
            usuarioService.inserir(request);
            Notification notification = Notification.show("Usuario salvo com sucesso.");
            notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
            nomeUsuario.clear();
            endereco.clear();
            celular.clear();
            cpfUsuario.clear();
            observacao.clear();
            salvarButton.setEnabled(false);
        } catch (ResponseStatusException ex) {
            Notification notification = Notification.show(ex.getReason() != null ? ex.getReason() : "Erro ao salvar usuario.");
            notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
        } catch (Exception ex) {
            Throwable causa = ex.getCause() != null ? ex.getCause() : ex;
            String mensagem = causa.getMessage() != null ? causa.getMessage() : "Erro ao salvar usuario.";
            Notification notification = Notification.show("Erro ao salvar usuario: " + mensagem);
            notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
        }
    }
}








