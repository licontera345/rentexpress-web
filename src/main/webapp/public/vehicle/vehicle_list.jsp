<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>

<fmt:setLocale value="${sessionScope.locale}" scope="session" />
<fmt:setBundle basename="i18n.Messages" />

<!DOCTYPE html>
<html lang="${sessionScope.locale.language != null ? sessionScope.locale.language : 'es'}">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title><fmt:message key="vehicle.list.title" /> · RentExpress</title>
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700&display=swap" rel="stylesheet">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet" integrity="sha384-QWTKZyjpPEjISv5WaRU9OFeRpok6YctnYmDr5pNlyT2bRjXh0JMhjY6hW+ALEwIH" crossorigin="anonymous">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.css" rel="stylesheet">
    <link href="https://cdn.jsdelivr.net/npm/flag-icons@6.6.6/css/flag-icons.min.css" rel="stylesheet">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/main.css">
</head>
<body class="bg-light d-flex flex-column min-vh-100">
    <%@ include file="/common/header.jsp"%>

    <main class="flex-grow-1 py-5">
        <div class="container">
            <div class="row g-4">
                <div class="col-lg-4">
                    <div class="bg-white rounded-4 shadow-soft p-4 h-100">
                        <h1 class="h4 fw-bold mb-1"><fmt:message key="vehicle.filters.title" /></h1>
                        <p class="text-muted mb-4"><fmt:message key="vehicle.list.subtitle" /></p>
                        <form action="${pageContext.request.contextPath}/public/VehicleServlet" method="get" class="d-flex flex-column gap-3">
                            <input type="hidden" name="action" value="list" />
                            <div>
                                <label for="brand" class="form-label fw-semibold small text-uppercase text-muted"><fmt:message key="vehicle.filters.brand" /></label>
                                <input type="text" class="form-control" id="brand" name="brand" value="${vehicleCriteria.brand}" placeholder="Toyota, Kia...">
                            </div>
                            <div>
                                <label for="model" class="form-label fw-semibold small text-uppercase text-muted"><fmt:message key="vehicle.filters.model" /></label>
                                <input type="text" class="form-control" id="model" name="model" value="${vehicleCriteria.model}" placeholder="Corolla, 208...">
                            </div>
                            <div>
                                <label for="categoryId" class="form-label fw-semibold small text-uppercase text-muted"><fmt:message key="vehicle.filters.category" /></label>
                                <select class="form-select" id="categoryId" name="categoryId">
                                    <option value=""><fmt:message key="search.vehicleType.any" /></option>
                                    <c:forEach var="category" items="${vehicleCategories}">
                                        <option value="${category.categoryId}" <c:if test="${vehicleCriteria.categoryId eq category.categoryId}">selected</c:if>>
                                            <c:out value="${category.categoryName}" />
                                        </option>
                                    </c:forEach>
                                </select>
                            </div>
                            <div>
                                <label for="vehicleStatusId" class="form-label fw-semibold small text-uppercase text-muted"><fmt:message key="vehicle.filters.status" /></label>
                                <select class="form-select" id="vehicleStatusId" name="vehicleStatusId">
                                    <option value=""><fmt:message key="search.vehicleType.any" /></option>
                                    <c:forEach var="status" items="${vehicleStatuses}">
                                        <option value="${status.vehicleStatusId}" <c:if test="${vehicleCriteria.vehicleStatusId eq status.vehicleStatusId}">selected</c:if>>
                                            <c:out value="${status.statusName}" />
                                        </option>
                                    </c:forEach>
                                </select>
                            </div>
                            <div class="row g-3">
                                <div class="col-6">
                                    <label for="priceMin" class="form-label fw-semibold small text-uppercase text-muted"><fmt:message key="vehicle.filters.priceMin" /></label>
                                    <input type="number" step="0.01" min="0" class="form-control" id="priceMin" name="priceMin" value="${vehicleCriteria.dailyPriceMin}" />
                                </div>
                                <div class="col-6">
                                    <label for="priceMax" class="form-label fw-semibold small text-uppercase text-muted"><fmt:message key="vehicle.filters.priceMax" /></label>
                                    <input type="number" step="0.01" min="0" class="form-control" id="priceMax" name="priceMax" value="${vehicleCriteria.dailyPriceMax}" />
                                </div>
                            </div>
                            <div class="d-flex gap-2">
                                <button type="submit" class="btn btn-brand flex-grow-1">
                                    <i class="bi bi-funnel me-2"></i>
                                    <fmt:message key="vehicle.filters.search" />
                                </button>
                                <a href="${pageContext.request.contextPath}/public/VehicleServlet?action=list" class="btn btn-outline-brand">
                                    <i class="bi bi-arrow-counterclockwise"></i>
                                    <span class="visually-hidden"><fmt:message key="vehicle.filters.reset" /></span>
                                </a>
                            </div>
                        </form>
                    </div>
                </div>
                <div class="col-lg-8">
                    <div class="d-flex flex-column gap-3">
                        <div class="bg-white rounded-4 shadow-soft p-4">
                            <div class="d-flex flex-column flex-md-row justify-content-between align-items-md-center gap-2">
                                <div>
                                    <h2 class="h4 fw-bold mb-1"><fmt:message key="vehicle.list.title" /></h2>
                                    <c:choose>
                                        <c:when test="${vehicleResults.total > 0}">
                                            <p class="text-muted mb-0">
                                                <fmt:message key="vehicle.results.showingRange">
                                                    <fmt:param value="${vehicleResults.fromRow}" />
                                                    <fmt:param value="${vehicleResults.toRow}" />
                                                    <fmt:param value="${vehicleResults.total}" />
                                                </fmt:message>
                                            </p>
                                        </c:when>
                                        <c:otherwise>
                                            <p class="text-muted mb-0"><fmt:message key="vehicle.list.subtitle" /></p>
                                        </c:otherwise>
                                    </c:choose>
                                </div>
                                <c:if test="${vehicleResults.total > 0}">
                                    <span class="badge bg-brand-soft text-brand fw-semibold px-3 py-2">
                                        <fmt:message key="vehicle.results.count">
                                            <fmt:param value="${vehicleResults.total}" />
                                        </fmt:message>
                                    </span>
                                </c:if>
                            </div>
                        </div>

                        <c:if test="${vehicleListError}">
                            <div class="alert alert-danger" role="alert">
                                <fmt:message key="vehicle.results.error" />
                            </div>
                        </c:if>

                        <c:choose>
                            <c:when test="${not empty vehicleList}">
                                <div class="row g-4">
                                    <c:forEach var="vehicle" items="${vehicleList}">
                                        <div class="col-md-6">
                                            <div class="card h-100 border-0 shadow-soft">
                                                <div class="ratio ratio-4x3 rounded-top">
                                                    <c:choose>
                                                        <c:when test="${vehicleImages[vehicle.vehicleId]}">
                                                            <img src="${pageContext.request.contextPath}/public/vehicle-image?vehicleId=${vehicle.vehicleId}" class="img-fluid object-fit-cover rounded-top" alt="${vehicle.brand} ${vehicle.model}" />
                                                        </c:when>
                                                        <c:otherwise>
                                                            <div class="d-flex align-items-center justify-content-center bg-light rounded-top">
                                                                <span class="text-muted small text-uppercase fw-semibold"><fmt:message key="vehicle.card.noImage" /></span>
                                                            </div>
                                                        </c:otherwise>
                                                    </c:choose>
                                                </div>
                                                <div class="card-body d-flex flex-column gap-2">
                                                    <div class="d-flex justify-content-between align-items-start">
                                                        <div>
                                                            <h3 class="h5 fw-bold mb-1">${vehicle.brand} ${vehicle.model}</h3>
                                                            <div class="text-muted small text-uppercase fw-semibold">
                                                                <fmt:message key="vehicle.card.category" />:
                                                                <c:out value="${vehicle.vehicleCategory != null ? vehicle.vehicleCategory.categoryName : '-'}" />
                                                            </div>
                                                        </div>
                                                        <c:if test="${vehicle.vehicleStatus != null && vehicle.vehicleStatus.statusName != null}">
                                                            <span class="badge bg-brand-soft text-brand fw-semibold">
                                                                <fmt:message key="vehicle.card.status" />:
                                                                <c:out value="${vehicle.vehicleStatus.statusName}" />
                                                            </span>
                                                        </c:if>
                                                    </div>
                                                    <c:if test="${vehicle.currentHeadquarters != null}">
                                                        <div class="text-muted small d-flex align-items-center gap-2">
                                                            <i class="bi bi-geo-alt"></i>
                                                            <span>
                                                                <fmt:message key="vehicle.card.headquarters" />:
                                                                <c:out value="${vehicle.currentHeadquarters.name}" />
                                                                <c:if test="${vehicle.currentHeadquarters.city != null || vehicle.currentHeadquarters.province != null}">
                                                                    &nbsp;·&nbsp;
                                                                    <c:if test="${vehicle.currentHeadquarters.city != null}">
                                                                        <c:out value="${vehicle.currentHeadquarters.city.cityName}" />
                                                                    </c:if>
                                                                    <c:if test="${vehicle.currentHeadquarters.city != null && vehicle.currentHeadquarters.province != null}">, </c:if>
                                                                    <c:if test="${vehicle.currentHeadquarters.province != null}">
                                                                        <c:out value="${vehicle.currentHeadquarters.province.provinceName}" />
                                                                    </c:if>
                                                                </c:if>
                                                            </span>
                                                        </div>
                                                    </c:if>
                                                    <div class="d-flex flex-wrap gap-3 text-muted small">
                                                        <c:if test="${vehicle.manufactureYear != null}">
                                                            <span class="d-flex align-items-center gap-2">
                                                                <i class="bi bi-calendar-event"></i>
                                                                <fmt:message key="vehicle.card.year" />: ${vehicle.manufactureYear}
                                                            </span>
                                                        </c:if>
                                                        <c:if test="${vehicle.currentMileage != null}">
                                                            <span class="d-flex align-items-center gap-2">
                                                                <i class="bi bi-speedometer2"></i>
                                                                <fmt:message key="vehicle.card.mileage" />: ${vehicle.currentMileage} <fmt:message key="vehicle.card.mileage.unit" />
                                                            </span>
                                                        </c:if>
                                                    </div>
                                                    <div class="mt-auto d-flex justify-content-between align-items-center">
                                                        <c:if test="${vehicle.dailyPrice != null}">
                                                            <div class="fs-5 fw-bold text-brand">
                                                                <fmt:formatNumber value="${vehicle.dailyPrice}" type="number" maxFractionDigits="2" minFractionDigits="2" />€
                                                                <span class="text-muted fs-6"><fmt:message key="vehicle.card.pricePerDay" /></span>
                                                            </div>
                                                        </c:if>
                                                        <a class="btn btn-outline-brand" href="${pageContext.request.contextPath}/public/index.jsp#hero">
                                                            <i class="bi bi-calendar-check me-2"></i>
                                                            <fmt:message key="search.cta.search" />
                                                        </a>
                                                    </div>
                                                </div>
                                            </div>
                                        </div>
                                    </c:forEach>
                                </div>

                                <c:if test="${vehicleResults.totalPages > 1}">
                                    <nav aria-label="Vehicle pagination" class="mt-4">
                                        <ul class="pagination justify-content-center">
                                            <c:if test="${vehicleResults.hasPrev}">
                                                <c:url var="prevUrl" value="${pageContext.request.contextPath}/public/VehicleServlet">
                                                    <c:param name="action" value="list" />
                                                    <c:param name="page" value="${vehicleResults.page - 1}" />
                                                    <c:if test="${vehicleCriteria.brand != null}">
                                                        <c:param name="brand" value="${vehicleCriteria.brand}" />
                                                    </c:if>
                                                    <c:if test="${vehicleCriteria.model != null}">
                                                        <c:param name="model" value="${vehicleCriteria.model}" />
                                                    </c:if>
                                                    <c:if test="${vehicleCriteria.categoryId != null}">
                                                        <c:param name="categoryId" value="${vehicleCriteria.categoryId}" />
                                                    </c:if>
                                                    <c:if test="${vehicleCriteria.vehicleStatusId != null}">
                                                        <c:param name="vehicleStatusId" value="${vehicleCriteria.vehicleStatusId}" />
                                                    </c:if>
                                                    <c:if test="${vehicleCriteria.dailyPriceMin != null}">
                                                        <c:param name="priceMin" value="${vehicleCriteria.dailyPriceMin}" />
                                                    </c:if>
                                                    <c:if test="${vehicleCriteria.dailyPriceMax != null}">
                                                        <c:param name="priceMax" value="${vehicleCriteria.dailyPriceMax}" />
                                                    </c:if>
                                                </c:url>
                                                <li class="page-item">
                                                    <a class="page-link" href="${prevUrl}"><fmt:message key="vehicle.pagination.previous" /></a>
                                                </li>
                                            </c:if>
                                            <c:forEach var="pageNumber" begin="1" end="${vehicleResults.totalPages}">
                                                <c:url var="pageUrl" value="${pageContext.request.contextPath}/public/VehicleServlet">
                                                    <c:param name="action" value="list" />
                                                    <c:param name="page" value="${pageNumber}" />
                                                    <c:if test="${vehicleCriteria.brand != null}">
                                                        <c:param name="brand" value="${vehicleCriteria.brand}" />
                                                    </c:if>
                                                    <c:if test="${vehicleCriteria.model != null}">
                                                        <c:param name="model" value="${vehicleCriteria.model}" />
                                                    </c:if>
                                                    <c:if test="${vehicleCriteria.categoryId != null}">
                                                        <c:param name="categoryId" value="${vehicleCriteria.categoryId}" />
                                                    </c:if>
                                                    <c:if test="${vehicleCriteria.vehicleStatusId != null}">
                                                        <c:param name="vehicleStatusId" value="${vehicleCriteria.vehicleStatusId}" />
                                                    </c:if>
                                                    <c:if test="${vehicleCriteria.dailyPriceMin != null}">
                                                        <c:param name="priceMin" value="${vehicleCriteria.dailyPriceMin}" />
                                                    </c:if>
                                                    <c:if test="${vehicleCriteria.dailyPriceMax != null}">
                                                        <c:param name="priceMax" value="${vehicleCriteria.dailyPriceMax}" />
                                                    </c:if>
                                                </c:url>
                                                <li class="page-item ${vehicleResults.page == pageNumber ? 'active' : ''}">
                                                    <a class="page-link" href="${pageUrl}">${pageNumber}</a>
                                                </li>
                                            </c:forEach>
                                            <c:if test="${vehicleResults.hasNext}">
                                                <c:url var="nextUrl" value="${pageContext.request.contextPath}/public/VehicleServlet">
                                                    <c:param name="action" value="list" />
                                                    <c:param name="page" value="${vehicleResults.page + 1}" />
                                                    <c:if test="${vehicleCriteria.brand != null}">
                                                        <c:param name="brand" value="${vehicleCriteria.brand}" />
                                                    </c:if>
                                                    <c:if test="${vehicleCriteria.model != null}">
                                                        <c:param name="model" value="${vehicleCriteria.model}" />
                                                    </c:if>
                                                    <c:if test="${vehicleCriteria.categoryId != null}">
                                                        <c:param name="categoryId" value="${vehicleCriteria.categoryId}" />
                                                    </c:if>
                                                    <c:if test="${vehicleCriteria.vehicleStatusId != null}">
                                                        <c:param name="vehicleStatusId" value="${vehicleCriteria.vehicleStatusId}" />
                                                    </c:if>
                                                    <c:if test="${vehicleCriteria.dailyPriceMin != null}">
                                                        <c:param name="priceMin" value="${vehicleCriteria.dailyPriceMin}" />
                                                    </c:if>
                                                    <c:if test="${vehicleCriteria.dailyPriceMax != null}">
                                                        <c:param name="priceMax" value="${vehicleCriteria.dailyPriceMax}" />
                                                    </c:if>
                                                </c:url>
                                                <li class="page-item">
                                                    <a class="page-link" href="${nextUrl}"><fmt:message key="vehicle.pagination.next" /></a>
                                                </li>
                                            </c:if>
                                        </ul>
                                    </nav>
                                </c:if>
                            </c:when>
                            <c:otherwise>
                                <div class="bg-white rounded-4 shadow-soft p-5 text-center">
                                    <i class="bi bi-car-front text-brand display-4 mb-3"></i>
                                    <p class="text-muted mb-2"><fmt:message key="vehicle.results.empty" /></p>
                                    <a class="btn btn-outline-brand" href="${pageContext.request.contextPath}/public/VehicleServlet?action=list">
                                        <fmt:message key="vehicle.filters.reset" />
                                    </a>
                                </div>
                            </c:otherwise>
                        </c:choose>
                    </div>
                </div>
            </div>
        </div>
    </main>

    <%@ include file="/common/footer.jsp"%>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js" integrity="sha384-YvpcrYf0tY3lHB60NNkmXc5s9fDVZLESaAA55NDzOxhy9GkcIdslK1eN7N6jIeHz" crossorigin="anonymous"></script>
</body>
</html>
