<%@ include file="/common/header.jsp" %>
<c:set var="errors" value="${forgotPasswordErrors}" />
<c:set var="email" value="${forgotPasswordEmail}" />
<div class="row justify-content-center">
    <div class="col-lg-6">
        <div class="card card-common">
            <div class="card-header">Recupera tu contraseña</div>
            <div class="card-body p-4">
                <p class="text-muted">Introduce el correo con el que te registraste. Te enviaremos un código temporal
                    (académico) para validar que eres el propietario de la cuenta.</p>
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
                <form method="post" action="${ctx}/app/password/forgot" class="needs-validation" novalidate>
                    <div class="mb-3">
                        <label for="email" class="form-label">Correo electrónico</label>
                        <input type="email" class="form-control" id="email" name="email" required
                               value="${not empty email ? email : ''}">
                        <div class="form-text">Si tienes dudas, contacta con el equipo de soporte de RentExpress.</div>
                    </div>
                    <div class="d-flex justify-content-between align-items-center flex-wrap gap-2">
                        <button type="submit" class="btn btn-brand">Enviar código</button>
                        <a href="${ctx}/app/auth/login" class="text-decoration-none">Volver al inicio de sesión</a>
                    </div>
                </form>
            </div>
        </div>
    </div>
</div>
<%@ include file="/common/footer.jsp" %>
