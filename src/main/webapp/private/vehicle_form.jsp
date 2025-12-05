<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>



<%-- ============================================
     CONFIGURACIÓN
     ============================================ --%>
<fmt:setLocale value="${sessionScope.appLocale != null ? sessionScope.appLocale : pageContext.request.locale}" scope="session" />
<fmt:setBundle basename="i18n.Messages" scope="session" />
<c:set var="ctx" value="${pageContext.request.contextPath}" />
<c:set var="vehicle" value="${requestScope.vehicle}" />
<c:set var="errors" value="${requestScope.errors}" />
<c:set var="errorGeneral" value="${requestScope.errorGeneral}" />
<%@ include file="/common/header.jsp" %>

<%-- ============================================
     VALIDACIONES
     ============================================ --%>

<%-- ============================================
     FORMULARIO/CONTENIDO
     ============================================ --%>
<section class="private-section py-6">
    <div class="container narrow">
        <header class="page-header">
            <span class="section-eyebrow"><fmt:message key="vehicle.manage.pageTitle" /></span>
            <h2 class="page-title">
                <c:choose>
                    <c:when test="${empty vehicle.vehicleId}">
                        <fmt:message key="vehicle.form.createTitle" />
                    </c:when>
                    <c:otherwise>
                        <fmt:message key="vehicle.form.editTitle" />
                    </c:otherwise>
                </c:choose>
            </h2>
            <p class="page-subtitle"><fmt:message key="vehicle.manage.filters.title" /></p>
        </header>
        <c:if test="${not empty errorGeneral}">
            <div class="alert alert-danger">${errorGeneral}</div>
        </c:if>
        <c:if test="${errors != null and not errors.isEmpty()}">
            <div class="alert alert-danger" role="alert">
                <c:forEach var="entry" items="${errors.all}">
                    <div>${entry.message}</div>
                </c:forEach>
            </div>
        </c:if>
        <form method="post" action="${ctx}/private/VehicleServlet" class="card-common shadow-soft form-card">
            <input type="hidden" name="action" value="${empty vehicle.vehicleId ? 'createVehicle' : 'updateVehicle'}" />
            <c:if test="${not empty vehicle.vehicleId}">
                <input type="hidden" name="vehicleId" value="${vehicle.vehicleId}" />
            </c:if>
            <div class="grid two-columns">
                <div class="form-group">
                    <label for="brand"><fmt:message key="employee.manage.vehicle.brand" /></label>
                    <input type="text" id="brand" name="brand" value="${vehicle.brand}" required class="form-control" />
                </div>
                <div class="form-group">
                    <label for="model"><fmt:message key="employee.manage.vehicle.model" /></label>
                    <input type="text" id="model" name="model" value="${vehicle.model}" required class="form-control" />
                </div>
                <div class="form-group">
                    <label for="licensePlate"><fmt:message key="employee.manage.vehicle.licensePlate" /></label>
                    <input type="text" id="licensePlate" name="licensePlate" value="${vehicle.licensePlate}" required class="form-control" />
                </div>
                <div class="form-group">
                    <label for="vinNumber"><fmt:message key="employee.manage.vehicle.vin" /></label>
                    <input type="text" id="vinNumber" name="vinNumber" value="${vehicle.vinNumber}" class="form-control" />
                </div>
                <div class="form-group">
                    <label for="manufactureYear"><fmt:message key="employee.manage.vehicle.year" /></label>
                    <input type="number" id="manufactureYear" name="manufactureYear" value="${vehicle.manufactureYear}" class="form-control" />
                </div>
                <div class="form-group">
                    <label for="currentMileage"><fmt:message key="employee.manage.vehicle.mileage" /></label>
                    <input type="number" id="currentMileage" name="currentMileage" value="${vehicle.currentMileage}" class="form-control" />
                </div>
                <div class="form-group">
                    <label for="dailyPrice"><fmt:message key="employee.manage.vehicle.price" /></label>
                    <input type="number" step="0.01" id="dailyPrice" name="dailyPrice" value="${vehicle.dailyPrice}" required class="form-control" />
                </div>
                <div class="form-group">
                    <label for="categoryId"><fmt:message key="employee.manage.vehicle.category" /></label>
                    <select id="categoryId" name="categoryId" required class="form-select">
                        <option value=""><fmt:message key="employee.manage.vehicle.select" /></option>
                        <c:forEach var="category" items="${requestScope.categories}">
                            <option value="${category.categoryId}" ${category.categoryId eq vehicle.categoryId ? 'selected' : ''}>
                                <c:out value="${category.categoryName}" />
                            </option>
                        </c:forEach>
                    </select>
                </div>
                <div class="form-group">
                    <label for="statusId"><fmt:message key="employee.manage.vehicle.status" /></label>
                    <select id="statusId" name="statusId" required class="form-select">
                        <option value=""><fmt:message key="employee.manage.vehicle.select" /></option>
                        <c:forEach var="status" items="${requestScope.statuses}">
                            <option value="${status.vehicleStatusId}" ${status.vehicleStatusId eq vehicle.vehicleStatusId ? 'selected' : ''}>
                                <c:out value="${status.statusName}" />
                            </option>
                        </c:forEach>
                    </select>
                </div>
                <div class="form-group">
                    <label for="headquartersId"><fmt:message key="employee.manage.vehicle.headquarters" /></label>
                    <select id="headquartersId" name="headquartersId" required class="form-select">
                        <option value=""><fmt:message key="employee.manage.employee.select" /></option>
                        <c:forEach var="hq" items="${requestScope.headquarters}">
                            <option value="${hq.id}" ${hq.id eq vehicle.currentHeadquartersId ? 'selected' : ''}>
                                <c:out value="${hq.name}" />
                            </option>
                        </c:forEach>
                    </select>
                </div>
            </div>
            <div class="form-actions">
                <button type="submit" class="btn btn-primary"><fmt:message key="employee.manage.submit" /></button>
                <a class="btn btn-secondary" href="${ctx}/private/VehicleServlet"><fmt:message key="actions.cancel" /></a>
            </div>
        </form>
    </div>
</section>
<%@ include file="/common/footer.jsp" %>
