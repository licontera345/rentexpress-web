# RentExpress Web Middleware Guidelines

Antes de implementar nuevos servlets, servicios u otros componentes dentro del proyecto **rentexpress-web**, revisa primero el contenido del middleware empaquetado en [`src/main/webapp/WEB-INF/lib/RentExpres.jar`](src/main/webapp/WEB-INF/lib/RentExpres.jar).

1. Extrae o lista los recursos existentes (por ejemplo con `jar tf src/main/webapp/WEB-INF/lib/RentExpres.jar`).
2. Comprueba si ya existen los *services*, *DAOs* u otras clases que necesitas reutilizar.
3. Si el JAR expone la clase en formato `.java`, abre el archivo y revisa cómo funcionan exactamente los métodos implicados.
4. Verifica que el método que deseas usar realmente existe en el middleware; si no existe, detén la implementación hasta definir una alternativa con el equipo.
5. Si el método existe, replica fielmente su comportamiento en tu servlet o componente para mantener la coherencia funcional.

Seguir este flujo asegura que las funcionalidades nuevas aprovechen la base ya implementada en el middleware y que la información mostrada en la aplicación esté alineada con los servicios disponibles.


A tener en cuenta:

El objetivo es crear una aplicación web para la reserva y alquiler de coches. La aplicación implementará funcionalidades clave como CRUD de entidades, gestión de perfiles de usuario, internacionalización, seguridad (división pública/privada), validación de formularios, gestión de sesiones y la lógica de negocio específica para las reservas.

Tecnologías Utilizadas:

Frontend: JSP, HTML, CSS, Bootstrap

Backend: Java Servlets (Jakarta EE)

Base de datos: MySQL

Servidor: Apache Tomcat 10

Build: Maven

Lógica de Negocio: Reserva y Alquiler
Esta es la acción principal de la aplicación.

Diferencia Reserva vs. Alquiler:

Reserva: El usuario paga por adelantado para apartar un coche en una fecha futura.

Alquiler: Cuando llega la fecha de inicio de la reserva, esta se convierte automáticamente en un "Alquiler" activo hasta la fecha de devolución.

Flujo obligatorio: Siempre se debe pasar por una reserva antes de un alquiler, incluso si es para el mismo día.

Gestión de Estado y Fechas:

El usuario selecciona un coche y un rango de fechas (inicio y fin).

El sistema comprueba la disponibilidad del coche en esas fechas.

Si está disponible, se crea la reserva y el estado del coche cambia a Reservado.

Cuando llega la fecha de inicio de la reserva, el estado cambia automáticamente a Alquilado.

Cuando llega la fecha de fin del alquiler, el estado vuelve a Disponible.

Reglas de Disponibilidad:

Un coche "Reservado" o "Alquilado" no puede ser seleccionado por otro usuario.

Aunque los coches no disponibles se muestren en el catálogo, no se podrán reservar ni alquilar.

Notificación de "Reserva" en Sesión:

Si un usuario elige un coche (lo añade al "carrito") pero no completa el pago, ese coche se guarda temporalmente en la sesión (HttpSession), pero no en la base de datos.

Se mostrará una notificación al usuario (ej: "Tiene un coche en reserva. Por favor, complete su compra").

Esta reserva temporal solo existe mientras la sesión del usuario esté activa; si cierra sesión, se pierde.

Seguridad: Autenticación y Autorización
El sistema se dividirá en una parte pública (visible para todos) y una parte privada (requiere login).

Autenticación (Login):

Es obligatorio iniciar sesión para poder reservar o alquilar un coche.

Si un usuario anónimo intenta acceder a estas funciones, será redirigido a la página de login o registro.

"Remember Me": Se usarán cookies (con setMaxAge) para guardar el usuario y contraseña si el usuario lo solicita, permitiendo rellenar el formulario de login en futuras visitas.

Email y 2FA: Se enviarán correos de bienvenida. Para la recuperación de contraseña (y opcionalmente 2FA), se enviará un correo con un código aleatorio de 6 dígitos válido por un corto período (ej. 1 minuto).

