<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ include file="/common/header.jsp" %>
<div class="row justify-content-center">
    <div class="col-lg-8">
        <div class="card shadow-sm mb-4">
            <div class="card-body p-4">
                <h1 class="h3 fw-bold mb-3">Perfil del empleado</h1>
                <p class="text-muted">En esta sección podrás consultar y actualizar los datos clave del personal de RentExpress.</p>
                <div class="alert alert-info" role="alert">
                    <strong>Próximamente:</strong> vincularemos este panel con el middleware corporativo para mostrar los datos reales del empleado en sesión.
                </div>
                <div class="row g-3">
                    <div class="col-md-6">
                        <label class="form-label">Nombre y apellidos</label>
                        <input type="text" class="form-control" value="${not empty sessionScope.currentEmployee ? sessionScope.currentEmployee.fullName : 'Pendiente de integración'}" disabled>
                    </div>
                    <div class="col-md-6">
                        <label class="form-label">Correo</label>
                        <input type="email" class="form-control" value="${not empty sessionScope.currentEmployee ? sessionScope.currentEmployee.email : 'empleado@rentexpress.com'}" disabled>
                    </div>
                    <div class="col-md-6">
                        <label class="form-label">Rol interno</label>
                        <input type="text" class="form-control" value="${not empty sessionScope.currentEmployee ? sessionScope.currentEmployee.role : 'Sin asignar'}" disabled>
                    </div>
                    <div class="col-md-6">
                        <label class="form-label">Sede</label>
                        <input type="text" class="form-control" value="${not empty sessionScope.currentEmployee ? sessionScope.currentEmployee.headquarters : 'Pendiente'}" disabled>
                    </div>
                </div>
                <p class="mt-4 mb-0 text-muted small">Recuerda que cualquier cambio manual debe comunicarse al departamento de RRHH.</p>
            </div>
        </div>
        <a class="btn btn-outline-brand" href="${pageContext.request.contextPath}/app/rentals/private">
            <i class="bi bi-arrow-left"></i> Volver al panel interno
        </a>
    </div>
</div>
<%@ include file="/common/footer.jsp" %>
