<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>

<fmt:setLocale value="${sessionScope.locale}" scope="session" />
<fmt:setBundle basename="i18n.Messages" />

<h2>
	<fmt:message
		key="${empty usuario ? 'usuario.create.title' : 'usuario.edit.title'}" />
</h2>

<form action="${pageContext.request.contextPath}/public/UsuarioServlet"
	method="post">
	<input type="hidden" name="action"
		value="${empty usuario ? 'save' : 'update'}" />
	<c:if test="${not empty usuario}">
		<input type="hidden" name="id" value="${usuario.id}" />
	</c:if>

	<label><fmt:message key="usuario.detail.name" />:</label> <input
		type="text" name="nombreUsuario" value="${usuario.nombreUsuario}"
		required /><br /> <br /> <label><fmt:message
			key="usuario.detail.email" />:</label> <input type="email" name="email"
		value="${usuario.email}" required /><br /> <br />

	<button type="submit">
		<fmt:message key="action.save" />
	</button>
</form>
