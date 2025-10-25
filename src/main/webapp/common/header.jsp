<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>

<fmt:setLocale value="${sessionScope.locale}" scope="session" />
<fmt:setBundle basename="i18n.Messages" />

<div class="header">
	<h1>RentExpress</h1>

	<c:if test="${not empty sessionScope.usuario}">
		<span><fmt:message key="header.welcome" /> <c:out
				value="${sessionScope.usuario.nombreUsuario}" /></span> |

        <form
			action="${pageContext.request.contextPath}/public/UsuarioServlet"
			method="get" style="display: inline;">
			<input type="hidden" name="action" value="changeLocale" /> <select
				name="lenguage" onchange="this.form.submit()">
				<option value="es"
					${sessionScope.locale.language == 'es' ? 'selected' : ''}>Español</option>
				<option value="en"
					${sessionScope.locale.language == 'en' ? 'selected' : ''}>English</option>
				<option value="fr"
					${sessionScope.locale.language == 'fr' ? 'selected' : ''}>Français</option>
			</select>
		</form> |

        <a
			href="${pageContext.request.contextPath}/public/UsuarioServlet?action=logout">
			<fmt:message key="header.logout" />
		</a>
	</c:if>

	<c:if test="${empty sessionScope.usuario}">
		<a href="${pageContext.request.contextPath}/public/usuario/login.jsp">
			<fmt:message key="header.login" />
		</a>
	</c:if>
</div>
<hr />
