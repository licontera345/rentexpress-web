<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head><title>Perfil</title></head>
<body>
  <h2>Mi Perfil</h2>

  <c:if test="${param.success eq '1'}">
      <p style="color:green;">Perfil actualizado correctamente.</p>
  </c:if>

  <c:if test="${not empty errorMsg}">
      <p style="color:red;">${errorMsg}</p>
  </c:if>

  <form method="post" enctype="multipart/form-data">
      <label>Nombre:</label><br>
      <input type="text" name="name" value="${account.name}" required><br>

      <label>Teléfono:</label><br>
      <input type="text" name="phone" value="${account.phone}"><br>

      <label>Email:</label><br>
      <input type="email" name="email" value="${account.email}" required><br>

      <label>Nueva Contraseña (opcional):</label><br>
      <input type="password" name="password"><br>

      <label>Foto de Perfil:</label><br>
      <input type="file" name="avatar" accept="image/*"><br><br>

      <button type="submit">Actualizar</button>
  </form>

  <hr>
  <h3>Vista previa del avatar</h3>
  <img src="${pageContext.request.contextPath}/media/${role == 'CLIENT' ? 'user' : 'employee'}/${account.id}" width="120">

  <br><br>
  <a href="${pageContext.request.contextPath}/private/user">Volver</a>
</body>
</html>
