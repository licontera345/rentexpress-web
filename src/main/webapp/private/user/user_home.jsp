<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<fmt:message key="user.dashboard.pageTitle" var="dashboardTitle" />
<c:set var="pageTitle" value="${dashboardTitle}" />
<%@ include file="/common/header.jsp" %>
<c:set var="profile" value="${profile}" />
<c:set var="activities" value="${activityEntries}" />
<c:set var="greetingName" value="${greetingName}" />
<c:if test="${not empty flashSuccess}">
    <div class="alert alert-success shadow-soft">${flashSuccess}</div>
</c:if>
<c:if test="${not empty flashError}">
    <div class="alert alert-danger shadow-soft">${flashError}</div>
</c:if>
<c:if test="${not empty flashInfo}">
    <div class="alert alert-info shadow-soft">${flashInfo}</div>
</c:if>
<div class="row g-4 align-items-start">
    <div class="col-xl-8">
        <div class="card shadow-soft mb-4">
            <div class="card-body p-4">
                <span class="badge rounded-pill bg-brand-subtle text-brand fw-semibold text-uppercase">
                    <fmt:message key="user.dashboard.badge" />
                </span>
                <h1 class="display-6 fw-bold mt-3 mb-2">
                    <fmt:message key="user.dashboard.greeting">
                        <fmt:param value="${greetingName}" />
                    </fmt:message>
                </h1>
                <p class="text-muted mb-4">
                    <fmt:message key="user.dashboard.subtitle" />
                </p>
                <div class="d-flex flex-wrap gap-2">
                    <a class="btn btn-brand" href="${ctx}/app/users/private">
                        <i class="bi bi-person-lines-fill me-2"></i>
                        <fmt:message key="user.dashboard.actions.profile" />
                    </a>
                    <a class="btn btn-outline-brand" href="${ctx}/app/reservations/private">
                        <i class="bi bi-calendar-check me-2"></i>
                        <fmt:message key="user.dashboard.actions.reservations" />
                    </a>
                    <form method="post" action="${ctx}/logout" class="d-inline">
                        <button type="submit" class="btn btn-outline-secondary">
                            <i class="bi bi-box-arrow-right me-2"></i>
                            <fmt:message key="user.dashboard.actions.logout" />
                        </button>
                    </form>
                </div>
            </div>
        </div>
        <div class="card shadow-soft">
            <div class="card-body p-4">
                <h2 class="h5 fw-semibold mb-3">
                    <fmt:message key="user.dashboard.profile.title" />
                </h2>
                <div class="row g-3">
                    <div class="col-md-6">
                        <div class="profile-summary-item">
                            <span class="text-muted text-uppercase small fw-semibold">
                                <fmt:message key="user.dashboard.profile.fullName" />
                            </span>
                            <p class="mb-0 h6">${profile.fullName}</p>
                        </div>
                    </div>
                    <div class="col-md-6">
                        <div class="profile-summary-item">
                            <span class="text-muted text-uppercase small fw-semibold">
                                <fmt:message key="user.dashboard.profile.email" />
                            </span>
                            <p class="mb-0 h6">${profile.email}</p>
                        </div>
                    </div>
                    <div class="col-md-6">
                        <div class="profile-summary-item">
                            <span class="text-muted text-uppercase small fw-semibold">
                                <fmt:message key="user.dashboard.profile.phone" />
                            </span>
                            <p class="mb-0 h6">${profile.phone}</p>
                        </div>
                    </div>
                    <div class="col-md-6">
                        <div class="profile-summary-item">
                            <span class="text-muted text-uppercase small fw-semibold">
                                <fmt:message key="user.dashboard.profile.role" />
                            </span>
                            <p class="mb-0 h6">
                                <fmt:message key="${profileRoleKey}" />
                            </p>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
    <div class="col-xl-4">
        <div class="card shadow-soft mb-4">
            <div class="card-body text-center p-4">
                <h2 class="h6 text-uppercase text-muted mb-3">
                    <fmt:message key="user.dashboard.avatar.title" />
                </h2>
                <p class="text-muted small">
                    <fmt:message key="user.dashboard.avatar.subtitle" />
                </p>
                <c:set var="avatarPath" value="${profile.avatarPath}" />
                <c:choose>
                    <c:when test="${not empty avatarPath}">
                        <img src="${ctx}/app/images/view?entity=user&amp;entityId=${profile.id}" alt="Avatar"
                             class="rounded-circle shadow" width="120" height="120" />
                    </c:when>
                    <c:otherwise>
                        <div class="rounded-circle bg-secondary-subtle text-secondary d-flex align-items-center justify-content-center"
                             style="width:120px;height:120px;">
                            <i class="bi bi-person" style="font-size:3rem;"></i>
                        </div>
                    </c:otherwise>
                </c:choose>
            </div>
        </div>
        <div class="card shadow-soft">
            <div class="card-body p-4">
                <h2 class="h6 text-uppercase text-muted mb-3">
                    <fmt:message key="user.dashboard.support.title" />
                </h2>
                <p class="mb-2">
                    <i class="bi bi-shield-lock text-brand me-2"></i>
                    <fmt:message key="user.dashboard.support.security" />
                </p>
                <p class="mb-0">
                    <i class="bi bi-chat-dots text-brand me-2"></i>
                    <fmt:message key="user.dashboard.support.contact" />
                </p>
            </div>
        </div>
    </div>
</div>
<%@ include file="/common/footer.jsp" %>
