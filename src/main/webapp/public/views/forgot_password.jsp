<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<fmt:message key="page.forgotPassword.title" var="forgotPasswordTitle" />
<c:set var="pageTitle" value="${forgotPasswordTitle}" />
<%@ include file="/common/header.jsp" %>
<c:set var="errors" value="${forgotPasswordErrors}" />
<c:set var="email" value="${forgotPasswordEmail}" />
<div class="row justify-content-center">
    <div class="col-lg-6">
        <div class="card card-common">
            <div class="card-header"><fmt:message key="page.forgotPassword.title" /></div>
            <div class="card-body p-4">
                <p class="text-muted"><fmt:message key="forgotPassword.intro" /></p>
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
                <form method="post" action="${ctx}/app/password/forgot" class="needs-validation" novalidate>
                    <div class="mb-3">
                        <label for="email" class="form-label"><fmt:message key="forgotPassword.label.email" /></label>
                        <input type="email" class="form-control" id="email" name="email" required
                               value="${not empty email ? email : ''}">
                        <div class="form-text"><fmt:message key="forgotPassword.help.email" /></div>
                    </div>
                    <div class="d-flex justify-content-between align-items-center flex-wrap gap-2">
                        <button type="submit" class="btn btn-brand"><fmt:message key="forgotPassword.submit" /></button>
                        <a href="${ctx}/app/auth/login" class="text-decoration-none"><fmt:message key="forgotPassword.back" /></a>
                    </div>
                </form>
            </div>
        </div>
    </div>
</div>
<%@ include file="/common/footer.jsp" %>
