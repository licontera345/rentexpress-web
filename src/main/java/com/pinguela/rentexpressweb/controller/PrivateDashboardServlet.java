package com.pinguela.rentexpressweb.controller;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.pinguela.rentexpres.model.EmployeeDTO;
import com.pinguela.rentexpres.model.UserDTO;
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
        UserDTO currentUser = (UserDTO) SessionManager.get(request, AppConstants.ATTR_CURRENT_USER);
        EmployeeDTO currentEmployee = (EmployeeDTO) SessionManager.get(request, AppConstants.ATTR_CURRENT_EMPLOYEE);
        if (currentUser == null && currentEmployee == null) {
            response.sendRedirect(request.getContextPath() + Views.PUBLIC_LOGIN);
            return;
        }
        Map<String, Object> profile = new HashMap<String, Object>();
        if (currentUser != null) {
            profile.put(UserConstants.PARAM_EMAIL, currentUser.getEmail());
            profile.put(UserConstants.PARAM_FULL_NAME, currentUser.getFirstName());
        } else {
            profile.put(UserConstants.PARAM_EMAIL, currentEmployee.getEmail());
            profile.put(UserConstants.PARAM_FULL_NAME, currentEmployee.getEmployeeName());
        }
        request.setAttribute(UserConstants.ATTR_PROFILE_DATA, profile);
        request.getRequestDispatcher(Views.PRIVATE_USER_HOME).forward(request, response);
    }
}
