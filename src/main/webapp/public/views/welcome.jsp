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
<section class="section-spacing">
    <div class="text-center mb-5">
        <span class="section-eyebrow">Tu punto de partida</span>
        <h2 class="display-6 fw-semibold mb-3">Todo lo necesario para presentar RentExpress con orgullo</h2>
        <p class="section-description mx-auto">
            Unifica tu documentación académica con una experiencia visual moderna. Cada módulo del middleware ya
            disponible tiene su propio espacio para brillar en el welcome.
        </p>
    </div>
    <div class="row g-4">
        <div class="col-md-4">
            <div class="glass-card h-100">
                <div class="icon-badge bg-icon-primary"><i class="bi bi-person-check"></i></div>
                <h3 class="h5 fw-bold mb-2">Autenticación impecable</h3>
                <p class="text-muted mb-3">Formulario de login con 2FA, recuperación de contraseña y recordatorio de sesión
                    listos para mostrarse desde el primer día.</p>
                <ul class="list-check">
                    <li>JSP dedicadas a login y recuperación</li>
                    <li>Mensajería flash integrada</li>
                    <li>Enlaces directos a rutas públicas y privadas</li>
                </ul>
            </div>
        </div>
        <div class="col-md-4">
            <div class="glass-card h-100">
                <div class="icon-badge bg-icon-secondary"><i class="bi bi-kanban"></i></div>
                <h3 class="h5 fw-bold mb-2">Gestión operativa clara</h3>
                <p class="text-muted mb-3">Presenta el catálogo de vehículos, el informe público de alquileres y los
                    formularios de empleados con llamadas a la acción coherentes.</p>
                <ul class="list-check">
                    <li>Accesos a catálogo y reportes</li>
                    <li>Cards con estados y métricas</li>
                    <li>Diseño responsive con Bootstrap 5</li>
                </ul>
            </div>
        </div>
        <div class="col-md-4">
            <div class="glass-card h-100">
                <div class="icon-badge bg-icon-tertiary"><i class="bi bi-diagram-3"></i></div>
                <h3 class="h5 fw-bold mb-2">Middleware aprovechado</h3>
                <p class="text-muted mb-3">Destaca que todo se apoya en los DAO y servicios del paquete `RentExpres.jar`,
                    evitando desarrollos duplicados.</p>
                <ul class="list-check">
                    <li>Clases DAO y DTO referenciadas</li>
                    <li>Configuraciones centralizadas</li>
                    <li>Procesos alineados al README</li>
                </ul>
            </div>
        </div>
    </div>
</section>
<section class="section-spacing">
    <div class="gradient-panel">
        <div class="row g-4 align-items-center">
            <div class="col-lg-5">
                <span class="section-eyebrow text-white">Flujos clave</span>
                <h2 class="fw-semibold text-white mb-3">Recorre el ciclo completo de reservas y alquileres</h2>
                <p class="text-white-50 mb-4">El welcome se convierte en tu carta de presentación: muestra los procesos
                    disponibles y guía al docente a las vistas privadas que prueban la integración.</p>
                <div class="d-flex flex-wrap gap-3">
                    <a class="btn btn-light btn-lg px-4" href="${ctx}/public/vehicles">
                        <i class="bi bi-car-front-fill me-2"></i>Ver catálogo público
                    </a>
                    <a class="btn btn-outline-light btn-lg px-4" href="${ctx}/public/rentals">
                        <i class="bi bi-bar-chart-line me-2"></i>Informe de actividad
                    </a>
                </div>
            </div>
            <div class="col-lg-7">
                <div class="row g-4">
                    <div class="col-sm-6">
                        <div class="step-card h-100">
                            <div class="step-number">01</div>
                            <h3 class="h6 fw-bold mb-2">Configura tu entorno</h3>
                            <p class="text-muted mb-0">Importa el proyecto Maven, valida el uso de Java 1.8 y prepara las
                                propiedades de conexión.</p>
                        </div>
                    </div>
                    <div class="col-sm-6">
                        <div class="step-card h-100">
                            <div class="step-number">02</div>
                            <h3 class="h6 fw-bold mb-2">Conecta el middleware</h3>
                            <p class="text-muted mb-0">Reutiliza los DAO y servicios empaquetados para poblar los JSP sin
                                duplicar lógica.</p>
                        </div>
                    </div>
                    <div class="col-sm-6">
                        <div class="step-card h-100">
                            <div class="step-number">03</div>
                            <h3 class="h6 fw-bold mb-2">Personaliza la experiencia</h3>
                            <p class="text-muted mb-0">Ajusta roles, sesiones y notificaciones para entregar una demo
                                convincente.</p>
                        </div>
                    </div>
                    <div class="col-sm-6">
                        <div class="step-card h-100">
                            <div class="step-number">04</div>
                            <h3 class="h6 fw-bold mb-2">Entrega evidencias</h3>
                            <p class="text-muted mb-0">Captura pantallas, prepara reportes y documenta tus pruebas para la
                                revisión final.</p>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</section>
