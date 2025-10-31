<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>

<fmt:setLocale value="${sessionScope.locale}" scope="session" />
<fmt:setBundle basename="i18n.Messages" />
<c:set var="ctx" value="${pageContext.request.contextPath}" />

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
                                href="${ctx}/public/users/detail?id=${u.id}">
                                        <fmt:message key="action.view" />
                        </a> | <a
                                href="${ctx}/public/users?action=edit&amp;userId=${u.id}">
                                        <fmt:message key="action.edit" />
                        </a> | <a
                                href="${ctx}/public/users?action=delete&amp;userId=${u.id}"
                                onclick="return confirm('¿Seguro que quieres eliminar?')"> <fmt:message
                                                key="action.delete" />
                        </a></td>
		</tr>
	</c:forEach>
</table>

<br />
<a
        href="${ctx}/public/users?action=create">
        <fmt:message key="action.new" />
</a>
