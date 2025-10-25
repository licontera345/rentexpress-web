<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>

<fmt:setLocale value="${sessionScope.locale}" scope="session" />
<fmt:setBundle basename="i18n.Messages" />

<!DOCTYPE html>
<html lang="${sessionScope.locale.language != null ? sessionScope.locale.language : 'es'}">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title><fmt:message key="employee.detail.title" /></title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet" integrity="sha384-QWTKZyjpPEjISv5WaRU9OFeRpok6YctnYmDr5pNlyT2bRjXh0JMhjY6hW+ALEwIH" crossorigin="anonymous">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.css" rel="stylesheet">
    <link href="https://cdn.jsdelivr.net/npm/flag-icons@6.6.6/css/flag-icons.min.css" rel="stylesheet">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/main.css">
</head>
<body class="bg-light d-flex flex-column min-vh-100">
    <%@ include file="/common/header.jsp"%>

    <main class="flex-grow-1 py-5">
        <div class="container">
            <c:if test="${not empty employee}">
                <c:set var="employeeId" value="${not empty employee.id ? employee.id : (not empty employee.employeeId ? employee.employeeId : employee.idEmployee)}" />
                <div class="row g-4">
                    <div class="col-lg-4">
                        <div class="card card-common h-100">
                            <div class="card-body text-center">
                                <c:set var="PH_SVG" value="data:image/svg+xml;utf8,<svg xmlns='http://www.w3.org/2000/svg' width='240' height='240' viewBox='0 0 240 240'><rect width='100%' height='100%' fill='%23f2f2f2'/><circle cx='120' cy='90' r='44' fill='%23c2c2c2'/><rect x='40' y='150' width='160' height='60' rx='30' fill='%23c2c2c2'/></svg>" />
                                <div class="avatar-wrapper mb-3">
                                    <c:choose>
                                        <c:when test="${hasImage}">
                                            <img alt="Foto de empleado" loading="lazy" src="${pageContext.request.contextPath}/DownloadImageServlet?id=${employeeId}">
                                        </c:when>
                                        <c:otherwise>
                                            <img alt="Placeholder" loading="lazy" src="${PH_SVG}">
                                        </c:otherwise>
                                    </c:choose>
                                    <form action="${pageContext.request.contextPath}/UploadImageServlet" method="post" enctype="multipart/form-data" class="avatar-edit-btn" title="<fmt:message key='profile.avatar.change' />">
                                        <input type="hidden" name="employeeId" value="${employeeId}" />
                                        <label for="avatarFile" class="text-white"><i class="bi bi-camera-fill"></i></label>
                                        <input type="file" id="avatarFile" name="imagen" accept=".jpg,.jpeg,.png" class="d-none" onchange="this.form.submit()" />
                                    </form>
                                </div>
                                <h4 class="fw-semibold mb-1"><c:out value="${employee.employeeName}" /></h4>
                                <p class="text-muted mb-3"><c:out value="${employee.email}" /></p>
                                <div class="d-grid gap-2">
                                    <a href="${pageContext.request.contextPath}/public/EmployeeServlet?action=edit&id=${employeeId}" class="btn btn-outline-brand">
                                        <i class="bi bi-pencil-square me-2"></i>
                                        <fmt:message key="action.edit" />
                                    </a>
                                    <a href="${pageContext.request.contextPath}/public/EmployeeServlet?action=list" class="btn btn-outline-secondary">
                                        <i class="bi bi-arrow-left-circle me-2"></i>
                                        <fmt:message key="back.index" />
                                    </a>
                                </div>
                            </div>
                        </div>
                    </div>

                    <div class="col-lg-8">
                        <div class="card card-common mb-4">
                            <div class="card-body">
                                <h3 class="fw-bold mb-3"><fmt:message key="employee.detail.title" /></h3>
                                <div class="row g-3">
                                    <div class="col-md-6">
                                        <p class="text-muted mb-1"><fmt:message key="employee.detail.id" /></p>
                                        <p class="fw-semibold mb-0">${employeeId}</p>
                                    </div>
                                    <div class="col-md-6">
                                        <p class="text-muted mb-1"><fmt:message key="employee.detail.name" /></p>
                                        <p class="fw-semibold mb-0"><c:out value="${employee.employeeName}" /></p>
                                    </div>
                                    <div class="col-md-6">
                                        <p class="text-muted mb-1"><fmt:message key="employee.detail.email" /></p>
                                        <p class="fw-semibold mb-0"><c:out value="${employee.email}" /></p>
                                    </div>
                                    <div class="col-md-6">
                                        <p class="text-muted mb-1"><fmt:message key="employee.detail.phone" /></p>
                                        <p class="fw-semibold mb-0">
                                            <c:choose>
                                                <c:when test="${not empty employee.phone}"><c:out value="${employee.phone}" /></c:when>
                                                <c:otherwise>-</c:otherwise>
                                            </c:choose>
                                        </p>
                                    </div>
                                    <div class="col-md-6">
                                        <p class="text-muted mb-1"><fmt:message key="employee.detail.role" /></p>
                                        <p class="fw-semibold mb-0">
                                            <c:choose>
                                                <c:when test="${employee.role != null}"><c:out value="${employee.role.roleName}" /></c:when>
                                                <c:otherwise>-</c:otherwise>
                                            </c:choose>
                                        </p>
                                    </div>
                                    <div class="col-md-6">
                                        <p class="text-muted mb-1"><fmt:message key="employee.detail.status" /></p>
                                        <span class="badge ${employee.activeStatus ? 'bg-success-subtle text-success' : 'bg-danger-subtle text-danger'} fw-semibold px-3 py-2">
                                            <fmt:message key="${employee.activeStatus ? 'employee.status.active' : 'employee.status.inactive'}" />
                                        </span>
                                    </div>
                                    <c:if test="${employee.headquarters != null}">
                                        <div class="col-md-6">
                                            <p class="text-muted mb-1"><fmt:message key="employee.detail.headquarters" /></p>
                                            <p class="fw-semibold mb-1"><c:out value="${employee.headquarters.name}" /></p>
                                            <c:if test="${employee.headquarters.address != null}">
                                                <p class="text-muted small mb-1">
                                                    <c:out value="${employee.headquarters.address.street}" />
                                                    <c:if test="${not empty employee.headquarters.address.number}">
                                                        <span> <c:out value="${employee.headquarters.address.number}" /></span>
                                                    </c:if>
                                                </p>
                                            </c:if>
                                            <c:if test="${employee.headquarters.city != null || employee.headquarters.province != null}">
                                                <p class="text-muted small mb-0">
                                                    <c:if test="${employee.headquarters.city != null}">
                                                        <c:out value="${employee.headquarters.city.cityName}" />
                                                    </c:if>
                                                    <c:if test="${employee.headquarters.city != null && employee.headquarters.province != null}"> · </c:if>
                                                    <c:if test="${employee.headquarters.province != null}">
                                                        <c:out value="${employee.headquarters.province.provinceName}" />
                                                    </c:if>
                                                </p>
                                            </c:if>
                                        </div>
                                    </c:if>
                                </div>
                            </div>
                        </div>

                        <div class="card card-common mb-4">
                            <div class="card-body">
                                <div class="d-flex justify-content-between align-items-center mb-3">
                                    <h4 class="fw-bold mb-0"><fmt:message key="reservations.active.title" /></h4>
                                    <span class="badge bg-brand">0</span>
                                </div>
                                <div class="reservation-card">
                                    <p class="text-muted mb-0"><fmt:message key="reservations.empty.active" /></p>
                                </div>
                            </div>
                        </div>

                        <div class="card card-common">
                            <div class="card-body">
                                <div class="d-flex justify-content-between align-items-center mb-3">
                                    <h4 class="fw-bold mb-0"><fmt:message key="reservations.history.title" /></h4>
                                    <span class="badge bg-secondary">0</span>
                                </div>
                                <div class="reservation-card inactive">
                                    <p class="text-muted mb-0"><fmt:message key="reservations.empty.history" /></p>
                                </div>
                            </div>
                        </div>

                        <c:if test="${not empty image}">
                            <div class="card card-common mt-4">
                                <div class="card-body">
                                    <h5 class="fw-bold mb-3"><fmt:message key="employee.detail.images" /></h5>
                                    <div class="row g-3">
                                        <div class="col-6 col-md-4">
                                            <div class="ratio ratio-1x1 rounded-4 overflow-hidden shadow-sm">
                                                <img alt="miniatura" loading="lazy" src="${pageContext.request.contextPath}/DownloadImageServlet?id=${employeeId}" class="w-100 h-100 object-fit-cover">
                                            </div>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </c:if>
                    </div>
                </div>
            </c:if>

            <c:if test="${empty employee}">
                <div class="alert alert-danger d-flex align-items-center" role="alert">
                    <i class="bi bi-exclamation-octagon-fill me-3 fs-4"></i>
                    <div><fmt:message key="employee.detail.notfound" /></div>
                </div>
            </c:if>
        </div>
    </main>

    <%@ include file="/common/footer.jsp"%>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js" integrity="sha384-YvpcrYf0tY3lHB60NNkmXc5s9fDVZLESaAA55NDzOxhy9GkcIdslK1eN7N6jIeHz" crossorigin="anonymous"></script>
</body>
</html>
