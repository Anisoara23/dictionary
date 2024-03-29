package com.example.dictionary.ui.login;

import com.example.dictionary.application.security.util.SecurityUtils;
import com.example.dictionary.ui.profile.ProfileView;
import com.example.dictionary.ui.registration.RegistrationView;
import com.vaadin.flow.component.login.LoginI18n;
import com.vaadin.flow.component.login.LoginOverlay;
import com.vaadin.flow.router.AfterNavigationEvent;
import com.vaadin.flow.router.AfterNavigationObserver;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;

import static com.example.dictionary.ui.util.UiUtils.APP_NAME;

@Route("login")
@PageTitle("Login | " + APP_NAME)
@AnonymousAllowed
public class LoginView extends LoginOverlay implements AfterNavigationObserver, BeforeEnterObserver {

    public LoginView() {
        LoginI18n loginI18n = LoginI18n.createDefault();
        loginI18n.setHeader(new LoginI18n.Header());
        loginI18n.getHeader().setTitle(APP_NAME);
        loginI18n.setAdditionalInformation(null);
        loginI18n.setForm(new LoginI18n.Form());
        loginI18n.getForm().setSubmit(getTranslation("login.signin"));
        loginI18n.getForm().setUsername(getTranslation("email"));
        loginI18n.getForm().setPassword(getTranslation("password"));
        loginI18n.getForm().setForgotPassword(getTranslation("register"));
        LoginI18n.ErrorMessage errorMessage = loginI18n.getErrorMessage();
        errorMessage.setTitle(getTranslation("login.error.title"));
        errorMessage.setMessage(getTranslation("login.error.message"));
        loginI18n.setErrorMessage(errorMessage);
        loginI18n.getErrorMessage().setUsername(getTranslation("validation.email.required"));
        loginI18n.getErrorMessage().setPassword(getTranslation("validation.password.required"));

        addForgotPasswordListener(event -> getUI().ifPresent(ui -> ui.navigate(RegistrationView.class)));

        setI18n(loginI18n);
        setAction("login");
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        if (SecurityUtils.isUserLoggedIn()) {
            event.forwardTo(ProfileView.class);
        } else {
            setOpened(true);
        }
    }

    @Override
    public void afterNavigation(AfterNavigationEvent event) {
        setError(
                event.getLocation().getQueryParameters().getParameters().containsKey(
                        "error"
                )
        );
    }
}
