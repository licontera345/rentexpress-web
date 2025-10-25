<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>

<fmt:setLocale value="${sessionScope.locale}" scope="session" />
<fmt:setBundle basename="i18n.Messages" />

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Company Policy | RentExpress</title>
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700&display=swap" rel="stylesheet">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet" integrity="sha384-QWTKZyjpPEjISv5WaRU9OFeRpok6YctnYmDr5pNlyT2bRjXh0JMhjY6hW+ALEwIH" crossorigin="anonymous">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.css" rel="stylesheet">
    <link href="https://cdn.jsdelivr.net/npm/flag-icons@6.6.6/css/flag-icons.min.css" rel="stylesheet">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/main.css">
</head>
<body class="bg-light d-flex flex-column min-vh-100">
    <%@ include file="/common/header.jsp"%>

    <main class="flex-grow-1 py-5">
        <div class="container">
            <div class="bg-white shadow-soft rounded-4 p-4 p-lg-5">
                <div class="d-flex flex-column flex-lg-row align-items-lg-center justify-content-between gap-3 mb-4">
                    <div>
                        <span class="badge bg-brand text-uppercase fw-semibold mb-2">RentExpress</span>
                        <h1 class="fw-bold mb-0">Company Policy</h1>
                        <p class="text-muted mb-0">Terms, privacy practices, and our commitment to responsible cookie usage.</p>
                    </div>
                    <div class="d-flex flex-wrap gap-2">
                        <a class="btn btn-outline-brand" href="#terms">
                            <i class="bi bi-file-earmark-text me-2"></i> Terms
                        </a>
                        <a class="btn btn-outline-brand" href="#privacy">
                            <i class="bi bi-shield-lock me-2"></i> Privacy
                        </a>
                        <a class="btn btn-outline-brand" href="#cookies">
                            <i class="bi bi-cookie me-2"></i> Cookies
                        </a>
                    </div>
                </div>

                <section id="terms" class="mb-5">
                    <h2 class="h4 fw-semibold mb-3">Terms and Conditions</h2>
                    <p>
                        These terms govern the use of the RentExpress platform. By creating an account or placing a reservation you agree to
                        comply with the obligations below and any applicable local regulations.
                    </p>
                    <ul class="list-unstyled ps-0">
                        <li class="d-flex gap-3 mb-2">
                            <i class="bi bi-check-circle-fill text-brand mt-1"></i>
                            <span>Provide accurate personal and payment information to enable secure vehicle reservations.</span>
                        </li>
                        <li class="d-flex gap-3 mb-2">
                            <i class="bi bi-check-circle-fill text-brand mt-1"></i>
                            <span>Respect pick-up and drop-off schedules defined in each reservation confirmation.</span>
                        </li>
                        <li class="d-flex gap-3">
                            <i class="bi bi-check-circle-fill text-brand mt-1"></i>
                            <span>Notify RentExpress immediately if you suspect unauthorized activity on your account.</span>
                        </li>
                    </ul>
                    <p class="mb-0 text-muted">Breach of these terms may result in reservation cancellations or suspension of account access.</p>
                </section>

                <section id="privacy" class="mb-5">
                    <h2 class="h4 fw-semibold mb-3">Privacy Notice</h2>
                    <p>
                        We collect the minimum information required to deliver our services and keep your data protected. Access to customer
                        information is restricted to authorized staff who need it to manage reservations, support requests, or fulfill legal
                        obligations.
                    </p>
                    <ul class="list-unstyled ps-0">
                        <li class="d-flex gap-3 mb-2">
                            <i class="bi bi-lock-fill text-brand mt-1"></i>
                            <span>Personal details are stored in encrypted systems hosted within the European Union.</span>
                        </li>
                        <li class="d-flex gap-3 mb-2">
                            <i class="bi bi-lock-fill text-brand mt-1"></i>
                            <span>We keep reservation data for as long as necessary to meet legal and accounting requirements.</span>
                        </li>
                        <li class="d-flex gap-3">
                            <i class="bi bi-lock-fill text-brand mt-1"></i>
                            <span>You can request, update, or delete your personal information by contacting privacy@rentexpress.com.</span>
                        </li>
                    </ul>
                    <p class="mb-0 text-muted">We do not sell your personal data and only share it with trusted partners essential to the rental process.</p>
                </section>

                <section id="cookies">
                    <h2 class="h4 fw-semibold mb-3">Cookie Policy</h2>
                    <p>
                        Cookies help us remember your language preferences and understand how visitors interact with our website. We use
                        strictly necessary cookies to keep the platform running and optional analytics cookies to improve the experience.
                    </p>
                    <div class="row g-3">
                        <div class="col-md-6">
                            <div class="border rounded-4 p-3 h-100">
                                <h3 class="h6 fw-semibold mb-2"><i class="bi bi-gear-wide-connected me-2"></i> Essential Cookies</h3>
                                <p class="mb-0">Required for authentication, session management, and secure payment operations.</p>
                            </div>
                        </div>
                        <div class="col-md-6">
                            <div class="border rounded-4 p-3 h-100">
                                <h3 class="h6 fw-semibold mb-2"><i class="bi bi-graph-up-arrow me-2"></i> Analytics Cookies</h3>
                                <p class="mb-0">Used to analyze site usage patterns so we can optimize navigation and reduce booking time.</p>
                            </div>
                        </div>
                    </div>
                    <p class="mt-3 mb-0 text-muted">You can manage optional cookies through your browser settings or by contacting support@rentexpress.com.</p>
                </section>
            </div>
        </div>
    </main>

    <%@ include file="/common/footer.jsp"%>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js" integrity="sha384-YvpcrYf0tY3lHB60NNkmXc5s9fDVZLESaAA55NDzOxhy9GkcIdslK1eN7N6jIeHz" crossorigin="anonymous"></script>
</body>
</html>
