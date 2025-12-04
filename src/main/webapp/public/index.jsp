<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>

<%-- ============================================
     CONFIGURACIÓN
     ============================================ --%>
<fmt:setLocale value="${sessionScope.appLocale != null ? sessionScope.appLocale : pageContext.request.locale}" scope="session" />
<fmt:setBundle basename="i18n.Messages" scope="session" />

<%-- Variables de contexto y datos --%>
<c:set var="ctx" value="${pageContext.request.contextPath}" />
<c:set var="featuredVehicles" value="${requestScope.featuredVehicles}" />
<c:set var="featuredVehicleImages" value="${requestScope.featuredVehicleImages}" />

<%-- Mensajes precargados --%>
<fmt:message var="homeHeroImageAlt" key="home.hero.image.alt" />
<fmt:message var="vehicleCurrencySymbol" key="vehicle.catalog.pricing.currency" />

<%-- ============================================
     ENCABEZADO
     ============================================ --%>
<%@ include file="/common/header.jsp" %>

<%-- ============================================
     HERO
     ============================================ --%>
<section id="hero" class="py-6 py-lg-7">
    <div class="container">
        <div class="hero-section shadow-soft">
            <div class="row align-items-center g-4">
                <div class="col-lg-6">
                    <span class="hero-eyebrow d-inline-flex align-items-center gap-2">
                        <i class="bi bi-geo-alt-fill"></i>
                        <fmt:message key="common.home.hero.badge" />
                    </span>
                    <h1 class="display-5 fw-bold mt-3 text-dark"><fmt:message key="common.home.hero.title" /></h1>
                    <p class="lead text-secondary"><fmt:message key="common.home.hero.subtitle" /></p>
                    <div class="d-flex flex-wrap gap-3 mt-4">
                        <a href="#destacados" class="btn-brand btn-lg px-4 d-inline-flex align-items-center gap-2">
                            <i class="bi bi-car-front-fill"></i>
                            <fmt:message key="home.hero.cta.catalog" />
                        </a>
                        <a href="#faq" class="btn-outline-brand btn-lg px-4 d-inline-flex align-items-center gap-2">
                            <i class="bi bi-question-circle"></i>
                            <fmt:message key="home.hero.cta.faq" />
                        </a>
                    </div>
                    <div class="hero-trust mt-4">
                        <div class="trust-item">
                            <i class="bi bi-shield-check"></i>
                            <span><fmt:message key="home.hero.trust.compliance" /></span>
                        </div>
                        <div class="trust-item">
                            <i class="bi bi-clock-history"></i>
                            <span><fmt:message key="home.hero.trust.availability" /></span>
                        </div>
                    </div>
                </div>
                <div class="col-lg-6">
                    <div class="position-relative hero-media">
                        <img
                            src="https://images.unsplash.com/photo-1511919884226-fd3cad34687c?auto=format&fit=crop&w=900&q=80"
                            class="img-fluid rounded-4"
                            alt="${homeHeroImageAlt}" />
                        <div class="position-absolute bottom-0 start-50 translate-middle-x hero-floating-card p-4 w-100"
                            style="max-width: 420px;">
                            <div class="d-flex align-items-center gap-3">
                                <div class="feature-icon m-0"><i class="bi bi-speedometer2"></i></div>
                                <div>
                                    <p class="mb-1 fw-semibold text-uppercase small text-muted"><fmt:message key="home.hero.service.badge" /></p>
                                    <p class="mb-0 fw-semibold text-dark"><fmt:message key="home.hero.service.caption" /></p>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</section>

<%-- ============================================
     BENEFICIOS DESTACADOS
     ============================================ --%>
