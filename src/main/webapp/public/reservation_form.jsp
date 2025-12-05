<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>


<%-- ============================================
     CONFIGURACIÓN
     ============================================ --%>
<fmt:setLocale value="${sessionScope.appLocale != null ? sessionScope.appLocale : pageContext.request.locale}" scope="session" />
<fmt:setBundle basename="i18n.Messages" scope="session" />
<c:set var="ctx" value="${pageContext.request.contextPath}" />
<c:set var="cartVehicle" value="${requestScope.reservationCartVehicle}" />
<c:set var="headquarters" value="${requestScope.headquarters}" />
<c:set var="formStartDate" value="${requestScope.reservationParamStartDate}" />
<c:set var="formStartTime" value="${requestScope.reservationParamStartTime}" />
<c:set var="formEndDate" value="${requestScope.reservationParamEndDate}" />
<c:set var="formEndTime" value="${requestScope.reservationParamEndTime}" />
<c:set var="pickupHeadquarters" value="${requestScope.reservationPickupHeadquarters}" />
<c:set var="formReturnHeadquarters" value="${requestScope.reservationParamReturnHeadquarters}" />
<fmt:message var="reservationDatesHint" key="reservation.form.help.dates" />
<fmt:message var="reservationHeadquartersHint" key="reservation.form.help.headquarters" />
<fmt:message var="reservationSubmitLabel" key="reservation.form.submit" />
<fmt:message var="reservationSubmitDisabled" key="reservation.form.submit.disabled" />
<%@ include file="/common/header.jsp" %>

<%-- ============================================
     VALIDACIONES
     ============================================ --%>
<c:set var="flashSuccess" value="${requestScope.flashSuccess}" />
<c:set var="flashInfo" value="${requestScope.flashInfo}" />
<c:set var="flashError" value="${requestScope.flashError}" />

<%-- ============================================
     FORMULARIO/CONTENIDO
     ============================================ --%>
