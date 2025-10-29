package com.pinguela.rentexpressweb.controller;

import com.pinguela.rentexpres.dao.HeadquartersDAO;
import com.pinguela.rentexpres.dao.impl.HeadquartersDAOImpl;
import com.pinguela.rentexpres.exception.DataException;
import com.pinguela.rentexpres.model.HeadquartersDTO;
import com.pinguela.rentexpres.util.JDBCUtils;
import com.pinguela.rentexpressweb.constants.AppConstants;
import com.pinguela.rentexpressweb.constants.VehicleConstants;
import com.pinguela.rentexpressweb.security.RememberMeManager;
import com.pinguela.rentexpressweb.security.SessionManager;
import com.pinguela.rentexpressweb.util.Views;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Servlet implementation class WelcomeServlet
 */
@WebServlet("/app/welcome")
public class WelcomeServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private static final Logger LOGGER = LogManager.getLogger(WelcomeServlet.class);

    private final HeadquartersDAO headquartersDAO = new HeadquartersDAOImpl();

    public WelcomeServlet() {
        super();
    }

    /**
     * @see HttpServlet#doGet(HttpServletRequest, HttpServletResponse)
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        RememberMeManager.applyRememberedUser(request);
        exposeFlashMessage(request);

        request.setAttribute(AppConstants.ATTR_PAGE_TITLE, "Bienvenido");
        request.setAttribute(VehicleConstants.ATTR_HEADQUARTERS, loadHeadquarters());

        request.getRequestDispatcher(Views.PUBLIC_WELCOME).forward(request, response);
    }

    /**
     * @see HttpServlet#doPost(HttpServletRequest, HttpServletResponse)
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request, response);
    }

    private void exposeFlashMessage(HttpServletRequest request) {
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
    }

    private List<HeadquartersDTO> loadHeadquarters() {
        Connection connection = null;
        try {
            connection = JDBCUtils.getConnection();
            JDBCUtils.beginTransaction(connection);
            List<HeadquartersDTO> list = headquartersDAO.findAll(connection);
            JDBCUtils.commitTransaction(connection);
            return list;
        } catch (SQLException | DataException ex) {
            JDBCUtils.rollbackTransaction(connection);
            LOGGER.error("Error al recuperar las sedes para la portada", ex);
            return new ArrayList<>();
        } finally {
            JDBCUtils.close(connection);
        }
    }
}
