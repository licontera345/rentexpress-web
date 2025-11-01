<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ include file="/common/header.jsp" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<c:set var="vehicles" value="${vehicles}" />
<c:set var="filters" value="${vehicleFilters}" />
<c:set var="searchValue" value="${filters.search}" />
<c:set var="selectedCategoryId" value="${filters.categoryId}" />
<c:set var="selectedStatus" value="${filters.status}" />
<c:set var="selectedMinPrice" value="${filters.priceMin}" />
<c:set var="selectedMaxPrice" value="${filters.priceMax}" />
<c:set var="onlyAvailableFlag" value="${filters.availableOnly}" />
<c:set var="categories" value="${vehicleCategories}" />
<c:set var="statuses" value="${vehicleStatuses}" />
<c:set var="totalResults" value="${fn:length(vehicles)}" />

<fmt:message key="vehicle.catalog.filter.search.placeholder" var="searchPlaceholder" />

<div class="row g-4">
    <div class="col-lg-3">
        <div class="card shadow-soft catalog-filter-card">
            <div class="card-body">
                <h2 class="h5 fw-semibold mb-3"><fmt:message key="vehicle.catalog.filter.title" /></h2>
                <form method="get" action="${ctx}/public/vehicles" class="catalog-form">
                    <div class="mb-3">
                        <label for="search" class="form-label"><fmt:message key="vehicle.catalog.filter.search" /></label>
                        <input type="text" class="form-control" id="search" name="search"
                               placeholder="${searchPlaceholder}" value="${searchValue}">
                    </div>
                    <div class="mb-3">
                        <label for="category" class="form-label"><fmt:message key="vehicle.catalog.filter.category" /></label>
                        <select class="form-select" id="category" name="categoryId">
                            <option value=""><fmt:message key="vehicle.catalog.filter.category.all" /></option>
                            <c:forEach var="category" items="${categories}">
                                <option value="${category.categoryId}" ${category.categoryId eq selectedCategoryId ? 'selected' : ''}>
                                    ${category.categoryName}
                                </option>
                            </c:forEach>
                        </select>
                    </div>
                    <div class="mb-3">
                        <label for="status" class="form-label"><fmt:message key="vehicle.catalog.filter.status" /></label>
                        <select class="form-select" id="status" name="status">
                            <option value=""><fmt:message key="vehicle.catalog.filter.status.all" /></option>
                            <c:forEach var="state" items="${statuses}">
                                <option value="${state.vehicleStatusId}" ${state.vehicleStatusId eq selectedStatus ? 'selected' : ''}>
                                    ${state.statusName}
                                </option>
                            </c:forEach>
                        </select>
                    </div>
                    <div class="row g-2 mb-3">
                        <div class="col">
                            <label for="minPrice" class="form-label"><fmt:message key="vehicle.catalog.filter.minPrice" /></label>
                            <input type="number" step="0.01" min="0" class="form-control" id="minPrice"
                                   name="priceMin" value="${selectedMinPrice}">
                        </div>
                        <div class="col">
                            <label for="maxPrice" class="form-label"><fmt:message key="vehicle.catalog.filter.maxPrice" /></label>
                            <input type="number" step="0.01" min="0" class="form-control" id="maxPrice"
                                   name="priceMax" value="${selectedMaxPrice}">
                        </div>
                    </div>
                    <div class="form-check form-switch mb-4">
                        <input class="form-check-input" type="checkbox" id="onlyAvailable" name="availableOnly"
                               value="true" ${onlyAvailableFlag ? 'checked' : ''}>
                        <label class="form-check-label" for="onlyAvailable"><fmt:message key="vehicle.catalog.filter.onlyAvailable" /></label>
                    </div>
                    <div class="d-grid gap-2">
                        <button type="submit" class="btn btn-brand"><fmt:message key="vehicle.catalog.filter.apply" /></button>
                        <a class="btn btn-outline-brand" href="${ctx}/public/vehicles"><fmt:message key="vehicle.catalog.filter.clear" /></a>
                    </div>
                </form>
            </div>
        </div>
    </div>
    <div class="col-lg-9">
        <div class="d-flex justify-content-between align-items-center flex-wrap gap-2 mb-3">
            <div>
                <h1 class="h3 fw-semibold mb-0"><fmt:message key="vehicle.catalog.header.title" /></h1>
                <p class="text-muted mb-0">
                    <fmt:message key="vehicle.catalog.header.summary">
                        <fmt:param value="${totalResults}" />
                    </fmt:message>
                </p>
            </div>
            <a class="btn btn-outline-brand" href="${ctx}/login">
                <i class="bi bi-box-arrow-in-right me-2"></i><fmt:message key="vehicle.catalog.header.loginCta" />
            </a>
        </div>

        <c:if test="${not empty error}">
            <div class="alert alert-warning shadow-soft">
                <ul class="mb-0">
                    <c:forEach var="message" items="${error}">
                        <li>${message}</li>
                    </c:forEach>
                </ul>
            </div>
        </c:if>

        <c:choose>
            <c:when test="${empty vehicles}">
                <div class="alert alert-info shadow-soft">
                    <fmt:message key="catalog.noResults" />
                </div>
            </c:when>
            <c:otherwise>
                <div class="row g-4">
                    <c:forEach var="vehicle" items="${vehicles}">
                        <div class="col-md-6 col-xl-4">
                            <div class="vehicle-card h-100 d-flex flex-column">
                                <div class="vehicle-card-body flex-grow-1">
                                    <c:set var="vehicleCategory" value="" />
                                    <c:forEach var="category" items="${categories}">
                                        <c:if test="${category.categoryId eq vehicle.categoryId}">
                                            <c:set var="vehicleCategory" value="${category.categoryName}" />
                                        </c:if>
                                    </c:forEach>
                                    <c:if test="${not empty vehicleCategory}">
                                        <span class="vehicle-category">${vehicleCategory}</span>
                                    </c:if>
                                    <h2 class="h5 fw-bold mt-2">${vehicle.brand} ${vehicle.model}</h2>
                                    <ul class="vehicle-features list-unstyled">
                                        <li>
                                            <i class="bi bi-calendar3"></i>
                                            <fmt:message key="vehicle.catalog.feature.year">
                                                <fmt:param value="${vehicle.manufactureYear}" />
                                            </fmt:message>
                                        </li>
                                        <li>
                                            <i class="bi bi-speedometer2"></i>
                                            <fmt:message key="vehicle.catalog.feature.mileage">
                                                <fmt:param value="${vehicle.currentMileage}" />
                                            </fmt:message>
                                        </li>
                                        <li>
                                            <i class="bi bi-geo-alt"></i>
                                            <c:choose>
                                                <c:when test="${vehicle.currentHeadquarters != null}">
                                                    <fmt:message key="vehicle.catalog.feature.currentHeadquarters.label" />
                                                    <span class="fw-semibold">
                                                        <c:out value="${vehicle.currentHeadquarters.name}" />
                                                        <c:if test="${vehicle.currentHeadquarters.city != null}">
                                                            &nbsp;·&nbsp;<c:out value="${vehicle.currentHeadquarters.city.cityName}" />
                                                        </c:if>
                                                        <c:if test="${vehicle.currentHeadquarters.city != null && vehicle.currentHeadquarters.province != null}">, </c:if>
                                                        <c:if test="${vehicle.currentHeadquarters.province != null}">
                                                            <c:out value="${vehicle.currentHeadquarters.province.provinceName}" />
                                                        </c:if>
                                                    </span>
                                                </c:when>
                                                <c:otherwise>
                                                    <fmt:message key="vehicle.catalog.feature.currentHeadquarters.unassigned" />
                                                </c:otherwise>
                                            </c:choose>
                                        </li>
                                    </ul>
                                </div>
                                <div class="vehicle-card-footer">
                                    <div>
                                        <small class="text-muted"><fmt:message key="vehicle.catalog.pricing.dailyRate" /></small>
                                        <div class="h5 mb-0">
                                            <fmt:formatNumber value="${vehicle.dailyPrice}" type="currency" currencySymbol="€" />
                                            <span class="text-muted fs-6"><fmt:message key="vehicle.catalog.pricing.perDay" /></span>
                                        </div>
                                    </div>
                                    <c:url var="detailUrl" value="/public/vehicles/detail">
                                        <c:param name="vehicleId" value="${vehicle.vehicleId}" />
                                    </c:url>
                                    <a class="btn btn-brand" href="${ctx}${detailUrl}">
                                        <fmt:message key="vehicle.catalog.button.viewDetails" />
                                    </a>
                                </div>
                            </div>
                        </div>
                    </c:forEach>
                </div>
            </c:otherwise>
        </c:choose>
    </div>
</div>

<%@ include file="/common/footer.jsp" %>
