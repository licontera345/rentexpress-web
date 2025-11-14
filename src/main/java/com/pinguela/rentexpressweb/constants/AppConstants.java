package com.pinguela.rentexpressweb.constants;

import com.pinguela.rentexpressweb.util.Views;

/**
 * Conjunto de constantes compartidas en toda la aplicación. Mantener los
 * nombres centralizados facilita reutilizar la misma clave tanto en servlets
 * como en las JSP.
 */
public final class AppConstants {

    private AppConstants() {
        // Evitar instanciación
    }

    // Internacionalización - CORREGIDO: debe ser "appLocale"
    public static final String ATTR_LOCALE = "appLocale";
    public static final String ATTR_LANGUAGE = "language";
    public static final String PARAM_LANGUAGE = "lang";
    public static final String COOKIE_LANGUAGE = "appLanguage";
    public static final String DEFAULT_LANGUAGE = "es";
    public static final String[] SUPPORTED_LANGUAGES = { "es", "en", "fr" };

    // Parámetros comunes
    public static final String ACTION = "action";

    // Acciones compartidas con el registro/gestión de usuarios
    public static final String VIEW_REGISTER = "viewRegister";
    public static final String REGISTER_USER = "register";
    public static final String VIEW_PROFILE = "viewProfile";
    public static final String UPDATE_USER = "update";
    public static final String DELETE_USER = "delete";
    public static final String UPDATE_PHOTO = "updatePhoto";

    // Acciones de gestión interna de empleados
    public static final String LIST_EMPLOYEES = "listEmployees";
    public static final String CREATE_EMPLOYEE = "createEmployee";
    public static final String UPDATE_EMPLOYEE = "updateEmployee";
    public static final String DELETE_EMPLOYEE = "deleteEmployee";

    // Acciones de gestión interna de vehículos
    public static final String LIST_VEHICLES = "listVehicles";
    public static final String CREATE_VEHICLE = "createVehicle";
    public static final String UPDATE_VEHICLE = "updateVehicle";
    public static final String DELETE_VEHICLE = "deleteVehicle";

    // Acciones del catálogo público de vehículos
    public static final String LIST_AVAILABLE_VEHICLES = "listAvailableVehicles";
    public static final String FILTER_VEHICLES = "filterVehicles";
    public static final String VIEW_VEHICLE_DETAIL = "viewVehicleDetail";

    // Recuperación de contraseña
    public static final String ATTR_RECOVERY_VERIFIED = "recoveryVerified";
    public static final String ATTR_RECOVERY_EMAIL = "recoveryEmail";

    // Mensajes flash
    public static final String ATTR_FLASH_SUCCESS = "flashSuccess";
    public static final String ATTR_FLASH_ERROR = "flashError";
    public static final String ATTR_FLASH_INFO = "flashInfo";
    public static final String ATTR_GENERAL_ERROR = "errorGeneral";
    public static final String ATTR_VEHICLES = "vehicles";
    public static final String ATTR_CATEGORIES = "categories";
    public static final String ATTR_HEADQUARTERS = "headquarters";
    public static final String ATTR_HEADQUARTERS_MAP = "headquartersMap";
    public static final String ATTR_CATEGORIES_MAP = "categoriesMap";
    public static final String ATTR_PROVINCES = "provinces";
    public static final String ATTR_CITIES = "cities";
    public static final String ATTR_SELECTED_CATEGORY = "selectedCategoryId";
    public static final String ATTR_SELECTED_MAX_PRICE = "selectedMaxPrice";
    public static final String ATTR_SELECTED_MIN_PRICE = "selectedMinPrice";
    public static final String ATTR_SELECTED_HEADQUARTERS = "selectedHeadquartersId";
    public static final String ATTR_VEHICLE_SEARCH = "vehicleSearchQuery";
    public static final String ATTR_VEHICLE_HEADQUARTERS_NAME = "vehicleHeadquartersName";
    public static final String ATTR_VEHICLE_CATEGORY_NAME = "vehicleCategoryName";
    public static final String ATTR_CURRENT_PAGE = "currentPage";
    public static final String ATTR_PAGINATION_BASE_PATH = "paginationBasePath";
    public static final String ATTR_TOTAL_PAGES = "totalPages";
    public static final String ATTR_TOTAL_RESULTS = "totalResults";
    public static final String ATTR_PAGE_SIZE = "pageSize";
    public static final String PARAM_PAGE_SIZE = ATTR_PAGE_SIZE;
    public static final String ATTR_CURRENT_ACTION = "currentAction";
    public static final String ATTR_SELECTED_MODEL = "selectedModel";
    public static final String ATTR_SELECTED_YEAR_FROM = "selectedYearFrom";
    public static final String ATTR_SELECTED_YEAR_TO = "selectedYearTo";
    public static final String ATTR_SELECTED_MILEAGE_MIN = "selectedMileageMin";
    public static final String ATTR_SELECTED_MILEAGE_MAX = "selectedMileageMax";
    public static final String ATTR_FORM_ERRORS = "errors";
    public static final String ATTR_FORM_DATA = "formData";

