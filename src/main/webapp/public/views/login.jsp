<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ include file="/common/header.jsp" %>
<div class="row justify-content-center">
    <div class="col-lg-6">
        <div class="card card-common">
            <div class="card-header">Iniciar sesión</div>
            <div class="card-body p-4">
                <p class="text-muted">Introduce tus credenciales oficiales para acceder a la plataforma y gestionar tus
                    reservas.</p>
                <c:if test="${alreadyAuthenticated}">
                    <div class="alert alert-warning d-flex flex-column flex-md-row align-items-md-center gap-2 justify-content-between">
                        <span>Ya tienes una sesión activa como <strong>${rememberedEmail}</strong>. Si quieres continuar con esa cuenta, regresa a la página principal.</span>
                        <a class="btn btn-sm btn-outline-brand" href="${ctx}/app/welcome">Ir a la portada</a>
                    </div>
                </c:if>
                <c:if test="${not empty flashSuccess}">
                    <div class="alert alert-success">${flashSuccess}</div>
                </c:if>
                <c:if test="${not empty flashError}">
                    <div class="alert alert-danger">${flashError}</div>
                </c:if>
                <c:if test="${not empty flashInfo}">
                    <div class="alert alert-info">${flashInfo}</div>
                </c:if>
                <c:if test="${not empty error}">
                    <div class="alert alert-danger">
                        <c:forEach var="entry" items="${error.entrySet()}">
                            <div class="mb-1">${entry.value}</div>
                        </c:forEach>
                    </div>
                </c:if>
                <form method="post" action="${ctx}/app/auth/login" class="needs-validation" novalidate>
                    <div class="mb-3">
                        <label for="email" class="form-label">Correo electrónico</label>
                        <input type="email" class="form-control" id="email" name="email" required
                               value="${not empty rememberedEmail ? rememberedEmail : ''}">
                    </div>
                    <div class="mb-3">
                        <label for="password" class="form-label">Contraseña</label>
                        <input type="password" class="form-control" id="password" name="password" required>
                    </div>
                    <div class="form-check mb-3">
                        <input class="form-check-input" type="checkbox" value="on" id="remember" name="remember"
                                ${not empty rememberedEmail ? 'checked' : ''}>
                        <label class="form-check-label" for="remember">
                            Recordarme en este equipo
                        </label>
                    </div>
                    <div class="d-flex justify-content-between align-items-center flex-wrap gap-2">
                        <button type="submit" class="btn btn-brand">Entrar</button>
                        <div class="text-end">
                            <a href="${ctx}/app/users/register" class="text-decoration-none d-block">¿No tienes cuenta? Regístrate</a>
                            <a href="${ctx}/app/password/forgot" class="text-decoration-none small">¿Olvidaste tu contraseña?</a>
                        </div>
                    </div>
                </form>
            </div>
        </div>
    </div>
</div>
<%@ include file="/common/footer.jsp" %>
