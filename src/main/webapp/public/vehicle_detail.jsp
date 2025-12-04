<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>

<%-- ============================================
     CONFIGURACIÓN
     ============================================ --%>
<fmt:setLocale value="${sessionScope.appLocale != null ? sessionScope.appLocale : pageContext.request.locale}" scope="session" />
<fmt:setBundle basename="i18n.Messages" scope="session" />
<c:set var="ctx" value="${pageContext.request.contextPath}" />
<c:set var="vehicle" value="${requestScope.vehicle}" />
<c:set var="cartVehicle"
    value="${not empty requestScope.cartVehicle ? requestScope.cartVehicle : sessionScope.reservationCartVehicle}" />
<c:set var="categories" value="${requestScope.categories}" />
<c:set var="headquarters" value="${requestScope.headquarters}" />
<fmt:message var="vehicleMileageUnit" key="vehicle.catalog.feature.mileage.unit" />
<%@ include file="/common/header.jsp" %>

<%-- ============================================
     FORMULARIO/CONTENIDO
     ============================================ --%>
<section class="detail-section py-6">
    <div class="container">
        <c:choose>
            <c:when test="${not empty vehicle}">
                <c:set var="detailCategoryName" value="" />
                <c:set var="detailHeadquartersName" value="" />
                <c:forEach var="category" items="${categories}">
                    <c:if test="${category.categoryId eq vehicle.categoryId}">
                        <c:set var="detailCategoryName" value="${category.categoryName}" />
                    </c:if>
                </c:forEach>
                <c:forEach var="hq" items="${headquarters}">
                    <c:if test="${hq.id eq vehicle.currentHeadquartersId}">
                        <c:set var="detailHeadquartersName" value="${hq.name}" />
                    </c:if>
                </c:forEach>
                <article class="vehicle-detail-surface card-common shadow-soft">
                    <header class="detail-header">
                        <div class="detail-heading">
                            <span class="section-eyebrow"><fmt:message key="vehicle.catalog.title" /></span>
                            <h2 class="detail-title">
                                <c:out value="${vehicle.brand}" />
                                <c:out value=" ${vehicle.model}" />
                            </h2>
                            <p class="detail-subtitle">
                                <fmt:message key="vehicle.catalog.subtitle" />
                            </p>
                        </div>
                        <div class="detail-price">
                            <span class="price-value"><c:out value="${vehicle.dailyPrice}" /></span>
                            <span class="price-unit"><fmt:message key="vehicle.catalog.pricing.perDay" /></span>
                        </div>
                    </header>
                    <div class="detail-grid">
                        <div class="detail-panel">
                            <ul class="detail-list">
                                <li>
                                    <span class="detail-label"><fmt:message key="vehicle.catalog.feature.category" /></span>
                                    <span class="detail-value">
                                        <c:choose>
                                            <c:when test="${not empty detailCategoryName}">
                                                <c:out value="${detailCategoryName}" />
                                            </c:when>
                                            <c:otherwise>
                                                <fmt:message key="vehicle.catalog.feature.category.unassigned" />
                                            </c:otherwise>
                                        </c:choose>
                                    </span>
                                </li>
                                <li>
                                    <span class="detail-label"><fmt:message key="vehicle.detail.plate" /></span>
                                    <span class="detail-value">
                                        <c:choose>
                                            <c:when test="${not empty vehicle.licensePlate}">
                                                <c:out value="${vehicle.licensePlate}" />
                                            </c:when>
                                            <c:otherwise>
                                                <fmt:message key="vehicle.detail.plate.unavailable" />
                                            </c:otherwise>
                                        </c:choose>
                                    </span>
                                </li>
                                <li>
                                    <span class="detail-label"><fmt:message key="vehicle.detail.year" /></span>
                                    <span class="detail-value">
                                        <c:choose>
                                            <c:when test="${not empty vehicle.manufactureYear}">
                                                <c:out value="${vehicle.manufactureYear}" />
                                            </c:when>
                                            <c:otherwise>
                                                <fmt:message key="vehicle.catalog.feature.year.unavailable" />
                                            </c:otherwise>
                                        </c:choose>
                                    </span>
                                </li>
                                <li>
                                    <span class="detail-label"><fmt:message key="vehicle.detail.mileage" /></span>
                                    <span class="detail-value">
                                        <c:choose>
                                            <c:when test="${not empty vehicle.currentMileage}">
                                                <c:out value="${vehicle.currentMileage}" /> ${vehicleMileageUnit}
                                            </c:when>
                                            <c:otherwise>
                                                <fmt:message key="vehicle.catalog.feature.mileage.unavailable" />
                                            </c:otherwise>
                                        </c:choose>
                                    </span>
                                </li>
                                <li>
                                    <span class="detail-label"><fmt:message key="vehicle.detail.vin" /></span>
                                    <span class="detail-value">
                                        <c:choose>
                                            <c:when test="${not empty vehicle.vinNumber}">
                                                <c:out value="${vehicle.vinNumber}" />
                                            </c:when>
                                            <c:otherwise>
                                                <fmt:message key="vehicle.detail.vin.unavailable" />
                                            </c:otherwise>
                                        </c:choose>
                                    </span>
                                </li>
                                <li>
                                    <span class="detail-label"><fmt:message key="vehicle.catalog.feature.currentHeadquarters.label" /></span>
                                    <span class="detail-value">
                                        <c:choose>
                                            <c:when test="${not empty detailHeadquartersName}">
                                                <c:out value="${detailHeadquartersName}" />
                                            </c:when>
                                            <c:otherwise>
                                                <fmt:message key="vehicle.catalog.feature.currentHeadquarters.unassigned" />
                                            </c:otherwise>
                                        </c:choose>
                                    </span>
                                </li>
                            </ul>
                        </div>
                        <div class="detail-highlight">
                            <div class="detail-pill">
                                <span class="detail-pill-label"><fmt:message key="vehicle.catalog.pricing.dailyRate" /></span>
                                <span class="detail-pill-value">
                                    <c:out value="${vehicle.dailyPrice}" />
                                    <small><fmt:message key="vehicle.catalog.pricing.perDay" /></small>
                                </span>
                            </div>
                            <p class="detail-note"><fmt:message key="vehicle.catalog.header.loginCta" /></p>
                            <div class="detail-actions">
                                <c:choose>
                                    <c:when test="${not empty cartVehicle and cartVehicle.vehicleId eq vehicle.vehicleId}">
                                        <span class="badge-selected"><fmt:message key="vehicle.catalog.cart.selected" /></span>
                                        <a class="btn-brand" href="${ctx}/public/reservations">
                                            <fmt:message key="vehicle.reserve" />
                                        </a>
                                        <form method="post" action="${ctx}/public/VehicleServlet" class="inline-form">
                                            <input type="hidden" name="action" value="releaseVehicle" />
                                            <button type="submit" class="btn-link danger">
                                                <fmt:message key="reservation.form.vehicle.release" />
                                            </button>
                                        </form>
                                    </c:when>
                                    <c:otherwise>
                                        <form method="post" action="${ctx}/public/VehicleServlet" class="inline-form">
                                            <input type="hidden" name="action" value="addVehicleToCart" />
                                            <input type="hidden" name="vehicleId" value="${vehicle.vehicleId}" />
                                            <button type="submit" class="btn-brand">
                                                <span aria-hidden="true">&#43;</span>
                                                <fmt:message key="vehicle.catalog.cart.add" />
                                            </button>
                                        </form>
                                        <a class="btn-outline-brand" href="${ctx}/public/reservations">
                                            <fmt:message key="vehicle.reserve" />
                                        </a>
                                    </c:otherwise>
                                </c:choose>
                                <a class="btn-link" href="${ctx}/public/VehicleServlet"><fmt:message key="actions.back" /></a>
                            </div>
                        </div>
                    </div>
                </article>
            </c:when>
            <c:otherwise>
                <div class="card-common shadow-soft empty-results">
                    <p><fmt:message key="vehicle.detail.notfound" /></p>
                    <a class="btn-outline-brand" href="${ctx}/public/VehicleServlet"><fmt:message key="actions.back" /></a>
                </div>
            </c:otherwise>
        </c:choose>
    </div>
</section>
<jsp:include page="/common/footer.jsp" />
