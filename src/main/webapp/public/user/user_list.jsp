<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ include file="/common/header.jsp" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<fmt:message key="public.user.list.filters.search.placeholder" var="userSearchPlaceholder" />
<section class="mb-4">
    <div class="row g-4">
        <div class="col-lg-4">
            <div class="card card-common">
                <div class="card-header"><fmt:message key="public.user.list.filters.title" /></div>
                <div class="card-body">
                    <form method="get" action="${ctx}/public/users">
                        <div class="mb-3">
                            <label for="search" class="form-label"><fmt:message key="public.user.list.filters.search.label" /></label>
                            <input type="text" class="form-control" id="search" name="search"
                                   placeholder="${userSearchPlaceholder}" value="${search}" />
                        </div>
                        <div class="mb-3">
                            <label for="role" class="form-label"><fmt:message key="common.field.role" /></label>
                            <select class="form-select" id="role" name="role">
                                <option value=""><fmt:message key="common.option.all" /></option>
                                <c:forEach var="role" items="${roles}">
                                    <option value="${role.roleId}" ${role.roleId == selectedRoleId ? 'selected' : ''}>
                                        ${role.roleName}
                                    </option>
                                </c:forEach>
                            </select>
                        </div>
                        <div class="mb-3">
                            <label for="active" class="form-label"><fmt:message key="common.field.status" /></label>
                            <select class="form-select" id="active" name="active">
                                <option value="all" ${selectedActive == 'all' ? 'selected' : ''}><fmt:message key="common.option.all" /></option>
                                <option value="active" ${selectedActive == 'active' ? 'selected' : ''}><fmt:message key="status.active.plural" /></option>
                                <option value="inactive" ${selectedActive == 'inactive' ? 'selected' : ''}><fmt:message key="status.inactive.plural" /></option>
                            </select>
                        </div>
                        <div class="mb-3">
                            <label for="sort" class="form-label"><fmt:message key="public.user.list.filters.sort.label" /></label>
                            <select class="form-select" id="sort" name="sort">
                                <option value="createdDesc" ${selectedSort == 'createdDesc' ? 'selected' : ''}><fmt:message key="public.user.list.filters.sort.recent" /></option>
                                <option value="nameAsc" ${selectedSort == 'nameAsc' ? 'selected' : ''}><fmt:message key="public.user.list.filters.sort.name" /></option>
                                <option value="roleAsc" ${selectedSort == 'roleAsc' ? 'selected' : ''}><fmt:message key="public.user.list.filters.sort.role" /></option>
                            </select>
                        </div>
                        <input type="hidden" name="size" value="${size}" />
                        <div class="d-flex gap-2">
                            <button type="submit" class="btn btn-brand flex-grow-1"><i class="bi bi-search"></i> <fmt:message key="common.button.search" /></button>
                            <a href="${ctx}/public/users" class="btn btn-outline-secondary" title="<fmt:message key='public.user.list.filters.reset' />">
                                <i class="bi bi-arrow-counterclockwise"></i>
                            </a>
                        </div>
                    </form>
                </div>
            </div>
            <jsp:include page="/public/user/user_form.jsp" />
        </div>
        <div class="col-lg-8">
            <div class="card card-common mb-4">
                <div class="card-header"><fmt:message key="public.user.list.metrics.title" /></div>
                <div class="card-body">
                    <div class="row text-center">
                        <div class="col-6 col-md-3">
                            <div class="metric-label"><fmt:message key="public.user.list.metrics.results" /></div>
                            <div class="metric-value">${total}</div>
                        </div>
                        <div class="col-6 col-md-3">
                            <div class="metric-label"><fmt:message key="public.user.list.metrics.active" /></div>
                            <div class="metric-value text-success">${totalActive}</div>
                        </div>
                        <div class="col-6 col-md-3 mt-3 mt-md-0">
                            <div class="metric-label"><fmt:message key="public.user.list.metrics.inactive" /></div>
                            <div class="metric-value text-danger">${totalInactive}</div>
                        </div>
                        <div class="col-6 col-md-3 mt-3 mt-md-0">
                            <div class="metric-label"><fmt:message key="public.user.list.metrics.lastRegistration" /></div>
                            <div class="metric-value">${lastRegistration}</div>
                        </div>
                    </div>
                </div>
            </div>

            <c:if test="${not empty error}">
                <div class="alert alert-warning shadow-soft">
                    <ul class="mb-0">
                        <c:forEach var="entry" items="${error}">
                            <li>${entry.value}</li>
                        </c:forEach>
                    </ul>
                </div>
            </c:if>

            <c:choose>
                <c:when test="${empty items}">
                    <div class="card card-common">
                        <div class="card-body text-center text-muted">
                            <i class="bi bi-people display-6 d-block mb-2"></i>
                            <fmt:message key="public.user.list.empty" />
                        </div>
                    </div>
                </c:when>
                <c:otherwise>
                    <div class="table-responsive">
                        <table class="table table-hover align-middle">
                            <thead>
                                <tr>
                                    <th><fmt:message key="common.table.header.name" /></th>
                                    <th><fmt:message key="common.table.header.email" /></th>
                                    <th class="d-none d-md-table-cell"><fmt:message key="common.table.header.role" /></th>
                                    <th class="d-none d-md-table-cell"><fmt:message key="common.table.header.status" /></th>
                                    <th></th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:forEach var="user" items="${items}">
                                    <tr>
                                        <td>
                                            <strong>${empty user.firstName ? user.username : user.firstName}</strong>
                                            <c:if test="${not empty user.lastName1}">
                                                <span>${user.lastName1}</span>
                                            </c:if>
                                        </td>
                                        <td>${user.email}</td>
                                        <td class="d-none d-md-table-cell">${roleNames[user.roleId]}</td>
                                        <td class="d-none d-md-table-cell">
                                            <span class="badge ${user.activeStatus ? 'bg-success-subtle text-success' : 'bg-danger-subtle text-danger'}">
                                                <fmt:message key="${user.activeStatus ? 'status.active' : 'status.inactive'}" />
                                            </span>
                                        </td>
                                        <td class="text-end">
                                            <a class="btn btn-sm btn-outline-brand"
                                               href="${ctx}/public/users/detail?id=${user.userId}">
                                                <i class="bi bi-eye"></i> <fmt:message key="common.button.view" />
                                            </a>
                                        </td>
                                    </tr>
                                </c:forEach>
                            </tbody>
                        </table>
                    </div>

                    <c:if test="${totalPages > 1}">
                        <c:url var="prevUrl" value="/public/users">
                            <c:if test="${not empty search}">
                                <c:param name="search" value="${search}" />
                            </c:if>
                            <c:if test="${not empty selectedRoleId}">
                                <c:param name="role" value="${selectedRoleId}" />
                            </c:if>
                            <c:if test="${not empty selectedActive}">
                                <c:param name="active" value="${selectedActive}" />
                            </c:if>
                            <c:if test="${not empty selectedSort}">
                                <c:param name="sort" value="${selectedSort}" />
                            </c:if>
                            <c:param name="size" value="${size}" />
                            <c:param name="page" value="${prevPage}" />
                        </c:url>
                        <c:url var="nextUrl" value="/public/users">
                            <c:if test="${not empty search}">
                                <c:param name="search" value="${search}" />
                            </c:if>
                            <c:if test="${not empty selectedRoleId}">
                                <c:param name="role" value="${selectedRoleId}" />
                            </c:if>
                            <c:if test="${not empty selectedActive}">
                                <c:param name="active" value="${selectedActive}" />
                            </c:if>
                            <c:if test="${not empty selectedSort}">
                                <c:param name="sort" value="${selectedSort}" />
                            </c:if>
                            <c:param name="size" value="${size}" />
                            <c:param name="page" value="${nextPage}" />
                        </c:url>
                        <nav aria-label="<fmt:message key='public.user.list.pagination.aria' />" class="mt-3">
                            <ul class="pagination justify-content-center">
                                <li class="page-item ${!hasPrev ? 'disabled' : ''}">
                                    <a class="page-link" href="${ctx}${prevUrl}" aria-label="<fmt:message key='common.pagination.previous' />">
                                        <span aria-hidden="true">&laquo;</span>
                                    </a>
                                </li>
                                <li class="page-item disabled"><span class="page-link">
                                    <fmt:message key="common.pagination.pageOf">
                                        <fmt:param value="${page}" />
                                        <fmt:param value="${totalPages}" />
                                    </fmt:message>
                                </span></li>
                                <li class="page-item ${!hasNext ? 'disabled' : ''}">
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
