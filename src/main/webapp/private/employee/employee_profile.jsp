<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<fmt:message key="employee.profile.heading" var="employeeProfileTitle" />
<c:set var="pageTitle" value="${employeeProfileTitle}" />
<%@ include file="/common/header.jsp" %>
<c:set var="employeeDto" value="${requestScope.employee}" />
<c:set var="headquartersDto" value="${requestScope.headquarters}" />
<c:set var="fullName" value="${requestScope.fullName}" />
<c:set var="accountName" value="${not empty employeeDto.employeeName ? employeeDto.employeeName : fullName}" />
<c:choose>
    <c:when test="${isActive}">
        <fmt:message key="employee.profile.status.active" var="statusLabel" />
    </c:when>
    <c:otherwise>
        <fmt:message key="employee.profile.status.inactive" var="statusLabel" />
    </c:otherwise>
</c:choose>
<div class="row justify-content-center">
    <div class="col-lg-8">
        <c:if test="${empty employeeDto}">
            <div class="alert alert-warning shadow-soft mb-4">
                <fmt:message key="employee.profile.loadError" />
            </div>
        </c:if>

        <c:if test="${not empty employeeDto}">
            <div class="card shadow-sm mb-4">
                <div class="card-body p-4">
                    <div class="d-flex justify-content-between align-items-start flex-wrap gap-3 mb-3">
                        <div>
                            <h1 class="h3 fw-bold mb-1">${employeeProfileTitle}</h1>
                            <p class="text-muted mb-0"><fmt:message key="employee.profile.subtitle" /></p>
                        </div>
                        <div class="text-end">
                            <span class="badge ${statusClass} px-3 py-2">${statusLabel}</span>
                            <c:if test="${not empty employeeDto.id}">
                                <p class="text-muted small mb-0 mt-2">
                                    <fmt:message key="employee.profile.internalId">
                                        <fmt:param value="${employeeDto.id}" />
                                    </fmt:message>
                                </p>
                            </c:if>
                        </div>
                    </div>
                    <div class="row g-3">
                        <div class="col-md-6">
                            <label class="form-label"><fmt:message key="employee.profile.label.fullName" /></label>
                            <input type="text" class="form-control" value="${empty fullName ? '-' : fullName}" disabled>
                        </div>
                        <div class="col-md-6">
                            <label class="form-label"><fmt:message key="employee.profile.label.accountName" /></label>
                            <input type="text" class="form-control" value="${empty accountName ? '-' : accountName}" disabled>
                        </div>
                        <div class="col-md-6">
                            <label class="form-label"><fmt:message key="employee.profile.label.email" /></label>
                            <input type="email" class="form-control" value="${empty employeeDto.email ? '-' : employeeDto.email}" disabled>
                        </div>
                        <div class="col-md-6">
                            <label class="form-label"><fmt:message key="employee.profile.label.phone" /></label>
                            <input type="text" class="form-control" value="${empty employeeDto.phone ? '-' : employeeDto.phone}" disabled>
                        </div>
                        <div class="col-md-6">
                            <label class="form-label"><fmt:message key="employee.profile.label.role" /></label>
                            <input type="text" class="form-control" value="${empty roleName ? '-' : roleName}" disabled>
                        </div>
                        <div class="col-md-6">
                            <label class="form-label"><fmt:message key="employee.profile.label.headquarters" /></label>
                            <input type="text" class="form-control" value="${empty headquartersDto.name ? '-' : headquartersDto.name}" disabled>
                            <c:if test="${not empty requestScope.headquartersLocation}">
                                <div class="form-text">${requestScope.headquartersLocation}</div>
                            </c:if>
                        </div>
                        <div class="col-md-6">
                            <label class="form-label"><fmt:message key="employee.profile.label.headquartersContact" /></label>
                            <input type="text" class="form-control" value="${empty requestScope.headquartersPhone ? '-' : requestScope.headquartersPhone}" disabled>
                            <div class="form-text">${empty requestScope.headquartersEmail ? '-' : requestScope.headquartersEmail}</div>
                        </div>
                        <div class="col-md-6">
                            <label class="form-label"><fmt:message key="employee.profile.label.created" /></label>
                            <input type="text" class="form-control" value="${empty createdAtFormatted ? '-' : createdAtFormatted}" disabled>
                        </div>
                        <div class="col-md-6">
                            <label class="form-label"><fmt:message key="employee.profile.label.updated" /></label>
                            <input type="text" class="form-control" value="${empty updatedAtFormatted ? '-' : updatedAtFormatted}" disabled>
                        </div>
                    </div>
                    <p class="mt-4 mb-0 text-muted small"><fmt:message key="employee.profile.notice" /></p>
                </div>
            </div>
        </c:if>

        <a class="btn btn-outline-brand" href="${pageContext.request.contextPath}/app/rentals/private">
            <i class="bi bi-arrow-left"></i> <fmt:message key="employee.profile.back" />
        </a>
    </div>
</div>
<%@ include file="/common/footer.jsp" %>
