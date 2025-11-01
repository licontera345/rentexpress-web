<%@ include file="/common/header.jsp" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="filters" value="${employeeFilters}" />
<c:set var="pagination" value="${employeePagination}" />
<section class="mb-4">
    <div class="row g-4">
        <div class="col-lg-4">
            <div class="card card-common">
                <div class="card-header"><fmt:message key="employee.public.list.filters.title" /></div>
                <div class="card-body">
                    <form method="get" action="${ctx}/public/employees">
                        <div class="mb-3">
                            <label for="search" class="form-label"><fmt:message key="employee.public.list.filters.search.label" /></label>
                            <input type="text" class="form-control" id="search" name="search"
                                   placeholder="<fmt:message key="employee.public.list.filters.search.placeholder" />" value="${filters.search}" />
                        </div>
                        <div class="mb-3">
                            <label for="role" class="form-label"><fmt:message key="common.field.role" /></label>
                            <select class="form-select" id="role" name="role">
                                <option value=""><fmt:message key="common.option.all" /></option>
                                <c:forEach var="role" items="${employeeRoles}">
                                    <option value="${role.roleId}" ${role.roleId == selectedEmployeeRole ? 'selected' : ''}>
                                        ${role.roleName}
                                    </option>
                                </c:forEach>
                            </select>
                        </div>
                        <div class="mb-3">
                            <label for="headquarters" class="form-label"><fmt:message key="common.field.headquarters" /></label>
                            <select class="form-select" id="headquarters" name="headquarters">
                                <option value=""><fmt:message key="common.option.all.feminine" /></option>
                                <c:forEach var="hq" items="${employeeHeadquarters}">
                                    <option value="${hq.id}" ${hq.id == selectedEmployeeHeadquarters ? 'selected' : ''}>
                                        ${hq.name}
                                    </option>
                                </c:forEach>
                            </select>
                        </div>
                        <div class="mb-3">
                            <label for="active" class="form-label"><fmt:message key="common.field.status" /></label>
                            <select class="form-select" id="active" name="active">
                                <option value="all" ${selectedEmployeeActive == 'all' ? 'selected' : ''}><fmt:message key="common.option.all" /></option>
                                <option value="active" ${selectedEmployeeActive == 'active' ? 'selected' : ''}><fmt:message key="common.status.active.plural" /></option>
                                <option value="inactive" ${selectedEmployeeActive == 'inactive' ? 'selected' : ''}><fmt:message key="common.status.inactive.plural" /></option>
                            </select>
                        </div>
                        <div class="mb-3">
                            <label for="sort" class="form-label"><fmt:message key="employee.public.list.filters.sort.label" /></label>
                            <select class="form-select" id="sort" name="sort">
                                <option value="createdDesc" ${selectedEmployeeSort == 'createdDesc' ? 'selected' : ''}><fmt:message key="employee.public.list.filters.sort.recent" /></option>
                                <option value="nameAsc" ${selectedEmployeeSort == 'nameAsc' ? 'selected' : ''}><fmt:message key="employee.public.list.filters.sort.name" /></option>
                                <option value="hqAsc" ${selectedEmployeeSort == 'hqAsc' ? 'selected' : ''}><fmt:message key="employee.public.list.filters.sort.headquarters" /></option>
                            </select>
                        </div>
                        <div class="d-flex gap-2">
                            <button type="submit" class="btn btn-brand flex-grow-1"><i class="bi bi-search"></i> <fmt:message key="common.button.search" /></button>
                            <a href="${ctx}/public/employees" class="btn btn-outline-secondary" title="<fmt:message key='public.employee.list.filters.reset' />">
                                <i class="bi bi-arrow-counterclockwise"></i>
                            </a>
                        </div>
                    </form>
                </div>
            </div>
            <jsp:include page="/public/employee/employee_form.jsp" />
        </div>
        <div class="col-lg-8">
            <div class="card card-common mb-4">
                <div class="card-header"><fmt:message key="employee.public.list.metrics.title" /></div>
                <div class="card-body">
                    <div class="row text-center">
                        <div class="col-6 col-md-3">
                            <div class="metric-label"><fmt:message key="employee.public.list.metrics.results" /></div>
                            <div class="metric-value">${pagination.total}</div>
                        </div>
                        <div class="col-6 col-md-3">
                            <div class="metric-label"><fmt:message key="employee.public.list.metrics.active" /></div>
                            <div class="metric-value text-success">${employeeSummary.active}</div>
                        </div>
                        <div class="col-6 col-md-3 mt-3 mt-md-0">
                            <div class="metric-label"><fmt:message key="employee.public.list.metrics.inactive" /></div>
                            <div class="metric-value text-danger">${employeeSummary.inactive}</div>
                        </div>
                        <div class="col-6 col-md-3 mt-3 mt-md-0">
                            <div class="metric-label"><fmt:message key="employee.public.list.metrics.headquarters" /></div>
                            <div class="metric-value">${employeeSummary.headquarters}</div>
                        </div>
                    </div>
                </div>
            </div>

            <c:if test="${not empty employeeFilterErrors}">
                <div class="alert alert-warning shadow-soft">
                    <ul class="mb-0">
                        <c:forEach var="error" items="${employeeFilterErrors}">
                            <li>${error}</li>
                        </c:forEach>
                    </ul>
                </div>
            </c:if>

            <c:choose>
                <c:when test="${empty employees}">
                    <div class="card card-common">
                        <div class="card-body text-center text-muted">
                            <i class="bi bi-person-workspace display-6 d-block mb-2"></i>
                            <fmt:message key="employee.public.list.empty" />
                        </div>
                    </div>
                </c:when>
                <c:otherwise>
                    <div class="table-responsive">
                        <table class="table table-hover align-middle">
                            <thead>
                                <tr>
                                    <th><fmt:message key="common.table.header.name" /></th>
                                    <th class="d-none d-md-table-cell"><fmt:message key="common.table.header.email" /></th>
                                    <th class="d-none d-md-table-cell"><fmt:message key="common.table.header.role" /></th>
                                    <th class="d-none d-lg-table-cell"><fmt:message key="common.table.header.headquarters" /></th>
                                    <th><fmt:message key="common.table.header.status" /></th>
                                    <th></th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:forEach var="employee" items="${employees}">
                                    <tr>
                                        <td>
                                            <strong>${empty employee.firstName ? employee.employeeName : employee.firstName}</strong>
                                            <c:if test="${not empty employee.lastName1}">
                                                <span>${employee.lastName1}</span>
                                            </c:if>
                                        </td>
                                        <td class="d-none d-md-table-cell">${employee.email}</td>
                                        <td class="d-none d-md-table-cell">${employeeRoleNames[employee.roleId]}</td>
                                        <td class="d-none d-lg-table-cell">${employeeHeadquartersNames[employee.headquartersId]}</td>
                                        <td>
                                            <span class="badge ${employee.activeStatus ? 'bg-success-subtle text-success' : 'bg-danger-subtle text-danger'}">
                                                <fmt:message key="${employee.activeStatus ? 'common.status.active' : 'common.status.inactive'}" />
                                            </span>
                                        </td>
                                        <td class="text-end">
                                            <a class="btn btn-sm btn-outline-brand"
                                               href="${ctx}/public/employees?action=view&amp;employeeId=${employee.employeeId}">
                                                <i class="bi bi-eye"></i> <fmt:message key="common.button.view" />
                                            </a>
                                        </td>
                                    </tr>
                                </c:forEach>
                            </tbody>
                        </table>
                    </div>

                    <c:if test="${pagination.totalPages > 1}">
                        <c:set var="prevPage" value="${pagination.page - 1}" />
                        <c:set var="nextPage" value="${pagination.page + 1}" />
                        <c:if test="${prevPage < 1}"><c:set var="prevPage" value="1" /></c:if>
                        <c:if test="${nextPage > pagination.totalPages}"><c:set var="nextPage" value="${pagination.totalPages}" /></c:if>
                        <c:url var="prevUrl" value="/public/employees">
                            <c:forEach var="entry" items="${filters}">
                                <c:if test="${not empty entry.value && entry.key ne 'page'}">
                                    <c:param name="${entry.key}" value="${entry.value}" />
                                </c:if>
                            </c:forEach>
                            <c:param name="page" value="${prevPage}" />
                        </c:url>
                        <c:url var="nextUrl" value="/public/employees">
                            <c:forEach var="entry" items="${filters}">
                                <c:if test="${not empty entry.value && entry.key ne 'page'}">
                                    <c:param name="${entry.key}" value="${entry.value}" />
                                </c:if>
                            </c:forEach>
                            <c:param name="page" value="${nextPage}" />
                        </c:url>
                        <nav aria-label="<fmt:message key='public.employee.list.pagination.aria' />" class="mt-3">
                            <ul class="pagination justify-content-center">
                                <li class="page-item ${!pagination.hasPrev ? 'disabled' : ''}">
                                    <a class="page-link" href="${ctx}${prevUrl}" aria-label="<fmt:message key='common.pagination.previous' />">
                                        <span aria-hidden="true">&laquo;</span>
                                    </a>
                                </li>
                                <li class="page-item disabled"><span class="page-link">
                                    <fmt:message key="common.pagination.pageOf">
                                        <fmt:param value="${pagination.page}" />
                                        <fmt:param value="${pagination.totalPages}" />
                                    </fmt:message>
                                </span></li>
                                <li class="page-item ${!pagination.hasNext ? 'disabled' : ''}">
                                    <a class="page-link" href="${ctx}${nextUrl}" aria-label="<fmt:message key='common.pagination.next' />">
                                        <span aria-hidden="true">&raquo;</span>
                                    </a>
                                </li>
                            </ul>
                        </nav>
                    </c:if>
                </c:otherwise>
            </c:choose>
        </div>
    </div>
</section>
<%@ include file="/common/footer.jsp" %>
