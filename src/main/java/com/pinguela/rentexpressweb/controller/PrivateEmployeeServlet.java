package com.pinguela.rentexpressweb.controller;

import java.io.IOException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.pinguela.rentexpres.exception.DataException;
import com.pinguela.rentexpres.exception.RentexpresException;
import com.pinguela.rentexpres.model.EmployeeCriteria;
import com.pinguela.rentexpres.model.EmployeeDTO;
import com.pinguela.rentexpres.model.Results;
import com.pinguela.rentexpres.service.EmployeeService;
import com.pinguela.rentexpres.service.HeadquartersService;
import com.pinguela.rentexpres.service.RoleService;
import com.pinguela.rentexpres.service.impl.EmployeeServiceImpl;
import com.pinguela.rentexpres.service.impl.HeadquartersServiceImpl;
import com.pinguela.rentexpres.service.impl.RoleServiceImpl;
import com.pinguela.rentexpressweb.constants.AppConstants;
import com.pinguela.rentexpressweb.constants.EmployeeConstants;
import com.pinguela.rentexpressweb.util.MessageResolver;
import com.pinguela.rentexpressweb.util.PaginationUtils;
import com.pinguela.rentexpressweb.util.RequestHelper;
import com.pinguela.rentexpressweb.util.SessionManager;
import com.pinguela.rentexpressweb.util.Views;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/private/EmployeeServlet")
public class PrivateEmployeeServlet extends AbstractManagementServlet {

    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = LogManager.getLogger(PrivateEmployeeServlet.class);
    private static final String GENERIC_ERROR_KEY = "employee.manage.flash.error";
    private static final int DEFAULT_PAGE_SIZE = 10;

    private final EmployeeService employeeService;
    private final RoleService roleService;
    private final HeadquartersService headquartersService;

    /*
     * Inicializa el servlet con las implementaciones estándar de servicios para
     * empleados, roles y sedes.
     */
    public PrivateEmployeeServlet() {
        this(new EmployeeServiceImpl(), new RoleServiceImpl(), new HeadquartersServiceImpl());
    }

    /*
     * Permite inyectar servicios específicos (por ejemplo en pruebas) y configura
     * las acciones CRUD soportadas por la clase base.
     */
    PrivateEmployeeServlet(EmployeeService employeeService, RoleService roleService,
            HeadquartersService headquartersService) {
        super(AppConstants.CREATE_EMPLOYEE, AppConstants.UPDATE_EMPLOYEE, AppConstants.DELETE_EMPLOYEE);
        this.employeeService = employeeService;
        this.roleService = roleService;
        this.headquartersService = headquartersService;
    }

    @Override
    /*
     * Obtiene el identificador del empleado desde los parámetros de la petición.
     */
    protected Integer resolveEntityId(HttpServletRequest request) {
        return RequestHelper.parseInteger(param(request, EmployeeConstants.PARAM_ID));
    }

