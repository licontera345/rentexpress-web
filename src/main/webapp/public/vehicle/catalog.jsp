<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ include file="/common/header.jsp" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<c:set var="params" value="${vehicleParamNames}" />
<c:set var="sortValues" value="${vehicleSortValues}" />
<c:set var="filters" value="${vehicleFilters}" />
<c:set var="categories" value="${vehicleCategories}" />
<c:set var="categoryNames" value="${vehicleCategoryNames}" />
<c:set var="vehicles" value="${vehicles}" />
<c:set var="filterErrors" value="${vehicleFilterErrors}" />
<c:set var="total" value="${totalVehicles}" />
<c:set var="headquarters" value="${vehicleHeadquarters}" />
<c:set var="headquartersNames" value="${vehicleHeadquartersNames}" />
<c:set var="statuses" value="${vehicleStatuses}" />
<c:set var="pageSizes" value="${vehiclePageSizes}" />
<c:set var="results" value="${vehicleResults}" />
<c:set var="resultsFromRow" value="${empty vehicleResultsFromRow ? 0 : vehicleResultsFromRow}" />
<c:set var="resultsToRow" value="${empty vehicleResultsToRow ? 0 : vehicleResultsToRow}" />
<fmt:message key="vehicle.catalog.filter.search.placeholder" var="catalogSearchPlaceholder" />
<fmt:message key="vehicle.catalog.filter.brand.placeholder" var="catalogBrandPlaceholder" />
<fmt:message key="vehicle.catalog.filter.model.placeholder" var="catalogModelPlaceholder" />
<fmt:message key="vehicle.catalog.pagination.aria" var="catalogPaginationAria" />
<fmt:message key="vehicle.catalog.pagination.previous" var="catalogPaginationPrevious" />
<fmt:message key="vehicle.catalog.pagination.next" var="catalogPaginationNext" />
<c:set var="selectedPageSize"
        value="${results != null && results.pageSize != null ? results.pageSize : pageSizes[0]}" />

