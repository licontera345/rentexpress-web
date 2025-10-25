<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>

<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>RentExpress</title>
</head>
<body>
	<%@ include file="/common/header.jsp"%>
	<fmt:setLocale value="${sessionScope.locale}" scope="session" />
	<fmt:setBundle basename="i18n.Messages" />

	<h2>
		<fmt:message key="welcomeMessage" />
	</h2>

	<h3>Menú principal</h3>
	<ul>

		<li><a
			href="${pageContext.request.contextPath}/public/UsuarioServlet?action=detail&id=${sessionScope.usuario.id}">
				<fmt:message key="usuario.detail.button" />
		</a></li>

		<li><a
			href="${pageContext.request.contextPath}/public/UsuarioServlet?action=list">
				<fmt:message key="usuario.list.title" />
		</a></li>

		<li><a
			href="${pageContext.request.contextPath}/public/UsuarioServlet?action=create">
				<fmt:message key="usuario.create.title" />
		</a></li>
	</ul>

	<c:if test="${not empty sessionScope.usuario}">
		<p>
			<fmt:message key="header.welcome" />
			<strong><c:out value="${sessionScope.usuario.nombreUsuario}" /></strong>
		</p>
	</c:if>

	<c:if test="${empty sessionScope.usuario}">
		<p>
			<fmt:message key="login.please" />
		</p>
	</c:if>
</body>
</html>
