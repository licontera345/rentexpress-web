<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<fmt:setLocale value="${sessionScope.appLocale != null ? sessionScope.appLocale : pageContext.request.locale}" scope="session" />
<fmt:setBundle basename="i18n.Messages" scope="session" />
<%@ include file="/common/header.jsp" %>
<c:set var="criteria" value="${requestScope.criteria}" />
<c:set var="results" value="${requestScope.results}" />
<c:set var="vehicles"
       value="${not empty requestScope.vehicles ? requestScope.vehicles : (not empty results ? results.results : null)}" />
<c:set var="totalResults"
       value="${not empty results and not empty results.totalRecords ? results.totalRecords : 0}" />
<c:set var="currentPage"
       value="${not empty results and not empty results.pageNumber ? results.pageNumber : 1}" />
<c:set var="totalPages"
       value="${not empty results and not empty results.totalPages ? results.totalPages : 1}" />
<c:set var="pageSize"
       value="${not empty results and not empty results.pageSize ? results.pageSize : 9}" />
<c:set var="categories" value="${requestScope.categories}" />
<c:set var="headquarters" value="${requestScope.headquarters}" />
<c:set var="selectedCategoryId" value="${not empty criteria ? criteria.categoryId : null}" />
<c:set var="selectedHeadquartersId"
       value="${not empty criteria ? criteria.currentHeadquartersId : null}" />
<c:set var="selectedMinPrice" value="${not empty criteria ? criteria.dailyPriceMin : null}" />
<c:set var="selectedMaxPrice" value="${not empty criteria ? criteria.dailyPriceMax : null}" />
<c:set var="searchQuery" value="${not empty criteria ? criteria.brand : null}" />
<c:set var="selectedModel" value="${not empty criteria ? criteria.model : null}" />
<c:set var="selectedYearFrom"
       value="${not empty criteria ? criteria.manufactureYearFrom : null}" />
<c:set var="selectedYearTo" value="${not empty criteria ? criteria.manufactureYearTo : null}" />
<c:set var="selectedMileageMin"
       value="${not empty criteria ? criteria.currentMileageMin : null}" />
<c:set var="selectedMileageMax"
       value="${not empty criteria ? criteria.currentMileageMax : null}" />
<c:set var="cartVehicle"
       value="${not empty requestScope.cartVehicle ? requestScope.cartVehicle : sessionScope.reservationCartVehicle}"
       scope="request" />
<fmt:message var="searchPlaceholder" key="vehicle.catalog.filter.search.placeholder" />

