<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>

<fmt:setLocale value="${sessionScope.locale}" scope="session" />
<fmt:setBundle basename="i18n.Messages" />

<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title><c:choose>
		<c:when test="${empty usuario}">
			<fmt:message key="usuario.create.title" />
		</c:when>
		<c:otherwise>
			<fmt:message key="usuario.edit.title" />
		</c:otherwise>
	</c:choose></title>

<link rel="stylesheet"
	href="${pageContext.request.contextPath}/css/main.css">
<script src="${pageContext.request.contextPath}/js/validaciones.js"></script>
</head>
<body>
	<%@ include file="/common/header.jsp"%>

	<h2>
		<c:choose>
			<c:when test="${empty usuario}">
				<fmt:message key="usuario.create.title" />
			</c:when>
			<c:otherwise>
				<fmt:message key="usuario.edit.title" />
			</c:otherwise>
		</c:choose>
	</h2>

	<form action="${pageContext.request.contextPath}/public/UsuarioServlet"
		method="post" onsubmit="return validarUsuarioForm()">

		<input type="hidden" name="action"
			value="${empty usuario ? 'save' : 'update'}" />

		<c:if test="${not empty usuario}">
			<input type="hidden" name="id" value="${usuario.id}" />
		</c:if>

		<label for="nombreUsuario"><fmt:message
				key="usuario.detail.name" /></label><br /> <input type="text"
			id="nombreUsuario" name="nombreUsuario"
			value="${usuario.nombreUsuario}" required /><br /> <br /> <label
			for="email"><fmt:message key="usuario.detail.email" /></label><br />
		<input type="email" id="email" name="email" value="${usuario.email}"
			required /><br /> <br />

		<button type="submit">
			<fmt:message key="action.save" />
		</button>
	</form>

	<p>
		<a
			href="${pageContext.request.contextPath}/public/UsuarioServlet?action=list">
			<fmt:message key="back.index" />
		</a>
	</p>
</body>
</html>
