<%@ include file="/common/header.jsp" %>
<div class="row justify-content-center">
    <div class="col-lg-6">
        <div class="card card-common">
            <div class="card-header">Verificación en dos pasos</div>
            <div class="card-body p-4">
                <p class="text-muted">Confirma tu acceso introduciendo el código temporal que enviamos a
                    <strong>${pendingEmail}</strong>.</p>
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
                <form method="post" action="${ctx}/app/auth/verify-2fa" class="needs-validation" novalidate>
                    <div class="mb-3">
                        <label for="code" class="form-label">Código de verificación</label>
                        <input type="text" class="form-control text-center fs-4" id="code" name="code" maxlength="6"
                               pattern="\\d{6}" required value="${not empty submittedCode ? submittedCode : ''}">
                        <div class="form-text">Introduce el código de 6 dígitos. Caduca en ${secondsRemaining} segundos.</div>
                    </div>
                    <div class="d-flex justify-content-between align-items-center">
                        <button type="submit" class="btn btn-brand">Confirmar acceso</button>
                        <div class="text-end">
                            <a href="${ctx}/app/auth/verify-2fa?resend=1" class="btn btn-link">Reenviar código</a>
                            <a href="${ctx}/app/auth/login" class="btn btn-link">Cancelar</a>
                        </div>
                    </div>
                </form>
            </div>
        </div>
    </div>
</div>
<%@ include file="/common/footer.jsp" %>
