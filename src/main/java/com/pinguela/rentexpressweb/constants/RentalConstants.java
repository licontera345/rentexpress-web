package com.pinguela.rentexpressweb.constants;

/**
 * Constantes específicas para la gestión y analítica de alquileres.
 */
public final class RentalConstants {

    private RentalConstants() {
    }

    public static final String PARAM_STATUS = "status";
    public static final String PARAM_START_FROM = "startDateFrom";
    public static final String PARAM_START_TO = "startDateTo";
    public static final String PARAM_MIN_COST = "minCost";
    public static final String PARAM_MAX_COST = "maxCost";
    public static final String PARAM_ACTION = "action";

    public static final String ATTR_RENTAL_FILTERS = "rentalFilters";
    public static final String ATTR_RENTAL_ERRORS = "rentalFilterErrors";
    public static final String ATTR_RENTALS = "rentals";
    public static final String ATTR_LATEST_RENTALS = "latestRentals";
    public static final String ATTR_STATUS_OPTIONS = "rentalStatusOptions";
    public static final String ATTR_STATUS_NAMES = "rentalStatusNames";
    public static final String ATTR_STATUS_COUNTS = "rentalStatusCounts";
    public static final String ATTR_RENTAL_SUMMARY = "rentalSummary";
    public static final String ATTR_CONVERSION_RESULT = "rentalConversionResult";

    public static final String ATTR_PARAM_ACTION = "rentalParamAction";
    public static final String ATTR_PARAM_STATUS = "rentalParamStatus";
    public static final String ATTR_PARAM_START_FROM = "rentalParamStartFrom";
    public static final String ATTR_PARAM_START_TO = "rentalParamStartTo";
    public static final String ATTR_PARAM_MIN_COST = "rentalParamMinCost";
    public static final String ATTR_PARAM_MAX_COST = "rentalParamMaxCost";
}
