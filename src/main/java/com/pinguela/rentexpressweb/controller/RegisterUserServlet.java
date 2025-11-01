package com.pinguela.rentexpressweb.controller;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.pinguela.rentexpres.exception.RentexpresException;
import com.pinguela.rentexpres.model.UserDTO;
import com.pinguela.rentexpres.service.UserService;
import com.pinguela.rentexpres.service.impl.UserServiceImpl;
import com.pinguela.rentexpressweb.constants.AppConstants;
import com.pinguela.rentexpressweb.constants.UserConstants;
import com.pinguela.rentexpressweb.util.MessageResolver;
import com.pinguela.rentexpressweb.util.SessionManager;
import com.pinguela.rentexpressweb.util.Views;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/app/users/register")
public class RegisterUserServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final String KEY_FLASH_SUCCESS = "register.user.flash.success";
    private static final String KEY_ERROR_EMAIL_REQUIRED = "error.validation.emailRequired";
    private static final String KEY_ERROR_PASSWORD_REQUIRED = "error.validation.passwordRequired";
    private static final String KEY_ERROR_CONFIRM_REQUIRED = "error.validation.confirmPasswordRequired";
    private static final String KEY_ERROR_PASSWORD_MISMATCH = "error.validation.passwordMismatch";

    private static final Logger LOGGER = LogManager.getLogger(RegisterUserServlet.class);

    private transient UserService userService;

    @Override
    public void init() throws ServletException {
        super.init();
        this.userService = new UserServiceImpl();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        request.getRequestDispatcher(Views.PUBLIC_REGISTER_USER).forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        Map<String, String> errors = new LinkedHashMap<String, String>();
        Map<String, String> form = new LinkedHashMap<String, String>();

        String email = normalize(request.getParameter(UserConstants.PARAM_EMAIL));
        String password = normalize(request.getParameter(UserConstants.PARAM_PASSWORD));
        String confirmPassword = normalize(request.getParameter(UserConstants.PARAM_CONFIRM_PASSWORD));

        if (email == null) {
            errors.put(UserConstants.PARAM_EMAIL, MessageResolver.getMessage(request, KEY_ERROR_EMAIL_REQUIRED));
        } else {
            form.put(UserConstants.PARAM_EMAIL, email);
        }
        if (password == null) {
            errors.put(UserConstants.PARAM_PASSWORD, MessageResolver.getMessage(request, KEY_ERROR_PASSWORD_REQUIRED));
        }
        if (confirmPassword == null) {
            errors.put(UserConstants.PARAM_CONFIRM_PASSWORD, MessageResolver.getMessage(request, KEY_ERROR_CONFIRM_REQUIRED));
        } else if (password != null && !password.equals(confirmPassword)) {
            errors.put(UserConstants.PARAM_CONFIRM_PASSWORD,
                    MessageResolver.getMessage(request, KEY_ERROR_PASSWORD_MISMATCH));
        }

        if (!errors.isEmpty()) {
            request.setAttribute(AppConstants.ATTR_FORM_ERRORS, errors);
            request.setAttribute(AppConstants.ATTR_FORM_DATA, form);
            request.getRequestDispatcher(Views.PUBLIC_REGISTER_USER).forward(request, response);
            return;
        }

        try {
            UserDTO user = new UserDTO();
            user.setEmail(email);
            user.setPassword(password);
            user.setActiveStatus(Boolean.TRUE);
            userService.create(user);

            SessionManager.set(request, AppConstants.ATTR_FLASH_SUCCESS,
                    MessageResolver.getMessage(request, KEY_FLASH_SUCCESS));
            response.sendRedirect(request.getContextPath() + Views.PUBLIC_LOGIN);
        } catch (RentexpresException ex) {
            LOGGER.error("Error registering user {}", email, ex);
            errors.put(AppConstants.ATTR_FLASH_ERROR, ex.getMessage());
            request.setAttribute(AppConstants.ATTR_FORM_ERRORS, errors);
            request.setAttribute(AppConstants.ATTR_FORM_DATA, form);
            request.setAttribute(AppConstants.ATTR_FLASH_ERROR, ex.getMessage());
            request.getRequestDispatcher(Views.PUBLIC_REGISTER_USER).forward(request, response);
        }
    }

    private String normalize(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }
}
