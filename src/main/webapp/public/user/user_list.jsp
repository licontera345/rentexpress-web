<%@ include file="/common/header.jsp" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="filters" value="${userFilters}" />
<c:set var="pagination" value="${userPagination}" />
<section class="mb-4">
    <div class="row g-4">
        <div class="col-lg-4">
            <div class="card card-common">
                <div class="card-header">Filtros de búsqueda</div>
                <div class="card-body">
                    <form method="get" action="${ctx}/public/users">
                        <div class="mb-3">
                            <label for="search" class="form-label">Nombre, email o usuario</label>
                            <input type="text" class="form-control" id="search" name="search"
                                   placeholder="Ej. María" value="${filters.search}" />
                        </div>
                        <div class="mb-3">
                            <label for="role" class="form-label">Rol</label>
                            <select class="form-select" id="role" name="role">
                                <option value="">Todos</option>
                                <c:forEach var="role" items="${userRoles}">
                                    <option value="${role.roleId}" ${role.roleId == selectedRoleId ? 'selected' : ''}>
                                        ${role.roleName}
                                    </option>
                                </c:forEach>
                            </select>
                        </div>
                        <div class="mb-3">
                            <label for="active" class="form-label">Estado</label>
                            <select class="form-select" id="active" name="active">
                                <option value="all" ${selectedActive == 'all' ? 'selected' : ''}>Todos</option>
                                <option value="active" ${selectedActive == 'active' ? 'selected' : ''}>Activos</option>
                                <option value="inactive" ${selectedActive == 'inactive' ? 'selected' : ''}>Inactivos</option>
                            </select>
                        </div>
                        <div class="mb-3">
                            <label for="sort" class="form-label">Ordenar por</label>
                            <select class="form-select" id="sort" name="sort">
                                <option value="createdDesc" ${selectedSort == 'createdDesc' ? 'selected' : ''}>Recientes primero</option>
                                <option value="nameAsc" ${selectedSort == 'nameAsc' ? 'selected' : ''}>Nombre (A-Z)</option>
                                <option value="roleAsc" ${selectedSort == 'roleAsc' ? 'selected' : ''}>Rol (A-Z)</option>
                            </select>
                        </div>
                        <div class="d-flex gap-2">
                            <button type="submit" class="btn btn-brand flex-grow-1"><i class="bi bi-search"></i> Buscar</button>
                            <a href="${ctx}/public/users" class="btn btn-outline-secondary" title="Limpiar filtros">
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
                <div class="card-header">Resumen</div>
                <div class="card-body">
                    <div class="row text-center">
                        <div class="col-6 col-md-3">
                            <div class="metric-label">Resultados</div>
                            <div class="metric-value">${pagination.total}</div>
                        </div>
                        <div class="col-6 col-md-3">
                            <div class="metric-label">Activos</div>
                            <div class="metric-value text-success">${userSummary.active}</div>
                        </div>
                        <div class="col-6 col-md-3 mt-3 mt-md-0">
                            <div class="metric-label">Inactivos</div>
                            <div class="metric-value text-danger">${userSummary.inactive}</div>
                        </div>
                        <div class="col-6 col-md-3 mt-3 mt-md-0">
                            <div class="metric-label">Último alta</div>
                            <div class="metric-value">${userSummary.lastRegistration}</div>
                        </div>
                    </div>
                </div>
            </div>

            <c:if test="${not empty userFilterErrors}">
                <div class="alert alert-warning shadow-soft">
                    <ul class="mb-0">
                        <c:forEach var="error" items="${userFilterErrors}">
                            <li>${error}</li>
                        </c:forEach>
                    </ul>
                </div>
            </c:if>

            <c:choose>
                <c:when test="${empty users}">
                    <div class="card card-common">
                        <div class="card-body text-center text-muted">
                            <i class="bi bi-people display-6 d-block mb-2"></i>
                            No se encontraron usuarios con los filtros seleccionados.
                        </div>
                    </div>
                </c:when>
                <c:otherwise>
                    <div class="table-responsive">
                        <table class="table table-hover align-middle">
                            <thead>
                                <tr>
                                    <th>Nombre</th>
                                    <th>Email</th>
                                    <th class="d-none d-md-table-cell">Rol</th>
                                    <th class="d-none d-md-table-cell">Estado</th>
                                    <th></th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:forEach var="user" items="${users}">
                                    <tr>
                                        <td>
                                            <strong>${empty user.firstName ? user.username : user.firstName}</strong>
                                            <c:if test="${not empty user.lastName1}">
                                                <span>${user.lastName1}</span>
                                            </c:if>
                                        </td>
                                        <td>${user.email}</td>
                                        <td class="d-none d-md-table-cell">${userRoleNames[user.roleId]}</td>
                                        <td class="d-none d-md-table-cell">
                                            <span class="badge ${user.activeStatus ? 'bg-success-subtle text-success' : 'bg-danger-subtle text-danger'}">
                                                ${user.activeStatus ? 'Activo' : 'Inactivo'}
                                            </span>
                                        </td>
                                        <td class="text-end">
                                            <a class="btn btn-sm btn-outline-brand"
                                               href="${ctx}/public/users?action=view&amp;userId=${user.userId}">
                                                <i class="bi bi-eye"></i> Ver
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
                        <c:if test="${prevPage < 1}">
                            <c:set var="prevPage" value="1" />
                        </c:if>
                        <c:if test="${nextPage > pagination.totalPages}">
                            <c:set var="nextPage" value="${pagination.totalPages}" />
                        </c:if>
                        <c:url var="prevUrl" value="/public/users">
                            <c:forEach var="entry" items="${filters}">
                                <c:if test="${not empty entry.value && entry.key ne 'page'}">
                                    <c:param name="${entry.key}" value="${entry.value}" />
                                </c:if>
                            </c:forEach>
                            <c:param name="page" value="${prevPage}" />
                        </c:url>
                        <c:url var="nextUrl" value="/public/users">
                            <c:forEach var="entry" items="${filters}">
                                <c:if test="${not empty entry.value && entry.key ne 'page'}">
                                    <c:param name="${entry.key}" value="${entry.value}" />
                                </c:if>
                            </c:forEach>
                            <c:param name="page" value="${nextPage}" />
                        </c:url>
                        <nav aria-label="Paginación de usuarios" class="mt-3">
                            <ul class="pagination justify-content-center">
                                <li class="page-item ${!pagination.hasPrev ? 'disabled' : ''}">
                                    <a class="page-link" href="${ctx}${prevUrl}" aria-label="Anterior">
                                        <span aria-hidden="true">&laquo;</span>
                                    </a>
                                </li>
                                <li class="page-item disabled"><span class="page-link">Página ${pagination.page} de ${pagination.totalPages}</span></li>
                                <li class="page-item ${!pagination.hasNext ? 'disabled' : ''}">
                                    <a class="page-link" href="${ctx}${nextUrl}" aria-label="Siguiente">
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
