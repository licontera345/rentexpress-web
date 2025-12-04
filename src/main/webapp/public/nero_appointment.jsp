<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt"%>

<fmt:setLocale value="${sessionScope.appLocale}" />
<fmt:setBundle basename="i18n.Messages" />

<!DOCTYPE html>
<html lang="es">
<head>
  <meta charset="UTF-8" />
  <title><fmt:message key="nero.appointment.title" /></title>
  <link rel="stylesheet" href="<c:url value='/css/main.css' />" />
</head>
<body>
  <%@ include file="/common/header.jsp" %>

  <main class="container nero-appointment">
    <header class="page-header">
      <div class="page-heading">
        <p class="pill"><fmt:message key="nero.login.heading" /></p>
        <h1 class="page-title"><fmt:message key="nero.appointment.heading" /></h1>
        <p class="page-subtitle">
          <fmt:message key="nero.appointment.welcome" />
          <strong>${sessionScope.neroClientName}</strong>
        </p>
      </div>
    </header>

    <section class="appointment-layout">
      <div class="appointment-info card surface">
        <p class="section-eyebrow">Nero Assist</p>
        <h2 class="appointment-title">
          <fmt:message key="nero.appointment.title" />
        </h2>
        <p class="appointment-description">
          Agenda rápidamente la revisión con nuestro equipo. Comparte el detalle del
          incidente y obtén una confirmación inmediata de tu cita.
        </p>
        <ul class="appointment-highlights">
          <li>Atención prioritaria para clientes Nero.</li>
          <li>Fechas y horas ajustadas a tu disponibilidad.</li>
          <li>Seguimiento y recordatorios automáticos.</li>
        </ul>
      </div>

      <div class="appointment-card card">
        <h3 class="appointment-card-title">Completa los datos de tu cita</h3>

        <c:if test="${neroAppointmentSuccess}">
          <div class="alert alert-success">
            <fmt:message key="nero.appointment.success" />:
            <strong>${neroAppointmentId}</strong>
          </div>
        </c:if>

        <c:if test="${not empty neroAppointmentError}">
          <div class="alert alert-danger">
            ${neroAppointmentError}
          </div>
        </c:if>

        <form method="post" action="<c:url value='/consumesapi' />" class="appointment-form">
          <input type="hidden" name="action" value="create_appointment" />

          <div class="form-grid">
            <label for="headquartersId" class="form-label">
              <fmt:message key="nero.appointment.headquarters" />
            </label>
            <input
              type="text"
              id="headquartersId"
              name="headquartersId"
              class="form-control"
              placeholder="Ej: CDMX-01"
              required
            />

            <label for="date" class="form-label">
              <fmt:message key="nero.appointment.date" />
            </label>
            <input type="date" id="date" name="date" class="form-control" required />

            <label for="time" class="form-label">
              <fmt:message key="nero.appointment.time" />
            </label>
            <input type="time" id="time" name="time" class="form-control" required />

            <label for="details" class="form-label">
              <fmt:message key="nero.appointment.details" />
            </label>
            <textarea
              id="details"
              name="details"
              rows="3"
              class="form-control"
              placeholder="Describe la situación para que podamos ayudarte mejor"
            ></textarea>
          </div>

          <div class="form-actions full-width">
            <button type="submit" class="btn btn-primary">
              <fmt:message key="nero.appointment.submit" />
            </button>
          </div>
        </form>
      </div>
    </section>
  </main>

  <%@ include file="/common/footer.jsp" %>
</body>
</html>
