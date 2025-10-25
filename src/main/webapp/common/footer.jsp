<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>

<footer class="footer py-5 mt-5">
    <div class="container">
        <div class="row gy-4 align-items-center">
            <div class="col-lg-4 text-lg-start text-center">
                <h5 class="text-white mb-3">RentExpress</h5>
                <p class="mb-0 small">&copy; <%= java.time.Year.now() %> RentExpress. <fmt:message key="welcomeMessage" /></p>
            </div>
            <div class="col-lg-4 d-flex justify-content-center">
                <div class="d-flex flex-column gap-2 text-center text-lg-start">
                    <a href="${pageContext.request.contextPath}/public/company-policy.jsp#terms" class="small text-decoration-none"><fmt:message key="footer.terms" /></a>
                    <a href="${pageContext.request.contextPath}/public/company-policy.jsp#privacy" class="small text-decoration-none"><fmt:message key="footer.privacy" /></a>
                    <a href="${pageContext.request.contextPath}/public/company-policy.jsp#cookies" class="small text-decoration-none"><fmt:message key="footer.cookies" /></a>
                </div>
            </div>
            <div class="col-lg-4 text-lg-end text-center">
                <a href="#" class="btn btn-outline-light px-4 py-2 shadow-sm">
                    <i class="bi bi-life-preserver me-2"></i>
                    <fmt:message key="footer.support" />
                </a>
            </div>
        </div>
    </div>
</footer>
