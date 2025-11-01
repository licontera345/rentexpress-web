<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<fmt:setLocale value="${not empty sessionScope.appLocale ? sessionScope.appLocale : pageContext.request.locale}" scope="session" />
<fmt:setBundle basename="i18n.Messages" />
<fmt:message key="register.user.heading" var="registerUserTitle" />
<c:set var="pageTitle" value="${registerUserTitle}" />
<%@ include file="/common/header.jsp" %>
<div class="row justify-content-center">
    <div class="col-lg-9 col-xl-8">
        <div class="card shadow-sm mb-4">
            <div class="card-body p-4">
                <h1 class="h3 fw-bold mb-3"><fmt:message key="register.user.heading" /></h1>
                <p class="text-muted"><fmt:message key="register.user.description" /></p>
                <c:if test="${not empty error}">
                    <div class="alert alert-danger" role="alert">
                        <c:forEach var="entry" items="${error.entrySet()}">
                            <div>${entry.value}</div>
                        </c:forEach>
                    </div>
                </c:if>
                <form method="post" class="row g-3 needs-validation" novalidate>
                    <div class="col-md-6">
                        <label for="firstName" class="form-label"><fmt:message key="register.user.firstName" /></label>
                        <input type="text" id="firstName" name="firstName" class="form-control" value="${param.firstName}" required>
                        <div class="form-text"><fmt:message key="register.user.firstName.help" /></div>
                    </div>
                    <div class="col-md-6">
                        <label for="birthDate" class="form-label"><fmt:message key="register.user.birthDate" /></label>
                        <input type="date" id="birthDate" name="birthDate" class="form-control" value="${param.birthDate}" required>
                        <div class="form-text"><fmt:message key="register.user.birthDate.help" /></div>
                    </div>
                    <div class="col-md-6">
                        <label for="lastName1" class="form-label"><fmt:message key="register.user.lastName1" /></label>
                        <input type="text" id="lastName1" name="lastName1" class="form-control" value="${param.lastName1}" required>
                        <div class="form-text"><fmt:message key="register.user.lastName1.help" /></div>
                    </div>
                    <div class="col-md-6">
                        <label for="lastName2" class="form-label"><fmt:message key="register.user.lastName2" /></label>
                        <input type="text" id="lastName2" name="lastName2" class="form-control" value="${param.lastName2}">
                        <div class="form-text"><fmt:message key="register.user.lastName2.help" /></div>
                    </div>
                    <div class="col-md-6">
                        <label for="email" class="form-label"><fmt:message key="register.user.email" /></label>
                        <input type="email" id="email" name="email" class="form-control" value="${param.email}" required>
                        <div class="form-text"><fmt:message key="register.user.email.help" /></div>
                    </div>
                    <div class="col-md-6">
                        <label for="phone" class="form-label"><fmt:message key="register.user.phone" /></label>
                        <input type="tel" id="phone" name="phone" class="form-control" value="${param.phone}" placeholder="<fmt:message key='register.user.phone.placeholder' />">
                        <div class="form-text"><fmt:message key="register.user.phone.help" /></div>
                    </div>
                    <div class="col-md-6">
                        <label for="password" class="form-label"><fmt:message key="register.user.password" /></label>
                        <input type="password" id="password" name="password" class="form-control" required>
                        <div class="form-text"><fmt:message key="register.user.password.help" /></div>
                    </div>
                    <div class="col-md-6">
                        <label for="confirmPassword" class="form-label"><fmt:message key="register.user.confirmPassword" /></label>
                        <input type="password" id="confirmPassword" name="confirmPassword" class="form-control" required>
                        <div class="form-text"><fmt:message key="register.user.confirmPassword.help" /></div>
                    </div>
                    <div class="col-12">
                        <h2 class="h5 mb-3"><fmt:message key="register.user.address.title" /></h2>
                    </div>
                    <div class="col-md-8">
                        <label for="street" class="form-label"><fmt:message key="register.user.street" /></label>
                        <input type="text" id="street" name="street" class="form-control" value="${param.street}" required>
                        <div class="form-text"><fmt:message key="register.user.street.help" /></div>
                    </div>
                    <div class="col-md-4">
                        <label for="number" class="form-label"><fmt:message key="register.user.number" /></label>
                        <input type="text" id="number" name="number" class="form-control" value="${param.number}" required>
                        <div class="form-text"><fmt:message key="register.user.number.help" /></div>
                    </div>
                    <div class="col-md-6">
                        <label for="province" class="form-label"><fmt:message key="register.user.province" /></label>
                        <select id="province" name="province" class="form-select" required>
                            <option value="" <c:if test="${empty param.province}">selected</c:if>><fmt:message key="register.user.province.placeholder" /></option>
                            <option value="A Coruña" <c:if test="${param.province == 'A Coruña'}">selected</c:if>>A Coruña</option>
                            <option value="Lugo" <c:if test="${param.province == 'Lugo'}">selected</c:if>>Lugo</option>
                            <option value="Ourense" <c:if test="${param.province == 'Ourense'}">selected</c:if>>Ourense</option>
                            <option value="Pontevedra" <c:if test="${param.province == 'Pontevedra'}">selected</c:if>>Pontevedra</option>
                        </select>
                    </div>
                    <div class="col-md-6">
                        <label for="city" class="form-label"><fmt:message key="register.user.city" /></label>
                        <input type="text" id="city" name="city" class="form-control" value="${param.city}" placeholder="<fmt:message key='register.user.city.placeholder' />" required>
                        <div class="form-text"><fmt:message key="register.user.city.help" /></div>
                    </div>
                    <div class="col-12">
                        <div class="form-check">
                            <input class="form-check-input" type="checkbox" value="accepted" id="acceptTerms" name="acceptTerms" <c:if test="${not empty param.acceptTerms}">checked</c:if> required>
                            <label class="form-check-label" for="acceptTerms">
                                <fmt:message key="register.user.acceptTerms">
                                    <fmt:param>
                                        <a href="${pageContext.request.contextPath}/public/terms.jsp" class="link-primary" target="_blank">
                                            <fmt:message key="register.user.terms" />
                                        </a>
                                    </fmt:param>
                                </fmt:message>
                            </label>
                        </div>
                    </div>
                    <div class="col-12 d-flex flex-column flex-md-row justify-content-between align-items-md-center mt-3">
                        <button type="submit" class="btn btn-brand mb-3 mb-md-0"><fmt:message key="register.user.submit" /></button>
                        <a href="${pageContext.request.contextPath}/login" class="link-secondary">
                            <fmt:message key="register.user.already" />
                        </a>
                    </div>
                </form>
            </div>
        </div>
        <div class="card border-0 bg-light">
            <div class="card-body">
                <h2 class="h5"><fmt:message key="register.user.benefits.title" /></h2>
                <ul class="mb-0">
                    <li><fmt:message key="register.user.benefits.1" /></li>
                    <li><fmt:message key="register.user.benefits.2" /></li>
                    <li><fmt:message key="register.user.benefits.3" /></li>
                </ul>
            </div>
        </div>
    </div>
</div>
<%@ include file="/common/footer.jsp" %>
