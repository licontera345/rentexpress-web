package com.pinguela.rentexpressweb.controller;

import com.pinguela.rentexpres.exception.DataException;
import com.pinguela.rentexpres.exception.RentexpresException;
import com.pinguela.rentexpres.model.CityDTO;
import com.pinguela.rentexpres.model.EmployeeDTO;
import com.pinguela.rentexpres.model.HeadquartersDTO;
import com.pinguela.rentexpres.model.ProvinceDTO;
import com.pinguela.rentexpres.model.RoleDTO;
import com.pinguela.rentexpres.service.RoleService;
import com.pinguela.rentexpres.service.impl.RoleServiceImpl;
import com.pinguela.rentexpressweb.constants.AppConstants;
import com.pinguela.rentexpressweb.constants.EmployeeConstants;
import com.pinguela.rentexpressweb.constants.SecurityConstants;
import com.pinguela.rentexpressweb.security.EmployeeSessionResolver;
import com.pinguela.rentexpressweb.security.SessionManager;
import com.pinguela.rentexpres.service.HeadquartersService;
import com.pinguela.rentexpres.service.impl.HeadquartersServiceImpl;
import com.pinguela.rentexpressweb.util.LegacyDateUtils;
import com.pinguela.rentexpressweb.util.Views;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.StringJoiner;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Servlet implementation class EmployeeProfileServlet
 */
