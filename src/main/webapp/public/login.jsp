<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ include file="/common/header.jsp" %>
<section class="auth-section py-6">
    <div class="container">
        <div class="auth-layout card-common shadow-soft">
              <div class="auth-intro">
                  <span class="section-eyebrow"><fmt:message key="common.home.hero.badge" /></span>
                  <h2 class="form-title"><fmt:message key="login.title" /></h2>
                  <p class="form-subtitle"><fmt:message key="login.intro" /></p>
                  <ul class="benefits-list">
                    <li><fmt:message key="register.user.benefits.1" /></li>
                    <li><fmt:message key="register.user.benefits.2" /></li>
                    <li><fmt:message key="register.user.benefits.3" /></li>
                </ul>
            </div>
              <form method="post" action="${ctx}/public/login" class="auth-form form-grid single-column">
                  <c:if test="${not empty requestScope.flashSuccess}">
                      <div class="alert alert-success">${requestScope.flashSuccess}</div>
                  </c:if>
                  <c:if test="${not empty requestScope.errorGeneral}">
                      <div class="alert alert-danger">${requestScope.errorGeneral}</div>
                  </c:if>
                  <div class="form-group">
                      <label for="email"><fmt:message key="login.email" /></label>
                    <input type="email" name="email" id="email" class="form-control"
                        value="${requestScope.loginEmail}" required />
                </div>
                <div class="form-group">
                    <label for="password"><fmt:message key="login.password" /></label>
                    <input type="password" name="password" id="password" class="form-control" required />
                </div>
                  <div class="form-group radio-group">
                      <span class="form-label"><fmt:message key="login.type" /></span>
                    <label class="radio-option">
                        <input type="radio" name="type" value="user"
                            ${requestScope.selectedType eq 'employee' ? '' : 'checked'} />
                        <span><fmt:message key="login.type.user" /></span>
                    </label>
                    <label class="radio-option">
                        <input type="radio" name="type" value="employee"
                            ${requestScope.selectedType eq 'employee' ? 'checked' : ''} />
                        <span><fmt:message key="login.type.employee" /></span>
                    </label>
                  </div>
                  <div class="form-group form-actions between">
                      <label class="checkbox-option">
                          <input type="checkbox" name="remember" ${requestScope.rememberSelected ? 'checked' : ''} />
                          <span><fmt:message key="login.remember" /></span>
                      </label>
                      <a class="form-link" href="${ctx}/public/security/recovery"><fmt:message key="login.forgotPassword" /></a>
                  </div>
                  <div class="form-actions full-width">
                      <button type="submit" class="btn-brand"><fmt:message key="login.button" /></button>
                  </div>
              </form>
          </div>
    </div>
</section>
<jsp:include page="/common/footer.jsp" />
