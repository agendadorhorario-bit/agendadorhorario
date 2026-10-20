package com.agendador.view;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import com.vaadin.flow.server.VaadinSession;

@Route("")
@RouteAlias("home")
@PageTitle("Agendador")
public class MainView extends VerticalLayout {

    public MainView() {
        setSizeFull();
        setSpacing(true);
        setPadding(true);
        setAlignItems(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.CENTER);
        getStyle().set("background", "linear-gradient(180deg, #f4f8ff 0%, #ffffff 100%)");

        Image logo = new Image("images/agendador-logo.png", "Logo do Agendador");
        logo.setMaxWidth("520px");
        logo.setWidthFull();
        logo.getStyle().set("width", "min(90vw, 520px)");
        logo.getStyle().set("height", "auto");

        H1 title = new H1("Bem-vindo ao Agendador");
        title.getStyle().set("margin", "12px 0 0 0");
        title.getStyle().set("font-size", "clamp(1.5rem, 2.6vw, 2.3rem)");

        Paragraph subtitle = new Paragraph("Escolha uma opcao para comecar.");
        subtitle.getStyle().set("margin", "0");
        subtitle.getStyle().set("color", "#5f6f91");

        Button entrarAdmin = new Button("Entrar", event -> {
            VaadinSession.getCurrent().setAttribute("adminAutenticado", false);
            VaadinSession.getCurrent().setAttribute("acessoDiretoCalendario", true);
            VaadinSession.getCurrent().setAttribute("idEmpreendedorLogado", null);
            getUI().ifPresent(ui -> ui.navigate("calendario"));
        });
        entrarAdmin.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        Button login = new Button("Admin", event -> getUI()
                .ifPresent(ui -> ui.navigate("login")));
        login.addThemeVariants(ButtonVariant.LUMO_CONTRAST);

        HorizontalLayout actions = new HorizontalLayout(entrarAdmin, login);
        actions.setSpacing(true);
        actions.setPadding(false);
        actions.setJustifyContentMode(JustifyContentMode.CENTER);

        add(logo, title, subtitle, actions);
    }
}