<section class="section-spacing">
    <div class="row g-4 align-items-stretch">
        <div class="col-lg-7">
            <div class="glass-card h-100">
                <div class="section-eyebrow">Checklist del README</div>
                <h2 class="h4 fw-semibold mb-3">Lo que el jurado espera ver funcionando</h2>
                <p class="text-muted mb-4">El welcome resume los apartados críticos del README para que nadie dude de que
                    tu entrega cumple los mínimos profesionales.</p>
                <div class="row g-4">
                    <div class="col-sm-6">
                        <div class="mini-card">
                            <i class="bi bi-fingerprint"></i>
                            <h3 class="h6 fw-bold mb-1">Seguridad</h3>
                            <p class="text-muted mb-0">Login, 2FA, filtros de autorización y control de sesiones documentados.</p>
                        </div>
                    </div>
                    <div class="col-sm-6">
                        <div class="mini-card">
                            <i class="bi bi-gear"></i>
                            <h3 class="h6 fw-bold mb-1">Reglas de negocio</h3>
                            <p class="text-muted mb-0">Reservas, retenciones y notificaciones soportadas por tus servlets.</p>
                        </div>
                    </div>
                    <div class="col-sm-6">
                        <div class="mini-card">
                            <i class="bi bi-translate"></i>
                            <h3 class="h6 fw-bold mb-1">Internacionalización</h3>
                            <p class="text-muted mb-0">Selector de idioma y textos preparados para múltiples locales.</p>
                        </div>
                    </div>
                    <div class="col-sm-6">
                        <div class="mini-card">
                            <i class="bi bi-cloud-arrow-up"></i>
                            <h3 class="h6 fw-bold mb-1">Gestión de archivos</h3>
                            <p class="text-muted mb-0">Subida de imágenes y descarga controlada desde el catálogo.</p>
                        </div>
                    </div>
                </div>
            </div>
        </div>
        <div class="col-lg-5">
            <div class="glass-card h-100">
                <div class="section-eyebrow">Recursos rápidos</div>
                <h3 class="h5 fw-semibold mb-3">Rutas que validan tu integración</h3>
                <ul class="resource-list">
                    <li>
                        <i class="bi bi-box-arrow-in-right"></i>
                        <div>
                            <span class="resource-title">Acceso seguro</span>
                            <a class="resource-link" href="${ctx}/app/auth/login">${ctx}/app/auth/login</a>
                        </div>
                    </li>
                    <li>
                        <i class="bi bi-people"></i>
                        <div>
                            <span class="resource-title">Gestión de usuarios</span>
                            <a class="resource-link" href="${ctx}/public/users">${ctx}/public/users</a>
                        </div>
                    </li>
                    <li>
                        <i class="bi bi-file-text"></i>
                        <div>
                            <span class="resource-title">Formularios estructurados</span>
                            <a class="resource-link" href="${ctx}/public/employees">${ctx}/public/employees</a>
                        </div>
                    </li>
                    <li>
                        <i class="bi bi-database"></i>
                        <div>
                            <span class="resource-title">DAO del middleware</span>
                            <span class="resource-hint">`src/main/webapp/WEB-INF/lib/RentExpres.jar`</span>
                        </div>
                    </li>
                </ul>
                <div class="alert alert-info shadow-soft mt-4 mb-0">
                    Mantén esta sección actualizada cuando incorpores nuevas vistas o endpoints relevantes.
                </div>
            </div>
        </div>
    </div>
</section>
<section class="section-spacing">
    <div class="cta-banner">
        <div>
            <span class="section-eyebrow text-white">Listo para demostrar</span>
            <h2 class="h3 fw-semibold text-white mb-2">Explica tu arquitectura con una sola mirada</h2>
            <p class="text-white-50 mb-0">Incluye esta pantalla en tus entregas, conectada a cada servlet y JSP clave para
                evidenciar que dominaste el stack completo.</p>
        </div>
        <div class="d-flex flex-wrap gap-3">
            <a class="btn btn-light btn-lg px-4" href="${ctx}/app/users/register">
                <i class="bi bi-person-plus me-2"></i>Crear cuenta demo
            </a>
            <a class="btn btn-outline-light btn-lg px-4" href="${ctx}/public/views/login.jsp">
                <i class="bi bi-eye me-2"></i>Previsualizar login JSP
            </a>
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
