package com.pinguela.rentexpressweb.controller;

import com.pinguela.rentexpres.exception.RentexpresException;
import com.pinguela.rentexpres.model.Results;
import com.pinguela.rentexpres.model.RoleDTO;
import com.pinguela.rentexpres.model.UserCriteria;
import com.pinguela.rentexpres.model.UserDTO;
import com.pinguela.rentexpres.service.RoleService;
import com.pinguela.rentexpres.service.UserService;
import com.pinguela.rentexpres.service.impl.RoleServiceImpl;
import com.pinguela.rentexpres.service.impl.UserServiceImpl;
import com.pinguela.rentexpressweb.constants.AppConstants;
import com.pinguela.rentexpressweb.constants.SecurityConstants;
import com.pinguela.rentexpressweb.constants.UserConstants;
import com.pinguela.rentexpressweb.security.CredentialStore;
import com.pinguela.rentexpressweb.security.SessionManager;
import com.pinguela.rentexpressweb.util.Views;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Servlet implementation class RegisterUserServlet
 */
@WebServlet("/app/users/register")
public class RegisterUserServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private static final Logger LOGGER = LogManager.getLogger(RegisterUserServlet.class);
    private static final int RECENT_REGISTRATION_LIMIT = 5;
    private static final DateTimeFormatter RECENT_REGISTRATION_FORMATTER =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private final UserService userService = new UserServiceImpl();
    private final RoleService roleService = new RoleServiceImpl();

    public RegisterUserServlet() {
        super();
    }

    /**
     * @see HttpServlet#doGet(HttpServletRequest, HttpServletResponse)
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setAttribute(AppConstants.ATTR_PAGE_TITLE, "Crea tu cuenta");
        request.setAttribute("formData", new HashMap<String, String>());
        request.setAttribute(AppConstants.ATTR_RECENT_REGISTRATIONS, loadRecentRegistrations());
        request.getRequestDispatcher(Views.PUBLIC_REGISTER_USER).forward(request, response);
    }

    /**
     * @see HttpServlet#doPost(HttpServletRequest, HttpServletResponse)
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Map<String, String> formData = new HashMap<String, String>();
        String fullName = trimToNull(request.getParameter(UserConstants.PARAM_FULL_NAME));
        String email = trimToNull(request.getParameter(UserConstants.PARAM_EMAIL));
        String phone = trimToNull(request.getParameter(UserConstants.PARAM_PHONE));
        String password = request.getParameter(UserConstants.PARAM_PASSWORD);
        boolean acceptTerms = request.getParameter(UserConstants.PARAM_ACCEPT_TERMS) != null;

        if (fullName != null) {
            formData.put(UserConstants.PARAM_FULL_NAME, fullName);
        } else {
            formData.put(UserConstants.PARAM_FULL_NAME, "");
        }
        if (email != null) {
            formData.put(UserConstants.PARAM_EMAIL, email);
        } else {
            formData.put(UserConstants.PARAM_EMAIL, "");
        }
        if (phone != null) {
            formData.put(UserConstants.PARAM_PHONE, phone);
        } else {
            formData.put(UserConstants.PARAM_PHONE, "");
        }

        List<String> errors = new ArrayList<String>();
        NameParts nameParts = null;
        if (fullName == null) {
            errors.add("El nombre completo es obligatorio.");
        } else {
            nameParts = extractNameParts(fullName);
            if (nameParts == null) {
                errors.add("Indica tu nombre y al menos un apellido.");
            }
        }
        if (email == null) {
            errors.add("El correo electrónico es obligatorio.");
        } else if (!isValidEmail(email)) {
            errors.add("El correo electrónico proporcionado no parece válido.");
        }

        String sanitizedPassword = password == null ? null : password.trim();
        if (sanitizedPassword == null || sanitizedPassword.isEmpty()) {
            errors.add("Debes indicar una contraseña.");
        } else if (sanitizedPassword.length() < 8) {
            errors.add("La contraseña debe tener al menos 8 caracteres.");
        }

        if (phone != null && phone.length() > 20) {
            errors.add("El teléfono no puede superar los 20 caracteres.");
        }

        if (!acceptTerms) {
            errors.add("Debes aceptar los términos y condiciones de uso.");
        }

        String sanitizedEmail = email == null ? null : email.toLowerCase(Locale.ROOT);

        if (errors.isEmpty() && sanitizedEmail != null) {
            try {
                if (isEmailAlreadyRegistered(sanitizedEmail)) {
                    errors.add("Ya existe una cuenta registrada con ese correo electrónico.");
                }
            } catch (RentexpresException ex) {
                LOGGER.error("No se pudo comprobar la disponibilidad del correo {}", sanitizedEmail, ex);
                errors.add("No se pudo validar la disponibilidad del correo. Inténtalo de nuevo en unos minutos.");
            }
        }

        Integer customerRoleId = null;
        if (errors.isEmpty()) {
            try {
                customerRoleId = resolveCustomerRoleId();
            } catch (RentexpresException ex) {
                LOGGER.error("Error obteniendo el rol de cliente", ex);
                errors.add("No se pudo preparar el registro de usuario. Inténtalo más tarde.");
            }
            if (customerRoleId == null) {
                errors.add("No se pudo determinar el rol de cliente para completar el registro.");
            }
        }

        if (!errors.isEmpty()) {
            request.setAttribute("errors", errors);
            request.setAttribute("formData", formData);
            request.setAttribute(AppConstants.ATTR_PAGE_TITLE, "Crea tu cuenta");
            request.setAttribute(AppConstants.ATTR_RECENT_REGISTRATIONS, loadRecentRegistrations());
            request.getRequestDispatcher(Views.PUBLIC_REGISTER_USER).forward(request, response);
            return;
        }

        UserDTO user = new UserDTO();
        user.setUsername(sanitizedEmail);
        user.setEmail(sanitizedEmail);
        user.setFirstName(nameParts.getFirstName());
        user.setLastName1(nameParts.getLastName1());
        user.setLastName2(nameParts.getLastName2());
        user.setPhone(phone);
        user.setPassword(sanitizedPassword);
        user.setRoleId(customerRoleId);
        user.setActiveStatus(Boolean.TRUE);

        try {
            boolean created = userService.create(user);
            if (created) {
                CredentialStore.updatePassword(getServletContext(), sanitizedEmail, sanitizedPassword);
                LOGGER.info("Registrado nuevo usuario {}", sanitizedEmail);
                SessionManager.setAttribute(request, AppConstants.ATTR_FLASH_SUCCESS,
                        "Registro completado. Ya puedes iniciar sesión con tu correo y contraseña.");
                response.sendRedirect(request.getContextPath() + SecurityConstants.LOGIN_ENDPOINT);
                return;
            }
            errors.add("No se pudo completar el registro. Inténtalo de nuevo.");
        } catch (RentexpresException ex) {
            LOGGER.error("Error creando el usuario {}", sanitizedEmail, ex);
            errors.add("Ha ocurrido un error al crear tu cuenta. Inténtalo de nuevo en unos minutos.");
        }

        request.setAttribute("errors", errors);
        request.setAttribute("formData", formData);
        request.setAttribute(AppConstants.ATTR_PAGE_TITLE, "Crea tu cuenta");
        request.setAttribute(AppConstants.ATTR_RECENT_REGISTRATIONS, loadRecentRegistrations());
        request.getRequestDispatcher(Views.PUBLIC_REGISTER_USER).forward(request, response);
    }

    private List<RecentRegistration> loadRecentRegistrations() {
        UserCriteria criteria = new UserCriteria();
        criteria.setPage(Integer.valueOf(1));
        criteria.setPageSize(Integer.valueOf(RECENT_REGISTRATION_LIMIT));
        criteria.setOrderBy("created_at");
        criteria.setOrderDir("DESC");

        try {
            Results<UserDTO> results = userService.findByCriteria(criteria);
            if (results == null || results.getItems() == null) {
                return Collections.emptyList();
            }
            List<UserDTO> items = results.getItems();
            List<RecentRegistration> recent = new ArrayList<RecentRegistration>(items.size());
            for (UserDTO user : items) {
                if (user == null) {
                    continue;
                }
                recent.add(toRecentRegistration(user));
            }
            return recent;
        } catch (RentexpresException ex) {
            LOGGER.error("No se pudieron recuperar los últimos usuarios registrados", ex);
            return Collections.emptyList();
        }
    }

    private RecentRegistration toRecentRegistration(UserDTO user) {
        String fullName = buildFullName(user);
        LocalDateTime createdAt = user.getCreatedAt();
        String formatted = createdAt != null ? createdAt.format(RECENT_REGISTRATION_FORMATTER) : "Sin fecha";
        String email = user.getEmail();
        String phone = user.getPhone();
        return new RecentRegistration(fullName, email, phone, formatted);
    }

    private String buildFullName(UserDTO user) {
        StringBuilder builder = new StringBuilder();
        appendIfPresent(builder, user.getFirstName());
        appendIfPresent(builder, user.getLastName1());
        appendIfPresent(builder, user.getLastName2());
        return builder.toString();
    }

    private void appendIfPresent(StringBuilder builder, String value) {
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

    private boolean isEmailAlreadyRegistered(String email) throws RentexpresException {
        UserCriteria criteria = new UserCriteria();
        criteria.setEmail(email);
        criteria.setPage(Integer.valueOf(1));
        criteria.setPageSize(Integer.valueOf(1));
        Results<UserDTO> results = userService.findByCriteria(criteria);
        return results != null && results.getItems() != null && !results.getItems().isEmpty();
    }

    private Integer resolveCustomerRoleId() throws RentexpresException {
        List<RoleDTO> roles = roleService.findAll();
        if (roles == null) {
            return null;
        }
        for (RoleDTO role : roles) {
            if (role == null || role.getRoleName() == null) {
                continue;
            }
            String roleName = role.getRoleName().trim();
            if (UserConstants.ROLE_NAME_CUSTOMER.equalsIgnoreCase(roleName)) {
                return role.getRoleId();
            }
        }
        return null;
    }

    private String trimToNull(String value) {
        if (value == null) {
            return null;
        }
        String cleaned = value.trim();
        return cleaned.isEmpty() ? null : cleaned;
    }

    private boolean isValidEmail(String email) {
        int atIndex = email.indexOf('@');
        if (atIndex <= 0 || atIndex == email.length() - 1) {
            return false;
        }
        int dotIndex = email.indexOf('.', atIndex);
        return dotIndex > atIndex + 1 && dotIndex < email.length() - 1;
    }

    private NameParts extractNameParts(String fullName) {
        String normalized = fullName.trim().replaceAll("\\s+", " ");
        if (normalized.isEmpty()) {
            return null;
        }
        int lastSpace = normalized.lastIndexOf(' ');
        if (lastSpace <= 0) {
            return null;
        }
        String lastName1 = normalized.substring(lastSpace + 1).trim();
        String beforeLast = normalized.substring(0, lastSpace).trim();
        if (beforeLast.isEmpty()) {
            return null;
        }
        int secondLastSpace = beforeLast.lastIndexOf(' ');
        String firstName;
        String lastName2 = null;
        if (secondLastSpace > 0) {
            lastName2 = beforeLast.substring(secondLastSpace + 1).trim();
            firstName = beforeLast.substring(0, secondLastSpace).trim();
            if (firstName.isEmpty()) {
                firstName = beforeLast.substring(0, secondLastSpace).trim();
            }
        } else {
            firstName = beforeLast;
        }
        if (firstName == null || firstName.isEmpty()) {
            return null;
        }
        if (lastName1.isEmpty()) {
            return null;
        }
        if (lastName2 != null && lastName2.isEmpty()) {
            lastName2 = null;
        }
        return new NameParts(firstName, lastName1, lastName2);
    }

    private static final class RecentRegistration {
        private final String fullName;
        private final String email;
        private final String phone;
        private final String registeredAt;

        private RecentRegistration(String fullName, String email, String phone, String registeredAt) {
            this.fullName = fullName;
            this.email = email;
            this.phone = phone;
            this.registeredAt = registeredAt;
        }

        public String getFullName() {
            return fullName;
        }

        public String getEmail() {
            return email;
        }

        public String getPhone() {
            return phone;
        }

        public String getRegisteredAt() {
            return registeredAt;
        }
    }

    private static final class NameParts {
        private final String firstName;
        private final String lastName1;
        private final String lastName2;

        private NameParts(String firstName, String lastName1, String lastName2) {
            this.firstName = firstName;
            this.lastName1 = lastName1;
            this.lastName2 = lastName2;
        }

        public String getFirstName() {
            return firstName;
        }

        public String getLastName1() {
            return lastName1;
        }

        public String getLastName2() {
            return lastName2;
        }
    }
}
