<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ include file="/common/header.jsp" %>
<c:set var="filters" value="${employeeFilters}" />
<c:set var="summary" value="${employeeSummary}" />
<c:set var="roleNames" value="${employeeRoleNames}" />
<c:set var="headquartersNames" value="${employeeHeadquartersNames}" />
<div class="row g-4">
    <div class="col-lg-4">
        <div class="card shadow-sm">
            <div class="card-header">Filtra la plantilla</div>
            <div class="card-body">
                <form method="get" action="${ctx}/app/employees/private" class="vstack gap-3">
                    <div>
                        <label class="form-label" for="search">Nombre o correo</label>
                        <input class="form-control" type="text" id="search" name="search" value="${filters.search}" placeholder="Ej. Laura" />
                    </div>
                    <div>
                        <label class="form-label" for="headquarters">Sede</label>
                        <select class="form-select" id="headquarters" name="headquarters">
                            <option value="">Todas</option>
                            <c:forEach var="entry" items="${headquartersNames}">
                                <option value="${entry.key}" ${entry.key == filters.headquarters ? 'selected' : ''}>${entry.value}</option>
                            </c:forEach>
                        </select>
                    </div>
                    <div>
                        <label class="form-label" for="active">Estado</label>
                        <select class="form-select" id="active" name="active">
                            <option value="all" ${filters.active == 'all' ? 'selected' : ''}>Todos</option>
                            <option value="active" ${filters.active == 'active' ? 'selected' : ''}>Activos</option>
                            <option value="inactive" ${filters.active == 'inactive' ? 'selected' : ''}>Inactivos</option>
                        </select>
                    </div>
                    <div class="d-flex gap-2">
                        <button type="submit" class="btn btn-brand flex-grow-1"><i class="bi bi-search"></i> Aplicar</button>
                        <a class="btn btn-outline-secondary" href="${ctx}/app/employees/private" title="Restablecer filtros">
                            <i class="bi bi-arrow-counterclockwise"></i>
                        </a>
                    </div>
                </form>
            </div>
        </div>
        <div class="card shadow-sm mt-4">
            <div class="card-header">Indicadores</div>
            <div class="card-body">
                <ul class="list-unstyled mb-0">
                    <li class="d-flex justify-content-between"><span>Total registrados</span><strong>${summary.total}</strong></li>
                    <li class="d-flex justify-content-between text-success"><span>Activos</span><strong>${summary.active}</strong></li>
                    <li class="d-flex justify-content-between text-danger"><span>Inactivos</span><strong>${summary.inactive}</strong></li>
                    <li class="d-flex justify-content-between"><span>Coinciden con filtros</span><strong>${summary.filtered}</strong></li>
                    <li class="d-flex justify-content-between"><span>Sedes representadas</span><strong>${summary.headquarters}</strong></li>
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
                <span>Equipo actual</span>
                <span class="badge bg-secondary-subtle text-secondary">${summary.filtered} resultados</span>
            </div>
            <div class="table-responsive">
                <table class="table table-hover align-middle mb-0">
                    <thead>
                        <tr>
                            <th>Empleado</th>
                            <th class="d-none d-md-table-cell">Correo</th>
                            <th class="d-none d-lg-table-cell">Sede</th>
                            <th>Estado</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:choose>
                            <c:when test="${empty employees}">
                                <tr>
                                    <td colspan="4" class="text-center text-muted py-4">Ningún empleado coincide con los filtros.</td>
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
                                            <span class="badge ${employee.activeStatus ? 'bg-success-subtle text-success' : 'bg-danger-subtle text-danger'}">
                                                ${employee.activeStatus ? 'Activo' : 'Inactivo'}
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
            <a class="btn btn-outline-brand" href="${ctx}/app/rentals/private"><i class="bi bi-speedometer"></i> Volver al panel</a>
        </div>
    </div>
</div>
<%@ include file="/common/footer.jsp" %>
