<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<fmt:setBundle basename="i18n.Messages" />
<fmt:setLocale value="${sessionScope.appLocale != null ? sessionScope.appLocale : pageContext.request.locale}" />
<%@ include file="/common/header.jsp" %>
<c:set var="employees" value="${requestScope.employees}" />
<c:set var="currentPage" value="${requestScope.currentPage}" />
<c:set var="totalPages" value="${requestScope.totalPages}" />
<c:set var="roles" value="${requestScope.roles}" />
<c:set var="headquarters" value="${requestScope.headquarters}" />
<c:set var="filterRoleId" value="${requestScope.filterRoleId}" />
<c:set var="filterHeadquartersId" value="${requestScope.filterHeadquartersId}" />
<c:set var="filterActiveStatus" value="${requestScope.filterActiveStatus}" />
<section class="private-section py-6">
    <div class="container">
        <header class="page-header">
            <div class="page-heading">
                <span class="section-eyebrow"><fmt:message key="employee.manage.pageTitle" /></span>
                <h2 class="page-title"><fmt:message key="employee.list.title" /></h2>
                <p class="page-subtitle"><fmt:message key="employee.manage.section.employees.help" /></p>
            </div>
            <div class="page-actions">
                <a class="btn btn-primary" href="${ctx}/private/EmployeeServlet?action=createEmployee">
                    <fmt:message key="employee.manage.action.new" />
                </a>
            </div>
        </header>
        <c:if test="${not empty requestScope.flashSuccess}">
            <div class="alert alert-success" role="alert">
                ${requestScope.flashSuccess}
            </div>
        </c:if>
        <c:if test="${not empty requestScope.flashError}">
            <div class="alert alert-danger" role="alert">
                ${requestScope.flashError}
            </div>
        </c:if>
        <form method="get" action="${ctx}/private/EmployeeServlet" class="row g-3 mb-4">
            <div class="col-lg-3 col-md-6">
                <label class="form-label" for="nameFilter"><fmt:message key="employee.filter.name" /></label>
                <input type="text" id="nameFilter" name="name" class="form-control" value="${requestScope.filterName}" />
            </div>
            <div class="col-lg-3 col-md-6">
                <label class="form-label" for="emailFilter"><fmt:message key="employee.filter.email" /></label>
                <input type="email" id="emailFilter" name="email" class="form-control"
                    value="${requestScope.filterEmail}" />
            </div>
            <div class="col-lg-3 col-md-6">
                <label class="form-label" for="roleFilter"><fmt:message key="employee.filter.role" /></label>
                <select id="roleFilter" name="roleId" class="form-select">
                    <option value=""><fmt:message key="employee.filter.role.all" /></option>
                    <c:forEach var="role" items="${roles}">
                        <option value="${role.roleId}" ${role.roleId eq filterRoleId ? 'selected' : ''}>
                            <c:out value="${role.roleName}" />
                        </option>
                    </c:forEach>
                </select>
            </div>
            <div class="col-lg-3 col-md-6">
                <label class="form-label" for="headquartersFilter"><fmt:message key="employee.filter.headquarters" /></label>
                <select id="headquartersFilter" name="headquartersId" class="form-select">
                    <option value=""><fmt:message key="employee.filter.headquarters.all" /></option>
                    <c:forEach var="hq" items="${headquarters}">
                        <option value="${hq.id}" ${hq.id eq filterHeadquartersId ? 'selected' : ''}>
                            <c:out value="${hq.name}" />
                        </option>
                    </c:forEach>
                </select>
            </div>
            <div class="col-lg-3 col-md-6">
                <label class="form-label" for="statusFilter"><fmt:message key="employee.filter.status" /></label>
                <select id="statusFilter" name="active" class="form-select">
                    <option value=""><fmt:message key="employee.filter.status.all" /></option>
                    <option value="true" ${filterActiveStatus == 'true' ? 'selected' : ''}>
                        <fmt:message key="employee.filter.status.active" />
                    </option>
                    <option value="false" ${filterActiveStatus == 'false' ? 'selected' : ''}>
                        <fmt:message key="employee.filter.status.inactive" />
                    </option>
                </select>
            </div>
            <div class="col-lg-3 col-md-6 d-flex align-items-end">
                <button type="submit" class="btn btn-primary me-2"><fmt:message key="employee.filter.apply" /></button>
                <a class="btn btn-outline-secondary" href="${ctx}/private/EmployeeServlet"><fmt:message
                        key="employee.filter.reset" /></a>
            </div>
        </form>
        <div class="data-surface card-common shadow-soft">
            <table class="table data-table">
                <thead>
                    <tr>
                        <th><fmt:message key="employee.table.name" /></th>
                        <th><fmt:message key="employee.table.username" /></th>
                        <th><fmt:message key="employee.table.email" /></th>
                        <th><fmt:message key="employee.table.phone" /></th>
                        <th><fmt:message key="employee.table.role" /></th>
                        <th><fmt:message key="employee.table.headquarters" /></th>
                        <th><fmt:message key="employee.table.status" /></th>
                        <th class="text-end"><fmt:message key="employee.table.actions" /></th>
                    </tr>
                </thead>
                <tbody>
                    <c:choose>
                        <c:when test="${not empty employees}">
                            <c:forEach var="employee" items="${employees}">
                                <tr>
                                    <td>
                                        <c:out value="${employee.firstName}" />
                                        <c:if test="${not empty employee.lastName1}">
                                            <c:out value=" ${employee.lastName1}" />
                                        </c:if>
                                        <c:if test="${not empty employee.lastName2}">
                                            <c:out value=" ${employee.lastName2}" />
                                        </c:if>
                                    </td>
                                    <td><c:out value="${employee.employeeName}" /></td>
                                    <td><c:out value="${employee.email}" /></td>
                                    <td><c:out value="${employee.phone}" /></td>
                                    <td><c:out value="${employee.role.roleName}" /></td>
                                    <td>
                                        <c:choose>
                                            <c:when test="${not empty employee.headquarters}">
                                                <c:out value="${employee.headquarters.name}" />
                                            </c:when>
                                            <c:otherwise>-</c:otherwise>
                                        </c:choose>
                                    </td>
                                    <td>
                                        <c:choose>
                                            <c:when test="${employee.activeStatus}">
                                                <span class="badge bg-success"><fmt:message key="employee.status.active" /></span>
                                            </c:when>
                                            <c:otherwise>
                                                <span class="badge bg-secondary"><fmt:message key="employee.status.inactive" /></span>
                                            </c:otherwise>
                                        </c:choose>
                                    </td>
                                    <td class="text-end">
                                        <a class="btn btn-sm btn-outline-primary me-2" href="${ctx}/private/EmployeeServlet?action=updateEmployee&amp;employeeId=${employee.id}">
                                            <fmt:message key="actions.edit" />
                                        </a>
                                        <form method="post" action="${ctx}/private/EmployeeServlet" class="d-inline">
                                            <input type="hidden" name="action" value="deleteEmployee" />
                                            <input type="hidden" name="employeeId" value="${employee.id}" />
                                            <button type="submit" class="btn btn-sm btn-outline-danger">
                                                <fmt:message key="actions.delete" />
                                            </button>
                                        </form>
                                    </td>
                                </tr>
                            </c:forEach>
                        </c:when>
                        <c:otherwise>
                            <tr>
                                <td colspan="8" class="empty-cell"><fmt:message key="employee.list.empty" /></td>
                            </tr>
                        </c:otherwise>
                    </c:choose>
                </tbody>
            </table>
            <c:if test="${totalPages >= 1}">
                <c:set var="paginationCurrentPage" value="${currentPage}" scope="request" />
                <c:set var="paginationTotalPages" value="${totalPages}" scope="request" />
                <jsp:include page="/public/pagination.jsp" />
                <c:remove var="paginationCurrentPage" scope="request" />
                <c:remove var="paginationTotalPages" scope="request" />
            </c:if>
        </div>
    </div>
</section>
<%@ include file="/common/footer.jsp" %>
