<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ include file="/common/header.jsp" %>
<c:set var="errors" value="${resetPasswordErrors}" />
<c:set var="pendingEmail" value="${pendingResetEmail}" />
<div class="row justify-content-center">
    <div class="col-lg-6">
        <div class="card card-common">
            <div class="card-header">Define una contraseña nueva</div>
            <div class="card-body p-4">
                <p class="text-muted">Estás actualizando la contraseña de <strong>${pendingEmail}</strong>. Elige una
                    clave robusta para continuar.</p>
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
                        <c:forEach var="entry" items="${errors.entrySet()}">
                            <div class="mb-1">${entry.value}</div>
                        </c:forEach>
                    </div>
                </c:if>
                <form method="post" action="${ctx}/app/password/reset" class="needs-validation" novalidate>
                    <div class="mb-3">
                        <label for="newPassword" class="form-label">Nueva contraseña</label>
                        <input type="password" class="form-control" id="newPassword" name="newPassword" minlength="8" required>
                        <div class="form-text">Mínimo 8 caracteres. Usa mayúsculas, minúsculas y números.</div>
                    </div>
                    <div class="mb-3">
                        <label for="confirmPassword" class="form-label">Confirma la contraseña</label>
                        <input type="password" class="form-control" id="confirmPassword" name="confirmPassword" minlength="8" required>
                    </div>
                    <div class="d-flex justify-content-between align-items-center flex-wrap gap-2">
                        <button type="submit" class="btn btn-brand">Actualizar contraseña</button>
                        <a href="${ctx}/app/auth/login" class="text-decoration-none">Volver al inicio de sesión</a>
                    </div>
                </form>
            </div>
        </div>
    </div>
</div>
<%@ include file="/common/footer.jsp" %>
