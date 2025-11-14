package com.pinguela.rentexpressweb.constants;

/**
 * Constantes específicas para los listados y detalles de empleados en
 * pantallas públicas y privadas.
 */
public final class EmployeeConstants {

    private EmployeeConstants() {
    }

    public static final String PARAM_ID = "employeeId";
    public static final String PARAM_EMPLOYEE_NAME = "employeeName";
    public static final String PARAM_PASSWORD = "password";
    public static final String PARAM_FIRST_NAME = "firstName";
    public static final String PARAM_LAST_NAME1 = "lastName1";
    public static final String PARAM_LAST_NAME2 = "lastName2";
    public static final String PARAM_EMAIL = "email";
    public static final String PARAM_PHONE = "phone";
    public static final String PARAM_ROLE_ID = "roleId";
    public static final String PARAM_HEADQUARTERS_ID = "headquartersId";
    public static final String PARAM_ACTIVE = "active";
    public static final String PARAM_SEARCH = "search";
    public static final String PARAM_ROLE = "role";
    public static final String PARAM_HEADQUARTERS = "headquarters";
    public static final String PARAM_CREATED = "created";
    public static final String PARAM_UPDATED = "updated";
    public static final String PARAM_DELETED = "deleted";
    public static final String PARAM_ACTIVATED = "activated";
    public static final String PARAM_ERROR = "error";

    public static final String ACTION_LIST = "list";
    public static final String ACTION_VIEW_REGISTER = "viewRegister";
    public static final String ACTION_EDIT = "edit";
    public static final String ACTION_CREATE = "create";
    public static final String ACTION_UPDATE = "update";
    public static final String ACTION_DELETE = "delete";
    public static final String ACTION_ACTIVATE = "activate";

    public static final String ATTR_EMPLOYEES = "employees";
    public static final String ATTR_TOTAL_EMPLOYEES = "totalEmployees";
    public static final String ATTR_EMPLOYEE = "employee";
    public static final String ATTR_IS_EDIT = "isEdit";
    public static final String ATTR_FORM_ACTION = "formAction";
    public static final String ATTR_ROLES = "roles";
    public static final String ATTR_HEADQUARTERS = "headquarters";
    public static final String ATTR_PROFILE_DATA = "employeeProfileData";
}