<div class="row g-4">
    <div class="col-lg-3">
        <div class="card shadow-soft catalog-filter-card">
            <div class="card-body">
                <h2 class="h5 fw-semibold mb-3"><fmt:message key="vehicle.catalog.filter.title" /></h2>
                <form method="get" action="${ctx}/public/vehicles" class="catalog-form">
                    <div class="mb-3">
                        <label for="search" class="form-label"><fmt:message key="vehicle.catalog.filter.search" /></label>
                        <input type="text" class="form-control" id="search" name="${params.search}"
                               placeholder="${catalogSearchPlaceholder}"
                               value="${filters[params.search]}">
                    </div>
                    <div class="row g-2 mb-3">
                        <div class="col-12 col-xl-6">
                            <label for="brand" class="form-label"><fmt:message key="vehicle.catalog.filter.brand" /></label>
                            <input type="text" class="form-control" id="brand"
                                   name="${params.brand}"
                                   placeholder="${catalogBrandPlaceholder}"
                                   value="${filters[params.brand]}">
                        </div>
                        <div class="col-12 col-xl-6">
                            <label for="model" class="form-label"><fmt:message key="vehicle.catalog.filter.model" /></label>
                            <input type="text" class="form-control" id="model"
                                   name="${params.model}"
                                   placeholder="${catalogModelPlaceholder}"
                                   value="${filters[params.model]}">
                        </div>
                    </div>
                    <div class="mb-3">
                        <label for="category" class="form-label"><fmt:message key="vehicle.catalog.filter.category" /></label>
                        <select class="form-select" id="category" name="${params.category}">
                            <option value=""><fmt:message key="vehicle.catalog.filter.category.all" /></option>
                            <c:forEach var="category" items="${categories}">
                                <option value="${category.categoryId}"
                                        ${category.categoryId eq filters[params.category] ? 'selected' : ''}>
                                    ${category.categoryName}
                                </option>
                            </c:forEach>
                        </select>
                    </div>
                    <div class="mb-3">
                        <label for="headquarters" class="form-label"><fmt:message key="vehicle.catalog.filter.headquarters" /></label>
                        <select class="form-select" id="headquarters" name="${params.headquarters}">
                            <option value=""><fmt:message key="vehicle.catalog.filter.headquarters.all" /></option>
                            <c:forEach var="hq" items="${headquarters}">
                                <option value="${hq.id}"
                                        ${hq.id eq filters[params.headquarters] ? 'selected' : ''}>
                                    <c:out value="${hq.name}" />
                                    <c:if test="${hq.city != null || hq.province != null}">
                                        &nbsp;·&nbsp;
                                        <c:if test="${hq.city != null}">
                                            <c:out value="${hq.city.cityName}" />
                                        </c:if>
                                        <c:if test="${hq.city != null && hq.province != null}">, </c:if>
                                        <c:if test="${hq.province != null}">
                                            <c:out value="${hq.province.provinceName}" />
                                        </c:if>
                                    </c:if>
                                </option>
                            </c:forEach>
                        </select>
                    </div>
                    <div class="row g-2 mb-3">
                        <div class="col">
                            <label for="minPrice" class="form-label"><fmt:message key="vehicle.catalog.filter.minPrice" /></label>
                            <input type="number" step="0.01" min="0" class="form-control" id="minPrice"
                                   name="${params.minPrice}"
                                   value="${filters[params.minPrice]}">
                        </div>
                        <div class="col">
                            <label for="maxPrice" class="form-label"><fmt:message key="vehicle.catalog.filter.maxPrice" /></label>
                            <input type="number" step="0.01" min="0" class="form-control" id="maxPrice"
                                   name="${params.maxPrice}"
                                   value="${filters[params.maxPrice]}">
                        </div>
                    </div>
                    <div class="row g-2 mb-3">
                        <div class="col">
                            <label for="minYear" class="form-label"><fmt:message key="vehicle.catalog.filter.minYear" /></label>
                            <input type="number" min="1900" max="2100" class="form-control" id="minYear"
                                   name="${params.minYear}"
                                   value="${filters[params.minYear]}">
                        </div>
                        <div class="col">
                            <label for="maxYear" class="form-label"><fmt:message key="vehicle.catalog.filter.maxYear" /></label>
                            <input type="number" min="1900" max="2100" class="form-control" id="maxYear"
                                   name="${params.maxYear}"
                                   value="${filters[params.maxYear]}">
                        </div>
                    </div>
                    <div class="mb-3">
                        <label for="status" class="form-label"><fmt:message key="vehicle.catalog.filter.status" /></label>
                        <select class="form-select" id="status" name="${params.status}">
                            <option value=""><fmt:message key="vehicle.catalog.filter.status.all" /></option>
                            <c:forEach var="status" items="${statuses}">
                                <option value="${status.vehicleStatusId}"
                                        ${status.vehicleStatusId eq filters[params.status] ? 'selected' : ''}>
                                    ${status.statusName}
                                </option>
                            </c:forEach>
                        </select>
                    </div>
                    <div class="mb-4">
                        <label for="sort" class="form-label"><fmt:message key="vehicle.catalog.filter.sort" /></label>
                        <select class="form-select" id="sort" name="${params.sort}">
                            <option value="${sortValues.priceAsc}"
                                    ${filters[params.sort] == sortValues.priceAsc ? 'selected' : ''}>
                                <fmt:message key="vehicle.catalog.filter.sort.priceAsc" />
                            </option>
                            <option value="${sortValues.priceDesc}"
                                    ${filters[params.sort] == sortValues.priceDesc ? 'selected' : ''}>
                                <fmt:message key="vehicle.catalog.filter.sort.priceDesc" />
                            </option>
                            <option value="${sortValues.yearDesc}"
                                    ${filters[params.sort] == sortValues.yearDesc ? 'selected' : ''}>
                                <fmt:message key="vehicle.catalog.filter.sort.yearDesc" />
                            </option>
                        </select>
                    </div>
                    <div class="form-check form-switch mb-4">
                        <input class="form-check-input" type="checkbox" id="onlyAvailable"
                               name="${params.onlyAvailable}" value="true"
                               ${filters[params.onlyAvailable] == 'true' ? 'checked' : ''} />
                        <label class="form-check-label" for="onlyAvailable"><fmt:message key="vehicle.catalog.filter.onlyAvailable" /></label>
                    </div>
                    <div class="mb-4">
                        <label for="pageSize" class="form-label"><fmt:message key="vehicle.catalog.filter.pageSize" /></label>
                        <select class="form-select" id="pageSize" name="${params.pageSize}">
                            <c:forEach var="size" items="${pageSizes}">
                                <option value="${size}" ${size == selectedPageSize ? 'selected' : ''}>${size}</option>
                            </c:forEach>
                        </select>
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
                        <fmt:param value="${total}" />
                    </fmt:message>
                    <c:if test="${resultsFromRow > 0 and resultsToRow > 0}">
                        <fmt:message key="vehicle.catalog.header.summaryRange">
                            <fmt:param value="${resultsFromRow}" />
                            <fmt:param value="${resultsToRow}" />
                        </fmt:message>
                    </c:if>
                </p>
            </div>
            <a class="btn btn-outline-brand" href="${ctx}/app/auth/login">
                <i class="bi bi-box-arrow-in-right me-2"></i><fmt:message key="vehicle.catalog.header.loginCta" />
            </a>
        </div>

        <c:if test="${not empty filterErrors}">
            <div class="alert alert-warning shadow-soft">
                <ul class="mb-0">
                    <c:forEach var="error" items="${filterErrors}">
                        <li>${error}</li>
                    </c:forEach>
                </ul>
            </div>
        </c:if>

        <c:choose>
            <c:when test="${empty vehicles}">
                <div class="alert alert-info shadow-soft">
                    <fmt:message key="vehicle.catalog.alert.noResults" />
                </div>
            </c:when>
            <c:otherwise>
                <div class="row g-4">
                    <c:forEach var="vehicle" items="${vehicles}">
                        <div class="col-md-6 col-xl-4">
                            <div class="vehicle-card h-100 d-flex flex-column">
                                <div class="vehicle-card-body flex-grow-1">
                                    <span class="vehicle-category">${categoryNames[vehicle.categoryId]}</span>
                                    <h2 class="h5 fw-bold mt-2">${vehicle.brand} ${vehicle.model}</h2>
                                    <ul class="vehicle-features list-unstyled">
                                        <li><i class="bi bi-calendar3"></i>
                                            <fmt:message key="vehicle.catalog.feature.year">
                                                <fmt:param value="${vehicle.manufactureYear}" />
                                            </fmt:message>
                                        </li>
                                        <li><i class="bi bi-speedometer2"></i>
                                            <fmt:message key="vehicle.catalog.feature.mileage">
                                                <fmt:param value="${vehicle.currentMileage}" />
                                            </fmt:message>
                                        </li>
                                        <li>
                                            <i class="bi bi-geo-alt"></i>
                                            <c:choose>
                                                <c:when test="${vehicle.currentHeadquarters != null}">
                                                    <fmt:message key="vehicle.catalog.feature.currentHeadquarters.label" />&nbsp;
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
                                                <c:when test="${vehicle.currentHeadquartersId != null}">
                                                    <c:choose>
                                                        <c:when test="${not empty headquartersNames[vehicle.currentHeadquartersId]}">
                                                            <fmt:message key="vehicle.catalog.feature.currentHeadquarters.label" />&nbsp;${headquartersNames[vehicle.currentHeadquartersId]}
                                                        </c:when>
                                                        <c:otherwise>
                                                            <fmt:message key="vehicle.catalog.feature.currentHeadquarters.label" />&nbsp;#${vehicle.currentHeadquartersId}
                                                        </c:otherwise>
                                                    </c:choose>
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
                                            <fmt:formatNumber value="${vehicle.dailyPrice}" type="currency" currencySymbol="€"/>
                                            <span class="text-muted fs-6"><fmt:message key="vehicle.catalog.pricing.perDay" /></span>
                                        </div>
                                    </div>
                                    <c:url var="detailUrl" value="/public/vehicles/detail">
                                        <c:param name="${params.vehicleId}" value="${vehicle.vehicleId}" />
                                        <c:if test="${not empty filters[params.headquarters]}">
                                            <c:param name="${params.headquarters}"
                                                     value="${filters[params.headquarters]}" />
                                        </c:if>
                                        <c:if test="${not empty filters[params.pickupDate]}">
                                            <c:param name="${params.pickupDate}"
                                                     value="${filters[params.pickupDate]}" />
                                        </c:if>
                                        <c:if test="${not empty filters[params.returnDate]}">
                                            <c:param name="${params.returnDate}"
                                                     value="${filters[params.returnDate]}" />
                                        </c:if>
                                    </c:url>
                                    <a class="btn btn-brand" href="${detailUrl}">
                                        <fmt:message key="vehicle.catalog.button.viewDetails" />
                                    </a>
                                </div>
                            </div>
                        </div>
                    </c:forEach>
                </div>
                <c:if test="${results.totalPages > 1}">
                    <nav class="mt-4" aria-label="${catalogPaginationAria}">
                        <ul class="pagination justify-content-center">
                            <li class="page-item ${!results.hasPrev ? 'disabled' : ''}">
                                <c:url var="prevUrl" value="/public/vehicles">
                                    <c:param name="${params.page}" value="${results.page - 1}" />
                                    <c:param name="${params.pageSize}" value="${results.pageSize}" />
                                    <c:param name="${params.sort}" value="${filters[params.sort]}" />
                                    <c:forEach var="entry" items="${filters}">
                                        <c:if test="${entry.key ne params.page && entry.key ne params.pageSize && entry.key ne params.sort && entry.value ne null && entry.value ne ''}">
                                            <c:param name="${entry.key}" value="${entry.value}" />
                                        </c:if>
                                    </c:forEach>
                                </c:url>
                                <c:set var="prevHref" value="${ctx}${prevUrl}" />
                                <c:if test="${!results.hasPrev}">
                                    <c:set var="prevHref" value="#" />
                                </c:if>
                                <a class="page-link" href="${prevHref}" aria-label="${catalogPaginationPrevious}">
                                    <span aria-hidden="true">&laquo;</span>
                                </a>
                            </li>
                            <c:forEach begin="1" end="${results.totalPages}" var="pageNumber">
                                <c:url var="pageUrl" value="/public/vehicles">
                                    <c:param name="${params.page}" value="${pageNumber}" />
                                    <c:param name="${params.pageSize}" value="${results.pageSize}" />
                                    <c:param name="${params.sort}" value="${filters[params.sort]}" />
                                    <c:forEach var="entry" items="${filters}">
                                        <c:if test="${entry.key ne params.page && entry.key ne params.pageSize && entry.key ne params.sort && entry.value ne null && entry.value ne ''}">
                                            <c:param name="${entry.key}" value="${entry.value}" />
                                        </c:if>
                                    </c:forEach>
                                </c:url>
                                <li class="page-item ${pageNumber == results.page ? 'active' : ''}">
                                    <a class="page-link" href="${ctx}${pageUrl}">${pageNumber}</a>
                                </li>
                            </c:forEach>
                            <li class="page-item ${!results.hasNext ? 'disabled' : ''}">
                                <c:url var="nextUrl" value="/public/vehicles">
                                    <c:param name="${params.page}" value="${results.page + 1}" />
                                    <c:param name="${params.pageSize}" value="${results.pageSize}" />
                                    <c:param name="${params.sort}" value="${filters[params.sort]}" />
                                    <c:forEach var="entry" items="${filters}">
                                        <c:if test="${entry.key ne params.page && entry.key ne params.pageSize && entry.key ne params.sort && entry.value ne null && entry.value ne ''}">
                                            <c:param name="${entry.key}" value="${entry.value}" />
                                        </c:if>
                                    </c:forEach>
                                </c:url>
                                <c:set var="nextHref" value="${ctx}${nextUrl}" />
                                <c:if test="${!results.hasNext}">
                                    <c:set var="nextHref" value="#" />
                                </c:if>
                                <a class="page-link" href="${nextHref}" aria-label="${catalogPaginationNext}">
                                    <span aria-hidden="true">&raquo;</span>
                                </a>
                            </li>
                        </ul>
                    </nav>
                </c:if>
            </c:otherwise>
        </c:choose>
    </div>
</div>

<%@ include file="/common/footer.jsp" %>
