<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ include file="/common/header.jsp" %>
<div class="row justify-content-center">
    <div class="col-lg-7">
        <div class="card shadow-sm border-0">
            <div class="card-body p-5 text-center">
                <i class="bi bi-lock-fill display-4 text-danger mb-3"></i>
                <h1 class="h3 fw-bold mb-2"><fmt:message key="error.accessDenied.title" /></h1>
                <p class="text-muted"><fmt:message key="error.accessDenied.message" /></p>
                <div class="d-flex justify-content-center gap-2">
                    <a class="btn btn-outline-brand" href="${pageContext.request.contextPath}/app/auth/login"><fmt:message key="error.accessDenied.login" /></a>
                    <a class="btn btn-brand" href="${pageContext.request.contextPath}/public/home"><fmt:message key="error.accessDenied.home" /></a>
                </div>
            </div>
        </div>
    </div>
</div>
<%@ include file="/common/footer.jsp" %>
