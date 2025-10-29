<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ include file="/common/header.jsp" %>
<c:set var="profile" value="${employeeProfile}" />
<div class="row justify-content-center">
    <div class="col-lg-8">
        <c:if test="${not empty flashSuccess}">
            <div class="alert alert-success shadow-soft mb-4">${flashSuccess}</div>
        </c:if>
        <c:if test="${not empty flashError}">
            <div class="alert alert-danger shadow-soft mb-4">${flashError}</div>
        </c:if>
        <c:if test="${not empty flashInfo}">
            <div class="alert alert-info shadow-soft mb-4">${flashInfo}</div>
        </c:if>

        <c:if test="${empty profile}">
            <div class="alert alert-warning shadow-soft mb-4">
                No se pudo cargar la información del empleado. Vuelve a iniciar sesión para sincronizar tus datos corporativos.
            </div>
        </c:if>

        <c:if test="${not empty profile}">
            <div class="card shadow-sm mb-4">
                <div class="card-body p-4">
                    <div class="d-flex justify-content-between align-items-start flex-wrap gap-3 mb-3">
                        <div>
                            <h1 class="h3 fw-bold mb-1">Perfil del empleado</h1>
                            <p class="text-muted mb-0">Información sincronizada con el directorio corporativo de RentExpress.</p>
                        </div>
                        <div class="text-end">
                            <span class="badge ${profile.statusStyle} px-3 py-2">${profile.statusLabel}</span>
                            <c:if test="${not empty profile.employeeId}">
                                <p class="text-muted small mb-0 mt-2">ID interno: #${profile.employeeId}</p>
                            </c:if>
                        </div>
                    </div>
                    <div class="row g-3">
                        <div class="col-md-6">
                            <label class="form-label">Nombre completo</label>
                            <input type="text" class="form-control" value="${profile.fullName}" disabled>
                        </div>
                        <div class="col-md-6">
                            <label class="form-label">Nombre en nómina</label>
                            <input type="text" class="form-control" value="${profile.accountName}" disabled>
                        </div>
                        <div class="col-md-6">
                            <label class="form-label">Correo corporativo</label>
                            <input type="email" class="form-control" value="${profile.email}" disabled>
                        </div>
                        <div class="col-md-6">
                            <label class="form-label">Teléfono</label>
                            <input type="text" class="form-control" value="${profile.phone}" disabled>
                        </div>
                        <div class="col-md-6">
                            <label class="form-label">Rol interno</label>
                            <input type="text" class="form-control" value="${profile.role}" disabled>
                        </div>
                        <div class="col-md-6">
                            <label class="form-label">Sede asignada</label>
                            <input type="text" class="form-control" value="${profile.headquarters}" disabled>
                            <c:if test="${not empty profile.headquartersLocation}">
                                <div class="form-text">${profile.headquartersLocation}</div>
                            </c:if>
                        </div>
                        <div class="col-md-6">
                            <label class="form-label">Contacto de la sede</label>
                            <input type="text" class="form-control" value="${profile.headquartersPhone}" disabled>
                            <div class="form-text">${profile.headquartersEmail}</div>
                        </div>
                        <div class="col-md-6">
                            <label class="form-label">Alta en sistema</label>
                            <input type="text" class="form-control" value="${profile.createdAt}" disabled>
                        </div>
                        <div class="col-md-6">
                            <label class="form-label">Última actualización</label>
                            <input type="text" class="form-control" value="${profile.updatedAt}" disabled>
                        </div>
                    </div>
                    <p class="mt-4 mb-0 text-muted small">Si detectas algún dato incorrecto, contacta con el departamento de RRHH para actualizarlo en el sistema maestro.</p>
                </div>
            </div>
        </c:if>

        <a class="btn btn-outline-brand" href="${pageContext.request.contextPath}/app/rentals/private">
            <i class="bi bi-arrow-left"></i> Volver al panel interno
        </a>
    </div>
</div>
<%@ include file="/common/footer.jsp" %>
