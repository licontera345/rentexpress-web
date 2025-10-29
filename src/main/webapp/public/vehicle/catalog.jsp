<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="com.pinguela.rentexpressweb.constants.VehicleConstants" %>
<%@ include file="/common/header.jsp" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
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

<div class="row g-4">
    <div class="col-lg-3">
        <div class="card shadow-soft catalog-filter-card">
            <div class="card-body">
                <h2 class="h5 fw-semibold mb-3">Filtra tu búsqueda</h2>
                <form method="get" action="${ctx}/public/vehicles" class="catalog-form">
                    <div class="mb-3">
                        <label for="search" class="form-label">Buscar</label>
                        <input type="text" class="form-control" id="search" name="${VehicleConstants.PARAM_SEARCH}"
                               placeholder="Marca y modelo (por ejemplo, Ford Focus)"
                               value="${filters[VehicleConstants.PARAM_SEARCH]}">
                    </div>
                    <div class="row g-2 mb-3">
                        <div class="col-12 col-xl-6">
                            <label for="brand" class="form-label">Marca</label>
                            <input type="text" class="form-control" id="brand"
                                   name="${VehicleConstants.PARAM_BRAND}"
                                   placeholder="Toyota"
                                   value="${filters[VehicleConstants.PARAM_BRAND]}">
                        </div>
                        <div class="col-12 col-xl-6">
                            <label for="model" class="form-label">Modelo</label>
                            <input type="text" class="form-control" id="model"
                                   name="${VehicleConstants.PARAM_MODEL}"
                                   placeholder="Corolla"
                                   value="${filters[VehicleConstants.PARAM_MODEL]}">
                        </div>
                    </div>
                    <div class="mb-3">
                        <label for="category" class="form-label">Categoría</label>
                        <select class="form-select" id="category" name="${VehicleConstants.PARAM_CATEGORY}">
                            <option value="">Todas las categorías</option>
                            <c:forEach var="category" items="${categories}">
                                <option value="${category.categoryId}"
                                        ${category.categoryId eq filters[VehicleConstants.PARAM_CATEGORY] ? 'selected' : ''}>
                                    ${category.categoryName}
                                </option>
                            </c:forEach>
                        </select>
                    </div>
                    <div class="mb-3">
                        <label for="headquarters" class="form-label">Sede</label>
                        <select class="form-select" id="headquarters" name="${VehicleConstants.PARAM_HEADQUARTERS}">
                            <option value="">Todas las sedes</option>
                            <c:forEach var="hq" items="${headquarters}">
                                <option value="${hq.headquartersId}"
                                        ${hq.headquartersId eq filters[VehicleConstants.PARAM_HEADQUARTERS] ? 'selected' : ''}>
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
                            <label for="minPrice" class="form-label">Precio mínimo (€)</label>
                            <input type="number" step="0.01" min="0" class="form-control" id="minPrice"
                                   name="${VehicleConstants.PARAM_MIN_PRICE}"
                                   value="${filters[VehicleConstants.PARAM_MIN_PRICE]}">
                        </div>
                        <div class="col">
                            <label for="maxPrice" class="form-label">Precio máximo (€)</label>
                            <input type="number" step="0.01" min="0" class="form-control" id="maxPrice"
                                   name="${VehicleConstants.PARAM_MAX_PRICE}"
                                   value="${filters[VehicleConstants.PARAM_MAX_PRICE]}">
                        </div>
                    </div>
                    <div class="row g-2 mb-3">
                        <div class="col">
                            <label for="minYear" class="form-label">Año desde</label>
                            <input type="number" min="1900" max="2100" class="form-control" id="minYear"
                                   name="${VehicleConstants.PARAM_MIN_YEAR}"
                                   value="${filters[VehicleConstants.PARAM_MIN_YEAR]}">
                        </div>
                        <div class="col">
                            <label for="maxYear" class="form-label">Año hasta</label>
                            <input type="number" min="1900" max="2100" class="form-control" id="maxYear"
                                   name="${VehicleConstants.PARAM_MAX_YEAR}"
                                   value="${filters[VehicleConstants.PARAM_MAX_YEAR]}">
                        </div>
                    </div>
                    <div class="mb-3">
                        <label for="status" class="form-label">Estado</label>
                        <select class="form-select" id="status" name="${VehicleConstants.PARAM_STATUS}">
                            <option value="">Todos los estados</option>
                            <c:forEach var="status" items="${statuses}">
                                <option value="${status.vehicleStatusId}"
                                        ${status.vehicleStatusId eq filters[VehicleConstants.PARAM_STATUS] ? 'selected' : ''}>
                                    ${status.statusName}
                                </option>
                            </c:forEach>
                        </select>
                    </div>
                    <div class="mb-4">
                        <label for="sort" class="form-label">Ordenar por</label>
                        <select class="form-select" id="sort" name="${VehicleConstants.PARAM_SORT}">
                            <option value="${VehicleConstants.VALUE_SORT_PRICE_ASC}"
                                    ${filters[VehicleConstants.PARAM_SORT] == VehicleConstants.VALUE_SORT_PRICE_ASC ? 'selected' : ''}>
                                Precio ascendente
                            </option>
                            <option value="${VehicleConstants.VALUE_SORT_PRICE_DESC}"
                                    ${filters[VehicleConstants.PARAM_SORT] == VehicleConstants.VALUE_SORT_PRICE_DESC ? 'selected' : ''}>
                                Precio descendente
                            </option>
                            <option value="${VehicleConstants.VALUE_SORT_YEAR_DESC}"
                                    ${filters[VehicleConstants.PARAM_SORT] == VehicleConstants.VALUE_SORT_YEAR_DESC ? 'selected' : ''}>
                                Más recientes
                            </option>
                        </select>
                    </div>
                    <div class="form-check form-switch mb-4">
                        <input class="form-check-input" type="checkbox" id="onlyAvailable"
                               name="${VehicleConstants.PARAM_ONLY_AVAILABLE}" value="true"
                               ${filters[VehicleConstants.PARAM_ONLY_AVAILABLE] == 'true' ? 'checked' : ''} />
                        <label class="form-check-label" for="onlyAvailable">Solo mostrar vehículos disponibles</label>
                    </div>
                    <div class="mb-4">
                        <label for="pageSize" class="form-label">Resultados por página</label>
                        <select class="form-select" id="pageSize" name="${VehicleConstants.PARAM_PAGE_SIZE}">
                            <c:forEach var="size" items="${pageSizes}">
                                <option value="${size}" ${size == results.pageSize ? 'selected' : ''}>${size}</option>
                            </c:forEach>
                        </select>
                    </div>
                    <div class="d-grid gap-2">
                        <button type="submit" class="btn btn-brand">Aplicar filtros</button>
                        <a class="btn btn-outline-brand" href="${ctx}/public/vehicles">Limpiar</a>
                    </div>
                </form>
            </div>
        </div>
    </div>
    <div class="col-lg-9">
        <div class="d-flex justify-content-between align-items-center flex-wrap gap-2 mb-3">
            <div>
                <h1 class="h3 fw-semibold mb-0">Catálogo disponible</h1>
                <p class="text-muted mb-0">
                    ${total} vehículos encontrados
                    <c:if test="${results.total > 0}">
                        · mostrando ${results.fromRow} - ${results.toRow}
                    </c:if>
                </p>
            </div>
            <a class="btn btn-outline-brand" href="${ctx}/app/auth/login">
                <i class="bi bi-box-arrow-in-right me-2"></i>Inicia sesión para gestionar tu reserva
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
                    No se han encontrado vehículos disponibles con los criterios seleccionados. Ajusta los filtros o amplía tu búsqueda para ver más opciones.
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
                                        <li><i class="bi bi-calendar3"></i>Año ${vehicle.manufactureYear}</li>
                                        <li><i class="bi bi-speedometer2"></i>Kilometraje: ${vehicle.currentMileage}</li>
                                        <li>
                                            <i class="bi bi-geo-alt"></i>
                                            <c:choose>
                                                <c:when test="${vehicle.currentHeadquarters != null}">
                                                    Sede actual:
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
                                                            Sede actual: ${headquartersNames[vehicle.currentHeadquartersId]}
                                                        </c:when>
                                                        <c:otherwise>
                                                            Sede actual: #${vehicle.currentHeadquartersId}
                                                        </c:otherwise>
                                                    </c:choose>
                                                </c:when>
                                                <c:otherwise>
                                                    Sede actual sin asignar
                                                </c:otherwise>
                                            </c:choose>
                                        </li>
                                    </ul>
                                </div>
                                <div class="vehicle-card-footer">
                                    <div>
                                        <small class="text-muted">Tarifa diaria</small>
                                        <div class="h5 mb-0">
                                            <fmt:formatNumber value="${vehicle.dailyPrice}" type="currency" currencySymbol="€"/>
                                            <span class="text-muted fs-6">/día</span>
                                        </div>
                                    </div>
                                    <c:url var="detailUrl" value="/public/vehicles/detail">
                                        <c:param name="${VehicleConstants.PARAM_VEHICLE_ID}" value="${vehicle.vehicleId}" />
                                        <c:if test="${not empty filters[VehicleConstants.PARAM_HEADQUARTERS]}">
                                            <c:param name="${VehicleConstants.PARAM_HEADQUARTERS}"
                                                     value="${filters[VehicleConstants.PARAM_HEADQUARTERS]}" />
                                        </c:if>
                                        <c:if test="${not empty filters[VehicleConstants.PARAM_PICKUP_DATE]}">
                                            <c:param name="${VehicleConstants.PARAM_PICKUP_DATE}"
                                                     value="${filters[VehicleConstants.PARAM_PICKUP_DATE]}" />
                                        </c:if>
                                        <c:if test="${not empty filters[VehicleConstants.PARAM_RETURN_DATE]}">
                                            <c:param name="${VehicleConstants.PARAM_RETURN_DATE}"
                                                     value="${filters[VehicleConstants.PARAM_RETURN_DATE]}" />
                                        </c:if>
                                    </c:url>
                                    <a class="btn btn-brand" href="${ctx}${detailUrl}">
                                        Ver detalles
                                    </a>
                                </div>
                            </div>
                        </div>
                    </c:forEach>
                </div>
                <c:if test="${results.totalPages > 1}">
                    <nav class="mt-4" aria-label="Paginación de vehículos">
                        <ul class="pagination justify-content-center">
                            <li class="page-item ${!results.hasPrev ? 'disabled' : ''}">
                                <c:url var="prevUrl" value="/public/vehicles">
                                    <c:param name="${VehicleConstants.PARAM_PAGE}" value="${results.page - 1}" />
                                    <c:param name="${VehicleConstants.PARAM_PAGE_SIZE}" value="${results.pageSize}" />
                                    <c:param name="${VehicleConstants.PARAM_SORT}" value="${filters[VehicleConstants.PARAM_SORT]}" />
                                    <c:forEach var="entry" items="${filters}">
                                        <c:if test="${entry.key ne VehicleConstants.PARAM_PAGE && entry.key ne VehicleConstants.PARAM_PAGE_SIZE && entry.key ne VehicleConstants.PARAM_SORT && entry.value ne null && entry.value ne ''}">
                                            <c:param name="${entry.key}" value="${entry.value}" />
                                        </c:if>
                                    </c:forEach>
                                </c:url>
                                <c:set var="prevHref" value="${ctx}${prevUrl}" />
                                <c:if test="${!results.hasPrev}">
                                    <c:set var="prevHref" value="#" />
                                </c:if>
                                <a class="page-link" href="${prevHref}" aria-label="Anterior">
                                    <span aria-hidden="true">&laquo;</span>
                                </a>
                            </li>
                            <c:forEach begin="1" end="${results.totalPages}" var="pageNumber">
                                <c:url var="pageUrl" value="/public/vehicles">
                                    <c:param name="${VehicleConstants.PARAM_PAGE}" value="${pageNumber}" />
                                    <c:param name="${VehicleConstants.PARAM_PAGE_SIZE}" value="${results.pageSize}" />
                                    <c:param name="${VehicleConstants.PARAM_SORT}" value="${filters[VehicleConstants.PARAM_SORT]}" />
                                    <c:forEach var="entry" items="${filters}">
                                        <c:if test="${entry.key ne VehicleConstants.PARAM_PAGE && entry.key ne VehicleConstants.PARAM_PAGE_SIZE && entry.key ne VehicleConstants.PARAM_SORT && entry.value ne null && entry.value ne ''}">
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
                                    <c:param name="${VehicleConstants.PARAM_PAGE}" value="${results.page + 1}" />
                                    <c:param name="${VehicleConstants.PARAM_PAGE_SIZE}" value="${results.pageSize}" />
                                    <c:param name="${VehicleConstants.PARAM_SORT}" value="${filters[VehicleConstants.PARAM_SORT]}" />
                                    <c:forEach var="entry" items="${filters}">
                                        <c:if test="${entry.key ne VehicleConstants.PARAM_PAGE && entry.key ne VehicleConstants.PARAM_PAGE_SIZE && entry.key ne VehicleConstants.PARAM_SORT && entry.value ne null && entry.value ne ''}">
                                            <c:param name="${entry.key}" value="${entry.value}" />
                                        </c:if>
                                    </c:forEach>
                                </c:url>
                                <c:set var="nextHref" value="${ctx}${nextUrl}" />
                                <c:if test="${!results.hasNext}">
                                    <c:set var="nextHref" value="#" />
                                </c:if>
                                <a class="page-link" href="${nextHref}" aria-label="Siguiente">
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
