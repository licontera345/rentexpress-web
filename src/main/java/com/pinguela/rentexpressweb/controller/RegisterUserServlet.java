package com.pinguela.rentexpressweb.controller;

import com.pinguela.rentexpressweb.constants.AppConstants;
import com.pinguela.rentexpressweb.constants.SecurityConstants;
import com.pinguela.rentexpressweb.constants.UserConstants;
import com.pinguela.rentexpressweb.security.SessionManager;
import com.pinguela.rentexpressweb.util.Views;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.ServletContext;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Servlet implementation class RegisterUserServlet
 */
@WebServlet("/app/users/register")
public class RegisterUserServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    public RegisterUserServlet() {
        super();
    }

    /**
     * @see HttpServlet#doGet(HttpServletRequest, HttpServletResponse)
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setAttribute(AppConstants.ATTR_PAGE_TITLE, "Crea tu cuenta");
        request.setAttribute("formData", new HashMap<String, String>());
        request.setAttribute(AppConstants.ATTR_RECENT_REGISTRATIONS, getRecentRegistrations());
        request.getRequestDispatcher(Views.PUBLIC_REGISTER_USER).forward(request, response);
    }

    /**
     * @see HttpServlet#doPost(HttpServletRequest, HttpServletResponse)
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Map<String, String> formData = new HashMap<>();
        String fullName = request.getParameter(UserConstants.PARAM_FULL_NAME);
        String email = request.getParameter(UserConstants.PARAM_EMAIL);
        String phone = request.getParameter(UserConstants.PARAM_PHONE);
        String password = request.getParameter(UserConstants.PARAM_PASSWORD);
        boolean acceptTerms = request.getParameter(UserConstants.PARAM_ACCEPT_TERMS) != null;

        formData.put(UserConstants.PARAM_FULL_NAME, fullName);
        formData.put(UserConstants.PARAM_EMAIL, email);
        formData.put(UserConstants.PARAM_PHONE, phone);

        List<String> errors = new ArrayList<>();
        if (fullName == null || fullName.trim().isEmpty()) {
            errors.add("El nombre completo es obligatorio.");
        }
        if (email == null || email.trim().isEmpty()) {
            errors.add("El correo electrónico es obligatorio.");
        }
        if (password == null || password.trim().isEmpty()) {
            errors.add("Debes indicar una contraseña.");
        }
        if (!acceptTerms) {
            errors.add("Debes aceptar los términos y condiciones de uso.");
        }

        if (!errors.isEmpty()) {
            request.setAttribute("errors", errors);
            request.setAttribute("formData", formData);
            request.setAttribute(AppConstants.ATTR_PAGE_TITLE, "Crea tu cuenta");
            request.setAttribute(AppConstants.ATTR_RECENT_REGISTRATIONS, getRecentRegistrations());
            request.getRequestDispatcher(Views.PUBLIC_REGISTER_USER).forward(request, response);
            return;
        }

        RegisteredUser registeredUser = new RegisteredUser(fullName, email, phone);
        getRegisteredUsers().add(registeredUser);

        SessionManager.setAttribute(request, AppConstants.ATTR_FLASH_SUCCESS,
                "Registro completado. Ahora puedes iniciar sesión con la cuenta demo mientras conectas la base de datos.");
        response.sendRedirect(request.getContextPath() + SecurityConstants.LOGIN_ENDPOINT);
    }

    @SuppressWarnings("unchecked")
    private List<RegisteredUser> getRegisteredUsers() {
        ServletContext context = getServletContext();
        List<RegisteredUser> list = (List<RegisteredUser>) context.getAttribute(AppConstants.CONTEXT_REGISTERED_USERS);
        if (list == null) {
            list = Collections.synchronizedList(new ArrayList<RegisteredUser>());
            context.setAttribute(AppConstants.CONTEXT_REGISTERED_USERS, list);
        }
        return list;
    }

    private List<RegisteredUser> getRecentRegistrations() {
        List<RegisteredUser> copy = new ArrayList<RegisteredUser>(getRegisteredUsers());
        Collections.sort(copy, new Comparator<RegisteredUser>() {
            public int compare(RegisteredUser first, RegisteredUser second) {
                return second.getRegisteredAt().compareTo(first.getRegisteredAt());
            }
        });
        if (copy.size() > 5) {
            return new ArrayList<RegisteredUser>(copy.subList(0, 5));
        }
        return copy;
    }

    private static final class RegisteredUser {
        private final String fullName;
        private final String email;
        private final String phone;
        private final Date registeredAt;

        private RegisteredUser(String fullName, String email, String phone) {
            this.fullName = fullName;
            this.email = email;
            this.phone = phone;
            this.registeredAt = new Date();
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

        public Date getRegisteredAt() {
            return registeredAt;
        }
    }
}
