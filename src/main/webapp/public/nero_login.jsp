<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<fmt:setLocale value="${sessionScope.appLocale}" />
<fmt:setBundle basename="i18n.Messages" />

<!DOCTYPE html>
<html lang="es">
<head>
  <meta charset="UTF-8" />
  <title><fmt:message key="nero.login.title" /></title>
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/main.css" />
</head>
<body>
  <%@ include file="/common/header.jsp" %>

  <main class="container">
    <h1><fmt:message key="nero.login.heading" /></h1>
    <p><fmt:message key="nero.login.description" /></p>

    <form method="post" action="${pageContext.request.contextPath}/consumesapi">
      <label for="email"><fmt:message key="nero.login.email" /></label>
      <input type="email" id="email" name="email" required />

      <label for="password"><fmt:message key="nero.login.password" /></label>
      <input type="password" id="password" name="password" required />

      <button type="submit"><fmt:message key="nero.login.submit" /></button>
    </form>
  </main>

  <%@ include file="/common/footer.jsp" %>
</body>
</html>
