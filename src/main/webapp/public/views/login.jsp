<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ include file="/common/header.jsp" %>
<div class="row justify-content-center">
    <div class="col-lg-6">
        <div class="card card-common">
            <div class="card-header"><fmt:message key="login.title" /></div>
            <div class="card-body p-4">
                <p class="text-muted"><fmt:message key="login.intro" /></p>
                <c:if test="${param.logout eq '1'}">
                    <div class="alert alert-success"><fmt:message key="login.logoutMessage" /></div>
                </c:if>
                <c:if test="${param.error eq 'auth'}">
                    <div class="alert alert-warning"><fmt:message key="login.authRequired" /></div>
                </c:if>
                <c:if test="${not empty error}">
                    <div class="alert alert-danger">
                        <c:forEach var="entry" items="${error.entrySet()}">
                            <div class="mb-1">${entry.value}</div>
                        </c:forEach>
                    </div>
                </c:if>
                <form method="post" action="${ctx}/login" class="needs-validation" novalidate>
                    <div class="mb-3">
                        <label for="email" class="form-label"><fmt:message key="login.email" /></label>
                        <input type="email" class="form-control" id="email" name="email" required
                               value="${email}">
                    </div>
                    <div class="mb-3">
                        <label for="password" class="form-label"><fmt:message key="login.password" /></label>
                        <input type="password" class="form-control" id="password" name="password" required>
                    </div>
                    <div class="form-check mb-3">
                        <input class="form-check-input" type="checkbox" value="1" id="remember" name="remember">
                        <label class="form-check-label" for="remember">
                            <fmt:message key="login.remember" />
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
