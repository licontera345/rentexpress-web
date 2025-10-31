<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ include file="/common/header.jsp" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<c:set var="items" value="${items}" />
<c:set var="total" value="${empty total ? 0 : total}" />
<c:set var="currentPage" value="${empty page ? 1 : page}" />
<c:set var="pageSize" value="${empty size ? 20 : size}" />
<c:set var="pages" value="${empty totalPages ? 1 : totalPages}" />
<c:set var="pageSizes" value="${pageSizes}" />
<c:set var="searchValue" value="${search}" />
<c:set var="selectedCategory" value="${categoryId}" />
<c:set var="selectedStatus" value="${statusId}" />
<c:set var="selectedMinPrice" value="${minPrice}" />
<c:set var="selectedMaxPrice" value="${maxPrice}" />
<c:set var="onlyAvailableFlag" value="${onlyAvailable}" />

<fmt:message key="vehicle.catalog.filter.search.placeholder" var="searchPlaceholder" />
<fmt:message key="vehicle.catalog.pagination.aria" var="paginationAria" />
<fmt:message key="vehicle.catalog.pagination.previous" var="paginationPrevious" />
<fmt:message key="vehicle.catalog.pagination.next" var="paginationNext" />

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
                        <select class="form-select" id="category" name="category">
                            <option value=""><fmt:message key="vehicle.catalog.filter.category.all" /></option>
                            <c:forEach var="category" items="${categories}">
                                <option value="${category.categoryId}"
                                        ${category.categoryId eq selectedCategory ? 'selected' : ''}>
                                    ${category.categoryName}
                                </option>
                            </c:forEach>
                        </select>
                    </div>
                    <div class="mb-3">
                        <label for="status" class="form-label"><fmt:message key="vehicle.catalog.filter.status" /></label>
                        <select class="form-select" id="status" name="status">
                            <option value=""><fmt:message key="vehicle.catalog.filter.status.all" /></option>
                            <c:forEach var="status" items="${statuses}">
                                <option value="${status.vehicleStatusId}"
                                        ${status.vehicleStatusId eq selectedStatus ? 'selected' : ''}>
                                    ${status.statusName}
                                </option>
                            </c:forEach>
                        </select>
                    </div>
                    <div class="row g-2 mb-3">
                        <div class="col">
                            <label for="minPrice" class="form-label"><fmt:message key="vehicle.catalog.filter.minPrice" /></label>
                            <input type="number" step="0.01" min="0" class="form-control" id="minPrice"
                                   name="minPrice" value="${selectedMinPrice}">
                        </div>
                        <div class="col">
                            <label for="maxPrice" class="form-label"><fmt:message key="vehicle.catalog.filter.maxPrice" /></label>
                            <input type="number" step="0.01" min="0" class="form-control" id="maxPrice"
                                   name="maxPrice" value="${selectedMaxPrice}">
                        </div>
                    </div>
                    <div class="form-check form-switch mb-3">
                        <input class="form-check-input" type="checkbox" id="onlyAvailable" name="onlyAvailable"
                               value="true" ${onlyAvailableFlag ? 'checked' : ''}>
                        <label class="form-check-label" for="onlyAvailable"><fmt:message key="vehicle.catalog.filter.onlyAvailable" /></label>
                    </div>
                    <div class="mb-4">
                        <label for="pageSize" class="form-label"><fmt:message key="vehicle.catalog.filter.pageSize" /></label>
                        <select class="form-select" id="pageSize" name="size">
                            <c:forEach var="option" items="${pageSizes}">
                                <option value="${option}" ${option == pageSize ? 'selected' : ''}>${option}</option>
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
                    <c:if test="${from > 0 and to > 0}">
                        <fmt:message key="vehicle.catalog.header.summaryRange">
                            <fmt:param value="${from}" />
                            <fmt:param value="${to}" />
                        </fmt:message>
                    </c:if>
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
            <c:when test="${empty items}">
                <div class="alert alert-info shadow-soft">
                    <fmt:message key="vehicle.catalog.alert.noResults" />
                </div>
            </c:when>
            <c:otherwise>
                <div class="row g-4">
                    <c:forEach var="vehicle" items="${items}">
                        <div class="col-md-6 col-xl-4">
                            <div class="vehicle-card h-100 d-flex flex-column">
                                <div class="vehicle-card-body flex-grow-1">
                                    <c:set var="vehicleCategory" value="" />
                                    <c:forEach var="category" items="${categories}">
                                        <c:if test="${category.categoryId eq vehicle.categoryId}">
                                            <c:set var="vehicleCategory" value="${category.categoryName}" />
                                        </c:if>
                                    </c:forEach>
                                    <span class="vehicle-category">${vehicleCategory}</span>
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

                <c:if test="${pages > 1}">
                    <nav class="mt-4" aria-label="${paginationAria}">
                        <ul class="pagination justify-content-center">
                            <c:set var="hasPrevious" value="${currentPage > 1}" />
                            <li class="page-item ${!hasPrevious ? 'disabled' : ''}">
                                <c:url var="prevUrl" value="/public/vehicles">
                                    <c:param name="page" value="${currentPage - 1}" />
                                    <c:param name="size" value="${pageSize}" />
                                    <c:if test="${not empty searchValue}"><c:param name="search" value="${searchValue}" /></c:if>
                                    <c:if test="${not empty selectedCategory}"><c:param name="category" value="${selectedCategory}" /></c:if>
                                    <c:if test="${not empty selectedStatus}"><c:param name="status" value="${selectedStatus}" /></c:if>
                                    <c:if test="${not empty selectedMinPrice}"><c:param name="minPrice" value="${selectedMinPrice}" /></c:if>
                                    <c:if test="${not empty selectedMaxPrice}"><c:param name="maxPrice" value="${selectedMaxPrice}" /></c:if>
                                    <c:if test="${onlyAvailableFlag}"><c:param name="onlyAvailable" value="true" /></c:if>
                                </c:url>
                                <a class="page-link" href="${hasPrevious ? ctx.concat(prevUrl) : '#'}" aria-label="${paginationPrevious}">
                                    <span aria-hidden="true">&laquo;</span>
                                </a>
                            </li>
                            <c:forEach begin="1" end="${pages}" var="pageNumber">
                                <c:url var="pageUrl" value="/public/vehicles">
                                    <c:param name="page" value="${pageNumber}" />
                                    <c:param name="size" value="${pageSize}" />
                                    <c:if test="${not empty searchValue}"><c:param name="search" value="${searchValue}" /></c:if>
                                    <c:if test="${not empty selectedCategory}"><c:param name="category" value="${selectedCategory}" /></c:if>
                                    <c:if test="${not empty selectedStatus}"><c:param name="status" value="${selectedStatus}" /></c:if>
                                    <c:if test="${not empty selectedMinPrice}"><c:param name="minPrice" value="${selectedMinPrice}" /></c:if>
                                    <c:if test="${not empty selectedMaxPrice}"><c:param name="maxPrice" value="${selectedMaxPrice}" /></c:if>
                                    <c:if test="${onlyAvailableFlag}"><c:param name="onlyAvailable" value="true" /></c:if>
                                </c:url>
                                <li class="page-item ${pageNumber == currentPage ? 'active' : ''}">
                                    <a class="page-link" href="${ctx}${pageUrl}">${pageNumber}</a>
                                </li>
                            </c:forEach>
                            <c:set var="hasNext" value="${currentPage < pages}" />
                            <li class="page-item ${!hasNext ? 'disabled' : ''}">
                                <c:url var="nextUrl" value="/public/vehicles">
                                    <c:param name="page" value="${currentPage + 1}" />
                                    <c:param name="size" value="${pageSize}" />
                                    <c:if test="${not empty searchValue}"><c:param name="search" value="${searchValue}" /></c:if>
                                    <c:if test="${not empty selectedCategory}"><c:param name="category" value="${selectedCategory}" /></c:if>
                                    <c:if test="${not empty selectedStatus}"><c:param name="status" value="${selectedStatus}" /></c:if>
                                    <c:if test="${not empty selectedMinPrice}"><c:param name="minPrice" value="${selectedMinPrice}" /></c:if>
                                    <c:if test="${not empty selectedMaxPrice}"><c:param name="maxPrice" value="${selectedMaxPrice}" /></c:if>
                                    <c:if test="${onlyAvailableFlag}"><c:param name="onlyAvailable" value="true" /></c:if>
                                </c:url>
                                <a class="page-link" href="${hasNext ? ctx.concat(nextUrl) : '#'}" aria-label="${paginationNext}">
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
