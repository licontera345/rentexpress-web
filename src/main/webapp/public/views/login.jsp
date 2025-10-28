<%@ include file="/common/header.jsp" %>
<div class="row justify-content-center">
    <div class="col-lg-6">
        <div class="card card-common">
            <div class="card-header">Iniciar sesión</div>
            <div class="card-body p-4">
                <p class="text-muted">Usa la cuenta demo <code>demo@rentexpress.com</code> con contraseña
                    <code>RentExpress123</code> para probar la aplicación.</p>
                <c:if test="${not empty flashSuccess}">
                    <div class="alert alert-success">${flashSuccess}</div>
                </c:if>
                <c:if test="${not empty flashError}">
                    <div class="alert alert-danger">${flashError}</div>
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
                    <div class="d-flex justify-content-between align-items-center">
                        <button type="submit" class="btn btn-brand">Entrar</button>
                        <a href="${ctx}/app/users/register" class="text-decoration-none">¿No tienes cuenta? Regístrate</a>
                    </div>
                </form>
            </div>
        </div>
    </div>
</div>
<%@ include file="/common/footer.jsp" %>
