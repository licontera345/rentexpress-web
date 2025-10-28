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
    public static final String PARAM_PICKUP_HEADQUARTERS = "pickupHeadquarters";
    public static final String PARAM_RETURN_HEADQUARTERS = "returnHeadquarters";

    public static final String ATTR_HEADQUARTERS = "headquarters";
    public static final String ATTR_RESERVATION_ERRORS = "reservationErrors";
    public static final String ATTR_RESERVATION_FORM = "reservationForm";
    public static final String ATTR_RESERVATION_SUMMARY = "reservationSummary";
    public static final String ATTR_RESERVATION_REFERENCE = "reservationReference";
}
