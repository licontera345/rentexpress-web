<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<c:set var="_paginationCurrentPage"
       value="${empty requestScope.paginationCurrentPage ? 1 : requestScope.paginationCurrentPage}" />
<c:set var="_paginationTotalPages"
       value="${empty requestScope.paginationTotalPages ? 1 : requestScope.paginationTotalPages}" />
<c:set var="_paginationBasePath"
       value="${not empty requestScope.paginationBasePath ? requestScope.paginationBasePath : pageContext.request.servletPath}" />
<c:url value="${_paginationBasePath}" var="_paginationPreviousUrl">
    <c:forEach var="entry" items="${paramValues}">
        <c:if test="${entry.key ne 'page'}">
            <c:forEach var="value" items="${entry.value}">
                <c:param name="${entry.key}" value="${value}" />
            </c:forEach>
        </c:if>
    </c:forEach>
    <c:param name="page" value="${_paginationCurrentPage - 1}" />
</c:url>
<c:url value="${_paginationBasePath}" var="_paginationNextUrl">
    <c:forEach var="entry" items="${paramValues}">
        <c:if test="${entry.key ne 'page'}">
            <c:forEach var="value" items="${entry.value}">
                <c:param name="${entry.key}" value="${value}" />
            </c:forEach>
        </c:if>
    </c:forEach>
    <c:param name="page" value="${_paginationCurrentPage + 1}" />
</c:url>
<fmt:message var="paginationStatusMessage" key="pagination.status">
    <fmt:param value="${_paginationCurrentPage}" />
    <fmt:param value="${_paginationTotalPages}" />
</fmt:message>
<div class="d-flex justify-content-center gap-2 my-3">
    <c:if test="${_paginationCurrentPage > 1}">
        <a class="btn btn-outline-secondary" href="${_paginationPreviousUrl}">←</a>
    </c:if>
    <span>${paginationStatusMessage}</span>
    <c:if test="${_paginationCurrentPage < _paginationTotalPages}">
        <a class="btn btn-outline-secondary" href="${_paginationNextUrl}">→</a>
    </c:if>
</div>
