package com.pinguela.rentexpressweb.controller;

import java.io.IOException;

import jakarta.servlet.http.Cookie;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jasypt.exceptions.EncryptionOperationNotPossibleException;

import com.pinguela.rentexpres.exception.RentexpresException;
import com.pinguela.rentexpres.model.EmployeeDTO;
import com.pinguela.rentexpres.model.UserDTO;
import com.pinguela.rentexpres.service.EmployeeService;
import com.pinguela.rentexpres.service.MailService;
import com.pinguela.rentexpres.service.UserService;
import com.pinguela.rentexpres.service.impl.EmployeeServiceImpl;
import com.pinguela.rentexpres.service.impl.UserServiceImpl;
import com.pinguela.rentexpressweb.constants.AppConstants;
import com.pinguela.rentexpressweb.security.CookieManager;
import com.pinguela.rentexpressweb.security.TwoFactorManager;
import com.pinguela.rentexpressweb.util.MessageResolver;
import com.pinguela.rentexpressweb.util.SessionManager;
import com.pinguela.rentexpressweb.util.Views;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet(name = "LoginServlet", urlPatterns = AppConstants.ROUTE_PUBLIC_LOGIN_CONTROLLER)
public class LoginServlet extends BasePrivateServlet {

    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = LogManager.getLogger(LoginServlet.class);

    private final UserService userService;
    private final EmployeeService employeeService;
    private final MailService mailService;

    /*
     * Construye el servlet utilizando las implementaciones estándar de servicios
     * para usuarios, empleados y correo, adecuadas para el entorno productivo.
     */
    public LoginServlet() {
        this(new UserServiceImpl(), new EmployeeServiceImpl(), new com.pinguela.rentexpres.service.impl.MailServiceImpl());
    }

    /*
     * Permite inyectar servicios personalizados (por ejemplo en pruebas) para
     * gestionar el inicio de sesión y la verificación en dos pasos.
     */
    LoginServlet(UserService userService, EmployeeService employeeService, MailService mailService) {
        this.userService = userService;
        this.employeeService = employeeService;
        this.mailService = mailService;
    }

    @Override
    /*
     * Muestra el formulario de login preparando la codificación y precargando los
     * datos recordados en cookies.
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        configureEncoding(request, response);
        populateRememberedEmail(request);
        forward(request, response, Views.PUBLIC_LOGIN);
    }

    @Override
    /*
     * Gestiona el envío del formulario de login delegando el flujo principal en
     * el método dedicado al procesamiento de credenciales.
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        configureEncoding(request, response);
        handleLogin(request, response);
    }

    /*
     * Orquesta la autenticación del usuario o empleado, validando parámetros,
     * estableciendo mensajes de error y activando el proceso de doble factor cuando
     * es necesario.
     */
    private void handleLogin(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        String type = request.getParameter("type");
        boolean rememberSelected = isRememberSelected(request);

        request.setAttribute(AppConstants.ATTR_LOGIN_EMAIL, email);
        request.setAttribute("selectedType", type);
        request.setAttribute("rememberSelected", rememberSelected);

        if (email == null || email.isEmpty() || password == null || password.isEmpty()) {
            setError(request, "login.error.invalid");
            forward(request, response, Views.PUBLIC_LOGIN);
            return;
        }

        try {
            boolean employeeSelected = "employee".equalsIgnoreCase(type);
            boolean userSelected = "user".equalsIgnoreCase(type);

            if (employeeSelected) {
                authenticateEmployee(request, response, email, password, rememberSelected);
                return;
            }
            UserDTO user = authenticateUserCredentials(email, password);
            if (user != null) {
                initiateTwoFactor(request, response, user, rememberSelected);
                return;
            }

            if (userSelected) {
                setError(request, "login.error.invalid");
                forward(request, response, Views.PUBLIC_LOGIN);
                return;
            }

            request.setAttribute("selectedType", "employee");
            authenticateEmployee(request, response, email, password, rememberSelected);
        } catch (RentexpresException ex) {
            LOGGER.error("Error durante el inicio de sesión", ex);
            setError(request, "login.error.generic");
            forward(request, response, Views.PUBLIC_LOGIN);
        }
    }

    /*
     * Valida las credenciales del empleado, renueva la sesión, gestiona la opción
     * de recordar usuario y redirige al panel privado en caso de éxito.
     */
    private void authenticateEmployee(HttpServletRequest request, HttpServletResponse response, String email, String password,
            boolean rememberSelected) throws ServletException, IOException, RentexpresException {
        EmployeeDTO employee = null;
        try {
            employee = employeeService.autenticar(email, password);
        } catch (EncryptionOperationNotPossibleException ex) {
            LOGGER.warn("No ha sido posible verificar la contraseña cifrada del empleado {}", email, ex);
        }
        if (employee == null) {
            setError(request, "login.error.invalid");
            forward(request, response, Views.PUBLIC_LOGIN);
            return;
        }
        renewSession(request);
        SessionManager.set(request, AppConstants.ATTR_CURRENT_EMPLOYEE, employee);
        SessionManager.remove(request, AppConstants.ATTR_CURRENT_USER);
        if (rememberSelected) {
            rememberUser(request, response, email);
        } else {
            forgetUser(request, response);
        }
        redirectAfterLogin(request, response, Views.PRIVATE_DASHBOARD);
    }

    /*
     * Encapsula la llamada al servicio de usuarios para verificar las
     * credenciales del cliente.
     */
    private UserDTO authenticateUserCredentials(String email, String password) throws RentexpresException {
        return userService.authenticate(email, password);
    }

