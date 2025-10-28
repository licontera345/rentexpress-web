<%@ include file="/common/header.jsp" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="user" value="${selectedUser}" />
<section class="mb-4">
    <div class="d-flex justify-content-between align-items-center mb-3">
        <h1 class="h3 mb-0">Ficha de usuario</h1>
        <a href="${ctx}/public/users" class="btn btn-outline-secondary"><i class="bi bi-arrow-left"></i> Volver</a>
    </div>
    <div class="row g-4">
        <div class="col-lg-8">
            <div class="card card-common">
                <div class="card-header">Información principal</div>
                <div class="card-body">
                    <dl class="row mb-0">
                        <dt class="col-sm-4">Nombre completo</dt>
                        <dd class="col-sm-8">
                            <c:choose>
                                <c:when test="${not empty user.firstName}">
                                    ${user.firstName} ${user.lastName1} ${user.lastName2}
                                </c:when>
                                <c:otherwise>
                                    ${user.username}
                                </c:otherwise>
                            </c:choose>
                        </dd>
                        <dt class="col-sm-4">Correo electrónico</dt>
                        <dd class="col-sm-8">${user.email}</dd>
                        <dt class="col-sm-4">Teléfono</dt>
                        <dd class="col-sm-8">${empty user.phone ? 'No informado' : user.phone}</dd>
                        <dt class="col-sm-4">Rol asignado</dt>
                        <dd class="col-sm-8">${empty selectedUserRole ? 'Sin rol asignado' : selectedUserRole}</dd>
                        <dt class="col-sm-4">Estado</dt>
                        <dd class="col-sm-8">
                            <span class="badge ${user.activeStatus ? 'bg-success-subtle text-success' : 'bg-danger-subtle text-danger'}">
                                ${user.activeStatus ? 'Activo' : 'Inactivo'}
                            </span>
                        </dd>
                        <dt class="col-sm-4">Fecha de alta</dt>
                        <dd class="col-sm-8">
                            <c:choose>
                                <c:when test="${not empty user.createdAt}">${user.createdAt}</c:when>
                                <c:otherwise>-</c:otherwise>
                            </c:choose>
                        </dd>
                        <dt class="col-sm-4">Última actualización</dt>
                        <dd class="col-sm-8">
                            <c:choose>
                                <c:when test="${not empty user.updatedAt}">${user.updatedAt}</c:when>
                                <c:otherwise>-</c:otherwise>
                            </c:choose>
                        </dd>
                    </dl>
                </div>
            </div>
        </div>
        <div class="col-lg-4">
            <div class="card card-common">
                <div class="card-header">Sugerencias académicas</div>
                <div class="card-body">
                    <p class="text-muted">Este detalle utiliza el servicio `UserService` del middleware. Puedes reutilizar
                        la lógica para construir vistas privadas o generar reportes descargables.</p>
                    <ul class="list-unstyled small mb-0">
                        <li class="mb-2"><i class="bi bi-check-circle text-success"></i> Filtra en memoria para aplicar
                            búsquedas combinadas.</li>
                        <li class="mb-2"><i class="bi bi-check-circle text-success"></i> Usa los DTO para poblar un formulario
                            de edición en la zona privada.</li>
                        <li><i class="bi bi-check-circle text-success"></i> Registra auditoría (createdAt/updatedAt) al
                            persistir cambios.</li>
                    </ul>
                </div>
            </div>
            <jsp:include page="/public/user/user_form.jsp" />
        </div>
    </div>
</section>
<%@ include file="/common/footer.jsp" %>
