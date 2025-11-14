package com.pinguela.rentexpressweb.controller;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Collections;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.pinguela.rentexpres.exception.RentexpresException;
import com.pinguela.rentexpres.model.AddressDTO;
import com.pinguela.rentexpres.model.UserDTO;
import com.pinguela.rentexpres.service.AddressService;
import com.pinguela.rentexpres.service.CityService;
import com.pinguela.rentexpres.service.MailService;
import com.pinguela.rentexpres.service.ProvinceService;
import com.pinguela.rentexpres.service.UserService;
import com.pinguela.rentexpres.service.impl.AddressServiceImpl;
import com.pinguela.rentexpres.service.impl.CityServiceImpl;
import com.pinguela.rentexpres.service.impl.ProvinceServiceImpl;
import com.pinguela.rentexpres.service.impl.UserServiceImpl;
import com.pinguela.rentexpressweb.constants.AppConstants;
import com.pinguela.rentexpressweb.security.TwoFactorManager;
import com.pinguela.rentexpressweb.util.MessageResolver;
import com.pinguela.rentexpressweb.util.Views;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/public/users/register")
public class PublicUserServlet extends BasePrivateServlet {

    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = LogManager.getLogger(PublicUserServlet.class);

    private final UserService userService = new UserServiceImpl();
    private final ProvinceService provinceService = new ProvinceServiceImpl();
    private final CityService cityService = new CityServiceImpl();
    private final AddressService addressService = new AddressServiceImpl();
    private final MailService mailService = new com.pinguela.rentexpres.service.impl.MailServiceImpl();

    @Override
    /*
     * Carga el formulario de registro público recuperando provincias y dejando la
     * lista de ciudades vacía hasta la selección del usuario.
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        configureEncoding(request, response);

        try {
            loadLocationData(request);
            request.getRequestDispatcher(Views.PUBLIC_REGISTER_USER).forward(request, response);
        } catch (RentexpresException ex) {
            LOGGER.error("Error loading provinces or cities", ex);
            throw new ServletException("Unable to load registration data", ex);
        }
    }

    @Override
    /*
     * Procesa el alta de un nuevo cliente registrando su dirección, usuario y
     * activando el flujo de doble factor de autenticación.
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        configureEncoding(request, response);

        try {
            loadLocationData(request);

            AddressDTO address = createAddress(request);
            if (!addressService.create(address)) {
                throw new RentexpresException("Unable to persist address for new user");
            }

            UserDTO user = createUser(request, address.getId());
            if (!userService.create(user)) {
                throw new RentexpresException("Unable to persist user record");
            }

            UserDTO persistedUser = userService.findById(user.getUserId());
            if (persistedUser == null) {
                throw new RentexpresException("Unable to reload persisted user for 2FA");
            }

            if (!TwoFactorManager.startTwoFactor(request, persistedUser, false, mailService)) {
                request.setAttribute(AppConstants.ATTR_FLASH_ERROR,
                        MessageResolver.getMessage(request, "twofactor.email.error"));
                request.getRequestDispatcher(Views.PUBLIC_REGISTER_USER).forward(request, response);
                return;
            }

            response.sendRedirect(request.getContextPath() + "/public/security/verify-2fa");
        } catch (RentexpresException | NumberFormatException | DateTimeParseException ex) {
            LOGGER.error("Error creating user", ex);
            reloadAfterFailure(request, response);
        }
    }

    @Override
    /*
     * Devuelve el logger asociado a este servlet para integrarse con la jerarquía
     * de controladores.
     */
    protected Logger getLogger() {
        return LOGGER;
    }

    private void loadLocationData(HttpServletRequest request) throws RentexpresException {
        request.setAttribute(AppConstants.ATTR_PROVINCES, provinceService.findAll());

        Integer provinceId = parseInt(p(request, AppConstants.PARAM_PROVINCE_ID));
        if (provinceId != null) {
            request.setAttribute(AppConstants.ATTR_CITIES, cityService.findByProvinceId(provinceId));
        } else {
            request.setAttribute(AppConstants.ATTR_CITIES, Collections.emptyList());
        }
    }

    private AddressDTO createAddress(HttpServletRequest request) {
        AddressDTO address = new AddressDTO();
        address.setStreet(p(request, "street"));
        address.setNumber(p(request, AppConstants.PARAM_ADDRESS_NUMBER));

        Integer provinceId = parseInt(p(request, AppConstants.PARAM_PROVINCE_ID));
        if (provinceId != null) {
            address.setProvinceId(provinceId);
        }

        Integer cityId = parseInt(p(request, AppConstants.PARAM_CITY_ID));
        if (cityId != null) {
            address.setCityId(cityId);
        }

        return address;
    }

    private UserDTO createUser(HttpServletRequest request, Integer addressId) {
        UserDTO user = new UserDTO();
        user.setFirstName(p(request, "firstName"));
        user.setLastName1(p(request, "lastName1"));
        user.setLastName2(p(request, "lastName2"));
        user.setEmail(p(request, "email"));
        user.setUsername(p(request, "username"));
        user.setPassword(p(request, "password"));
        user.setPhone(p(request, "phone"));
        user.setRoleId(AppConstants.ROLE_CLIENT);
        user.setActiveStatus(Boolean.TRUE);
        user.setAddressId(addressId);

        String birth = p(request, "birthDate");
        if (birth != null && !birth.isEmpty()) {
            user.setBirthDate(LocalDate.parse(birth));
        }

        return user;
    }

    private void reloadAfterFailure(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            loadLocationData(request);
        } catch (RentexpresException ex) {
            LOGGER.error("Error reloading data after failure", ex);
            throw new ServletException("Unable to load registration data", ex);
        }
        request.getRequestDispatcher(Views.PUBLIC_REGISTER_USER).forward(request, response);
    }

    private String p(HttpServletRequest request, String name) {
        return request.getParameter(name);
    }

    private Integer parseInt(String value) {
        if (value == null || value.isEmpty()) {
            return null;
        }
        return Integer.valueOf(value);
    }
}
