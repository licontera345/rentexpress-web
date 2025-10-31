<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<fmt:message key="reservation.success.pageTitle" var="reservationSuccessTitle" />
<c:set var="pageTitle" value="${reservationSuccessTitle}" />
<%@ include file="/common/header.jsp" %>
<c:set var="privateHome" value="${empty sessionScope.currentUser ? ctx.concat('/public/home') : ctx.concat('/app/home')}" />
<c:set var="summary" value="${reservationSummary}" />
<c:if test="${summary == null}">
    <div class="alert alert-warning"><fmt:message key="reservation.success.empty" /></div>
</c:if>
<c:if test="${summary != null}">
    <div class="card card-common mb-4">
        <div class="card-header">
            <fmt:message key="reservation.success.header">
                <fmt:param value="${reservationReference}" />
            </fmt:message>
        </div>
        <div class="card-body p-4">
            <p class="lead">
                <fmt:message key="reservation.success.thankYou">
                    <fmt:param>
                        <strong>${summary.contactEmail}</strong>
                    </fmt:param>
                </fmt:message>
            </p>
            <p><fmt:message key="reservation.success.intro" /></p>
            <div class="row g-4 mt-1">
                <div class="col-md-6">
                    <div class="summary-box">
                        <h2 class="h6 text-uppercase text-muted"><fmt:message key="reservation.success.section.vehicle.title" /></h2>
                        <p class="h5 mb-1">${summary.vehicle.brand} ${summary.vehicle.model}</p>
                        <p class="text-muted mb-0">
                            <fmt:message key="reservation.success.section.vehicle.category">
                                <fmt:param value="${summary.vehicleCategoryName}" />
                            </fmt:message>
                        </p>
                        <p class="text-muted mb-0">
                            <fmt:message key="reservation.success.section.vehicle.year">
                                <fmt:param value="${summary.vehicle.manufactureYear}" />
                            </fmt:message>
                        </p>
                    </div>
                </div>
                <div class="col-md-6">
                    <div class="summary-box">
                        <h2 class="h6 text-uppercase text-muted"><fmt:message key="reservation.success.section.dates.title" /></h2>
                        <p class="mb-1">
                            <fmt:message key="reservation.success.section.dates.pickup">
                                <fmt:param>
                                    <strong>${summary.formattedStartDate}</strong>
                                </fmt:param>
                            </fmt:message>
                        </p>
                        <p class="mb-1">
                            <fmt:message key="reservation.success.section.dates.return">
                                <fmt:param>
                                    <strong>${summary.formattedEndDate}</strong>
                                </fmt:param>
                            </fmt:message>
                        </p>
                        <p class="text-muted mb-0">
                            <fmt:message key="reservation.success.section.dates.duration">
                                <fmt:param value="${summary.rentalDays}" />
                            </fmt:message>
                        </p>
                    </div>
                </div>
                <div class="col-md-6">
                    <div class="summary-box">
                        <h2 class="h6 text-uppercase text-muted"><fmt:message key="reservation.success.section.headquarters.title" /></h2>
                        <p class="mb-1">
                            <fmt:message key="reservation.success.section.headquarters.pickup">
                                <fmt:param>
                                    <strong>${summary.pickupHeadquarters}</strong>
                                </fmt:param>
                            </fmt:message>
                        </p>
                        <p class="mb-0">
                            <fmt:message key="reservation.success.section.headquarters.return">
                                <fmt:param>
                                    <strong>${summary.returnHeadquarters}</strong>
                                </fmt:param>
                            </fmt:message>
                        </p>
                    </div>
                </div>
                <div class="col-md-6">
                    <div class="summary-box">
                        <h2 class="h6 text-uppercase text-muted"><fmt:message key="reservation.success.section.costs.title" /></h2>
                        <p class="mb-1">
                            <fmt:message key="reservation.success.section.costs.vehicle">
                                <fmt:param>
                                    <strong><fmt:formatNumber value="${summary.vehicleSubtotal}" type="currency" currencySymbol="€"/></strong>
                                </fmt:param>
                            </fmt:message>
                        </p>
                        <p class="mb-0">
                            <fmt:message key="reservation.success.section.costs.total">
                                <fmt:param>
                                    <strong><fmt:formatNumber value="${summary.total}" type="currency" currencySymbol="€"/></strong>
                                </fmt:param>
                            </fmt:message>
                        </p>
                    </div>
                </div>
            </div>
        </div>
    </div>
    <div class="d-flex gap-2">
        <a class="btn btn-brand" href="${ctx}/public/vehicles">
            <i class="bi bi-arrow-left me-2"></i><fmt:message key="reservation.success.backToCatalog" />
        </a>
        <a class="btn btn-outline-brand" href="${privateHome}">
            <i class="bi bi-house-door me-2"></i><fmt:message key="reservation.success.goHome" />
        </a>
    </div>
</c:if>
<%@ include file="/common/footer.jsp" %>
