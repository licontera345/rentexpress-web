package com.pinguela.rentexpressweb.controller;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.pinguela.rentexpressweb.constants.AppConstants;
import com.pinguela.rentexpressweb.constants.UserConstants;
import com.pinguela.rentexpressweb.util.SessionManager;
import com.pinguela.rentexpressweb.util.Views;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/app/home")
public class PrivateDashboardServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        Object currentUser = SessionManager.get(request, AppConstants.ATTR_CURRENT_USER);
        if (currentUser == null) {
            response.sendRedirect(request.getContextPath() + Views.PUBLIC_LOGIN);
            return;
        }
        Map<String, Object> profile = new HashMap<String, Object>();
        profile.put(UserConstants.PARAM_EMAIL, currentUser);
        request.setAttribute(UserConstants.ATTR_PROFILE_DATA, Collections.unmodifiableMap(profile));
        request.getRequestDispatcher(Views.PRIVATE_USER_HOME).forward(request, response);
    }
}
