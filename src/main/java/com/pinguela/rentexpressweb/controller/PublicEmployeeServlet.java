package com.pinguela.rentexpressweb.controller;

import com.pinguela.rentexpres.dao.HeadquartersDAO;
import com.pinguela.rentexpres.dao.impl.HeadquartersDAOImpl;
import com.pinguela.rentexpres.exception.DataException;
import com.pinguela.rentexpres.exception.RentexpresException;
import com.pinguela.rentexpres.model.EmployeeDTO;
import com.pinguela.rentexpres.model.HeadquartersDTO;
import com.pinguela.rentexpres.model.RoleDTO;
import com.pinguela.rentexpres.service.EmployeeService;
import com.pinguela.rentexpres.service.RoleService;
import com.pinguela.rentexpres.service.impl.EmployeeServiceImpl;
import com.pinguela.rentexpres.service.impl.RoleServiceImpl;
import com.pinguela.rentexpres.util.JDBCUtils;
import com.pinguela.rentexpressweb.constants.AppConstants;
import com.pinguela.rentexpressweb.constants.EmployeeConstants;
import com.pinguela.rentexpressweb.constants.FilterConstants;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Listado público de empleados de RentExpress. Permite filtrar por rol, sede y
 * estado y consultar el detalle de cada profesional.
 */
