package com.pinguela.rentexpressweb.controller;

import java.io.IOException;
import java.util.Collections;

import com.pinguela.rentexpressweb.constants.EmployeeConstants;
import com.pinguela.rentexpressweb.util.Views;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/public/employees")
public class PublicEmployeeListServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        request.setAttribute(EmployeeConstants.ATTR_EMPLOYEES, Collections.emptyList());
        request.getRequestDispatcher(Views.PUBLIC_EMPLOYEE_LIST).forward(request, response);
    }
}
