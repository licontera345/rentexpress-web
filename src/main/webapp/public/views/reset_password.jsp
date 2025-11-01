<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<fmt:message key="common.page.resetPassword.title" var="resetPasswordTitle" />
<c:set var="pageTitle" value="${resetPasswordTitle}" />
<%@ include file="/common/header.jsp" %>
<c:set var="errors" value="${resetPasswordErrors}" />
<c:set var="pendingEmail" value="${pendingResetEmail}" />
<div class="row justify-content-center">
    <div class="col-lg-6">
        <div class="card card-common">
            <div class="card-header"><fmt:message key="common.page.resetPassword.title" /></div>
            <div class="card-body p-4">
                <p class="text-muted">
                    <fmt:message key="common.password.reset.intro">
                        <fmt:param>
                            <strong>${pendingEmail}</strong>
                        </fmt:param>
                    </fmt:message>
                </p>
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
                        <label for="newPassword" class="form-label"><fmt:message key="common.password.reset.label.newPassword" /></label>
                        <input type="password" class="form-control" id="newPassword" name="newPassword" minlength="8" required>
                        <div class="form-text"><fmt:message key="common.password.reset.help.newPassword" /></div>
                    </div>
                    <div class="mb-3">
                        <label for="confirmPassword" class="form-label"><fmt:message key="common.password.reset.label.confirmPassword" /></label>
                        <input type="password" class="form-control" id="confirmPassword" name="confirmPassword" minlength="8" required>
                    </div>
                    <div class="d-flex justify-content-between align-items-center flex-wrap gap-2">
                        <button type="submit" class="btn btn-brand"><fmt:message key="common.password.reset.submit" /></button>
                        <a href="${ctx}/login" class="text-decoration-none"><fmt:message key="common.password.reset.back" /></a>
                    </div>
                </form>
            </div>
        </div>
    </div>
</div>
<%@ include file="/common/footer.jsp" %>
