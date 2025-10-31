<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ include file="/common/header.jsp" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<c:if test="${empty currentEmployee}">
    <c:redirect url="${ctx}/public/home" />
</c:if>
<c:set var="filters" value="${rentalFilters}" />
<c:set var="errors" value="${rentalFilterErrors}" />
<c:set var="statusOptions" value="${rentalStatusOptions}" />
<c:set var="statusCounts" value="${rentalStatusCounts}" />
<c:set var="summary" value="${rentalSummary}" />
<c:set var="rentals" value="${rentals}" />
<c:set var="latestRentals" value="${latestRentals}" />
<c:set var="paramStatus" value="${rentalParamStatus}" />
<c:set var="paramStartFrom" value="${rentalParamStartFrom}" />
<c:set var="paramStartTo" value="${rentalParamStartTo}" />
<c:set var="paramMinCost" value="${rentalParamMinCost}" />
<c:set var="paramMaxCost" value="${rentalParamMaxCost}" />

<div class="row g-4 align-items-start">
    <div class="col-lg-4">
        <div class="card shadow-soft analytics-card">
            <div class="card-body">
                <h2 class="h5 fw-semibold mb-3"><fmt:message key="public.rentals.report.sidebar.title" /></h2>
                <p class="text-muted"><fmt:message key="public.rentals.report.sidebar.description" /></p>
                <form method="get" action="${ctx}/public/rentals" class="analytics-form">
                    <div class="mb-3">
                        <label for="status" class="form-label"><fmt:message key="public.rentals.report.filter.status.label" /></label>
                        <select class="form-select" id="status" name="${paramStatus}">
                            <option value=""><fmt:message key="public.rentals.report.filter.status.all" /></option>
                            <c:forEach var="status" items="${statusOptions}">
                                <option value="${status.rentalStatusId}"
                                        ${status.rentalStatusId eq filters[paramStatus] ? 'selected' : ''}>
                                    ${status.statusName}
                                </option>
                            </c:forEach>
                        </select>
                    </div>
                    <div class="row g-2 mb-3">
                        <div class="col">
                            <label for="startFrom" class="form-label"><fmt:message key="public.rentals.report.filter.startFrom" /></label>
                            <input type="date" class="form-control" id="startFrom"
                                   name="${paramStartFrom}"
                                   value="${filters[paramStartFrom]}">
                        </div>
                        <div class="col">
                            <label for="startTo" class="form-label"><fmt:message key="public.rentals.report.filter.startTo" /></label>
                            <input type="date" class="form-control" id="startTo"
                                   name="${paramStartTo}"
                                   value="${filters[paramStartTo]}">
                        </div>
                    </div>
                    <div class="row g-2 mb-4">
                        <div class="col">
                            <label for="minCost" class="form-label"><fmt:message key="public.rentals.report.filter.minCost" /></label>
                            <input type="number" step="0.01" min="0" class="form-control" id="minCost"
                                   name="${paramMinCost}"
                                   value="${filters[paramMinCost]}">
                        </div>
                        <div class="col">
                            <label for="maxCost" class="form-label"><fmt:message key="public.rentals.report.filter.maxCost" /></label>
                            <input type="number" step="0.01" min="0" class="form-control" id="maxCost"
                                   name="${paramMaxCost}"
                                   value="${filters[paramMaxCost]}">
                        </div>
                    </div>
                    <div class="d-grid gap-2">
                        <button type="submit" class="btn btn-brand"><fmt:message key="public.rentals.report.actions.apply" /></button>
                        <a class="btn btn-outline-brand" href="${ctx}/public/rentals"><fmt:message key="public.rentals.report.actions.clear" /></a>
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
    <div class="col-lg-8">
        <div class="row g-3 mb-4">
            <div class="col-md-4">
                <div class="analytics-stat">
                    <span class="analytics-label"><fmt:message key="public.rentals.report.stat.filtered" /></span>
                    <span class="analytics-value">${summary.totalRentals}</span>
                </div>
            </div>
            <div class="col-md-4">
                <div class="analytics-stat">
                    <span class="analytics-label"><fmt:message key="public.rentals.report.stat.revenue" /></span>
                    <span class="analytics-value">
                        <fmt:formatNumber value="${summary.totalRevenue}" type="currency" currencySymbol="€"/>
                    </span>
                </div>
            </div>
            <div class="col-md-4">
                <div class="analytics-stat">
                    <span class="analytics-label"><fmt:message key="public.rentals.report.stat.duration" /></span>
                    <span class="analytics-value">
                        <fmt:message key="public.rentals.report.stat.duration.value">
                            <fmt:param>
                                <fmt:formatNumber value="${summary.averageDuration}" minFractionDigits="1" maxFractionDigits="1" />
                            </fmt:param>
                        </fmt:message>
                    </span>
                </div>
            </div>
        </div>

        <div class="card card-common mb-4">
            <div class="card-header"><fmt:message key="public.rentals.report.section.statusDistribution" /></div>
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
            <div class="card-header"><fmt:message key="public.rentals.report.section.latest" /></div>
            <div class="card-body">
                <c:choose>
                    <c:when test="${empty latestRentals}">
                        <p class="text-muted mb-0"><fmt:message key="public.rentals.report.section.latest.empty" /></p>
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
            <div class="card-header"><fmt:message key="public.rentals.report.section.details" /></div>
            <div class="card-body p-0">
                <div class="table-responsive">
                    <table class="table table-hover mb-0 align-middle">
                        <thead class="table-light">
                            <tr>
                                <th><fmt:message key="public.rentals.report.table.header.id" /></th>
                                <th><fmt:message key="public.rentals.report.table.header.vehicle" /></th>
                                <th><fmt:message key="public.rentals.report.table.header.status" /></th>
                                <th><fmt:message key="public.rentals.report.table.header.start" /></th>
                                <th><fmt:message key="public.rentals.report.table.header.end" /></th>
                                <th class="text-end"><fmt:message key="public.rentals.report.table.header.amount" /></th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:if test="${empty rentals}">
                                <tr>
                                    <td colspan="6" class="text-center text-muted py-4"><fmt:message key="public.rentals.report.table.empty" /></td>
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
