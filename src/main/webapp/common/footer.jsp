<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
</main>
<jsp:useBean id="now" class="java.util.Date" />
<fmt:formatDate value="${now}" pattern="yyyy" var="currentYear" />
<fmt:message key="nav.brand" var="brandName" />
<footer class="mt-auto py-4">
    <div class="container">
        <div class="bg-white rounded-4 shadow-soft p-4 d-flex flex-column flex-md-row align-items-center justify-content-between gap-3">
            <div>
                <p class="mb-1 fw-semibold"><fmt:message key="footer.message" /></p>
                <small class="text-muted"><fmt:message key="footer.tagline" /></small>
            </div>
            <div class="d-flex flex-wrap align-items-center gap-3">
                <span class="text-muted small mb-0">
                    <fmt:message key="footer.copyright">
                        <fmt:param value="${currentYear}" />
                        <fmt:param value="${brandName}" />
                    </fmt:message>
                </span>
                <div class="d-flex flex-wrap gap-2">
                    <a class="text-decoration-none text-muted" href="#"><fmt:message key="footer.cookies" /></a>
                    <span class="text-muted">·</span>
                    <a class="text-decoration-none text-muted" href="#"><fmt:message key="footer.privacy" /></a>
                    <span class="text-muted">·</span>
                    <a class="text-decoration-none text-muted" href="mailto:contacto@rentexpress.com"><fmt:message key="footer.contact" /></a>
                </div>
            </div>
        </div>
    </div>
</footer>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"
        integrity="sha384-YvpcrYf0tY3lHB60NNkmXc5s9fDVZLESaAA55NDzOxhy9GkcIdslK1eN7N6jIeHz"
        crossorigin="anonymous"></script>
</body>
</html>
