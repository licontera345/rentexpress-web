package com.pinguela.rentexpressweb.controller;

import java.io.IOException;

import com.pinguela.rentexpressweb.constants.UserConstants;
import com.pinguela.rentexpressweb.util.Views;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/public/users/detail")
public class PublicUserDetailServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        request.setAttribute(UserConstants.ATTR_SELECTED_USER, request.getParameter(UserConstants.PARAM_USER_ID));
        request.getRequestDispatcher(Views.PUBLIC_USER_DETAIL).forward(request, response);
    }
}