<section class="py-6 bg-white">
    <div class="container">
        <header class="section-header text-center mb-5">
            <span class="hero-eyebrow d-inline-flex justify-content-center">
                <fmt:message key="common.home.features.badge" />
            </span>
            <h2 class="display-6 fw-bold mt-3"><fmt:message key="home.features.title" /></h2>
            <p class="text-muted lead mb-0"><fmt:message key="home.features.subtitle" /></p>
        </header>
        <div class="row g-4 text-center">
            <div class="col-md-6 col-lg-3">
                <article class="p-4 h-100 rounded-4 shadow-soft bg-white feature-card">
                    <div class="feature-icon mx-auto"><i class="bi bi-search"></i></div>
                    <h3 class="h5 fw-semibold"><fmt:message key="common.home.feature.1.title" /></h3>
                    <p class="text-muted mb-0"><fmt:message key="common.home.feature.1.desc" /></p>
                </article>
            </div>
            <div class="col-md-6 col-lg-3">
                <article class="p-4 h-100 rounded-4 shadow-soft bg-white feature-card">
                    <div class="feature-icon mx-auto"><i class="bi bi-shield-lock"></i></div>
                    <h3 class="h5 fw-semibold"><fmt:message key="common.home.feature.2.title" /></h3>
                    <p class="text-muted mb-0"><fmt:message key="common.home.feature.2.desc" /></p>
                </article>
            </div>
            <div class="col-md-6 col-lg-3">
                <article class="p-4 h-100 rounded-4 shadow-soft bg-white feature-card">
                    <div class="feature-icon mx-auto"><i class="bi bi-cash-coin"></i></div>
                    <h3 class="h5 fw-semibold"><fmt:message key="common.home.feature.3.title" /></h3>
                    <p class="text-muted mb-0"><fmt:message key="common.home.feature.3.desc" /></p>
                </article>
            </div>
            <div class="col-md-6 col-lg-3">
                <article class="p-4 h-100 rounded-4 shadow-soft bg-white feature-card">
                    <div class="feature-icon mx-auto"><i class="bi bi-headset"></i></div>
                    <h3 class="h5 fw-semibold"><fmt:message key="common.home.feature.4.title" /></h3>
                    <p class="text-muted mb-0"><fmt:message key="common.home.feature.4.desc" /></p>
                </article>
            </div>
        </div>
    </div>
</section>

<%-- ============================================
     VEHÍCULOS DESTACADOS
     ============================================ --%>
