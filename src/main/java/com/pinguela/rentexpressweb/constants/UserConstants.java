package com.pinguela.rentexpressweb.constants;

/**
 * Constantes para parámetros y atributos relacionados con usuarios.
 */
public final class UserConstants {

    private UserConstants() {
    }

    // Parámetros comunes
    public static final String PARAM_ID = "id";
    public static final String PARAM_USER_ID = "userId";
    public static final String PARAM_USERNAME = "username";
    public static final String PARAM_EMAIL = "email";
    public static final String PARAM_PASSWORD = "password";
    public static final String PARAM_CONFIRM_PASSWORD = "confirmPassword";
    public static final String PARAM_FULL_NAME = "fullName";
    public static final String PARAM_FIRST_NAME = "firstName";
    public static final String PARAM_LAST_NAME1 = "lastName1";
    public static final String PARAM_LAST_NAME2 = "lastName2";
    public static final String PARAM_BIRTH_DATE = "birthDate";
    public static final String PARAM_PHONE = "phone";
    public static final String PARAM_STREET = "street";
    public static final String PARAM_NUMBER = "number";
    public static final String PARAM_PROVINCE_ID = "provinceId";
    public static final String PARAM_CITY_ID = "cityId";
    public static final String PARAM_ADDRESS_ID = "addressId";
    public static final String PARAM_ACCEPT_TERMS = "acceptTerms";

    // Parámetros de filtrado y acciones
    public static final String PARAM_SEARCH = "search";
    public static final String PARAM_ROLE = "role";
    public static final String PARAM_ACTIVE = "active";
    public static final String PARAM_STATUS = "status";

    public static final String PARAM_CREATED = "created";
    public static final String PARAM_UPDATED = "updated";
    public static final String PARAM_DELETED = "deleted";
    public static final String PARAM_ACTIVATED = "activated";
    public static final String PARAM_ERROR = "error";

    // Valores de filtro
    public static final String VALUE_ACTIVE = "active";
    public static final String VALUE_INACTIVE = "inactive";
    public static final String VALUE_ALL = "all";

    // Acciones CRUD
    public static final String ACTION_LIST = "list";
    public static final String ACTION_VIEW_REGISTER = "viewRegister";
    public static final String ACTION_EDIT = "edit";
    public static final String ACTION_CREATE = "create";
    public static final String ACTION_UPDATE = "update";
    public static final String ACTION_DELETE = "delete";
    public static final String ACTION_ACTIVATE = "activate";

    // Información de roles
    public static final String ROLE_NAME_CUSTOMER = "Customer";
    public static final String ROLE_NAME_CUSTOMER_ES = "Cliente";
    public static final String ROLE_NAME_CUSTOMER_EN = "Client";
    public static final String ROLE_NAME_USER = "User";
    public static final String ROLE_NAME_CUSTOMER_CODE = "CLIENT";

    public static final int ROLE_ID_CUSTOMER_FALLBACK = 3;

    // Atributos compartidos
    public static final String ATTR_USERS = "users";
    public static final String ATTR_FILTERS = "userFilters";
    public static final String ATTR_FILTER_ERRORS = "userFilterErrors";
    public static final String ATTR_ROLES = "userRoles";
    public static final String ATTR_ROLE_NAMES = "userRoleNames";
    public static final String ATTR_PAGINATION = "userPagination";
    public static final String ATTR_SUMMARY = "userSummary";
    public static final String ATTR_SELECTED_USER = "selectedUser";
    public static final String ATTR_SELECTED_ROLE_NAME = "selectedUserRole";
    public static final String ATTR_USER = "user";
    public static final String ATTR_IS_EDIT = "isEdit";
    public static final String ATTR_FORM_ACTION = "formAction";

    public static final String ATTR_PROFILE_DATA = "userProfileData";
    public static final String ATTR_PROFILE_FORM = "userProfileForm";
    public static final String ATTR_PROFILE_ERRORS = "userProfileErrors";
    public static final String ATTR_ACTIVITY_LOG = "userActivityLog";

    public static final String ATTR_PROVINCES = "provinces";
    public static final String ATTR_CITIES = "cities";
}
