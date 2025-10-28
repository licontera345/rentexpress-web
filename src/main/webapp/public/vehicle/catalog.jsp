<%@ page import="com.pinguela.rentexpressweb.constants.VehicleConstants" %>
<%@ include file="/common/header.jsp" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<fmt:setLocale value="es_ES" scope="request" />
<c:set var="filters" value="${vehicleFilters}" />
<c:set var="categories" value="${vehicleCategories}" />
<c:set var="categoryNames" value="${vehicleCategoryNames}" />
<c:set var="vehicles" value="${vehicles}" />
<c:set var="filterErrors" value="${vehicleFilterErrors}" />
<c:set var="total" value="${totalVehicles}" />

<div class="row g-4">
    <div class="col-lg-3">
        <div class="card shadow-soft catalog-filter-card">
            <div class="card-body">
                <h2 class="h5 fw-semibold mb-3">Filtra tu búsqueda</h2>
                <form method="get" action="${ctx}/public/vehicles" class="catalog-form">
                    <div class="mb-3">
                        <label for="search" class="form-label">Buscar</label>
                        <input type="text" class="form-control" id="search" name="${VehicleConstants.PARAM_SEARCH}"
                               placeholder="Marca, modelo, palabra clave"
                               value="${filters[VehicleConstants.PARAM_SEARCH]}">
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
                <p class="text-muted mb-0">${total} vehículos encontrados</p>
            </div>
            <a class="btn btn-outline-brand" href="${ctx}/app/auth/login">
                <i class="bi bi-box-arrow-in-right me-2"></i>Accede para simular tu reserva
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
                    No se han encontrado vehículos con los criterios seleccionados. Ajusta los filtros para ver más opciones.
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
                                        <li><i class="bi bi-geo-alt"></i>Sede actual ID: ${vehicle.currentHeadquartersId}</li>
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
                                    <a class="btn btn-brand" href="${ctx}/public/vehicles/detail?vehicleId=${vehicle.vehicleId}">
                                        Ver detalles
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
