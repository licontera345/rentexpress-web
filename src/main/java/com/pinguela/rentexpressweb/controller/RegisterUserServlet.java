package com.pinguela.rentexpressweb.controller;

import com.pinguela.rentexpres.model.AddressDTO;
import com.pinguela.rentexpres.model.CityDTO;
import com.pinguela.rentexpres.model.ProvinceDTO;
import com.pinguela.rentexpres.model.Results;
import com.pinguela.rentexpres.model.RoleDTO;
import com.pinguela.rentexpres.model.UserCriteria;
import com.pinguela.rentexpres.model.UserDTO;
import com.pinguela.rentexpres.service.AddressService;
import com.pinguela.rentexpres.service.CityService;
import com.pinguela.rentexpres.service.MailService;
import com.pinguela.rentexpres.service.ProvinceService;
import com.pinguela.rentexpres.service.RoleService;
import com.pinguela.rentexpres.service.UserService;
import com.pinguela.rentexpres.service.impl.AddressServiceImpl;
import com.pinguela.rentexpres.service.impl.CityServiceImpl;
import com.pinguela.rentexpres.service.impl.MailServiceImpl;
import com.pinguela.rentexpres.service.impl.ProvinceServiceImpl;
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
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
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
    private final UserService userService = new UserServiceImpl();
    private final RoleService roleService = new RoleServiceImpl();
    private final AddressService addressService = new AddressServiceImpl();
    private final ProvinceService provinceService = new ProvinceServiceImpl();
    private final CityService cityService = new CityServiceImpl();
    private final MailService mailService = new MailServiceImpl();

    public RegisterUserServlet() {
        super();
    }

    /**
     * @see HttpServlet#doGet(HttpServletRequest, HttpServletResponse)
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Map<String, String> formData = new HashMap<String, String>();
        List<String> loadErrors = new ArrayList<String>();
        prepareLocationData(request, loadErrors);
        if (!loadErrors.isEmpty()) {
            request.setAttribute("errors", loadErrors);
        }
        request.setAttribute(AppConstants.ATTR_PAGE_TITLE, "Crea tu cuenta");
        request.setAttribute("formData", formData);
        request.getRequestDispatcher(Views.PUBLIC_REGISTER_USER).forward(request, response);
    }

    /**
     * @see HttpServlet#doPost(HttpServletRequest, HttpServletResponse)
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Map<String, String> formData = new HashMap<String, String>();
        String firstName = trimToNull(request.getParameter(UserConstants.PARAM_FIRST_NAME));
        String lastName1 = trimToNull(request.getParameter(UserConstants.PARAM_LAST_NAME1));
        String lastName2 = trimToNull(request.getParameter(UserConstants.PARAM_LAST_NAME2));
        String birthDateValue = trimToNull(request.getParameter(UserConstants.PARAM_BIRTH_DATE));
        String email = trimToNull(request.getParameter(UserConstants.PARAM_EMAIL));
        String phone = trimToNull(request.getParameter(UserConstants.PARAM_PHONE));
        String password = request.getParameter(UserConstants.PARAM_PASSWORD);
        String confirmPassword = request.getParameter(UserConstants.PARAM_CONFIRM_PASSWORD);
        boolean acceptTerms = request.getParameter(UserConstants.PARAM_ACCEPT_TERMS) != null;
        String street = trimToNull(request.getParameter(UserConstants.PARAM_STREET));
        String number = trimToNull(request.getParameter(UserConstants.PARAM_NUMBER));
        String provinceValue = trimToNull(request.getParameter(UserConstants.PARAM_PROVINCE_ID));
        String cityValue = trimToNull(request.getParameter(UserConstants.PARAM_CITY_ID));

        formData.put(UserConstants.PARAM_FIRST_NAME, firstName != null ? firstName : "");
        formData.put(UserConstants.PARAM_LAST_NAME1, lastName1 != null ? lastName1 : "");
        formData.put(UserConstants.PARAM_LAST_NAME2, lastName2 != null ? lastName2 : "");
        formData.put(UserConstants.PARAM_BIRTH_DATE, birthDateValue != null ? birthDateValue : "");
        formData.put(UserConstants.PARAM_EMAIL, email != null ? email : "");
        formData.put(UserConstants.PARAM_PHONE, phone != null ? phone : "");
        formData.put(UserConstants.PARAM_STREET, street != null ? street : "");
        formData.put(UserConstants.PARAM_NUMBER, number != null ? number : "");
        formData.put(UserConstants.PARAM_PROVINCE_ID, provinceValue != null ? provinceValue : "");
        formData.put(UserConstants.PARAM_CITY_ID, cityValue != null ? cityValue : "");
        formData.put(UserConstants.PARAM_ACCEPT_TERMS, acceptTerms ? "on" : "");

        List<String> errors = new ArrayList<String>();
        LocalDate birthDate = null;

        if (firstName == null) {
            errors.add("El nombre es obligatorio.");
        } else if (firstName.length() > 120) {
            errors.add("El nombre no puede superar los 120 caracteres.");
        }

        if (lastName1 == null) {
            errors.add("El primer apellido es obligatorio.");
        } else if (lastName1.length() > 120) {
            errors.add("El primer apellido no puede superar los 120 caracteres.");
        }

        if (lastName2 != null && lastName2.length() > 120) {
            errors.add("El segundo apellido no puede superar los 120 caracteres.");
        }
        if (street != null) {
            formData.put(UserConstants.PARAM_STREET, street);
        } else {
            formData.put(UserConstants.PARAM_STREET, "");
        }
        if (number != null) {
            formData.put(UserConstants.PARAM_NUMBER, number);
        } else {
            formData.put(UserConstants.PARAM_NUMBER, "");
        }
        if (provinceValue != null) {
            formData.put(UserConstants.PARAM_PROVINCE_ID, provinceValue);
        } else {
            formData.put(UserConstants.PARAM_PROVINCE_ID, "");
        }
        if (cityValue != null) {
            formData.put(UserConstants.PARAM_CITY_ID, cityValue);
        } else {
            formData.put(UserConstants.PARAM_CITY_ID, "");
        }
        if (acceptTerms) {
            formData.put(UserConstants.PARAM_ACCEPT_TERMS, "on");
        } else {
            formData.put(UserConstants.PARAM_ACCEPT_TERMS, "");
        }

        if (birthDateValue == null) {
            errors.add("La fecha de nacimiento es obligatoria.");
        } else {
            try {
                birthDate = LocalDate.parse(birthDateValue);
                if (birthDate.isAfter(LocalDate.now())) {
                    errors.add("La fecha de nacimiento no puede ser futura.");
                }
            } catch (DateTimeParseException ex) {
                errors.add("Indica una fecha de nacimiento válida (formato AAAA-MM-DD).");
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

        String sanitizedConfirm = confirmPassword == null ? null : confirmPassword.trim();
        if (sanitizedConfirm == null || sanitizedConfirm.isEmpty()) {
            errors.add("Debes confirmar la contraseña.");
        } else if (sanitizedPassword != null && !sanitizedPassword.equals(sanitizedConfirm)) {
            errors.add("Las contraseñas no coinciden.");
        }

        if (phone == null) {
            errors.add("El teléfono es obligatorio.");
        } else {
            if (phone.length() > 20) {
                errors.add("El teléfono no puede superar los 20 caracteres.");
            }
            if (!isAllowedPhone(phone)) {
                errors.add("El teléfono solo puede contener números, espacios, '+' o '-'.");
            }
        }

        if (!acceptTerms) {
            errors.add("Debes aceptar los términos y condiciones de uso.");
        }

        if (street == null) {
            errors.add("La calle de la dirección es obligatoria.");
        } else if (street.length() > 255) {
            errors.add("La calle no puede superar los 255 caracteres.");
        }

        if (number == null) {
            errors.add("El número de la dirección es obligatorio.");
        } else if (number.length() > 10) {
            errors.add("El número de la dirección no puede superar los 10 caracteres.");
        }

        Integer provinceId = null;
        if (provinceValue == null) {
            errors.add("Selecciona la provincia de residencia.");
        } else {
            provinceId = parseInteger(provinceValue);
            if (provinceId == null) {
                errors.add("Selecciona una provincia válida.");
            }
        }

        Integer cityId = null;
        if (cityValue == null) {
            errors.add("Selecciona la ciudad de residencia.");
        } else {
            cityId = parseInteger(cityValue);
            if (cityId == null) {
                errors.add("Selecciona una ciudad válida.");
            }
        }

        ProvinceDTO selectedProvince = null;
        if (provinceId != null) {
            try {
                selectedProvince = provinceService.findById(provinceId);
                if (selectedProvince == null) {
                    errors.add("La provincia seleccionada no existe.");
                }
            } catch (Exception ex) {
                LOGGER.error("Error comprobando la provincia {}", provinceId, ex);
                errors.add("No se pudo validar la provincia seleccionada. Inténtalo de nuevo más tarde.");
            }
        }

        CityDTO selectedCity = null;
        if (cityId != null) {
            try {
                selectedCity = cityService.findById(cityId);
                if (selectedCity == null) {
                    errors.add("La ciudad seleccionada no existe.");
                } else if (provinceId != null && selectedCity.getProvinceId() != null
                        && !selectedCity.getProvinceId().equals(provinceId)) {
                    errors.add("La ciudad seleccionada no pertenece a la provincia elegida.");
                }
            } catch (Exception ex) {
                LOGGER.error("Error comprobando la ciudad {}", cityId, ex);
                errors.add("No se pudo validar la ciudad seleccionada. Inténtalo de nuevo más tarde.");
            }
        }

        String sanitizedEmail = email == null ? null : email.toLowerCase(Locale.ROOT);

        if (errors.isEmpty() && sanitizedEmail != null) {
            try {
                if (isEmailAlreadyRegistered(sanitizedEmail)) {
                    errors.add("Ya existe una cuenta registrada con ese correo electrónico.");
                }
            } catch (Exception ex) {
                LOGGER.error("No se pudo comprobar la disponibilidad del correo {}", sanitizedEmail, ex);
                errors.add("No se pudo validar la disponibilidad del correo. Inténtalo de nuevo en unos minutos.");
            }
        }

        Integer customerRoleId = null;
        if (errors.isEmpty()) {
            try {
                customerRoleId = resolveCustomerRoleId();
            } catch (Exception ex) {
                LOGGER.error("Error obteniendo el rol de cliente", ex);
                errors.add("No se pudo preparar el registro de usuario. Inténtalo más tarde.");
            }
            if (customerRoleId == null) {
                errors.add("No se pudo determinar el rol de cliente para completar el registro.");
            }
        }

        AddressDTO createdAddress = null;
        if (errors.isEmpty() && cityId != null) {
            AddressDTO address = new AddressDTO();
            address.setStreet(street);
            address.setNumber(number);
            address.setCityId(cityId);
            try {
                if (addressService.create(address)) {
                    createdAddress = address;
                } else {
                    errors.add("No se pudo guardar la dirección proporcionada.");
                }
            } catch (Exception ex) {
                LOGGER.error("Error creando la dirección para el registro de {}", sanitizedEmail, ex);
                errors.add("Ha ocurrido un error al guardar la dirección. Inténtalo de nuevo más tarde.");
            }
        }

        if (!errors.isEmpty()) {
            prepareLocationData(request, errors);
            request.setAttribute("errors", errors);
            request.setAttribute("formData", formData);
            request.setAttribute(AppConstants.ATTR_PAGE_TITLE, "Crea tu cuenta");
            request.getRequestDispatcher(Views.PUBLIC_REGISTER_USER).forward(request, response);
            return;
        }

        UserDTO user = new UserDTO();
        user.setUsername(sanitizedEmail);
        user.setEmail(sanitizedEmail);
        user.setFirstName(firstName);
        user.setLastName1(lastName1);
        user.setLastName2(lastName2);
        user.setBirthDate(birthDate);
        user.setPhone(phone);
        user.setPassword(sanitizedPassword);
        user.setRoleId(customerRoleId);
        user.setActiveStatus(Boolean.TRUE);
        if (createdAddress != null) {
            user.setAddressId(createdAddress.getId());
        }

        try {
            boolean created = userService.create(user);
            if (created) {
                CredentialStore.updatePassword(getServletContext(), sanitizedEmail, sanitizedPassword);
                LOGGER.info("Registrado nuevo usuario {}", sanitizedEmail);
                sendWelcomeEmail(sanitizedEmail, firstName);
                SessionManager.setAttribute(request, AppConstants.ATTR_FLASH_SUCCESS,
                        "Registro completado. Ya puedes iniciar sesión con tu correo y contraseña.");
                response.sendRedirect(request.getContextPath() + SecurityConstants.LOGIN_ENDPOINT);
                return;
            }
            errors.add("No se pudo completar el registro. Inténtalo de nuevo.");
            cleanupAddress(createdAddress);
        } catch (Exception ex) {
            LOGGER.error("Error creando el usuario {}", sanitizedEmail, ex);
            errors.add("Ha ocurrido un error al crear tu cuenta. Inténtalo de nuevo en unos minutos.");
            cleanupAddress(createdAddress);
        }

        prepareLocationData(request, errors);
        request.setAttribute("errors", errors);
        request.setAttribute("formData", formData);
        request.setAttribute(AppConstants.ATTR_PAGE_TITLE, "Crea tu cuenta");
        request.getRequestDispatcher(Views.PUBLIC_REGISTER_USER).forward(request, response);
    }

    private void sendWelcomeEmail(String email, String firstName) {
        if (email == null) {
            return;
        }
        String nameForMessage = firstName != null ? firstName : "cliente";
        StringBuilder message = new StringBuilder();
        message.append("Hola ");
        message.append(nameForMessage);
        message.append(",\n\nGracias por registrarte en RentExpress. Tu cuenta ya está lista y puedes iniciar sesión para reservar tu próximo vehículo.\n\n");
        message.append("¡Te esperamos!\nEquipo RentExpress");
        try {
            if (!mailService.send(email, "Bienvenido a RentExpress", message.toString())) {
                LOGGER.error("No se pudo enviar el correo de bienvenida a {}", email);
            }
        } catch (RuntimeException ex) {
            LOGGER.error("Error inesperado enviando el correo de bienvenida a {}", email, ex);
        }
    }

    private boolean isEmailAlreadyRegistered(String email) throws Exception {
        UserCriteria criteria = new UserCriteria();
        criteria.setEmail(email);
        criteria.setPageNumber(Integer.valueOf(1));
        criteria.setPageSize(Integer.valueOf(1));
        Results<UserDTO> results = userService.findByCriteria(criteria);
        return results != null && results.getResults() != null && !results.getResults().isEmpty();
    }

    private Integer resolveCustomerRoleId() throws Exception {
        Integer fallbackRoleId = Integer.valueOf(UserConstants.ROLE_ID_CUSTOMER_FALLBACK);
        List<RoleDTO> roles = roleService.findAll();
        if (roles == null || roles.isEmpty()) {
            LOGGER.info("No se pudieron obtener roles desde el servicio. Se usará el rol por defecto {}.", fallbackRoleId);
            return fallbackRoleId;
        }
        for (RoleDTO role : roles) {
            if (role == null) {
                continue;
            }
            String roleName = role.getRoleName();
            if (roleName == null) {
                continue;
            }
            String trimmedName = roleName.trim();
            if (isCustomerRoleName(trimmedName)) {
                return role.getRoleId();
            }
        }
        for (RoleDTO role : roles) {
            if (role == null) {
                continue;
            }
            Integer roleId = role.getRoleId();
            if (roleId != null && roleId.equals(fallbackRoleId)) {
                LOGGER.info("No se encontró un rol de cliente por nombre. Se usará el id {} obtenido del servicio.",
                        fallbackRoleId);
                return roleId;
            }
        }
        LOGGER.info("No se encontró un rol de cliente en el servicio. Se usará el id por defecto {}.", fallbackRoleId);
        return fallbackRoleId;
    }

    private boolean isCustomerRoleName(String roleName) {
        if (roleName == null) {
            return false;
        }
        if (UserConstants.ROLE_NAME_CUSTOMER.equalsIgnoreCase(roleName)) {
            return true;
        }
        if (UserConstants.ROLE_NAME_CUSTOMER_ES.equalsIgnoreCase(roleName)) {
            return true;
        }
        if (UserConstants.ROLE_NAME_CUSTOMER_EN.equalsIgnoreCase(roleName)) {
            return true;
        }
        if (UserConstants.ROLE_NAME_CUSTOMER_USER.equalsIgnoreCase(roleName)) {
            return true;
        }
        if (UserConstants.ROLE_NAME_CUSTOMER_CODE.equalsIgnoreCase(roleName)) {
            return true;
        }
        return false;
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

    private Integer parseInteger(String value) {
        try {
            return Integer.valueOf(Integer.parseInt(value));
        } catch (NumberFormatException ex) {
            return null;
        }
    }

    private boolean isAllowedPhone(String value) {
        if (value == null) {
            return false;
        }
        boolean hasDigit = false;
        for (int i = 0; i < value.length(); i++) {
            char current = value.charAt(i);
            if (Character.isDigit(current)) {
                hasDigit = true;
                continue;
            }
            if (current == ' ' || current == '+' || current == '-') {
                continue;
            }
            return false;
        }
        return hasDigit;
    }

    private void prepareLocationData(HttpServletRequest request, List<String> errors) {
        List<ProvinceDTO> provinces = null;
        try {
            provinces = provinceService.findAll();
        } catch (Exception ex) {
            LOGGER.error("Error cargando provincias", ex);
            if (errors != null) {
                errors.add("No se pudo cargar la lista de provincias. Inténtalo de nuevo más tarde.");
            }
        }
        if (provinces == null) {
            provinces = new ArrayList<ProvinceDTO>();
        }
        request.setAttribute(UserConstants.ATTR_PROVINCES, provinces);

        List<CityDTO> cities = null;
        try {
            cities = cityService.findAll();
        } catch (Exception ex) {
            LOGGER.error("Error cargando ciudades", ex);
            if (errors != null) {
                errors.add("No se pudo cargar la lista de ciudades. Inténtalo de nuevo más tarde.");
            }
        }
        if (cities == null) {
            cities = new ArrayList<CityDTO>();
        }
        request.setAttribute(UserConstants.ATTR_CITIES, cities);

    }

    private void cleanupAddress(AddressDTO address) {
        if (address == null || address.getId() == null) {
            return;
        }
        try {
            addressService.delete(address);
        } catch (Exception ex) {
            LOGGER.error("No se pudo revertir la dirección {} tras un error de registro", address.getId(), ex);
        }
    }

}
