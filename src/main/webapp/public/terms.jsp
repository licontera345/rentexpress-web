<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ include file="/common/header.jsp" %>
<div class="row justify-content-center">
    <div class="col-lg-10 col-xl-8">
        <div class="card card-common mb-4">
            <div class="card-header">Términos y condiciones de uso</div>
            <div class="card-body p-4">
                <p class="text-muted">Este resumen explica los puntos esenciales que aceptas al darte de alta en RentExpress. Consulta la documentación legal completa en nuestras oficinas si necesitas más detalle.</p>
                <h2 class="h5 fw-semibold mt-4">Uso permitido de la plataforma</h2>
                <ul class="text-muted">
                    <li>Debes proporcionar información veraz y mantener tus datos actualizados.</li>
                    <li>La cuenta es personal e intransferible; evita compartir tus credenciales.</li>
                    <li>Los pagos y reservas están sujetos a verificación de identidad y disponibilidad de flota.</li>
                </ul>
                <h2 class="h5 fw-semibold mt-4">Protección de datos personales</h2>
                <p class="text-muted">Tratamos tus datos siguiendo la normativa vigente y solo los utilizamos para gestionar tus reservas, notificaciones y comunicaciones comerciales relacionadas con nuestros servicios.</p>
                <h2 class="h5 fw-semibold mt-4">Cancelaciones y modificaciones</h2>
                <p class="text-muted">Las reservas pueden modificarse o cancelarse según las condiciones de cada tarifa. Algunas tarifas especiales pueden no admitir reembolsos.</p>
                <h2 class="h5 fw-semibold mt-4">Contacto</h2>
                <p class="text-muted mb-0">Para cualquier consulta adicional escríbenos a <a href="mailto:legal@rentexpress.com" class="text-decoration-none">legal@rentexpress.com</a>.</p>
            </div>
        </div>
        <a class="btn btn-outline-secondary" href="${ctx}/app/users/register"><i class="bi bi-arrow-left me-2"></i>Volver al formulario de registro</a>
    </div>
</div>
<%@ include file="/common/footer.jsp" %>
