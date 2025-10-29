<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<fmt:message key="page.terms.title" var="termsTitle" />
<c:set var="pageTitle" value="${termsTitle}" />
<%@ include file="/common/header.jsp" %>
<div class="row justify-content-center">
    <div class="col-lg-10 col-xl-8">
        <div class="card card-common mb-4">
            <div class="card-header"><fmt:message key="terms.title" /></div>
            <div class="card-body p-4">
                <p class="text-muted"><fmt:message key="terms.intro" /></p>
                <h2 class="h5 fw-semibold mt-4"><fmt:message key="terms.section.usage.title" /></h2>
                <ul class="text-muted">
                    <li><fmt:message key="terms.section.usage.item1" /></li>
                    <li><fmt:message key="terms.section.usage.item2" /></li>
                    <li><fmt:message key="terms.section.usage.item3" /></li>
                </ul>
                <h2 class="h5 fw-semibold mt-4"><fmt:message key="terms.section.data.title" /></h2>
                <p class="text-muted"><fmt:message key="terms.section.data.text" /></p>
                <h2 class="h5 fw-semibold mt-4"><fmt:message key="terms.section.cancellations.title" /></h2>
                <p class="text-muted"><fmt:message key="terms.section.cancellations.text" /></p>
                <h2 class="h5 fw-semibold mt-4"><fmt:message key="terms.section.contact.title" /></h2>
                <p class="text-muted mb-0">
                    <fmt:message key="terms.section.contact.text">
                        <fmt:param>
                            <a href="mailto:legal@rentexpress.com" class="text-decoration-none">legal@rentexpress.com</a>
                        </fmt:param>
                    </fmt:message>
                </p>
            </div>
        </div>
        <a class="btn btn-outline-secondary" href="${ctx}/app/users/register">
            <i class="bi bi-arrow-left me-2"></i><fmt:message key="terms.backToRegister" />
        </a>
    </div>
</div>
<%@ include file="/common/footer.jsp" %>
