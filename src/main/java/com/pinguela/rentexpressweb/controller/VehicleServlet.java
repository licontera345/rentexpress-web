package com.pinguela.rentexpressweb.controller;

import java.io.IOException;
import java.util.Collections;

import com.pinguela.rentexpressweb.constants.AppConstants;
import com.pinguela.rentexpressweb.constants.VehicleConstants;
import com.pinguela.rentexpressweb.util.SessionManager;
import com.pinguela.rentexpressweb.util.Views;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/app/vehicles")
public class VehicleServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (SessionManager.get(request, AppConstants.ATTR_CURRENT_USER) == null) {
            response.sendRedirect(request.getContextPath() + Views.PUBLIC_LOGIN);
            return;
        }
        request.setAttribute(VehicleConstants.ATTR_VEHICLES, Collections.emptyList());
        request.getRequestDispatcher(Views.PRIVATE_VEHICLE_LIST).forward(request, response);
    }
}
