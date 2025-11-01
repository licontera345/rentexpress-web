<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ include file="/common/header.jsp" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="user" value="${item}" />
<section class="mb-4">
    <div class="d-flex justify-content-between align-items-center mb-3">
        <h1 class="h3 mb-0"><fmt:message key="user.public.detail.title" /></h1>
        <a href="${ctx}/public/users" class="btn btn-outline-secondary"><i class="bi bi-arrow-left"></i> <fmt:message key="common.button.back" /></a>
    </div>
    <div class="row g-4">
        <div class="col-lg-8">
            <div class="card card-common">
                <div class="card-header"><fmt:message key="user.public.detail.mainInfo" /></div>
                <div class="card-body">
                    <dl class="row mb-0">
                        <dt class="col-sm-4"><fmt:message key="user.public.detail.fullName" /></dt>
                        <dd class="col-sm-8">
                            <c:choose>
                                <c:when test="${not empty user.firstName}">
                                    ${user.firstName} ${user.lastName1} ${user.lastName2}
                                </c:when>
                                <c:otherwise>
                                    ${user.username}
                                </c:otherwise>
                            </c:choose>
                        </dd>
                        <dt class="col-sm-4"><fmt:message key="common.table.header.email" /></dt>
                        <dd class="col-sm-8">${user.email}</dd>
                        <dt class="col-sm-4"><fmt:message key="common.field.phone" /></dt>
                        <dd class="col-sm-8">
                            <c:choose>
                                <c:when test="${empty user.phone}">
                                    <fmt:message key="user.public.detail.phone.missing" />
                                </c:when>
                                <c:otherwise>${user.phone}</c:otherwise>
                            </c:choose>
                        </dd>
                        <dt class="col-sm-4"><fmt:message key="user.public.detail.role" /></dt>
                        <dd class="col-sm-8">
                            <c:choose>
                                <c:when test="${empty roleName}">
                                    <fmt:message key="user.public.detail.role.missing" />
                                </c:when>
                                <c:otherwise>${roleName}</c:otherwise>
                            </c:choose>
                        </dd>
                        <dt class="col-sm-4"><fmt:message key="common.table.header.status" /></dt>
                        <dd class="col-sm-8">
                            <span class="badge ${user.activeStatus ? 'bg-success-subtle text-success' : 'bg-danger-subtle text-danger'}">
                                <fmt:message key="${user.activeStatus ? 'common.status.active' : 'common.status.inactive'}" />
                            </span>
                        </dd>
                        <dt class="col-sm-4"><fmt:message key="user.public.detail.createdAt" /></dt>
                        <dd class="col-sm-8">
                            <c:choose>
                                <c:when test="${not empty user.createdAt}">${user.createdAt}</c:when>
                                <c:otherwise>-</c:otherwise>
                            </c:choose>
                        </dd>
                        <dt class="col-sm-4"><fmt:message key="user.public.detail.updatedAt" /></dt>
                        <dd class="col-sm-8">
                            <c:choose>
                                <c:when test="${not empty user.updatedAt}">${user.updatedAt}</c:when>
                                <c:otherwise>-</c:otherwise>
                            </c:choose>
                        </dd>
                    </dl>
                </div>
            </div>
        </div>
        <div class="col-lg-4">
            <div class="card card-common">
                <div class="card-header"><fmt:message key="user.public.detail.recommended.title" /></div>
                <div class="card-body">
                    <p class="text-muted"><fmt:message key="user.public.detail.recommended.description" /></p>
                    <ul class="list-unstyled small mb-0">
                        <li class="mb-2"><i class="bi bi-check-circle text-success"></i> <fmt:message key="user.public.detail.recommended.item1" /></li>
                        <li class="mb-2"><i class="bi bi-check-circle text-success"></i> <fmt:message key="user.public.detail.recommended.item2" /></li>
                        <li><i class="bi bi-check-circle text-success"></i> <fmt:message key="user.public.detail.recommended.item3" /></li>
                    </ul>
                </div>
            </div>
            <jsp:include page="/public/user/user_form.jsp" />
        </div>
    </div>
</section>
<%@ include file="/common/footer.jsp" %>
