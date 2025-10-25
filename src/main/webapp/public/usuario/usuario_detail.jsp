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
    <title><fmt:message key="usuario.detail.title" /></title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet" integrity="sha384-QWTKZyjpPEjISv5WaRU9OFeRpok6YctnYmDr5pNlyT2bRjXh0JMhjY6hW+ALEwIH" crossorigin="anonymous">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.css" rel="stylesheet">
    <link href="https://cdn.jsdelivr.net/npm/flag-icons@6.6.6/css/flag-icons.min.css" rel="stylesheet">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/main.css">
</head>
<body class="bg-light d-flex flex-column min-vh-100">
    <%@ include file="/common/header.jsp"%>

    <main class="flex-grow-1 py-5">
        <div class="container">
            <c:if test="${not empty usuario}">
                <div class="row g-4">
                    <div class="col-lg-4">
                        <div class="card card-common h-100">
                            <div class="card-body text-center">
                                <c:set var="PH_SVG" value="data:image/svg+xml;utf8,<svg xmlns='http://www.w3.org/2000/svg' width='240' height='240' viewBox='0 0 240 240'><rect width='100%' height='100%' fill='%23f2f2f2'/><circle cx='120' cy='90' r='44' fill='%23c2c2c2'/><rect x='40' y='150' width='160' height='60' rx='30' fill='%23c2c2c2'/></svg>" />
                                <div class="avatar-wrapper mb-3">
                                    <c:choose>
                                        <c:when test="${tieneImagen}">
                                            <img alt="Foto de usuario" loading="lazy" src="${pageContext.request.contextPath}/DownloadImageServlet?id=${usuario.id}">
                                        </c:when>
                                        <c:otherwise>
                                            <img alt="Placeholder" loading="lazy" src="${PH_SVG}">
                                        </c:otherwise>
                                    </c:choose>
                                    <form action="${pageContext.request.contextPath}/UploadImageServlet" method="post" enctype="multipart/form-data" class="avatar-edit-btn" title="<fmt:message key='profile.avatar.change' />">
                                        <input type="hidden" name="idUsuario" value="${usuario.id}" />
                                        <label for="avatarFile" class="text-white"><i class="bi bi-camera-fill"></i></label>
                                        <input type="file" id="avatarFile" name="imagen" accept=".jpg,.jpeg,.png" class="d-none" onchange="this.form.submit()" />
                                    </form>
                                </div>
                                <h4 class="fw-semibold mb-1"><c:out value="${usuario.nombreUsuario}" /></h4>
                                <p class="text-muted mb-3"><c:out value="${usuario.email}" /></p>
                                <div class="d-grid gap-2">
                                    <a href="${pageContext.request.contextPath}/public/UsuarioServlet?action=edit&id=${usuario.id}" class="btn btn-outline-brand">
                                        <i class="bi bi-pencil-square me-2"></i>
                                        <fmt:message key="action.edit" />
                                    </a>
                                    <a href="${pageContext.request.contextPath}/public/UsuarioServlet?action=list" class="btn btn-outline-secondary">
                                        <i class="bi bi-arrow-left-circle me-2"></i>
                                        <fmt:message key="back.index" />
                                    </a>
                                </div>
                            </div>
                        </div>
                    </div>

                    <div class="col-lg-8">
                        <div class="card card-common mb-4">
                            <div class="card-body">
                                <h3 class="fw-bold mb-3"><fmt:message key="usuario.detail.title" /></h3>
                                <div class="row g-3">
                                    <div class="col-md-6">
                                        <p class="text-muted mb-1"><fmt:message key="usuario.detail.id" /></p>
                                        <p class="fw-semibold mb-0">${usuario.id}</p>
                                    </div>
                                    <div class="col-md-6">
                                        <p class="text-muted mb-1"><fmt:message key="usuario.detail.name" /></p>
                                        <p class="fw-semibold mb-0"><c:out value="${usuario.nombreUsuario}" /></p>
                                    </div>
                                    <div class="col-md-6">
                                        <p class="text-muted mb-1"><fmt:message key="usuario.detail.email" /></p>
                                        <p class="fw-semibold mb-0"><c:out value="${usuario.email}" /></p>
                                    </div>
                                </div>
                            </div>
                        </div>

                        <div class="card card-common mb-4">
                            <div class="card-body">
                                <div class="d-flex justify-content-between align-items-center mb-3">
                                    <h4 class="fw-bold mb-0"><fmt:message key="reservations.active.title" /></h4>
                                    <span class="badge bg-brand">0</span>
                                </div>
                                <div class="reservation-card">
                                    <p class="text-muted mb-0"><fmt:message key="reservations.empty.active" /></p>
                                </div>
                            </div>
                        </div>

                        <div class="card card-common">
                            <div class="card-body">
                                <div class="d-flex justify-content-between align-items-center mb-3">
                                    <h4 class="fw-bold mb-0"><fmt:message key="reservations.history.title" /></h4>
                                    <span class="badge bg-secondary">0</span>
                                </div>
                                <div class="reservation-card inactive">
                                    <p class="text-muted mb-0"><fmt:message key="reservations.empty.history" /></p>
                                </div>
                            </div>
                        </div>

                        <c:if test="${not empty imagenes}">
                            <div class="card card-common mt-4">
                                <div class="card-body">
                                    <h5 class="fw-bold mb-3"><fmt:message key="usuario.detail.images" /></h5>
                                    <div class="row g-3">
                                        <c:forEach var="img" items="${imagenes}">
                                            <div class="col-6 col-md-4">
                                                <div class="ratio ratio-1x1 rounded-4 overflow-hidden shadow-sm">
                                                    <img alt="miniatura" loading="lazy" src="${pageContext.request.contextPath}/DownloadImageServlet?id=${usuario.id}" class="w-100 h-100 object-fit-cover">
                                                </div>
                                            </div>
                                        </c:forEach>
                                    </div>
                                </div>
                            </div>
                        </c:if>
                    </div>
                </div>
            </c:if>

            <c:if test="${empty usuario}">
                <div class="alert alert-danger d-flex align-items-center" role="alert">
                    <i class="bi bi-exclamation-octagon-fill me-3 fs-4"></i>
                    <div><fmt:message key="usuario.detail.notfound" /></div>
                </div>
            </c:if>
        </div>
    </main>

    <%@ include file="/common/footer.jsp"%>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js" integrity="sha384-YvpcrYf0tY3lHB60NNkmXc5s9fDVZLESaAA55NDzOxhy9GkcIdslK1eN7N6jIeHz" crossorigin="anonymous"></script>
</body>
</html>
