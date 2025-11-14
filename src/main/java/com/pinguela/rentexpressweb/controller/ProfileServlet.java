package com.pinguela.rentexpressweb.controller;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Locale;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.pinguela.rentexpres.exception.RentexpresException;
import com.pinguela.rentexpres.model.AddressDTO;
import com.pinguela.rentexpres.model.EmployeeDTO;
import com.pinguela.rentexpres.model.UserDTO;
import com.pinguela.rentexpres.service.AddressService;
import com.pinguela.rentexpres.service.EmployeeService;
import com.pinguela.rentexpres.service.UserService;
import com.pinguela.rentexpres.service.impl.AddressServiceImpl;
import com.pinguela.rentexpres.service.impl.EmployeeServiceImpl;
import com.pinguela.rentexpres.service.impl.UserServiceImpl;
import com.pinguela.rentexpressweb.constants.AppConstants;
import com.pinguela.rentexpressweb.constants.EmployeeConstants;
import com.pinguela.rentexpressweb.constants.UserConstants;
import com.pinguela.rentexpressweb.util.MessageResolver;
import com.pinguela.rentexpressweb.util.SessionManager;
import com.pinguela.rentexpressweb.util.Views;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet(AppConstants.ROUTE_PRIVATE_PROFILE)
public class ProfileServlet extends BasePrivateServlet {

    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = LogManager.getLogger(ProfileServlet.class);

    private final UserService userService;
    private final EmployeeService employeeService;
    private final AddressService addressService;

    /*
     * Crea el servlet utilizando las implementaciones estándar de servicios para
     * usuarios, empleados y direcciones.
     */
    public ProfileServlet() {
        this(new UserServiceImpl(), new EmployeeServiceImpl(), new AddressServiceImpl());
    }

    /*
     * Permite inyectar servicios específicos, útil en escenarios de prueba o
     * configuraciones avanzadas.
     */
    ProfileServlet(UserService userService, EmployeeService employeeService, AddressService addressService) {
        this.userService = userService;
        this.employeeService = employeeService;
        this.addressService = addressService;
    }

    @Override
    /*
     * Atiende la visualización del perfil asegurando la autenticación, cargando
     * los datos del usuario o empleado y reenviando a la vista privada.
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        configureEncoding(request, response);

        if (!ensureAuthenticated(request, response)) {
            return;
        }

        Object sessionUser = SessionManager.get(request, AppConstants.ATTR_CURRENT_USER);
        Object sessionEmployee = SessionManager.get(request, AppConstants.ATTR_CURRENT_EMPLOYEE);

        try {
            if (sessionUser instanceof UserDTO) {
                UserDTO userDTO = (UserDTO) sessionUser;
                loadUserProfile(request, userDTO);
            } else if (sessionEmployee instanceof EmployeeDTO) {
                EmployeeDTO employeeDTO = (EmployeeDTO) sessionEmployee;
                loadEmployeeProfile(request, employeeDTO);
            } else {
                response.sendRedirect(request.getContextPath() + Views.PUBLIC_LOGIN);
                return;
            }
        } catch (RentexpresException ex) {
            LOGGER.error("Unable to load profile information", ex);
            request.setAttribute(AppConstants.ATTR_GENERAL_ERROR,
                    MessageResolver.getMessage(request, "profile.error.load"));
        }

        forward(request, response, Views.PRIVATE_PROFILE);
    }

    /*
     * Recupera la información completa del cliente autenticado, incluyendo datos
     * personales, dirección y fechas relevantes.
     */
    private void loadUserProfile(HttpServletRequest request, UserDTO sessionUser) throws RentexpresException {
        request.setAttribute(AppConstants.ATTR_PROFILE_TYPE, "user");

        UserDTO user = sessionUser;
        if (sessionUser != null && sessionUser.getUserId() != null) {
            UserDTO reloaded = userService.findById(sessionUser.getUserId());
            if (reloaded != null) {
                user = reloaded;
            }
        }

        request.setAttribute(UserConstants.ATTR_PROFILE_DATA, user);

        if (user != null && user.getAddressId() != null) {
            AddressDTO address = addressService.findById(user.getAddressId());
            request.setAttribute(AppConstants.ATTR_PROFILE_ADDRESS, address);
        } else {
            request.setAttribute(AppConstants.ATTR_PROFILE_ADDRESS, null);
        }

        LocalDate birthDate = user != null ? user.getBirthDate() : null;
        LocalDateTime createdAt = user != null ? user.getCreatedAt() : null;
        LocalDateTime updatedAt = user != null ? user.getUpdatedAt() : null;
        applyDateAttributes(request, birthDate, createdAt, updatedAt);
    }

    /*
     * Carga los datos del empleado autenticado refrescando la información desde el
     * servicio y preparando las fechas para la vista.
     */
    private void loadEmployeeProfile(HttpServletRequest request, EmployeeDTO sessionEmployee) throws RentexpresException {
        request.setAttribute(AppConstants.ATTR_PROFILE_TYPE, "employee");

        EmployeeDTO employee = sessionEmployee;
        if (sessionEmployee != null && sessionEmployee.getId() != null) {
            EmployeeDTO reloaded = employeeService.findById(sessionEmployee.getId());
            if (reloaded != null) {
                employee = reloaded;
            }
        }

        request.setAttribute(EmployeeConstants.ATTR_PROFILE_DATA, employee);

        LocalDateTime createdAt = employee != null ? employee.getCreatedAt() : null;
        LocalDateTime updatedAt = employee != null ? employee.getUpdatedAt() : null;
        applyDateAttributes(request, null, createdAt, updatedAt);
    }

    /*
     * Formatea las fechas a mostrar en el perfil según la configuración regional de
     * la petición y las expone como atributos.
     */
    private void applyDateAttributes(HttpServletRequest request, LocalDate birthDate, LocalDateTime createdAt,
            LocalDateTime updatedAt) {
        Locale locale = request != null && request.getLocale() != null ? request.getLocale() : Locale.getDefault();

        DateTimeFormatter dateFormatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM).withLocale(locale);
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM, FormatStyle.SHORT)
                .withLocale(locale);

        String birthDateText = birthDate != null ? dateFormatter.format(birthDate) : null;
        String createdAtText = createdAt != null ? dateTimeFormatter.format(createdAt) : null;
        String updatedAtText = updatedAt != null ? dateTimeFormatter.format(updatedAt) : null;

        request.setAttribute(AppConstants.ATTR_PROFILE_BIRTH_DATE, birthDateText);
        request.setAttribute(AppConstants.ATTR_PROFILE_CREATED_AT, createdAtText);
        request.setAttribute(AppConstants.ATTR_PROFILE_UPDATED_AT, updatedAtText);
    }

    @Override
    /*
     * Facilita el logger asociado a este servlet para integrarse con la clase base.
     */
    protected Logger getLogger() {
        return LOGGER;
    }
}
