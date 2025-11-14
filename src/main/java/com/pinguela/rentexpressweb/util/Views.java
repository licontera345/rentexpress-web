package com.pinguela.rentexpressweb.util;

/**
 * Centraliza las rutas de las vistas JSP públicas y privadas.
 */
public final class Views {

    private Views() {
    }

    public static final String PUBLIC_INDEX = "/public/index.jsp";
    public static final String PUBLIC_LOGIN = "/public/login.jsp";
    public static final String PUBLIC_REGISTER_USER = "/public/register_user.jsp";
    public static final String PUBLIC_VEHICLE_LIST = "/public/vehicle_list.jsp";
    public static final String PUBLIC_VEHICLE_DETAIL = "/public/vehicle_detail.jsp";
    public static final String PUBLIC_RESERVATION_FORM = "/public/reservation_form.jsp";
    public static final String PUBLIC_VERIFY_2FA = "/public/verify_2fa.jsp";
    public static final String PUBLIC_RECOVERY_REQUEST = "/public/forgot_password.jsp";
    public static final String PUBLIC_RECOVERY_VERIFY = "/public/verify_recovery.jsp";

    public static final String PRIVATE_DASHBOARD = "/private/dashboard.jsp";
    public static final String PRIVATE_EMPLOYEE_LIST = "/private/employee_list.jsp";
    public static final String PRIVATE_EMPLOYEE_FORM = "/private/employee_form.jsp";
    public static final String PRIVATE_VEHICLE_LIST = "/private/vehicle_list.jsp";
    public static final String PRIVATE_VEHICLE_FORM = "/private/vehicle_form.jsp";

    public static final String PRIVATE_PROFILE = "/private/profile.jsp";

    public static final String SERVLET_LANGUAGE = "/common/language";
}
