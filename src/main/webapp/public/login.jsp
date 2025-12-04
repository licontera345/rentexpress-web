<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>

<%-- ============================================
     CONFIGURACIÓN
     ============================================ --%>
<c:set var="ctx" value="${pageContext.request.contextPath}" />

<c:set var="flashSuccess" value="${requestScope.flashSuccess}" />
<c:set var="errorGeneral" value="${requestScope.errorGeneral}" />
<c:set var="loginEmail" value="${requestScope.loginEmail}" />
<c:set var="selectedType" value="${requestScope.selectedType}" />
<c:set var="rememberSelected" value="${requestScope.rememberSelected}" />

<fmt:message var="pageTitle" key="login.title" />
<fmt:message var="introText" key="login.intro" />
<fmt:message var="radioUserLabel" key="login.type.user" />
<fmt:message var="radioEmployeeLabel" key="login.type.employee" />

<%@ include file="/common/header.jsp" %>

<%-- ============================================
     SECCIÓN DE AUTENTICACIÓN
     ============================================ --%>
<section class="auth-section py-6">
    <div class="container">
        <div class="auth-layout card-common shadow-soft">
            <div class="auth-intro">
                <span class="section-eyebrow"><fmt:message key="common.home.hero.badge" /></span>
                <h2 class="form-title">${pageTitle}</h2>
                <p class="form-subtitle">${introText}</p>
                <ul class="benefits-list">
                    <li><fmt:message key="register.user.benefits.1" /></li>
                    <li><fmt:message key="register.user.benefits.2" /></li>
                    <li><fmt:message key="register.user.benefits.3" /></li>
                </ul>
            </div>

            <%-- VALIDACIONES --%>
            <form method="post" action="${ctx}/public/login" class="auth-form form-grid single-column">
                <c:if test="${not empty flashSuccess}">
                    <div class="alert alert-success">${flashSuccess}</div>
                </c:if>

                <c:if test="${not empty errorGeneral}">
                    <div class="alert alert-danger">${errorGeneral}</div>
                </c:if>

                <%-- ========== Campo Email ========== --%>
                <div class="form-group">
                    <label for="email"><fmt:message key="login.email" /></label>
                    <input type="email"
                           name="email"
                           id="email"
                           class="form-control"
                           value="${loginEmail}"
                           required />
                </div>

                <%-- ========== Campo Password ========== --%>
                <div class="form-group">
                    <label for="password"><fmt:message key="login.password" /></label>
                    <input type="password" name="password" id="password" class="form-control" required />
                </div>

                <%-- ========== Tipo de Usuario ========== --%>
                <div class="form-group radio-group">
                    <span class="form-label"><fmt:message key="login.type" /></span>
                    <label class="radio-option">
                        <input type="radio"
                               name="type"
                               value="user"
                               ${selectedType eq 'employee' ? '' : 'checked'} />
                        <span>${radioUserLabel}</span>
                    </label>
                    <label class="radio-option">
                        <input type="radio"
                               name="type"
                               value="employee"
                               ${selectedType eq 'employee' ? 'checked' : ''} />
                        <span>${radioEmployeeLabel}</span>
                    </label>
                </div>

                <%-- ========== Recordar y Recuperación ========== --%>
                <div class="form-group form-actions between">
                    <label class="checkbox-option">
                        <input type="checkbox" name="remember" ${rememberSelected ? 'checked' : ''} />
                        <span><fmt:message key="login.remember" /></span>
                    </label>
                    <a class="form-link" href="${ctx}/public/security/recovery">
                        <fmt:message key="login.forgotPassword" />
                    </a>
                </div>

                <%-- ========== Acción de Inicio de Sesión ========== --%>
                <div class="form-actions full-width">
                    <button type="submit" class="btn-brand">
                        <fmt:message key="login.button" />
                    </button>
                </div>
            </form>
        </div>
    </div>
</section>

<jsp:include page="/common/footer.jsp" />
