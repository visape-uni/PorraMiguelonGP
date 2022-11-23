package com.victor.porraGP.controllers;

import com.victor.porraGP.dto.UserDto;
import com.victor.porraGP.exceptions.UserAlreadyExistException;
import com.victor.porraGP.services.UserService;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.web.WebAttributes;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

@Controller
public class LoginController {

    private static final String ERROR_INVALID_USERNAME_PASSWORD = "error.invalidUsernamePassword";
    private static final String ERROR_TEAM_ALREADY_EXISTS = "error.teamAlreadyExists";
    private final UserService userService;

    public LoginController(UserService userService) {
        this.userService = userService;
    }

    @RequestMapping("/login")
    public String login() {
        return "login";
    }

    @RequestMapping("/login-error")
    public String loginError(HttpServletRequest request, Model model) {
        model.addAttribute("errorMessage", getErrorMessage(request));
        return "login";
    }

    @GetMapping("/register")
    public String reigster(Model model) {
        model.addAttribute("user", new UserDto());
        return "register";
    }

    @PostMapping("/register")
    public ModelAndView createUser(@Valid @ModelAttribute("user") UserDto userDto,
                                   Errors errors, ModelAndView modelAndView) {
        if (!errors.hasErrors()) {
            try {
                userService.createNewUser(userDto);
                return new ModelAndView("redirect:/index", "createdUser", userDto);
            } catch (UserAlreadyExistException ex) {
                return new ModelAndView("register", "errorMessage", ERROR_TEAM_ALREADY_EXISTS);
            }
        }
        return modelAndView;
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
