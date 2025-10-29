package com.pinguela.rentexpressweb.constants;

/**
 * Constantes para parámetros y atributos relacionados con usuarios.
 */
public final class UserConstants {

    private UserConstants() {
    }

    public static final String PARAM_EMAIL = "email";
    public static final String PARAM_PASSWORD = "password";
    public static final String PARAM_FULL_NAME = "fullName";
    public static final String PARAM_PHONE = "phone";
    public static final String PARAM_ACCEPT_TERMS = "acceptTerms";
    public static final String PARAM_REMEMBER_ME = "remember";

    public static final String ROLE_NAME_CUSTOMER = "Customer";

    public static final String PARAM_SEARCH = "search";
    public static final String PARAM_ROLE = "role";
    public static final String PARAM_ACTIVE = "active";
    public static final String PARAM_USER_ID = "userId";

    public static final String VALUE_ACTIVE = "active";
    public static final String VALUE_INACTIVE = "inactive";
    public static final String VALUE_ALL = "all";

    public static final String VALUE_SORT_CREATED_DESC = "createdDesc";
    public static final String VALUE_SORT_NAME_ASC = "nameAsc";
    public static final String VALUE_SORT_ROLE_ASC = "roleAsc";

    public static final String ATTR_USERS = "users";
    public static final String ATTR_FILTERS = "userFilters";
    public static final String ATTR_FILTER_ERRORS = "userFilterErrors";
    public static final String ATTR_ROLES = "userRoles";
    public static final String ATTR_ROLE_NAMES = "userRoleNames";
    public static final String ATTR_PAGINATION = "userPagination";
    public static final String ATTR_SUMMARY = "userSummary";
    public static final String ATTR_SELECTED_USER = "selectedUser";
    public static final String ATTR_SELECTED_ROLE_NAME = "selectedUserRole";
}
