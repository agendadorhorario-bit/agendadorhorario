package com.agendador.view;

import com.agendador.empreendedor.EmpreendedorRequest;
import com.agendador.empreendedor.EmpreendedorService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.springframework.web.server.ResponseStatusException;

@Route("empreendedor/cadastro")
@PageTitle("Cadastro de Empreendedor")
public class EmpreendedorView extends VerticalLayout {

    private final EmpreendedorService empreendedorService;

    public EmpreendedorView(EmpreendedorService empreendedorService) {
        this.empreendedorService = empreendedorService;

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

        H2 title = new H2("Cadastre um novo empreendedor");
        title.getStyle().set("margin-top", "0");

        Paragraph subtitle = new Paragraph("Preencha os dados ao lado para criar um novo empreendedor no sistema.");
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

        H4 section = new H4("EMPREENDEDORES");
        section.getStyle().set("color", "#0b5fff");
        section.getStyle().set("margin", "0");

        H2 title = new H2("Cadastro de Empreendedor");
        title.getStyle().set("margin", "0");

        Paragraph subtitle = new Paragraph("Informe os dados abaixo para cadastrar um novo empreendedor.");
        subtitle.getStyle().set("color", "#5f6f91");

        FormLayout form = new FormLayout();
        form.setWidthFull();
        form.setResponsiveSteps(
                new FormLayout.ResponsiveStep("0", 1),
                new FormLayout.ResponsiveStep("900px", 1)
        );

        TextField nome = new TextField("Nome do Empreendedor");
        nome.setPlaceholder("Digite o nome do empreendedor");
        nome.setPrefixComponent(VaadinIcon.USER.create());
        nome.setWidthFull();

        TextField endereco = new TextField("Endereco");
        endereco.setPlaceholder("Digite o endereco do empreendedor");
        endereco.setPrefixComponent(VaadinIcon.HOME.create());
        endereco.setWidthFull();

        TextField celular = new TextField("Celular");
        celular.setPlaceholder("Digite o celular do empreendedor");
        celular.setPrefixComponent(VaadinIcon.MOBILE.create());
        celular.setWidthFull();
        celular.setValueChangeMode(ValueChangeMode.EAGER);
        celular.addValueChangeListener(event -> aplicarMascaraCelular(celular));

        TextField usuario = new TextField("Usuario");
        usuario.setPlaceholder("Digite o usuario de acesso");
        usuario.setPrefixComponent(VaadinIcon.USER_CARD.create());
        usuario.setWidthFull();

        PasswordField senha = new PasswordField("Senha");
        senha.setPlaceholder("Digite a senha");
        senha.setWidthFull();

        form.add(nome, endereco, celular, usuario, senha);

        Button salvar = new Button("Salvar Empreendedor", VaadinIcon.CHECK.create());
        salvar.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        salvar.addClickListener(event -> salvarEmpreendedor(nome, endereco, celular, usuario, senha));

        Button cancelar = new Button("Cancelar", VaadinIcon.CLOSE_SMALL.create());
        cancelar.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        cancelar.addClickListener(event -> {
            nome.clear();
            endereco.clear();
            celular.clear();
            usuario.clear();
            senha.clear();
        });

        HorizontalLayout actions = new HorizontalLayout(cancelar, salvar);
        actions.setWidthFull();
        actions.setJustifyContentMode(FlexComponent.JustifyContentMode.END);
        actions.getStyle().set("margin-top", "8px");

        formPanel.add(section, title, subtitle, form, actions);
        return formPanel;
    }

    private void salvarEmpreendedor(TextField nome, TextField endereco, TextField celular,
                                    TextField usuario, PasswordField senha) {
        if (isBlank(nome.getValue()) || isBlank(endereco.getValue()) || isBlank(celular.getValue())
                || isBlank(usuario.getValue()) || isBlank(senha.getValue())) {
            Notification notification = Notification.show("Preencha os campos obrigatorios.");
            notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
            return;
        }

        EmpreendedorRequest request = new EmpreendedorRequest();
        request.setNomeEmpreendedor(nome.getValue());
        request.setEnderecoEmpreendedor(endereco.getValue());
        request.setCelularEmpreendedor(celular.getValue().replaceAll("\\D", ""));
        request.setUsuarioEmpreendedor(usuario.getValue());
        request.setSenhaEmpreendedor(senha.getValue());

        try {
            empreendedorService.inserir(request);
            Notification notification = Notification.show("Empreendedor salvo com sucesso.");
            notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
            nome.clear();
            endereco.clear();
            celular.clear();
            usuario.clear();
            senha.clear();
        } catch (ResponseStatusException ex) {
            Notification notification = Notification.show(ex.getReason() != null ? ex.getReason() : "Erro ao salvar empreendedor.");
            notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
        } catch (Exception ex) {
            Throwable causa = ex.getCause() != null ? ex.getCause() : ex;
            String mensagem = causa.getMessage() != null ? causa.getMessage() : "Erro ao salvar empreendedor.";
            Notification notification = Notification.show("Erro ao salvar empreendedor: " + mensagem);
            notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
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

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}