@WebServlet("/app/employees/profile")
public class EmployeeProfileServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private static final Logger LOGGER = LogManager.getLogger(EmployeeProfileServlet.class);

    private static final String DATE_DISPLAY_PATTERN = "dd/MM/yyyy HH:mm";
    private static final String DEFAULT_VALUE = "No disponible";
    private static final String STATUS_LABEL_ACTIVE = "Activo";
    private static final String STATUS_LABEL_INACTIVE = "Inactivo";
    private static final String STATUS_STYLE_ACTIVE = "bg-success-subtle text-success fw-semibold";
    private static final String STATUS_STYLE_INACTIVE = "bg-secondary-subtle text-secondary fw-semibold";

    private static final String PROFILE_KEY_EMPLOYEE_ID = "employeeId";
    private static final String PROFILE_KEY_FULL_NAME = "fullName";
    private static final String PROFILE_KEY_ACCOUNT_NAME = "accountName";
    private static final String PROFILE_KEY_EMAIL = "email";
    private static final String PROFILE_KEY_PHONE = "phone";
    private static final String PROFILE_KEY_ROLE = "role";
    private static final String PROFILE_KEY_HEADQUARTERS = "headquarters";
    private static final String PROFILE_KEY_HEADQUARTERS_LOCATION = "headquartersLocation";
    private static final String PROFILE_KEY_HEADQUARTERS_PHONE = "headquartersPhone";
    private static final String PROFILE_KEY_HEADQUARTERS_EMAIL = "headquartersEmail";
    private static final String PROFILE_KEY_STATUS_LABEL = "statusLabel";
    private static final String PROFILE_KEY_STATUS_STYLE = "statusStyle";
    private static final String PROFILE_KEY_CREATED_AT = "createdAt";
    private static final String PROFILE_KEY_UPDATED_AT = "updatedAt";

    private final RoleService roleService = new RoleServiceImpl();
    private final HeadquartersService headquartersService = new HeadquartersServiceImpl();

    /**
     * @see HttpServlet#HttpServlet()
     */
    public EmployeeProfileServlet() {
        super();
    }

    /**
     * @see HttpServlet#doGet(HttpServletRequest, HttpServletResponse)
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Object currentUser = SessionManager.getAttribute(request, AppConstants.ATTR_CURRENT_USER);
        if (currentUser == null) {
            SessionManager.setAttribute(request, AppConstants.ATTR_FLASH_ERROR,
                    "Inicia sesión para consultar tu perfil de empleado.");
            response.sendRedirect(request.getContextPath() + SecurityConstants.LOGIN_ENDPOINT);
            return;
        }

        EmployeeSessionResolver.refresh(request);
        Object employeeAttr = SessionManager.getAttribute(request, AppConstants.ATTR_CURRENT_EMPLOYEE);
        if (!(employeeAttr instanceof EmployeeDTO)) {
            SessionManager.setAttribute(request, AppConstants.ATTR_FLASH_ERROR,
                    "Tu cuenta no está vinculada a un perfil de empleado.");
            response.sendRedirect(request.getContextPath() + SecurityConstants.HOME_ENDPOINT);
            return;
        }

        disableCaching(response);
        exposeFlashMessages(request);

        EmployeeDTO employee = (EmployeeDTO) employeeAttr;
        Map<String, Object> profile = buildEmployeeProfile(employee);

        request.setAttribute(AppConstants.ATTR_PAGE_TITLE, "Perfil del empleado");
        request.setAttribute(EmployeeConstants.ATTR_EMPLOYEE_PROFILE, profile);
        request.getRequestDispatcher(Views.PRIVATE_EMPLOYEE_PROFILE).forward(request, response);
    }

    /**
     * @see HttpServlet#doPost(HttpServletRequest, HttpServletResponse)
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request, response);
    }

    private void exposeFlashMessages(HttpServletRequest request) {
        Object success = SessionManager.getAttribute(request, AppConstants.ATTR_FLASH_SUCCESS);
        if (success != null) {
            request.setAttribute(AppConstants.ATTR_FLASH_SUCCESS, success);
            SessionManager.removeAttribute(request, AppConstants.ATTR_FLASH_SUCCESS);
        }

        Object error = SessionManager.getAttribute(request, AppConstants.ATTR_FLASH_ERROR);
        if (error != null) {
            request.setAttribute(AppConstants.ATTR_FLASH_ERROR, error);
            SessionManager.removeAttribute(request, AppConstants.ATTR_FLASH_ERROR);
        }

        Object info = SessionManager.getAttribute(request, AppConstants.ATTR_FLASH_INFO);
        if (info != null) {
            request.setAttribute(AppConstants.ATTR_FLASH_INFO, info);
            SessionManager.removeAttribute(request, AppConstants.ATTR_FLASH_INFO);
        }
    }

    private void disableCaching(HttpServletResponse response) {
        response.setHeader("Cache-Control", "no-store");
        response.setHeader("Pragma", "no-cache");
        response.setDateHeader("Expires", 0);
    }

    private Map<String, Object> buildEmployeeProfile(EmployeeDTO employee) {
        if (employee == null) {
            return Collections.emptyMap();
        }

        Map<String, Object> profile = new LinkedHashMap<String, Object>();
        Integer identifier = resolveEmployeeId(employee);
        profile.put(PROFILE_KEY_EMPLOYEE_ID, identifier);

        String fullName = resolveFullName(employee);
        profile.put(PROFILE_KEY_FULL_NAME, defaultString(fullName));

        String accountName = employee.getEmployeeName();
        if (accountName == null || accountName.trim().isEmpty()) {
            accountName = fullName;
        }
        profile.put(PROFILE_KEY_ACCOUNT_NAME, defaultString(accountName));

        profile.put(PROFILE_KEY_EMAIL, defaultString(employee.getEmail()));
        profile.put(PROFILE_KEY_PHONE, defaultString(employee.getPhone()));
        profile.put(PROFILE_KEY_ROLE, defaultString(resolveRoleName(employee)));

        HeadquartersDTO headquarters = resolveHeadquarters(employee);
        profile.put(PROFILE_KEY_HEADQUARTERS, defaultString(resolveHeadquartersName(headquarters)));

        String location = optionalString(resolveHeadquartersLocation(headquarters));
        if (location != null) {
            profile.put(PROFILE_KEY_HEADQUARTERS_LOCATION, location);
        }

        profile.put(PROFILE_KEY_HEADQUARTERS_PHONE, defaultString(resolveHeadquartersPhone(headquarters)));
        profile.put(PROFILE_KEY_HEADQUARTERS_EMAIL, defaultString(resolveHeadquartersEmail(headquarters)));

        boolean active = Boolean.TRUE.equals(employee.getActiveStatus());
        profile.put(PROFILE_KEY_STATUS_LABEL, active ? STATUS_LABEL_ACTIVE : STATUS_LABEL_INACTIVE);
        profile.put(PROFILE_KEY_STATUS_STYLE, active ? STATUS_STYLE_ACTIVE : STATUS_STYLE_INACTIVE);

        profile.put(PROFILE_KEY_CREATED_AT, formatTimestamp(employee.getCreatedAt()));
        profile.put(PROFILE_KEY_UPDATED_AT, formatTimestamp(employee.getUpdatedAt()));

        return Collections.unmodifiableMap(profile);
    }

    private Integer resolveEmployeeId(EmployeeDTO employee) {
        if (employee.getId() != null) {
            return employee.getId();
        }
        return employee.getId();
    }

    private String resolveFullName(EmployeeDTO employee) {
        StringBuilder builder = new StringBuilder();
        appendNamePart(builder, employee.getFirstName());
        appendNamePart(builder, employee.getLastName1());
        appendNamePart(builder, employee.getLastName2());
        String fullName = builder.toString().trim();
        if (fullName.isEmpty()) {
            String employeeName = employee.getEmployeeName();
            if (employeeName != null) {
                return employeeName.trim();
            }
        }
        return fullName;
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
            String roleName = role.getRoleName().trim();
            if (!roleName.isEmpty()) {
                return roleName;
            }
        }
        Integer roleId = employee.getRoleId();
        if (roleId == null) {
            return null;
        }
        try {
            RoleDTO resolved = roleService.findById(roleId);
            if (resolved != null && resolved.getRoleName() != null) {
                String name = resolved.getRoleName().trim();
                if (!name.isEmpty()) {
                    return name;
                }
            }
        } catch (DataException ex) {
            LOGGER.warn("No se pudo recuperar el rol {}", roleId, ex);
        } catch (RentexpresException ex) {
            LOGGER.warn("No se pudo recuperar el rol {}", roleId, ex);
        }
        return null;
    }

    private HeadquartersDTO resolveHeadquarters(EmployeeDTO employee) {
        HeadquartersDTO headquarters = employee.getHeadquarters();
        if (headquarters != null && optionalString(headquarters.getName()) != null) {
            return headquarters;
        }
        return findHeadquarters(employee.getHeadquartersId());
    }

    private HeadquartersDTO findHeadquarters(Integer headquartersId) {
        if (headquartersId == null) {
            return null;
        }
        try {
            HeadquartersDTO headquarters = headquartersService.findById(headquartersId);
            if (headquarters == null) {
                LOGGER.warn("No se pudo recuperar la sede {}", headquartersId);
            }
            return headquarters;
        } catch (DataException ex) {
            LOGGER.warn("Error al recuperar la sede {}", headquartersId, ex);
            return null;
        } catch (RentexpresException ex) {
            LOGGER.warn("Error al recuperar la sede {}", headquartersId, ex);
            return null;
        }
    }

    private String resolveHeadquartersName(HeadquartersDTO headquarters) {
        if (headquarters == null) {
            return null;
        }
        return headquarters.getName();
    }

    private String resolveHeadquartersLocation(HeadquartersDTO headquarters) {
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

    private String resolveHeadquartersPhone(HeadquartersDTO headquarters) {
        if (headquarters == null) {
            return null;
        }
        return headquarters.getPhone();
    }

    private String resolveHeadquartersEmail(HeadquartersDTO headquarters) {
        if (headquarters == null) {
            return null;
        }
        return headquarters.getEmail();
    }

    private String formatTimestamp(Object value) {
        Date date = LegacyDateUtils.toDate(value);
        if (date == null) {
            return DEFAULT_VALUE;
        }
        return LegacyDateUtils.formatDate(date, DATE_DISPLAY_PATTERN);
    }

    private String defaultString(String value) {
        if (value == null) {
            return DEFAULT_VALUE;
        }
        String trimmed = value.trim();
        if (trimmed.isEmpty()) {
            return DEFAULT_VALUE;
        }
        return trimmed;
    }

    private String optionalString(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        if (trimmed.isEmpty()) {
            return null;
        }
        return trimmed;
    }
}
