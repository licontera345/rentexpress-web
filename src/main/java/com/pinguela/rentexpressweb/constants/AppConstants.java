package com.pinguela.rentexpressweb.constants;

/**
 * Conjunto de constantes compartidas en toda la aplicación.
 * Mantener los nombres centralizados facilita reutilizar la
 * misma clave tanto en servlets como en las JSP.
 */
public final class AppConstants {

    private AppConstants() {
        // Evitar instanciación
    }

    public static final String ATTR_PAGE_TITLE = "pageTitle";
    public static final String ATTR_CURRENT_USER = "currentUser";
    public static final String ATTR_CURRENT_EMPLOYEE = "currentEmployee";
    public static final String ATTR_FLASH_SUCCESS = "flashSuccess";
    public static final String ATTR_FLASH_ERROR = "flashError";
    public static final String ATTR_FLASH_INFO = "flashInfo";
    public static final String ATTR_FORM_ERRORS = "error";
    public static final String ATTR_FORM_DATA = "formData";
    public static final String ATTR_LOGIN_EMAIL = "loginEmail";
    public static final String ATTR_REDIRECT_TARGET = "redirectTarget";
    public static final String ATTR_LOCALE = "appLocale";

    public static final String PARAM_LANGUAGE = "lang";
    public static final String PARAM_REMEMBER_ME = "remember";

    public static final String COOKIE_REMEMBER_USER = "rememberUser";

    public static final String CONTEXT_REGISTERED_USERS = "rentexpress.registeredUsers";
    public static final String CONTEXT_CREDENTIALS = "rentexpress.credentials";
    public static final String CONTEXT_KNOWN_EMAILS = "rentexpress.knownEmails";
}
