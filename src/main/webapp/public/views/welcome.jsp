<%@ include file="/common/header.jsp" %>
<c:if test="${not empty flashSuccess}">
    <div class="alert alert-success shadow-soft border-0">${flashSuccess}</div>
</c:if>
<c:if test="${not empty flashError}">
    <div class="alert alert-danger shadow-soft border-0">${flashError}</div>
</c:if>
<section class="hero-section position-relative overflow-hidden">
    <div class="row align-items-center g-5">
        <div class="col-lg-7">
            <div class="hero-badge mb-3">
                <i class="bi bi-stars"></i>
                Lanzamiento de la plataforma académica
            </div>
            <h1 class="display-5 fw-bold mb-3 text-brand">Gestiona reservas y alquileres como un profesional</h1>
            <p class="lead text-muted mb-4">
                RentExpress te guía paso a paso para construir una aplicación Java EE completa:
                validación de formularios, sesiones, seguridad, CRUD y más. Conéctate con tus
                credenciales oficiales y explora cada flujo mientras integras tu base de datos.
            </p>
            <div class="d-flex flex-wrap gap-3">
                <a class="btn btn-brand btn-lg px-4" href="${ctx}/app/auth/login">
                    <i class="bi bi-box-arrow-in-right me-2"></i>Inicia sesión con tu cuenta oficial
                </a>
                <a class="btn btn-outline-brand btn-lg px-4" href="${ctx}/app/users/register">
                    <i class="bi bi-person-plus-fill me-2"></i>Regístrate y prepara tu proyecto
                </a>
            </div>
            <div class="hero-trust mt-4">
                <span class="trust-item"><i class="bi bi-shield-check"></i> Sesiones seguras</span>
                <span class="trust-item"><i class="bi bi-translate"></i> I18n preparada</span>
                <span class="trust-item"><i class="bi bi-hdd-network"></i> DAO en middleware</span>
            </div>
        </div>
        <div class="col-lg-5">
            <div class="hero-floating-card bg-white p-4 shadow-soft rounded-4">
                <h2 class="h4 fw-semibold mb-3">¿Qué incluye el sprint inicial?</h2>
                <ul class="list-unstyled mb-0">
                    <li class="d-flex align-items-start gap-3 mb-3">
                        <div class="feature-icon"><i class="bi bi-person-badge"></i></div>
                        <div>
                            <h3 class="h6 fw-bold mb-1">Autenticación básica</h3>
                            <p class="text-muted mb-0">Inicio de sesión con 2FA, opción "recordarme" y gestión de sesión.</p>
                        </div>
                    </li>
                    <li class="d-flex align-items-start gap-3 mb-3">
                        <div class="feature-icon"><i class="bi bi-ui-checks-grid"></i></div>
                        <div>
                            <h3 class="h6 fw-bold mb-1">Patrón MVC aplicado</h3>
                            <p class="text-muted mb-0">Servlets limpios que delegan en JSP estilizadas con Bootstrap.</p>
                        </div>
                    </li>
                    <li class="d-flex align-items-start gap-3">
                        <div class="feature-icon"><i class="bi bi-rocket-takeoff"></i></div>
                        <div>
                            <h3 class="h6 fw-bold mb-1">Registro guiado</h3>
                            <p class="text-muted mb-0">Valida tus formularios y conserva un histórico académico.</p>
                        </div>
                    </li>
                </ul>
            </div>
        </div>
    </div>
</section>
<c:if test="${not empty currentUser}">
    <section class="mt-5">
        <div class="card card-common">
            <div class="card-header">Sesión activa</div>
            <div class="card-body">
                <p class="mb-0">Has iniciado sesión como <strong>${currentUser}</strong>. Usa el menú para seguir explorando.</p>
            </div>
        </div>
    </section>
</c:if>
<%@ include file="/common/footer.jsp" %>
