package com.pinguela.rentexpressweb.controller;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.pinguela.rentexpres.exception.RentexpresException;
import com.pinguela.rentexpres.model.EmployeeDTO;
import com.pinguela.rentexpres.service.EmployeeService;
import com.pinguela.rentexpres.service.impl.EmployeeServiceImpl;
import com.pinguela.rentexpressweb.constants.AppConstants;
import com.pinguela.rentexpressweb.constants.EmployeeConstants;
import com.pinguela.rentexpressweb.constants.UserConstants;
import com.pinguela.rentexpressweb.util.MessageResolver;
import com.pinguela.rentexpressweb.util.SessionManager;
import com.pinguela.rentexpressweb.util.Views;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/private/employee/profile")
public class EmployeeProfileServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private static final Logger LOGGER = LogManager.getLogger(EmployeeProfileServlet.class);

    private transient EmployeeService employeeService;

    @Override
    public void init() throws ServletException {
        super.init();
        this.employeeService = new EmployeeServiceImpl();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        EmployeeDTO currentEmployee = (EmployeeDTO) SessionManager.get(request, AppConstants.ATTR_CURRENT_EMPLOYEE);
        if (currentEmployee == null) {
            response.sendRedirect(request.getContextPath() + Views.PUBLIC_LOGIN);
            return;
        }
        try {
            EmployeeDTO reloaded = employeeService.findById(currentEmployee.getId());
            if (reloaded != null) {
                SessionManager.set(request, AppConstants.ATTR_CURRENT_EMPLOYEE, reloaded);
                currentEmployee = reloaded;
            }
        } catch (RentexpresException ex) {
            LOGGER.error("Error loading employee profile {}", currentEmployee.getId(), ex);
            request.setAttribute(AppConstants.ATTR_FLASH_ERROR, ex.getMessage());
        }
        request.setAttribute(EmployeeConstants.ATTR_EMPLOYEE_PROFILE, currentEmployee);
        request.getRequestDispatcher(Views.PRIVATE_EMPLOYEE_PROFILE).forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        EmployeeDTO currentEmployee = (EmployeeDTO) SessionManager.get(request, AppConstants.ATTR_CURRENT_EMPLOYEE);
        if (currentEmployee == null) {
            response.sendRedirect(request.getContextPath() + Views.PUBLIC_LOGIN);
            return;
        }

        Map<String, String> errors = new LinkedHashMap<String, String>();
        Map<String, String> form = new LinkedHashMap<String, String>();

        String fullName = normalize(request.getParameter(UserConstants.PARAM_FULL_NAME));
        String phone = normalize(request.getParameter(UserConstants.PARAM_PHONE));

        if (fullName == null) {
            errors.put(UserConstants.PARAM_FULL_NAME,
                    MessageResolver.getMessage(request, "error.validation.fullNameRequired"));
        } else {
            form.put(UserConstants.PARAM_FULL_NAME, fullName);
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

        EmployeeDTO updated = new EmployeeDTO();
        updated.setId(currentEmployee.getId());
        updated.setEmployeeName(fullName);
        updated.setFirstName(fullName);
        updated.setPhone(phone);
        updated.setEmail(currentEmployee.getEmail());

        try {
            employeeService.update(updated);
            EmployeeDTO refreshed = employeeService.findById(currentEmployee.getId());
            if (refreshed != null) {
                SessionManager.set(request, AppConstants.ATTR_CURRENT_EMPLOYEE, refreshed);
                request.setAttribute(AppConstants.ATTR_FLASH_SUCCESS,
                        MessageResolver.getMessage(request, "profile.update.success"));
            } else {
                request.setAttribute(AppConstants.ATTR_FLASH_ERROR,
                        MessageResolver.getMessage(request, "employee.profile.loadError"));
            }
        } catch (RentexpresException ex) {
            LOGGER.error("Error updating employee profile {}", currentEmployee.getId(), ex);
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
