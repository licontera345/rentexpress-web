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

@WebServlet("/app/users/profile")
public class UserProfileServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private static final Logger LOGGER = LogManager.getLogger(UserProfileServlet.class);

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
        UserDTO currentUser = (UserDTO) SessionManager.get(request, AppConstants.ATTR_CURRENT_USER);
        if (currentUser == null) {
            response.sendRedirect(request.getContextPath() + Views.PUBLIC_LOGIN);
            return;
        }
        try {
            UserDTO reloaded = userService.findById(currentUser.getUserId());
            if (reloaded != null) {
                SessionManager.set(request, AppConstants.ATTR_CURRENT_USER, reloaded);
                currentUser = reloaded;
            }
        } catch (RentexpresException ex) {
            LOGGER.error("Error loading user profile for {}", currentUser.getUserId(), ex);
            request.setAttribute(AppConstants.ATTR_FLASH_ERROR, ex.getMessage());
        }
        request.setAttribute(UserConstants.ATTR_PROFILE_DATA, currentUser);
        request.getRequestDispatcher(Views.PRIVATE_USER_PROFILE).forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        UserDTO currentUser = (UserDTO) SessionManager.get(request, AppConstants.ATTR_CURRENT_USER);
        if (currentUser == null) {
            response.sendRedirect(request.getContextPath() + Views.PUBLIC_LOGIN);
            return;
        }

        Map<String, String> errors = new LinkedHashMap<String, String>();
        Map<String, String> form = new LinkedHashMap<String, String>();

        String firstName = normalize(request.getParameter(UserConstants.PARAM_FIRST_NAME));
        String lastName1 = normalize(request.getParameter(UserConstants.PARAM_LAST_NAME1));
        String phone = normalize(request.getParameter(UserConstants.PARAM_PHONE));

        if (firstName == null) {
            errors.put(UserConstants.PARAM_FIRST_NAME,
                    MessageResolver.getMessage(request, "error.validation.firstNameRequired"));
        } else {
            form.put(UserConstants.PARAM_FIRST_NAME, firstName);
        }
        if (lastName1 == null) {
            errors.put(UserConstants.PARAM_LAST_NAME1,
                    MessageResolver.getMessage(request, "error.validation.lastNameRequired"));
        } else {
            form.put(UserConstants.PARAM_LAST_NAME1, lastName1);
        }
        if (phone != null) {
            form.put(UserConstants.PARAM_PHONE, phone);
        }

        if (!errors.isEmpty()) {
            request.setAttribute(UserConstants.ATTR_PROFILE_ERRORS, errors);
            request.setAttribute(UserConstants.ATTR_PROFILE_FORM, form);
            doGet(request, response);
            return;
        }

        UserDTO updated = new UserDTO();
        updated.setUserId(currentUser.getUserId());
        updated.setFirstName(firstName);
        updated.setLastName1(lastName1);
        updated.setPhone(phone);
        updated.setEmail(currentUser.getEmail());

        try {
            userService.update(updated);
            SessionManager.set(request, AppConstants.ATTR_CURRENT_USER, updated);
            request.setAttribute(AppConstants.ATTR_FLASH_SUCCESS,
                    MessageResolver.getMessage(request, "profile.update.success"));
        } catch (RentexpresException ex) {
            LOGGER.error("Error updating user profile {}", currentUser.getUserId(), ex);
            request.setAttribute(AppConstants.ATTR_FLASH_ERROR, ex.getMessage());
        }

        doGet(request, response);
    }

    private String normalize(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }
}
