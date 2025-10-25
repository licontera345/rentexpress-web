<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<fmt:setLocale value="${sessionScope.locale}" scope="session" />
<fmt:setBundle basename="i18n.Messages" />
<%
String rememberedUser = "";
Cookie[] cookies = request.getCookies();
if (cookies != null) {
    for (Cookie cookie : cookies) {
        if ("rememberedUser".equals(cookie.getName())) {
            rememberedUser = cookie.getValue();
            break;
        }
    }
}
request.setAttribute("rememberedUser", rememberedUser);
%>

<!DOCTYPE html>
<html lang="${sessionScope.locale.language != null ? sessionScope.locale.language : 'es'}">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title><fmt:message key="login.title" /></title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet" integrity="sha384-QWTKZyjpPEjISv5WaRU9OFeRpok6YctnYmDr5pNlyT2bRjXh0JMhjY6hW+ALEwIH" crossorigin="anonymous">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.css" rel="stylesheet">
    <link href="https://cdn.jsdelivr.net/npm/flag-icons@6.6.6/css/flag-icons.min.css" rel="stylesheet">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/main.css">
</head>
<body class="bg-light d-flex flex-column min-vh-100">
    <%@ include file="/common/header.jsp"%>

    <main class="flex-grow-1 d-flex align-items-center py-5">
        <div class="container">
            <div class="row justify-content-center">
                <div class="col-lg-10">
                    <div class="card login-card overflow-hidden">
                        <div class="row g-0">
                            <div class="col-lg-6 login-illustration"></div>
                            <div class="col-lg-6 p-5">
                                <div class="mb-4">
                                    <h2 class="fw-bold mb-2"><fmt:message key="login.title" /></h2>
                                    <p class="text-muted mb-0"><fmt:message key="login.subtitle" /></p>
                                </div>
                                <form action="${pageContext.request.contextPath}/public/UsuarioServlet" method="post" class="needs-validation" novalidate>
                                    <input type="hidden" name="action" value="login" />

                                    <div class="mb-4">
                                        <label class="form-label d-block mb-2 fw-semibold text-muted"><fmt:message key="login.userType" /></label>
                                        <div class="d-flex gap-3 flex-wrap">
                                            <div class="form-check form-check-inline">
                                                <input class="btn-check" type="radio" name="userType" id="login-customer" value="customer" checked>
                                                <label class="btn btn-outline-brand px-4" for="login-customer">
                                                    <i class="bi bi-person me-2"></i>
                                                    <fmt:message key="login.userType.customer" />
                                                </label>
                                            </div>
                                            <div class="form-check form-check-inline">
                                                <input class="btn-check" type="radio" name="userType" id="login-employee" value="employee">
                                                <label class="btn btn-outline-brand px-4" for="login-employee">
                                                    <i class="bi bi-briefcase me-2"></i>
                                                    <fmt:message key="login.userType.employee" />
                                                </label>
                                            </div>
                                        </div>
                                    </div>

                                    <div class="mb-4">
                                        <label for="username" class="form-label fw-semibold"><fmt:message key="login.username" /></label>
                                        <div class="input-group">
                                            <span class="input-group-text"><i class="bi bi-person-circle"></i></span>
                                            <input type="text" class="form-control" id="username" name="username" value="<c:out value='${rememberedUser}'/>" required>
                                            <div class="invalid-feedback">
                                                <fmt:message key="usuario.detail.name" />
                                            </div>
                                        </div>
                                    </div>

                                    <div class="mb-4">
                                        <label for="password" class="form-label fw-semibold"><fmt:message key="login.password" /></label>
                                        <div class="input-group">
                                            <span class="input-group-text"><i class="bi bi-shield-lock"></i></span>
                                            <input type="password" class="form-control" id="password" name="password" required>
                                            <button class="btn btn-outline-secondary" type="button" id="togglePassword" aria-label="<fmt:message key='login.showPassword' />" data-label-show="<fmt:message key='login.showPassword' />" data-label-hide="<fmt:message key='login.hidePassword' />">
                                                <i class="bi bi-eye"></i>
                                            </button>
                                            <div class="invalid-feedback">
                                                <fmt:message key="login.password" />
                                            </div>
                                        </div>
                                    </div>

                                    <div class="d-flex flex-wrap justify-content-between align-items-center mb-4">
                                        <div class="form-check">
                                            <input class="form-check-input" type="checkbox" id="rememberMe" name="rememberMe" <c:if test="${not empty rememberedUser}">checked</c:if>>
                                            <label class="form-check-label" for="rememberMe">
                                                <fmt:message key="login.rememberMe" />
                                            </label>
                                        </div>
                                        <a href="#" class="text-brand text-decoration-none small"><fmt:message key="login.forgot" /></a>
                                    </div>

                                    <button type="submit" class="btn btn-brand w-100 py-3 mb-3">
                                        <fmt:message key="login.button" />
                                    </button>

                                    <div class="text-center text-muted small">
                                        <fmt:message key="login.noAccount" />
                                        <a href="${pageContext.request.contextPath}/public/UsuarioServlet?action=create" class="text-brand text-decoration-none fw-semibold">
                                            <fmt:message key="login.registerNow" />
                                        </a>
                                    </div>
                                </form>

                                <c:if test="${not empty error}">
                                    <div class="alert alert-danger mt-4 d-flex align-items-center" role="alert">
                                        <i class="bi bi-exclamation-triangle-fill me-2"></i>
                                        <div><c:out value="${error}" /></div>
                                    </div>
                                </c:if>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </main>

    <%@ include file="/common/footer.jsp"%>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js" integrity="sha384-YvpcrYf0tY3lHB60NNkmXc5s9fDVZLESaAA55NDzOxhy9GkcIdslK1eN7N6jIeHz" crossorigin="anonymous"></script>
    <script>
        (function () {
            'use strict';
            const forms = document.querySelectorAll('.needs-validation');
            Array.from(forms).forEach(form => {
                form.addEventListener('submit', event => {
                    if (!form.checkValidity()) {
                        event.preventDefault();
                        event.stopPropagation();
                    }
                    form.classList.add('was-validated');
                }, false);
            });
        })();

        const togglePassword = document.getElementById('togglePassword');
        const passwordInput = document.getElementById('password');
        if (togglePassword && passwordInput) {
            togglePassword.addEventListener('click', () => {
                const isHidden = passwordInput.getAttribute('type') === 'password';
                passwordInput.setAttribute('type', isHidden ? 'text' : 'password');
                const icon = togglePassword.querySelector('i');
                if (icon) {
                    icon.classList.toggle('bi-eye');
                    icon.classList.toggle('bi-eye-slash');
                }
                togglePassword.setAttribute('aria-label', isHidden ? togglePassword.dataset.labelHide : togglePassword.dataset.labelShow);
            });
        }
    </script>
</body>
</html>
