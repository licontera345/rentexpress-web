<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ include file="/common/header.jsp" %>
<div class="row justify-content-center">
    <div class="col-lg-7">
        <div class="card card-common mb-4">
            <div class="card-header">Crear una cuenta de cliente</div>
            <div class="card-body p-4">
                <p class="text-muted">Completa tus datos personales para habilitar el acceso a RentExpress y gestionar reservas reales en cualquiera de nuestras sedes.</p>
                <c:if test="${not empty errors}">
                    <div class="alert alert-danger">
                        <ul class="mb-0">
                            <c:forEach var="error" items="${errors}">
                                <li>${error}</li>
                            </c:forEach>
                        </ul>
                    </div>
                </c:if>
                <form method="post" action="${ctx}/app/users/register" class="row g-3">
                    <div class="col-12">
                        <label for="fullName" class="form-label">Nombre completo</label>
                        <input type="text" class="form-control" id="fullName" name="fullName" required
                               value="${not empty formData['fullName'] ? formData['fullName'] : ''}">
                    </div>
                    <div class="col-md-6">
                        <label for="email" class="form-label">Correo electrónico</label>
                        <input type="email" class="form-control" id="email" name="email" required
                               value="${not empty formData['email'] ? formData['email'] : ''}">
                    </div>
                    <div class="col-md-6">
                        <label for="phone" class="form-label">Teléfono</label>
                        <input type="tel" class="form-control" id="phone" name="phone"
                               placeholder="Opcional" value="${not empty formData['phone'] ? formData['phone'] : ''}">
                    </div>
                    <div class="col-12">
                        <label for="password" class="form-label">Contraseña</label>
                        <input type="password" class="form-control" id="password" name="password" required>
                        <small class="text-muted">Utiliza una contraseña robusta con al menos 8 caracteres, combinando letras, números y símbolos.</small>
                    </div>
                    <div class="col-12">
                        <div class="form-check">
                            <input class="form-check-input" type="checkbox" value="on" id="acceptTerms" name="acceptTerms"
                                   required>
                            <label class="form-check-label" for="acceptTerms">
                                Acepto los términos y condiciones de RentExpress.
                            </label>
                        </div>
                    </div>
                    <div class="col-12 d-flex justify-content-between align-items-center">
                        <button type="submit" class="btn btn-brand">Registrar</button>
                        <a href="${ctx}/app/auth/login" class="text-decoration-none">Ya tengo una cuenta</a>
                    </div>
                </form>
            </div>
        </div>
    </div>
    <div class="col-lg-5">
        <div class="card card-common">
            <div class="card-header">Últimos clientes registrados</div>
            <div class="card-body">
                <c:choose>
                    <c:when test="${empty registeredUsers}">
                        <p class="text-muted mb-0">Todavía no hay clientes registrados. Completa el formulario para dar de alta el primero.</p>
                    </c:when>
                    <c:otherwise>
                        <ul class="list-group list-group-flush">
                            <c:forEach var="user" items="${registeredUsers}">
                                <li class="list-group-item">
                                    <div class="d-flex justify-content-between">
                                        <span class="fw-semibold">${user.fullName}</span>
                                        <small class="text-muted">${user.registeredAt}</small>
                                    </div>
                                    <div class="text-muted small">${user.email}
                                        <c:if test="${not empty user.phone}">
                                            · ${user.phone}
                                        </c:if>
                                    </div>
                                </li>
                            </c:forEach>
                        </ul>
                    </c:otherwise>
                </c:choose>
            </div>
        </div>
    </div>
</div>
<%@ include file="/common/footer.jsp" %>
