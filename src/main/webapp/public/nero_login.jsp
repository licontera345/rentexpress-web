<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt"%>

<fmt:setLocale value="${sessionScope.appLocale}" />
<fmt:setBundle basename="i18n.Messages" />
<c:set var="skipDocumentLayout" value="true" scope="request" />

<!DOCTYPE html>
<html lang="es">
<head>
  <meta charset="UTF-8" />
  <meta name="viewport" content="width=device-width, initial-scale=1" />
  <title><fmt:message key="nero.login.title" /></title>
  <link rel="preconnect" href="https://fonts.googleapis.com" />
  <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin />
  <link href="https://fonts.googleapis.com/css2?family=Plus+Jakarta+Sans:wght@400;500;600;700&display=swap" rel="stylesheet" />
  <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css"
        rel="stylesheet"
        integrity="sha384-QWTKZyjpPEjISv5WaRU9OFeRpok6YctnYmDr5pNlyT2bRjXh0JMhjY6hW+ALEwIH"
        crossorigin="anonymous" />
  <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css"
        rel="stylesheet" crossorigin="anonymous" />
  <link rel="stylesheet" href="<c:url value='/css/main.css' />" />
</head>
<body class="auth-body">
  <%@ include file="/common/header.jsp" %>

  <main class="auth-section py-6">
    <div class="container">
      <div class="auth-grid glass-panel">
        <div class="auth-intro">
          <span class="pill">
            <i class="bi bi-shield-check"></i>
            <fmt:message key="nero.login.heading" />
          </span>
          <h1 class="auth-title"><fmt:message key="nero.login.title" /></h1>
          <p class="auth-subtitle"><fmt:message key="nero.login.description" /></p>

          <div class="auth-highlights">
            <div class="auth-highlight">
              <div class="feature-icon"><i class="bi bi-clock-history"></i></div>
              <div>
                <p class="highlight-label text-muted"><fmt:message key="home.hero.trust.availability" /></p>
                <p class="highlight-text">24/7</p>
              </div>
            </div>
            <div class="auth-highlight">
              <div class="feature-icon"><i class="bi bi-people"></i></div>
              <div>
                <p class="highlight-label text-muted"><fmt:message key="home.hero.trust.compliance" /></p>
                <p class="highlight-text">Nero Assist</p>
              </div>
            </div>
          </div>
        </div>

        <div class="auth-card">
          <div class="auth-card-header">
            <div>
              <p class="section-eyebrow">Nero</p>
              <h2 class="auth-card-title"><fmt:message key="nero.login.heading" /></h2>
              <p class="auth-card-subtitle"><fmt:message key="nero.login.description" /></p>
            </div>
            <div class="auth-badge">V.2</div>
          </div>

          <form method="post" action="<c:url value='/consumesapi' />" class="auth-form">
            <div class="form-group">
              <label class="form-label" for="email"><fmt:message key="nero.login.email" /></label>
              <input type="email" id="email" name="email" class="form-control" required />
            </div>
            <div class="form-group">
              <label class="form-label" for="password"><fmt:message key="nero.login.password" /></label>
              <input type="password" id="password" name="password" class="form-control" required />
            </div>
            <button type="submit" class="btn btn-primary w-100"><fmt:message key="nero.login.submit" /></button>
          </form>

          <div class="auth-footer">
            <a href="<c:url value='/public/register_user.jsp' />" class="btn-link">
              <fmt:message key="nero.login.description" />
            </a>
            <div class="auth-contact">
              <span class="small text-muted">🚀</span>
              <span class="small">support@rentexpress.com</span>
            </div>
          </div>
        </div>
      </div>
    </div>
  </main>

  <%@ include file="/common/footer.jsp" %>
</body>
</html>
