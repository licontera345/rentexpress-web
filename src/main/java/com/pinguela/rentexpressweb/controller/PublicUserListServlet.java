package com.pinguela.rentexpressweb.controller;

import java.io.IOException;
import java.util.Collections;

import com.pinguela.rentexpressweb.constants.UserConstants;
import com.pinguela.rentexpressweb.util.Views;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/public/users")
public class PublicUserListServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setAttribute(UserConstants.ATTR_USERS, Collections.emptyList());
        request.getRequestDispatcher(Views.PUBLIC_USER_LIST).forward(request, response);
    }
}
