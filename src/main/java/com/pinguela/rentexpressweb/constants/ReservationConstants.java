package com.pinguela.rentexpressweb.constants;

/**
 * Constantes para la gestión de reservas en la aplicación.
 */
public final class ReservationConstants {

    private ReservationConstants() {
    }

    public static final String PARAM_VEHICLE_ID = "vehicleId";
    public static final String PARAM_START_DATE = "startDate";
    public static final String PARAM_END_DATE = "endDate";
    public static final String PARAM_START_TIME = "startTime";
    public static final String PARAM_END_TIME = "endTime";
    public static final String PARAM_PICKUP_HEADQUARTERS = "pickupHeadquarters";
    public static final String PARAM_PICKUP_HEADQUARTERS_ID = "pickupHeadquartersId";
    public static final String PARAM_RETURN_HEADQUARTERS = "returnHeadquarters";
    public static final String PARAM_EMPLOYEE_ID = "employeeId";

    public static final String ATTR_HEADQUARTERS = "headquarters";
    public static final String ATTR_RESERVATION_ERRORS = "reservationErrors";
    public static final String ATTR_RESERVATION_FORM = "reservationForm";
    public static final String ATTR_RESERVATION_SUMMARY = "reservationSummary";
    public static final String ATTR_RESERVATION_REFERENCE = "reservationReference";
    public static final String ATTR_CART_VEHICLE = "reservationCartVehicle";
    public static final String ATTR_CART_TIMESTAMP = "reservationCartTimestamp";

    public static final String ATTR_PARAM_VEHICLE_ID = "reservationParamVehicleId";
    public static final String ATTR_PARAM_START_DATE = "reservationParamStartDate";
    public static final String ATTR_PARAM_END_DATE = "reservationParamEndDate";
    public static final String ATTR_PARAM_START_TIME = "reservationParamStartTime";
    public static final String ATTR_PARAM_END_TIME = "reservationParamEndTime";
    public static final String ATTR_PARAM_PICKUP_HEADQUARTERS = "reservationParamPickupHeadquarters";
    public static final String ATTR_PARAM_RETURN_HEADQUARTERS = "reservationParamReturnHeadquarters";
    public static final String ATTR_PARAM_EMPLOYEE_ID = "reservationParamEmployeeId";
    public static final String ATTR_PICKUP_HEADQUARTERS = "reservationPickupHeadquarters";

    public static final int RESERVATION_STATUS_RESERVED_ID = 1;
    public static final String CONFIG_DEFAULT_EMPLOYEE_ID = "reservation.default.employeeId";
}