Autorización (Roles y Filtros):

Se implementará un AuthFilter (Filtro de Autorización) para controlar el acceso a las URLs.

Roles: Se definirán roles claros (ej. Cliente, Empleado). Solo los "Clientes" pueden reservar o alquilar.

Lógica del Filtro:

El filtro intercepta la petición y obtiene la URL solicitada.

Obtiene la sesión del usuario (si no existe, crea una para un usuario "Anónimo").

Se llama a un método de autorización que verifica si el rol del usuario en sesión tiene permiso para acceder a esa URL (comparando con una lista de URLs autorizadas por rol).

Si tiene permiso, se ejecuta chain.doFilter(request, response).

Si no tiene permiso, se le redirige (ej. al login o a una página de acceso denegado).

Validación de Formularios y Gestión de Errores
La validación se gestionará centralizadamente en los Servlets.

El Servlet de validación recibe los parámetros del formulario (ej. request.getParameter("username")).

Valida cada campo según las reglas de negocio.

Si se encuentran errores, se almacenan en un Map<String, String> (ej. errors.put("username", "El usuario es obligatorio.")).

Si hay errores (el Map no está vacío):

El Map de errores se guarda en la request (request.setAttribute("error", errors)).

Se redirige (mediante forward) de vuelta al formulario JSP.

Si no hay errores (el Map está vacío):

Se continúa con el proceso normal (ej. guardar en BD, redirigir a "success.jsp").

Visualización de Errores en JSP (usando JSTL): En el formulario JSP, se comprueba si existe el atributo "error" y se iteran los mensajes.

Java

<c:if test="${not empty error}">
    <div class="error-messages">
        <c:forEach var="entry" items="${error.entrySet()}">
            <div class="error-message">${entry.value}</div>
            <%-- Para internacionalización, se podría usar la clave:
                 <fmt:message key="${entry.key}_error" /> 
            --%>
        </c:forEach>
    </div>
</c:if>
Internacionalización (i18n)
El usuario podrá cambiar el idioma, incluso si no ha iniciado sesión.

Prioridad de Selección de Idioma (Lógica del LanguageServlet):

Cookie: Se comprueba si el cliente envía una cookie llamada "locale". Si existe, se usa ese idioma y se guarda en la sesión.

Cabecera del Navegador: Si no hay cookie, se extrae la cabecera Accept-Language (request.getHeader("Accept-Language")).

Búsqueda de Coincidencia: Se compara la lista ordenada de idiomas preferidos del navegador con la lista de idiomas soportados por la aplicación (ej. "en", "es", "fr") usando Locale.lookup.

Establecimiento: Si se encuentra una coincidencia soportada, se guarda en la sesión.

Por Defecto: Si no se encuentra ninguna coincidencia, se establece el idioma por defecto (ej. "en") en la sesión.

Persistencia: Cuando un usuario selecciona activamente un idioma, este se guarda en una cookie con fecha de expiración para futuras visitas.

Otras Funcionalidades y Prácticas
CRUD y Vistas: Implementación de operaciones CRUD (Crear, Leer, Actualizar, Eliminar) para las entidades principales (ej. Coches, Productos) que incluirán fotos. Las JSPs mostrarán estas entidades y sus imágenes.

Edición de Perfil: Los usuarios podrán editar su información de perfil, incluyendo la subida de una foto de avatar.

Gestión de Archivos: El sistema manejará la subida y descarga de imágenes (fotos de coches, avatares).

Búsqueda y Paginación: Se implementará búsqueda estructurada (con filtros) y paginación para los listados de entidades.

Evitar Literales (Strings "Mágicos"): Los nombres de los parámetros de request, atributos de sesión, etc., no deben escribirse directamente en el código. Se definirán como constantes private static final String al inicio de la clase Servlet.

