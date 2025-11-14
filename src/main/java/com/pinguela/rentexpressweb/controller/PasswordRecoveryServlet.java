package com.pinguela.rentexpressweb.controller;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.pinguela.rentexpres.exception.RentexpresException;
import com.pinguela.rentexpres.model.UserDTO;
import com.pinguela.rentexpres.service.MailService;
import com.pinguela.rentexpres.service.UserService;
import com.pinguela.rentexpres.service.impl.UserServiceImpl;
import com.pinguela.rentexpressweb.constants.AppConstants;
import com.pinguela.rentexpressweb.util.MessageResolver;
import com.pinguela.rentexpressweb.util.Views;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/public/security/recovery")
public class PasswordRecoveryServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = LogManager.getLogger(PasswordRecoveryServlet.class);
    private static final long CODE_TTL_MILLIS = 10 * 60 * 1000L;
    private static final List<RecoveryEntry> TOKENS = new CopyOnWriteArrayList<>();

    private final UserService userService;
    private final MailService mailService;

    /*
     * Inicializa el servlet con los servicios predeterminados de usuarios y
     * correo para la gestión del flujo de recuperación.
     */
    public PasswordRecoveryServlet() {
        this(new UserServiceImpl(), new com.pinguela.rentexpres.service.impl.MailServiceImpl());
    }

    /*
     * Permite inyectar dependencias personalizadas para facilitar pruebas o
     * configuraciones específicas del envío de códigos.
     */
    PasswordRecoveryServlet(UserService userService, MailService mailService) {
        this.userService = userService;
        this.mailService = mailService;
    }

    @Override
    /*
     * Muestra la pantalla correspondiente al flujo de recuperación según la
     * acción solicitada (petición de código o verificación).
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = action(request);
        if ("verify".equals(action)) {
            forward(request, response, Views.PUBLIC_RECOVERY_VERIFY);
        } else {
            forward(request, response, Views.PUBLIC_RECOVERY_REQUEST);
        }
    }

    @Override
    /*
     * Procesa las peticiones POST delegando en la acción específica de solicitud
     * de código o verificación del mismo.
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = action(request);
        if ("verify".equals(action)) {
            handleVerify(request, response);
        } else {
            handleRequestCode(request, response);
        }
    }

    /*
     * Valida el formulario de solicitud, genera el código de recuperación y gestiona
     * el envío del correo informando de errores cuando sea necesario.
     */
    private void handleRequestCode(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String email = param(request, "email");
        String emailError = null;

        if (email == null || email.isEmpty()) {
            emailError = MessageResolver.getMessage(request, "recovery.request.error.email");
        }

        UserDTO targetUser = null;
        if (emailError == null) {
            targetUser = findUserByEmail(email);
            if (targetUser == null) {
                emailError = MessageResolver.getMessage(request, "recovery.request.error.notfound");
            }
        }

        if (emailError != null) {
            request.setAttribute("email", email);
            request.setAttribute("errorEmail", emailError);
            forward(request, response, Views.PUBLIC_RECOVERY_REQUEST);
            return;
        }

        String code = generateCode();
        storeToken(email, code);

        boolean sent = sendRecoveryMail(request, targetUser, code);
        if (!sent) {
            removeToken(email);
            request.setAttribute("email", email);
            request.setAttribute("errorEmail",
                    MessageResolver.getMessage(request, "recovery.request.error.emailSend"));
            forward(request, response, Views.PUBLIC_RECOVERY_REQUEST);
            return;
        }

        LOGGER.info("Código de recuperación generado para {}", email);
        request.setAttribute("email", email);
        request.setAttribute(AppConstants.ATTR_FLASH_INFO,
                MessageResolver.getMessage(request, "recovery.verify.notice", email));
        forward(request, response, Views.PUBLIC_RECOVERY_VERIFY);
    }

    /*
     * Comprueba el código proporcionado por el usuario, valida las nuevas
     * contraseñas y actualiza las credenciales cuando los datos son correctos.
     */
    private void handleVerify(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String email = param(request, "email");
        String code = param(request, "code");
        String password = param(request, "password");
        String confirm = param(request, "confirmPassword");

        RecoveryEntry entry = findToken(email);

        String emailError = null;
        String codeError = null;
        String passwordError = null;
        String confirmError = null;

        if (email == null || email.isEmpty()) {
            emailError = MessageResolver.getMessage(request, "recovery.request.error.email");
        }
        if (code == null || code.isEmpty()) {
            codeError = MessageResolver.getMessage(request, "recovery.verify.error.code");
        } else if (entry == null || isExpired(entry.timestamp) || !entry.code.equals(code)) {
            codeError = MessageResolver.getMessage(request, "recovery.verify.error.expired");
        }
        if (password == null || password.length() < 6) {
            passwordError = MessageResolver.getMessage(request, "recovery.verify.error.password");
        }
        if (confirm == null || !confirm.equals(password)) {
            confirmError = MessageResolver.getMessage(request, "recovery.verify.error.mismatch");
        }

        if (emailError != null || codeError != null || passwordError != null || confirmError != null) {
            request.setAttribute("email", email);
            request.setAttribute("errorEmail", emailError);
            request.setAttribute("errorCode", codeError);
            request.setAttribute("errorPassword", passwordError);
            request.setAttribute("errorConfirm", confirmError);
            forward(request, response, Views.PUBLIC_RECOVERY_VERIFY);
            return;
        }

        UserDTO user = findUserByEmail(email);
        if (user == null) {
            request.setAttribute("errorEmail",
                    MessageResolver.getMessage(request, "recovery.request.error.notfound"));
            forward(request, response, Views.PUBLIC_RECOVERY_VERIFY);
            return;
        }

        user.setPassword(password);
        try {
            boolean updated = userService.update(user);
            if (!updated) {
                request.setAttribute("email", email);
                request.setAttribute("errorPassword",
                        MessageResolver.getMessage(request, "recovery.verify.error.general"));
                forward(request, response, Views.PUBLIC_RECOVERY_VERIFY);
                return;
            }
        } catch (RentexpresException ex) {
            LOGGER.error("Error actualizando contraseña para {}", email, ex);
            request.setAttribute("email", email);
            request.setAttribute("errorPassword",
                    MessageResolver.getMessage(request, "recovery.verify.error.general"));
            forward(request, response, Views.PUBLIC_RECOVERY_VERIFY);
            return;
        }

        removeToken(email);
        request.setAttribute(AppConstants.ATTR_FLASH_SUCCESS,
                MessageResolver.getMessage(request, "recovery.verify.success"));
        request.setAttribute(AppConstants.ATTR_LOGIN_EMAIL, email);
        forward(request, response, Views.PUBLIC_LOGIN);
    }

    /*
     * Busca al usuario asociado al correo electrónico consultando al servicio de
     * usuarios.
     */
    private UserDTO findUserByEmail(String email) {
        if (email == null) {
            return null;
        }
        try {
            for (UserDTO user : userService.findAll()) {
                if (user != null && email.equalsIgnoreCase(user.getEmail())) {
                    return user;
                }
            }
        } catch (RentexpresException ex) {
            LOGGER.error("Error buscando usuario por email", ex);
        }
        return null;
    }

    /*
     * Permite introducir configuraciones previas comunes antes de procesar la
     * petición de recuperación.
     */
    private String action(HttpServletRequest request) {
        String action = param(request, AppConstants.ACTION);
        return action != null ? action : "request";
    }

    /*
     * Comprueba si el código de recuperación ha superado su tiempo de vida
     * configurado.
     */
    private boolean isExpired(long timestamp) {
        return System.currentTimeMillis() - timestamp > CODE_TTL_MILLIS;
    }

    /*
     * Genera un código numérico aleatorio de seis dígitos para la verificación del
     * usuario.
     */
    private String generateCode() {
        int number = (int) (Math.random() * 900000) + 100000;
        return String.valueOf(number);
    }

    /*
     * Construye y envía el correo electrónico con el código de recuperación,
     * devolviendo si la operación fue satisfactoria.
     */
    private boolean sendRecoveryMail(HttpServletRequest request, UserDTO user, String code) {
        if (mailService == null || user == null) {
            return false;
        }
        String email = user.getEmail();
        if (email == null || email.trim().isEmpty()) {
            return false;
        }
        String subject = MessageResolver.getMessage(request, "recovery.email.subject", "RentExpress password reset");
        String bodyTemplate = MessageResolver.getMessage(request, "recovery.email.body",
                "Hello {0}, your recovery code is {1}");
        String firstName = user.getFirstName() != null ? user.getFirstName() : "";
        String body = MessageFormat.format(bodyTemplate, firstName, code);
        return mailService.send(email, subject, body);
    }

    /*
     * Obtiene y depura un parámetro de la petición eliminando espacios
     * sobrantes.
     */
    private String param(HttpServletRequest request, String name) {
        String value = request.getParameter(name);
        return value != null ? value.trim() : null;
    }

    /*
     * Delegado para reenviar la petición a la vista indicada dentro del flujo de
     * recuperación.
     */
    private void forward(HttpServletRequest request, HttpServletResponse response, String view)
            throws ServletException, IOException {
        request.getRequestDispatcher(view).forward(request, response);
    }

    private static final class RecoveryEntry {
        private final String email;
        private final String code;
        private final long timestamp;

        /*
         * Crea una entrada de recuperación asociando el código generado con el
         * instante de emisión.
         */
        private RecoveryEntry(String email, String code, long timestamp) {
            this.email = email;
            this.code = code;
            this.timestamp = timestamp;
        }

        private boolean matches(String candidate) {
            return email != null && email.equalsIgnoreCase(candidate);
        }
    }

    /*
     * Registra o actualiza el código asociado a un correo electrónico sustituyendo
     * cualquier entrada previa.
     */
    private void storeToken(String email, String code) {
        if (email == null) {
            return;
        }
        removeToken(email);
        TOKENS.add(new RecoveryEntry(email, code, System.currentTimeMillis()));
    }

    /*
     * Recupera la entrada de recuperación vinculada al correo indicado buscando en
     * la colección interna de tokens.
     */
    private RecoveryEntry findToken(String email) {
        if (email == null) {
            return null;
        }
        for (RecoveryEntry entry : TOKENS) {
            if (entry != null && entry.matches(email)) {
                return entry;
            }
        }
        return null;
    }

    /*
     * Elimina el token asociado al correo electrónico cuando deja de ser necesario
     * (por ejemplo tras completar la verificación).
     */
    private void removeToken(String email) {
        if (email == null) {
            return;
        }
        for (int index = 0; index < TOKENS.size(); index++) {
            RecoveryEntry entry = TOKENS.get(index);
            if (entry != null && entry.matches(email)) {
                TOKENS.remove(index);
                index--;
            }
        }
    }
}
