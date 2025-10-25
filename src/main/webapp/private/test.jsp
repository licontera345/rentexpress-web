<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>

<fmt:setLocale value="${sessionScope.locale}" scope="session" />
<fmt:setBundle basename="i18n.Messages" />

<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title><fmt:message key="private.test.title" /></title>
</head>
<body>
	<%@ include file="../common/header.jsp"%>

	<h2>
		<fmt:message key="private.test.title" />
	</h2>
	<p>
		<fmt:message key="private.test.content" />
	</p>

        <form action="${pageContext.request.contextPath}/public/EmployeeServlet"
                method="get">
		<input type="hidden" name="action" value="index" />
		<button type="submit">
			<fmt:message key="back.index" />
		</button>
	</form>
</body>
</html>
