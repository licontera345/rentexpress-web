<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<fmt:setLocale value="${not empty sessionScope.appLocale ? sessionScope.appLocale : pageContext.request.locale}" scope="session" />
<fmt:setBundle basename="i18n.Messages" />
<fmt:message key="user.profile.heading" var="userProfileTitle" />
<fmt:message key="user.profile.placeholder.newPassword" var="userProfilePasswordPlaceholder" />
<fmt:message key="user.profile.preview.alt" var="userProfilePreviewAlt" />
<c:set var="pageTitle" value="${userProfileTitle}" />
<%@ include file="/common/header.jsp" %>
<c:set var="account" value="${account}" />
<c:set var="errors" value="${error}" />
<div class="row justify-content-center">
    <div class="col-lg-8">
        <div class="card shadow-sm mb-4">
            <div class="card-body p-4">
                <h1 class="h3 fw-bold mb-3">${userProfileTitle}</h1>
                <p class="text-muted"><fmt:message key="user.profile.intro" /></p>

                <c:if test="${not empty flashSuccess}">
                    <div class="alert alert-success">${flashSuccess}</div>
                </c:if>
                <c:if test="${not empty flashError}">
                    <div class="alert alert-danger">${flashError}</div>
                </c:if>
                <c:if test="${not empty errors}">
                    <div class="alert alert-warning">
                        <c:forEach var="entry" items="${errors.entrySet()}">
                            <div>${entry.value}</div>
                        </c:forEach>
                    </div>
                </c:if>

                <form method="post" enctype="multipart/form-data" class="row g-3">
                    <div class="col-12">
                        <label class="form-label" for="fullName"><fmt:message key="user.profile.label.fullName" /></label>
                        <input class="form-control" type="text" id="fullName" name="fullName" value="${account.fullName}" required />
                    </div>
                    <div class="col-md-6">
                        <label class="form-label" for="phone"><fmt:message key="user.profile.label.phone" /></label>
                        <input class="form-control" type="text" id="phone" name="phone" value="${account.phone}" />
                    </div>
                    <div class="col-md-6">
                        <label class="form-label" for="email"><fmt:message key="user.profile.label.email" /></label>
                        <input class="form-control" type="email" id="email" name="email" value="${account.email}" readonly />
                    </div>
                    <div class="col-md-6">
                        <label class="form-label" for="password"><fmt:message key="user.profile.label.newPassword" /></label>
                        <input class="form-control" type="password" id="password" name="password" placeholder="${userProfilePasswordPlaceholder}" />
                    </div>
                    <div class="col-md-6">
                        <label class="form-label" for="avatar"><fmt:message key="user.profile.label.avatar" /></label>
                        <input class="form-control" type="file" id="avatar" name="image" accept="image/*" />
                        <div class="form-text"><fmt:message key="user.profile.help.avatar" /></div>
                    </div>
                    <div class="col-12 d-flex justify-content-end gap-2">
                        <button type="submit" class="btn btn-brand">
                            <i class="bi bi-save"></i> <fmt:message key="user.profile.submit" />
                        </button>
                        <a class="btn btn-outline-secondary" href="${ctx}/app/home">
                            <i class="bi bi-arrow-left"></i> <fmt:message key="user.profile.back" />
                        </a>
                    </div>
                </form>
            </div>
        </div>

        <div class="card shadow-sm">
            <div class="card-body d-flex align-items-center gap-3">
                <div>
                    <h2 class="h5 mb-1"><fmt:message key="user.profile.preview.title" /></h2>
                    <p class="text-muted mb-0"><fmt:message key="user.profile.preview.subtitle" /></p>
                </div>
                <c:set var="avatarPath" value="${account.avatarPath}" />
                <c:choose>
                    <c:when test="${not empty avatarPath}">
                        <img src="${ctx}/app/images/view?entity=user&amp;entityId=${account.id}" alt="${userProfilePreviewAlt}" class="rounded-circle shadow" width="96" height="96" />
                    </c:when>
                    <c:otherwise>
                        <div class="rounded-circle bg-secondary-subtle text-secondary d-flex align-items-center justify-content-center" style="width:96px;height:96px;">
                            <i class="bi bi-person" style="font-size:2.5rem;"></i>
                        </div>
                    </c:otherwise>
                </c:choose>
            </div>
        </div>
    </div>
</div>
<%@ include file="/common/footer.jsp" %>
