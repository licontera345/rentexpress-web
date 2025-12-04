<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>

<c:set var="renderLayoutShell" value="${renderLayoutShell == false ? false : true}" />

<c:if test="${empty ctx}">
    <c:set var="ctx" value="${pageContext.request.contextPath}" />
</c:if>

</main>

<footer class="footer mt-auto">
    <div class="footer-glow"></div>
    <div class="container position-relative">
        <div class="footer-inner flex-column align-items-stretch">
            <div class="row g-4 align-items-start">
                <div class="col-12 col-lg-5">
                    <div class="footer-brand">
                        <span class="brand-symbol flex-shrink-0">RE</span>
                        <div>
                            <p class="footer-title text-uppercase mb-1"><fmt:message key="layout.appName" /></p>
                            <p class="footer-tagline"><fmt:message key="common.home.hero.subtitle" /></p>
                        </div>
                    </div>
                    <p class="text-white-50 small mt-3 mb-0">
                        <fmt:message key="layout.footer.description" />
                    </p>
                </div>

                <div class="col-6 col-md-4 col-lg-3">
                    <h6 class="text-uppercase fw-semibold text-white-50 mb-3">
                        <fmt:message key="layout.footer.navigation" />
                    </h6>
                    <nav class="footer-links flex-column">
                        <a href="${ctx}/public/index" class="footer-link">
                            <fmt:message key="layout.home" />
                        </a>
                        <a href="${ctx}/public/VehicleServlet" class="footer-link">
                            <fmt:message key="vehicle.catalog.title" />
                        </a>
                        <a href="${ctx}/public/login" class="footer-link">
                            <fmt:message key="login.title" />
                        </a>
                    </nav>
                </div>

                <div class="col-6 col-md-4 col-lg-3 ms-lg-auto">
                    <h6 class="text-uppercase fw-semibold text-white-50 mb-3">
                        <fmt:message key="layout.footer.contact" />
                    </h6>
                    <ul class="list-unstyled text-white-50 small d-flex flex-column gap-2 mb-3">
                        <li class="d-flex align-items-start gap-2">
                            <i class="bi bi-geo-alt-fill"></i>
                            <span><fmt:message key="layout.footer.address" /></span>
                        </li>
                        <li class="d-flex align-items-start gap-2">
                            <i class="bi bi-telephone-fill"></i>
                            <span><fmt:message key="layout.footer.phone" /></span>
                        </li>
                        <li class="d-flex align-items-start gap-2">
                            <i class="bi bi-envelope-fill"></i>
                            <span><fmt:message key="layout.footer.email" /></span>
                        </li>
                    </ul>
                    <p class="text-white-50 small mb-0">
                        <fmt:message key="layout.footer.support" />
                    </p>
                </div>
            </div>

            <hr class="border-secondary opacity-25 my-4" />

            <div class="d-flex flex-column flex-lg-row justify-content-between align-items-start align-items-lg-center gap-3">
                <div class="footer-meta">
                    <span>&copy; <fmt:message key="layout.footer.copyright" /></span>
                </div>
                <div class="footer-meta flex-wrap">
                    <span><fmt:message key="layout.footer.legal" /></span>
                    <span>·</span>
                    <span><fmt:message key="layout.footer.privacy" /></span>
                </div>
            </div>
        </div>
    </div>
</footer>

<c:if test="${requestScope.emitDocument}">
    <!-- JS al final -->
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"
            integrity="sha384-YvpcrYf0tY3lHB60NNkmXc5s9fDVZLESaAA55NDzOxhy9GkcIdslK1eN7N6jIeHz"
            crossorigin="anonymous"></script>
</c:if>

<c:if test="${requestScope.emitDocument}">
</body>
</html>
</c:if>
