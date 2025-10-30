<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<fmt:message key="employee.profile.heading" var="employeeProfileTitle" />
<c:set var="pageTitle" value="${employeeProfileTitle}" />
<%@ include file="/common/header.jsp" %>
<c:set var="profile" value="${employeeProfile}" />
<div class="row justify-content-center">
    <div class="col-lg-8">
        <c:if test="${not empty flashSuccess}">
            <div class="alert alert-success shadow-soft mb-4">${flashSuccess}</div>
        </c:if>
        <c:if test="${not empty flashError}">
            <div class="alert alert-danger shadow-soft mb-4">${flashError}</div>
        </c:if>
        <c:if test="${not empty flashInfo}">
            <div class="alert alert-info shadow-soft mb-4">${flashInfo}</div>
        </c:if>

        <c:if test="${empty profile}">
            <div class="alert alert-warning shadow-soft mb-4">
                <fmt:message key="employee.profile.loadError" />
            </div>
        </c:if>

        <c:if test="${not empty profile}">
            <div class="card shadow-sm mb-4">
                <div class="card-body p-4">
                    <div class="d-flex justify-content-between align-items-start flex-wrap gap-3 mb-3">
                        <div>
                            <h1 class="h3 fw-bold mb-1">${employeeProfileTitle}</h1>
                            <p class="text-muted mb-0"><fmt:message key="employee.profile.subtitle" /></p>
                        </div>
                        <div class="text-end">
                            <span class="badge ${profile.statusStyle} px-3 py-2">${profile.statusLabel}</span>
                            <c:if test="${not empty profile.employeeId}">
                                <p class="text-muted small mb-0 mt-2">
                                    <fmt:message key="employee.profile.internalId">
                                        <fmt:param value="${profile.employeeId}" />
                                    </fmt:message>
                                </p>
                            </c:if>
                        </div>
                    </div>
                    <div class="row g-3">
                        <div class="col-md-6">
                            <label class="form-label"><fmt:message key="employee.profile.label.fullName" /></label>
                            <input type="text" class="form-control" value="${profile.fullName}" disabled>
                        </div>
                        <div class="col-md-6">
                            <label class="form-label"><fmt:message key="employee.profile.label.accountName" /></label>
                            <input type="text" class="form-control" value="${profile.accountName}" disabled>
                        </div>
                        <div class="col-md-6">
                            <label class="form-label"><fmt:message key="employee.profile.label.email" /></label>
                            <input type="email" class="form-control" value="${profile.email}" disabled>
                        </div>
                        <div class="col-md-6">
                            <label class="form-label"><fmt:message key="employee.profile.label.phone" /></label>
                            <input type="text" class="form-control" value="${profile.phone}" disabled>
                        </div>
                        <div class="col-md-6">
                            <label class="form-label"><fmt:message key="employee.profile.label.role" /></label>
                            <input type="text" class="form-control" value="${profile.role}" disabled>
                        </div>
                        <div class="col-md-6">
                            <label class="form-label"><fmt:message key="employee.profile.label.headquarters" /></label>
                            <input type="text" class="form-control" value="${profile.headquarters}" disabled>
                            <c:if test="${not empty profile.headquartersLocation}">
                                <div class="form-text">${profile.headquartersLocation}</div>
                            </c:if>
                        </div>
                        <div class="col-md-6">
                            <label class="form-label"><fmt:message key="employee.profile.label.headquartersContact" /></label>
                            <input type="text" class="form-control" value="${profile.headquartersPhone}" disabled>
                            <div class="form-text">${profile.headquartersEmail}</div>
                        </div>
                        <div class="col-md-6">
                            <label class="form-label"><fmt:message key="employee.profile.label.created" /></label>
                            <input type="text" class="form-control" value="${profile.createdAt}" disabled>
                        </div>
                        <div class="col-md-6">
                            <label class="form-label"><fmt:message key="employee.profile.label.updated" /></label>
                            <input type="text" class="form-control" value="${profile.updatedAt}" disabled>
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