    @Override
    /*
     * Carga el listado paginado de empleados aplicando filtros y trasladando los
     * mensajes flash a la vista correspondiente.
     */
    protected void loadList(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            int pageNumber = PaginationUtils.resolvePage(request);
            int pageSize = PaginationUtils.resolvePageSize(request, AppConstants.PARAM_PAGE_SIZE, DEFAULT_PAGE_SIZE);

            EmployeeCriteria criteria = buildCriteria(request, pageNumber, pageSize);

            Results<EmployeeDTO> results = employeeService.findByCriteria(criteria);

            PaginationUtils.apply(request, results, pageNumber, pageSize, EmployeeConstants.ATTR_EMPLOYEES);
            request.setAttribute(EmployeeConstants.ATTR_ROLES, roleService.findAll());
            request.setAttribute(EmployeeConstants.ATTR_HEADQUARTERS, headquartersService.findAll());
            request.setAttribute("filterName", param(request, "name"));
            request.setAttribute("filterEmail", param(request, "email"));
            request.setAttribute("filterRoleId", param(request, EmployeeConstants.PARAM_ROLE_ID));
            request.setAttribute("filterHeadquartersId", param(request, EmployeeConstants.PARAM_HEADQUARTERS_ID));
            request.setAttribute("filterActiveStatus", param(request, EmployeeConstants.PARAM_ACTIVE));
            transferFlashAttributes(request);
            forward(request, response, Views.PRIVATE_EMPLOYEE_LIST);
        } catch (RentexpresException | DataException ex) {
            LOGGER.error("Unable to load employees", ex);
            SessionManager.set(request, AppConstants.ATTR_FLASH_ERROR,
                    MessageResolver.getMessage(request, GENERIC_ERROR_KEY));
            transferFlashAttributes(request);
            forward(request, response, Views.PRIVATE_EMPLOYEE_LIST);
        }
    }

    @Override
    /*
     * Prepara y muestra el formulario de creación o edición del empleado,
     * cargando los datos de referencia necesarios.
     */
    protected void showForm(HttpServletRequest request, HttpServletResponse response, Integer employeeId)
            throws ServletException, IOException {
        try {
            EmployeeDTO employee = employeeId != null ? employeeService.findById(employeeId) : null;
            if (employeeId != null && employee == null) {
                SessionManager.set(request, AppConstants.ATTR_FLASH_ERROR,
                        MessageResolver.getMessage(request, GENERIC_ERROR_KEY));
                response.sendRedirect(request.getContextPath() + "/private/EmployeeServlet");
                return;
            }

            request.setAttribute(EmployeeConstants.ATTR_ROLES, roleService.findAll());
            request.setAttribute(EmployeeConstants.ATTR_HEADQUARTERS, headquartersService.findAll());
            request.setAttribute(EmployeeConstants.ATTR_EMPLOYEE, employee);
            transferFlashAttributes(request);
            forward(request, response, Views.PRIVATE_EMPLOYEE_FORM);
        } catch (RentexpresException | DataException ex) {
            LOGGER.error("Unable to load employee form", ex);
            SessionManager.set(request, AppConstants.ATTR_FLASH_ERROR,
                    MessageResolver.getMessage(request, GENERIC_ERROR_KEY));
            response.sendRedirect(request.getContextPath() + "/private/EmployeeServlet");
        }
    }

    @Override
    /*
     * Gestiona la creación de un nuevo empleado delegando en la lógica común de
     * persistencia.
     */
    protected void handleCreate(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processPersist(request, response, true);
    }

    @Override
    /*
     * Gestiona la actualización de un empleado existente reutilizando la lógica
     * de persistencia compartida.
     */
    protected void handleUpdate(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processPersist(request, response, false);
    }

    private void processPersist(HttpServletRequest request, HttpServletResponse response, boolean creating)
            throws IOException, ServletException {
        String successKey = creating ? "employee.manage.flash.created" : "employee.manage.flash.updated";

        try {
            EmployeeDTO employee = buildEmployee(request);

            boolean success = creating ? employeeService.create(employee) : employeeService.update(employee);

            if (success) {
                SessionManager.set(request, AppConstants.ATTR_FLASH_SUCCESS,
                        MessageResolver.getMessage(request, successKey));
            } else {
                SessionManager.set(request, AppConstants.ATTR_FLASH_ERROR,
                        MessageResolver.getMessage(request, GENERIC_ERROR_KEY));
            }
        } catch (RentexpresException ex) {
            String logAction = creating ? "create" : "update";
            LOGGER.error("Unable to {} employee", logAction, ex);
            SessionManager.set(request, AppConstants.ATTR_FLASH_ERROR,
                    MessageResolver.getMessage(request, GENERIC_ERROR_KEY));
        }

        response.sendRedirect(request.getContextPath() + "/private/EmployeeServlet");
    }

    @Override
    /*
     * Realiza el borrado lógico de un empleado gestionando los mensajes de
     * resultado.
     */
    protected void handleDelete(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Integer employeeId = RequestHelper.parseInteger(param(request, EmployeeConstants.PARAM_ID));

        if (employeeId == null) {
            SessionManager.set(request, AppConstants.ATTR_FLASH_ERROR,
                    MessageResolver.getMessage(request, GENERIC_ERROR_KEY));
            response.sendRedirect(request.getContextPath() + "/private/EmployeeServlet");
            return;
        }

        try {
            EmployeeDTO employee = new EmployeeDTO();
            employee.setId(employeeId);

            if (employeeService.delete(employee, employeeId)) {
                SessionManager.set(request, AppConstants.ATTR_FLASH_SUCCESS,
                        MessageResolver.getMessage(request, "employee.manage.flash.deleted"));
            } else {
                SessionManager.set(request, AppConstants.ATTR_FLASH_ERROR,
                        MessageResolver.getMessage(request, GENERIC_ERROR_KEY));
            }
        } catch (RentexpresException ex) {
            LOGGER.error("Unable to delete employee", ex);
            SessionManager.set(request, AppConstants.ATTR_FLASH_ERROR,
                    MessageResolver.getMessage(request, GENERIC_ERROR_KEY));
        }
        response.sendRedirect(request.getContextPath() + "/private/EmployeeServlet");
    }

    @Override
    /*
     * Devuelve el logger asociado a este servlet para cumplir con el contrato de
     * la superclase.
     */
    protected Logger getLogger() {
        return LOGGER;
    }

    private EmployeeCriteria buildCriteria(HttpServletRequest request, int pageNumber, int pageSize) {
        EmployeeCriteria criteria = new EmployeeCriteria();
        criteria.setPageNumber(pageNumber);
        criteria.setPageSize(pageSize);
        criteria.setFirstName(param(request, "name"));
        criteria.setEmail(param(request, "email"));
        criteria.setRoleId(RequestHelper.parseInteger(param(request, EmployeeConstants.PARAM_ROLE_ID)));
        criteria.setHeadquartersId(RequestHelper.parseInteger(param(request, EmployeeConstants.PARAM_HEADQUARTERS_ID)));
        criteria.setActiveStatus(parseBoolean(param(request, EmployeeConstants.PARAM_ACTIVE)));
        return criteria;
    }

    private EmployeeDTO buildEmployee(HttpServletRequest request) {
        EmployeeDTO employee = new EmployeeDTO();
        employee.setId(RequestHelper.parseInteger(param(request, EmployeeConstants.PARAM_ID)));
        employee.setEmployeeName(param(request, EmployeeConstants.PARAM_EMPLOYEE_NAME));
        employee.setFirstName(param(request, EmployeeConstants.PARAM_FIRST_NAME));
        employee.setLastName1(param(request, EmployeeConstants.PARAM_LAST_NAME1));
        employee.setLastName2(param(request, EmployeeConstants.PARAM_LAST_NAME2));
        employee.setEmail(param(request, EmployeeConstants.PARAM_EMAIL));
        employee.setPhone(param(request, EmployeeConstants.PARAM_PHONE));
        employee.setPassword(param(request, EmployeeConstants.PARAM_PASSWORD));
        employee.setRoleId(RequestHelper.parseInteger(param(request, EmployeeConstants.PARAM_ROLE_ID)));
        employee.setHeadquartersId(RequestHelper.parseInteger(param(request, EmployeeConstants.PARAM_HEADQUARTERS_ID)));
        return employee;
    }

    private Boolean parseBoolean(String value) {
        if (value == null) {
            return null;
        }
        if ("true".equalsIgnoreCase(value)) {
            return Boolean.TRUE;
        }
        if ("false".equalsIgnoreCase(value)) {
            return Boolean.FALSE;
        }
        return null;
    }
}
