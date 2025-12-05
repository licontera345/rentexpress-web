<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt"%>

<fmt:setLocale value="${sessionScope.appLocale}" />
<fmt:setBundle basename="i18n.Messages" />

<!DOCTYPE html>
<html lang="es">
<head>
  <meta charset="UTF-8" />
  <meta name="viewport" content="width=device-width, initial-scale=1" />
  <title><fmt:message key="nero.appointment.title" /></title>
  
  <link rel="preconnect" href="https://fonts.googleapis.com" />
  <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin />
  <link href="https://fonts.googleapis.com/css2?family=Plus+Jakarta+Sans:wght@400;500;600;700&display=swap" rel="stylesheet" />
  <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css"
        rel="stylesheet"
        integrity="sha384-QWTKZyjpPEjISv5WaRU9OFeRpok6YctnYmDr5pNlyT2bRjXh0JMhjY6hW+ALEwIH"
        crossorigin="anonymous" />
  <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css" 
        rel="stylesheet" />
  <link href="${pageContext.request.contextPath}/css/style.css" rel="stylesheet" />
  
</head>
<body>
  <%@ include file="/common/header.jsp" %>

  <main class="container py-5">
    <section class="row justify-content-center">
      <div class="col-lg-8 col-md-10">
        <h2 class="mb-4 text-center">
          <i class="bi bi-calendar-check me-2"></i>
          <fmt:message key="nero.appointment.header" />
        </h2>
        
        <c:if test="${not empty neroAppointmentSuccess and neroAppointmentSuccess}">
          <div class="alert alert-success alert-dismissible fade show" role="alert">
            <i class="bi bi-check-circle me-2"></i>
            <strong>¡Cita Creada!</strong> Tu cita ha sido registrada exitosamente.
            <c:if test="${not empty neroAppointmentId}">
              ID de Cita: **${neroAppointmentId}**
            </c:if>
            <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
          </div>
        </c:if>
        
        <c:if test="${not empty neroAppointmentError}">
          <div class="alert alert-danger alert-dismissible fade show" role="alert">
            <i class="bi bi-exclamation-triangle me-2"></i>
            <strong>Error al Registrar Cita:</strong> ${neroAppointmentError}
            <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
          </div>
        </c:if>

        <form
            method="post"
            action="<c:url value='/consumesapi' />"
            class="p-4 border rounded shadow-sm bg-light"
        >
          <input type="hidden" name="action" value="create_appointment" />
          
          <div class="row g-3">
            
            <div class="col-md-12">
              <label for="headquartersId" class="form-label">
                <i class="bi bi-house-door me-1"></i>
                <fmt:message key="nero.appointment.headquarters" />
              </label>
              <select class="form-select" id="headquartersId" name="headquartersId" required>
                <option value="" disabled selected>Selecciona una sede...</option>
                <c:forEach var="hq" items="${headquarters}">
                  <option value="${hq.id}">
                    ${hq.name} - (${hq.localityName})
                  </option>
                </c:forEach>
              </select>
            </div>
            
            <div class="col-md-6">
              <label for="date" class="form-label">
                <i class="bi bi-calendar me-1"></i>
                <fmt:message key="nero.appointment.date" />
              </label>
              <input type="date" class="form-control" id="date" name="date" required />
            </div>

            <div class="col-md-6">
              <label for="time" class="form-label">
                <i class="bi bi-clock me-1"></i>
                <fmt:message key="nero.appointment.time" />
              </label>
              <input type="time" class="form-control" id="time" name="time" required />
            </div>

            <div class="col-12">
              <label for="details" class="form-label">
                <i class="bi bi-chat-left-text me-1"></i>
                <fmt:message key="nero.appointment.details" />
              </label>
              <textarea
                id="details"
                name="details"
                rows="4"
                class="form-control"
                placeholder="Describe la situación para que podamos ayudarte mejor..."
              ></textarea>
              <div class="form-text">
                <i class="bi bi-info-circle"></i>
                Proporciona detalles sobre el motivo de tu cita (opcional)
              </div>
            </div>

            <div class="col-12 mt-4">
              <button type="submit" class="btn btn-primary btn-lg w-100">
                <i class="bi bi-check-circle me-2"></i>
                <fmt:message key="nero.appointment.submit" />
              </button>
            </div>

            <div class="col-12">
              <a href="<c:url value='/index.jsp' />" class="btn btn-outline-secondary w-100">
                <i class="bi bi-arrow-left me-2"></i>
                Volver al inicio
              </a>
            </div>
          </div>
        </form>
      </div>
    </section>
  </main>

  <%@ include file="/common/footer.jsp" %>

  <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"
          integrity="sha384-YvpcrYf0tY3lHB60NNkmXc5s9fDVZLESaAA55NDzOxhy9GkcIdslK1eN7N6jIeHz"
          crossorigin="anonymous"></script>
</body>
</html>