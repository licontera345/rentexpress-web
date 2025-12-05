<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>


<%-- ============================================
     CONFIGURACIÓN
     ============================================ --%>
<fmt:setLocale value="${sessionScope.appLocale != null ? sessionScope.appLocale : pageContext.request.locale}" scope="session" />
<fmt:setBundle basename="i18n.Messages" scope="session" />
<c:set var="ctx" value="${pageContext.request.contextPath}" />
<c:set var="employee" value="${requestScope.employee}" />
<c:set var="errors" value="${requestScope.errors}" />
<c:set var="errorGeneral" value="${requestScope.errorGeneral}" />
<%@ include file="/common/header.jsp" %>

<%-- ============================================
     VALIDACIONES
     ============================================ --%>

<%-- ============================================
     FORMULARIO/CONTENIDO
     ============================================ --%>
<section class="private-section py-6">
    <div class="container narrow">
        <header class="page-header">
            <span class="section-eyebrow"><fmt:message key="employee.manage.pageTitle" /></span>
            <h2 class="page-title">
                <c:choose>
                    <c:when test="${empty employee.id}">
                        <fmt:message key="employee.manage.form.create" />
                    </c:when>
                    <c:otherwise>
                        <fmt:message key="employee.manage.form.update" />
                    </c:otherwise>
                </c:choose>
            </h2>
            <p class="page-subtitle"><fmt:message key="employee.manage.section.employees.help" /></p>
        </header>
        <c:if test="${not empty errorGeneral}">
            <div class="alert alert-danger">${errorGeneral}</div>
        </c:if>
        <c:if test="${errors != null and not errors.isEmpty()}">
            <div class="alert alert-danger" role="alert">
                <c:forEach var="entry" items="${errors.all}">
                    <div>${entry.message}</div>
                </c:forEach>
            </div>
        </c:if>
        <form method="post" action="${ctx}/private/EmployeeServlet" class="card-common shadow-soft form-card">
            <input type="hidden" name="action" value="${empty employee.id ? 'createEmployee' : 'updateEmployee'}" />
            <c:if test="${not empty employee.id}">
                <input type="hidden" name="employeeId" value="${employee.id}" />
            </c:if>
            <div class="grid two-columns">
                <div class="form-group">
                    <label for="firstName"><fmt:message key="employee.manage.employee.firstName" /></label>
                    <input type="text" id="firstName" name="firstName" value="${employee.firstName}" required class="form-control" />
                </div>
                <div class="form-group">
                    <label for="lastName1"><fmt:message key="employee.manage.employee.lastName1" /></label>
                    <input type="text" id="lastName1" name="lastName1" value="${employee.lastName1}" required class="form-control" />
                </div>
                <div class="form-group">
                    <label for="lastName2"><fmt:message key="employee.manage.employee.lastName2" /></label>
                    <input type="text" id="lastName2" name="lastName2" value="${employee.lastName2}" class="form-control" />
                </div>
                <div class="form-group">
                    <label for="email"><fmt:message key="employee.manage.employee.email" /></label>
                    <input type="email" id="email" name="email" value="${employee.email}" required class="form-control" />
                </div>
                <div class="form-group">
                    <label for="phone"><fmt:message key="employee.manage.employee.phone" /></label>
                    <input type="text" id="phone" name="phone" value="${employee.phone}" class="form-control" />
                </div>
                <div class="form-group">
                    <label for="employeeName"><fmt:message key="employee.manage.user.username" /></label>
                    <input type="text" id="employeeName" name="employeeName" value="${employee.employeeName}" required
                        class="form-control" />
                </div>
                <div class="form-group">
                    <label for="password"><fmt:message key="employee.manage.employee.password" /></label>
                    <input type="password" id="password" name="password" ${empty employee.id ? 'required' : ''} class="form-control" />
                </div>
                <div class="form-group">
                    <label for="roleId"><fmt:message key="employee.manage.employee.role" /></label>
                    <select id="roleId" name="roleId" required class="form-select">
                        <option value=""><fmt:message key="employee.manage.employee.select" /></option>
                        <c:forEach var="role" items="${requestScope.roles}">
                            <option value="${role.roleId}" ${role.roleId eq employee.roleId ? 'selected' : ''}>
                                <c:out value="${role.roleName}" />
                            </option>
                        </c:forEach>
                    </select>
                </div>
                <div class="form-group">
                    <label for="headquartersId"><fmt:message key="employee.manage.employee.headquarters" /></label>
                    <select id="headquartersId" name="headquartersId" required class="form-select">
                        <option value=""><fmt:message key="employee.manage.employee.select" /></option>
                        <c:forEach var="hq" items="${requestScope.headquarters}">
                            <option value="${hq.id}" ${hq.id eq employee.headquartersId ? 'selected' : ''}>
                                <c:out value="${hq.name}" />
                            </option>
                        </c:forEach>
                    </select>
                </div>
            </div>
            <div class="form-actions">
                <button type="submit" class="btn btn-primary"><fmt:message key="employee.manage.submit" /></button>
                <a class="btn btn-secondary" href="${ctx}/private/EmployeeServlet"><fmt:message key="actions.cancel" /></a>
            </div>
        </form>
    </div>
</section>
<%@ include file="/common/footer.jsp" %>
