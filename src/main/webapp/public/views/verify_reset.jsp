<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<fmt:setLocale value="${not empty sessionScope.appLocale ? sessionScope.appLocale : pageContext.request.locale}" scope="session" />
<fmt:setBundle basename="i18n.Messages" />
<fmt:message key="common.page.verifyReset.title" var="verifyResetTitle" />
<c:set var="pageTitle" value="${verifyResetTitle}" />
<%@ include file="/common/header.jsp" %>
<c:set var="errors" value="${verifyResetErrors}" />
<c:set var="submittedCode" value="${submittedResetCode}" />
<c:set var="pendingEmail" value="${pendingResetEmail}" />
<c:set var="secondsRemaining" value="${resetSecondsRemaining}" />
<div class="row justify-content-center">
    <div class="col-lg-6">
        <div class="card card-common">
            <div class="card-header"><fmt:message key="common.page.verifyReset.title" /></div>
            <div class="card-body p-4">
                <p class="text-muted">
                    <fmt:message key="common.password.verify.intro">
                        <fmt:param>
                            <strong>${pendingEmail}</strong>
                        </fmt:param>
                        <fmt:param value="${secondsRemaining}" />
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
                <form method="post" action="${ctx}/app/password/verify-reset" class="needs-validation" novalidate>
                    <div class="mb-3">
                        <label for="code" class="form-label"><fmt:message key="common.password.verify.label.code" /></label>
                        <input type="text" class="form-control" id="code" name="code" required pattern="\\d{6}"
                               maxlength="6" value="${not empty submittedCode ? submittedCode : ''}">
                        <div class="form-text">
                            <fmt:message key="common.password.verify.help.code">
                                <fmt:param value="${secondsRemaining}" />
                            </fmt:message>
                        </div>
                    </div>
                    <div class="d-flex justify-content-between align-items-center flex-wrap gap-2">
                        <button type="submit" class="btn btn-brand"><fmt:message key="common.password.verify.submit" /></button>
                        <div class="text-end">
                            <a href="${ctx}/app/password/verify-reset?resend=1" class="text-decoration-none d-block"><fmt:message key="common.password.verify.resend" /></a>
                            <a href="${ctx}/app/password/forgot" class="text-decoration-none small"><fmt:message key="common.password.verify.changeEmail" /></a>
                        </div>
                    </div>
                </form>
            </div>
        </div>
    </div>
</div>
<%@ include file="/common/footer.jsp" %>
