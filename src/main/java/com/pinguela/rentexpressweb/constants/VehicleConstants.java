package com.pinguela.rentexpressweb.constants;

/**
 * Constantes específicas para la gestión del catálogo de vehículos.
 */
public final class VehicleConstants {

    private VehicleConstants() {
    }

    public static final String PARAM_SEARCH = "search";
    public static final String PARAM_CATEGORY = "category";
    public static final String PARAM_MIN_PRICE = "minPrice";
    public static final String PARAM_MAX_PRICE = "maxPrice";
    public static final String PARAM_SORT = "sort";
    public static final String PARAM_VEHICLE_ID = "vehicleId";

    public static final String VALUE_SORT_PRICE_ASC = "priceAsc";
    public static final String VALUE_SORT_PRICE_DESC = "priceDesc";
    public static final String VALUE_SORT_YEAR_DESC = "yearDesc";

    public static final String ATTR_VEHICLES = "vehicles";
    public static final String ATTR_FILTERS = "vehicleFilters";
    public static final String ATTR_FILTER_ERRORS = "vehicleFilterErrors";
    public static final String ATTR_AVAILABLE_CATEGORIES = "vehicleCategories";
    public static final String ATTR_CATEGORY_NAMES = "vehicleCategoryNames";
    public static final String ATTR_SELECTED_CATEGORY_NAME = "selectedVehicleCategoryName";
    public static final String ATTR_SELECTED_VEHICLE = "selectedVehicle";
    public static final String ATTR_RELATED_VEHICLES = "relatedVehicles";
    public static final String ATTR_TOTAL_RESULTS = "totalVehicles";
}
