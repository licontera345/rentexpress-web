package com.pinguela.rentexpressweb.controller;

import com.pinguela.rentexpres.model.RoleDTO;
import com.pinguela.rentexpres.model.UserDTO;
import com.pinguela.rentexpres.service.RoleService;
import com.pinguela.rentexpres.service.UserService;
import com.pinguela.rentexpres.service.impl.RoleServiceImpl;
import com.pinguela.rentexpres.service.impl.UserServiceImpl;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@WebServlet("/public/users/detail")
public class PublicUserDetailServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private static final Logger LOGGER = LogManager.getLogger(PublicUserDetailServlet.class);

    private final UserService userService = new UserServiceImpl();
    private final RoleService roleService = new RoleServiceImpl();

    public PublicUserDetailServlet() {
        super();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Integer userId = parseInteger(request.getParameter("id"));
        if (userId == null) {
            response.sendRedirect(request.getContextPath() + "/public/users?error=notfound");
            return;
        }

        UserDTO user = loadUser(userId);
        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/public/users?error=notfound");
            return;
        }

        String roleName = resolveRoleName(user.getRoleId());

        request.setAttribute("item", user);
        request.setAttribute("roleName", roleName);

        request.getRequestDispatcher("/public/user/user_detail.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request, response);
    }

    private Integer parseInteger(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        try {
            return Integer.valueOf(value.trim());
        } catch (NumberFormatException ex) {
            return null;
        }
    }

    private UserDTO loadUser(Integer userId) {
        try {
            return userService.findById(userId);
        } catch (Exception ex) {
            LOGGER.error("No se pudo recuperar el usuario {}", userId, ex);
            return null;
        }
    }

    private String resolveRoleName(Integer roleId) {
        if (roleId == null) {
            return null;
        }
        try {
            RoleDTO role = roleService.findById(roleId);
            return role != null ? role.getRoleName() : null;
        } catch (Exception ex) {
            LOGGER.warn("No se pudo recuperar el rol {}", roleId, ex);
            return null;
        }
    }
}
