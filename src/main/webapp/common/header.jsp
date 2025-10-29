<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<fmt:setLocale value="${not empty sessionScope.appLocale ? sessionScope.appLocale : pageContext.request.locale}" scope="session" />
<fmt:setBundle basename="i18n.Messages" />
<c:set var="ctx" value="${pageContext.request.contextPath}" />
<c:set var="htmlLang" value="es" />
<c:if test="${empty sessionScope.appLocale and not empty cookie.appLocale.value}">
    <c:set var="htmlLang" value="${cookie.appLocale.value}" />
</c:if>
<c:if test="${not empty sessionScope.appLocale}">
    <c:set var="htmlLang" value="${sessionScope.appLocale}" />
</c:if>
<c:if test="${empty sessionScope.appLocale and empty cookie.appLocale.value and not empty pageContext.request.locale.language}">
    <c:set var="htmlLang" value="${pageContext.request.locale.language}" />
</c:if>
<c:set var="resolvedTitle" value="${empty pageTitle ? 'RentExpress' : pageTitle}" />
<!DOCTYPE html>
<html lang="${htmlLang}">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>${resolvedTitle} · RentExpress</title>
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700&display=swap" rel="stylesheet">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet"
          integrity="sha384-QWTKZyjpPEjISv5WaRU9OFeRpok6YctnYmDr5pNlyT2bRjXh0JMhjY6hW+ALEwIH" crossorigin="anonymous">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.css" rel="stylesheet">
    <link href="https://cdn.jsdelivr.net/npm/flag-icons@6.6.6/css/flag-icons.min.css" rel="stylesheet">
    <link rel="stylesheet" href="${ctx}/css/main.css">
</head>
<body class="bg-light d-flex flex-column min-vh-100">
<header class="bg-white shadow-sm">
    <nav class="navbar navbar-expand-lg navbar-light container py-3">
        <a class="navbar-brand fw-bold text-brand" href="${ctx}/app/welcome">
            <i class="bi bi-geo-alt-fill me-2"></i><fmt:message key="nav.brand" />
        </a>
        <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarNav"
                aria-controls="navbarNav" aria-expanded="false" aria-label="Toggle navigation">
            <span class="navbar-toggler-icon"></span>
        </button>
        <div class="collapse navbar-collapse" id="navbarNav">
            <ul class="navbar-nav me-auto mb-2 mb-lg-0">
                <li class="nav-item"><a class="nav-link" href="${ctx}/app/welcome"><fmt:message key="nav.home" /></a></li>
                <li class="nav-item"><a class="nav-link" href="${ctx}/public/vehicles"><fmt:message key="nav.catalog" /></a></li>
                <li class="nav-item"><a class="nav-link" href="${ctx}/public/rentals"><fmt:message key="nav.rentals" /></a></li>
            </ul>
            <div class="d-flex flex-wrap align-items-center gap-3">
                <c:set var="currentFlag" value="fi-es" />
                <c:choose>
                    <c:when test="${htmlLang eq 'en'}">
                        <c:set var="currentFlag" value="fi-gb" />
                    </c:when>
                    <c:when test="${htmlLang eq 'fr'}">
                        <c:set var="currentFlag" value="fi-fr" />
                    </c:when>
                </c:choose>
                <form class="d-flex align-items-center gap-2 language-switcher" method="post" action="${ctx}/app/settings/language">
                    <span class="text-muted small fw-semibold text-uppercase"><fmt:message key="nav.language.label" /></span>
                    <span class="fi ${currentFlag} rounded-circle"></span>
                    <select class="form-select form-select-sm language-select" name="lang" onchange="this.form.submit()">
                        <option value="es" ${htmlLang eq 'es' ? 'selected' : ''}><fmt:message key="nav.language.es" /></option>
                        <option value="en" ${htmlLang eq 'en' ? 'selected' : ''}><fmt:message key="nav.language.en" /></option>
                        <option value="fr" ${htmlLang eq 'fr' ? 'selected' : ''}><fmt:message key="nav.language.fr" /></option>
                    </select>
                    <noscript>
                        <button type="submit" class="btn btn-sm btn-outline-secondary">
                            <fmt:message key="nav.language.submit" />
                        </button>
                    </noscript>
                </form>
                <div class="d-flex gap-2">
                    <a class="btn btn-outline-brand" href="${ctx}/app/auth/login"><fmt:message key="nav.login" /></a>
                    <a class="btn btn-brand" href="${ctx}/app/users/register"><fmt:message key="nav.register" /></a>
                </div>
            </div>
        </div>
    </nav>
</header>
<main class="container my-4 flex-grow-1">
