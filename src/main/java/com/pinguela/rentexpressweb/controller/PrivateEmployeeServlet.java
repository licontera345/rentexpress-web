package com.pinguela.rentexpressweb.controller;

import com.pinguela.rentexpressweb.constants.AppConstants;
import com.pinguela.rentexpressweb.constants.EmployeeConstants;
import com.pinguela.rentexpressweb.constants.SecurityConstants;
import com.pinguela.rentexpressweb.security.SessionManager;
import com.pinguela.rentexpressweb.service.employee.EmployeeDirectoryFilter;
import com.pinguela.rentexpressweb.service.employee.EmployeeDirectoryFilterFactory;
import com.pinguela.rentexpressweb.service.employee.EmployeeDirectoryService;
import com.pinguela.rentexpressweb.service.employee.EmployeeDirectoryServiceImpl;
import com.pinguela.rentexpressweb.service.employee.EmployeeDirectoryView;
import com.pinguela.rentexpressweb.util.ControllerUtils;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Consola interna para revisar y filtrar el equipo de RentExpress.
 */
@WebServlet("/app/employees/private")
public class PrivateEmployeeServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private final EmployeeDirectoryService directoryService = new EmployeeDirectoryServiceImpl();
    private final EmployeeDirectoryFilterFactory filterFactory = new EmployeeDirectoryFilterFactory();

    public PrivateEmployeeServlet() {
        super();
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Object currentUser = SessionManager.getAttribute(request, AppConstants.ATTR_CURRENT_USER);
        Object currentEmployee = SessionManager.getAttribute(request, AppConstants.ATTR_CURRENT_EMPLOYEE);
        if (currentUser == null) {
            SessionManager.setAttribute(request, AppConstants.ATTR_FLASH_ERROR,
                    "Inicia sesión para acceder a la zona privada.");
            response.sendRedirect(request.getContextPath() + SecurityConstants.LOGIN_ENDPOINT);
            return;
        }
        if (currentEmployee == null) {
            SessionManager.setAttribute(request, AppConstants.ATTR_FLASH_ERROR,
                    "No tienes permisos para consultar el panel de empleados.");
            response.sendRedirect(request.getContextPath() + SecurityConstants.HOME_ENDPOINT);
            return;
        }

        ControllerUtils.disableCaching(response);
        ControllerUtils.exposeFlashMessages(request);

        Map<String, String> filterErrors = new LinkedHashMap<String, String>();
        EmployeeDirectoryFilter filter = filterFactory.build(request, filterErrors);
        EmployeeDirectoryView directoryView = directoryService.prepareDirectory(filter);

        request.setAttribute(AppConstants.ATTR_PAGE_TITLE, "Panel interno de empleados");
        request.setAttribute(EmployeeConstants.ATTR_FILTERS, filter.getRawFilters());
        request.setAttribute(EmployeeConstants.ATTR_FILTER_ERRORS, filterErrors.values());
        request.setAttribute(EmployeeConstants.ATTR_EMPLOYEES, directoryView.getEmployees());
        request.setAttribute(EmployeeConstants.ATTR_ROLE_NAMES, directoryView.getRoleNames());
        request.setAttribute(EmployeeConstants.ATTR_HEADQUARTERS_NAMES, directoryView.getHeadquartersNames());
        request.setAttribute(EmployeeConstants.ATTR_SUMMARY, directoryView.getSummary());

        request.getRequestDispatcher("/private/employee/employee_admin.jsp").forward(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request, response);
    }
}
