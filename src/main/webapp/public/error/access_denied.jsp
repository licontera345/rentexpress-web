<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ include file="/common/header.jsp" %>
<div class="row justify-content-center">
    <div class="col-lg-7">
        <div class="card shadow-sm border-0">
            <div class="card-body p-5 text-center">
                <i class="bi bi-lock-fill display-4 text-danger mb-3"></i>
                <h1 class="h3 fw-bold mb-2">Acceso restringido</h1>
                <p class="text-muted">No dispones de permisos para ver este contenido. Comprueba si has iniciado sesión con la cuenta correcta o contacta con un administrador.</p>
                <div class="d-flex justify-content-center gap-2">
                    <a class="btn btn-outline-brand" href="${pageContext.request.contextPath}/app/auth/login">Iniciar sesión</a>
                    <a class="btn btn-brand" href="${pageContext.request.contextPath}/app/welcome">Volver al inicio</a>
                </div>
            </div>
        </div>
    </div>
</div>
<%@ include file="/common/footer.jsp" %>
