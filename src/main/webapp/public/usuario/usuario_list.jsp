<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>

<fmt:setLocale value="${sessionScope.locale}" scope="session" />
<fmt:setBundle basename="i18n.Messages" />

<h2>
	<fmt:message key="usuario.list.title" />
</h2>

<table border="1">
	<tr>
		<th>ID</th>
		<th><fmt:message key="usuario.detail.name" /></th>
		<th><fmt:message key="usuario.detail.email" /></th>
		<th><fmt:message key="actions" /></th>
	</tr>
	<c:forEach var="u" items="${usuarios}">
		<tr>
			<td><c:out value="${u.id}" /></td>
			<td><c:out value="${u.nombreUsuario}" /></td>
			<td><c:out value="${u.email}" /></td>
			<td><a
				href="${pageContext.request.contextPath}/public/UsuarioServlet?action=detail&id=${u.id}">
					<fmt:message key="action.view" />
			</a> | <a
				href="${pageContext.request.contextPath}/public/UsuarioServlet?action=edit&id=${u.id}">
					<fmt:message key="action.edit" />
			</a> | <a
				href="${pageContext.request.contextPath}/public/UsuarioServlet?action=delete&id=${u.id}"
				onclick="return confirm('¿Seguro que quieres eliminar?')"> <fmt:message
						key="action.delete" />
			</a></td>
		</tr>
	</c:forEach>
</table>

<br />
<a
	href="${pageContext.request.contextPath}/public/UsuarioServlet?action=create">
	<fmt:message key="action.new" />
</a>
