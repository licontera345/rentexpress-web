<%@ include file="/common/header.jsp" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="employee" value="${selectedEmployee}" />
<section class="mb-4">
    <div class="d-flex justify-content-between align-items-center mb-3">
        <h1 class="h3 mb-0">Perfil del colaborador</h1>
        <a href="${ctx}/public/employees" class="btn btn-outline-secondary"><i class="bi bi-arrow-left"></i> Volver</a>
    </div>
    <div class="row g-4">
        <div class="col-lg-8">
            <div class="card card-common">
                <div class="card-header">Datos profesionales</div>
                <div class="card-body">
                    <dl class="row mb-0">
                        <dt class="col-sm-4">Nombre completo</dt>
                        <dd class="col-sm-8">
                            <c:choose>
                                <c:when test="${not empty employee.firstName}">
                                    ${employee.firstName} ${employee.lastName1} ${employee.lastName2}
                                </c:when>
                                <c:otherwise>
                                    ${employee.employeeName}
                                </c:otherwise>
                            </c:choose>
                        </dd>
                        <dt class="col-sm-4">Correo</dt>
                        <dd class="col-sm-8">${employee.email}</dd>
                        <dt class="col-sm-4">Teléfono</dt>
                        <dd class="col-sm-8">${empty employee.phone ? 'No informado' : employee.phone}</dd>
                        <dt class="col-sm-4">Rol</dt>
                        <dd class="col-sm-8">${empty selectedEmployeeRole ? 'Sin rol' : selectedEmployeeRole}</dd>
                        <dt class="col-sm-4">Sede</dt>
                        <dd class="col-sm-8">${empty selectedEmployeeHeadquarters ? 'No asignada' : selectedEmployeeHeadquarters}</dd>
                        <dt class="col-sm-4">Estado</dt>
                        <dd class="col-sm-8">
                            <span class="badge ${employee.activeStatus ? 'bg-success-subtle text-success' : 'bg-danger-subtle text-danger'}">
                                ${employee.activeStatus ? 'Activo' : 'Inactivo'}
                            </span>
                        </dd>
                        <dt class="col-sm-4">Alta</dt>
                        <dd class="col-sm-8">
                            <c:choose>
                                <c:when test="${not empty employee.createdAt}">${employee.createdAt}</c:when>
                                <c:otherwise>-</c:otherwise>
                            </c:choose>
                        </dd>
                        <dt class="col-sm-4">Actualización</dt>
                        <dd class="col-sm-8">
                            <c:choose>
                                <c:when test="${not empty employee.updatedAt}">${employee.updatedAt}</c:when>
                                <c:otherwise>-</c:otherwise>
                            </c:choose>
                        </dd>
                    </dl>
                </div>
            </div>
        </div>
        <div class="col-lg-4">
            <div class="card card-common">
                <div class="card-header">Ideas para tu entrega</div>
                <div class="card-body">
                    <p class="text-muted">En la zona privada puedes habilitar acciones sobre el empleado: reactivar, cambiar
                        de sede o asignar roles. El middleware ya expone los métodos necesarios en `EmployeeService`.</p>
                    <ul class="list-unstyled small mb-0">
                        <li class="mb-2"><i class="bi bi-check-circle text-success"></i> Construye un formulario de edición
                            reutilizando este DTO.</li>
                        <li class="mb-2"><i class="bi bi-check-circle text-success"></i> Muestra el histórico de sedes usando
                            tu capa DAO.</li>
                        <li><i class="bi bi-check-circle text-success"></i> Integra notificaciones por correo para cambios de
                            rol.</li>
                    </ul>
                </div>
            </div>
            <jsp:include page="/public/employee/employee_form.jsp" />
        </div>
    </div>
</section>
<%@ include file="/common/footer.jsp" %>
