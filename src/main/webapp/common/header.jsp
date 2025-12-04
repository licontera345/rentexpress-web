<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c"  uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@ taglib prefix="fn"  uri="jakarta.tags.functions" %>

<c:set var="renderLayoutShell" value="${renderLayoutShell == false ? false : true}" />

<%-- Contexto y i18n --%>
<c:set var="ctx" value="${pageContext.request.contextPath}" />
<c:set var="activeLocale" value="${sessionScope.appLocale}" />
<c:if test="${empty activeLocale}">
    <c:set var="activeLocale" value="es" />
</c:if>
<fmt:setLocale value="${activeLocale}" scope="session" />
<fmt:setBundle basename="i18n.Messages" scope="session" />
<c:set var="emitDocument" value="${requestScope.skipDocumentLayout ne true}" scope="request" />

<%-- Ruta actual para resaltar menú --%>
<c:set var="currentPath" value="${pageContext.request.requestURI}" />

<%-- Flags de navegación (evita expresiones largas en atributos) --%>
<c:set var="isHome"
       value="${fn:endsWith(currentPath, '/public/index') or fn:endsWith(currentPath, '/public/index.jsp')}" />
<c:set var="isCat"
       value="${fn:contains(currentPath, '/public/VehicleServlet')}" />
<c:set var="isProf"
       value="${fn:contains(currentPath, '/private/profile')}" />
<c:set var="isDash"
       value="${fn:contains(currentPath, '/private/dashboard')}" />
<c:set var="isEmp"
       value="${fn:contains(currentPath, '/private/EmployeeServlet') or fn:contains(currentPath, '/private/employee')}" />
<c:set var="isVeh"
       value="${fn:contains(currentPath, '/private/VehicleServlet') or fn:contains(currentPath, '/private/vehicle')}" />

<c:if test="${emitDocument}">
    <!DOCTYPE html>
    <html lang="${activeLocale}">
    <head>
        <meta charset="UTF-8" />
        <meta name="viewport" content="width=device-width, initial-scale=1" />
        <title><fmt:message key="layout.appName" /></title>

        <link rel="preconnect" href="https://fonts.googleapis.com" />
        <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin />
        <link href="https://fonts.googleapis.com/css2?family=Plus+Jakarta+Sans:wght@400;500;600;700&display=swap" rel="stylesheet" />
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css"
              rel="stylesheet"
              integrity="sha384-QWTKZyjpPEjISv5WaRU9OFeRpok6YctnYmDr5pNlyT2bRjXh0JMhjY6hW+ALEwIH"
              crossorigin="anonymous" />
        <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css"
              rel="stylesheet" crossorigin="anonymous" />
        <link rel="stylesheet" href="<c:url value='/css/main.css' />" />
    </head>
    <body class="d-flex flex-column min-vh-100">
</c:if>

<header class="shadow-sm border-bottom bg-white">
    <nav class="navbar navbar-expand-lg navbar-light py-3">
        <div class="container">
            <a class="navbar-brand d-flex align-items-center gap-2" href="${ctx}/public/index">
                <span class="brand-symbol flex-shrink-0">RE</span>
                <span class="fw-semibold text-uppercase"><fmt:message key="layout.appName" /></span>
            </a>

            <span class="d-none d-lg-inline text-muted small ms-lg-3">
                <fmt:message key="common.home.hero.subtitle" />
            </span>

            <button class="navbar-toggler" type="button" data-bs-toggle="collapse"
                    data-bs-target="#mainNavbar" aria-controls="mainNavbar"
                    aria-expanded="false" aria-label="Toggle navigation">
                <span class="navbar-toggler-icon"></span>
            </button>

            <div class="collapse navbar-collapse" id="mainNavbar">
                <ul class="navbar-nav me-auto mb-2 mb-lg-0">
                    <li class="nav-item">
                        <a class="nav-link ${isHome ? 'active fw-semibold' : ''}"
                           aria-current="${isHome ? 'page' : ''}"
                           href="${ctx}/public/index">
                            <fmt:message key="layout.home" />
                        </a>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link ${isCat ? 'active fw-semibold' : ''}"
                           aria-current="${isCat ? 'page' : ''}"
                           href="${ctx}/public/VehicleServlet">
                            <fmt:message key="layout.vehicles" />
                        </a>
                    </li>

                    <c:if test="${not empty sessionScope.currentEmployee}">
                        <li class="nav-item">
                            <a class="nav-link ${isProf ? 'active fw-semibold' : ''}"
                               aria-current="${isProf ? 'page' : ''}"
                               href="${ctx}/private/profile">
                                <fmt:message key="layout.profile" />
                            </a>
                        </li>
                        <li class="nav-item">
                            <a class="nav-link ${isDash ? 'active fw-semibold' : ''}"
                               aria-current="${isDash ? 'page' : ''}"
                               href="${ctx}/private/dashboard.jsp">
                                <fmt:message key="layout.dashboard" />
                            </a>
                        </li>
                        <li class="nav-item">
                            <a class="nav-link ${isEmp ? 'active fw-semibold' : ''}"
                               aria-current="${isEmp ? 'page' : ''}"
                               href="${ctx}/private/EmployeeServlet">
                                <fmt:message key="layout.employees" />
                            </a>
                        </li>
                        <li class="nav-item">
                            <a class="nav-link ${isVeh ? 'active fw-semibold' : ''}"
                               aria-current="${isVeh ? 'page' : ''}"
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
                        <select name="lang" id="lang" class="form-select language-select" onchange="this.form.submit()">
                            <option value="es" ${activeLocale eq 'es' ? 'selected' : ''}>${languageOptionEs}</option>
                            <option value="en" ${activeLocale eq 'en' ? 'selected' : ''}>${languageOptionEn}</option>
                            <option value="fr" ${activeLocale eq 'fr' ? 'selected' : ''}>${languageOptionFr}</option>
                        </select>
                    </form>

                    <div class="d-flex flex-column flex-lg-row gap-2 align-items-lg-center mt-3 mt-lg-0">
                        <c:choose>
                            <c:when test="${not empty sessionScope.currentEmployee}">
                                <form method="post" action="${ctx}/logout" class="d-flex">
                                    <button type="submit" class="btn btn-outline-danger">
                                        <fmt:message key="layout.logout" />
                                    </button>
                                </form>
                            </c:when>
                            <c:when test="${not empty sessionScope.currentUser}">
                                <a class="btn btn-primary" href="${ctx}/private/profile">
                                    <fmt:message key="layout.profile" />
                                </a>
                                <a class="btn btn-outline-primary" href="${ctx}/logout">
                                    <fmt:message key="layout.logout" />
                                </a>
                            </c:when>
                            <c:otherwise>
                                <a class="btn btn-outline-primary" href="${ctx}/public/login">
                                    <fmt:message key="layout.login" />
                                </a>
                                <a class="btn btn-primary" href="${ctx}/public/users/register">
                                    <fmt:message key="layout.register" />
                                </a>
                            </c:otherwise>
                        </c:choose>
                    </div>
                </div>
            </div>
        </div>
    </nav>
</header>

<main class="flex-fill">
