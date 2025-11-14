<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<c:set var="ctx" value="${pageContext.request.contextPath}" scope="request" />
<c:set var="ctx" value="${ctx}" scope="page" />
<c:set var="activeLocale" value="${sessionScope.appLocale}" scope="page" />
<c:if test="${empty activeLocale}">
    <c:set var="activeLocale" value="es" scope="page" />
</c:if>
<c:set var="headerCartVehicle" value="${sessionScope.reservationCartVehicle}" scope="page" />
<fmt:setLocale value="${activeLocale}" scope="session" />
<fmt:setBundle basename="i18n.Messages" scope="session" />
<c:set var="currentPath" value="${pageContext.request.requestURI}" />
<!DOCTYPE html>
<html lang="${activeLocale}">
<head>
    <meta charset="UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1" />
    <title><fmt:message key="layout.appName" /></title>
    <link rel="preconnect" href="https://fonts.googleapis.com" />
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin />
    <link
        href="https://fonts.googleapis.com/css2?family=Plus+Jakarta+Sans:wght@400;500;600;700&display=swap"
        rel="stylesheet" />
    <link
        href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css"
        rel="stylesheet"
        integrity="sha384-QWTKZyjpPEjISv5WaRU9OFeRpok6YctnYmDr5pNlyT2bRjXh0JMhjY6hW+ALEwIH"
        crossorigin="anonymous" />
    <link
        href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css"
        rel="stylesheet"
        integrity="sha384-XGjxtQfXaH2tnPFa9x+ruJTuLE3Aa6LhHSWRr1XeTyhezb4abCG4ccI5AkVDxqC+"
        crossorigin="anonymous" />
    <link rel="stylesheet" href="<c:url value='/css/main.css' />" />
