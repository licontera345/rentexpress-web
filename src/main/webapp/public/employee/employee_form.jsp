<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<fmt:setLocale value="${sessionScope.locale}" scope="session" />
<fmt:setBundle basename="i18n.Messages" />

<!DOCTYPE html>
<html lang="${sessionScope.locale.language != null ? sessionScope.locale.language : 'es'}">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>
        <c:choose>
            <c:when test="${empty employee}">
                <fmt:message key="employee.create.title" />
            </c:when>
            <c:otherwise>
                <fmt:message key="employee.edit.title" />
            </c:otherwise>
        </c:choose>
    </title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet" integrity="sha384-QWTKZyjpPEjISv5WaRU9OFeRpok6YctnYmDr5pNlyT2bRjXh0JMhjY6hW+ALEwIH" crossorigin="anonymous">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.css" rel="stylesheet">
    <link href="https://cdn.jsdelivr.net/npm/flag-icons@6.6.6/css/flag-icons.min.css" rel="stylesheet">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/main.css">
</head>
<body class="bg-light d-flex flex-column min-vh-100">
    <%@ include file="/common/header.jsp"%>

    <main class="flex-grow-1 py-5">
        <div class="container">
            <div class="row g-4">
                <div class="col-lg-4">
                    <div class="card card-common h-100">
                        <div class="card-body">
                            <div class="d-flex flex-column align-items-center text-center">
                                <div class="avatar-wrapper mb-3">
                                <img src="https://ui-avatars.com/api/?background=66b2ff&color=fff&name=<c:out value='${employee.employeeName}'/>" alt="avatar">
                                <c:if test="${not empty employee}">
                                        <c:set var="employeeId" value="${not empty employee.id ? employee.id : (not empty employee.employeeId ? employee.employeeId : employee.idEmployee)}" />
                                        <a href="${pageContext.request.contextPath}/public/EmployeeServlet?action=detail&id=${employeeId}" class="avatar-edit-btn" title="<fmt:message key='profile.avatar.change' />">
                                            <i class="bi bi-camera"></i>
                                        </a>
                                </c:if>
                            </div>
                            <h5 class="fw-semibold mb-1"><c:out value="${employee.employeeName != null ? employee.employeeName : 'RentExpress Employee'}" /></h5>
                            <p class="text-muted small mb-3"><c:out value="${employee.email != null ? employee.email : ''}" /></p>
                                <div class="w-100 bg-light rounded-3 p-3 text-start">
                                    <p class="fw-semibold mb-1"><fmt:message key="profile.status.complete" /></p>
                                    <p class="text-muted small mb-0"><fmt:message key="profile.status.complete.desc" /></p>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>

                <div class="col-lg-8">
                    <div class="card card-common">
                        <div class="card-body p-4">
                            <h3 class="fw-bold mb-4">
                                <c:choose>
                                    <c:when test="${empty employee}">
                                        <fmt:message key="employee.create.title" />
                                    </c:when>
                                    <c:otherwise>
                                        <fmt:message key="employee.edit.title" />
                                    </c:otherwise>
                                </c:choose>
                            </h3>

                            <form action="${pageContext.request.contextPath}/public/EmployeeServlet" method="post" class="needs-validation" novalidate>
                                <input type="hidden" name="action" value="${empty employee ? 'save' : 'update'}" />
                                <c:if test="${not empty employee}">
                                    <input type="hidden" name="id" value="${employeeId}" />
                                </c:if>

                                <c:if test="${not empty formErrorMessageKey}">
                                    <div class="alert alert-danger" role="alert">
                                        <fmt:message key="${formErrorMessageKey}" />
                                    </div>
                                </c:if>

                                <div class="row g-4">
                                    <div class="col-12">
                                        <div class="form-section">
                                            <p class="form-section-title"><fmt:message key="profile.section.personal" /></p>
                                            <div class="row g-3">
                                                <div class="col-md-6">
                                                    <label for="nombreUsuario" class="form-label"><fmt:message key="employee.detail.name" /></label>
                                                    <input type="text" class="form-control" id="nombreUsuario" name="username" value="${employee.employeeName}" required>
                                                    <div class="invalid-feedback">
                                                        <fmt:message key="employee.detail.name" />
                                                    </div>
                                                </div>
                                                <div class="col-md-6">
                                                    <label for="password" class="form-label"><fmt:message key="login.password" /></label>
                                                    <input type="password" class="form-control" id="password" name="password" autocomplete="new-password"
                                                        <c:if test="${empty employee}">required</c:if>>
                                                </div>
                                                <div class="col-md-6">
                                                    <label for="firstName" class="form-label"><fmt:message key="profile.firstName" /></label>
                                                    <input type="text" class="form-control" id="firstName" name="firstName" value="${employee.firstName}">
                                                </div>
                                                <div class="col-md-6">
                                                    <label for="lastName1" class="form-label"><fmt:message key="profile.lastName" /></label>
                                                    <input type="text" class="form-control" id="lastName1" name="lastName1" value="${employee.lastName1}">
                                                </div>
                                                <div class="col-md-6">
                                                    <label for="lastName2" class="form-label"><fmt:message key="profile.lastName2" /></label>
                                                    <input type="text" class="form-control" id="lastName2" name="lastName2" value="${employee.lastName2}">
                                                </div>
                                            </div>
                                        </div>
                                    </div>

                                    <div class="col-12">
                                        <div class="form-section">
                                            <p class="form-section-title"><fmt:message key="profile.section.contact" /></p>
                                            <div class="row g-3">
                                                <div class="col-md-6">
                                                    <label for="email" class="form-label"><fmt:message key="employee.detail.email" /></label>
                                                    <input type="email" class="form-control" id="email" name="email" value="${employee.email}" required>
                                                    <div class="invalid-feedback">
                                                        <fmt:message key="employee.detail.email" />
                                                    </div>
                                                </div>
                                                <div class="col-md-6">
                                                    <label for="phone" class="form-label"><fmt:message key="profile.phone" /></label>
                                                    <input type="tel" class="form-control" id="phone" name="phone" value="${employee.phone}" placeholder="+34 600 000 000">
                                                </div>
                                                <div class="col-md-12">
                                                    <label for="address" class="form-label"><fmt:message key="profile.address" /></label>
                                                    <input type="text" class="form-control" id="address" name="address" placeholder="Calle Mayor, 15">
                                                </div>
                                            </div>
                                        </div>
                                    </div>

                                    <div class="col-12">
                                        <div class="form-section">
                                            <p class="form-section-title"><fmt:message key="profile.section.location" /></p>
                                            <div class="row g-3">
                                                <div class="col-md-6">
                                                    <label for="provinceId" class="form-label"><fmt:message key="profile.province" /></label>
                                                    <select class="form-select" id="provinceId" name="provinceId">
                                                        <option value="">--</option>
                                                    </select>
                                                </div>
                                                <div class="col-md-6">
                                                    <label for="cityId" class="form-label"><fmt:message key="profile.city" /></label>
                                                    <select class="form-select" id="cityId" name="cityId" disabled>
                                                        <option value=""><fmt:message key="profile.locale.select" /></option>
                                                    </select>
                                                </div>
                                                <div class="col-md-6">
                                                    <label for="postalCode" class="form-label"><fmt:message key="profile.postalCode" /></label>
                                                    <input type="text" class="form-control" id="postalCode" name="postalCode" placeholder="28013">
                                                </div>
                                                <div class="col-md-6">
                                                    <div class="form-check mt-4 pt-2">
                                                        <input class="form-check-input" type="checkbox" id="newsletter" name="newsletter">
                                                        <label class="form-check-label" for="newsletter">
                                                            <fmt:message key="profile.newsletter" />
                                                        </label>
                                                    </div>
                                                </div>
                                            </div>
                                        </div>
                                    </div>

                                    <div class="col-12">
                                        <div class="form-section">
                                            <p class="form-section-title"><fmt:message key="profile.section.company" /></p>
                                            <div class="row g-3">
                                                <div class="col-md-6">
                                                    <label for="roleId" class="form-label"><fmt:message key="employee.form.role" /></label>
                                                    <select class="form-select" id="roleId" name="roleId">
                                                        <option value="">--</option>
                                                        <c:forEach var="role" items="${roles}">
                                                            <option value="${role.roleId}" <c:if test="${role.roleId eq employee.roleId}">selected</c:if>>
                                                                <c:out value="${role.roleName}" />
                                                            </option>
                                                        </c:forEach>
                                                    </select>
                                                </div>
                                                <div class="col-md-6">
                                                    <label for="headquartersId" class="form-label"><fmt:message key="employee.form.headquarters" /></label>
                                                    <select class="form-select" id="headquartersId" name="headquartersId">
                                                        <option value="">--</option>
                                                        <c:forEach var="headquarters" items="${headquartersList}">
                                                            <option value="${headquarters.headquartersId}"
                                                                data-province-id="${headquarters.province != null ? headquarters.province.provinceId : ''}"
                                                                data-city-id="${headquarters.city != null ? headquarters.city.cityId : ''}"
                                                                data-street="${headquarters.address != null ? fn:escapeXml(headquarters.address.street) : ''}"
                                                                data-number="${headquarters.address != null ? fn:escapeXml(headquarters.address.number) : ''}"
                                                                data-city-name="${headquarters.city != null ? fn:escapeXml(headquarters.city.cityName) : ''}"
                                                                data-province-name="${headquarters.province != null ? fn:escapeXml(headquarters.province.provinceName) : ''}"
                                                                data-phone="${headquarters.phone != null ? fn:escapeXml(headquarters.phone) : ''}"
                                                                data-email="${headquarters.email != null ? fn:escapeXml(headquarters.email) : ''}"
                                                                <c:if test="${headquarters.headquartersId eq selectedHeadquartersId}">selected</c:if>>
                                                                <c:out value="${headquarters.name}" />
                                                            </option>
                                                        </c:forEach>
                                                    </select>
                                                </div>
                                                <div class="col-12">
                                                    <div class="form-check mt-2">
                                                        <input class="form-check-input" type="checkbox" id="activeStatus" name="activeStatus" value="true"
                                                            <c:if test="${empty employee || employee.activeStatus ne false}">checked</c:if>>
                                                        <label class="form-check-label" for="activeStatus">
                                                            <fmt:message key="employee.form.active" />
                                                        </label>
                                                    </div>
                                                </div>
                                            </div>
                                        </div>
                                    </div>
                                </div>

                                <div class="d-flex flex-wrap gap-3 mt-4">
                                    <button type="submit" class="btn btn-brand px-4"><fmt:message key="profile.save" /></button>
                                    <a href="${pageContext.request.contextPath}/public/EmployeeServlet?action=list" class="btn btn-outline-brand px-4"><fmt:message key="action.cancel" /></a>
                                </div>
                            </form>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </main>

    <%@ include file="/common/footer.jsp"%>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js" integrity="sha384-YvpcrYf0tY3lHB60NNkmXc5s9fDVZLESaAA55NDzOxhy9GkcIdslK1eN7N6jIeHz" crossorigin="anonymous"></script>
    <script>
        (function () {
            'use strict';
            const forms = document.querySelectorAll('.needs-validation');
            Array.from(forms).forEach(form => {
                form.addEventListener('submit', event => {
                    if (!form.checkValidity()) {
                        event.preventDefault();
                        event.stopPropagation();
                    }
                    form.classList.add('was-validated');
                }, false);
            });
        })();

        const provincesData = [
        <c:forEach var="province" items="${provinces}" varStatus="provinceStatus">
            { id: ${province.provinceId}, name: "${fn:escapeXml(province.provinceName)}" }<c:if test="${!provinceStatus.last}">,</c:if>
        </c:forEach>
        ];

        const citiesByProvince = {
        <c:forEach var="entry" items="${citiesByProvince}" varStatus="status">
            "${entry.key}": [
                <c:forEach var="city" items="${entry.value}" varStatus="cityStatus">
                    { id: ${city.cityId}, name: "${fn:escapeXml(city.cityName)}" }<c:if test="${!cityStatus.last}">,</c:if>
                </c:forEach>
            ]<c:if test="${!status.last}">,</c:if>
        </c:forEach>
        };

        const initialSelectedProvinceId = <c:choose><c:when test="${not empty selectedProvinceId}">${selectedProvinceId}</c:when><c:otherwise>null</c:otherwise></c:choose>;
        const initialSelectedCityId = <c:choose><c:when test="${not empty selectedCityId}">${selectedCityId}</c:when><c:otherwise>null</c:otherwise></c:choose>;
        const initialSelectedHeadquartersId = <c:choose><c:when test="${not empty selectedHeadquartersId}">${selectedHeadquartersId}</c:when><c:otherwise>null</c:otherwise></c:choose>;

        const localeSelectMessage = "<fmt:message key='profile.locale.select' />";
        const emptyOptionLabel = "--";

        const provinceSelect = document.getElementById('provinceId');
        const citySelect = document.getElementById('cityId');
        const headquartersSelect = document.getElementById('headquartersId');
        const addressInput = document.getElementById('address');
        const phoneInput = document.getElementById('phone');

        let selectedProvinceId = initialSelectedProvinceId;
        let selectedCityId = initialSelectedCityId;

        function createOption(value, label) {
            const option = document.createElement('option');
            option.value = value !== null && value !== undefined && value !== '' ? String(value) : '';
            option.textContent = label;
            return option;
        }

        function populateProvinceOptions() {
            if (!provinceSelect) {
                return;
            }
            provinceSelect.innerHTML = '';
            provinceSelect.appendChild(createOption('', emptyOptionLabel));
            provincesData.forEach(({ id, name }) => {
                const option = createOption(id, name);
                if (selectedProvinceId !== null && id === selectedProvinceId) {
                    option.selected = true;
                }
                provinceSelect.appendChild(option);
            });
            provinceSelect.disabled = provincesData.length === 0;
        }

        function populateCityOptions() {
            if (!citySelect) {
                return;
            }
            const provinceValue = provinceSelect && provinceSelect.value ? provinceSelect.value : '';
            const provinceId = provinceValue ? parseInt(provinceValue, 10) : null;
            const cities = provinceId !== null ? (citiesByProvince[String(provinceId)] || []) : [];
            citySelect.innerHTML = '';
            if (cities.length === 0) {
                const label = provinceId === null ? emptyOptionLabel : localeSelectMessage;
                citySelect.appendChild(createOption('', label));
                citySelect.disabled = provinceId === null;
                return;
            }
            citySelect.appendChild(createOption('', emptyOptionLabel));
            cities.forEach(({ id, name }) => {
                const option = createOption(id, name);
                if (selectedCityId !== null && id === selectedCityId) {
                    option.selected = true;
                }
                citySelect.appendChild(option);
            });
            citySelect.disabled = false;
        }

        function buildAddressFromOption(option) {
            const parts = [];
            const street = option.dataset.street || '';
            const number = option.dataset.number || '';
            if (street) {
                parts.push(number ? `${street} ${number}` : street);
            }
            const locality = [];
            if (option.dataset.cityName) {
                locality.push(option.dataset.cityName);
            }
            if (option.dataset.provinceName) {
                locality.push(option.dataset.provinceName);
            }
            if (locality.length > 0) {
                parts.push(locality.join(', '));
            }
            return parts.join(' · ');
        }

        function applyHeadquartersSelection(option) {
            if (!option) {
                return;
            }
            if (option.dataset.provinceId) {
                selectedProvinceId = parseInt(option.dataset.provinceId, 10);
            }
            if (option.dataset.cityId) {
                selectedCityId = parseInt(option.dataset.cityId, 10);
            }
            populateProvinceOptions();
            populateCityOptions();

            if (addressInput) {
                const address = buildAddressFromOption(option);
                if (address) {
                    addressInput.value = address;
                }
            }
            if (phoneInput && option.dataset.phone && !phoneInput.value) {
                phoneInput.value = option.dataset.phone;
            }
        }

        populateProvinceOptions();
        populateCityOptions();

        if (provinceSelect) {
            provinceSelect.addEventListener('change', () => {
                selectedProvinceId = provinceSelect.value ? parseInt(provinceSelect.value, 10) : null;
                selectedCityId = null;
                populateCityOptions();
                if (headquartersSelect) {
                    headquartersSelect.value = '';
                }
            });
        }

        if (citySelect) {
            citySelect.addEventListener('change', () => {
                selectedCityId = citySelect.value ? parseInt(citySelect.value, 10) : null;
                if (headquartersSelect) {
                    headquartersSelect.value = '';
                }
            });
        }

        if (headquartersSelect) {
            headquartersSelect.addEventListener('change', () => {
                const option = headquartersSelect.options[headquartersSelect.selectedIndex];
                if (option) {
                    applyHeadquartersSelection(option);
                }
            });

            if (initialSelectedHeadquartersId !== null) {
                const option = headquartersSelect.querySelector(`option[value="${initialSelectedHeadquartersId}"]`);
                if (option) {
                    headquartersSelect.value = String(initialSelectedHeadquartersId);
                    applyHeadquartersSelection(option);
                }
            }
        }
    </script>
</body>
</html>
