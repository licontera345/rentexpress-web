<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ include file="/common/header.jsp" %>
<div class="row justify-content-center">
    <div class="col-lg-8">
        <div class="card shadow-sm mb-4">
            <div class="card-body p-4">
                <h1 class="h3 fw-bold mb-3">Registra un nuevo empleado</h1>
                <p class="text-muted">Utiliza este formulario para adelantar los datos de incorporación. Por ahora el proceso es informativo y el alta definitiva la completa un administrador.</p>
                <c:if test="${not empty messages}">
                    <div class="alert alert-info" role="alert">
                        <c:forEach var="message" items="${messages}">
                            <div>${message}</div>
                        </c:forEach>
                    </div>
                </c:if>
                <form method="post" class="row g-3" novalidate>
                    <div class="col-md-6">
                        <label for="fullName" class="form-label">Nombre completo</label>
                        <input type="text" id="fullName" name="fullName" class="form-control" placeholder="Ej: Laura Gómez" disabled>
                    </div>
                    <div class="col-md-6">
                        <label for="email" class="form-label">Correo corporativo</label>
                        <input type="email" id="email" name="email" class="form-control" placeholder="nombre@rentexpress.com" disabled>
                    </div>
                    <div class="col-md-6">
                        <label for="role" class="form-label">Rol</label>
                        <select id="role" name="role" class="form-select" disabled>
                            <option>Gestor de reservas</option>
                            <option>Responsable de flota</option>
                            <option>Administración</option>
                        </select>
                    </div>
                    <div class="col-md-6">
                        <label for="headquarters" class="form-label">Sede asignada</label>
                        <input type="text" id="headquarters" name="headquarters" class="form-control" placeholder="Madrid Centro" disabled>
                    </div>
                    <div class="col-12">
                        <label for="notes" class="form-label">Notas internas</label>
                        <textarea id="notes" name="notes" class="form-control" rows="4" placeholder="Comentarios adicionales" disabled></textarea>
                    </div>
                    <div class="col-12 d-flex justify-content-between align-items-center mt-3">
                        <span class="text-muted small">El flujo automático de altas estará disponible en breve.</span>
                        <button type="submit" class="btn btn-brand" disabled>Enviar solicitud</button>
                    </div>
                </form>
            </div>
        </div>
        <div class="alert alert-warning" role="alert">
            Mientras tanto, puedes registrar manualmente al empleado contactando con <a href="mailto:rrhh@rentexpress.com" class="alert-link">rrhh@rentexpress.com</a>.
        </div>
    </div>
</div>
<%@ include file="/common/footer.jsp" %>
