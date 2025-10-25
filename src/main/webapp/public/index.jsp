<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>

<fmt:setLocale value="${sessionScope.locale}" scope="session" />
<fmt:setBundle basename="i18n.Messages" />

<!DOCTYPE html>
<html lang="${sessionScope.locale.language != null ? sessionScope.locale.language : 'es'}">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>RentExpress</title>
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

    <main class="flex-grow-1">
        <section id="hero" class="py-5">
            <div class="container">
                <div class="hero-section shadow-soft">
                    <div class="row align-items-center g-4">
                        <div class="col-lg-6">
                            <span class="hero-badge"><i class="bi bi-geo-alt-fill"></i> RentExpress</span>
                            <h1 class="display-5 fw-bold mt-3"><fmt:message key="home.hero.title" /></h1>
                            <p class="lead text-secondary"><fmt:message key="home.hero.subtitle" /></p>
                            <div class="d-flex flex-wrap gap-3 mt-4">
                                <a href="#destacados" class="btn btn-brand btn-lg px-4">
                                    <i class="bi bi-car-front-fill me-2"></i>
                                    <fmt:message key="home.hero.ctaPrimary" />
                                </a>
                                <a href="#faq" class="btn btn-outline-brand btn-lg px-4">
                                    <i class="bi bi-question-circle me-2"></i>
                                    <fmt:message key="home.hero.ctaSecondary" />
                                </a>
                            </div>
                            <div class="d-flex align-items-center gap-3 mt-4">
                                <div class="d-flex align-items-center gap-2">
                                    <i class="bi bi-shield-check text-brand fs-4"></i>
                                    <span class="small text-muted">SSL &amp; PCI DSS</span>
                                </div>
                                <div class="d-flex align-items-center gap-2">
                                    <i class="bi bi-clock-history text-brand fs-4"></i>
                                    <span class="small text-muted">24/7</span>
                                </div>
                            </div>
                        </div>
                        <div class="col-lg-6">
                            <div class="position-relative">
                                <img src="https://images.unsplash.com/photo-1511919884226-fd3cad34687c?auto=format&fit=crop&w=900&q=80" alt="RentExpress hero" class="img-fluid rounded-4 shadow-soft">
                                <div class="position-absolute bottom-0 start-50 translate-middle-x bg-white rounded-4 shadow-soft p-4 w-100" style="max-width: 420px;">
                                    <div class="d-flex align-items-center gap-3">
                                        <div class="feature-icon m-0"><i class="bi bi-speedometer2"></i></div>
                                        <div>
                                            <p class="mb-1 fw-semibold text-uppercase small text-muted">360º Service</p>
                                            <p class="mb-0 fw-semibold">+5.000 reservas gestionadas</p>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </section>

        <section class="py-5 bg-white">
            <div class="container">
                <div class="row text-center g-4">
                    <div class="col-md-6 col-lg-3">
                        <div class="p-4 h-100 rounded-4 shadow-soft bg-white">
                            <div class="feature-icon mx-auto"><i class="bi bi-car-front"></i></div>
                            <h5 class="fw-semibold"><fmt:message key="home.feature.1.title" /></h5>
                            <p class="text-muted mb-0"><fmt:message key="home.feature.1.desc" /></p>
                        </div>
                    </div>
                    <div class="col-md-6 col-lg-3">
                        <div class="p-4 h-100 rounded-4 shadow-soft bg-white">
                            <div class="feature-icon mx-auto"><i class="bi bi-shield-lock"></i></div>
                            <h5 class="fw-semibold"><fmt:message key="home.feature.2.title" /></h5>
                            <p class="text-muted mb-0"><fmt:message key="home.feature.2.desc" /></p>
                        </div>
                    </div>
                    <div class="col-md-6 col-lg-3">
                        <div class="p-4 h-100 rounded-4 shadow-soft bg-white">
                            <div class="feature-icon mx-auto"><i class="bi bi-cash-coin"></i></div>
                            <h5 class="fw-semibold"><fmt:message key="home.feature.3.title" /></h5>
                            <p class="text-muted mb-0"><fmt:message key="home.feature.3.desc" /></p>
                        </div>
                    </div>
                    <div class="col-md-6 col-lg-3">
                        <div class="p-4 h-100 rounded-4 shadow-soft bg-white">
                            <div class="feature-icon mx-auto"><i class="bi bi-headset"></i></div>
                            <h5 class="fw-semibold"><fmt:message key="home.feature.4.title" /></h5>
                            <p class="text-muted mb-0"><fmt:message key="home.feature.4.desc" /></p>
                        </div>
                    </div>
                </div>
            </div>
        </section>

        <section id="destacados" class="py-5">
            <div class="container">
                <div class="row align-items-center mb-4">
                    <div class="col-md-8">
                        <h2 class="fw-bold mb-2"><fmt:message key="home.featured.title" /></h2>
                        <p class="text-muted mb-0"><fmt:message key="home.featured.subtitle" /></p>
                    </div>
                    <div class="col-md-4 text-md-end mt-3 mt-md-0">
                        <a href="${pageContext.request.contextPath}/public/UsuarioServlet?action=list" class="btn btn-outline-brand px-4">
                            <i class="bi bi-grid me-2"></i>
                            <fmt:message key="search.cta.fleet" />
                        </a>
                    </div>
                </div>

                <div id="carouselDestacados" class="carousel slide" data-bs-ride="carousel">
                    <div class="carousel-inner">
                        <div class="carousel-item active">
                            <div class="carousel-vehicle">
                                <img src="https://images.unsplash.com/photo-1549924231-f129b911e442?auto=format&fit=crop&w=1400&q=80" class="d-block w-100" alt="Audi A3">
                                <div class="carousel-caption text-start">
                                    <h4 class="fw-semibold">Audi A3 Sportback</h4>
                                    <p class="mb-2">Premium · Automático · 2023</p>
                                    <span class="badge bg-brand fs-6">39€ / día</span>
                                </div>
                            </div>
                        </div>
                        <div class="carousel-item">
                            <div class="carousel-vehicle">
                                <img src="https://images.unsplash.com/photo-1503736334956-4c8f8e92946d?auto=format&fit=crop&w=1400&q=80" class="d-block w-100" alt="Toyota RAV4">
                                <div class="carousel-caption text-start">
                                    <h4 class="fw-semibold">Toyota RAV4 Hybrid</h4>
                                    <p class="mb-2">SUV · Híbrido · 2022</p>
                                    <span class="badge bg-brand fs-6">45€ / día</span>
                                </div>
                            </div>
                        </div>
                        <div class="carousel-item">
                            <div class="carousel-vehicle">
                                <img src="https://images.unsplash.com/photo-1552519507-da3b142c6e3d?auto=format&fit=crop&w=1400&q=80" class="d-block w-100" alt="Mini Cooper">
                                <div class="carousel-caption text-start">
                                    <h4 class="fw-semibold">Mini Cooper Cabrio</h4>
                                    <p class="mb-2">Descapotable · Manual · 2021</p>
                                    <span class="badge bg-brand fs-6">55€ / día</span>
                                </div>
                            </div>
                        </div>
                        <div class="carousel-item">
                            <div class="carousel-vehicle">
                                <img src="https://images.unsplash.com/photo-1493238792000-8113da705763?auto=format&fit=crop&w=1400&q=80" class="d-block w-100" alt="Mercedes Vito">
                                <div class="carousel-caption text-start">
                                    <h4 class="fw-semibold">Mercedes Vito Tourer</h4>
                                    <p class="mb-2">Familiar · Automático · 2020</p>
                                    <span class="badge bg-brand fs-6">62€ / día</span>
                                </div>
                            </div>
                        </div>
                    </div>
                    <button class="carousel-control-prev" type="button" data-bs-target="#carouselDestacados" data-bs-slide="prev">
                        <span class="carousel-control-prev-icon" aria-hidden="true"></span>
                        <span class="visually-hidden">Previous</span>
                    </button>
                    <button class="carousel-control-next" type="button" data-bs-target="#carouselDestacados" data-bs-slide="next">
                        <span class="carousel-control-next-icon" aria-hidden="true"></span>
                        <span class="visually-hidden">Next</span>
                    </button>
                </div>
            </div>
        </section>

        <section id="faq" class="py-5 bg-white">
            <div class="container">
                <div class="row g-5 align-items-center">
                    <div class="col-lg-5">
                        <h2 class="fw-bold mb-3"><fmt:message key="home.faq.title" /></h2>
                        <p class="text-muted mb-4"><fmt:message key="home.subtitle" /></p>
                        <div class="d-flex align-items-center gap-3">
                            <div class="feature-icon m-0"><i class="bi bi-chat-dots"></i></div>
                            <div>
                                <p class="mb-1 fw-semibold">WhatsApp / Phone</p>
                                <p class="mb-0 text-muted">+34 600 123 456</p>
                            </div>
                        </div>
                    </div>
                    <div class="col-lg-7">
                        <div class="accordion" id="faqAccordion">
                            <div class="accordion-item">
                                <h2 class="accordion-header" id="faqHeadingOne">
                                    <button class="accordion-button" type="button" data-bs-toggle="collapse" data-bs-target="#faqCollapseOne" aria-expanded="true" aria-controls="faqCollapseOne">
                                        <fmt:message key="home.faq.q1" />
                                    </button>
                                </h2>
                                <div id="faqCollapseOne" class="accordion-collapse collapse show" aria-labelledby="faqHeadingOne" data-bs-parent="#faqAccordion">
                                    <div class="accordion-body">
                                        <fmt:message key="home.faq.a1" />
                                    </div>
                                </div>
                            </div>
                            <div class="accordion-item">
                                <h2 class="accordion-header" id="faqHeadingTwo">
                                    <button class="accordion-button collapsed" type="button" data-bs-toggle="collapse" data-bs-target="#faqCollapseTwo" aria-expanded="false" aria-controls="faqCollapseTwo">
                                        <fmt:message key="home.faq.q2" />
                                    </button>
                                </h2>
                                <div id="faqCollapseTwo" class="accordion-collapse collapse" aria-labelledby="faqHeadingTwo" data-bs-parent="#faqAccordion">
                                    <div class="accordion-body">
                                        <fmt:message key="home.faq.a2" />
                                    </div>
                                </div>
                            </div>
                            <div class="accordion-item">
                                <h2 class="accordion-header" id="faqHeadingThree">
                                    <button class="accordion-button collapsed" type="button" data-bs-toggle="collapse" data-bs-target="#faqCollapseThree" aria-expanded="false" aria-controls="faqCollapseThree">
                                        <fmt:message key="home.faq.q3" />
                                    </button>
                                </h2>
                                <div id="faqCollapseThree" class="accordion-collapse collapse" aria-labelledby="faqHeadingThree" data-bs-parent="#faqAccordion">
                                    <div class="accordion-body">
                                        <fmt:message key="home.faq.a3" />
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </section>
    </main>

    <%@ include file="/common/footer.jsp"%>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js" integrity="sha384-YvpcrYf0tY3lHB60NNkmXc5s9fDVZLESaAA55NDzOxhy9GkcIdslK1eN7N6jIeHz" crossorigin="anonymous"></script>
</body>
</html>