Control de Caché: Se usarán cabeceras HTTP en los Servlets (Cache-Control: no-cache, no-store, Expires: 0) para forzar al navegador a no guardar en caché las respuestas de páginas dinámicas o privadas.

## Database Schema

The shared entity-relationship diagram highlights the main tables and their relationships. The following summary captures that information so that anyone on the team (including Codex) can consult it without relying on the original image.

### Core Tables

* **language** (`language_id` PK, `name`, `code`): catalog of supported languages.
* **role** (`role_id` PK, `name`): system roles such as Customer or Employee.
* **user** (`user_id` PK, `role_id` FK → `role.role_id`, authentication/profile fields like `email`, `password`, `first_name`, `last_name`, `phone`).
* **address** (`address_id` PK, `street`, `postal_code`, `city_id` FK → `city.city_id`).
* **province** (`province_id` PK, `name`).
* **city** (`city_id` PK, `province_id` FK → `province.province_id`, `name`).
* **headquarters** (`headquarters_id` PK, `name`, `phone`, `email`).
* **headquarters_address** (`headquarters_id` PK/FK → `headquarters.headquarters_id`, `address_id` FK → `address.address_id`): bridge table linking each branch to its physical address.
* **employee** (`employee_id` PK, `user_id` FK → `user.user_id`, `headquarters_id` FK → `headquarters.headquarters_id`, employment data such as `hire_date`, `salary`).
* **vehicle_category** (`category_id` PK, `code`): catalog of vehicle categories.
* **vehicle_category_language** (`category_id` PK/FK → `vehicle_category.category_id`, `language_id` PK/FK → `language.language_id`, `name`, `description`): localized names and descriptions for each category.
* **vehicle_status** (`vehicle_status_id` PK, `code`): internal vehicle statuses.
* **vehicle_status_language** (`vehicle_status_id` PK/FK → `vehicle_status.vehicle_status_id`, `language_id` PK/FK → `language.language_id`, `name`, `description`): localized status descriptions.
* **vehicle** (`vehicle_id` PK, `category_id` FK → `vehicle_category.category_id`, `vehicle_status_id` FK → `vehicle_status.vehicle_status_id`, `headquarters_id` FK → `headquarters.headquarters_id`, attributes such as `license_plate`, `brand`, `model`, `year`, `price_per_day`).
* **reservation_status** (`reservation_status_id` PK, `code`): overall reservation statuses.
* **reservation_status_language** (`reservation_status_id` PK/FK → `reservation_status.reservation_status_id`, `language_id` PK/FK → `language.language_id`, `name`, `description`): localized reservation status descriptions.
* **rental_status** (`rental_status_id` PK, `code`): active rental statuses.
* **rental_status_language** (`rental_status_id` PK/FK → `rental_status.rental_status_id`, `language_id` PK/FK → `language.language_id`, `name`, `description`): localized rental status descriptions.
* **reservation** (`reservation_id` PK, `user_id` FK → `user.user_id`, `vehicle_id` FK → `vehicle.vehicle_id`, `reservation_status_id` FK → `reservation_status.reservation_status_id`, fields like `start_date`, `end_date`, `total_price`, `payment_reference`).
* **rental** (`rental_id` PK, `reservation_id` FK → `reservation.reservation_id`, `rental_status_id` FK → `rental_status.rental_status_id`, fields such as `pickup_date`, `return_date`, `mileage_start`, `mileage_end`).

### Key Relationships

* Each **user** belongs to a **role** and can own multiple **reservations**.
* A **reservation** targets a specific **vehicle** and references a **reservation_status**. Once activated, it creates a related **rental** record describing the active rental state.
* **Vehicles** depend on a **category**, a **status**, and a **headquarters**; each of these entities has language-specific tables to support localization.
* The geographic hierarchy flows from **province** → **city** → **address**, which then links to branches through **headquarters_address**.
* **Employees** extend **user** records and attach to a **headquarters**, distinguishing them from customers.

This documentation mirrors the diagram faithfully and serves as a quick reference for development and database integrations.
