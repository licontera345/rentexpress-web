<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>

<%-- ============================================
     CONFIGURACIÓN
     ============================================ --%>
<fmt:setLocale value="${sessionScope.appLocale != null ? sessionScope.appLocale : pageContext.request.locale}" scope="session" />
<fmt:setBundle basename="i18n.Messages" scope="session" />
<c:set var="ctx" value="${pageContext.request.contextPath}" />
<c:set var="provinces" value="${requestScope.provinces}" />
<c:set var="cities" value="${requestScope.cities}" />
<c:url var="citiesByProvinceUrl" value="/public/cities/by-province" />
<fmt:message var="firstNamePlaceholder" key="register.firstName" />
<fmt:message var="lastName1Placeholder" key="register.lastName1" />
<fmt:message var="lastName2Placeholder" key="register.lastName2" />
<fmt:message var="emailPlaceholder" key="register.email" />
<fmt:message var="usernamePlaceholder" key="register.username" />
<fmt:message var="passwordPlaceholder" key="register.password" />
<fmt:message var="phonePlaceholder" key="register.phone" />
<fmt:message var="birthDatePlaceholder" key="register.birthDate" />
<fmt:message var="streetPlaceholder" key="register.street" />
<fmt:message var="addressNumberPlaceholder" key="register.addressNumber" />
<fmt:message var="provincePlaceholder" key="register.province.placeholder" />
<fmt:message var="provinceLabel" key="register.province" />
<fmt:message var="cityPlaceholder" key="register.city.placeholder" />
<fmt:message var="cityLabel" key="register.city" />
<%@ include file="/common/header.jsp" %>

<%-- ============================================
     VALIDACIONES
     ============================================ --%>
<c:set var="flashError" value="${requestScope.flashError}" />

<%-- ============================================
     FORMULARIO/CONTENIDO
     ============================================ --%>

<section class="auth-section register-section py-6">
  <div class="container">
    <div class="register-card">
      <div class="register-card__glow"></div>
      <div class="auth-layout">
        <div class="auth-intro">
          <span class="auth-badge"><fmt:message key="common.home.features.badge" /></span>
          <h2 class="auth-title"><fmt:message key="register.user.heading" /></h2>
          <p class="auth-copy"><fmt:message key="register.user.description" /></p>
          <ul class="benefits-list">
            <li><fmt:message key="register.user.benefits.1" /></li>
            <li><fmt:message key="register.user.benefits.2" /></li>
            <li><fmt:message key="register.user.benefits.3" /></li>
          </ul>
        </div>

        <c:if test="${not empty flashError}">
          <div class="alert alert-danger" role="alert">${flashError}</div>
        </c:if>
        <form method="post" action="${ctx}/public/users/register" class="auth-form form-grid two-columns">
          <input type="text" name="firstName" class="input-field" placeholder="${firstNamePlaceholder}" required
            value="${fn:escapeXml(param.firstName)}">
          <input type="text" name="lastName1" class="input-field" placeholder="${lastName1Placeholder}" required
            value="${fn:escapeXml(param.lastName1)}">
          <input type="text" name="lastName2" class="input-field" placeholder="${lastName2Placeholder}"
            value="${fn:escapeXml(param.lastName2)}">
          <input type="email" name="email" class="input-field" placeholder="${emailPlaceholder}" required
            value="${fn:escapeXml(param.email)}">
          <input type="text" name="username" class="input-field" placeholder="${usernamePlaceholder}" required
            value="${fn:escapeXml(param.username)}">
          <input type="password" name="password" class="input-field" placeholder="${passwordPlaceholder}" required>
          <input type="text" name="phone" class="input-field" placeholder="${phonePlaceholder}"
            value="${fn:escapeXml(param.phone)}">
          <input type="date" name="birthDate" class="input-field" placeholder="${birthDatePlaceholder}"
            value="${param.birthDate}">

          <input type="text" name="street" class="input-field" placeholder="${streetPlaceholder}" required
            value="${fn:escapeXml(param.street)}">
          <input type="text" name="addressNumber" class="input-field" placeholder="${addressNumberPlaceholder}" required
            value="${fn:escapeXml(param.addressNumber)}">

          <select name="provinceId" id="provinceId" class="input-field" data-selected-province="${param.provinceId}" required aria-label="${provinceLabel}">
            <option value="">${provincePlaceholder}</option>
            <c:forEach var="p" items="${provinces}">
              <option value="${p.provinceId}" <c:if test="${param.provinceId == p.provinceId}">selected</c:if>>${p.provinceName}</option>
            </c:forEach>
          </select>

          <select
            name="cityId"
            id="cityId"
            class="input-field"
            data-placeholder="${cityPlaceholder}"
            data-selected-city="${param.cityId}"
            required aria-label="${cityLabel}">
            <option value="">${cityPlaceholder}</option>
            <c:forEach var="c" items="${cities}">
              <option value="${c.id}" <c:if test="${param.cityId == c.id}">selected</c:if>>${c.cityName}</option>
            </c:forEach>
          </select>

          <div class="form-actions form-full">
            <button type="submit" class="btn btn-primary btn-gradient">
              <fmt:message key="register.user.submit" />
            </button>
          </div>
        </form>
      </div>
    </div>
  </div>

  <script>
    document.addEventListener('DOMContentLoaded', function () {
      const provinceSelect = document.getElementById('provinceId');
      const citySelect = document.getElementById('cityId');

      if (!provinceSelect || !citySelect) {
        return;
      }

      const endpoint = '${citiesByProvinceUrl}';
      const placeholder = citySelect.dataset.placeholder || (citySelect.options[0] ? citySelect.options[0].textContent : '');
      const selectedProvince = provinceSelect.dataset.selectedProvince;
      const selectedCity = citySelect.dataset.selectedCity;

      const resetCities = function () {
        citySelect.innerHTML = '';
        const defaultOption = document.createElement('option');
        defaultOption.value = '';
        defaultOption.textContent = placeholder || '';
        citySelect.appendChild(defaultOption);
      };

      const populateCities = function (cities, cityToSelect) {
        if (!Array.isArray(cities)) {
          return;
        }

        cities.forEach(function (city) {
          if (!city || typeof city.id === 'undefined' || city.id === null) {
            return;
          }

          const option = document.createElement('option');
          option.value = city.id;
          option.textContent = city.name || '';

          if (cityToSelect && String(city.id) === String(cityToSelect)) {
            option.selected = true;
          }

          citySelect.appendChild(option);
        });
      };

      const loadCities = function (provinceId, cityToSelect) {
        if (!provinceId) {
          resetCities();
          return;
        }

        fetch(endpoint + '?provinceId=' + encodeURIComponent(provinceId), {
          headers: {
            Accept: 'application/json',
          },
        })
          .then(function (response) {
            if (!response.ok) {
              throw new Error('Network response was not ok');
            }
            return response.json();
          })
          .then(function (data) {
            resetCities();
            populateCities(data, cityToSelect);
          })
          .catch(function (error) {
            console.error('Error loading cities', error);
            resetCities();
          });
      };

      provinceSelect.addEventListener('change', function (event) {
        loadCities(event.target.value, null);
      });

      if (selectedProvince) {
        loadCities(selectedProvince, selectedCity);
      } else {
        resetCities();
      }
    });
  </script>
</section>

<%@ include file="/common/footer.jsp" %>
