# RentExpress Web

Aplicación web para la reserva y alquiler de coches desarrollada con Java Servlets y JSP.

## Índice
- [1. Resumen del proyecto](#1-resumen-del-proyecto)
- [2. Middleware incluido en el proyecto](#2-middleware-incluido-en-el-proyecto)
  - [2.1 Pasos para aprovechar el middleware](#21-pasos-para-aprovechar-el-middleware)
  - [2.2 Clases disponibles en `RentExpres.jar`](#22-clases-disponibles-en-rentexpresjar)
- [3. Arquitectura y patrón MVC](#3-arquitectura-y-patrón-mvc)
- [4. Funcionalidades principales](#4-funcionalidades-principales)
  - [4.1 Validación de formularios](#41-validación-de-formularios)
  - [4.2 Gestión de sesiones y cookies](#42-gestión-de-sesiones-y-cookies)
  - [4.3 Gestión de archivos](#43-gestión-de-archivos)
  - [4.4 Acción de negocio: reservas y alquileres](#44-acción-de-negocio-reservas-y-alquileres)
- [5. Reglas de negocio para reservas y alquileres](#5-reglas-de-negocio-para-reservas-y-alquileres)
- [6. Autenticación y autorización](#6-autenticación-y-autorización)
  - [6.1 Flujo de autenticación](#61-flujo-de-autenticación)
  - [6.2 Filtro de autorización (`AuthFilter`)](#62-filtro-de-autorización-authfilter)
  - [6.3 Recuerda: no usar un único front-controller](#63-recuerda-no-usar-un-único-front-controller)
- [7. Gestión de sesiones, cookies y caché](#7-gestión-de-sesiones-cookies-y-caché)
- [8. Validación y gestión de errores](#8-validación-y-gestión-de-errores)
- [9. Internacionalización (i18n)](#9-internacionalización-i18n)
- [10. Notificaciones y retenciones de vehículos](#10-notificaciones-y-retenciones-de-vehículos)
- [11. CRUD, búsquedas estructuradas y perfiles](#11-crud-búsquedas-estructuradas-y-perfiles)
- [12. Gestión de archivos (subida y descarga de imágenes)](#12-gestión-de-archivos-subida-y-descarga-de-imágenes)
- [13. Seguridad imprescindible](#13-seguridad-imprescindible)
- [14. Capa de datos y persistencia](#14-capa-de-datos-y-persistencia)
- [15. Tecnologías utilizadas](#15-tecnologías-utilizadas)
- [16. Esquema de base de datos](#16-esquema-de-base-de-datos)
  - [16.1 Tablas principales](#161-tablas-principales)
  - [16.2 Relaciones clave](#162-relaciones-clave)
- [17. Utilidades clave y buenas prácticas](#17-utilidades-clave-y-buenas-prácticas)
- [18. Checklist de "lo mínimo decente"](#18-checklist-de-lo-mínimo-decente)
- [19. Ejemplos de código de referencia](#19-ejemplos-de-código-de-referencia)
  - [19.1 Servlet de validación](#191-servlet-de-validación)
  - [19.2 Validación en JSP](#192-validación-en-jsp)
  - [19.3 Servlet de idioma y i18n](#193-servlet-de-idioma-y-i18n)
  - [19.4 Notificación de coche retenido en sesión](#194-notificación-de-coche-retenido-en-sesión)
  - [19.5 Servicios de correo y 2FA](#195-servicios-de-correo-y-2fa)
  - [19.6 Login con "remember me"](#196-login-con-remember-me)
  - [19.7 Constantes para parámetros de petición](#197-constantes-para-parámetros-de-petición)
  - [19.8 Control de caché en respuestas](#198-control-de-caché-en-respuestas)
  - [19.9 Uso del header `Accept-Language`](#199-uso-del-header-accept-language)
  - [19.10 CRUD de ejemplo con fotos](#1910-crud-de-ejemplo-con-fotos)
prohibido:usar lambda ...muy prohibido 
importante:uso java 1.8 siempre
recuerda:no crearas nuevas cosas del middleware lo tuyo es hacer que lo que tengo en el middleware usarlo en el appweb si no esta no inventes

## 1. Resumen del proyecto
La meta es construir una aplicación web que permita gestionar reservas y alquileres de vehículos. Debe cubrir las capacidades básicas del dominio: CRUD de entidades, búsqueda estructurada, edición de perfil, internacionalización, seguridad (zonas públicas y privadas), validación de formularios, gestión de sesión y cookies, gestión de archivos y lógica de negocio específica para reservas y alquileres.

Siempre revisar el archivo que está a mano para confirmar requisitos previos antes de implementar nuevas funcionalidades.

## 2. Middleware incluido en el proyecto
Antes de crear nuevos servlets, servicios u otros componentes, revisa el middleware empaquetado en [`src/main/webapp/WEB-INF/lib/RentExpres.jar`](src/main/webapp/WEB-INF/lib/RentExpres.jar).

### 2.1 Pasos para aprovechar el middleware
1. Lista o extrae los recursos disponibles (por ejemplo, `jar tf src/main/webapp/WEB-INF/lib/RentExpres.jar`).
2. Comprueba si los servicios, DAO u otras clases que necesitas ya existen y pueden reutilizarse.
3. Si la clase está accesible como `.java`, revisa el código para entender el comportamiento de los métodos relevantes.
4. Confirma que el método que planeas usar esté realmente disponible. Si no existe, coordina con el equipo antes de continuar.
5. Cuando el método exista, replica su comportamiento en los componentes nuevos para mantener la coherencia funcional.

Seguir este flujo garantiza que las nuevas funcionalidades se apoyen en lo que ya está implementado y que la información mostrada en la aplicación se mantenga alineada con los servicios existentes.

### 2.2 Clases disponibles en `RentExpres.jar`
El archivo `rentexpress-middleware.zip` contiene las siguientes clases dentro de `RentExpres/src`:

- `com.pinguela.rentexpres.config.ConfigManager`
- `com.pinguela.rentexpres.dao.AddressDAO`
- `com.pinguela.rentexpres.dao.CityDAO`
- `com.pinguela.rentexpres.dao.EmployeeDAO`
- `com.pinguela.rentexpres.dao.HeadquartersDAO`
- `com.pinguela.rentexpres.dao.ProvinceDAO`
- `com.pinguela.rentexpres.dao.RentalDAO`
- `com.pinguela.rentexpres.dao.RentalStatusDAO`
- `com.pinguela.rentexpres.dao.ReservationDAO`
- `com.pinguela.rentexpres.dao.ReservationStatusDAO`
- `com.pinguela.rentexpres.dao.RoleDAO`
- `com.pinguela.rentexpres.dao.UserDAO`
- `com.pinguela.rentexpres.dao.VehicleCategoryDAO`
- `com.pinguela.rentexpres.dao.VehicleDAO`
- `com.pinguela.rentexpres.dao.VehicleStatusDAO`
- `com.pinguela.rentexpres.dao.impl.AddressDAOImpl`
- `com.pinguela.rentexpres.dao.impl.CityDAOImpl`
- `com.pinguela.rentexpres.dao.impl.EmployeeDAOImpl`
- `com.pinguela.rentexpres.dao.impl.HeadquartersDAOImpl`
- `com.pinguela.rentexpres.dao.impl.ProvinceDAOImpl`
- `com.pinguela.rentexpres.dao.impl.RentalDAOImpl`
- `com.pinguela.rentexpres.dao.impl.RentalStatusDAOImpl`
- `com.pinguela.rentexpres.dao.impl.ReservationDAOImpl`
- `com.pinguela.rentexpres.dao.impl.ReservationStatusDAOImpl`
- `com.pinguela.rentexpres.dao.impl.RoleDAOImpl`
- `com.pinguela.rentexpres.dao.impl.UserDAOImpl`
- `com.pinguela.rentexpres.dao.impl.VehicleCategoryDAOImpl`
- `com.pinguela.rentexpres.dao.impl.VehicleDAOImpl`
- `com.pinguela.rentexpres.dao.impl.VehicleStatusDAOImpl`
- `com.pinguela.rentexpres.exception.DataException`
- `com.pinguela.rentexpres.exception.RentexpresException`
- `com.pinguela.rentexpres.model.AddressDTO`
- `com.pinguela.rentexpres.model.CityDTO`
- `com.pinguela.rentexpres.model.CriteriaBase`
- `com.pinguela.rentexpres.model.EmployeeCriteria`
- `com.pinguela.rentexpres.model.EmployeeDTO`
- `com.pinguela.rentexpres.model.HeadquartersDTO`
- `com.pinguela.rentexpres.model.LanguageDTO`
- `com.pinguela.rentexpres.model.ProvinceDTO`
- `com.pinguela.rentexpres.model.RentalCriteria`
- `com.pinguela.rentexpres.model.RentalDTO`
- `com.pinguela.rentexpres.model.RentalStatusDTO`
- `com.pinguela.rentexpres.model.RentalStatusLanguageDTO`
- `com.pinguela.rentexpres.model.ReservationCriteria`
- `com.pinguela.rentexpres.model.ReservationDTO`
- `com.pinguela.rentexpres.model.ReservationStatusDTO`
- `com.pinguela.rentexpres.model.ReservationStatusLanguageDTO`
- `com.pinguela.rentexpres.model.Results`
- `com.pinguela.rentexpres.model.RoleDTO`
- `com.pinguela.rentexpres.model.UserCriteria`
- `com.pinguela.rentexpres.model.UserDTO`
- `com.pinguela.rentexpres.model.ValueObject`
- `com.pinguela.rentexpres.model.VehicleCategoryDTO`
- `com.pinguela.rentexpres.model.VehicleCategoryLanguageDTO`
- `com.pinguela.rentexpres.model.VehicleCriteria`
- `com.pinguela.rentexpres.model.VehicleDTO`
- `com.pinguela.rentexpres.model.VehicleStatusDTO`
- `com.pinguela.rentexpres.model.VehicleStatusLanguageDTO`
- `com.pinguela.rentexpres.service.AddressService`
- `com.pinguela.rentexpres.service.CityService`
- `com.pinguela.rentexpres.service.EmployeeService`
- `com.pinguela.rentexpres.service.FileService`
- `com.pinguela.rentexpres.service.MailService`
- `com.pinguela.rentexpres.service.ProvinceService`
- `com.pinguela.rentexpres.service.RentalService`
- `com.pinguela.rentexpres.service.RentalStatusService`
- `com.pinguela.rentexpres.service.ReservationService`
- `com.pinguela.rentexpres.service.ReservationStatusService`
- `com.pinguela.rentexpres.service.RoleService`
- `com.pinguela.rentexpres.service.UserService`
- `com.pinguela.rentexpres.service.VehicleCategoryService`
- `com.pinguela.rentexpres.service.VehicleService`
- `com.pinguela.rentexpres.service.VehicleStatusService`
- `com.pinguela.rentexpres.service.impl.AddressServiceImpl`
- `com.pinguela.rentexpres.service.impl.CityServiceImpl`
- `com.pinguela.rentexpres.service.impl.EmployeeServiceImpl`
- `com.pinguela.rentexpres.service.impl.FileServiceImpl`
- `com.pinguela.rentexpres.service.impl.MailServiceImpl`
- `com.pinguela.rentexpres.service.impl.ProvinceServiceImpl`
- `com.pinguela.rentexpres.service.impl.RentalServiceImpl`
- `com.pinguela.rentexpres.service.impl.RentalStatusServiceImpl`
- `com.pinguela.rentexpres.service.impl.ReservationServiceImpl`
- `com.pinguela.rentexpres.service.impl.ReservationStatusServiceImpl`
- `com.pinguela.rentexpres.service.impl.RoleServiceImpl`
- `com.pinguela.rentexpres.service.impl.UserServiceImpl`
- `com.pinguela.rentexpres.service.impl.VehicleCategoryServiceImpl`
- `com.pinguela.rentexpres.service.impl.VehicleServiceImpl`
- `com.pinguela.rentexpres.service.impl.VehicleStatusServiceImpl`
- `com.pinguela.rentexpres.util.JDBCUtils`

## 3. Arquitectura y patrón MVC
Sigue una separación estricta entre capas: las JSP actúan únicamente como vistas y toda la lógica reside en Servlets, servicios o DAOs. Usa el Servlet como punto de entrada para cada petición: valida parámetros, invoca la capa de servicio/DAO y deja los datos listos como atributos (`request.setAttribute`). Termina el flujo con un `RequestDispatcher.forward` hacia la JSP correspondiente, donde se renderiza con JSTL y Expression Language. Evita los *scriptlets* (`<% ... %>`) salvo para casos puntuales.

Aunque las JSP permiten directivas y scripting, prioriza siempre EL/JSTL y clases Java externas. Esta disciplina mantiene el código más limpio, permite testear la lógica con facilidad y evita mezclar responsabilidades.

## 4. Funcionalidades principales
### 4.1 Validación de formularios
1. Implementar la validación directamente en los servlets.
2. Gestionar errores y excepciones de forma adecuada.
3. Mostrar los mensajes de error mediante JSP.
4. Utilizar un servlet dedicado para manejar excepciones y redirigir a páginas de error personalizadas.

### 4.2 Gestión de sesiones y cookies
1. Implementar la gestión de sesiones para el inicio de sesión.
2. Guardar las preferencias del usuario mediante cookies.
3. Mostrar las preferencias del usuario en JSP.
4. Controlar login y preferencias desde un servlet específico.

### 4.3 Gestión de archivos
1. Permitir la subida y descarga de imágenes.
2. Gestionar ambas operaciones mediante un servlet.
3. Mostrar formularios y enlaces de descarga con JSP.

### 4.4 Acción de negocio: reservas y alquileres
1. Implementar la lógica completa de reserva y alquiler de coches.
2. Utilizar servlets para procesar las reservas y alquileres.
3. Mostrar formularios y resultados en JSP.
4. Mantener el estado de reservas y alquileres en la base de datos.
- Acción de mi negocio: reserva/alquiler de coches con cambio automático de estado según fechas.

## 5. Reglas de negocio para reservas y alquileres
- La reserva es un paso previo obligatorio al alquiler. Aunque el usuario desee alquilar directamente, debe generar primero una reserva.
- Un coche reservado o alquilado no puede volver a reservarse ni alquilarse por otro usuario mientras no se libere.
- La gestión de fechas debe evitar solapamientos:
  1. El usuario selecciona vehículo y fechas de inicio/fin.
  2. El sistema valida la disponibilidad del vehículo en ese rango.
  3. Si hay disponibilidad, se crea la reserva y el vehículo pasa a estado **reservado**.
  4. En la fecha de inicio, el estado cambia automáticamente a **alquilado**.
  5. En la fecha de fin del alquiler, el vehículo vuelve al estado **disponible**.
- Aunque no haya sesión iniciada, el usuario puede cambiar el idioma y ver los vehículos, pero no puede reservar ni alquilar.
- Una notificación debe recordar al usuario cuando tiene un coche en reserva pero aún no ha pagado. El vehículo puede permanecer en la sesión (no en la base de datos) mientras dure `HttpSession`.
- Aunque lo muestres y permita ver sus características, un coche cuyo estado no sea **disponible** no puede reservarse ni alquilarse.
- Resumen de la aplicación web de reserva/alquiler de coches: siempre hay que autenticar al usuario antes de reservar o alquilar.

## 6. Autenticación y autorización
### 6.1 Flujo de autenticación
- Se debe autenticar al usuario antes de permitir reservas o alquileres. Si no está autenticado, se le redirige a la pantalla de login o registro.
- Logging in es obligatorio para reservar o alquilar un vehículo.
- Cuando un usuario anónimo intenta acceder a estas funciones, redirígelo a la pantalla adecuada.
- Usa cookies (`setMaxAge`) para la opción "Remember Me", rellenando el formulario de login en visitas futuras si el usuario lo solicita.
- Envía correos de bienvenida y códigos aleatorios de 6 dígitos (válidos 1 minuto) para recuperación de contraseña o 2FA.
- Tras iniciar sesión, tanto clientes como empleados deben seleccionar la sede (headquarters) desde la que operan, actualizando filtros y catálogos asociados.
- La barra de navegación debe saludar al usuario autenticado usando el marcador `{0}` definido en los bundles de i18n y mostrar el selector de sede.
- El primer alta pública siempre registra usuarios con rol **Customer**. Los empleados no se auto-registran: la organización crea sus cuentas y ellos únicamente inician sesión.

### 6.2 Filtro de autorización (`AuthFilter`)
- Implementa un filtro que obtenga la URL solicitada.
- Recupera la sesión existente (o crea una nueva si no hay) y determina el rol del usuario.
- Consulta una lista de URLs autorizadas por rol y permite el acceso solo si está autorizado.
- Si la comprobación es positiva, ejecuta `chain.doFilter(request, response)`; en caso contrario, redirige (login o acceso denegado).
- Define claramente los roles (por ejemplo, **Customer** y **Employee**). Solo los clientes pueden reservar o alquilar vehículos.
- Construye la autorización de cada rol a partir de listas explícitas de URLs permitidas y recórrelas para verificar la coincidencia con la ruta solicitada (piensa en el pseudocódigo `//authfilter`, `//inicio de decisión de filtrado`, `if/else`).

### 6.3 Recuerda: no usar un único front-controller
- No concentres toda la lógica en un único front-controller lleno de condicionales.
- Distribuye responsabilidades en clases específicas para cada comando o funcionalidad.

## 7. Gestión de sesiones, cookies y caché
HTTP es sin estado, por lo que dependemos de `HttpSession` para conservar datos como autenticación ligera, carritos o preferencias. El contenedor gestiona la cookie `JSESSIONID`, que identifica la sesión del cliente. Métodos clave: `request.getSession()`, `session.setAttribute(...)`, `session.getAttribute(...)`, `session.invalidate()` y `session.setMaxInactiveInterval(...)`.

Para seguridad básica, fuerza que el identificador de sesión viaje solo por cookie y bajo HTTPS, reduciendo riesgos de fijación o robo de sesión. Las preferencias simples (idioma, ciudad, última sede visitada) pueden persistirse mediante cookies propias (`response.addCookie`). En JSP consume esas cookies con `${cookie.miCookie.value}` y evita los *scriptlets*.

Usa cookies para recordar credenciales cuando se active "remember me" y define fechas de expiración claras. Controla la caché con cabeceras HTTP (`Cache-Control`, `Pragma`, `Expires`) para evitar almacenar páginas privadas en el navegador.
- "Gestión de sesiones y cookies": cubre login, preferencias de usuario y expiración controlada.
- Uso de cookies para guardar usuario y contraseña del login cuando se selecciona "remember me" (con caducidad definida).

## 8. Validación y gestión de errores
La validación se centraliza en los servlets, que reciben los parámetros, aplican reglas y, si detectan errores, los almacenan en un `Map<String, String>`. Estos mensajes se pasan a la JSP para mostrarse mediante JSTL/EL. Mantén un servlet de validación dedicado para reenviar a formularios con mensajes adecuados.

- `sevlet de validacion` (sí, valida y reenvía con los mensajes correspondientes).
- `error extends map<string ...>`: utiliza un mapa de errores para asociar campos con mensajes.
- Usa `request.setAttribute(...)` para compartir los errores con la vista y `<fmt:message key="${error.field}"/>` para internacionalizar los mensajes.
- "Validación y gestión de errores": cubre formularios y excepciones con respuestas claras.

## 9. Internacionalización (i18n)
Los usuarios pueden cambiar el idioma incluso sin haber iniciado sesión. Prioriza:
1. Comprobar si el cliente manda una cookie `locale`. Si existe, úsala y guárdala en sesión.
2. Si no hay cookie, lee el header `Accept-Language` (`request.getHeader("Accept-Language")`).
3. Empareja los locales preferidos del navegador con los soportados (`en`, `es`, `fr`) usando `Locale.lookup`.
4. Si hay coincidencia, úsala. Si no, fija `en` como idioma por defecto.
5. Cuando el usuario selecciona un idioma explícito, persiste la preferencia en cookies con caducidad.

Mantén bundles de mensajes y utiliza `<fmt:message>` y `<fmt:setBundle>` para renderizar textos internacionalizados.
- Internacionalización con cookies: combina el filtro de idioma con el cambio dinámico según la preferencia almacenada.

## 10. Notificaciones y retenciones de vehículos
- Si un usuario añade un coche a la reserva pero no finaliza el pago, guarda el vehículo temporalmente en sesión (no en BD) mientras la sesión siga activa.
- Muestra una notificación tipo "You have a car on hold. Please complete your purchase." para recordarle que finalice el proceso.
- Cuando el usuario sale (logout o caducidad de sesión), esa reserva provisional desaparece automáticamente.

## 11. CRUD, búsquedas estructuradas y perfiles
- Implementa CRUD completos (crear, leer, actualizar, eliminar) para entidades clave —por ejemplo vehículos— incluyendo manejo de imágenes.
- Provee vistas JSP para esas entidades (`datos` + `fotos`).
- Vistas de al menos una entidad (la misma que el CRUD) con datos reales y fotografías.
- Implementa búsqueda estructurada con filtros y paginación.
- Gestiona perfiles de usuario con edición y carga de foto avatar.
- Garantiza que existan vistas públicas y privadas según corresponda.
- Securización: define claramente la parte privada y la parte pública de la aplicación.
- "Búsqueda estructurada de una entidad con paginación": incluye filtros, página actual y tamaño de página.

## 12. Gestión de archivos (subida y descarga de imágenes)
- Habilita `@MultipartConfig` en los servlets que reciban ficheros.
- Procesa los `Part` recibidos con `request.getPart(...)` y decide si guardarás el binario o la ruta.
- Para descargas, configura el tipo de contenido y transmite el flujo adecuado al cliente.
- "Gestión de archivos": cubre la subida y descarga de imágenes (img) con sus formularios JSP.

## 13. Seguridad imprescindible
### Autenticación
- Nunca guardes contraseñas en texto plano. Usa BCrypt (por ejemplo, dependencias `jBCrypt` o `at.favre.lib:bcrypt`) con un coste adecuado.

### Autorización
- Protege zonas privadas implementando filtros (`javax.servlet.Filter` con `@WebFilter`).

### Confidencialidad e integridad
- Ejecuta el sitio bajo HTTPS para que credenciales y cookies no viajen en claro.

## 14. Capa de datos y persistencia
Selecciona la estrategia de persistencia que mejor encaje:

* **Opción JDBC.** Configura un `DataSource` vía JNDI en el contenedor, inyéctalo en tus DAOs y cierra recursos con bloques `finally` o `try-with-resources`. Implementa paginación (`LIMIT offset, pageSize`) desde el DAO para listados grandes.
* **Opción JPA/Hibernate.** Declara dependencias (`hibernate-entitymanager`, `mysql-connector-java`), organiza capas (Servlet/Controller → Service → DAO/JPA) y maneja transacciones con `@Transactional` o `UserTransaction`. Usa JPQL y `@NamedQuery` para consultas. Recuerda que `hibernate.hbm2ddl.auto` solo es aceptable en desarrollo.

## 15. Tecnologías utilizadas
- **Frontend:** JSP, HTML, CSS, Bootstrap
- **Backend:** Java Servlets (Jakarta EE)
- **Base de datos:** MySQL
- **Servidor de aplicaciones:** Apache Tomcat 10
- **Herramienta de compilación:** Maven

## 16. Esquema de base de datos
El diagrama entidad-relación compartido se resume a continuación para que cualquier integrante del equipo pueda consultarlo sin depender de la imagen original.

### 16.1 Tablas principales
- **language** (`language_id` PK, `name`, `code`)
- **role** (`role_id` PK, `name`)
- **user** (`user_id` PK, `role_id` FK → `role.role_id`, campos `email`, `password`, `first_name`, `last_name`, `phone`...)
- **address** (`address_id` PK, `street`, `postal_code`, `city_id` FK → `city.city_id`)
- **province** (`province_id` PK, `name`)
- **city** (`city_id` PK, `province_id` FK → `province.province_id`, `name`)
- **headquarters** (`headquarters_id` PK, `name`, `phone`, `email`)
- **headquarters_address** (`headquarters_id` PK/FK → `headquarters.headquarters_id`, `address_id` FK → `address.address_id`)
- **employee** (`employee_id` PK, `user_id` FK → `user.user_id`, `headquarters_id` FK → `headquarters.headquarters_id`, datos como `hire_date`, `salary`)
- **vehicle_category** (`category_id` PK, `code`)
- **vehicle_category_language** (`category_id` PK/FK → `vehicle_category.category_id`, `language_id` PK/FK → `language.language_id`, `name`, `description`)
- **vehicle_status** (`vehicle_status_id` PK, `code`)
- **vehicle_status_language** (`vehicle_status_id` PK/FK → `vehicle_status.vehicle_status_id`, `language_id` PK/FK → `language.language_id`, `name`, `description`)
- **vehicle** (`vehicle_id` PK, `category_id` FK → `vehicle_category.category_id`, `vehicle_status_id` FK → `vehicle_status.vehicle_status_id`, `headquarters_id` FK → `headquarters.headquarters_id`, atributos como `license_plate`, `brand`, `model`, `year`, `price_per_day`)
- **reservation_status** (`reservation_status_id` PK, `code`)
- **reservation_status_language** (`reservation_status_id` PK/FK → `reservation_status.reservation_status_id`, `language_id` PK/FK → `language.language_id`, `name`, `description`)
- **rental_status** (`rental_status_id` PK, `code`)
- **rental_status_language** (`rental_status_id` PK/FK → `rental_status.rental_status_id`, `language_id` PK/FK → `language.language_id`, `name`, `description`)
- **reservation** (`reservation_id` PK, `user_id` FK → `user.user_id`, `vehicle_id` FK → `vehicle.vehicle_id`, `reservation_status_id` FK → `reservation_status.reservation_status_id`, campos como `start_date`, `end_date`, `total_price`, `payment_reference`)
- **rental** (`rental_id` PK, `reservation_id` FK → `reservation.reservation_id`, `rental_status_id` FK → `rental_status.rental_status_id`, campos como `pickup_date`, `return_date`, `mileage_start`, `mileage_end`)

### 16.2 Relaciones clave
- Cada **user** pertenece a un **role** y puede crear múltiples **reservations**.
- Una **reservation** apunta a un **vehicle** y referencia un **reservation_status**; al activarse, crea un **rental** asociado.
- Los **vehicles** dependen de una **category**, un **status** y una **headquarters**; cada entidad dispone de tablas de idioma.
- La jerarquía geográfica va de **province** → **city** → **address**, enlazándose a las sedes mediante **headquarters_address**.
- Los **employees** amplían los registros de **user** y se asocian a una sede concreta.

## 17. Utilidades clave y buenas prácticas
- `Logs y depuración`: mantén el logging activo y revisa la consola o el manager del contenedor durante las pruebas.
- `Contenedores vs. servidores completos`: Tomcat cubre JSP/Servlets; si necesitas todo Jakarta EE (JTA, EJB, etc.) evalúa WildFly o GlassFish.
- Evita literales (`"magic strings"`): usa constantes `private static final String` para parámetros de petición y atributos de sesión.
- Maneja los encabezados HTTP para controlar caché e idioma.

## 18. Checklist de "lo mínimo decente"
- Tomcat 10 configurado y proyecto Maven WAR sin errores.
- JSP con JSTL/EL; cero lógica pesada en la vista.
- Control de sesión correcto con `JSESSIONID`; `logout` invalida sesión.
- Filtro de autorización para URLs privadas con redirección a login.
- Contraseñas con BCrypt y sitio bajo HTTPS.
- DataSource JNDI y DAOs limpios (JDBC o JPA bien encapsulados).
- Paginación en listados grandes desde el DAO.
- i18n con bundles y selector de idioma.
- Manejo de archivos cuando la app lo requiera.
- Scripts de BD o `hbm2ddl` solo en desarrollo, jamás directamente en producción.

## 19. Ejemplos de código de referencia
Los siguientes fragmentos recogen los ejemplos previamente acordados para guiar la implementación.

### 19.1 Servlet de validación
```java
@WebServlet("/validate")
public class ValidationServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Map<String, String> errors = new HashMap<>();

        String username = request.getParameter("username");
        String password = request.getParameter("password");

        if (username == null || username.isEmpty()) {
            errors.put("username", "Username is required.");
        }

        if (password == null || password.length() < 6) {
            errors.put("password", "Password must be at least 6 characters long.");
        }

        if (errors.isEmpty()) {
            response.sendRedirect("success.jsp");
        } else {
            request.setAttribute("error", errors);
            request.getRequestDispatcher("/form.jsp").forward(request, response);
        }
    }
}
```

### 19.2 Validación en JSP
```jsp
<%-- en el formulario JSP --%>
<c:if test="${not empty error}">
    <div class="error-messages">
        <c:forEach var="entry" items="${error.entrySet()}">
            <div class="error-message">${entry.value}</div>
            <%-- Para i18n:
                 <fmt:message key="${entry.key}_error" />
            --%>
        </c:forEach>
    </div>
</c:if>
```

### 19.3 Servlet de idioma y i18n
```java
@WebServlet("/language")
public class LanguageServlet extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Cookie[] cookies = request.getCookies();
        String locale = null;

        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("locale".equals(cookie.getName())) {
                    locale = cookie.getValue();
                    break;
                }
            }
        }

        if (locale == null) {
            String acceptLanguage = request.getHeader("Accept-Language");
            List<Locale.LanguageRange> languageRanges = Locale.LanguageRange.parse(acceptLanguage);
            List<Locale> supportedLocales = Arrays.asList(new Locale("en"), new Locale("es"), new Locale("fr"));

            Locale matchedLocale = Locale.lookup(languageRanges, supportedLocales);
            if (matchedLocale != null) {
                locale = matchedLocale.getLanguage();
            } else {
                locale = "en"; // default locale
            }
        }

        request.getSession().setAttribute("locale", locale);
        response.sendRedirect(request.getHeader("Referer"));
    }
}
```

### 19.4 Notificación de coche retenido en sesión
```java
@WebServlet("/notifyReservation")
public class NotifyReservationServlet extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        Car reservedCar = (Car) session.getAttribute("reservedCar");

        if (reservedCar != null) {
            request.setAttribute("notification", "You have a car reserved: " + reservedCar.getModel() + ". Please complete your purchase.");
        }

        request.getRequestDispatcher("/dashboard.jsp").forward(request, response);
    }
}
```

### 19.5 Servicios de correo y 2FA
```java
public class EmailService {
    public void sendWelcomeEmail(String toEmail) {
        String code = generateRandomCode(6);
        // Logic to send email with the code
    }

    public void sendPasswordRecoveryEmail(String toEmail) {
        String code = generateRandomCode(6);
        // Logic to send email with the code
    }

    private String generateRandomCode(int length) {
        Random random = new Random();
        StringBuilder code = new StringBuilder();
        for (int i = 0; i < length; i++) {
            code.append(random.nextInt(10));
        }
        return code.toString();
    }
}
```

### 19.6 Login con "remember me"
```java
@WebServlet("/login")
public class LoginServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        String rememberMe = request.getParameter("rememberMe");

        // Logic to authenticate user

        if ("on".equals(rememberMe)) {
            Cookie userCookie = new Cookie("username", username);
            Cookie passCookie = new Cookie("password", password);
            userCookie.setMaxAge(7 * 24 * 60 * 60);
            passCookie.setMaxAge(7 * 24 * 60 * 60);
            response.addCookie(userCookie);
            response.addCookie(passCookie);
        }

        response.sendRedirect("dashboard.jsp");
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Cookie[] cookies = request.getCookies();
        String username = "";
        String password = "";

        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("username".equals(cookie.getName())) {
                    username = cookie.getValue();
                }
                if ("password".equals(cookie.getName())) {
                    password = cookie.getValue();
                }
            }
        }

        request.setAttribute("username", username);
        request.setAttribute("password", password);
        request.getRequestDispatcher("/login.jsp").forward(request, response);
    }
}
```

### 19.7 Constantes para parámetros de petición
```java
@WebServlet("/example")
public class ExampleServlet extends HttpServlet {
    private static final String PARAM_USERNAME = "username";
    private static final String PARAM_PASSWORD = "password";

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String username = request.getParameter(PARAM_USERNAME);
        String password = request.getParameter(PARAM_PASSWORD);

        // Logic to process username and password

        response.sendRedirect("success.jsp");
    }
}
```

### 19.8 Control de caché en respuestas
```java
@WebServlet("/cacheExample")
public class CacheExampleServlet extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        response.setHeader("Pragma", "no-cache");
        response.setDateHeader("Expires", 0);

        // Logic to generate response content

        response.getWriter().write("This is a cache-controlled response.");
    }
}
```

### 19.9 Uso del header `Accept-Language`
```java
@WebServlet("/languageExample")
public class LanguageExampleServlet extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String acceptLanguage = request.getHeader("Accept-Language");
        List<Locale.LanguageRange> languageRanges = Locale.LanguageRange.parse(acceptLanguage);
        List<Locale> supportedLocales = Arrays.asList(new Locale("en"), new Locale("es"), new Locale("fr"));

        Locale matchedLocale = Locale.lookup(languageRanges, supportedLocales);
        if (matchedLocale != null) {
            request.getSession().setAttribute("locale", matchedLocale.getLanguage());
        } else {
            request.getSession().setAttribute("locale", "en");
        }

        response.sendRedirect("dashboard.jsp");
    }
}
```

### 19.10 CRUD de ejemplo con fotos
```java
public class Product {
    private int id;
    private String name;
    private String description;
    private double price;
    private String file;

    // Getters and setters
}

@WebServlet("/product")
public class ProductServlet extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");
        if ("list".equals(action)) {
            // Logic to list products
        } else if ("view".equals(action)) {
            // Logic to view a single product
        }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");
        if ("create".equals(action)) {
            // Logic to create a new product
        } else if ("update".equals(action)) {
            // Logic to update an existing product
        } else if ("delete".equals(action)) {
            // Logic to delete a product
        }
    }
}

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<html>
<head>
  <title>Product List</title>
</head>
  <body>
    <h1>Product List</h1>
    <c:forEach var="product" items="${products}">
        <div class="product">
            <h2>${product.name}</h2>
            <img src="images/${product.file}" alt="${product.name}" />
            <p>${product.description}</p>
            <p>Price: $${product.price}</p>
       </div>
    </c:forEach>
  </body>
</html>
```

**Comentarios estándar de los servlets**

Nunca elimines los comentarios generados automáticamente por Maven en cada servlet (por ejemplo, las anotaciones de `@see`). Esos comentarios forman parte del estándar del proyecto y deben mantenerse siempre
