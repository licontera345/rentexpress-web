<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<fmt:message key="register.employee.title" var="registerEmployeeTitle" />
<c:set var="pageTitle" value="${registerEmployeeTitle}" />
<%@ include file="/common/header.jsp" %>
<div class="row justify-content-center">
    <div class="col-lg-8">
        <div class="card shadow-sm mb-4">
            <div class="card-body p-4">
                <h1 class="h3 fw-bold mb-3"><fmt:message key="register.employee.title" /></h1>
                <p class="text-muted"><fmt:message key="register.employee.intro" /></p>
                <c:if test="${not empty messages}">
                    <div class="alert alert-info" role="alert">
                        <c:forEach var="message" items="${messages}">
                            <div>${message}</div>
                        </c:forEach>
                    </div>
                </c:if>
                <form method="post" class="row g-3" novalidate>
                    <div class="col-md-6">
                        <label for="fullName" class="form-label"><fmt:message key="register.employee.label.fullName" /></label>
                        <input type="text" id="fullName" name="fullName" class="form-control" placeholder="<fmt:message key='register.employee.placeholder.fullName' />" disabled>
                    </div>
                    <div class="col-md-6">
                        <label for="email" class="form-label"><fmt:message key="register.employee.label.email" /></label>
                        <input type="email" id="email" name="email" class="form-control" placeholder="<fmt:message key='register.employee.placeholder.email' />" disabled>
                    </div>
                    <div class="col-md-6">
                        <label for="role" class="form-label"><fmt:message key="register.employee.label.role" /></label>
                        <select id="role" name="role" class="form-select" disabled>
                            <option><fmt:message key="register.employee.option.role.reservations" /></option>
                            <option><fmt:message key="register.employee.option.role.fleet" /></option>
                            <option><fmt:message key="register.employee.option.role.admin" /></option>
                        </select>
                    </div>
                    <div class="col-md-6">
                        <label for="headquarters" class="form-label"><fmt:message key="register.employee.label.headquarters" /></label>
                        <input type="text" id="headquarters" name="headquarters" class="form-control" placeholder="<fmt:message key='register.employee.placeholder.headquarters' />" disabled>
                    </div>
                    <div class="col-12">
                        <label for="notes" class="form-label"><fmt:message key="register.employee.label.notes" /></label>
                        <textarea id="notes" name="notes" class="form-control" rows="4" placeholder="<fmt:message key='register.employee.placeholder.notes' />" disabled></textarea>
                    </div>
                    <div class="col-12 d-flex justify-content-between align-items-center mt-3">
                        <span class="text-muted small"><fmt:message key="register.employee.notice" /></span>
                        <button type="submit" class="btn btn-brand" disabled><fmt:message key="register.employee.submit" /></button>
                    </div>
                </form>
            </div>
        </div>
        <div class="alert alert-warning" role="alert">
            <fmt:message key="register.employee.alert">
                <fmt:param>
                    <a href="mailto:rrhh@rentexpress.com" class="alert-link">rrhh@rentexpress.com</a>
                </fmt:param>
            </fmt:message>
        </div>
    </div>
</div>
<%@ include file="/common/footer.jsp" %>
