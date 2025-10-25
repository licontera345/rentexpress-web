<%@ page isErrorPage="true" language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>

<fmt:setLocale value="${sessionScope.locale}" scope="session" />
<fmt:setBundle basename="i18n.Messages" />

<!DOCTYPE html>
<html lang="${sessionScope.locale.language != null ? sessionScope.locale.language : 'es'}">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title><fmt:message key="error.title" /></title>
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
                <div class="col-lg-8">
                    <div class="card card-common shadow-soft">
                        <div class="card-body p-5 text-center">
                            <div class="feature-icon mx-auto mb-3"><i class="bi bi-exclamation-triangle"></i></div>
                            <h1 class="fw-bold mb-3"><fmt:message key="error.title" /></h1>
                            <p class="text-muted mb-4"><fmt:message key="error.message" /></p>
                            <c:if test="${not empty pageContext.exception.message}">
                                <div class="alert alert-danger text-start" role="alert">
                                    <i class="bi bi-bug-fill me-2"></i>
                                    <c:out value="${pageContext.exception.message}" />
                                </div>
                            </c:if>
                            <form action="${pageContext.request.contextPath}/public/UsuarioServlet" method="get" class="mt-4">
                                <input type="hidden" name="action" value="index" />
                                <button type="submit" class="btn btn-brand px-4"><fmt:message key="error.back" /></button>
                            </form>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </main>

    <%@ include file="/common/footer.jsp"%>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js" integrity="sha384-YvpcrYf0tY3lHB60NNkmXc5s9fDVZLESaAA55NDzOxhy9GkcIdslK1eN7N6jIeHz" crossorigin="anonymous"></script>
</body>
</html>
