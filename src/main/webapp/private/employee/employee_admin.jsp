<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<fmt:message key="employee.admin.pageTitle" var="employeeAdminTitle" />
<fmt:message key="employee.admin.filters.search.placeholder" var="employeeAdminSearchPlaceholder" />
<fmt:message key="employee.admin.filters.reset" var="employeeAdminResetLabel" />
<c:set var="pageTitle" value="${employeeAdminTitle}" />
<%@ include file="/common/header.jsp" %>
<c:set var="filters" value="${employeeFilters}" />
<c:set var="summary" value="${employeeSummary}" />
<c:set var="roleNames" value="${employeeRoleNames}" />
<c:set var="headquartersNames" value="${employeeHeadquartersNames}" />
<div class="row g-4">
    <div class="col-lg-4">
        <div class="card shadow-sm">
            <div class="card-header"><fmt:message key="employee.admin.filters.title" /></div>
            <div class="card-body">
                <form method="get" action="${ctx}/app/employees/private" class="vstack gap-3">
                    <div>
                        <label class="form-label" for="search"><fmt:message key="employee.admin.filters.search" /></label>
                        <input class="form-control" type="text" id="search" name="search" value="${filters.search}" placeholder="${employeeAdminSearchPlaceholder}" />
                    </div>
                    <div>
                        <label class="form-label" for="headquarters"><fmt:message key="employee.admin.filters.headquarters" /></label>
                        <select class="form-select" id="headquarters" name="headquarters">
                            <option value=""><fmt:message key="employee.admin.filters.headquarters.all" /></option>
                            <c:forEach var="entry" items="${headquartersNames}">
                                <option value="${entry.key}" ${entry.key == filters.headquarters ? 'selected' : ''}>${entry.value}</option>
                            </c:forEach>
                        </select>
                    </div>
                    <div>
                        <label class="form-label" for="active"><fmt:message key="employee.admin.filters.status" /></label>
                        <select class="form-select" id="active" name="active">
                            <option value="all" ${filters.active == 'all' ? 'selected' : ''}><fmt:message key="employee.admin.filters.status.all" /></option>
                            <option value="active" ${filters.active == 'active' ? 'selected' : ''}><fmt:message key="employee.admin.filters.status.active" /></option>
                            <option value="inactive" ${filters.active == 'inactive' ? 'selected' : ''}><fmt:message key="employee.admin.filters.status.inactive" /></option>
                        </select>
                    </div>
                    <div class="d-flex gap-2">
                        <button type="submit" class="btn btn-brand flex-grow-1"><i class="bi bi-search"></i> <fmt:message key="employee.admin.filters.apply" /></button>
                        <a class="btn btn-outline-secondary" href="${ctx}/app/employees/private" title="${employeeAdminResetLabel}">
                            <i class="bi bi-arrow-counterclockwise"></i>
                        </a>
                    </div>
                </form>
            </div>
        </div>
        <div class="card shadow-sm mt-4">
            <div class="card-header"><fmt:message key="employee.admin.summary.title" /></div>
            <div class="card-body">
                <ul class="list-unstyled mb-0">
                    <li class="d-flex justify-content-between"><span><fmt:message key="employee.admin.summary.total" /></span><strong>${summary.total}</strong></li>
                    <li class="d-flex justify-content-between text-success"><span><fmt:message key="employee.admin.summary.active" /></span><strong>${summary.active}</strong></li>
                    <li class="d-flex justify-content-between text-danger"><span><fmt:message key="employee.admin.summary.inactive" /></span><strong>${summary.inactive}</strong></li>
                    <li class="d-flex justify-content-between"><span><fmt:message key="employee.admin.summary.matching" /></span><strong>${summary.filtered}</strong></li>
                    <li class="d-flex justify-content-between"><span><fmt:message key="employee.admin.summary.headquarters" /></span><strong>${summary.headquarters}</strong></li>
                </ul>
            </div>
        </div>
    </div>
    <div class="col-lg-8">
        <c:if test="${not empty employeeFilterErrors}">
            <div class="alert alert-warning">
                <ul class="mb-0">
                    <c:forEach var="error" items="${employeeFilterErrors}">
                        <li>${error}</li>
                    </c:forEach>
                </ul>
            </div>
        </c:if>
        <div class="card shadow-sm">
            <div class="card-header d-flex justify-content-between align-items-center">
                <span><fmt:message key="employee.admin.table.title" /></span>
                <span class="badge bg-secondary-subtle text-secondary">
                    <fmt:message key="employee.admin.table.badge">
                        <fmt:param value="${summary.filtered}" />
                    </fmt:message>
                </span>
            </div>
            <div class="table-responsive">
                <table class="table table-hover align-middle mb-0">
                    <thead>
                        <tr>
                            <th><fmt:message key="employee.admin.table.header.employee" /></th>
                            <th class="d-none d-md-table-cell"><fmt:message key="employee.admin.table.header.email" /></th>
                            <th class="d-none d-lg-table-cell"><fmt:message key="employee.admin.table.header.headquarters" /></th>
                            <th><fmt:message key="employee.admin.table.header.status" /></th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:choose>
                            <c:when test="${empty employees}">
                                <tr>
                                    <td colspan="4" class="text-center text-muted py-4"><fmt:message key="employee.admin.table.empty" /></td>
                                </tr>
                            </c:when>
                            <c:otherwise>
                                <c:forEach var="employee" items="${employees}">
                                    <tr>
                                        <td>
                                            <div class="fw-semibold">${not empty employee.fullName ? employee.fullName : employee.employeeName}</div>
                                            <div class="text-muted small">${roleNames[employee.roleId]}</div>
                                        </td>
                                        <td class="d-none d-md-table-cell">${employee.email}</td>
                                        <td class="d-none d-lg-table-cell">${headquartersNames[employee.headquartersId]}</td>
                                        <td>
                                            <c:set var="employeeStatusKey" value="${employee.activeStatus ? 'employee.admin.status.active' : 'employee.admin.status.inactive'}" />
                                            <span class="badge ${employee.activeStatus ? 'bg-success-subtle text-success' : 'bg-danger-subtle text-danger'}">
                                                <fmt:message key="${employeeStatusKey}" />
                                            </span>
                                        </td>
                                    </tr>
                                </c:forEach>
                            </c:otherwise>
                        </c:choose>
                    </tbody>
                </table>
            </div>
        </div>
        <div class="mt-3">
            <a class="btn btn-outline-brand" href="${ctx}/app/rentals/private"><i class="bi bi-speedometer"></i> <fmt:message key="employee.admin.back" /></a>
        </div>
    </div>
</div>
<%@ include file="/common/footer.jsp" %>
