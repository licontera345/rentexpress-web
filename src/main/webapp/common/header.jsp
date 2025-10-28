<%@ page contentType="text/html; charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}" />
<c:set var="resolvedTitle" value="${empty pageTitle ? 'RentExpress' : pageTitle}" />
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>${resolvedTitle} · RentExpress</title>
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css"
          integrity="sha384-QWTKZyjpPEjISv5WaRU9OFeRpok6YctnYmDr5pNlyT2bRjXh0JMhjY6hW+ALEwIH" crossorigin="anonymous">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css">
    <link rel="stylesheet" href="${ctx}/css/app.css">
</head>
<body>
<header class="py-3">
    <nav class="navbar navbar-expand-lg navbar-light bg-white shadow-sm rounded-4 container">
        <a class="navbar-brand fw-bold text-brand" href="${ctx}/app/welcome">RentExpress</a>
        <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarNav"
                aria-controls="navbarNav" aria-expanded="false" aria-label="Toggle navigation">
            <span class="navbar-toggler-icon"></span>
        </button>
        <div class="collapse navbar-collapse" id="navbarNav">
            <ul class="navbar-nav me-auto mb-2 mb-lg-0">
                <li class="nav-item"><a class="nav-link" href="${ctx}/app/welcome">Inicio</a></li>
                <li class="nav-item"><a class="nav-link" href="${ctx}/public/vehicle/catalog.jsp">Catálogo</a></li>
            </ul>
            <div class="d-flex gap-2">
                <a class="btn btn-outline-brand" href="${ctx}/app/auth/login">Iniciar sesión</a>
                <a class="btn btn-brand" href="${ctx}/app/users/register">Crear cuenta</a>
            </div>
        </div>
    </nav>
</header>
<main class="container my-4">
