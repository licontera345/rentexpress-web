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

@WebServlet("/app/employees/register")
public class RegisterEmployeeServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final String KEY_FLASH_SUCCESS = "register.employee.flash.success";
    private static final String KEY_ERROR_FULL_NAME_REQUIRED = "error.validation.fullNameRequired";
    private static final String KEY_ERROR_EMAIL_REQUIRED = "error.validation.emailRequired";
    private static final String KEY_ERROR_HEADQUARTERS_REQUIRED = "error.validation.headquartersRequired";

    private static final Logger LOGGER = LogManager.getLogger(RegisterEmployeeServlet.class);

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
        request.getRequestDispatcher(Views.PUBLIC_REGISTER_EMPLOYEE).forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        Map<String, String> errors = new LinkedHashMap<String, String>();
        Map<String, String> form = new LinkedHashMap<String, String>();

        String fullName = normalize(request.getParameter(UserConstants.PARAM_FULL_NAME));
        String email = normalize(request.getParameter(UserConstants.PARAM_EMAIL));
        String headquarters = normalize(request.getParameter(EmployeeConstants.PARAM_HEADQUARTERS));

        if (fullName == null) {
            errors.put(UserConstants.PARAM_FULL_NAME,
                    MessageResolver.getMessage(request, KEY_ERROR_FULL_NAME_REQUIRED));
        } else {
            form.put(UserConstants.PARAM_FULL_NAME, fullName);
        }
        if (email == null) {
            errors.put(UserConstants.PARAM_EMAIL, MessageResolver.getMessage(request, KEY_ERROR_EMAIL_REQUIRED));
        } else {
            form.put(UserConstants.PARAM_EMAIL, email);
        }
        Integer headquartersId = null;
        if (headquarters == null) {
            errors.put(EmployeeConstants.PARAM_HEADQUARTERS,
                    MessageResolver.getMessage(request, KEY_ERROR_HEADQUARTERS_REQUIRED));
        } else {
            headquartersId = parseInteger(headquarters);
            if (headquartersId == null) {
                errors.put(EmployeeConstants.PARAM_HEADQUARTERS,
                        MessageResolver.getMessage(request, "error.validation.headquartersInvalid"));
            } else {
                form.put(EmployeeConstants.PARAM_HEADQUARTERS, headquarters);
            }
        }

        if (!errors.isEmpty()) {
            request.setAttribute(AppConstants.ATTR_FORM_ERRORS, errors);
            request.setAttribute(AppConstants.ATTR_FORM_DATA, form);
            request.getRequestDispatcher(Views.PUBLIC_REGISTER_EMPLOYEE).forward(request, response);
            return;
        }

        try {
            EmployeeDTO employee = new EmployeeDTO();
            employee.setEmployeeName(fullName);
            employee.setFirstName(fullName);
            employee.setEmail(email);
            employee.setHeadquartersId(headquartersId);
            employee.setActiveStatus(Boolean.TRUE);
            employeeService.create(employee);

            EmployeeDTO persisted = null;
            try {
                persisted = employeeService.findByEmail(email);
            } catch (RentexpresException reloadEx) {
                LOGGER.error("Unable to reload employee by email {} after registration", email, reloadEx);
            }
            if (persisted == null && employee.getEmployeeId() != null) {
                try {
                    persisted = employeeService.findById(employee.getEmployeeId());
                } catch (RentexpresException reloadEx) {
                    LOGGER.error("Unable to reload employee {} after registration", employee.getEmployeeId(), reloadEx);
                }
            }
            if (persisted != null
                    && SessionManager.get(request, AppConstants.ATTR_CURRENT_EMPLOYEE) instanceof EmployeeDTO) {
                SessionManager.set(request, AppConstants.ATTR_CURRENT_EMPLOYEE, persisted);
            }

            SessionManager.set(request, AppConstants.ATTR_FLASH_SUCCESS,
                    MessageResolver.getMessage(request, KEY_FLASH_SUCCESS));
            response.sendRedirect(request.getContextPath() + Views.PUBLIC_LOGIN);
        } catch (RentexpresException ex) {
            LOGGER.error("Error registering employee {}", email, ex);
            errors.put(AppConstants.ATTR_FLASH_ERROR, ex.getMessage());
            request.setAttribute(AppConstants.ATTR_FORM_ERRORS, errors);
            request.setAttribute(AppConstants.ATTR_FORM_DATA, form);
            request.setAttribute(AppConstants.ATTR_FLASH_ERROR, ex.getMessage());
            request.getRequestDispatcher(Views.PUBLIC_REGISTER_EMPLOYEE).forward(request, response);
        }
    }

    private String normalize(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    private Integer parseInteger(String value) {
        if (value == null) {
            return null;
        }
        try {
            return Integer.valueOf(value.trim());
        } catch (NumberFormatException ex) {
            return null;
        }
    }
}