<section class="reservation-section py-6">
    <div class="container">
        <header class="catalog-header">
            <span class="section-eyebrow"><fmt:message key="vehicle.catalog.title" /></span>
            <h2 class="section-heading"><fmt:message key="reservation.form.pageTitle" /></h2>
            <p class="catalog-summary"><fmt:message key="reservation.form.intro" /></p>
        </header>

        <div class="reservation-layout">
            <aside class="reservation-summary">
                <c:choose>
                    <c:when test="${not empty cartVehicle}">
                        <c:set var="cartHeadquartersName" value="${pickupHeadquarters.name}" />
                        <div class="cart-card card-common shadow-soft">
                            <h3 class="cart-title"><fmt:message key="reservation.form.vehicle.title" /></h3>
                            <p class="cart-vehicle-name">
                                <c:out value="${cartVehicle.brand}" />
                                <c:out value=" ${cartVehicle.model}" />
                            </p>
                            <p class="cart-vehicle-meta">
                                <fmt:message key="vehicle.catalog.pricing.dailyRate" /> ·
                                <span class="accent">${cartVehicle.dailyPrice}</span>
                                <span class="unit"><fmt:message key="vehicle.catalog.pricing.perDay" /></span>
                            </p>
                            <p class="cart-vehicle-branch">
                                <span class="cart-label"><fmt:message key="vehicle.catalog.feature.currentHeadquarters.label" /></span>
                                <c:choose>
                                    <c:when test="${not empty cartHeadquartersName}">
                                        <c:out value="${cartHeadquartersName}" />
                                    </c:when>
                                    <c:otherwise>
                                        <fmt:message key="vehicle.catalog.feature.currentHeadquarters.unassigned" />
                                    </c:otherwise>
                                </c:choose>
                            </p>
                            <p class="cart-note"><fmt:message key="reservation.form.vehicle.cartInfo" /></p>
                            <div class="cart-actions">
                                <a class="btn-link danger" href="${ctx}/public/VehicleServlet?action=releaseVehicle">
                                    <fmt:message key="reservation.form.vehicle.release" />
                                </a>
                                <a class="btn-outline-brand" href="${ctx}/public/VehicleServlet">
                                    <fmt:message key="layout.vehicles" />
                                </a>
                            </div>
                        </div>
                    </c:when>
                    <c:otherwise>
                        <div class="card-common shadow-soft empty-results">
                            <p><fmt:message key="reservation.form.vehicle.missing" /></p>
                            <a class="btn-brand" href="${ctx}/public/VehicleServlet">
                                <fmt:message key="vehicle.public.catalog.pageTitle" />
                            </a>
                        </div>
                    </c:otherwise>
                </c:choose>
            </aside>

            <div class="reservation-form-card card-common shadow-soft">
                <c:if test="${not empty requestScope.flashSuccess}">
                    <div class="alert alert-success">${requestScope.flashSuccess}</div>
                </c:if>
                <c:if test="${not empty requestScope.flashInfo}">
                    <div class="alert alert-info">${requestScope.flashInfo}</div>
                </c:if>
                <c:if test="${not empty requestScope.flashError}">
                    <div class="alert alert-danger">${requestScope.flashError}</div>
                </c:if>

                <form method="post" action="${ctx}/public/reservations" class="reservation-form">
                    <p class="form-hint">${reservationDatesHint}</p>
                    <input type="hidden" name="employeeId" value="${requestScope.reservationParamEmployeeId}" />
                    <div class="form-grid">
                        <div class="form-group">
                            <label for="startDate"><fmt:message key="reservation.form.startDate" /></label>
                            <input type="date" id="startDate" name="startDate" class="form-control"
                                value="${formStartDate}" />
                        </div>
                        <div class="form-group">
                            <label for="startTime"><fmt:message key="reservation.form.startTime" /></label>
                            <input type="time" id="startTime" name="startTime" class="form-control"
                                value="${formStartTime}" />
                        </div>
                        <div class="form-group">
                            <label for="endDate"><fmt:message key="reservation.form.endDate" /></label>
                            <input type="date" id="endDate" name="endDate" class="form-control"
                                value="${formEndDate}" />
                        </div>
                        <div class="form-group">
                            <label for="endTime"><fmt:message key="reservation.form.endTime" /></label>
                            <input type="time" id="endTime" name="endTime" class="form-control"
                                value="${formEndTime}" />
                        </div>
                        <div class="form-group">
                            <label for="pickup"><fmt:message key="reservation.form.pickup" /></label>
                            <input type="text" id="pickup" name="pickup" class="form-control"
                                value="${pickupHeadquarters.name}" readonly />
                            <input type="hidden" name="pickupHeadquartersId" value="${pickupHeadquarters.id}" />
                        </div>
                        <div class="form-group">
                            <label for="returnHeadquarters"><fmt:message key="reservation.form.return" /></label>
                            <select id="returnHeadquarters" name="returnHeadquarters" class="form-control">
                                <option value=""><fmt:message key="reservation.form.headquarters.placeholder" /></option>
                                <c:forEach var="hq" items="${headquarters}">
                                    <option value="${hq.id}" ${hq.id eq formReturnHeadquarters ? 'selected' : ''}>
                                        <c:out value="${hq.name}" />
                                    </option>
                                </c:forEach>
                            </select>
                        </div>
                    </div>
                    <p class="form-hint">${reservationHeadquartersHint}</p>
                    <div class="form-actions">
                        <button type="submit" class="btn-brand"<c:if test="${empty cartVehicle}"> disabled title="${reservationSubmitDisabled}" aria-disabled="true"</c:if>>
                            ${reservationSubmitLabel}
                        </button>
                        <a class="btn-link" href="${ctx}/public/VehicleServlet"><fmt:message key="actions.back" /></a>
                    </div>
                </form>
            </div>
        </div>
    </div>
</section>
<jsp:include page="/common/footer.jsp" />
