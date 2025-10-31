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
	for (Cookie c : cookies) {
		if ("rememberUser".equals(c.getName())) {
	rememberedUser = c.getValue();
		}
	}
}
%>

<h2>
	<fmt:message key="login.title" />
</h2>

<form action="${pageContext.request.contextPath}/login"
        method="post">
	<input type="hidden" name="action" value="login" /> <label
		for="username"><fmt:message key="login.username" /></label><br /> <input
		type="text" id="username" name="username" value="<%=rememberedUser%>"
		required /><br /> <br /> <label for="password"><fmt:message
			key="login.password" /></label><br /> <input type="password" id="password"
		name="password" required /><br /> <br /> <label> <input
		type="checkbox" name="remember" value="yes" /> Recordar usuario
	</label><br /> <br />

	<button type="submit">
		<fmt:message key="login.button" />
	</button>
</form>

<c:if test="${not empty error}">
	<p style="color: red;">
		<c:out value="${error}" />
	</p>
</c:if>
