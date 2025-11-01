package com.pinguela.rentexpressweb.controller;

import java.io.IOException;

import com.pinguela.rentexpressweb.constants.AppConstants;
import com.pinguela.rentexpressweb.security.CookieManager;
import com.pinguela.rentexpressweb.util.SessionManager;
import com.pinguela.rentexpressweb.util.Views;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/logout")
public class LogoutServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        SessionManager.invalidate(request);
        Cookie cookie = CookieManager.getCookie(request, AppConstants.COOKIE_REMEMBER_USER);
        if (cookie != null) {
            CookieManager.removeCookie(response, AppConstants.COOKIE_REMEMBER_USER);
        }
        response.sendRedirect(request.getContextPath() + Views.PUBLIC_LOGIN);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        doGet(request, response);
    }
}
