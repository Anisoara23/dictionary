package com.example.dictionary.ui.registration;

import com.example.dictionary.application.facade.UserFacade;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;

import static com.example.dictionary.ui.util.UiUtils.APP_NAME;

@Route("register")
@PageTitle("Registration | " + APP_NAME)
@AnonymousAllowed
public class RegistrationView extends VerticalLayout {

    private final UserFacade userFacade;

    public RegistrationView(UserFacade userFacade) {
        this.userFacade = userFacade;
        RegistrationForm registrationForm = new RegistrationForm();
        registrationForm.getStyle()
                .set("box-shadow", " 0px 6px 22px -12px rgba(0,0,0,0.8)")
                .set("border-radius", "10px")
                .set("padding", "var(--lumo-space-l)");

        setHorizontalComponentAlignment(Alignment.CENTER, registrationForm);
        setHeightFull();

        add(registrationForm);
        setJustifyContentMode(JustifyContentMode.EVENLY);

        RegistrationFormBinder registrationFormBinder = new RegistrationFormBinder(registrationForm);
        registrationFormBinder.addBindingAndValidation(this.userFacade);
    }
}
