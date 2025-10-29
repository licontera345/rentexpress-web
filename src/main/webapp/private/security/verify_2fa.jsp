<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<fmt:message key="page.verify2fa.title" var="verify2faTitle" />
<c:set var="pageTitle" value="${verify2faTitle}" />
<%@ include file="/common/header.jsp" %>
<div class="row justify-content-center">
    <div class="col-lg-6">
        <div class="card card-common">
            <div class="card-header"><fmt:message key="page.verify2fa.title" /></div>
            <div class="card-body p-4">
                <p class="text-muted">
                    <fmt:message key="verify2fa.intro">
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
                <c:if test="${not empty error}">
                    <div class="alert alert-danger">
                        <c:forEach var="entry" items="${error.entrySet()}">
                            <div class="mb-1">${entry.value}</div>
                        </c:forEach>
                    </div>
                </c:if>
                <form method="post" action="${ctx}/app/auth/verify-2fa" class="needs-validation" novalidate>
                    <div class="mb-3">
                        <label for="code" class="form-label"><fmt:message key="verify2fa.label.code" /></label>
                        <input type="text" class="form-control text-center fs-4" id="code" name="code" maxlength="6"
                               pattern="\\d{6}" required value="${not empty submittedCode ? submittedCode : ''}">
                        <div class="form-text">
                            <fmt:message key="verify2fa.help.code">
                                <fmt:param value="${secondsRemaining}" />
                            </fmt:message>
                        </div>
                    </div>
                    <div class="d-flex justify-content-between align-items-center">
                        <button type="submit" class="btn btn-brand"><fmt:message key="verify2fa.submit" /></button>
                        <div class="text-end">
                            <a href="${ctx}/app/auth/verify-2fa?resend=1" class="btn btn-link"><fmt:message key="verify2fa.resend" /></a>
                            <a href="${ctx}/app/auth/login" class="btn btn-link"><fmt:message key="verify2fa.cancel" /></a>
                        </div>
                    </div>
                </form>
            </div>
        </div>
    </div>
</div>
<%@ include file="/common/footer.jsp" %>
