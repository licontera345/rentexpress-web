<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>

<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title><fmt:message key="usuario.detail.title" /></title>
</head>
<body>
	<%@ include file="/common/header.jsp"%>

	<fmt:setLocale value="${sessionScope.locale}" scope="session" />
	<fmt:setBundle basename="i18n.Messages" />

	<!-- Placeholder inline para NO hacer ninguna petición al servidor -->
	<c:set var="PH_SVG"
		value="data:image/svg+xml;utf8,
<svg xmlns='http://www.w3.org/2000/svg' width='240' height='240' viewBox='0 0 240 240'>
  <rect width='100%' height='100%' fill='%23f2f2f2'/>
  <circle cx='120' cy='90' r='44' fill='%23c2c2c2'/>
  <rect x='40' y='150' width='160' height='60' rx='30' fill='%23c2c2c2'/>
</svg>" />

	<c:if test="${not empty usuario}">
		<p>
			<strong><fmt:message key="usuario.detail.id" />:</strong>
			${usuario.id}
		</p>
		<p>
			<strong><fmt:message key="usuario.detail.name" />:</strong>
			<c:out value="${usuario.nombreUsuario}" />
		</p>
		<p>
			<strong><fmt:message key="usuario.detail.email" />:</strong>
			<c:out value="${usuario.email}" />
		</p>

		<div style="margin: 12px 0;">
			<c:choose>
				<c:when test="${tieneImagen}">
					<!-- Una sola llamada al servlet; si 204/404, el navegador NO reintenta -->
					<img alt="Foto de usuario" loading="lazy"
						src="${pageContext.request.contextPath}/DownloadImageServlet?id=${usuario.id}"
						style="max-width: 240px; max-height: 240px; object-fit: cover; border-radius: 8px; border: 1px solid #ddd; padding: 2px;" />
				</c:when>
				<c:otherwise>
					<!-- Sin imagen → data URI (no hay peticiones HTTP) -->
					<img alt="Placeholder" loading="lazy" src="${PH_SVG}"
						style="max-width: 240px; max-height: 240px; object-fit: cover; border-radius: 8px; border: 1px solid #ddd; padding: 2px;" />
				</c:otherwise>
			</c:choose>
		</div>

		<!-- Subir / cambiar imagen -->
		<form action="${pageContext.request.contextPath}/UploadImageServlet"
			method="post" enctype="multipart/form-data" style="margin-top: 10px;">
			<input type="hidden" name="idUsuario" value="${usuario.id}" /> <input
				type="file" name="imagen" accept=".jpg,.jpeg,.png" required />
			<button type="submit">
				<fmt:message key="usuario.detail.upload" />
			</button>
		</form>

		<c:if test="${not empty imagenes}">
			<h3>
				<fmt:message key="usuario.detail.images" />
			</h3>
			<div style="display: flex; gap: 12px; flex-wrap: wrap;">
				<c:forEach var="img" items="${imagenes}">
					<div
						style="border: 1px solid #ddd; border-radius: 8px; padding: 8px; width: 176px; text-align: center;">
						<img alt="miniatura" loading="lazy"
							src="${pageContext.request.contextPath}/DownloadImageServlet?id=${usuario.id}"
							style="width: 160px; height: 160px; object-fit: cover; border-radius: 6px;" />
					</div>
				</c:forEach>
			</div>
		</c:if>
	</c:if>

	<c:if test="${empty usuario}">
		<p style="color: red;">
			<fmt:message key="usuario.detail.notfound" />
		</p>
	</c:if>

	<form action="${pageContext.request.contextPath}/public/UsuarioServlet"
		method="get">
		<input type="hidden" name="action" value="index" />
		<button type="submit">
			<fmt:message key="back.index" />
		</button>
	</form>
</body>
</html>
