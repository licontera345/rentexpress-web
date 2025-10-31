package com.pinguela.rentexpressweb.controller;

import java.io.IOException;

import com.pinguela.rentexpressweb.constants.EmployeeConstants;
import com.pinguela.rentexpressweb.util.Views;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/public/employees/detail")
public class PublicEmployeeDetailServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setAttribute(EmployeeConstants.ATTR_SELECTED_EMPLOYEE, request.getParameter(EmployeeConstants.PARAM_EMPLOYEE_ID));
        request.getRequestDispatcher(Views.PUBLIC_EMPLOYEE_DETAIL).forward(request, response);
    }
}
