<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"
    import="com.pinguela.rentexpressweb.constants.VehicleConstants,com.pinguela.rentexpressweb.constants.ReservationConstants" %>
<%@ include file="/common/header.jsp" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<c:set var="vehicle" value="${selectedVehicle}" />
<c:set var="categoryName" value="${selectedVehicleCategoryName}" />
<c:set var="reservationForm" value="${reservationForm}" />
<c:set var="reservationErrors" value="${reservationErrors}" />
<c:set var="headquarters" value="${headquarters}" />

<c:if test="${vehicle == null}">
    <div class="alert alert-danger">No se encontró el vehículo solicitado.</div>
</c:if>
<c:if test="${vehicle != null}">
    <div class="card shadow-soft mb-4">
        <div class="card-body p-4 p-lg-5">
            <span class="vehicle-category">${categoryName}</span>
            <h1 class="display-6 fw-bold text-brand mt-2 mb-3">${vehicle.brand} ${vehicle.model}</h1>
            <div class="row g-3">
                <div class="col-sm-6">
                    <div class="vehicle-spec">
                        <i class="bi bi-calendar3"></i>
                        Año ${vehicle.manufactureYear}
                    </div>
                </div>
                <div class="col-sm-6">
                    <div class="vehicle-spec">
                        <i class="bi bi-speedometer2"></i>
                        Kilometraje actual: ${vehicle.currentMileage}
                    </div>
                </div>
                <div class="col-sm-6">
                    <div class="vehicle-spec">
                        <i class="bi bi-currency-euro"></i>
                        Tarifa diaria: <fmt:formatNumber value="${vehicle.dailyPrice}" type="currency" currencySymbol="€"/>
                    </div>
                </div>
                <div class="col-sm-6">
                    <div class="vehicle-spec">
                        <i class="bi bi-geo-alt"></i>
                        Sede actual ID: ${vehicle.currentHeadquartersId}
                    </div>
                </div>
            </div>
        </div>
    </div>

    <div class="row g-4">
        <div class="col-lg-7">
            <div class="card card-common">
                <div class="card-header">Simula tu reserva</div>
                <div class="card-body">
                    <c:if test="${not empty reservationErrors}">
                        <div class="alert alert-danger">
                            <ul class="mb-0">
                                <c:forEach var="error" items="${reservationErrors}">
                                    <li>${error}</li>
                                </c:forEach>
                            </ul>
                        </div>
                    </c:if>
                    <form method="post" action="${ctx}/app/reservations/private" class="row g-3">
                        <input type="hidden" name="${ReservationConstants.PARAM_VEHICLE_ID}" value="${vehicle.vehicleId}">
                        <div class="col-md-6">
                            <label for="startDate" class="form-label">Fecha de recogida</label>
                            <input type="date" class="form-control" id="startDate"
                                   name="${ReservationConstants.PARAM_START_DATE}"
                                   value="${reservationForm[ReservationConstants.PARAM_START_DATE]}">
                        </div>
                        <div class="col-md-6">
                            <label for="endDate" class="form-label">Fecha de devolución</label>
                            <input type="date" class="form-control" id="endDate"
                                   name="${ReservationConstants.PARAM_END_DATE}"
                                   value="${reservationForm[ReservationConstants.PARAM_END_DATE]}">
                        </div>
                        <div class="col-md-6">
                            <label for="pickup" class="form-label">Sede de recogida</label>
                            <select class="form-select" id="pickup" name="${ReservationConstants.PARAM_PICKUP_HEADQUARTERS}">
                                <option value="">Selecciona una sede</option>
                                <c:forEach var="hq" items="${headquarters}">
                                    <option value="${hq.id}"
                                            ${hq.id eq reservationForm[ReservationConstants.PARAM_PICKUP_HEADQUARTERS] ? 'selected' : ''}>
                                        ${hq.name} (${hq.city.cityName})
                                    </option>
                                </c:forEach>
                            </select>
                        </div>
                        <div class="col-md-6">
                            <label for="return" class="form-label">Sede de devolución</label>
                            <select class="form-select" id="return" name="${ReservationConstants.PARAM_RETURN_HEADQUARTERS}">
                                <option value="">Selecciona una sede</option>
                                <c:forEach var="hq" items="${headquarters}">
                                    <option value="${hq.id}"
                                            ${hq.id eq reservationForm[ReservationConstants.PARAM_RETURN_HEADQUARTERS] ? 'selected' : ''}>
                                        ${hq.name} (${hq.city.cityName})
                                    </option>
                                </c:forEach>
                            </select>
                        </div>
                        <div class="col-12 d-grid">
                            <button type="submit" class="btn btn-brand btn-lg">
                                <i class="bi bi-check-circle me-2"></i>Simular reserva
                            </button>
                        </div>
                    </form>
                </div>
            </div>
        </div>
        <div class="col-lg-5">
            <c:if test="${not empty relatedVehicles}">
                <div class="card shadow-soft">
                    <div class="card-body">
                        <h2 class="h5 fw-semibold mb-3">Vehículos similares</h2>
                        <c:forEach var="related" items="${relatedVehicles}">
                            <div class="related-vehicle d-flex align-items-center mb-3">
                                <div class="flex-grow-1 ms-3">
                                    <div class="fw-semibold">${related.brand} ${related.model}</div>
                                    <small class="text-muted">ID categoría: ${related.categoryId}</small>
                                </div>
                                <a class="btn btn-outline-brand btn-sm"
                                   href="${ctx}/public/vehicles/detail?vehicleId=${related.vehicleId}">Ver</a>
                            </div>
                        </c:forEach>
                    </div>
                </div>
            </c:if>
        </div>
    </div>
</c:if>

<%@ include file="/common/footer.jsp" %>
