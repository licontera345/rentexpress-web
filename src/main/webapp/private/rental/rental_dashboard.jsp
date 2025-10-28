<%@ page import="com.pinguela.rentexpressweb.constants.RentalConstants" %>
<%@ include file="/common/header.jsp" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<fmt:setLocale value="es_ES" scope="request" />
<c:set var="filters" value="${rentalFilters}" />
<c:set var="errors" value="${rentalFilterErrors}" />
<c:set var="statusOptions" value="${rentalStatusOptions}" />
<c:set var="statusCounts" value="${rentalStatusCounts}" />
<c:set var="summary" value="${rentalSummary}" />
<c:set var="rentals" value="${rentals}" />
<c:set var="latestRentals" value="${latestRentals}" />

<div class="d-flex justify-content-between align-items-start flex-wrap gap-3 mb-4">
    <div>
        <h1 class="h3 fw-semibold mb-1">Panel privado de alquileres</h1>
        <p class="text-muted mb-0">Monitoriza la conversión de reservas, estados de vehículos e ingresos estimados.</p>
    </div>
    <form method="post" action="${ctx}/app/rentals/private" class="d-flex gap-2 align-items-center">
        <input type="hidden" name="${RentalConstants.PARAM_ACTION}" value="autoconvert" />
        <button type="submit" class="btn btn-brand">
            <i class="bi bi-lightning-charge me-2"></i>Convertir reservas activas
        </button>
    </form>
</div>

<c:if test="${not empty flashSuccess}">
    <div class="alert alert-success shadow-soft">${flashSuccess}</div>
</c:if>
<c:if test="${not empty flashError}">
    <div class="alert alert-danger shadow-soft">${flashError}</div>
</c:if>
<c:if test="${not empty flashInfo}">
    <div class="alert alert-info shadow-soft">${flashInfo}</div>
</c:if>

