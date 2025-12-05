<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>



<%-- ============================================
     CONFIGURACIÓN
     ============================================ --%>
<fmt:setLocale value="${sessionScope.appLocale != null ? sessionScope.appLocale : pageContext.request.locale}" scope="session" />
<fmt:setBundle basename="i18n.Messages" scope="session" />
<c:set var="ctx" value="${pageContext.request.contextPath}" />
<c:set var="errorEmail" value="${requestScope.errorEmail}" />
<c:set var="requestEmail" value="${requestScope.email}" />
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
                <h2 class="form-title"><fmt:message key="recovery.request.title" /></h2>
                <p class="form-subtitle"><fmt:message key="recovery.request.intro" /></p>
            </div>
            <form method="post" action="${ctx}/public/security/recovery" class="auth-form form-grid single-column">
                <input type="hidden" name="action" value="request" />
                <c:if test="${not empty errorEmail}">
                    <div class="alert alert-danger">${errorEmail}</div>
                </c:if>
                <div class="form-group">
                    <label for="email"><fmt:message key="recovery.request.email" /></label>
                    <input type="email" id="email" name="email" value="${requestEmail}" required />
                </div>
                <div class="form-actions full-width">
                    <button type="submit" class="btn-brand"><fmt:message key="recovery.request.submit" /></button>
                </div>
            </form>
            <div class="form-footer">
                <a href="${ctx}/public/login" class="form-link"><fmt:message key="recovery.request.back" /></a>
            </div>
        </div>
    </div>
</section>

<%@ include file="/common/footer.jsp" %>
