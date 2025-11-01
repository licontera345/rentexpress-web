package com.pinguela.rentexpressweb.controller;

import java.io.IOException;

import com.pinguela.rentexpressweb.constants.AppConstants;
import com.pinguela.rentexpressweb.constants.UserConstants;
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
    private static final String PAGE_TITLE = "Mi perfil";

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        Object currentUser = SessionManager.get(request, AppConstants.ATTR_CURRENT_USER);
        if (currentUser == null) {
            response.sendRedirect(request.getContextPath() + Views.PUBLIC_LOGIN);
            return;
        }
        request.setAttribute(AppConstants.ATTR_PAGE_TITLE, PAGE_TITLE);
        request.setAttribute(UserConstants.ATTR_PROFILE_DATA, currentUser);
        request.getRequestDispatcher(Views.PRIVATE_USER_PROFILE).forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        doGet(request, response);
    }
}