@WebServlet("/public/employees")
public class PublicEmployeeServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private static final Logger LOGGER = LogManager.getLogger(PublicEmployeeServlet.class);

    private final EmployeeService employeeService = new EmployeeServiceImpl();
    private final RoleService roleService = new RoleServiceImpl();
    private final HeadquartersDAO headquartersDAO = new HeadquartersDAOImpl();

    public PublicEmployeeServlet() {
        super();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = Optional.ofNullable(request.getParameter(FilterConstants.PARAM_ACTION))
                .map(String::trim)
                .filter(value -> !value.isEmpty())
                .orElse(FilterConstants.ACTION_LIST);

        if (FilterConstants.ACTION_VIEW.equalsIgnoreCase(action)) {
            handleDetail(request, response);
            return;
        }

        handleList(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request, response);
    }

    private void handleList(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setAttribute(AppConstants.ATTR_PAGE_TITLE, "Equipo RentExpress");

        Map<String, String> filters = buildFilters(request);
        List<String> errors = new ArrayList<>();

        Integer selectedRole = parseInteger(filters.get(EmployeeConstants.PARAM_ROLE), errors,
                "El rol indicado no es válido.");
        Integer selectedHeadquarters = parseInteger(filters.get(EmployeeConstants.PARAM_HEADQUARTERS), errors,
                "La sede seleccionada no es válida.");
        Boolean activeState = parseActive(filters.get(EmployeeConstants.PARAM_ACTIVE), errors);

        List<EmployeeDTO> employees = loadEmployees();
        List<RoleDTO> roles = loadRoles();
        List<HeadquartersDTO> headquarters = loadHeadquarters();
        Map<Integer, String> roleNames = mapRoles(roles);
        Map<Integer, String> headquartersNames = mapHeadquarters(headquarters);

        List<EmployeeDTO> filtered = employees.stream()
                .filter(employee -> matchesRole(employee, selectedRole))
                .filter(employee -> matchesHeadquarters(employee, selectedHeadquarters))
                .filter(employee -> matchesActive(employee, activeState))
                .filter(employee -> matchesSearch(employee, filters.get(EmployeeConstants.PARAM_SEARCH)))
                .sorted(resolveComparator(filters.get(FilterConstants.PARAM_SORT), roleNames, headquartersNames))
                .collect(Collectors.toList());

        Map<String, Object> pagination = buildPagination(filtered, filters);
        @SuppressWarnings("unchecked")
        List<EmployeeDTO> pageItems = (List<EmployeeDTO>) pagination.get("items");

        Map<String, Object> summary = buildSummary(filtered);

        request.setAttribute("selectedEmployeeRole", selectedRole);
        request.setAttribute("selectedEmployeeHeadquarters", selectedHeadquarters);
        request.setAttribute("selectedEmployeeActive", filters.get(EmployeeConstants.PARAM_ACTIVE));
        request.setAttribute("selectedEmployeeSort", filters.get(FilterConstants.PARAM_SORT));

        request.setAttribute(EmployeeConstants.ATTR_EMPLOYEES, pageItems);
        request.setAttribute(EmployeeConstants.ATTR_FILTERS, filters);
        request.setAttribute(EmployeeConstants.ATTR_FILTER_ERRORS, errors);
        request.setAttribute(EmployeeConstants.ATTR_ROLES, roles);
        request.setAttribute(EmployeeConstants.ATTR_ROLE_NAMES, roleNames);
        request.setAttribute(EmployeeConstants.ATTR_HEADQUARTERS, headquarters);
        request.setAttribute(EmployeeConstants.ATTR_HEADQUARTERS_NAMES, headquartersNames);
        request.setAttribute(EmployeeConstants.ATTR_PAGINATION, pagination);
        request.setAttribute(EmployeeConstants.ATTR_SUMMARY, summary);

        request.getRequestDispatcher("/public/employee/employee_list.jsp").forward(request, response);
    }

    private void handleDetail(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Integer employeeId = parseInteger(request.getParameter(EmployeeConstants.PARAM_EMPLOYEE_ID), null, null);
        if (employeeId == null) {
            response.sendRedirect(request.getContextPath() + "/public/employees?error=notfound");
            return;
        }

        EmployeeDTO employee = findEmployee(employeeId);
        if (employee == null) {
            response.sendRedirect(request.getContextPath() + "/public/employees?error=notfound");
            return;
        }

        String roleName = resolveRoleName(employee.getRoleId());
        String headquartersName = resolveHeadquartersName(employee.getHeadquartersId());

        request.setAttribute(AppConstants.ATTR_PAGE_TITLE, buildDetailTitle(employee));
        request.setAttribute(EmployeeConstants.ATTR_SELECTED_EMPLOYEE, employee);
        request.setAttribute(EmployeeConstants.ATTR_SELECTED_ROLE_NAME, roleName);
        request.setAttribute(EmployeeConstants.ATTR_SELECTED_HEADQUARTERS, headquartersName);

        request.getRequestDispatcher("/public/employee/employee_detail.jsp").forward(request, response);
    }

    private Map<String, String> buildFilters(HttpServletRequest request) {
        Map<String, String> filters = new HashMap<>();
        filters.put(EmployeeConstants.PARAM_SEARCH, sanitize(request.getParameter(EmployeeConstants.PARAM_SEARCH)));
        filters.put(EmployeeConstants.PARAM_ROLE, sanitize(request.getParameter(EmployeeConstants.PARAM_ROLE)));
        filters.put(EmployeeConstants.PARAM_HEADQUARTERS,
                sanitize(request.getParameter(EmployeeConstants.PARAM_HEADQUARTERS)));
        filters.put(EmployeeConstants.PARAM_ACTIVE, defaultActive(request.getParameter(EmployeeConstants.PARAM_ACTIVE)));
        filters.put(FilterConstants.PARAM_SORT, resolveSort(request.getParameter(FilterConstants.PARAM_SORT)));
        filters.put(FilterConstants.PARAM_PAGE, defaultPage(request.getParameter(FilterConstants.PARAM_PAGE)));
        filters.put(FilterConstants.PARAM_PAGE_SIZE, defaultPageSize(request.getParameter(FilterConstants.PARAM_PAGE_SIZE)));
        return filters;
    }

    private String sanitize(String value) {
        return value != null ? value.trim() : null;
    }

    private String defaultActive(String raw) {
        if (raw == null || raw.trim().isEmpty()) {
            return EmployeeConstants.VALUE_ALL;
        }
        String cleaned = raw.trim().toLowerCase(Locale.ROOT);
        if (EmployeeConstants.VALUE_ACTIVE.equals(cleaned) || EmployeeConstants.VALUE_INACTIVE.equals(cleaned)) {
            return cleaned;
        }
        return EmployeeConstants.VALUE_ALL;
    }

    private String resolveSort(String rawSort) {
        if (EmployeeConstants.VALUE_SORT_NAME_ASC.equals(rawSort)
                || EmployeeConstants.VALUE_SORT_HEADQUARTERS_ASC.equals(rawSort)) {
            return rawSort;
        }
        return EmployeeConstants.VALUE_SORT_CREATED_DESC;
    }

    private String defaultPage(String rawPage) {
        if (rawPage == null || rawPage.trim().isEmpty()) {
            return "1";
        }
        return rawPage;
    }

    private String defaultPageSize(String rawSize) {
        if (rawSize == null || rawSize.trim().isEmpty()) {
            return "10";
        }
        return rawSize;
    }

    private Integer parseInteger(String value, List<String> errors, String errorMessage) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        try {
            return Integer.valueOf(value.trim());
        } catch (NumberFormatException ex) {
            if (errors != null && errorMessage != null) {
                errors.add(errorMessage);
            }
            return null;
        }
    }

    private Boolean parseActive(String value, List<String> errors) {
        if (value == null || value.isEmpty() || EmployeeConstants.VALUE_ALL.equals(value)) {
            return null;
        }
        if (EmployeeConstants.VALUE_ACTIVE.equals(value)) {
            return Boolean.TRUE;
        }
        if (EmployeeConstants.VALUE_INACTIVE.equals(value)) {
            return Boolean.FALSE;
        }
        if (errors != null) {
            errors.add("El filtro de estado es desconocido.");
        }
        return null;
    }

    private List<EmployeeDTO> loadEmployees() {
        try {
            List<EmployeeDTO> list = employeeService.findAll();
            return list != null ? list : Collections.emptyList();
        } catch (RentexpresException ex) {
            LOGGER.error("No se pudieron recuperar los empleados", ex);
            return Collections.emptyList();
        }
    }

    private List<RoleDTO> loadRoles() {
        try {
            List<RoleDTO> roles = roleService.findAll();
            return roles != null ? roles : Collections.emptyList();
        } catch (RentexpresException ex) {
            LOGGER.warn("No se pudieron recuperar los roles de empleado", ex);
            return Collections.emptyList();
        }
    }

    private List<HeadquartersDTO> loadHeadquarters() {
        Connection connection = null;
        try {
            connection = JDBCUtils.getConnection();
            JDBCUtils.beginTransaction(connection);
            List<HeadquartersDTO> list = headquartersDAO.findAll(connection);
            JDBCUtils.commitTransaction(connection);
            return list != null ? list : Collections.emptyList();
        } catch (SQLException | DataException ex) {
            JDBCUtils.rollbackTransaction(connection);
            LOGGER.warn("No se pudieron recuperar las sedes", ex);
            return Collections.emptyList();
        } finally {
            JDBCUtils.close(connection);
        }
    }

    private Map<Integer, String> mapRoles(List<RoleDTO> roles) {
        Map<Integer, String> map = new LinkedHashMap<>();
        for (RoleDTO role : roles) {
            if (role != null && role.getRoleId() != null) {
                map.put(role.getRoleId(), role.getRoleName());
            }
        }
        return map;
    }

    private Map<Integer, String> mapHeadquarters(List<HeadquartersDTO> headquarters) {
        Map<Integer, String> map = new LinkedHashMap<>();
        for (HeadquartersDTO dto : headquarters) {
            if (dto != null && dto.getHeadquartersId() != null) {
                map.put(dto.getHeadquartersId(), dto.getName());
            }
        }
        return map;
    }

    private boolean matchesRole(EmployeeDTO employee, Integer roleId) {
        if (roleId == null) {
            return true;
        }
        return Objects.equals(employee.getRoleId(), roleId);
    }

    private boolean matchesHeadquarters(EmployeeDTO employee, Integer headquartersId) {
        if (headquartersId == null) {
            return true;
        }
        return Objects.equals(employee.getHeadquartersId(), headquartersId);
    }

    private boolean matchesActive(EmployeeDTO employee, Boolean active) {
        if (active == null) {
            return true;
        }
        Boolean status = employee.getActiveStatus();
        if (status == null) {
            return !active.booleanValue();
        }
        return status.booleanValue() == active.booleanValue();
    }

    private boolean matchesSearch(EmployeeDTO employee, String search) {
        if (search == null || search.isEmpty()) {
            return true;
        }
        String normalized = search.toLowerCase(Locale.ROOT);
        return contains(employee.getFirstName(), normalized)
                || contains(employee.getLastName1(), normalized)
                || contains(employee.getLastName2(), normalized)
                || contains(employee.getEmail(), normalized)
                || contains(employee.getEmployeeName(), normalized);
    }

    private boolean contains(String value, String search) {
        return value != null && value.toLowerCase(Locale.ROOT).contains(search);
    }

    private Comparator<EmployeeDTO> resolveComparator(String sort, Map<Integer, String> roleNames,
            Map<Integer, String> headquartersNames) {
        Comparator<EmployeeDTO> comparator;
        if (EmployeeConstants.VALUE_SORT_NAME_ASC.equals(sort)) {
            comparator = Comparator.comparing(this::resolveFullName, Comparator.nullsLast(String::compareToIgnoreCase));
        } else if (EmployeeConstants.VALUE_SORT_HEADQUARTERS_ASC.equals(sort)) {
            comparator = Comparator.comparing(emp -> headquartersNames.getOrDefault(emp.getHeadquartersId(), "zzzz"),
                    String.CASE_INSENSITIVE_ORDER);
        } else {
            comparator = Comparator.comparing(EmployeeDTO::getCreatedAt,
                    Comparator.nullsLast(Comparator.reverseOrder()));
        }
        return comparator.thenComparing(EmployeeDTO::getEmployeeId, Comparator.nullsLast(Integer::compareTo));
    }

    private String resolveFullName(EmployeeDTO employee) {
        List<String> parts = new ArrayList<>();
        if (employee.getFirstName() != null) {
            parts.add(employee.getFirstName());
        }
        if (employee.getLastName1() != null) {
            parts.add(employee.getLastName1());
        }
        if (employee.getLastName2() != null) {
            parts.add(employee.getLastName2());
        }
        return String.join(" ", parts);
    }

    private Map<String, Object> buildPagination(List<EmployeeDTO> employees, Map<String, String> filters) {
        int total = employees.size();
        int page = parsePositive(filters.get(FilterConstants.PARAM_PAGE), 1);
        int size = normalizePageSize(parsePositive(filters.get(FilterConstants.PARAM_PAGE_SIZE), 10));

        int totalPages = total == 0 ? 1 : (int) Math.ceil((double) total / size);
        if (page > totalPages) {
            page = totalPages;
        }
        int fromIndex = Math.min((page - 1) * size, total);
        int toIndex = Math.min(fromIndex + size, total);

        Map<String, Object> pagination = new HashMap<>();
        pagination.put("page", Integer.valueOf(page));
        pagination.put("size", Integer.valueOf(size));
        pagination.put("total", Integer.valueOf(total));
        pagination.put("totalPages", Integer.valueOf(totalPages));
        pagination.put("hasPrev", Boolean.valueOf(page > 1));
        pagination.put("hasNext", Boolean.valueOf(page < totalPages));
        pagination.put("from", total == 0 ? 0 : fromIndex + 1);
        pagination.put("to", Integer.valueOf(toIndex));

        if (total == 0) {
            pagination.put("items", Collections.emptyList());
            return pagination;
        }

        pagination.put("items", Collections.unmodifiableList(new ArrayList<>(employees.subList(fromIndex, toIndex))));
        return pagination;
    }

    private Map<String, Object> buildSummary(List<EmployeeDTO> employees) {
        long active = employees.stream()
                .filter(emp -> Boolean.TRUE.equals(emp.getActiveStatus()))
                .count();
        long inactive = employees.stream()
                .filter(emp -> Boolean.FALSE.equals(emp.getActiveStatus()))
                .count();

        Map<String, Object> summary = new LinkedHashMap<>();
        summary.put("total", Long.valueOf(employees.size()));
        summary.put("active", Long.valueOf(active));
        summary.put("inactive", Long.valueOf(inactive));
        summary.put("headquarters", employees.stream()
                .map(EmployeeDTO::getHeadquartersId)
                .filter(Objects::nonNull)
                .distinct()
                .count());
        return summary;
    }

    private int parsePositive(String value, int defaultValue) {
        if (value == null) {
            return defaultValue;
        }
        try {
            int parsed = Integer.parseInt(value);
            return parsed > 0 ? parsed : defaultValue;
        } catch (NumberFormatException ex) {
            return defaultValue;
        }
    }

    private int normalizePageSize(int size) {
        switch (size) {
            case 10:
            case 20:
            case 25:
            case 50:
                return size;
            default:
                return 10;
        }
    }

    private EmployeeDTO findEmployee(Integer employeeId) {
        try {
            return employeeService.findById(employeeId);
        } catch (RentexpresException ex) {
            LOGGER.error("No se pudo recuperar el empleado {}", employeeId, ex);
            return null;
        }
    }

    private String resolveRoleName(Integer roleId) {
        if (roleId == null) {
            return null;
        }
        try {
            RoleDTO dto = roleService.findById(roleId);
            return dto != null ? dto.getRoleName() : null;
        } catch (RentexpresException ex) {
            LOGGER.warn("No se pudo recuperar el rol {}", roleId, ex);
            return null;
        }
    }

    private String resolveHeadquartersName(Integer headquartersId) {
        if (headquartersId == null) {
            return null;
        }
        Connection connection = null;
        try {
            connection = JDBCUtils.getConnection();
            JDBCUtils.beginTransaction(connection);
            HeadquartersDTO dto = headquartersDAO.findById(connection, headquartersId);
            JDBCUtils.commitTransaction(connection);
            return dto != null ? dto.getName() : null;
        } catch (SQLException | DataException ex) {
            JDBCUtils.rollbackTransaction(connection);
            LOGGER.warn("No se pudo recuperar el nombre de la sede {}", headquartersId, ex);
            return null;
        } finally {
            JDBCUtils.close(connection);
        }
    }

    private String buildDetailTitle(EmployeeDTO employee) {
        String fullName = resolveFullName(employee);
        if (fullName == null || fullName.isEmpty()) {
            return employee.getEmployeeName() != null ? employee.getEmployeeName() : "Detalle de empleado";
        }
        return fullName;
    }
}
