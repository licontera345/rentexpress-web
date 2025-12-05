<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>


<%@ include file="/common/header.jsp" %>
<c:set var="profileType" value="${requestScope.profileType}" />
<c:set var="userProfile" value="${requestScope.userProfileData}" />
<c:set var="employeeProfile" value="${requestScope.employeeProfileData}" />
<c:set var="profileAddress" value="${requestScope.profileAddress}" />
<c:set var="birthDate" value="${requestScope.profileBirthDate}" />
<c:set var="createdAt" value="${requestScope.profileCreatedAt}" />
<c:set var="updatedAt" value="${requestScope.profileUpdatedAt}" />
<c:set var="profileTitleKey" value="${profileType eq 'employee' ? 'profile.title.employee' : 'profile.title.user'}" />
<c:set var="profileSubtitleKey"
    value="${profileType eq 'employee' ? 'profile.subtitle.employee' : 'profile.subtitle.user'}" />
<section class="py-6 profile-section">
    <div class="container">
        <header class="page-header">
            <span class="section-eyebrow"><fmt:message key="profile.eyebrow" /></span>
            <h2 class="page-title"><fmt:message key="${profileTitleKey}" /></h2>
            <p class="page-subtitle"><fmt:message key="${profileSubtitleKey}" /></p>
        </header>
        <c:if test="${not empty requestScope.errorGeneral}">
            <div class="alert alert-danger" role="status">
                <c:out value="${requestScope.errorGeneral}" />
            </div>
        </c:if>
        <div class="row g-4">
            <div class="col-lg-6">
                <article class="card-common shadow-soft h-100">
                    <h3 class="h5 fw-semibold mb-3"><fmt:message key="profile.section.personal" /></h3>
                    <dl class="row profile-definition-list">
                        <dt class="col-sm-5 text-muted"><fmt:message key="profile.field.fullName" /></dt>
                        <dd class="col-sm-7">
                            <c:choose>
                                <c:when test="${profileType eq 'employee'}">
                                    <c:out value="${employeeProfile.firstName}" />
                                    <c:if test="${not empty employeeProfile.lastName1}">
                                        <c:out value=" ${employeeProfile.lastName1}" />
                                    </c:if>
                                    <c:if test="${not empty employeeProfile.lastName2}">
                                        <c:out value=" ${employeeProfile.lastName2}" />
                                    </c:if>
                                </c:when>
                                <c:otherwise>
                                    <c:out value="${userProfile.firstName}" />
                                    <c:if test="${not empty userProfile.lastName1}">
                                        <c:out value=" ${userProfile.lastName1}" />
                                    </c:if>
                                    <c:if test="${not empty userProfile.lastName2}">
                                        <c:out value=" ${userProfile.lastName2}" />
                                    </c:if>
                                </c:otherwise>
                            </c:choose>
                        </dd>
                        <dt class="col-sm-5 text-muted"><fmt:message key="profile.field.email" /></dt>
                        <dd class="col-sm-7">
                            <c:out value="${profileType eq 'employee' ? employeeProfile.email : userProfile.email}" />
                        </dd>
                        <dt class="col-sm-5 text-muted"><fmt:message key="profile.field.phone" /></dt>
                        <dd class="col-sm-7">
                            <c:choose>
                                <c:when test="${profileType eq 'employee'}">
                                    <c:out value="${employeeProfile.phone}" />
                                </c:when>
                                <c:otherwise>
                                    <c:out value="${userProfile.phone}" />
                                </c:otherwise>
                            </c:choose>
                        </dd>
                        <c:if test="${profileType ne 'employee' && not empty birthDate}">
                            <dt class="col-sm-5 text-muted"><fmt:message key="profile.field.birthDate" /></dt>
                            <dd class="col-sm-7">
                                <c:out value="${birthDate}" />
                            </dd>
                        </c:if>
                    </dl>
                </article>
            </div>
            <div class="col-lg-6">
                <article class="card-common shadow-soft h-100">
                    <h3 class="h5 fw-semibold mb-3"><fmt:message key="profile.section.account" /></h3>
                    <dl class="row profile-definition-list">
                        <dt class="col-sm-5 text-muted"><fmt:message key="profile.field.username" /></dt>
                        <dd class="col-sm-7">
                            <c:out value="${profileType eq 'employee' ? employeeProfile.employeeName : userProfile.username}" />
                        </dd>
                        <dt class="col-sm-5 text-muted"><fmt:message key="profile.field.role" /></dt>
                        <dd class="col-sm-7">
                            <c:choose>
                                <c:when test="${profileType eq 'employee'}">
                                    <c:choose>
                                        <c:when test="${not empty employeeProfile.role && not empty employeeProfile.role.roleName}">
                                            <c:out value="${employeeProfile.role.roleName}" />
                                        </c:when>
                                        <c:otherwise>
                                            <fmt:message key="profile.role.employee" />
                                        </c:otherwise>
                                    </c:choose>
                                </c:when>
                                <c:otherwise>
                                    <fmt:message key="profile.role.user" />
                                </c:otherwise>
                            </c:choose>
                        </dd>
                        <dt class="col-sm-5 text-muted"><fmt:message key="profile.field.status" /></dt>
                        <dd class="col-sm-7">
                            <c:choose>
                                <c:when test="${profileType eq 'employee'}">
                                    <fmt:message key="${employeeProfile.activeStatus ? 'profile.status.active' : 'profile.status.inactive'}" />
                                </c:when>
                                <c:otherwise>
                                    <fmt:message key="${userProfile.activeStatus ? 'profile.status.active' : 'profile.status.inactive'}" />
                                </c:otherwise>
                            </c:choose>
                        </dd>
                        <c:if test="${not empty createdAt}">
                            <dt class="col-sm-5 text-muted"><fmt:message key="profile.field.createdAt" /></dt>
                            <dd class="col-sm-7"><c:out value="${createdAt}" /></dd>
                        </c:if>
                        <c:if test="${not empty updatedAt}">
                            <dt class="col-sm-5 text-muted"><fmt:message key="profile.field.updatedAt" /></dt>
                            <dd class="col-sm-7"><c:out value="${updatedAt}" /></dd>
                        </c:if>
                    </dl>
                </article>
            </div>
            <c:if test="${profileType eq 'employee'}">
                <div class="col-lg-6">
                    <article class="card-common shadow-soft h-100">
                        <h3 class="h5 fw-semibold mb-3"><fmt:message key="profile.section.assignment" /></h3>
                        <dl class="row profile-definition-list">
                            <dt class="col-sm-5 text-muted"><fmt:message key="profile.field.headquarters" /></dt>
                            <dd class="col-sm-7">
                                <c:choose>
                                    <c:when test="${not empty employeeProfile.headquarters && not empty employeeProfile.headquarters.name}">
                                        <c:out value="${employeeProfile.headquarters.name}" />
                                    </c:when>
                                    <c:otherwise>
                                        <span class="text-muted"><fmt:message key="profile.field.headquarters.empty" /></span>
                                    </c:otherwise>
                                </c:choose>
                            </dd>
                            <c:if test="${not empty employeeProfile.headquarters && not empty employeeProfile.headquarters.city}">
                                <dt class="col-sm-5 text-muted"><fmt:message key="profile.field.city" /></dt>
                                <dd class="col-sm-7">
                                    <c:out value="${employeeProfile.headquarters.city.cityName}" />
                                </dd>
                            </c:if>
                            <c:if test="${not empty employeeProfile.headquarters && not empty employeeProfile.headquarters.province}">
                                <dt class="col-sm-5 text-muted"><fmt:message key="profile.field.province" /></dt>
                                <dd class="col-sm-7">
                                    <c:out value="${employeeProfile.headquarters.province.provinceName}" />
                                </dd>
                            </c:if>
                        </dl>
                    </article>
                </div>
            </c:if>
            <c:if test="${profileType ne 'employee'}">
                <div class="col-lg-6">
                    <article class="card-common shadow-soft h-100">
                        <h3 class="h5 fw-semibold mb-3"><fmt:message key="profile.section.location" /></h3>
                        <c:choose>
                            <c:when test="${not empty profileAddress}">
                                <dl class="row profile-definition-list">
                                    <dt class="col-sm-5 text-muted"><fmt:message key="profile.field.address" /></dt>
                                    <dd class="col-sm-7">
                                        <c:out value="${profileAddress.street}" />
                                        <c:if test="${not empty profileAddress.number}">
                                            <c:out value=", ${profileAddress.number}" />
                                        </c:if>
                                    </dd>
                                    <c:if test="${not empty profileAddress.cityName}">
                                        <dt class="col-sm-5 text-muted"><fmt:message key="profile.field.city" /></dt>
                                        <dd class="col-sm-7">
                                            <c:out value="${profileAddress.cityName}" />
                                        </dd>
                                    </c:if>
                                    <c:if test="${not empty profileAddress.provinceName}">
                                        <dt class="col-sm-5 text-muted"><fmt:message key="profile.field.province" /></dt>
                                        <dd class="col-sm-7">
                                            <c:out value="${profileAddress.provinceName}" />
                                        </dd>
                                    </c:if>
                                </dl>
                            </c:when>
                            <c:otherwise>
                                <p class="text-muted mb-0"><fmt:message key="profile.address.empty" /></p>
                            </c:otherwise>
                        </c:choose>
                    </article>
                </div>
            </c:if>
        </div>
    </div>
</section>
<%@ include file="/common/footer.jsp" %>
