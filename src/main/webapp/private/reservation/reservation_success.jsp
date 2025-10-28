<%@ include file="/common/header.jsp" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<fmt:setLocale value="es_ES" scope="request" />
<c:set var="summary" value="${reservationSummary}" />

<c:if test="${summary == null}">
    <div class="alert alert-warning">No hay ninguna simulación de reserva disponible.</div>
</c:if>
<c:if test="${summary != null}">
    <div class="card card-common mb-4">
        <div class="card-header">Reserva simulada · ${reservationReference}</div>
        <div class="card-body p-4">
            <p class="lead">¡Gracias por tu confianza, <strong>${summary.contactEmail}</strong>!</p>
            <p>Hemos generado un resumen académico de la reserva para que puedas validar la lógica en tu futura integración con base de datos.</p>
            <div class="row g-4 mt-1">
                <div class="col-md-6">
                    <div class="summary-box">
                        <h2 class="h6 text-uppercase text-muted">Vehículo</h2>
                        <p class="h5 mb-1">${summary.vehicle.brand} ${summary.vehicle.model}</p>
                        <p class="text-muted mb-0">Categoría: ${summary.vehicleCategoryName}</p>
                        <p class="text-muted mb-0">Año ${summary.vehicle.manufactureYear}</p>
                </div>
                </div>
                <div class="col-md-6">
                    <div class="summary-box">
                        <h2 class="h6 text-uppercase text-muted">Fechas</h2>
                        <p class="mb-1">Recogida: <strong>${summary.formattedStartDate}</strong></p>
                        <p class="mb-1">Devolución: <strong>${summary.formattedEndDate}</strong></p>
                        <p class="text-muted mb-0">Duración: ${summary.rentalDays} días</p>
                    </div>
                </div>
                <div class="col-md-6">
                    <div class="summary-box">
                        <h2 class="h6 text-uppercase text-muted">Sedes</h2>
                        <p class="mb-1">Recogida: <strong>${summary.pickupHeadquarters}</strong></p>
                        <p class="mb-0">Devolución: <strong>${summary.returnHeadquarters}</strong></p>
                </div>
                </div>
                <div class="col-md-6">
                    <div class="summary-box">
                        <h2 class="h6 text-uppercase text-muted">Importes estimados</h2>
                        <p class="mb-1">Vehículo: <strong><fmt:formatNumber value="${summary.vehicleSubtotal}" type="currency" currencySymbol="€"/></strong></p>
                        <p class="mb-0">Total: <strong><fmt:formatNumber value="${summary.total}" type="currency" currencySymbol="€"/></strong></p>
                    </div>
                </div>
            </div>
        </div>
    </div>
    <div class="d-flex gap-2">
        <a class="btn btn-brand" href="${ctx}/public/vehicles">
            <i class="bi bi-arrow-left me-2"></i>Volver al catálogo
        </a>
        <a class="btn btn-outline-brand" href="${ctx}/app/welcome">
            <i class="bi bi-house-door me-2"></i>Ir a la página principal
        </a>
    </div>
</c:if>

<%@ include file="/common/footer.jsp" %>
