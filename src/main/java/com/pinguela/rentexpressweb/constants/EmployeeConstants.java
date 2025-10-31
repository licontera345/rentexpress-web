package com.pinguela.rentexpressweb.constants;

/**
 * Constantes específicas para los listados y detalles públicos de empleados.
 */
public final class EmployeeConstants {

    private EmployeeConstants() {
    }

    public static final String PARAM_EMPLOYEE_ID = "employeeId";
    public static final String PARAM_SEARCH = "search";
    public static final String PARAM_ROLE = "role";
    public static final String PARAM_HEADQUARTERS = "headquarters";
    public static final String PARAM_ACTIVE = "active";

    public static final String VALUE_ACTIVE = "active";
    public static final String VALUE_INACTIVE = "inactive";
    public static final String VALUE_ALL = "all";

    public static final String VALUE_SORT_CREATED_DESC = "createdDesc";
    public static final String VALUE_SORT_NAME_ASC = "nameAsc";
    public static final String VALUE_SORT_HEADQUARTERS_ASC = "hqAsc";

    public static final String ATTR_EMPLOYEES = "employees";
    public static final String ATTR_FILTERS = "employeeFilters";
    public static final String ATTR_FILTER_ERRORS = "employeeFilterErrors";
    public static final String ATTR_ROLES = "employeeRoles";
    public static final String ATTR_ROLE_NAMES = "employeeRoleNames";
    public static final String ATTR_HEADQUARTERS = "employeeHeadquarters";
    public static final String ATTR_HEADQUARTERS_NAMES = "employeeHeadquartersNames";
    public static final String ATTR_PAGINATION = "employeePagination";
    public static final String ATTR_SUMMARY = "employeeSummary";
    public static final String ATTR_SELECTED_EMPLOYEE = "selectedEmployee";
    public static final String ATTR_SELECTED_ROLE_NAME = "selectedEmployeeRole";
    public static final String ATTR_SELECTED_HEADQUARTERS = "selectedEmployeeHeadquarters";
    public static final String ATTR_EMPLOYEE_PROFILE = "employeeProfile";

    public static final String SUMMARY_TOTAL = "total";
    public static final String SUMMARY_FILTERED = "filtered";
    public static final String SUMMARY_ACTIVE = "active";
    public static final String SUMMARY_INACTIVE = "inactive";
    public static final String SUMMARY_HEADQUARTERS = "headquarters";
}