    /*
     * Prepara el inicio de sesión de clientes eliminando sesiones anteriores,
     * disparando el envío del código de doble factor y gestionando el recuerdo del
     * usuario.
     */
    private void initiateTwoFactor(HttpServletRequest request, HttpServletResponse response, UserDTO user,
            boolean rememberSelected) throws IOException {
        renewSession(request);
        SessionManager.remove(request, AppConstants.ATTR_CURRENT_EMPLOYEE);
        SessionManager.remove(request, AppConstants.ATTR_CURRENT_USER);

        boolean sent = TwoFactorManager.startTwoFactor(request, user, rememberSelected, mailService);
        if (!sent) {
            LOGGER.warn("Falling back to direct login because the 2FA code could not be sent to {}",
                    user != null ? user.getEmail() : null);
            TwoFactorManager.clearTwoFactorState(request);
            completeUserLogin(request, response, user, rememberSelected);
            return;
        }

        response.sendRedirect(request.getContextPath() + "/public/security/verify-2fa");
    }

    /*
     * Completa el inicio de sesión de clientes cuando el envío del código 2FA no es
     * posible, asegurando una experiencia consistente con el inicio de sesión de
     * empleados.
     */
    private void completeUserLogin(HttpServletRequest request, HttpServletResponse response, UserDTO user,
            boolean rememberSelected) throws IOException {
        if (user == null) {
            LOGGER.error("Cannot complete login without user context after 2FA fallback");
            setError(request, "login.error.generic");
            forwardSilently(request, response, Views.PUBLIC_LOGIN);
            return;
        }

        SessionManager.set(request, AppConstants.ATTR_CURRENT_USER, user);
        SessionManager.remove(request, AppConstants.ATTR_CURRENT_EMPLOYEE);

        if (rememberSelected) {
            rememberUser(request, response, user.getEmail());
        } else {
            forgetUser(request, response);
        }

        redirectAfterLogin(request, response, Views.PUBLIC_INDEX);
    }

    /*
     * Intenta realizar un forward a la vista indicada y, si se produce un error,
     * registra la incidencia y realiza una redirección segura al login.
     */
    private void forwardSilently(HttpServletRequest request, HttpServletResponse response, String view) throws IOException {
        try {
            forward(request, response, view);
        } catch (ServletException ex) {
            LOGGER.error("Error reenviando a la vista {}", view, ex);
            response.sendRedirect(request.getContextPath() + Views.PUBLIC_LOGIN);
        }
    }

    /*
     * Redirige al usuario a la URL objetivo almacenada en sesión tras el login o
     * al destino por defecto si no existe dicho objetivo.
     */
    private void redirectAfterLogin(HttpServletRequest request, HttpServletResponse response, String defaultPath)
            throws IOException {
        Object target = SessionManager.get(request, AppConstants.ATTR_REDIRECT_TARGET);
        if (target instanceof String) {
            String redirectPath = (String) target;
            if (redirectPath.length() > 0 && redirectPath.charAt(0) == '/') {
                SessionManager.remove(request, AppConstants.ATTR_REDIRECT_TARGET);
                response.sendRedirect(request.getContextPath() + redirectPath);
                return;
            }
        }
        response.sendRedirect(request.getContextPath() + defaultPath);
    }

    /*
     * Recupera de la cookie "remember me" el correo almacenado para precargarlo
     * en el formulario de inicio de sesión.
     */
    private void populateRememberedEmail(HttpServletRequest request) {
        Cookie cookie = CookieManager.getCookie(request, AppConstants.COOKIE_REMEMBER_USER);
        if (cookie != null) {
            request.setAttribute(AppConstants.ATTR_LOGIN_EMAIL, cookie.getValue());
            request.setAttribute("rememberSelected", Boolean.TRUE);
        }
    }

    /*
     * Comprueba si el usuario ha marcado la opción de recordar su email en el
     * formulario de login.
     */
    private boolean isRememberSelected(HttpServletRequest request) {
        String remember = request.getParameter(AppConstants.PARAM_REMEMBER_ME);
        return remember != null && "on".equalsIgnoreCase(remember);
    }

    /*
     * Persiste en una cookie el correo electrónico del usuario cuando solicita la
     * funcionalidad "remember me".
     */
    private void rememberUser(HttpServletRequest request, HttpServletResponse response, String email) {
        if (email != null && !email.isEmpty()) {
            CookieManager.addCookie(request, response, AppConstants.COOKIE_REMEMBER_USER, email, 30);
        }
    }

    /*
     * Elimina la cookie de recordatorio de usuario cuando no debe mantenerse
     * almacenada.
     */
    private void forgetUser(HttpServletRequest request, HttpServletResponse response) {
        CookieManager.removeCookie(request, response, AppConstants.COOKIE_REMEMBER_USER);
    }

    /*
     * Invalida la sesión actual para evitar fijación y crea una nueva sesión
     * limpia previa al proceso de autenticación.
     */
    private void renewSession(HttpServletRequest request) {
        if (request == null) {
            return;
        }
        SessionManager.invalidate(request);
        request.getSession(true);
    }

    /*
     * Registra en la request el mensaje de error internacionalizado asociado a la
     * clave indicada.
     */
    private void setError(HttpServletRequest request, String messageKey) {
        request.setAttribute(AppConstants.ATTR_GENERAL_ERROR, MessageResolver.getMessage(request, messageKey));
    }

    @Override
    /*
     * Devuelve el logger específico de este servlet para cumplir con el contrato
     * de la clase base.
     */
    protected Logger getLogger() {
        return LOGGER;
    }
}
