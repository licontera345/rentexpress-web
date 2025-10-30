<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>

<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title><fmt:message key="usuario.detail.title" /></title>
</head>
<body>
	<%@ include file="../../common/header.jsp"%>

	<fmt:setLocale value="${sessionScope.locale}" scope="session" />
	<fmt:setBundle basename="i18n.Messages" />

	<h2>
		<fmt:message key="usuario.detail.title" />
	</h2>

	<c:if test="${not empty usuario}">
		<p>
			<strong><fmt:message key="usuario.detail.id" />:</strong>
			<c:out value="${usuario.id}" />
		</p>

		<p>
			<strong><fmt:message key="usuario.detail.name" />:</strong>
			<c:out value="${usuario.nombreUsuario}" />
		</p>

		<p>
			<strong><fmt:message key="usuario.detail.email" />:</strong>
			<c:out value="${usuario.email}" />
		</p>

		<c:if test="${not empty usuario.imagenes}">
			<p>
				<strong><fmt:message key="usuario.detail.image" />:</strong>
			</p>
			<img
				src="${pageContext.request.contextPath}/DownloadImageServlet?id=${usuario.id}"
				alt="Imagen de usuario" style="max-width: 200px;" />
		</c:if>
	</c:if>

	<c:if test="${empty usuario}">
		<p style="color: red;">
			<fmt:message key="usuario.detail.notfound" />
		</p>
	</c:if>

	<!-- Botón Volver al inicio -->
	<form action="${pageContext.request.contextPath}/public/UsuarioServlet"
		method="get">
		<input type="hidden" name="action" value="index" />
		<button type="submit">
			<fmt:message key="back.index" />
		</button>
	</form>
</body>
</html>
