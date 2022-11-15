package com.victor.porraGP.controllers;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.web.WebAttributes;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@Controller
public class LoginController {

    public static final String ERROR_INVALID_USERNAME_PASSWORD = "ERROR_INVALID_USERNAME_PASSWORD";

    @RequestMapping("/login")
    public String login() {
        return "login";
    }
    @RequestMapping("/login-error")
    public String loginError(HttpServletRequest request, Model model) {
        model.addAttribute("errorMessage", getErrorMessage(request));
        return "login";
    }

    private String getErrorMessage(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        String errorMessage = null;
        if (session != null) {
            Exception ex = (Exception) request.getSession().getAttribute(WebAttributes.AUTHENTICATION_EXCEPTION);
            if (ex != null) {
                if (ex instanceof BadCredentialsException) {
                    errorMessage = ERROR_INVALID_USERNAME_PASSWORD;
                } else {
                    errorMessage = ex.getMessage();
                }
            }
        }
        return errorMessage;
    }
}
