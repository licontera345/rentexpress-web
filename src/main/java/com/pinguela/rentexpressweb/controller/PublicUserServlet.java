package com.pinguela.rentexpressweb.controller;

import com.pinguela.rentexpres.exception.RentexpresException;
import com.pinguela.rentexpres.model.RoleDTO;
import com.pinguela.rentexpres.model.UserDTO;
import com.pinguela.rentexpres.service.RoleService;
import com.pinguela.rentexpres.service.UserService;
import com.pinguela.rentexpres.service.impl.RoleServiceImpl;
import com.pinguela.rentexpres.service.impl.UserServiceImpl;
import com.pinguela.rentexpressweb.constants.AppConstants;
import com.pinguela.rentexpressweb.constants.FilterConstants;
import com.pinguela.rentexpressweb.constants.UserConstants;
import com.pinguela.rentexpressweb.util.LegacyDateUtils;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Muestra un catálogo público de clientes registrados en el sistema. Permite
 * realizar búsquedas básicas, filtrar por rol y estado y consultar el detalle
 * de un usuario concreto.
 */
@WebServlet("/public/users")
public class PublicUserServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private static final Logger LOGGER = LogManager.getLogger(PublicUserServlet.class);
    private static final String DATE_DISPLAY_PATTERN = "dd/MM/yyyy HH:mm";

    private final UserService userService = new UserServiceImpl();
    private final RoleService roleService = new RoleServiceImpl();

    public PublicUserServlet() {
        super();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter(FilterConstants.PARAM_ACTION);
        if (action != null) {
            action = action.trim();
            if (action.isEmpty()) {
                action = null;
            }
        }
        if (action == null) {
            action = FilterConstants.ACTION_LIST;
        }

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

    private void handleList(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setAttribute(AppConstants.ATTR_PAGE_TITLE, "Personas registradas");

        Map<String, String> filters = buildFilters(request);
        List<String> errors = new ArrayList<>();

        Integer selectedRole = parseInteger(filters.get(UserConstants.PARAM_ROLE), errors,
                "El rol indicado no es válido.");
        Boolean activeState = parseActive(filters.get(UserConstants.PARAM_ACTIVE), errors);

        List<UserDTO> users = loadUsers();
        List<RoleDTO> roles = loadRoles();
        Map<Integer, String> roleNames = mapRoles(roles);

        List<UserDTO> filteredUsers = new ArrayList<>();
        for (UserDTO user : users) {
            if (user == null) {
                continue;
            }
            if (!matchesRole(user, selectedRole)) {
                continue;
            }
            if (!matchesActive(user, activeState)) {
                continue;
            }
            if (!matchesSearch(user, filters.get(UserConstants.PARAM_SEARCH))) {
                continue;
            }
            filteredUsers.add(user);
        }
        Collections.sort(filteredUsers, resolveComparator(filters.get(FilterConstants.PARAM_SORT), roleNames));

        Map<String, Object> pagination = buildPagination(filteredUsers, filters);
        @SuppressWarnings("unchecked")
        List<UserDTO> pageItems = (List<UserDTO>) pagination.get("items");

        Map<String, Object> summary = buildSummary(filteredUsers);

        request.setAttribute("selectedRoleId", selectedRole);
        request.setAttribute("selectedActive", filters.get(UserConstants.PARAM_ACTIVE));
        request.setAttribute("selectedSort", filters.get(FilterConstants.PARAM_SORT));
        request.setAttribute(UserConstants.ATTR_USERS, pageItems);
        request.setAttribute(UserConstants.ATTR_FILTERS, filters);
        request.setAttribute(UserConstants.ATTR_FILTER_ERRORS, errors);
        request.setAttribute(UserConstants.ATTR_ROLES, roles);
        request.setAttribute(UserConstants.ATTR_ROLE_NAMES, roleNames);
        request.setAttribute(UserConstants.ATTR_PAGINATION, pagination);
        request.setAttribute(UserConstants.ATTR_SUMMARY, summary);

        request.getRequestDispatcher("/public/user/user_list.jsp").forward(request, response);
    }

    private void handleDetail(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String rawId = request.getParameter(UserConstants.PARAM_USER_ID);
        Integer userId = parseInteger(rawId, null, null);
        if (userId == null) {
            response.sendRedirect(request.getContextPath() + "/public/users?error=notfound");
            return;
        }

        UserDTO user = findUser(userId);
        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/public/users?error=notfound");
            return;
        }

        String roleName = resolveRoleName(user.getRoleId());

        request.setAttribute(AppConstants.ATTR_PAGE_TITLE, buildDetailTitle(user));
        request.setAttribute(UserConstants.ATTR_SELECTED_USER, user);
        request.setAttribute(UserConstants.ATTR_SELECTED_ROLE_NAME, roleName);

        request.getRequestDispatcher("/public/user/user_detail.jsp").forward(request, response);
    }

    private Map<String, String> buildFilters(HttpServletRequest request) {
        Map<String, String> filters = new HashMap<>();
        filters.put(UserConstants.PARAM_SEARCH, sanitize(request.getParameter(UserConstants.PARAM_SEARCH)));
        filters.put(UserConstants.PARAM_ROLE, sanitize(request.getParameter(UserConstants.PARAM_ROLE)));
        filters.put(UserConstants.PARAM_ACTIVE, defaultActive(request.getParameter(UserConstants.PARAM_ACTIVE)));
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
            return UserConstants.VALUE_ALL;
        }
        String cleaned = raw.trim().toLowerCase(Locale.ROOT);
        if (UserConstants.VALUE_ACTIVE.equals(cleaned) || UserConstants.VALUE_INACTIVE.equals(cleaned)) {
            return cleaned;
        }
        return UserConstants.VALUE_ALL;
    }

    private String resolveSort(String rawSort) {
        if (UserConstants.VALUE_SORT_NAME_ASC.equals(rawSort)
                || UserConstants.VALUE_SORT_ROLE_ASC.equals(rawSort)) {
            return rawSort;
        }
        return UserConstants.VALUE_SORT_CREATED_DESC;
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
        if (value == null || value.isEmpty() || UserConstants.VALUE_ALL.equals(value)) {
            return null;
        }
        if (UserConstants.VALUE_ACTIVE.equals(value)) {
            return Boolean.TRUE;
        }
        if (UserConstants.VALUE_INACTIVE.equals(value)) {
            return Boolean.FALSE;
        }
        if (errors != null) {
            errors.add("El filtro de estado es desconocido.");
        }
        return null;
    }

    private List<UserDTO> loadUsers() {
        try {
            List<UserDTO> users = userService.findAll();
            return users != null ? users : Collections.emptyList();
        } catch (RentexpresException ex) {
            LOGGER.error("No se pudieron recuperar los usuarios", ex);
            return Collections.emptyList();
        }
    }

    private List<RoleDTO> loadRoles() {
        try {
            List<RoleDTO> roles = roleService.findAll();
            return roles != null ? roles : Collections.emptyList();
        } catch (RentexpresException ex) {
            LOGGER.warn("No se pudieron recuperar los roles de usuario", ex);
            return Collections.emptyList();
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

    private boolean matchesRole(UserDTO user, Integer roleId) {
        if (roleId == null) {
            return true;
        }
        return Objects.equals(user.getRoleId(), roleId);
    }

    private boolean matchesActive(UserDTO user, Boolean active) {
        if (active == null) {
            return true;
        }
        Boolean status = user.getActiveStatus();
        if (status == null) {
            return !active.booleanValue();
        }
        return status.booleanValue() == active.booleanValue();
    }

    private boolean matchesSearch(UserDTO user, String search) {
        if (search == null || search.isEmpty()) {
            return true;
        }
        String normalized = search.toLowerCase(Locale.ROOT);
        return contains(user.getUsername(), normalized)
                || contains(user.getEmail(), normalized)
                || contains(user.getFirstName(), normalized)
                || contains(user.getLastName1(), normalized)
                || contains(user.getLastName2(), normalized);
    }

    private boolean contains(String field, String search) {
        return field != null && field.toLowerCase(Locale.ROOT).contains(search);
    }

    private Comparator<UserDTO> resolveComparator(String sort, Map<Integer, String> roleNames) {
        final String effectiveSort = sort != null ? sort : UserConstants.VALUE_SORT_CREATED_DESC;
        return new Comparator<UserDTO>() {
            @Override
            public int compare(UserDTO first, UserDTO second) {
                if (first == second) {
                    return 0;
                }
                if (first == null) {
                    return 1;
                }
                if (second == null) {
                    return -1;
                }
                int result;
                if (UserConstants.VALUE_SORT_NAME_ASC.equals(effectiveSort)) {
                    result = compareFullName(first, second);
                } else if (UserConstants.VALUE_SORT_ROLE_ASC.equals(effectiveSort)) {
                    result = compareRole(first, second, roleNames);
                } else {
                    result = compareCreatedDate(first.getCreatedAt(), second.getCreatedAt());
                }
                if (result == 0) {
                    result = compareInteger(first.getUserId(), second.getUserId());
                }
                return result;
            }
        };
    }

    private int compareFullName(UserDTO first, UserDTO second) {
        String firstName = resolveFullName(first);
        String secondName = resolveFullName(second);
        if (firstName == null && secondName == null) {
            return 0;
        }
        if (firstName == null) {
            return 1;
        }
        if (secondName == null) {
            return -1;
        }
        return firstName.compareToIgnoreCase(secondName);
    }

    private int compareRole(UserDTO first, UserDTO second, Map<Integer, String> roleNames) {
        String firstRole = roleNames.get(first.getRoleId());
        String secondRole = roleNames.get(second.getRoleId());
        if (firstRole == null && secondRole == null) {
            return 0;
        }
        if (firstRole == null) {
            return 1;
        }
        if (secondRole == null) {
            return -1;
        }
        return firstRole.compareToIgnoreCase(secondRole);
    }

    private int compareCreatedDate(Object first, Object second) {
        Date firstDate = LegacyDateUtils.toDate(first);
        Date secondDate = LegacyDateUtils.toDate(second);
        if (firstDate == null && secondDate == null) {
            return 0;
        }
        if (firstDate == null) {
            return 1;
        }
        if (secondDate == null) {
            return -1;
        }
        return secondDate.compareTo(firstDate);
    }

    private int compareInteger(Integer first, Integer second) {
        if (first == null && second == null) {
            return 0;
        }
        if (first == null) {
            return 1;
        }
        if (second == null) {
            return -1;
        }
        return first.compareTo(second);
    }

    private String resolveFullName(UserDTO user) {
        StringBuilder builder = new StringBuilder();
        appendNamePart(builder, user.getFirstName());
        appendNamePart(builder, user.getLastName1());
        appendNamePart(builder, user.getLastName2());
        return builder.toString().trim();
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

    private Map<String, Object> buildPagination(List<UserDTO> users, Map<String, String> filters) {
        int total = users.size();
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

        pagination.put("items", Collections.unmodifiableList(new ArrayList<>(users.subList(fromIndex, toIndex))));
        return pagination;
    }

    private Map<String, Object> buildSummary(List<UserDTO> users) {
        long active = 0L;
        long inactive = 0L;
        Date lastCreated = null;
        for (UserDTO user : users) {
            if (user == null) {
                continue;
            }
            Boolean status = user.getActiveStatus();
            if (Boolean.TRUE.equals(status)) {
                active++;
            } else if (Boolean.FALSE.equals(status)) {
                inactive++;
            }
            Date createdAt = LegacyDateUtils.toDate(user.getCreatedAt());
            if (createdAt != null) {
                if (lastCreated == null || createdAt.after(lastCreated)) {
                    lastCreated = createdAt;
                }
            }
        }

        Map<String, Object> summary = new LinkedHashMap<>();
        summary.put("total", Long.valueOf(users.size()));
        summary.put("active", Long.valueOf(active));
        summary.put("inactive", Long.valueOf(inactive));
        summary.put("lastRegistration",
                lastCreated == null ? "-" : LegacyDateUtils.formatDate(lastCreated, DATE_DISPLAY_PATTERN));
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

    private UserDTO findUser(Integer userId) {
        try {
            return userService.findById(userId);
        } catch (RentexpresException ex) {
            LOGGER.error("No se pudo recuperar el usuario {}", userId, ex);
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

    private String buildDetailTitle(UserDTO user) {
        StringBuilder sb = new StringBuilder();
        if (user.getFirstName() != null) {
            sb.append(user.getFirstName());
        }
        if (user.getLastName1() != null) {
            if (sb.length() > 0) {
                sb.append(' ');
            }
            sb.append(user.getLastName1());
        }
        if (sb.length() == 0 && user.getUsername() != null) {
            sb.append(user.getUsername());
        }
        return sb.length() == 0 ? "Detalle de usuario" : sb.toString();
    }
}
