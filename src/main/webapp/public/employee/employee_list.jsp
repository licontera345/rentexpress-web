<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>

<fmt:setLocale value="${sessionScope.locale}" scope="session" />
<fmt:setBundle basename="i18n.Messages" />

<!DOCTYPE html>
<html lang="${sessionScope.locale.language != null ? sessionScope.locale.language : 'es'}">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title><fmt:message key="employee.list.title" /></title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet" integrity="sha384-QWTKZyjpPEjISv5WaRU9OFeRpok6YctnYmDr5pNlyT2bRjXh0JMhjY6hW+ALEwIH" crossorigin="anonymous">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.css" rel="stylesheet">
    <link href="https://cdn.jsdelivr.net/npm/flag-icons@6.6.6/css/flag-icons.min.css" rel="stylesheet">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/main.css">
</head>
<body class="bg-light d-flex flex-column min-vh-100">
    <%@ include file="/common/header.jsp"%>

    <main class="flex-grow-1 py-5">
        <div class="container">
            <div class="d-flex flex-wrap justify-content-between align-items-start align-items-md-center mb-4 gap-3">
                <div>
                    <h1 class="fw-bold mb-1"><fmt:message key="employee.list.title" /></h1>
                    <p class="text-muted mb-0"><fmt:message key="employee.list.subtitle" /></p>
                </div>
                <a href="${pageContext.request.contextPath}/public/EmployeeServlet?action=create" class="btn btn-brand d-flex align-items-center gap-2">
                    <i class="bi bi-plus-circle"></i>
                    <fmt:message key="action.new" />
                </a>
            </div>

            <div class="table-card">
                <div class="table-responsive">
                    <table class="table table-striped align-middle mb-0">
                        <thead>
                            <tr>
                                <th scope="col">ID</th>
                                <th scope="col"><fmt:message key="employee.detail.name" /></th>
                                <th scope="col"><fmt:message key="employee.detail.email" /></th>
                                <th scope="col" class="text-end"><fmt:message key="actions" /></th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach var="employee" items="${employees}">
                                <c:set var="employeeId" value="${not empty employee.id ? employee.id : (not empty employee.employeeId ? employee.employeeId : employee.idEmployee)}" />
                                <tr>
                                    <td>${employeeId}</td>
                                    <td><c:out value="${employee.employeeName}" /></td>
                                    <td><c:out value="${employee.email}" /></td>
                                    <td class="text-end">
                                        <div class="btn-group" role="group">
                                            <a href="${pageContext.request.contextPath}/public/EmployeeServlet?action=detail&id=${employeeId}" class="btn btn-outline-brand btn-sm">
                                                <i class="bi bi-eye"></i>
                                                <span class="visually-hidden"><fmt:message key="action.view" /></span>
                                            </a>
                                            <a href="${pageContext.request.contextPath}/public/EmployeeServlet?action=edit&id=${employeeId}" class="btn btn-outline-secondary btn-sm">
                                                <i class="bi bi-pencil"></i>
                                                <span class="visually-hidden"><fmt:message key="action.edit" /></span>
                                            </a>
                                            <a href="${pageContext.request.contextPath}/public/EmployeeServlet?action=deactivate&id=${employeeId}" class="btn btn-outline-danger btn-sm" data-confirm="<fmt:message key='employee.delete.confirm' />">
                                                <i class="bi bi-trash"></i>
                                                <span class="visually-hidden"><fmt:message key="action.delete" /></span>
                                            </a>
                                        </div>
                                    </td>
                                </tr>
                            </c:forEach>
                            <c:if test="${empty employees}">
                                <tr>
                                    <td colspan="4" class="text-center py-4 text-muted"><fmt:message key="employee.list.empty" /></td>
                                </tr>
                            </c:if>
                        </tbody>
                    </table>
                </div>
            </div>
        </div>
    </main>

    <%@ include file="/common/footer.jsp"%>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js" integrity="sha384-YvpcrYf0tY3lHB60NNkmXc5s9fDVZLESaAA55NDzOxhy9GkcIdslK1eN7N6jIeHz" crossorigin="anonymous"></script>
    <script>
        document.querySelectorAll('[data-confirm]').forEach(function (btn) {
            btn.addEventListener('click', function (event) {
                const message = btn.getAttribute('data-confirm');
                if (!confirm(message)) {
                    event.preventDefault();
                }
            });
        });
    </script>
</body>
</html>
