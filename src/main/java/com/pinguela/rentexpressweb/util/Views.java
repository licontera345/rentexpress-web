package com.pinguela.rentexpressweb.util;

/**
 * Rutas centralizadas a las vistas JSP públicas y privadas.
 */
public final class Views {

    private Views() {
    }

    public static final String PUBLIC_INDEX = "/public/index.jsp";
    public static final String PUBLIC_WELCOME = "/public/views/welcome.jsp";
    public static final String PUBLIC_LOGIN = "/public/views/login.jsp";
    public static final String PUBLIC_REGISTER_USER = "/public/views/register_user.jsp";
    public static final String PUBLIC_REGISTER_EMPLOYEE = "/public/views/register_employee.jsp";
    public static final String PUBLIC_FORGOT_PASSWORD = "/public/views/forgot_password.jsp";
    public static final String PUBLIC_VERIFY_RESET = "/public/views/verify_reset.jsp";
    public static final String PUBLIC_RESET_PASSWORD = "/public/views/reset_password.jsp";

    public static final String PUBLIC_USER_LIST = "/public/user/user_list.jsp";
    public static final String PUBLIC_USER_DETAIL = "/public/user/user_detail.jsp";

    public static final String PUBLIC_EMPLOYEE_LIST = "/public/employee/employee_list.jsp";
    public static final String PUBLIC_EMPLOYEE_DETAIL = "/public/employee/employee_detail.jsp";

    public static final String PUBLIC_VEHICLE_LIST = "/public/vehicle/catalog.jsp";
    public static final String PUBLIC_VEHICLE_DETAIL = "/public/vehicle/vehicle_detail.jsp";

    public static final String PUBLIC_RENTAL_REPORT = "/public/rental/rentals_report.jsp";

    public static final String PRIVATE_USER_HOME = "/private/user/user_home.jsp";
    public static final String PRIVATE_USER_PROFILE = "/private/user/user_profile.jsp";
    public static final String PRIVATE_EMPLOYEE_PROFILE = "/private/employee/employee_profile.jsp";
    public static final String PRIVATE_VEHICLE_LIST = "/private/vehicle/vehicle_list.jsp";
    public static final String PRIVATE_RENTAL_DASHBOARD = "/private/rental/rental_dashboard.jsp";
    public static final String PRIVATE_RESERVATION_SUCCESS = "/private/reservation/reservation_success.jsp";
}
