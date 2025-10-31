package com.pinguela.rentexpressweb.controller;

import com.pinguela.rentexpres.model.CityDTO;
import com.pinguela.rentexpres.model.EmployeeDTO;
import com.pinguela.rentexpres.model.HeadquartersDTO;
import com.pinguela.rentexpres.model.ProvinceDTO;
import com.pinguela.rentexpres.model.RoleDTO;
import com.pinguela.rentexpres.service.HeadquartersService;
import com.pinguela.rentexpres.service.RoleService;
import com.pinguela.rentexpres.service.impl.HeadquartersServiceImpl;
import com.pinguela.rentexpres.service.impl.RoleServiceImpl;
import com.pinguela.rentexpressweb.util.LegacyDateUtils;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.Date;
import java.util.StringJoiner;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@WebServlet("/app/employees/profile")
public class EmployeeProfileServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private static final Logger LOGGER = LogManager.getLogger(EmployeeProfileServlet.class);
    private static final String DATE_PATTERN = "dd/MM/yyyy HH:mm";
    private static final String STATUS_ACTIVE_CLASS = "bg-success-subtle text-success fw-semibold";
    private static final String STATUS_INACTIVE_CLASS = "bg-secondary-subtle text-secondary fw-semibold";

    private final RoleService roleService = new RoleServiceImpl();
    private final HeadquartersService headquartersService = new HeadquartersServiceImpl();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null) {
            response.sendRedirect(request.getContextPath() + "/login?error=login");
            return;
        }

        Object employeeAttr = session.getAttribute("currentEmployee");
        if (!(employeeAttr instanceof EmployeeDTO)) {
            response.sendRedirect(request.getContextPath() + "/login?error=login");
            return;
        }

        disableCaching(response);

        EmployeeDTO employee = (EmployeeDTO) employeeAttr;
        boolean active = Boolean.TRUE.equals(employee.getActiveStatus());
        request.setAttribute("employee", employee);
        request.setAttribute("fullName", buildFullName(employee));
        request.setAttribute("roleName", resolveRoleName(employee));

        HeadquartersDTO headquarters = resolveHeadquarters(employee);
        request.setAttribute("headquarters", headquarters);
        request.setAttribute("headquartersLocation", buildHeadquartersLocation(headquarters));
        request.setAttribute("headquartersPhone", headquarters == null ? null : headquarters.getPhone());
        request.setAttribute("headquartersEmail", headquarters == null ? null : headquarters.getEmail());

        request.setAttribute("isActive", Boolean.valueOf(active));
        request.setAttribute("statusClass", active ? STATUS_ACTIVE_CLASS : STATUS_INACTIVE_CLASS);
        request.setAttribute("createdAtFormatted", formatDate(employee.getCreatedAt()));
        request.setAttribute("updatedAtFormatted", formatDate(employee.getUpdatedAt()));

        request.getRequestDispatcher("/private/employee/employee_profile.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request, response);
    }

    private void disableCaching(HttpServletResponse response) {
        response.setHeader("Cache-Control", "no-store");
        response.setHeader("Pragma", "no-cache");
        response.setDateHeader("Expires", 0);
    }

    private String buildFullName(EmployeeDTO employee) {
        StringBuilder builder = new StringBuilder();
        appendNamePart(builder, employee.getFirstName());
        appendNamePart(builder, employee.getLastName1());
        appendNamePart(builder, employee.getLastName2());
        if (builder.length() == 0 && employee.getEmployeeName() != null) {
            String payrollName = employee.getEmployeeName().trim();
            if (!payrollName.isEmpty()) {
                builder.append(payrollName);
            }
        }
        return builder.toString();
    }

    private void appendNamePart(StringBuilder builder, String value) {
        if (value == null) {
            return;
        }
        String trimmed = value.trim();
        if (trimmed.isEmpty()) {
            return;
        }
        if (builder.length() > 0) {
            builder.append(' ');
        }
        builder.append(trimmed);
    }

    private String resolveRoleName(EmployeeDTO employee) {
        RoleDTO role = employee.getRole();
        if (role != null && role.getRoleName() != null) {
            String name = role.getRoleName().trim();
            if (!name.isEmpty()) {
                return name;
            }
        }
        Integer roleId = employee.getRoleId();
        if (roleId == null) {
            return null;
        }
        try {
            RoleDTO resolved = roleService.findById(roleId);
            if (resolved != null && resolved.getRoleName() != null) {
                String resolvedName = resolved.getRoleName().trim();
                if (!resolvedName.isEmpty()) {
                    return resolvedName;
                }
            }
        } catch (Exception ex) {
            LOGGER.warn("No se pudo recuperar el nombre del rol {}", roleId, ex);
        }
        return null;
    }

    private HeadquartersDTO resolveHeadquarters(EmployeeDTO employee) {
        HeadquartersDTO headquarters = employee.getHeadquarters();
        if (headquarters != null && headquarters.getName() != null && !headquarters.getName().trim().isEmpty()) {
            return headquarters;
        }
        Integer headquartersId = employee.getHeadquartersId();
        if (headquartersId == null) {
            return null;
        }
        try {
            return headquartersService.findById(headquartersId);
        } catch (Exception ex) {
            LOGGER.warn("No se pudo cargar la sede {} asociada al empleado", headquartersId, ex);
            return null;
        }
    }

    private String buildHeadquartersLocation(HeadquartersDTO headquarters) {
        if (headquarters == null) {
            return null;
        }
        CityDTO city = headquarters.getCity();
        ProvinceDTO province = headquarters.getProvince();
        StringJoiner joiner = new StringJoiner(", ");
        if (city != null && city.getCityName() != null) {
            String cityName = city.getCityName().trim();
            if (!cityName.isEmpty()) {
                joiner.add(cityName);
            }
        }
        if (province != null && province.getProvinceName() != null) {
            String provinceName = province.getProvinceName().trim();
            if (!provinceName.isEmpty()) {
                joiner.add(provinceName);
            }
        }
        String result = joiner.toString();
        return result.isEmpty() ? null : result;
    }

    private String formatDate(Object value) {
        Date date = LegacyDateUtils.toDate(value);
        if (date == null) {
            return null;
        }
        return LegacyDateUtils.formatDate(date, DATE_PATTERN);
    }
}
