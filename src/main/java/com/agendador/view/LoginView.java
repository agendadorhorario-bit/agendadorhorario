package com.agendador.view;

import com.agendador.empreendedor.EmpreendedorService;
import com.agendador.empreendedor.Empreendedor;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;
import org.springframework.web.server.ResponseStatusException;

@Route("login")
@PageTitle("Login - Agendador")
public class LoginView extends VerticalLayout {

    private final EmpreendedorService empreendedorService;

    public LoginView(EmpreendedorService empreendedorService) {
        this.empreendedorService = empreendedorService;
        setSizeFull();
        setPadding(true);
        setSpacing(true);
        setAlignItems(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.CENTER);
        getStyle().set("background", "linear-gradient(180deg, #f4f8ff 0%, #ffffff 100%)");

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
        leftPanel.getStyle().set("background", "linear-gradient(160deg,#e9f2ff,#d7e9ff)");
        leftPanel.getStyle().set("color", "#112f64");
        leftPanel.getStyle().set("padding", "40px");
        leftPanel.getStyle().set("min-width", "320px");
        leftPanel.getStyle().set("flex", "1 1 360px");

        Image logo = new Image("images/agendador-logo.png", "Logo do Agendador");
        logo.getStyle().set("width", "min(100%, 320px)");
        logo.getStyle().set("height", "auto");
        logo.getStyle().set("display", "block");
        logo.getStyle().set("margin", "0 auto 16px auto");

        Paragraph subtitle = new Paragraph("Gerencie seus compromissos de forma simples e eficiente.");
        subtitle.getStyle().set("font-size", "1.6rem");
        subtitle.getStyle().set("line-height", "1.4");

        leftPanel.add(logo, subtitle, feature("Organize sua agenda"), feature("Receba lembretes"), feature("Aumente sua produtividade"));
        return leftPanel;
    }

    private Div feature(String text) {
        Div feature = new Div();
        feature.setText(text);
        feature.getStyle().set("margin-top", "12px");
        feature.getStyle().set("font-weight", "600");
        return feature;
    }

    private VerticalLayout buildFormPanel() {
        VerticalLayout formPanel = new VerticalLayout();
        formPanel.setSpacing(true);
        formPanel.setPadding(true);
        formPanel.getStyle().set("padding", "40px");
        formPanel.getStyle().set("min-width", "320px");
        formPanel.getStyle().set("flex", "1 1 460px");

        H1 title = new H1("Entrar no ADM Agendador");
        title.getStyle().set("margin", "0");

        H2 subtitle = new H2("Acesse sua conta para continuar");
        subtitle.getStyle().set("margin", "0 0 20px 0");
        subtitle.getStyle().set("font-size", "1.8rem");
        subtitle.getStyle().set("font-weight", "500");
        subtitle.getStyle().set("color", "#5f6f91");

        TextField usuario = new TextField("Usuario ou e-mail");
        usuario.setPlaceholder("Digite seu usuario ou e-mail");
        usuario.setPrefixComponent(VaadinIcon.USER.create());
        usuario.setWidthFull();

        PasswordField senha = new PasswordField("Senha");
        senha.setPlaceholder("Digite sua senha");
        senha.setWidthFull();

        Button entrar = new Button("Entrar", VaadinIcon.ARROW_RIGHT.create(), event -> autenticar(usuario, senha));
        entrar.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        entrar.setWidthFull();

        Button voltar = new Button("Voltar", VaadinIcon.ARROW_LEFT.create(), event -> getUI()
                .ifPresent(ui -> ui.navigate("")));
        voltar.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

        HorizontalLayout actions = new HorizontalLayout(voltar);
        actions.setWidthFull();
        actions.setJustifyContentMode(FlexComponent.JustifyContentMode.START);

        formPanel.add(title, subtitle, usuario, senha, entrar, actions);
        return formPanel;
    }

    private void autenticar(TextField usuario, PasswordField senha) {
        try {
            Empreendedor empreendedorAutenticado = empreendedorService.autenticarLogin(usuario.getValue(), senha.getValue());
            VaadinSession.getCurrent().setAttribute("adminAutenticado", true);
            VaadinSession.getCurrent().setAttribute("acessoDiretoCalendario", false);
            VaadinSession.getCurrent().setAttribute("idEmpreendedorLogado", empreendedorAutenticado.getIdEmpreendedor());
            Notification notification = Notification.show("Login realizado com sucesso.");
            notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
            getUI().ifPresent(ui -> ui.navigate("calendario"));
        } catch (ResponseStatusException ex) {
            Notification notification = Notification.show(ex.getReason() != null ? ex.getReason() : "Erro no login.");
            notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
        }
    }
}

