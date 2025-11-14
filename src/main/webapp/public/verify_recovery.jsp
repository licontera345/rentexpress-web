<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ include file="/common/header.jsp" %>
<section class="auth-section py-6">
    <div class="container">
        <div class="auth-layout card-common shadow-soft">
            <div class="auth-intro">
                <span class="section-eyebrow"><fmt:message key="common.home.hero.badge" /></span>
                <h2 class="form-title"><fmt:message key="recovery.verify.title" /></h2>
                <p class="form-subtitle"><fmt:message key="recovery.verify.intro" /></p>
                <c:if test="${not empty requestScope.flashInfo}">
                    <div class="info-box">
                        <p>${requestScope.flashInfo}</p>
                    </div>
                </c:if>
            </div>
            <form method="post" action="${ctx}/public/security/recovery" class="auth-form form-grid single-column">
                <input type="hidden" name="action" value="verify" />
                <input type="hidden" name="email" value="${requestScope.email}" />
                <c:if test="${not empty requestScope.errorEmail}">
                    <div class="alert alert-danger">${requestScope.errorEmail}</div>
                </c:if>
                <c:if test="${not empty requestScope.errorCode}">
                    <div class="alert alert-danger">${requestScope.errorCode}</div>
                </c:if>
                <div class="form-group">
                    <label for="code"><fmt:message key="recovery.verify.code" /></label>
                    <input type="text" id="code" name="code" maxlength="6" required />
                </div>
                <div class="form-group">
                    <label for="password"><fmt:message key="recovery.verify.newPassword" /></label>
                    <input type="password" id="password" name="password" required />
                    <c:if test="${not empty requestScope.errorPassword}">
                        <small class="form-text error-text">${requestScope.errorPassword}</small>
                    </c:if>
                </div>
                <div class="form-group">
                    <label for="confirmPassword"><fmt:message key="recovery.verify.confirmPassword" /></label>
                    <input type="password" id="confirmPassword" name="confirmPassword" required />
                    <c:if test="${not empty requestScope.errorConfirm}">
                        <small class="form-text error-text">${requestScope.errorConfirm}</small>
                    </c:if>
                </div>
                <div class="form-actions full-width">
                    <button type="submit" class="btn-brand"><fmt:message key="recovery.verify.submit" /></button>
                </div>
            </form>
            <div class="form-footer">
                <a href="${ctx}/public/login" class="form-link"><fmt:message key="recovery.verify.back" /></a>
            </div>
        </div>
    </div>
</section>

<%@ include file="/common/footer.jsp" %>
