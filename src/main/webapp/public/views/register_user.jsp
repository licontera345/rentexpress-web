<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ include file="/common/header.jsp" %>
<fmt:message key="register.user.phone.placeholder" var="registerUserPhonePlaceholder" />
<div class="row justify-content-center">
    <div class="col-lg-7">
        <div class="card card-common mb-4">
            <div class="card-header"><fmt:message key="register.user.heading" /></div>
            <div class="card-body p-4">
                <p class="text-muted"><fmt:message key="register.user.description" /></p>
                <c:if test="${not empty flashSuccess}">
                    <div class="alert alert-success">${flashSuccess}</div>
                </c:if>
                <c:if test="${not empty flashError}">
                    <div class="alert alert-danger">${flashError}</div>
                </c:if>
                <c:if test="${not empty flashInfo}">
                    <div class="alert alert-info">${flashInfo}</div>
                </c:if>
                <c:if test="${not empty errors}">
                    <div class="alert alert-danger">
                        <ul class="mb-0">
                            <c:forEach var="error" items="${errors}">
                                <li>${error}</li>
                            </c:forEach>
                        </ul>
                    </div>
                </c:if>
                <form method="post" action="${ctx}/app/users/register" class="row g-3 needs-validation" novalidate>
                    <div class="col-md-6">
                        <label for="firstName" class="form-label"><fmt:message key="register.user.firstName" /></label>
                        <input type="text" class="form-control" id="firstName" name="firstName" maxlength="120" required
                               value="${not empty formData['firstName'] ? formData['firstName'] : ''}">
                        <div class="form-text"><fmt:message key="register.user.firstName.help" /></div>
                    </div>
                    <div class="col-md-6">
                        <label for="lastName1" class="form-label"><fmt:message key="register.user.lastName1" /></label>
                        <input type="text" class="form-control" id="lastName1" name="lastName1" maxlength="120" required
                               value="${not empty formData['lastName1'] ? formData['lastName1'] : ''}">
                        <div class="form-text"><fmt:message key="register.user.lastName1.help" /></div>
                    </div>
                    <div class="col-md-6">
                        <label for="lastName2" class="form-label"><fmt:message key="register.user.lastName2" /></label>
                        <input type="text" class="form-control" id="lastName2" name="lastName2" maxlength="120"
                               value="${not empty formData['lastName2'] ? formData['lastName2'] : ''}">
                        <div class="form-text"><fmt:message key="register.user.lastName2.help" /></div>
                    </div>
                    <div class="col-md-6">
                        <label for="birthDate" class="form-label"><fmt:message key="register.user.birthDate" /></label>
                        <input type="date" class="form-control" id="birthDate" name="birthDate" required
                               value="${not empty formData['birthDate'] ? formData['birthDate'] : ''}">
                        <div class="form-text"><fmt:message key="register.user.birthDate.help" /></div>
                    </div>
                    <div class="col-md-6">
                        <label for="email" class="form-label"><fmt:message key="register.user.email" /></label>
                        <input type="email" class="form-control" id="email" name="email" maxlength="120" required
                               autocomplete="email"
                               value="${not empty formData['email'] ? formData['email'] : ''}">
                        <div class="form-text"><fmt:message key="register.user.email.help" /></div>
                    </div>
                    <div class="col-md-6">
                        <label for="phone" class="form-label"><fmt:message key="register.user.phone" /></label>
                        <input type="tel" class="form-control" id="phone" name="phone" maxlength="20" placeholder="${registerUserPhonePlaceholder}"
                               autocomplete="tel"
                               value="${not empty formData['phone'] ? formData['phone'] : ''}">
                        <div class="form-text"><fmt:message key="register.user.phone.help" /></div>
                    </div>
                    <div class="col-12">
                        <h2 class="h6 fw-semibold mt-2 mb-0"><fmt:message key="register.user.address.title" /></h2>
                    </div>
                    <div class="col-md-8">
                        <label for="street" class="form-label"><fmt:message key="register.user.street" /></label>
                        <input type="text" class="form-control" id="street" name="street" maxlength="255" required
                               value="${not empty formData['street'] ? formData['street'] : ''}">
                        <div class="form-text"><fmt:message key="register.user.street.help" /></div>
                    </div>
                    <div class="col-md-4">
                        <label for="number" class="form-label"><fmt:message key="register.user.number" /></label>
                        <input type="text" class="form-control" id="number" name="number" maxlength="10" required
                               value="${not empty formData['number'] ? formData['number'] : ''}">
                        <div class="form-text"><fmt:message key="register.user.number.help" /></div>
                    </div>
                    <div class="col-md-6">
                        <label for="province" class="form-label"><fmt:message key="register.user.province" /></label>
                        <select class="form-select" id="province" name="provinceId" required>
                            <option value="" ${empty formData['provinceId'] ? 'selected' : ''}>
                                <fmt:message key="register.user.province.placeholder" />
                            </option>
                            <c:forEach var="province" items="${provinces}">
                                <option value="${province.provinceId}" <c:if test="${formData['provinceId'] eq province.provinceId}">selected</c:if>>
                                    ${province.provinceName}
                                </option>
                            </c:forEach>
                        </select>
                    </div>
                    <div class="col-md-6">
                        <label for="city" class="form-label"><fmt:message key="register.user.city" /></label>
                        <select class="form-select" id="city" name="cityId" required>
                            <option value="" ${empty formData['cityId'] ? 'selected' : ''}>
                                <fmt:message key="register.user.city.placeholder" />
                            </option>
                            <c:forEach var="city" items="${cities}">
                                <c:set var="cityId" value="${city != null && city.id != null ? city.id : 0}" />
                                <c:set var="provinceId" value="${city != null ? city.provinceId : 0}" />
                                <option value="${cityId}" data-province="${provinceId}" <c:if test="${formData['cityId'] eq cityId}">selected</c:if>>
                                    ${city.cityName}
                                </option>
                            </c:forEach>
                        </select>
                        <div class="form-text"><fmt:message key="register.user.city.help" /></div>
                    </div>
                    <div class="col-12">
                        <label for="password" class="form-label"><fmt:message key="register.user.password" /></label>
                        <input type="password" class="form-control" id="password" name="password" minlength="8" required autocomplete="new-password">
                        <div class="form-text"><fmt:message key="register.user.password.help" /></div>
                    </div>
                    <div class="col-12">
                        <label for="confirmPassword" class="form-label"><fmt:message key="register.user.confirmPassword" /></label>
                        <input type="password" class="form-control" id="confirmPassword" name="confirmPassword" minlength="8" required autocomplete="new-password">
                        <div class="form-text"><fmt:message key="register.user.confirmPassword.help" /></div>
                    </div>
                    <div class="col-12">
                        <div class="form-check">
                            <input class="form-check-input" type="checkbox" value="on" id="acceptTerms" name="acceptTerms" required
                                   ${formData['acceptTerms'] eq 'on' ? 'checked' : ''}>
                            <label class="form-check-label" for="acceptTerms">
                                <fmt:message key="register.user.acceptTerms">
                                    <fmt:param>
                                        <a href="${ctx}/public/terms.jsp" class="text-decoration-none"><fmt:message key="register.user.terms" /></a>
                                    </fmt:param>
                                </fmt:message>
                            </label>
                        </div>
                    </div>
                    <div class="col-12 d-flex justify-content-between align-items-center flex-wrap gap-2">
                        <button type="submit" class="btn btn-brand"><fmt:message key="register.user.submit" /></button>
                        <a href="${ctx}/app/auth/login" class="text-decoration-none"><fmt:message key="register.user.already" /></a>
                    </div>
                </form>
                <script>
                    (function () {
                        var provinceSelect = document.getElementById('province');
                        var citySelect = document.getElementById('city');
                        if (!provinceSelect || !citySelect) {
                            return;
                        }

                        function filterCities() {
                            var selectedProvince = provinceSelect.value;
                            var hasVisible = false;
                            for (var i = 0; i < citySelect.options.length; i++) {
                                var option = citySelect.options[i];
                                if (!option.value) {
                                    option.hidden = false;
                                    continue;
                                }
                                var optionProvince = option.getAttribute('data-province');
                                if (selectedProvince && optionProvince === selectedProvince) {
                                    option.hidden = false;
                                    hasVisible = true;
                                } else {
                                    option.hidden = true;
                                }
                            }
                            if (!selectedProvince) {
                                citySelect.value = '';
                                citySelect.disabled = true;
                            } else if (!hasVisible) {
                                citySelect.value = '';
                                citySelect.disabled = true;
                            } else {
                                var currentOption = citySelect.options[citySelect.selectedIndex];
                                if (!currentOption || currentOption.hidden) {
                                    citySelect.value = '';
                                }
                                citySelect.disabled = false;
                            }
                        }

                        filterCities();
                        provinceSelect.addEventListener('change', function () {
                            filterCities();
                        });
                    })();
                </script>
            </div>
        </div>
        <div class="card border-0 bg-light">
            <div class="card-body">
                <h2 class="h5 fw-semibold mb-3"><fmt:message key="register.user.benefits.title" /></h2>
                <ul class="list-unstyled mb-0 text-muted small">
                    <li class="mb-2"><i class="bi bi-check-circle-fill text-brand me-2"></i><fmt:message key="register.user.benefits.1" /></li>
                    <li class="mb-2"><i class="bi bi-check-circle-fill text-brand me-2"></i><fmt:message key="register.user.benefits.2" /></li>
                    <li class="mb-0"><i class="bi bi-check-circle-fill text-brand me-2"></i><fmt:message key="register.user.benefits.3" /></li>
                </ul>
            </div>
        </div>
    </div>
</div>
<%@ include file="/common/footer.jsp" %>
