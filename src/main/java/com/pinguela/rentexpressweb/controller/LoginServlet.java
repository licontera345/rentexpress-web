package com.pinguela.rentexpressweb.controller;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.pinguela.rentexpres.model.EmployeeDTO;
import com.pinguela.rentexpres.model.UserDTO;
import com.pinguela.rentexpres.service.EmployeeService;
import com.pinguela.rentexpres.service.UserService;
import com.pinguela.rentexpres.service.impl.EmployeeServiceImpl;
import com.pinguela.rentexpres.service.impl.UserServiceImpl;
import com.pinguela.rentexpressweb.security.CookieManager;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private static final Logger LOGGER = LogManager.getLogger(LoginServlet.class);
    private static final int REMEMBER_ME_DAYS = 7;

    private final UserService userService = new UserServiceImpl();
    private final EmployeeService employeeService = new EmployeeServiceImpl();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (request.getAttribute("email") == null) {
            Cookie remembered = CookieManager.getCookie(request, "rememberUser");
            if (remembered != null) {
                request.setAttribute("email", remembered.getValue());
            }
        }
        request.getRequestDispatcher("/public/views/login.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Map<String, String> errors = new LinkedHashMap<String, String>();

        String email = sanitize(request.getParameter("email"));
        String password = sanitize(request.getParameter("password"));
        boolean remember = request.getParameter("remember") != null;

        if (email == null || email.isEmpty()) {
            errors.put("email", "El correo electrónico es obligatorio.");
        }
        if (password == null || password.isEmpty()) {
            errors.put("password", "La contraseña es obligatoria.");
        }

        UserDTO user = null;
        if (errors.isEmpty()) {
            try {
                user = userService.authenticate(email, password);
                if (user == null) {
                    errors.put("global", "Credenciales incorrectas. Inténtalo de nuevo.");
                }
            } catch (Exception ex) {
                LOGGER.error("Error autenticando al usuario {}", email, ex);
                errors.put("global", "No se pudo iniciar sesión en este momento. Inténtalo más tarde.");
            }
        }

        if (!errors.isEmpty()) {
            request.setAttribute("error", errors);
            request.setAttribute("email", email);
            request.getRequestDispatcher("/public/views/login.jsp").forward(request, response);
            return;
        }

        HttpSession session = request.getSession(true);
        session.setAttribute("currentUser", user);
        EmployeeDTO employee = resolveEmployee(user);
        if (employee != null) {
            session.setAttribute("currentEmployee", employee);
        } else {
            session.removeAttribute("currentEmployee");
        }
        session.removeAttribute("resetCode");

        if (remember) {
            CookieManager.addCookie(response, "rememberUser", user.getEmail(), REMEMBER_ME_DAYS);
        } else {
            Cookie cookie = CookieManager.getCookie(request, "rememberUser");
            if (cookie != null) {
                CookieManager.removeCookie(response, "rememberUser");
            }
        }

        response.sendRedirect(request.getContextPath() + "/private/user/user_home.jsp");
    }

    private String sanitize(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    private EmployeeDTO resolveEmployee(UserDTO user) {
        if (user == null || user.getEmail() == null) {
            return null;
        }
        String normalized = user.getEmail().trim().toLowerCase(Locale.ROOT);
        try {
            List<EmployeeDTO> employees = employeeService.findAll();
            if (employees == null) {
                return null;
            }
            for (int i = 0; i < employees.size(); i++) {
                EmployeeDTO candidate = employees.get(i);
                if (candidate == null || candidate.getEmail() == null) {
                    continue;
                }
                String email = candidate.getEmail().trim().toLowerCase(Locale.ROOT);
                if (normalized.equals(email)) {
                    return candidate;
                }
            }
        } catch (Exception ex) {
            LOGGER.warn("No se pudo determinar si {} es empleado", user.getEmail(), ex);
        }
        return null;
    }
}
