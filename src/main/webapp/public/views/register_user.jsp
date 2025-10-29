<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ include file="/common/header.jsp" %>
<div class="row justify-content-center">
    <div class="col-lg-7">
        <div class="card card-common mb-4">
            <div class="card-header">Crear una cuenta de cliente</div>
            <div class="card-body p-4">
                <p class="text-muted">Completa tus datos personales para habilitar el acceso a RentExpress y gestionar reservas reales en cualquiera de nuestras sedes.</p>
                <c:if test="${not empty flashSuccess}">
                    <div class="alert alert-success">${flashSuccess}</div>
                </c:if>
                <c:if test="${not empty flashError}">
                    <div class="alert alert-danger">${flashError}</div>
                </c:if>
                <c:if test="${not empty flashInfo}">
                    <div class="alert alert-info">${flashInfo}</div>
                </c:if>
                <c:if test="${not empty errors}">
                    <div class="alert alert-danger">
                        <ul class="mb-0">
                            <c:forEach var="error" items="${errors}">
                                <li>${error}</li>
                            </c:forEach>
                        </ul>
                    </div>
                </c:if>
                <form method="post" action="${ctx}/app/users/register" class="row g-3 needs-validation" novalidate>
                    <div class="col-12">
                        <label for="fullName" class="form-label">Nombre completo</label>
                        <input type="text" class="form-control" id="fullName" name="fullName" maxlength="120" required
                               value="${not empty formData['fullName'] ? formData['fullName'] : ''}">
                        <div class="form-text">Debe coincidir con tu documento oficial de identidad.</div>
                    </div>
                    <div class="col-md-6">
                        <label for="email" class="form-label">Correo electrónico</label>
                        <input type="email" class="form-control" id="email" name="email" maxlength="120" required
                               autocomplete="email"
                               value="${not empty formData['email'] ? formData['email'] : ''}">
                        <div class="form-text">Será tu usuario para iniciar sesión y recibir confirmaciones.</div>
                    </div>
                    <div class="col-md-6">
                        <label for="phone" class="form-label">Teléfono</label>
                        <input type="tel" class="form-control" id="phone" name="phone" maxlength="20" placeholder="Opcional"
                               autocomplete="tel"
                               value="${not empty formData['phone'] ? formData['phone'] : ''}">
                        <div class="form-text">Lo usaremos solo si necesitamos confirmar detalles de tu reserva.</div>
                    </div>
                    <div class="col-12">
                        <label for="password" class="form-label">Contraseña</label>
                        <input type="password" class="form-control" id="password" name="password" minlength="8" required autocomplete="new-password">
                        <div class="form-text">Combina letras, números y símbolos para mayor seguridad.</div>
                    </div>
                    <div class="col-12">
                        <div class="form-check">
                            <input class="form-check-input" type="checkbox" value="on" id="acceptTerms" name="acceptTerms" required>
                            <label class="form-check-label" for="acceptTerms">
                                Acepto los <a href="${ctx}/public/terms.jsp" class="text-decoration-none">términos y condiciones</a> de RentExpress.
                            </label>
                        </div>
                    </div>
                    <div class="col-12 d-flex justify-content-between align-items-center flex-wrap gap-2">
                        <button type="submit" class="btn btn-brand">Registrar</button>
                        <a href="${ctx}/app/auth/login" class="text-decoration-none">Ya tengo una cuenta</a>
                    </div>
                </form>
            </div>
        </div>
        <div class="card border-0 bg-light">
            <div class="card-body">
                <h2 class="h5 fw-semibold mb-3">¿Por qué crear una cuenta?</h2>
                <ul class="list-unstyled mb-0 text-muted small">
                    <li class="mb-2"><i class="bi bi-check-circle-fill text-brand me-2"></i>Gestiona tus reservas y facturas desde un único panel.</li>
                    <li class="mb-2"><i class="bi bi-check-circle-fill text-brand me-2"></i>Recibe notificaciones de disponibilidad en tiempo real.</li>
                    <li class="mb-0"><i class="bi bi-check-circle-fill text-brand me-2"></i>Accede a promociones exclusivas para clientes registrados.</li>
                </ul>
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
                                    <div class="d-flex justify-content-between align-items-start">
                                        <div>
                                            <span class="fw-semibold d-block">${user.fullName}</span>
                                            <span class="text-muted small">${user.email}
                                                <c:if test="${not empty user.phone}">
                                                    · ${user.phone}
                                                </c:if>
                                            </span>
                                        </div>
                                        <span class="badge text-bg-light">${user.registeredAt}</span>
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
