<%@ page isErrorPage="true" language="java"
	contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>

<fmt:setLocale value="${sessionScope.locale}" scope="session" />
<fmt:setBundle basename="i18n.Messages" />

<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title><fmt:message key="error.title" /></title>
<link rel="stylesheet"
	href="${pageContext.request.contextPath}/css/main.css">
</head>
<body>
	<%@ include file="/common/header.jsp"%>

	<fmt:setLocale value="${sessionScope.locale}" scope="session" />
	<fmt:setBundle basename="i18n.Messages" />

	<h2>
		<fmt:message key="error.title" />
	</h2>
	<p>
		<fmt:message key="error.message" />
		:
	</p>

	<p style="color: red;">
		<c:out value="${pageContext.exception.message}" />
	</p>

	<form action="${pageContext.request.contextPath}/public/UsuarioServlet"
		method="get">
		<input type="hidden" name="action" value="index" />
		<button type="submit">
			<fmt:message key="error.back" />
		</button>
	</form>
</body>
</html>