<section id="destacados" class="py-6">
    <div class="container">
        <div class="row align-items-center mb-4">
            <div class="col-md-8">
                <h2 class="fw-bold mb-2"><fmt:message key="home.featured.title" /></h2>
                <p class="text-muted mb-0"><fmt:message key="home.featured.subtitle" /></p>
            </div>
            <div class="col-md-4 text-md-end mt-3 mt-md-0">
                <a href="${ctx}/public/VehicleServlet" class="btn-outline-brand px-4 d-inline-flex align-items-center gap-2">
                    <i class="bi bi-grid"></i>
                    <fmt:message key="home.featured.cta.allVehicles" />
                </a>
            </div>
        </div>

        <c:choose>
            <c:when test="${not empty featuredVehicles}">
                <div id="carouselDestacados" class="carousel slide" data-bs-ride="carousel">
                    <div class="carousel-inner">
                        <c:forEach var="vehicle" items="${featuredVehicles}" varStatus="status">
                            <div class="carousel-item ${status.first ? 'active' : ''}">
                                <div class="carousel-vehicle">
                                    <c:choose>
                                        <c:when test="${not empty featuredVehicleImages and featuredVehicleImages[vehicle.vehicleId] != null}">
                                            <c:url value="/public/vehicle-image" var="vehicleImageUrl">
                                                <c:param name="vehicleId" value="${vehicle.vehicleId}" />
                                            </c:url>
                                            <img src="${vehicleImageUrl}" class="d-block w-100" alt="${vehicle.brand} ${vehicle.model}" />
                                        </c:when>
                                        <c:otherwise>
                                            <div class="carousel-placeholder d-flex align-items-center justify-content-center">
                                                <span class="text-muted small text-uppercase fw-semibold">
                                                    <fmt:message key="home.featured.noImage" />
                                                </span>
                                            </div>
                                        </c:otherwise>
                                    </c:choose>
                                    <div class="carousel-caption text-start">
                                        <h4 class="fw-semibold">
                                            <c:out value="${vehicle.brand}" />
                                            <c:if test="${not empty vehicle.model}">
                                                <c:out value=" ${vehicle.model}" />
                                            </c:if>
                                        </h4>
                                        <p class="mb-2">
                                            <c:if test="${vehicle.vehicleCategory != null}">
                                                <c:out value="${vehicle.vehicleCategory.categoryName}" />
                                            </c:if>
                                            <c:if test="${vehicle.manufactureYear != null}">
                                                <c:if test="${vehicle.vehicleCategory != null}">
                                                    &nbsp;·&nbsp;
                                                </c:if>
                                                <c:out value="${vehicle.manufactureYear}" />
                                            </c:if>
                                        </p>
                                        <div class="d-flex flex-wrap align-items-center gap-2 mb-2">
                                            <c:if test="${vehicle.dailyPrice != null}">
                                                <span class="badge bg-brand fs-6">
                                                    <fmt:formatNumber value="${vehicle.dailyPrice}" type="number" maxFractionDigits="2" minFractionDigits="2" />
                                                    ${vehicleCurrencySymbol} / <fmt:message key="home.featured.pricePerDay" />
                                                </span>
                                            </c:if>
                                            <c:if test="${vehicle.vehicleStatus != null && vehicle.vehicleStatus.statusName != null}">
                                                <span class="badge bg-secondary-subtle text-secondary-emphasis fw-semibold">
                                                    <fmt:message key="vehicle.card.status" />:
                                                    <c:out value=" ${vehicle.vehicleStatus.statusName}" />
                                                </span>
                                            </c:if>
                                        </div>
                                        <c:if test="${vehicle.currentHeadquarters != null}">
                                            <p class="mb-0 small d-flex align-items-center gap-2">
                                                <i class="bi bi-geo-alt"></i>
                                                <span>
                                                    <fmt:message key="vehicle.card.headquarters" />:
                                                    <c:out value="${vehicle.currentHeadquarters.name}" />
                                                    <c:if test="${vehicle.currentHeadquarters.city != null || vehicle.currentHeadquarters.province != null}">
                                                        &nbsp;·&nbsp;
                                                        <c:if test="${vehicle.currentHeadquarters.city != null}">
                                                            <c:out value="${vehicle.currentHeadquarters.city.cityName}" />
                                                        </c:if>
                                                        <c:if test="${vehicle.currentHeadquarters.city != null && vehicle.currentHeadquarters.province != null}">, </c:if>
                                                        <c:if test="${vehicle.currentHeadquarters.province != null}">
                                                            <c:out value="${vehicle.currentHeadquarters.province.provinceName}" />
                                                        </c:if>
                                                    </c:if>
                                                </span>
                                            </p>
                                        </c:if>
                                    </div>
                                </div>
                            </div>
                        </c:forEach>
                    </div>
                    <button class="carousel-control-prev" type="button" data-bs-target="#carouselDestacados" data-bs-slide="prev">
                        <span class="carousel-control-prev-icon" aria-hidden="true"></span>
                        <span class="visually-hidden"><fmt:message key="home.featured.carousel.prev" /></span>
                    </button>
                    <button class="carousel-control-next" type="button" data-bs-target="#carouselDestacados" data-bs-slide="next">
                        <span class="carousel-control-next-icon" aria-hidden="true"></span>
                        <span class="visually-hidden"><fmt:message key="home.featured.carousel.next" /></span>
                    </button>
                </div>
            </c:when>
            <c:otherwise>
                <div class="alert alert-info mb-0" role="alert">
                    <fmt:message key="home.featured.empty" />
                </div>
            </c:otherwise>
        </c:choose>
    </div>
</section>

<%-- ============================================
     PREGUNTAS FRECUENTES
     ============================================ --%>
<section id="faq" class="py-6 bg-white">
    <div class="container">
        <div class="row g-5 align-items-center">
            <div class="col-lg-5">
                <h2 class="fw-bold mb-3"><fmt:message key="home.faq.title" /></h2>
                <p class="text-muted mb-4"><fmt:message key="home.faq.subtitle" /></p>
                <div class="nero-cta-card mb-4">
                    <div class="nero-cta-icon">
                        <i class="bi bi-shield-plus"></i>
                    </div>
                    <div>
                        <p class="mb-1 fw-semibold text-uppercase text-primary small">Atención en ruta</p>
                        <p class="nero-faq mb-3">
                            ¿Tuviste un percance con tu mascota u otra mientras conduces? Reserva una cita con NeroVeterinaria.
                        </p>
                        <a class="btn btn-primary nero-cta-btn" href="${pageContext.request.contextPath}/public/nero_login.jsp">
                            <span>Ir al login de Nero</span>
                            <i class="bi bi-arrow-right-short"></i>
                        </a>
                    </div>
                </div>
                <div class="d-flex align-items-center gap-3">
                    <div class="feature-icon m-0"><i class="bi bi-chat-dots"></i></div>
                    <div>
                        <p class="mb-1 fw-semibold"><fmt:message key="home.faq.contact.label" /></p>
                        <p class="mb-0 text-muted"><fmt:message key="home.faq.contact.phone" /></p>
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
<jsp:include page="/common/footer.jsp" />
