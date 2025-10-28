<%@ include file="/common/header.jsp" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="filters" value="${employeeFilters}" />
<c:set var="pagination" value="${employeePagination}" />
<section class="mb-4">
    <div class="row g-4">
        <div class="col-lg-4">
            <div class="card card-common">
                <div class="card-header">Filtra al equipo</div>
                <div class="card-body">
                    <form method="get" action="${ctx}/public/employees">
                        <div class="mb-3">
                            <label for="search" class="form-label">Nombre o correo</label>
                            <input type="text" class="form-control" id="search" name="search"
                                   placeholder="Ej. Patricia" value="${filters.search}" />
                        </div>
                        <div class="mb-3">
                            <label for="role" class="form-label">Rol</label>
                            <select class="form-select" id="role" name="role">
                                <option value="">Todos</option>
                                <c:forEach var="role" items="${employeeRoles}">
                                    <option value="${role.roleId}" ${role.roleId == selectedEmployeeRole ? 'selected' : ''}>
                                        ${role.roleName}
                                    </option>
                                </c:forEach>
                            </select>
                        </div>
                        <div class="mb-3">
                            <label for="headquarters" class="form-label">Sede</label>
                            <select class="form-select" id="headquarters" name="headquarters">
                                <option value="">Todas</option>
                                <c:forEach var="hq" items="${employeeHeadquarters}">
                                    <option value="${hq.headquartersId}" ${hq.headquartersId == selectedEmployeeHeadquarters ? 'selected' : ''}>
                                        ${hq.name}
                                    </option>
                                </c:forEach>
                            </select>
                        </div>
                        <div class="mb-3">
                            <label for="active" class="form-label">Estado</label>
                            <select class="form-select" id="active" name="active">
                                <option value="all" ${selectedEmployeeActive == 'all' ? 'selected' : ''}>Todos</option>
                                <option value="active" ${selectedEmployeeActive == 'active' ? 'selected' : ''}>Activos</option>
                                <option value="inactive" ${selectedEmployeeActive == 'inactive' ? 'selected' : ''}>Inactivos</option>
                            </select>
                        </div>
                        <div class="mb-3">
                            <label for="sort" class="form-label">Ordenar por</label>
                            <select class="form-select" id="sort" name="sort">
                                <option value="createdDesc" ${selectedEmployeeSort == 'createdDesc' ? 'selected' : ''}>Recientes primero</option>
                                <option value="nameAsc" ${selectedEmployeeSort == 'nameAsc' ? 'selected' : ''}>Nombre (A-Z)</option>
                                <option value="hqAsc" ${selectedEmployeeSort == 'hqAsc' ? 'selected' : ''}>Sede (A-Z)</option>
                            </select>
                        </div>
                        <div class="d-flex gap-2">
                            <button type="submit" class="btn btn-brand flex-grow-1"><i class="bi bi-search"></i> Buscar</button>
                            <a href="${ctx}/public/employees" class="btn btn-outline-secondary" title="Restablecer filtros">
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
                <div class="card-header">Indicadores rápidos</div>
                <div class="card-body">
                    <div class="row text-center">
                        <div class="col-6 col-md-3">
                            <div class="metric-label">Resultados</div>
                            <div class="metric-value">${pagination.total}</div>
                        </div>
                        <div class="col-6 col-md-3">
                            <div class="metric-label">Activos</div>
                            <div class="metric-value text-success">${employeeSummary.active}</div>
                        </div>
                        <div class="col-6 col-md-3 mt-3 mt-md-0">
                            <div class="metric-label">Inactivos</div>
                            <div class="metric-value text-danger">${employeeSummary.inactive}</div>
                        </div>
                        <div class="col-6 col-md-3 mt-3 mt-md-0">
                            <div class="metric-label">Sedes representadas</div>
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
                            Ningún empleado coincide con los filtros aplicados.
                        </div>
                    </div>
                </c:when>
                <c:otherwise>
                    <div class="table-responsive">
                        <table class="table table-hover align-middle">
                            <thead>
                                <tr>
                                    <th>Nombre</th>
                                    <th class="d-none d-md-table-cell">Correo</th>
                                    <th class="d-none d-md-table-cell">Rol</th>
                                    <th class="d-none d-lg-table-cell">Sede</th>
                                    <th>Estado</th>
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
                                                ${employee.activeStatus ? 'Activo' : 'Inactivo'}
                                            </span>
                                        </td>
                                        <td class="text-end">
                                            <a class="btn btn-sm btn-outline-brand"
                                               href="${ctx}/public/employees?action=view&amp;employeeId=${employee.employeeId}">
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
                        <nav aria-label="Paginación de empleados" class="mt-3">
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
