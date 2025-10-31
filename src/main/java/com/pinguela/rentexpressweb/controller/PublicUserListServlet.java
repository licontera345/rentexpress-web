package com.pinguela.rentexpressweb.controller;

import com.pinguela.rentexpres.model.RoleDTO;
import com.pinguela.rentexpres.model.UserDTO;
import com.pinguela.rentexpres.service.RoleService;
import com.pinguela.rentexpres.service.UserService;
import com.pinguela.rentexpres.service.impl.RoleServiceImpl;
import com.pinguela.rentexpres.service.impl.UserServiceImpl;
import com.pinguela.rentexpressweb.util.LegacyDateUtils;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@WebServlet("/public/users")
public class PublicUserListServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = LogManager.getLogger(PublicUserListServlet.class);
    private static final String SORT_CREATED = "createdDesc";
    private static final String SORT_NAME = "nameAsc";
    private static final String SORT_ROLE = "roleAsc";
    private static final String STATUS_ALL = "all";
    private static final String STATUS_ACTIVE = "active";
    private static final String STATUS_INACTIVE = "inactive";
    private static final String DATE_PATTERN = "dd/MM/yyyy HH:mm";

    private final UserService userService = new UserServiceImpl();
    private final RoleService roleService = new RoleServiceImpl();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Map<String, String> errors = new HashMap<String, String>();
        String search = trim(request.getParameter("search"));
        String sort = request.getParameter("sort");
        if (!SORT_NAME.equals(sort) && !SORT_ROLE.equals(sort)) { sort = SORT_CREATED; }
        String statusParam = request.getParameter("active");
        if (!STATUS_ACTIVE.equals(statusParam) && !STATUS_INACTIVE.equals(statusParam)) { statusParam = STATUS_ALL; }
        Boolean active = STATUS_ACTIVE.equals(statusParam) ? Boolean.TRUE : STATUS_INACTIVE.equals(statusParam) ? Boolean.FALSE : null;
        Integer roleId = parseRoleId(trim(request.getParameter("role")), errors);
        int page = parsePositive(request.getParameter("page"), 1);
        int size = normalizeSize(parsePositive(request.getParameter("size"), 10));

        List<UserDTO> users = loadUsers(errors);
        List<RoleDTO> roles = loadRoles(errors);
        Map<Integer, String> roleNames = new HashMap<Integer, String>();
        for (RoleDTO role : roles) { if (role != null && role.getRoleId() != null) { roleNames.put(role.getRoleId(), role.getRoleName()); } }

        String normalizedSearch = search == null ? null : search.toLowerCase(Locale.ROOT);
        List<UserDTO> filtered = new ArrayList<UserDTO>();
        int activeCount = 0; int inactiveCount = 0; Date lastRegistration = null;
        for (UserDTO user : users) {
            if (user == null) { continue; }
            if (roleId != null && !roleId.equals(user.getRoleId())) { continue; }
            if (!matchesStatus(user, active)) { continue; }
            if (normalizedSearch != null && !matchesSearch(user, normalizedSearch)) { continue; }
            filtered.add(user);
            Boolean status = user.getActiveStatus();
            if (Boolean.TRUE.equals(status)) { activeCount++; } else if (Boolean.FALSE.equals(status)) { inactiveCount++; }
            Date created = LegacyDateUtils.toDate(user.getCreatedAt());
            if (created != null && (lastRegistration == null || created.after(lastRegistration))) { lastRegistration = created; }
        }

        final String selectedSort = sort;
        Collections.sort(filtered, new Comparator<UserDTO>() {
            @Override
            public int compare(UserDTO first, UserDTO second) {
                if (SORT_NAME.equals(selectedSort)) {
                    String firstName = fullName(first);
                    String secondName = fullName(second);
                    if (firstName == null && secondName == null) { return 0; }
                    if (firstName == null) { return 1; }
                    if (secondName == null) { return -1; }
                    int result = firstName.compareToIgnoreCase(secondName);
                    if (result != 0) { return result; }
                } else if (SORT_ROLE.equals(selectedSort)) {
                    String firstRole = roleNames.get(first.getRoleId());
                    String secondRole = roleNames.get(second.getRoleId());
                    if (firstRole == null && secondRole == null) { return 0; }
                    if (firstRole == null) { return 1; }
                    if (secondRole == null) { return -1; }
                    int result = firstRole.compareToIgnoreCase(secondRole);
                    if (result != 0) { return result; }
                }
                Date firstDate = LegacyDateUtils.toDate(first.getCreatedAt());
                Date secondDate = LegacyDateUtils.toDate(second.getCreatedAt());
                if (firstDate == null && secondDate == null) { return 0; }
                if (firstDate == null) { return 1; }
                if (secondDate == null) { return -1; }
                return secondDate.compareTo(firstDate);
            }
        });

        int total = filtered.size();
        int totalPages = total == 0 ? 1 : (int) Math.ceil((double) total / size);
        if (page < 1) { page = 1; }
        if (page > totalPages) { page = totalPages; }
        int from = Math.min((page - 1) * size, total);
        int to = Math.min(from + size, total);
        List<UserDTO> pageItems = filtered.subList(from, to);

        if (!errors.isEmpty()) { request.setAttribute("error", errors); }
        request.setAttribute("items", pageItems); request.setAttribute("page", Integer.valueOf(page));
        request.setAttribute("size", Integer.valueOf(size)); request.setAttribute("total", Integer.valueOf(total));
        request.setAttribute("totalPages", Integer.valueOf(totalPages)); request.setAttribute("hasPrev", Boolean.valueOf(page > 1));
        request.setAttribute("hasNext", Boolean.valueOf(page < totalPages)); request.setAttribute("prevPage", Integer.valueOf(page > 1 ? page - 1 : 1));
        request.setAttribute("nextPage", Integer.valueOf(page < totalPages ? page + 1 : totalPages)); request.setAttribute("search", search);
        request.setAttribute("selectedRoleId", roleId); request.setAttribute("selectedActive", statusParam);
        request.setAttribute("selectedSort", sort); request.setAttribute("roles", roles);
        request.setAttribute("roleNames", roleNames); request.setAttribute("totalActive", Integer.valueOf(activeCount));
        request.setAttribute("totalInactive", Integer.valueOf(inactiveCount));
        request.setAttribute("lastRegistration", lastRegistration == null ? "-" : LegacyDateUtils.formatDate(lastRegistration, DATE_PATTERN));

        request.getRequestDispatcher("/public/user/user_list.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request, response);
    }

    private String trim(String value) { return value == null ? null : (value.trim().isEmpty() ? null : value.trim()); }
    private Integer parseRoleId(String raw, Map<String, String> errors) {
        if (raw == null) { return null; }
        try { return Integer.valueOf(raw); } catch (NumberFormatException ex) { errors.put("role", "El rol indicado no es válido."); return null; }
    }
    private int parsePositive(String raw, int defaultValue) {
        if (raw == null || raw.trim().isEmpty()) { return defaultValue; }
        try { int parsed = Integer.parseInt(raw.trim()); return parsed > 0 ? parsed : defaultValue; } catch (NumberFormatException ex) { return defaultValue; }
    }
    private int normalizeSize(int size) { return (size == 10 || size == 20 || size == 50) ? size : 10; }
    private boolean matchesStatus(UserDTO user, Boolean active) {
        if (active == null) { return true; }
        Boolean status = user.getActiveStatus();
        return status == null ? !active.booleanValue() : status.booleanValue() == active.booleanValue();
    }
    private boolean matchesSearch(UserDTO user, String search) {
        return search == null || contains(user.getUsername(), search) || contains(user.getEmail(), search)
                || contains(user.getFirstName(), search) || contains(user.getLastName1(), search)
                || contains(user.getLastName2(), search);
    }
    private boolean contains(String value, String search) { return value != null && value.toLowerCase(Locale.ROOT).contains(search); }
    private String fullName(UserDTO user) {
        StringBuilder builder = new StringBuilder();
        append(builder, user.getFirstName());
        append(builder, user.getLastName1());
        append(builder, user.getLastName2());
        return builder.length() == 0 ? user.getUsername() : builder.toString();
    }
    private void append(StringBuilder builder, String value) {
        if (value == null) { return; }
        String trimmed = value.trim();
        if (trimmed.isEmpty()) { return; }
        if (builder.length() > 0) { builder.append(' '); }
        builder.append(trimmed);
    }
    private List<UserDTO> loadUsers(Map<String, String> errors) {
        try {
            List<UserDTO> users = userService.findAll();
            if (users == null) { return new ArrayList<UserDTO>(); }
            return users;
        } catch (Exception ex) {
            LOGGER.error("No se pudieron recuperar los usuarios", ex);
            errors.put("users", "No se pudieron recuperar los usuarios.");
            return new ArrayList<UserDTO>();
        }
    }
    private List<RoleDTO> loadRoles(Map<String, String> errors) {
        try {
            List<RoleDTO> roles = roleService.findAll();
            if (roles == null) { return new ArrayList<RoleDTO>(); }
            return roles;
        } catch (Exception ex) {
            LOGGER.warn("No se pudieron recuperar los roles", ex);
            errors.put("roles", "No se pudieron recuperar los roles disponibles.");
            return new ArrayList<RoleDTO>();
        }
    }
}
