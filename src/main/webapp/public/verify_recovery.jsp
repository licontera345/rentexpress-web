<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>

<%-- ============================================
     CONFIGURACIÓN
     ============================================ --%>
<fmt:setLocale value="${sessionScope.appLocale != null ? sessionScope.appLocale : pageContext.request.locale}" scope="session" />
<fmt:setBundle basename="i18n.Messages" scope="session" />
<c:set var="ctx" value="${pageContext.request.contextPath}" />
<c:set var="flashInfo" value="${requestScope.flashInfo}" />
<c:set var="email" value="${requestScope.email}" />
<c:set var="errorEmail" value="${requestScope.errorEmail}" />
<c:set var="errorCode" value="${requestScope.errorCode}" />
<c:set var="errorPassword" value="${requestScope.errorPassword}" />
<c:set var="errorConfirm" value="${requestScope.errorConfirm}" />
<%@ include file="/common/header.jsp" %>

<%-- ============================================
     VALIDACIONES
     ============================================ --%>

<%-- ============================================
     FORMULARIO/CONTENIDO
     ============================================ --%>
<section class="auth-section py-6">
    <div class="container">
        <div class="auth-layout card-common shadow-soft">
            <div class="auth-intro">
                <span class="section-eyebrow"><fmt:message key="common.home.hero.badge" /></span>
                <h2 class="form-title"><fmt:message key="recovery.verify.title" /></h2>
                <p class="form-subtitle"><fmt:message key="recovery.verify.intro" /></p>
                <c:if test="${not empty flashInfo}">
                    <div class="info-box">
                        <p>${flashInfo}</p>
                    </div>
                </c:if>
            </div>
            <form method="post" action="${ctx}/public/security/recovery" class="auth-form form-grid single-column">
                <input type="hidden" name="action" value="verify" />
                <input type="hidden" name="email" value="${email}" />
                <c:if test="${not empty errorEmail}">
                    <div class="alert alert-danger">${errorEmail}</div>
                </c:if>
                <c:if test="${not empty errorCode}">
                    <div class="alert alert-danger">${errorCode}</div>
                </c:if>
                <div class="form-group">
                    <label for="code"><fmt:message key="recovery.verify.code" /></label>
                    <input type="text" id="code" name="code" maxlength="6" required />
                </div>
                <div class="form-group">
                    <label for="password"><fmt:message key="recovery.verify.newPassword" /></label>
                    <input type="password" id="password" name="password" required />
                    <c:if test="${not empty errorPassword}">
                        <small class="form-text error-text">${errorPassword}</small>
                    </c:if>
                </div>
                <div class="form-group">
                    <label for="confirmPassword"><fmt:message key="recovery.verify.confirmPassword" /></label>
                    <input type="password" id="confirmPassword" name="confirmPassword" required />
                    <c:if test="${not empty errorConfirm}">
                        <small class="form-text error-text">${errorConfirm}</small>
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
