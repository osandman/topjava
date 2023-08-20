package ru.javawebinar.topjava.web.interceptor;

import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import ru.javawebinar.topjava.AuthorizedUser;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

public class AuthenticationSuccessEventListener implements ApplicationListener<AuthenticationSuccessEvent> {
    private final HttpServletRequest request;

    public AuthenticationSuccessEventListener(HttpServletRequest request) {
        this.request = request;
    }

    @Override
    public void onApplicationEvent(AuthenticationSuccessEvent event) {
        AuthorizedUser authorizedUser = (AuthorizedUser) event.getAuthentication().getPrincipal();
        HttpSession session = request.getSession();
        session.setAttribute("userTo", authorizedUser.getUserTo());
    }
}