    // Usuario y empleado en sesión
    public static final String ATTR_CURRENT_USER = "currentUser";
    public static final String ATTR_CURRENT_EMPLOYEE = "currentEmployee";

    public static final String ATTR_PAGE_TITLE = "pageTitle";
    public static final String ATTR_LOGIN_EMAIL = "loginEmail";
    public static final String ATTR_REDIRECT_TARGET = "redirectTarget";
    public static final String ATTR_2FA_CODE = "twoFactorCode";
    public static final String ATTR_2FA_CODE_TIMESTAMP = "twoFactorCodeTimestamp";
    public static final String ATTR_PENDING_USER = "pendingUser";
    public static final String ATTR_PENDING_REMEMBER = "pendingRemember";

    public static final String ATTR_PROFILE_TYPE = "profileType";
    public static final String ATTR_PROFILE_ADDRESS = "profileAddress";
    public static final String ATTR_PROFILE_BIRTH_DATE = "profileBirthDate";
    public static final String ATTR_PROFILE_CREATED_AT = "profileCreatedAt";
    public static final String ATTR_PROFILE_UPDATED_AT = "profileUpdatedAt";

    public static final String PARAM_REMEMBER_ME = "remember";
    public static final String PARAM_PAGE = "page";
    public static final String PARAM_CATEGORY_ID = "categoryId";
    public static final String PARAM_MAX_PRICE = "maxPrice";
    public static final String PARAM_MIN_PRICE = "minPrice";
    public static final String PARAM_HEADQUARTERS_ID = "headquartersId";
    public static final String PARAM_SEARCH = "search";
    public static final String PARAM_MODEL = "model";
    public static final String PARAM_YEAR_FROM = "yearFrom";
    public static final String PARAM_YEAR_TO = "yearTo";
    public static final String PARAM_MILEAGE_MIN = "mileageMin";
    public static final String PARAM_MILEAGE_MAX = "mileageMax";
    public static final String PARAM_PROVINCE_ID = "provinceId";
    public static final String PARAM_CITY_ID = "cityId";
    public static final String PARAM_ADDRESS_NUMBER = "addressNumber";

    public static final String COOKIE_REMEMBER_USER = "rememberUser";

    public static final String PATH_PRIVATE_ROOT = "/private/";
    public static final String PATH_APP_ROOT = "/app/";
    public static final String ROUTE_PRIVATE_HOME = "/app/home";
    public static final String ROUTE_PUBLIC_HOME = "/public/home";
    public static final String ROUTE_PUBLIC_VEHICLES = "/public/vehicle/catalog";
    public static final String ROUTE_PUBLIC_REGISTER_USER = Views.PUBLIC_REGISTER_USER;
    public static final String ROUTE_PUBLIC_LOGIN = "/public/login";
    public static final String ROUTE_PUBLIC_LOGIN_CONTROLLER = "/public/login";
    public static final String ROUTE_PRIVATE_PROFILE = "/private/profile";

    public static final String PARAM_SUCCESS_FLAG = "success";
    public static final String PARAM_UPDATED_FLAG = "updated";
    public static final String VALUE_FLAG_TRUE = "1";
    public static final String VALUE_FLAG_FALSE = "0";

    public static final String CONTEXT_REGISTERED_USERS = "rentexpress.registeredUsers";
    public static final String CONTEXT_CREDENTIALS = "rentexpress.credentials";
    public static final String CONTEXT_KNOWN_EMAILS = "rentexpress.knownEmails";
    public static final String CONTEXT_RECOVERY_TOKENS = "rentexpress.recoveryTokens";
    public static final Integer ROLE_CLIENT = 3;

}
