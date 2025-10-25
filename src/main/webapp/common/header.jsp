<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>

<fmt:setLocale value="${sessionScope.locale}" scope="session" />
<fmt:setBundle basename="i18n.Messages" />

<nav class="navbar navbar-expand-lg navbar-light bg-white sticky-top py-3">
    <div class="container">
        <a class="navbar-brand d-flex align-items-center gap-2 text-decoration-none" href="${pageContext.request.contextPath}/public/index.jsp">
            <span class="badge bg-brand rounded-pill px-3 py-2 d-flex align-items-center gap-2 shadow-sm">
                <i class="bi bi-lightning-charge-fill"></i>
                RentExpress
            </span>
        </a>
        <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#rentexpressNavbar" aria-controls="rentexpressNavbar" aria-expanded="false" aria-label="Toggle navigation">
            <span class="navbar-toggler-icon"></span>
        </button>
        <div class="collapse navbar-collapse" id="rentexpressNavbar">
            <ul class="navbar-nav ms-auto align-items-lg-center gap-lg-3">
                <li class="nav-item">
                    <a class="nav-link" href="${pageContext.request.contextPath}/public/index.jsp#hero">
                        <fmt:message key="nav.inicio" />
                    </a>
                </li>
                <li class="nav-item">
                    <a class="nav-link" href="${pageContext.request.contextPath}/public/index.jsp#destacados">
                        <fmt:message key="nav.vehiculos" />
                    </a>
                </li>
                <li class="nav-item">
                    <a class="nav-link" href="${pageContext.request.contextPath}/public/index.jsp#faq">
                        <fmt:message key="nav.faq" />
                    </a>
                </li>
                <li class="nav-item dropdown">
                    <a class="nav-link dropdown-toggle d-flex align-items-center gap-2" href="#" id="dropdownIdioma" role="button" data-bs-toggle="dropdown" aria-expanded="false">
                        <i class="bi bi-translate"></i>
                        <c:out value="${sessionScope.locale.language != null ? sessionScope.locale.language.toUpperCase() : 'ES'}" />
                    </a>
                    <div class="dropdown-menu dropdown-menu-end p-3 shadow-sm" aria-labelledby="dropdownIdioma">
                        <form action="${pageContext.request.contextPath}/public/EmployeeServlet" method="get" class="d-flex flex-column gap-2">
                            <input type="hidden" name="action" value="changeLocale" />
                            <button type="submit" name="language" value="es" class="btn btn-outline-brand d-flex align-items-center justify-content-between">
                                <span class="d-flex align-items-center gap-2">
                                    <span class="fi fi-es language-flag"></span>
                                    Español
                                </span>
                                <c:if test="${sessionScope.locale.language == 'es'}">
                                    <i class="bi bi-check-circle-fill text-brand"></i>
                                </c:if>
                            </button>
                            <button type="submit" name="language" value="en" class="btn btn-outline-brand d-flex align-items-center justify-content-between">
                                <span class="d-flex align-items-center gap-2">
                                    <span class="fi fi-gb language-flag"></span>
                                    English
                                </span>
                                <c:if test="${sessionScope.locale.language == 'en'}">
                                    <i class="bi bi-check-circle-fill text-brand"></i>
                                </c:if>
                            </button>
                            <button type="submit" name="language" value="fr" class="btn btn-outline-brand d-flex align-items-center justify-content-between">
                                <span class="d-flex align-items-center gap-2">
                                    <span class="fi fi-fr language-flag"></span>
                                    Français
                                </span>
                                <c:if test="${sessionScope.locale.language == 'fr'}">
                                    <i class="bi bi-check-circle-fill text-brand"></i>
                                </c:if>
                            </button>
                        </form>
                    </div>
                </li>
                <c:choose>
                    <c:when test="${not empty sessionScope.employee}">
                        <c:set var="currentEmployeeId" value="${not empty sessionScope.employee.id ? sessionScope.employee.id : (not empty sessionScope.employee.employeeId ? sessionScope.employee.employeeId : sessionScope.employee.idEmployee)}" />
                        <li class="nav-item dropdown">
                            <a class="nav-link dropdown-toggle d-flex align-items-center gap-2" href="#" id="dropdownCuenta" role="button" data-bs-toggle="dropdown" aria-expanded="false">
                                <i class="bi bi-person-circle fs-5"></i>
                                <span class="fw-semibold text-dark"><c:out value="${sessionScope.employee.employeeName}" /></span>
                            </a>
                            <ul class="dropdown-menu dropdown-menu-end shadow-sm" aria-labelledby="dropdownCuenta">
                                <li>
                                    <a class="dropdown-item" href="${pageContext.request.contextPath}/public/EmployeeServlet?action=detail&id=${currentEmployeeId}">
                                        <i class="bi bi-person-lines-fill me-2"></i>
                                        <fmt:message key="usuario.detail.button" />
                                    </a>
                                </li>
                                <li>
                                    <a class="dropdown-item" href="${pageContext.request.contextPath}/public/EmployeeServlet?action=list">
                                        <i class="bi bi-people-fill me-2"></i>
                                        <fmt:message key="usuario.list.title" />
                                    </a>
                                </li>
                                <li><hr class="dropdown-divider" /></li>
                                <li>
                                    <a class="dropdown-item text-danger" href="${pageContext.request.contextPath}/public/EmployeeServlet?action=logout">
                                        <i class="bi bi-box-arrow-right me-2"></i>
                                        <fmt:message key="header.logout" />
                                    </a>
                                </li>
                            </ul>
                        </li>
                    </c:when>
                    <c:otherwise>
                        <li class="nav-item d-flex gap-2 mt-3 mt-lg-0">
                            <a class="btn btn-outline-brand px-3" href="${pageContext.request.contextPath}/public/usuario/login.jsp">
                                <fmt:message key="header.login" />
                            </a>
                            <a class="btn btn-brand px-3" href="${pageContext.request.contextPath}/public/EmployeeServlet?action=create">
                                <fmt:message key="usuario.create.title" />
                            </a>
                        </li>
                    </c:otherwise>
                </c:choose>
            </ul>
        </div>
    </div>
</nav>
