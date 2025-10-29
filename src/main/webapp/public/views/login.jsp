<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ include file="/common/header.jsp" %>
<div class="row justify-content-center">
    <div class="col-lg-6">
        <div class="card card-common">
            <div class="card-header"><fmt:message key="login.title" /></div>
            <div class="card-body p-4">
                <p class="text-muted"><fmt:message key="login.intro" /></p>
                <c:if test="${alreadyAuthenticated}">
                    <div class="alert alert-warning d-flex flex-column flex-md-row align-items-md-center gap-2 justify-content-between">
                        <span>
                            <fmt:message key="login.alreadyAuthenticated.message">
                                <fmt:param value="<strong>${rememberedEmail}</strong>" />
                            </fmt:message>
                        </span>
                        <a class="btn btn-sm btn-outline-brand" href="${ctx}/app/welcome">
                            <fmt:message key="login.alreadyAuthenticated.cta" />
                        </a>
                    </div>
                </c:if>
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
                <form method="post" action="${ctx}/app/auth/login" class="needs-validation" novalidate>
                    <div class="mb-3">
                        <label for="email" class="form-label"><fmt:message key="login.email" /></label>
                        <input type="email" class="form-control" id="email" name="email" required
                               value="${rememberedEmail}">
                    </div>
                    <div class="mb-3">
                        <label for="password" class="form-label"><fmt:message key="login.password" /></label>
                        <input type="password" class="form-control" id="password" name="password" required>
                    </div>
                    <div class="form-check mb-3">
                        <input class="form-check-input" type="checkbox" value="on" id="remember" name="remember"
                                ${not empty rememberedEmail ? 'checked' : ''}>
                        <label class="form-check-label" for="remember">
                            <fmt:message key="login.rememberMe" />
                        </label>
                    </div>
                    <div class="d-flex justify-content-between align-items-center flex-wrap gap-2">
                        <button type="submit" class="btn btn-brand"><fmt:message key="login.button" /></button>
                        <div class="text-end">
                            <a href="${ctx}/app/users/register" class="text-decoration-none d-block">
                                <fmt:message key="login.registerLink" />
                            </a>
                            <a href="${ctx}/app/password/forgot" class="text-decoration-none small">
                                <fmt:message key="login.forgotLink" />
                            </a>
                        </div>
                    </div>
                </form>
            </div>
        </div>
    </div>
</div>
<%@ include file="/common/footer.jsp" %>
