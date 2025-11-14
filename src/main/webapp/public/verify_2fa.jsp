<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ include file="/common/header.jsp" %>
<c:set var="errors" value="${requestScope.errors}" />

<section class="auth-section py-6">
    <div class="container">
        <div class="auth-layout card-common shadow-soft">
            <div class="auth-intro">
                <span class="section-eyebrow"><fmt:message key="common.home.hero.badge" /></span>
                <h2 class="form-title"><fmt:message key="twofactor.title" /></h2>
                <p class="form-subtitle"><fmt:message key="twofactor.intro" /></p>
                <c:if test="${not empty requestScope.pendingEmail}">
                    <div class="info-box">
                        <p>
                            <fmt:message key="twofactor.notice">
                                <fmt:param value="${requestScope.pendingEmail}" />
                            </fmt:message>
                        </p>
                    </div>
                </c:if>
            </div>
            <form method="post" action="${ctx}/public/security/verify-2fa"
                class="auth-form form-grid single-column">
                <input type="hidden" name="email" value="${requestScope.pendingEmail}" />
                <c:if test="${errors != null and errors.hasError('code')}">
                    <div class="alert alert-danger">${errors.getMessage('code')}</div>
                </c:if>
                <div class="form-group">
                    <label for="code"><fmt:message key="twofactor.code.label" /></label>
                    <input type="text" id="code" name="code" maxlength="6" required />
                    <small class="form-text"><fmt:message key="twofactor.helper" /></small>
                </div>
                <div class="form-actions full-width">
                    <button type="submit" class="btn-brand"><fmt:message key="twofactor.submit" /></button>
                </div>
            </form>
        </div>
    </div>
</section>

<%@ include file="/common/footer.jsp" %>
