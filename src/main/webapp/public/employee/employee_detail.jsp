<%@ include file="/common/header.jsp" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="employee" value="${selectedEmployee}" />
<section class="mb-4">
    <div class="d-flex justify-content-between align-items-center mb-3">
        <h1 class="h3 mb-0"><fmt:message key="public.employee.detail.title" /></h1>
        <a href="${ctx}/public/employees" class="btn btn-outline-secondary"><i class="bi bi-arrow-left"></i> <fmt:message key="common.button.back" /></a>
    </div>
    <div class="row g-4">
        <div class="col-lg-8">
            <div class="card card-common">
                <div class="card-header"><fmt:message key="public.employee.detail.section.professional" /></div>
                <div class="card-body">
                    <dl class="row mb-0">
                        <dt class="col-sm-4"><fmt:message key="public.employee.detail.field.fullName" /></dt>
                        <dd class="col-sm-8">
                            <c:choose>
                                <c:when test="${not empty employee.firstName}">
                                    ${employee.firstName} ${employee.lastName1} ${employee.lastName2}
                                </c:when>
                                <c:otherwise>
                                    ${employee.employeeName}
                                </c:otherwise>
                            </c:choose>
                        </dd>
                        <dt class="col-sm-4"><fmt:message key="public.employee.detail.field.email" /></dt>
                        <dd class="col-sm-8">${employee.email}</dd>
                        <dt class="col-sm-4"><fmt:message key="public.employee.detail.field.phone" /></dt>
                        <dd class="col-sm-8">
                            <c:choose>
                                <c:when test="${not empty employee.phone}">${employee.phone}</c:when>
                                <c:otherwise><fmt:message key="public.employee.detail.field.phone.empty" /></c:otherwise>
                            </c:choose>
                        </dd>
                        <dt class="col-sm-4"><fmt:message key="public.employee.detail.field.role" /></dt>
                        <dd class="col-sm-8">
                            <c:choose>
                                <c:when test="${not empty selectedEmployeeRole}">${selectedEmployeeRole}</c:when>
                                <c:otherwise><fmt:message key="public.employee.detail.field.role.empty" /></c:otherwise>
                            </c:choose>
                        </dd>
                        <dt class="col-sm-4"><fmt:message key="public.employee.detail.field.headquarters" /></dt>
                        <dd class="col-sm-8">
                            <c:choose>
                                <c:when test="${not empty selectedEmployeeHeadquarters}">${selectedEmployeeHeadquarters}</c:when>
                                <c:otherwise><fmt:message key="public.employee.detail.field.headquarters.empty" /></c:otherwise>
                            </c:choose>
                        </dd>
                        <dt class="col-sm-4"><fmt:message key="public.employee.detail.field.status" /></dt>
                        <dd class="col-sm-8">
                            <span class="badge ${employee.activeStatus ? 'bg-success-subtle text-success' : 'bg-danger-subtle text-danger'}">
                                <fmt:message key="${employee.activeStatus ? 'status.active' : 'status.inactive'}" />
                            </span>
                        </dd>
                        <dt class="col-sm-4"><fmt:message key="public.employee.detail.field.created" /></dt>
                        <dd class="col-sm-8">
                            <c:choose>
                                <c:when test="${not empty employee.createdAt}">${employee.createdAt}</c:when>
                                <c:otherwise>-</c:otherwise>
                            </c:choose>
                        </dd>
                        <dt class="col-sm-4"><fmt:message key="public.employee.detail.field.updated" /></dt>
                        <dd class="col-sm-8">
                            <c:choose>
                                <c:when test="${not empty employee.updatedAt}">${employee.updatedAt}</c:when>
                                <c:otherwise>-</c:otherwise>
                            </c:choose>
                        </dd>
                    </dl>
                </div>
            </div>
        </div>
        <div class="col-lg-4">
            <div class="card card-common">
                <div class="card-header"><fmt:message key="public.employee.detail.idea.title" /></div>
                <div class="card-body">
                    <p class="text-muted"><fmt:message key="public.employee.detail.idea.description" /></p>
                    <ul class="list-unstyled small mb-0">
                        <li class="mb-2"><i class="bi bi-check-circle text-success"></i> <fmt:message key="public.employee.detail.idea.item1" /></li>
                        <li class="mb-2"><i class="bi bi-check-circle text-success"></i> <fmt:message key="public.employee.detail.idea.item2" /></li>
                        <li><i class="bi bi-check-circle text-success"></i> <fmt:message key="public.employee.detail.idea.item3" /></li>
                    </ul>
                </div>
            </div>
            <jsp:include page="/public/employee/employee_form.jsp" />
        </div>
    </div>
</section>
<%@ include file="/common/footer.jsp" %>
