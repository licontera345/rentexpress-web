<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<fmt:setBundle basename="i18n.Messages" />
<fmt:setLocale value="${sessionScope.appLocale != null ? sessionScope.appLocale : pageContext.request.locale}" />
<%@ include file="/common/header.jsp" %>
<c:set var="vehicles" value="${requestScope.vehicles}" />
<c:set var="currentPage" value="${requestScope.currentPage}" />
<c:set var="totalPages" value="${requestScope.totalPages}" />
<c:set var="categories" value="${requestScope.categories}" />
<c:set var="statuses" value="${requestScope.statuses}" />
<c:set var="headquarters" value="${requestScope.headquarters}" />
<fmt:message key="vehicle.catalog.filter.search.placeholder" var="vehicleSearchPlaceholder" />
<fmt:message key="vehicle.manage.filter.status.all" var="vehicleStatusAll" />
<fmt:message key="vehicle.catalog.pricing.currency" var="vehicleCurrencySymbol" />
<section class="private-section py-6">
    <div class="container">
        <header class="page-header">
            <div class="page-heading">
                <span class="section-eyebrow"><fmt:message key="vehicle.manage.pageTitle" /></span>
                <h2 class="page-title"><fmt:message key="vehicle.list.title" /></h2>
                <p class="page-subtitle"><fmt:message key="vehicle.manage.table.title" /></p>
            </div>
            <div class="page-actions">
                <a class="btn btn-primary" href="${ctx}/private/VehicleServlet?action=createVehicle">
                    <fmt:message key="vehicle.manage.action.new" />
                </a>
            </div>
        </header>
        <c:if test="${not empty requestScope.flashSuccess}">
            <div class="alert alert-success" role="alert">
                ${requestScope.flashSuccess}
            </div>
        </c:if>
        <c:if test="${not empty requestScope.flashError}">
            <div class="alert alert-danger" role="alert">
                ${requestScope.flashError}
            </div>
        </c:if>
        <form method="get" action="${ctx}/private/VehicleServlet" class="row g-3 mb-4">
            <div class="col-lg-3 col-md-6">
                <label class="form-label" for="searchFilter"><fmt:message key="vehicle.catalog.filter.search" /></label>
                <input type="text" id="searchFilter" name="search" class="form-control"
                    placeholder="${fn:escapeXml(vehicleSearchPlaceholder)}"
                    value="${fn:escapeXml(requestScope.filterSearch)}" />
            </div>
            <div class="col-lg-3 col-md-6">
                <label class="form-label" for="licenseFilter"><fmt:message key="vehicle.manage.filter.licensePlate" /></label>
                <input type="text" id="licenseFilter" name="licensePlate" class="form-control"
                    value="${fn:escapeXml(requestScope.filterLicensePlate)}" />
            </div>
            <div class="col-lg-3 col-md-6">
                <label class="form-label" for="categoryFilter"><fmt:message key="vehicle.catalog.filter.category" /></label>
                <select id="categoryFilter" name="categoryId" class="form-select">
                    <option value=""><fmt:message key="vehicle.catalog.filter.category.all" /></option>
                    <c:forEach var="category" items="${categories}">
                        <option value="${category.categoryId}" ${category.categoryId eq requestScope.filterCategoryId ? 'selected' : ''}>
                            <c:out value="${category.categoryName}" />
                        </option>
                    </c:forEach>
                </select>
            </div>
            <div class="col-lg-3 col-md-6">
                <label class="form-label" for="statusFilter"><fmt:message key="vehicle.manage.filter.status" /></label>
                <select id="statusFilter" name="statusId" class="form-select">
                    <option value=""><c:out value="${vehicleStatusAll}" /></option>
                    <c:forEach var="status" items="${statuses}">
                        <option value="${status.vehicleStatusId}" ${status.vehicleStatusId eq requestScope.filterStatusId ? 'selected' : ''}>
                            <c:out value="${status.statusName}" />
                        </option>
                    </c:forEach>
                </select>
            </div>
            <div class="col-lg-3 col-md-6">
                <label class="form-label" for="headquartersFilter"><fmt:message key="vehicle.catalog.filter.headquarters" /></label>
                <select id="headquartersFilter" name="headquartersId" class="form-select">
                    <option value=""><fmt:message key="common.home.hero.form.headquarters.all" /></option>
                    <c:forEach var="hq" items="${headquarters}">
                        <option value="${hq.id}" ${hq.id eq requestScope.filterHeadquartersId ? 'selected' : ''}>
                            <c:out value="${hq.name}" />
                        </option>
                    </c:forEach>
                </select>
            </div>
            <div class="col-lg-3 col-md-6">
                <label class="form-label" for="priceMinFilter"><fmt:message key="vehicle.catalog.filter.minPrice" /></label>
                <input type="number" step="0.01" min="0" id="priceMinFilter" name="priceMin" class="form-control"
                    value="${requestScope.filterPriceMin}" />
            </div>
            <div class="col-lg-3 col-md-6">
                <label class="form-label" for="priceMaxFilter"><fmt:message key="vehicle.catalog.filter.maxPrice" /></label>
                <input type="number" step="0.01" min="0" id="priceMaxFilter" name="priceMax" class="form-control"
                    value="${requestScope.filterPriceMax}" />
            </div>
            <div class="col-lg-3 col-md-6">
                <label class="form-label" for="yearMinFilter"><fmt:message key="vehicle.manage.filter.minYear" /></label>
                <input type="number" id="yearMinFilter" name="minYear" class="form-control"
                    value="${requestScope.filterYearFrom}" />
            </div>
            <div class="col-lg-3 col-md-6">
                <label class="form-label" for="yearMaxFilter"><fmt:message key="vehicle.manage.filter.maxYear" /></label>
                <input type="number" id="yearMaxFilter" name="maxYear" class="form-control"
                    value="${requestScope.filterYearTo}" />
            </div>
            <div class="col-12 d-flex align-items-end justify-content-end gap-2">
                <button type="submit" class="btn btn-primary"><fmt:message key="vehicle.catalog.filter.apply" /></button>
                <a class="btn btn-outline-secondary" href="${ctx}/private/VehicleServlet"><fmt:message key="vehicle.catalog.filter.clear" /></a>
            </div>
        </form>
        <div class="data-surface card-common shadow-soft">
            <table class="table data-table">
                <thead>
                    <tr>
                        <th><fmt:message key="vehicle.table.model" /></th>
                        <th><fmt:message key="vehicle.table.plate" /></th>
                        <th><fmt:message key="vehicle.table.category" /></th>
                        <th><fmt:message key="vehicle.table.status" /></th>
                        <th><fmt:message key="vehicle.table.headquarters" /></th>
                        <th><fmt:message key="vehicle.table.price" /></th>
                        <th class="text-end"><fmt:message key="vehicle.table.actions" /></th>
                    </tr>
                </thead>
                <tbody>
                    <c:choose>
                        <c:when test="${not empty vehicles}">
                            <c:forEach var="vehicle" items="${vehicles}">
                                <tr>
                                    <td>
                                        <c:out value="${vehicle.brand}" />
                                        <c:if test="${not empty vehicle.model}">
                                            <c:out value=" ${vehicle.model}" />
                                        </c:if>
                                    </td>
                                    <td><c:out value="${vehicle.licensePlate}" default="-" /></td>
                                    <td>
                                        <c:set var="vehicleCategoryName" value="" />
                                        <c:forEach var="category" items="${categories}">
                                            <c:if test="${category.categoryId eq vehicle.categoryId}">
                                                <c:set var="vehicleCategoryName" value="${category.categoryName}" />
                                            </c:if>
                                        </c:forEach>
                                        <c:choose>
                                            <c:when test="${not empty vehicleCategoryName}">
                                                <c:out value="${vehicleCategoryName}" />
                                            </c:when>
                                            <c:otherwise>-</c:otherwise>
                                        </c:choose>
                                    </td>
                                    <td>
                                        <c:set var="vehicleStatusName" value="" />
                                        <c:forEach var="status" items="${statuses}">
                                            <c:if test="${status.vehicleStatusId eq vehicle.vehicleStatusId}">
                                                <c:set var="vehicleStatusName" value="${status.statusName}" />
                                            </c:if>
                                        </c:forEach>
                                        <c:choose>
                                            <c:when test="${not empty vehicleStatusName}">
                                                <c:out value="${vehicleStatusName}" />
                                            </c:when>
                                            <c:otherwise>-</c:otherwise>
                                        </c:choose>
                                    </td>
                                    <td>
                                        <c:set var="vehicleHeadquartersName" value="" />
                                        <c:forEach var="hq" items="${headquarters}">
                                            <c:if test="${hq.id eq vehicle.currentHeadquartersId}">
                                                <c:set var="vehicleHeadquartersName" value="${hq.name}" />
                                            </c:if>
                                        </c:forEach>
                                        <c:choose>
                                            <c:when test="${not empty vehicleHeadquartersName}">
                                                <c:out value="${vehicleHeadquartersName}" />
                                            </c:when>
                                            <c:otherwise>-</c:otherwise>
                                        </c:choose>
                                    </td>
                                    <td>
                                        <c:choose>
                                            <c:when test="${not empty vehicle.dailyPrice}">
                                            <fmt:formatNumber value="${vehicle.dailyPrice}" type="number" maxFractionDigits="2"
                                                    minFractionDigits="2" /> ${vehicleCurrencySymbol}
                                            </c:when>
                                            <c:otherwise>-</c:otherwise>
                                        </c:choose>
                                    </td>
                                    <td class="text-end">
                                        <a class="btn btn-sm btn-outline-primary me-2"
                                            href="${ctx}/private/VehicleServlet?action=updateVehicle&amp;vehicleId=${vehicle.vehicleId}">
                                            <fmt:message key="actions.edit" />
                                        </a>
                                        <form method="post" action="${ctx}/private/VehicleServlet" class="d-inline">
                                            <input type="hidden" name="action" value="deleteVehicle" />
                                            <input type="hidden" name="vehicleId" value="${vehicle.vehicleId}" />
                                            <button type="submit" class="btn btn-sm btn-outline-danger">
                                                <fmt:message key="actions.delete" />
                                            </button>
                                        </form>
                                    </td>
                                </tr>
                            </c:forEach>
                        </c:when>
                        <c:otherwise>
                            <tr>
                                <td colspan="7" class="empty-cell"><fmt:message key="vehicle.list.empty" /></td>
                            </tr>
                        </c:otherwise>
                    </c:choose>
                </tbody>
            </table>
            <c:if test="${totalPages >= 1}">
                <c:set var="paginationCurrentPage" value="${currentPage}" scope="request" />
                <c:set var="paginationTotalPages" value="${totalPages}" scope="request" />
                <jsp:include page="/public/pagination.jsp" />
                <c:remove var="paginationCurrentPage" scope="request" />
                <c:remove var="paginationTotalPages" scope="request" />
            </c:if>
        </div>
    </div>
</section>
<%@ include file="/common/footer.jsp" %>
