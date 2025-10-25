<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<fmt:setLocale value="${sessionScope.locale}" scope="session" />
<fmt:setBundle basename="i18n.Messages" />
<%
String rememberedUser = "";
Cookie[] cookies = request.getCookies();
if (cookies != null) {
	for (Cookie cookie : cookies) {
		if ("rememberedUser".equals(cookie.getName())) {
	rememberedUser = cookie.getValue();
	break;
		}
	}
}
%>

<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title><fmt:message key="login.title" /></title>
<link rel="stylesheet"
	href="${pageContext.request.contextPath}/css/main.css">
<script src="${pageContext.request.contextPath}/js/validaciones.js"></script>
</head>
<body>
	<%@ include file="/common/header.jsp"%>

	<h2>
		<fmt:message key="login.title" />
	</h2>

	<form action="${pageContext.request.contextPath}/public/UsuarioServlet"
		method="post" onsubmit="return validarLogin()">
		<input type="hidden" name="action" value="login" /> <label
			for="username"><fmt:message key="login.username" />:</label> <input
			type="text" id="username" name="username"
			value="<c:out value='${rememberedUser}'/>" required /> <br /> <br />
		<label for="password"><fmt:message key="login.password" />:</label> <input
			type="password" id="password" name="password" required /> <br /> <br />
		<input type="checkbox" id="rememberMe" name="rememberMe"
			<c:if test="${not empty rememberedUser}"> checked </c:if> /> <label
			for="rememberMe"><fmt:message key="login.rememberMe" /></label> <br />
		<br />

		<button type="submit">
			<fmt:message key="login.button" />
		</button>
	</form>

	<c:if test="${not empty error}">
		<p style="color: red;">
			<c:out value="${error}" />
		</p>
	</c:if>

	<p>
		<a href="${pageContext.request.contextPath}/public/index.jsp"> <fmt:message
				key="back.index" />
		</a>
	</p>
</body>
</html>