</head>
<body class="d-flex flex-column min-vh-100">
    <header class="shadow-sm border-bottom bg-white">
        <nav class="navbar navbar-expand-lg navbar-light py-3">
            <div class="container">
                <a class="navbar-brand d-flex align-items-center gap-2" href="${ctx}/public/index">
                    <span class="brand-symbol flex-shrink-0">RE</span>
                    <span class="fw-semibold text-uppercase"><fmt:message key="layout.appName" /></span>
                </a>
                <span class="d-none d-lg-inline text-muted small ms-lg-3"><fmt:message key="common.home.hero.subtitle" /></span>
                <button
                    class="navbar-toggler"
                    type="button"
                    data-bs-toggle="collapse"
                    data-bs-target="#mainNavbar"
                    aria-controls="mainNavbar"
                    aria-expanded="false"
                    aria-label="Toggle navigation">
                    <span class="navbar-toggler-icon"></span>
                </button>
                <div class="collapse navbar-collapse" id="mainNavbar">
                    <ul class="navbar-nav me-auto mb-2 mb-lg-0">
                        <li class="nav-item">
                            <a
                                class="nav-link ${fn:endsWith(currentPath, '/public/index') or fn:endsWith(currentPath, '/public/index.jsp') ? 'active fw-semibold' : ''}"
                                aria-current="${fn:endsWith(currentPath, '/public/index') or fn:endsWith(currentPath, '/public/index.jsp') ? 'page' : ''}"
                                href="${ctx}/public/index">
                                <fmt:message key="layout.home" />
                            </a>
                        </li>
                        <li class="nav-item">
                            <a
                                class="nav-link ${fn:contains(currentPath, '/public/VehicleServlet') ? 'active fw-semibold' : ''}"
                                aria-current="${fn:contains(currentPath, '/public/VehicleServlet') ? 'page' : ''}"
                                href="${ctx}/public/VehicleServlet">
                                <fmt:message key="layout.vehicles" />
                            </a>
                        </li>
                        <c:if test="${not empty sessionScope.currentEmployee}">
                            <li class="nav-item">
                                <a
                                    class="nav-link ${fn:contains(currentPath, '/private/profile') ? 'active fw-semibold' : ''}"
                                    aria-current="${fn:contains(currentPath, '/private/profile') ? 'page' : ''}"
                                    href="${ctx}/private/profile">
                                    <fmt:message key="layout.profile" />
                                </a>
                            </li>
                            <li class="nav-item">
                                <a
                                    class="nav-link ${fn:contains(currentPath, '/private/dashboard') ? 'active fw-semibold' : ''}"
                                    aria-current="${fn:contains(currentPath, '/private/dashboard') ? 'page' : ''}"
                                    href="${ctx}/private/dashboard.jsp">
                                    <fmt:message key="layout.dashboard" />
                                </a>
                            </li>
                            <li class="nav-item">
                                <a
                                    class="nav-link ${fn:contains(currentPath, '/private/EmployeeServlet') or fn:contains(currentPath, '/private/employee') ? 'active fw-semibold' : ''}"
                                    aria-current="${fn:contains(currentPath, '/private/EmployeeServlet') or fn:contains(currentPath, '/private/employee') ? 'page' : ''}"
                                    href="${ctx}/private/EmployeeServlet">
                                    <fmt:message key="layout.employees" />
                                </a>
                            </li>
                            <li class="nav-item">
                                <a
                                    class="nav-link ${fn:contains(currentPath, '/private/VehicleServlet') or fn:contains(currentPath, '/private/vehicle') ? 'active fw-semibold' : ''}"
                                    aria-current="${fn:contains(currentPath, '/private/VehicleServlet') or fn:contains(currentPath, '/private/vehicle') ? 'page' : ''}"
                                    href="${ctx}/private/VehicleServlet">
                                    <fmt:message key="layout.vehicles" />
                                </a>
                            </li>
                        </c:if>
                    </ul>
                    <div class="d-lg-flex align-items-center gap-3 ms-lg-3 w-100 w-lg-auto">
                        <form method="get" action="${ctx}/common/language" class="language-switcher ms-lg-auto">
                            <fmt:message var="languageOptionEs" key="layout.language.option.es" />
                            <fmt:message var="languageOptionEn" key="layout.language.option.en" />
                            <fmt:message var="languageOptionFr" key="layout.language.option.fr" />
                            <label for="lang" class="visually-hidden"><fmt:message key="layout.language" /></label>
                            <select
                                name="lang"
                                id="lang"
                                class="form-select language-select"
                                onchange="this.form.submit()">
                                <option value="es" ${activeLocale eq 'es' ? 'selected' : ''}>${languageOptionEs}</option>
                                <option value="en" ${activeLocale eq 'en' ? 'selected' : ''}>${languageOptionEn}</option>
                                <option value="fr" ${activeLocale eq 'fr' ? 'selected' : ''}>${languageOptionFr}</option>
                            </select>
                        </form>
                        <div class="d-flex flex-column flex-lg-row gap-2 align-items-lg-center mt-3 mt-lg-0">
                            <c:choose>
                                <c:when test="${not empty sessionScope.currentEmployee}">
                                    <form method="post" action="${ctx}/logout" class="d-flex">
                                        <button type="submit" class="btn btn-outline-danger"><fmt:message key="layout.logout" /></button>
                                    </form>
                                </c:when>
                                <c:when test="${not empty sessionScope.currentUser}">
                                    <a class="btn btn-primary" href="${ctx}/private/profile"><fmt:message key="layout.profile" /></a>
                                    <a class="btn btn-outline-primary" href="${ctx}/logout"><fmt:message key="layout.logout" /></a>
                                </c:when>
                                <c:otherwise>
                                    <a class="btn btn-outline-primary" href="${ctx}/public/login"><fmt:message key="layout.login" /></a>
                                    <a class="btn btn-primary" href="${ctx}/public/users/register"><fmt:message key="layout.register" /></a>
                                </c:otherwise>
                            </c:choose>
                            <c:if test="${not empty headerCartVehicle}">
                                <a class="btn btn-warning d-flex align-items-center gap-2 overflow-hidden" href="${ctx}/public/reservations">
                                    <span aria-hidden="true">&#128722;</span>
                                    <span class="text-truncate">
                                        <fmt:message key="vehicle.catalog.cart.view" />
                                        <c:if test="${not empty headerCartVehicle.brand}">
                                            ·
                                            <c:out value="${headerCartVehicle.brand}" />
                                            <c:if test="${not empty headerCartVehicle.model}">
                                                <c:out value=" ${headerCartVehicle.model}" />
                                            </c:if>
                                        </c:if>
                                    </span>
                                </a>
                            </c:if>
                        </div>
                    </div>
                </div>
            </div>
        </nav>
    </header>
    <main class="flex-fill">
