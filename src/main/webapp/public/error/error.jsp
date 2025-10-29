<%@ page isErrorPage="true" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ include file="/common/header.jsp" %>
<div class="row justify-content-center">
    <div class="col-lg-8">
        <div class="card shadow-sm border-0">
            <div class="card-body p-5 text-center">
                <i class="bi bi-exclamation-triangle-fill display-4 text-warning mb-3"></i>
                <h1 class="h3 fw-bold mb-2">Lo sentimos, algo ha fallado</h1>
                <p class="text-muted">Se ha producido un error inesperado al procesar tu solicitud. Nuestro equipo ya ha sido notificado.</p>
                <c:if test="${not empty exception}">
                    <pre class="alert alert-light text-start overflow-auto small">${exception}</pre>
                </c:if>
                <a class="btn btn-brand mt-3" href="${pageContext.request.contextPath}/app/welcome">Volver al inicio</a>
            </div>
        </div>
    </div>
</div>
<%@ include file="/common/footer.jsp" %>
