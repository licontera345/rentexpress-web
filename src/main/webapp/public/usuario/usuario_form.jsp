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
    <title>
        <c:choose>
            <c:when test="${empty usuario}">
                <fmt:message key="usuario.create.title" />
            </c:when>
            <c:otherwise>
                <fmt:message key="usuario.edit.title" />
            </c:otherwise>
        </c:choose>
    </title>
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
                    <div class="card card-common h-100">
                        <div class="card-body">
                            <div class="d-flex flex-column align-items-center text-center">
                                <div class="avatar-wrapper mb-3">
                                    <img src="https://ui-avatars.com/api/?background=66b2ff&color=fff&name=<c:out value='${usuario.nombreUsuario}'/>" alt="avatar">
                                    <c:if test="${not empty usuario}">
                                        <a href="${pageContext.request.contextPath}/public/UsuarioServlet?action=detail&id=${usuario.id}" class="avatar-edit-btn" title="<fmt:message key='profile.avatar.change' />">
                                            <i class="bi bi-camera"></i>
                                        </a>
                                    </c:if>
                                </div>
                                <h5 class="fw-semibold mb-1"><c:out value="${usuario.nombreUsuario != null ? usuario.nombreUsuario : 'Usuario RentExpress'}" /></h5>
                                <p class="text-muted small mb-3"><c:out value="${usuario.email != null ? usuario.email : ''}" /></p>
                                <div class="w-100 bg-light rounded-3 p-3 text-start">
                                    <p class="fw-semibold mb-1"><fmt:message key="profile.status.complete" /></p>
                                    <p class="text-muted small mb-0"><fmt:message key="profile.status.complete.desc" /></p>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>

                <div class="col-lg-8">
                    <div class="card card-common">
                        <div class="card-body p-4">
                            <h3 class="fw-bold mb-4">
                                <c:choose>
                                    <c:when test="${empty usuario}">
                                        <fmt:message key="usuario.create.title" />
                                    </c:when>
                                    <c:otherwise>
                                        <fmt:message key="usuario.edit.title" />
                                    </c:otherwise>
                                </c:choose>
                            </h3>

                            <form action="${pageContext.request.contextPath}/public/UsuarioServlet" method="post" class="needs-validation" novalidate>
                                <input type="hidden" name="action" value="${empty usuario ? 'save' : 'update'}" />
                                <c:if test="${not empty usuario}">
                                    <input type="hidden" name="id" value="${usuario.id}" />
                                </c:if>

                                <div class="row g-4">
                                    <div class="col-12">
                                        <div class="form-section">
                                            <p class="form-section-title"><fmt:message key="profile.section.personal" /></p>
                                            <div class="row g-3">
                                                <div class="col-md-6">
                                                    <label for="nombreUsuario" class="form-label"><fmt:message key="usuario.detail.name" /></label>
                                                    <input type="text" class="form-control" id="nombreUsuario" name="nombreUsuario" value="${usuario.nombreUsuario}" required>
                                                    <div class="invalid-feedback">
                                                        <fmt:message key="usuario.detail.name" />
                                                    </div>
                                                </div>
                                                <div class="col-md-6">
                                                    <label for="documento" class="form-label"><fmt:message key="profile.documentId" /></label>
                                                    <input type="text" class="form-control" id="documento" name="documento" placeholder="12345678X">
                                                </div>
                                                <div class="col-md-6">
                                                    <label for="fechaNacimiento" class="form-label"><fmt:message key="profile.birthDate" /></label>
                                                    <input type="date" class="form-control" id="fechaNacimiento" name="fechaNacimiento">
                                                </div>
                                                <div class="col-md-6">
                                                    <label for="telefono" class="form-label"><fmt:message key="profile.phone" /></label>
                                                    <input type="tel" class="form-control" id="telefono" name="telefono" placeholder="+34 600 000 000">
                                                </div>
                                            </div>
                                        </div>
                                    </div>

                                    <div class="col-12">
                                        <div class="form-section">
                                            <p class="form-section-title"><fmt:message key="profile.section.contact" /></p>
                                            <div class="row g-3">
                                                <div class="col-md-12">
                                                    <label for="email" class="form-label"><fmt:message key="usuario.detail.email" /></label>
                                                    <input type="email" class="form-control" id="email" name="email" value="${usuario.email}" required>
                                                    <div class="invalid-feedback">
                                                        <fmt:message key="usuario.detail.email" />
                                                    </div>
                                                </div>
                                                <div class="col-md-12">
                                                    <label for="direccion" class="form-label"><fmt:message key="profile.address" /></label>
                                                    <input type="text" class="form-control" id="direccion" name="direccion" placeholder="Calle Mayor, 15">
                                                </div>
                                            </div>
                                        </div>
                                    </div>

                                    <div class="col-12">
                                        <div class="form-section">
                                            <p class="form-section-title"><fmt:message key="profile.section.location" /></p>
                                            <div class="row g-3">
                                                <div class="col-md-6">
                                                    <label for="provincia" class="form-label"><fmt:message key="profile.province" /></label>
                                                    <select class="form-select" id="provincia" name="provincia">
                                                        <option value="" selected disabled>--</option>
                                                    </select>
                                                </div>
                                                <div class="col-md-6">
                                                    <label for="localidad" class="form-label"><fmt:message key="profile.city" /></label>
                                                    <select class="form-select" id="localidad" name="localidad" disabled>
                                                        <option value=""><fmt:message key="profile.locale.select" /></option>
                                                    </select>
                                                </div>
                                                <div class="col-md-6">
                                                    <label for="codigoPostal" class="form-label"><fmt:message key="profile.postalCode" /></label>
                                                    <input type="text" class="form-control" id="codigoPostal" name="codigoPostal" placeholder="28013">
                                                </div>
                                                <div class="col-md-6">
                                                    <div class="form-check mt-4 pt-2">
                                                        <input class="form-check-input" type="checkbox" id="newsletter" name="newsletter">
                                                        <label class="form-check-label" for="newsletter">
                                                            <fmt:message key="profile.newsletter" />
                                                        </label>
                                                    </div>
                                                </div>
                                            </div>
                                        </div>
                                    </div>
                                </div>

                                <div class="d-flex flex-wrap gap-3 mt-4">
                                    <button type="submit" class="btn btn-brand px-4"><fmt:message key="profile.save" /></button>
                                    <a href="${pageContext.request.contextPath}/public/UsuarioServlet?action=list" class="btn btn-outline-brand px-4"><fmt:message key="action.cancel" /></a>
                                </div>
                            </form>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </main>

    <%@ include file="/common/footer.jsp"%>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js" integrity="sha384-YvpcrYf0tY3lHB60NNkmXc5s9fDVZLESaAA55NDzOxhy9GkcIdslK1eN7N6jIeHz" crossorigin="anonymous"></script>
    <script>
        (function () {
            'use strict';
            const forms = document.querySelectorAll('.needs-validation');
            Array.from(forms).forEach(form => {
                form.addEventListener('submit', event => {
                    if (!form.checkValidity()) {
                        event.preventDefault();
                        event.stopPropagation();
                    }
                    form.classList.add('was-validated');
                }, false);
            });
        })();

        const locationData = {
            "A Coruña": ["A Coruña", "Ferrol", "Santiago de Compostela"],
            "Madrid": ["Madrid", "Alcalá de Henares", "Getafe"],
            "Barcelona": ["Barcelona", "Hospitalet de Llobregat", "Badalona"],
            "Valencia": ["Valencia", "Gandía", "Torrent"],
            "Sevilla": ["Sevilla", "Dos Hermanas", "Alcalá de Guadaíra"]
        };

        const provinciaSelect = document.getElementById('provincia');
        const localidadSelect = document.getElementById('localidad');

        if (provinciaSelect && localidadSelect) {
            const fragment = document.createDocumentFragment();
            Object.keys(locationData).forEach(provincia => {
                const option = document.createElement('option');
                option.value = provincia;
                option.textContent = provincia;
                fragment.appendChild(option);
            });
            provinciaSelect.appendChild(fragment);

            provinciaSelect.addEventListener('change', () => {
                const provincias = locationData[provinciaSelect.value] || [];
                localidadSelect.innerHTML = '';
                const defaultOption = document.createElement('option');
                defaultOption.value = '';
                defaultOption.textContent = provincias.length === 0 ? '' : '—';
                localidadSelect.appendChild(defaultOption);
                provincias.forEach(ciudad => {
                    const opt = document.createElement('option');
                    opt.value = ciudad;
                    opt.textContent = ciudad;
                    localidadSelect.appendChild(opt);
                });
                localidadSelect.disabled = provincias.length === 0;
            });
        }
    </script>
</body>
</html>
