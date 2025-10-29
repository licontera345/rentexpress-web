<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ include file="/common/header.jsp" %>
<c:set var="filters" value="${vehicleFilters}" />
<c:set var="statuses" value="${vehicleStatuses}" />
<c:set var="categories" value="${vehicleCategories}" />
<c:set var="statusNames" value="${vehicleStatusNames}" />
<c:set var="categoryNames" value="${vehicleCategoryNames}" />
<c:set var="results" value="${vehicleResults}" />
<div class="row g-4">
    <div class="col-lg-4">
        <div class="card shadow-sm">
            <div class="card-header">Filtros rápidos</div>
            <div class="card-body">
                <form method="get" action="${ctx}/app/vehicles/manage" class="vstack gap-3">
                    <div>
                        <label class="form-label" for="search">Marca o modelo</label>
                        <input class="form-control" type="text" id="search" name="search" value="${filters.search}" placeholder="Ej. Tesla" />
                    </div>
                    <div>
                        <label class="form-label" for="category">Categoría</label>
                        <select class="form-select" id="category" name="category">
                            <option value="">Todas</option>
                            <c:forEach var="category" items="${categories}">
                                <option value="${category.categoryId}" ${category.categoryId eq filters.category ? 'selected' : ''}>${category.categoryName}</option>
                            </c:forEach>
                        </select>
                    </div>
                    <div>
                        <label class="form-label" for="status">Estado</label>
                        <select class="form-select" id="status" name="status">
                            <option value="">Todos</option>
                            <c:forEach var="status" items="${statuses}">
                                <option value="${status.vehicleStatusId}" ${status.vehicleStatusId eq filters.status ? 'selected' : ''}>${status.statusName}</option>
                            </c:forEach>
                        </select>
                    </div>
                    <div>
                        <label class="form-label" for="sort">Orden</label>
                        <select class="form-select" id="sort" name="sort">
                            <option value="priceAsc" ${filters.sort == 'priceAsc' ? 'selected' : ''}>Precio (menor a mayor)</option>
                            <option value="priceDesc" ${filters.sort == 'priceDesc' ? 'selected' : ''}>Precio (mayor a menor)</option>
                            <option value="yearDesc" ${filters.sort == 'yearDesc' ? 'selected' : ''}>Año (reciente primero)</option>
                        </select>
                    </div>
                    <div class="d-flex gap-2">
                        <button type="submit" class="btn btn-brand flex-grow-1"><i class="bi bi-filter"></i> Filtrar</button>
                        <a class="btn btn-outline-secondary" href="${ctx}/app/vehicles/manage" title="Limpiar filtros">
                            <i class="bi bi-arrow-counterclockwise"></i>
                        </a>
                    </div>
                </form>
            </div>
        </div>
        <div class="card shadow-sm mt-4">
            <div class="card-header">Resumen</div>
            <div class="card-body">
                <ul class="list-unstyled mb-0">
                    <li class="d-flex justify-content-between"><span>Total flota</span><strong>${totalVehicles}</strong></li>
                    <li class="d-flex justify-content-between"><span>Resultados actuales</span><strong>${results.total}</strong></li>
                    <li class="d-flex justify-content-between"><span>Página</span><strong>${results.page} / ${results.totalPages}</strong></li>
                </ul>
            </div>
        </div>
    </div>
    <div class="col-lg-8">
        <c:if test="${not empty vehicleFilterErrors}">
            <div class="alert alert-warning">
                <ul class="mb-0">
                    <c:forEach var="error" items="${vehicleFilterErrors}">
                        <li>${error}</li>
                    </c:forEach>
                </ul>
            </div>
        </c:if>
        <div class="card shadow-sm">
            <div class="card-header d-flex justify-content-between align-items-center">
                <span>Catálogo interno</span>
                <span class="badge bg-secondary-subtle text-secondary">${results.total} vehículos</span>
            </div>
            <div class="table-responsive">
                <table class="table table-hover align-middle mb-0">
                    <thead>
                        <tr>
                            <th>Vehículo</th>
                            <th class="d-none d-md-table-cell">Matrícula</th>
                            <th class="d-none d-lg-table-cell">Categoría</th>
                            <th class="d-none d-lg-table-cell">Estado</th>
                            <th>Precio día</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:choose>
                            <c:when test="${empty vehicles}">
                                <tr>
                                    <td colspan="5" class="text-center text-muted py-4">Ningún vehículo coincide con los filtros aplicados.</td>
                                </tr>
                            </c:when>
                            <c:otherwise>
                                <c:forEach var="vehicle" items="${vehicles}">
                                    <tr>
                                        <td>
                                            <div class="fw-semibold">${vehicle.brand} ${vehicle.model}</div>
                                            <div class="text-muted small">Año ${vehicle.manufactureYear}</div>
                                        </td>
                                        <td class="d-none d-md-table-cell">${vehicle.licensePlate}</td>
                                        <td class="d-none d-lg-table-cell">${categoryNames[vehicle.categoryId]}</td>
                                        <td class="d-none d-lg-table-cell">${statusNames[vehicle.vehicleStatusId]}</td>
                                        <td>
                                            <fmt:formatNumber value="${vehicle.dailyPrice}" type="currency" currencySymbol="€" />
                                        </td>
                                    </tr>
                                </c:forEach>
                            </c:otherwise>
                        </c:choose>
                    </tbody>
                </table>
            </div>
        </div>
        <c:if test="${results.totalPages > 1}">
            <nav aria-label="Paginación" class="mt-3">
                <ul class="pagination justify-content-center">
                    <c:set var="prevPage" value="${results.page - 1}" />
                    <c:set var="nextPage" value="${results.page + 1}" />
                    <c:if test="${prevPage < 1}"><c:set var="prevPage" value="1" /></c:if>
                    <c:if test="${nextPage > results.totalPages}"><c:set var="nextPage" value="${results.totalPages}" /></c:if>
                    <li class="page-item ${results.page == 1 ? 'disabled' : ''}">
                        <a class="page-link" href="${ctx}/app/vehicles/manage?search=${filters.search}&category=${filters.category}&status=${filters.status}&sort=${filters.sort}&page=${prevPage}&pageSize=${results.pageSize}">&laquo;</a>
                    </li>
                    <li class="page-item disabled"><span class="page-link">Página ${results.page}</span></li>
                    <li class="page-item ${results.page == results.totalPages ? 'disabled' : ''}">
                        <a class="page-link" href="${ctx}/app/vehicles/manage?search=${filters.search}&category=${filters.category}&status=${filters.status}&sort=${filters.sort}&page=${nextPage}&pageSize=${results.pageSize}">&raquo;</a>
                    </li>
                </ul>
            </nav>
        </c:if>
        <div class="mt-3 d-flex gap-2">
            <a class="btn btn-outline-brand" href="${ctx}/app/rentals/private"><i class="bi bi-speedometer"></i> Panel de alquileres</a>
            <a class="btn btn-outline-secondary" href="${ctx}/public/vehicles"><i class="bi bi-box-arrow-up-right"></i> Ver catálogo público</a>
        </div>
    </div>
</div>
<%@ include file="/common/footer.jsp" %>