<div class="row g-4 align-items-start">
    <div class="col-xl-4">
        <div class="card shadow-soft analytics-card">
            <div class="card-body">
                <h2 class="h5 fw-semibold mb-3">Filtra tus alquileres</h2>
                <p class="text-muted">Aplica filtros para centrarte en los alquileres que más te interesan analizar.</p>
                <form method="get" action="${ctx}/app/rentals/private" class="analytics-form">
                    <div class="mb-3">
                        <label for="status" class="form-label">Estado</label>
                        <select class="form-select" id="status" name="${RentalConstants.PARAM_STATUS}">
                            <option value="">Todos</option>
                            <c:forEach var="status" items="${statusOptions}">
                                <option value="${status.rentalStatusId}"
                                        ${status.rentalStatusId eq filters[RentalConstants.PARAM_STATUS] ? 'selected' : ''}>
                                    ${status.statusName}
                                </option>
                            </c:forEach>
                        </select>
                    </div>
                    <div class="row g-2 mb-3">
                        <div class="col">
                            <label for="startFrom" class="form-label">Inicio desde</label>
                            <input type="date" class="form-control" id="startFrom"
                                   name="${RentalConstants.PARAM_START_FROM}"
                                   value="${filters[RentalConstants.PARAM_START_FROM]}">
                        </div>
                        <div class="col">
                            <label for="startTo" class="form-label">Inicio hasta</label>
                            <input type="date" class="form-control" id="startTo"
                                   name="${RentalConstants.PARAM_START_TO}"
                                   value="${filters[RentalConstants.PARAM_START_TO]}">
                        </div>
                    </div>
                    <div class="row g-2 mb-4">
                        <div class="col">
                            <label for="minCost" class="form-label">Importe mínimo (€)</label>
                            <input type="number" step="0.01" min="0" class="form-control" id="minCost"
                                   name="${RentalConstants.PARAM_MIN_COST}"
                                   value="${filters[RentalConstants.PARAM_MIN_COST]}">
                        </div>
                        <div class="col">
                            <label for="maxCost" class="form-label">Importe máximo (€)</label>
                            <input type="number" step="0.01" min="0" class="form-control" id="maxCost"
                                   name="${RentalConstants.PARAM_MAX_COST}"
                                   value="${filters[RentalConstants.PARAM_MAX_COST]}">
                        </div>
                    </div>
                    <div class="d-grid gap-2">
                        <button type="submit" class="btn btn-brand">Aplicar filtros</button>
                        <a class="btn btn-outline-brand" href="${ctx}/app/rentals/private">Limpiar</a>
                    </div>
                </form>
                <c:if test="${not empty errors}">
                    <div class="alert alert-warning mt-4">
                        <ul class="mb-0">
                            <c:forEach var="error" items="${errors}">
                                <li>${error}</li>
                            </c:forEach>
                        </ul>
                    </div>
                </c:if>
            </div>
        </div>
    </div>
    <div class="col-xl-8">
        <div class="row g-3 mb-4">
            <div class="col-md-3">
                <div class="analytics-stat">
                    <span class="analytics-label">Alquileres filtrados</span>
                    <span class="analytics-value">${summary.filteredCount}</span>
                </div>
            </div>
            <div class="col-md-3">
                <div class="analytics-stat">
                    <span class="analytics-label">Alquileres totales</span>
                    <span class="analytics-value">${summary.totalCount}</span>
                </div>
            </div>
            <div class="col-md-3">
                <div class="analytics-stat">
                    <span class="analytics-label">Ingresos filtrados</span>
                    <span class="analytics-value">
                        <fmt:formatNumber value="${summary.filteredRevenue}" type="currency" currencySymbol="€"/>
                    </span>
                </div>
            </div>
            <div class="col-md-3">
                <div class="analytics-stat">
                    <span class="analytics-label">Ticket medio</span>
                    <span class="analytics-value">
                        <fmt:formatNumber value="${summary.averageTicket}" type="currency" currencySymbol="€"/>
                    </span>
                </div>
            </div>
        </div>
        <div class="row g-3 mb-4">
            <div class="col-md-6">
                <div class="analytics-stat">
                    <span class="analytics-label">Alquileres activos</span>
                    <span class="analytics-value">${summary.activeRentals}</span>
                </div>
            </div>
            <div class="col-md-6">
                <div class="analytics-stat">
                    <span class="analytics-label">Duración media (filtro)</span>
                    <span class="analytics-value">
                        <fmt:formatNumber value="${summary.averageFilteredDuration}" minFractionDigits="1" maxFractionDigits="1" /> días
                    </span>
                </div>
            </div>
        </div>

        <div class="card card-common mb-4">
            <div class="card-header">Distribución por estado</div>
            <div class="card-body">
                <div class="row g-3">
                    <c:forEach var="status" items="${statusOptions}">
                        <div class="col-sm-6 col-lg-4">
                            <div class="status-pill">
                                <span class="status-label">${status.statusName}</span>
                                <span class="status-value">${statusCounts[status.rentalStatusId]}</span>
                            </div>
                        </div>
                    </c:forEach>
                </div>
            </div>
        </div>

        <div class="card card-common mb-4">
            <div class="card-header">Últimos movimientos</div>
            <div class="card-body">
                <c:choose>
                    <c:when test="${empty latestRentals}">
                        <p class="text-muted mb-0">No hay alquileres para mostrar con los filtros aplicados.</p>
                    </c:when>
                    <c:otherwise>
                        <ul class="timeline list-unstyled mb-0">
                            <c:forEach var="rental" items="${latestRentals}">
                                <li class="timeline-item">
                                    <div class="timeline-header">
                                        <span class="timeline-id">#${rental.id}</span>
                                        <span class="timeline-status">${rental.status}</span>
                                    </div>
                                    <div class="timeline-body">
                                        <div><strong>${rental.brand} ${rental.model}</strong> · ${rental.licensePlate}</div>
                                        <div class="text-muted">${rental.start} — ${rental.end}</div>
                                        <div class="timeline-footer">
                                            <fmt:formatNumber value="${rental.totalCost}" type="currency" currencySymbol="€" />
                                        </div>
                                    </div>
                                </li>
                            </c:forEach>
                        </ul>
                    </c:otherwise>
                </c:choose>
            </div>
        </div>

        <div class="card card-common">
            <div class="card-header">Detalle de alquileres</div>
            <div class="card-body p-0">
                <div class="table-responsive">
                    <table class="table table-hover mb-0 align-middle">
                        <thead class="table-light">
                            <tr>
                                <th>ID</th>
                                <th>Vehículo</th>
                                <th>Estado</th>
                                <th>Inicio</th>
                                <th>Fin</th>
                                <th class="text-end">Importe</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:if test="${empty rentals}">
                                <tr>
                                    <td colspan="6" class="text-center text-muted py-4">No hay datos que coincidan con los filtros.</td>
                                </tr>
                            </c:if>
                            <c:forEach var="rental" items="${rentals}">
                                <tr>
                                    <td>#${rental.id}</td>
                                    <td>
                                        <div class="fw-semibold">${rental.brand} ${rental.model}</div>
                                        <small class="text-muted">${rental.licensePlate}</small>
                                    </td>
                                    <td><span class="badge bg-brand bg-opacity-25 text-brand">${rental.status}</span></td>
                                    <td>${rental.start}</td>
                                    <td>${rental.end}</td>
                                    <td class="text-end">
                                        <fmt:formatNumber value="${rental.totalCost}" type="currency" currencySymbol="€" />
                                    </td>
                                </tr>
                            </c:forEach>
                        </tbody>
                    </table>
                </div>
            </div>
        </div>
    </div>
</div>

<%@ include file="/common/footer.jsp" %>
