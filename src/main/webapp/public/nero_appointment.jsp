<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<fmt:setLocale value="${sessionScope.appLocale}" />
<fmt:setBundle basename="i18n.Messages" />

<!DOCTYPE html>
<html lang="es">
<head>
  <meta charset="UTF-8" />
  <title><fmt:message key="nero.appointment.title" /></title>
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/main.css" />
</head>
<body>
  <%@ include file="/common/header.jsp" %>

  <main class="container">
    <h1><fmt:message key="nero.appointment.heading" /></h1>

    <p>
      <fmt:message key="nero.appointment.welcome" />
      <strong>${sessionScope.neroClientName}</strong>
    </p>

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

    <form method="post" action="${pageContext.request.contextPath}/consumesapi">
      <input type="hidden" name="action" value="create_appointment" />

      <label for="headquartersId"><fmt:message key="nero.appointment.headquarters" /></label>
      <input type="text" id="headquartersId" name="headquartersId" required />

      <label for="date"><fmt:message key="nero.appointment.date" /></label>
      <input type="date" id="date" name="date" required />

      <label for="time"><fmt:message key="nero.appointment.time" /></label>
      <input type="time" id="time" name="time" required />

      <label for="details"><fmt:message key="nero.appointment.details" /></label>
      <textarea id="details" name="details" rows="3"></textarea>

      <button type="submit"><fmt:message key="nero.appointment.submit" /></button>
    </form>
  </main>

  <%@ include file="/common/footer.jsp" %>
</body>
</html>