<section class="catalog-section py-6">
    <div class="container">
        <header class="catalog-header">
            <span class="section-eyebrow"><fmt:message key="vehicle.catalog.header.title" /></span>
            <div class="catalog-heading">
                <h2 class="section-heading"><fmt:message key="vehicle.public.catalog.pageTitle" /></h2>
                <p class="catalog-summary">
                    <fmt:message key="vehicle.catalog.header.summary">
                        <fmt:param value="${totalResults}" />
                    </fmt:message>
                </p>
            </div>
        </header>

        <div class="catalog-layout">
            <aside class="catalog-sidebar">
                <form method="get" action="${ctx}/public/VehicleServlet" class="catalog-filter-card card-common shadow-soft">
                    <input type="hidden" name="action" value="filterVehicles" />
                    <h3 class="filter-title"><fmt:message key="vehicle.catalog.filter.title" /></h3>
                    <div class="form-grid">
                        <div class="form-group">
                            <label for="search"><fmt:message key="vehicle.catalog.filter.search" /></label>
                            <input type="text" id="search" name="search" class="form-control" list="brandOptions"
                                value="${fn:escapeXml(searchQuery)}" placeholder="${searchPlaceholder}" autocomplete="off" />
                            <datalist id="brandOptions"></datalist>
                        </div>
                        <div class="form-group">
                            <label for="categoryId"><fmt:message key="vehicle.catalog.filter.category" /></label>
                            <select id="categoryId" name="categoryId" class="form-control">
                                <option value="">
                                    <fmt:message key="vehicle.catalog.filter.category.all" />
                                </option>
                                <c:forEach var="category" items="${categories}">
                                    <option value="${category.categoryId}" ${category.categoryId eq selectedCategoryId ? 'selected' : ''}>
                                        <c:out value="${category.categoryName}" />
                                    </option>
                                </c:forEach>
                            </select>
                        </div>
                        <div class="form-group">
                            <label for="headquartersId"><fmt:message key="vehicle.catalog.filter.headquarters" /></label>
                            <select id="headquartersId" name="headquartersId" class="form-control">
                                <option value="">
                                    <fmt:message key="common.home.hero.form.headquarters.all" />
                                </option>
                                <c:forEach var="hq" items="${headquarters}">
                                    <option value="${hq.id}" ${hq.id eq selectedHeadquartersId ? 'selected' : ''}>
                                        <c:out value="${hq.name}" />
                                    </option>
                                </c:forEach>
                            </select>
                        </div>
                        <div class="form-group form-group-inline">
                            <div>
                                <label for="minPrice"><fmt:message key="vehicle.catalog.filter.minPrice" /></label>
                                <input type="number" step="0.01" id="minPrice" name="minPrice" class="form-control"
                                    value="${selectedMinPrice}" />
                            </div>
                            <div>
                                <label for="maxPrice"><fmt:message key="vehicle.catalog.filter.maxPrice" /></label>
                                <input type="number" step="0.01" id="maxPrice" name="maxPrice" class="form-control"
                                    value="${selectedMaxPrice}" />
                            </div>
                        </div>
                        <div class="form-group">
                            <label for="model"><fmt:message key="vehicle.catalog.filter.model" /></label>
                            <input type="text" id="model" name="model" class="form-control"
                                value="${fn:escapeXml(selectedModel)}" autocomplete="off" />
                        </div>
                        <div class="form-group form-group-inline">
                            <div>
                                <label for="yearFrom"><fmt:message key="vehicle.catalog.filter.yearFrom" /></label>
                                <input type="number" id="yearFrom" name="yearFrom" class="form-control" min="1900"
                                    value="${selectedYearFrom}" />
                            </div>
                            <div>
                                <label for="yearTo"><fmt:message key="vehicle.catalog.filter.yearTo" /></label>
                                <input type="number" id="yearTo" name="yearTo" class="form-control" min="1900"
                                    value="${selectedYearTo}" />
                            </div>
                        </div>
                        <div class="form-group form-group-inline">
                            <div>
                                <label for="mileageMin"><fmt:message key="vehicle.catalog.filter.mileageMin" /></label>
                                <input type="number" id="mileageMin" name="mileageMin" class="form-control" min="0"
                                    value="${selectedMileageMin}" />
                            </div>
                            <div>
                                <label for="mileageMax"><fmt:message key="vehicle.catalog.filter.mileageMax" /></label>
                                <input type="number" id="mileageMax" name="mileageMax" class="form-control" min="0"
                                    value="${selectedMileageMax}" />
                            </div>
                        </div>
                    </div>
                    <div class="filter-actions">
                        <button type="submit" class="btn-brand">
                            <span aria-hidden="true">&#128269;</span>
                            <fmt:message key="vehicle.catalog.filter.apply" />
                        </button>
                        <a class="btn-link" href="${ctx}/public/VehicleServlet">
                            <fmt:message key="vehicle.catalog.filter.clear" />
                        </a>
                    </div>
                </form>

                <c:if test="${not empty cartVehicle}">
                    <c:set var="cartCategoryName" value="" />
                    <c:set var="cartHeadquartersName" value="" />
                    <c:forEach var="category" items="${categories}">
                        <c:if test="${category.categoryId eq cartVehicle.categoryId}">
                            <c:set var="cartCategoryName" value="${category.categoryName}" />
                        </c:if>
                    </c:forEach>
                    <c:forEach var="hq" items="${headquarters}">
                        <c:if test="${hq.id eq cartVehicle.currentHeadquartersId}">
                            <c:set var="cartHeadquartersName" value="${hq.name}" />
                        </c:if>
                    </c:forEach>
                    <aside class="cart-card card-common shadow-soft">
                        <h3 class="cart-title"><fmt:message key="reservation.form.vehicle.readonly" /></h3>
                        <p class="cart-vehicle-name">
                            <c:out value="${cartVehicle.brand}" />
                            <c:out value=" ${cartVehicle.model}" />
                        </p>
                        <p class="cart-vehicle-meta">
                            <fmt:message key="vehicle.catalog.pricing.dailyRate" /> ·
                            <span class="accent">${cartVehicle.dailyPrice}</span>
                            <span class="unit"><fmt:message key="vehicle.catalog.pricing.perDay" /></span>
                        </p>
                        <p class="cart-vehicle-category">
                            <span class="cart-label"><fmt:message key="vehicle.catalog.feature.category" /></span>
                            <c:choose>
                                <c:when test="${not empty cartCategoryName}">
                                    <c:out value="${cartCategoryName}" />
                                </c:when>
                                <c:otherwise>
                                    <fmt:message key="vehicle.catalog.feature.category.unassigned" />
                                </c:otherwise>
                            </c:choose>
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
                            <a class="btn-brand" href="${ctx}/public/reservations">
                                <fmt:message key="vehicle.reserve" />
                            </a>
                            <form method="post" action="${ctx}/public/VehicleServlet" class="inline-form">
                                <input type="hidden" name="action" value="releaseVehicle" />
                                <button type="submit" class="btn-link danger">
                                    <fmt:message key="reservation.form.vehicle.release" />
                                </button>
                            </form>
                        </div>
                    </aside>
                </c:if>
            </aside>

            <div class="catalog-results">
                <c:if test="${not empty requestScope.flashSuccess}">
                    <div class="alert alert-success">${requestScope.flashSuccess}</div>
                </c:if>
                <c:if test="${not empty requestScope.flashInfo}">
                    <div class="alert alert-info">${requestScope.flashInfo}</div>
                </c:if>
                <c:if test="${not empty requestScope.flashError}">
                    <div class="alert alert-danger">${requestScope.flashError}</div>
                </c:if>

                <div class="vehicle-grid">
                    <c:choose>
                        <c:when test="${not empty vehicles}">
                            <c:forEach var="vehicle" items="${vehicles}">
                                <c:set var="cardVehicle" value="${vehicle}" scope="request" />
                                <jsp:include page="/public/vehicleCard.jsp" />
                                <c:remove var="cardVehicle" scope="request" />
                            </c:forEach>
                        </c:when>
                        <c:otherwise>
                            <div class="empty-results card-common shadow-soft">
                                <p><fmt:message key="vehicle.catalog.alert.noResults" /></p>
                                <a class="btn-outline-brand" href="${ctx}/public/VehicleServlet">
                                    <fmt:message key="vehicle.catalog.backToHome" />
                                </a>
                            </div>
                        </c:otherwise>
                    </c:choose>
                </div>

                <c:if test="${totalPages gt 1}">
                    <c:set var="paginationCurrentPage" value="${currentPage}" scope="request" />
                    <c:set var="paginationTotalPages" value="${totalPages}" scope="request" />
                    <jsp:include page="/public/pagination.jsp" />
                    <c:remove var="paginationCurrentPage" scope="request" />
                    <c:remove var="paginationTotalPages" scope="request" />
                </c:if>
            </div>
        </div>
    </div>
</section>
<%@ include file="/common/footer.jsp" %>
<script>
(() => {
    const input = document.getElementById('search');
    const dataList = document.getElementById('brandOptions');
    if (!input || !dataList) {
        return;
    }
    let lastQuery = '';
    const baseUrl = '${ctx}';
    const fetchSuggestions = (query) => {
        const requestUrl = baseUrl + '/public/vehicles/search?q=' + encodeURIComponent(query);
        fetch(requestUrl)
            .then(response => response.ok ? response.json() : [])
            .then(results => {
                dataList.innerHTML = '';
                results.forEach(vehicle => {
                    const option = document.createElement('option');
                    option.value = `${vehicle.brand} ${vehicle.model}`.trim();
                    dataList.appendChild(option);
                });
            })
            .catch(() => {
                dataList.innerHTML = '';
            });
    };
    input.addEventListener('input', (event) => {
        const query = event.target.value.trim();
        if (query.length < 2 || query === lastQuery) {
            return;
        }
        lastQuery = query;
        fetchSuggestions(query);
    });
})();
</script>
