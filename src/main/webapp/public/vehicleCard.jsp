<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>


<c:if test="${empty ctx}">
    <c:set var="ctx" value="${pageContext.request.contextPath}" scope="request" />
</c:if>
<c:set var="cardVehicle" value="${requestScope.cardVehicle}" />
<c:if test="${empty cartVehicle}">
    <c:set var="cartVehicle"
        value="${not empty requestScope.cartVehicle ? requestScope.cartVehicle : sessionScope.reservationCartVehicle}" />
</c:if>
<fmt:message var="vehicleMileageUnit" key="vehicle.catalog.feature.mileage.unit" />
<c:if test="${not empty cardVehicle}">
<article class="vehicle-card card-common shadow-soft">
    <header class="vehicle-card-header">
        <div>
            <span class="vehicle-card-chip"><fmt:message key="vehicle.catalog.title" /></span>
            <h3 class="vehicle-name">
                <c:out value="${cardVehicle.brand}" />
                <c:out value=" ${cardVehicle.model}" />
            </h3>
        </div>
        <p class="vehicle-price">
            <span class="vehicle-price-value"><c:out value="${cardVehicle.dailyPrice}" /></span>
            <span class="vehicle-price-unit"><fmt:message key="vehicle.catalog.pricing.perDay" /></span>
        </p>
    </header>
    <c:set var="cardCategoryName" value="" />
    <c:set var="cardHeadquartersName" value="" />
    <c:if test="${not empty categories}">
        <c:forEach var="category" items="${categories}">
            <c:if test="${category.categoryId eq cardVehicle.categoryId}">
                <c:set var="cardCategoryName" value="${category.categoryName}" />
            </c:if>
        </c:forEach>
    </c:if>
    <c:if test="${not empty headquarters}">
        <c:forEach var="hq" items="${headquarters}">
            <c:if test="${hq.id eq cardVehicle.currentHeadquartersId}">
                <c:set var="cardHeadquartersName" value="${hq.name}" />
            </c:if>
        </c:forEach>
    </c:if>
    <ul class="vehicle-features">
        <li>
            <span class="feature-label"><fmt:message key="vehicle.catalog.feature.category" />:</span>
            <span class="feature-value">
                <c:choose>
                    <c:when test="${not empty cardCategoryName}">
                        <c:out value="${cardCategoryName}" />
                    </c:when>
                    <c:otherwise>
                        <fmt:message key="vehicle.catalog.feature.category.unassigned" />
                    </c:otherwise>
                </c:choose>
            </span>
        </li>
        <li>
            <span class="feature-label"><fmt:message key="vehicle.catalog.feature.year" />:</span>
            <span class="feature-value">
                <c:choose>
                    <c:when test="${not empty cardVehicle.manufactureYear}">
                        <c:out value="${cardVehicle.manufactureYear}" />
                    </c:when>
                    <c:otherwise>
                        <fmt:message key="vehicle.catalog.feature.year.unavailable" />
                    </c:otherwise>
                </c:choose>
            </span>
        </li>
        <li>
            <span class="feature-label"><fmt:message key="vehicle.catalog.feature.mileage" />:</span>
            <span class="feature-value">
                <c:choose>
                    <c:when test="${not empty cardVehicle.currentMileage}">
                        <c:out value="${cardVehicle.currentMileage}" /> ${vehicleMileageUnit}
                    </c:when>
                    <c:otherwise>
                        <fmt:message key="vehicle.catalog.feature.mileage.unavailable" />
                    </c:otherwise>
                </c:choose>
            </span>
        </li>
        <li>
            <span class="feature-label"><fmt:message key="vehicle.catalog.feature.currentHeadquarters.label" /></span>
            <span class="feature-value">
                <c:choose>
                    <c:when test="${not empty cardHeadquartersName}">
                        <c:out value="${cardHeadquartersName}" />
                    </c:when>
                    <c:otherwise>
                        <fmt:message key="vehicle.catalog.feature.currentHeadquarters.unassigned" />
                    </c:otherwise>
                </c:choose>
            </span>
        </li>
    </ul>
    <div class="vehicle-card-actions">
        <a class="btn-outline-brand"
            href="${ctx}/public/VehicleServlet?action=viewVehicleDetail&amp;vehicleId=${cardVehicle.vehicleId}">
            <fmt:message key="vehicle.catalog.button.viewDetails" />
        </a>
        <c:choose>
            <c:when test="${not empty cartVehicle and cartVehicle.vehicleId eq cardVehicle.vehicleId}">
                <span class="badge-selected"><fmt:message key="vehicle.catalog.cart.selected" /></span>
            </c:when>
            <c:otherwise>
                <form method="post" action="${ctx}/public/VehicleServlet" class="inline-form">
                    <input type="hidden" name="action" value="addVehicleToCart" />
                    <input type="hidden" name="vehicleId" value="${cardVehicle.vehicleId}" />
                    <button type="submit" class="btn-brand">
                        <span aria-hidden="true">&#43;</span>
                        <fmt:message key="vehicle.catalog.cart.add" />
                    </button>
                </form>
            </c:otherwise>
        </c:choose>
    </div>
</article>
</c:if>
