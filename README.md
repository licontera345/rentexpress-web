# Aplicación Web RentExpress

Aplicación Java EE (Jakarta) para la gestión integral de una empresa de alquiler de vehículos. El módulo web consume los
servicios expuestos por `RentExpres-middleware` para autenticar usuarios, consultar catálogos y tramitar reservas.

## ✨ Funcionalidades reales
- **Sitio público y multilingüe** (`PublicHomeServlet`, `LanguageServlet`): portada corporativa, selección de idioma por cookie
  y sesión, y cabecera común con navegación.<sup>1</sup>
- **Catálogo de vehículos** (`PublicVehicleServlet`, `VehicleSearchJsonServlet`): filtrado por marca/categoría, paginación
  con `Results<T>`, tarjetas reutilizables y selección temporal de un vehículo en carrito.<sup>2</sup>
- **Reservas online** (`PublicReservationServlet`): formulario con fechas, sedes de recogida/devolución y creación de reservas
  asociadas al usuario autenticado.<sup>3</sup>
- **Registro y autenticación con 2FA** (`PublicUserServlet`, `LoginServlet`, `TwoFactorServlet`): alta de clientes, inicio de
  sesión para usuarios y empleados, recordatorio "remember me" y verificación en dos pasos por correo.<sup>4</sup>
- **Recuperación de contraseñas** (`PasswordRecoveryServlet`): envío de códigos temporales por correo y restablecimiento seguro
  de la contraseña.<sup>5</sup>
- **Zona privada** (`PrivateVehicleServlet`, `PrivateEmployeeServlet`, `ProfileServlet`): panel para empleados con gestión de
  vehículos y empleados (CRUD con validación y mensajes de feedback) y edición del perfil.<sup>6</sup>
- **Utilidades comunes** (`CityByProvinceServlet`, `CookieManager`, `SessionManager`): carga dinámica de ciudades, gestión de
  sesión/cookies y helpers para la vista.<sup>7</sup>

> <sup>1</sup> Ver `src/main/java/com/pinguela/rentexpressweb/controller/PublicHomeServlet.java` y `LanguageServlet.java`.
> <sup>2</sup> Ver `src/main/java/com/pinguela/rentexpressweb/controller/PublicVehicleServlet.java` y `VehicleSearchJsonServlet.java`.
> <sup>3</sup> Ver `src/main/java/com/pinguela/rentexpressweb/controller/PublicReservationServlet.java`.
> <sup>4</sup> Ver `src/main/java/com/pinguela/rentexpressweb/controller/PublicUserServlet.java`, `LoginServlet.java` y `TwoFactorServlet.java`.
> <sup>5</sup> Ver `src/main/java/com/pinguela/rentexpressweb/controller/PasswordRecoveryServlet.java`.
> <sup>6</sup> Ver `src/main/java/com/pinguela/rentexpressweb/controller/PrivateVehicleServlet.java`, `PrivateEmployeeServlet.java` y `ProfileServlet.java`.
> <sup>7</sup> Ver `src/main/java/com/pinguela/rentexpressweb/controller/CityByProvinceServlet.java` y utilidades en `src/main/java/com/pinguela/rentexpressweb`.

## 🧠 Arquitectura en capas
```
JSP (vista) ⇆ Servlets (controlador) ⇆ RentExpres-middleware (servicio + DAO) ⇆ MySQL
```
- Las JSP sólo presentan datos con JSTL/EL (`src/main/webapp/public`, `src/main/webapp/private`).
- Los servlets validan parámetros, gestionan la sesión y delegan en el middleware (`com.pinguela.rentexpres.service.*`).
- Los filtros (`EncodingFilter`, `LoggingFilter`, `AuthFilter`) garantizan codificación UTF-8, auditoría y control de acceso.

## 🗂️ Estructura del proyecto
```
rentexpress-web/
├── pom.xml
├── src/main/java/com/pinguela/rentexpressweb/
│   ├── controller/     # Servlets públicos y privados (login, catálogo, reservas, backoffice)
│   ├── filter/         # Filtros de autenticación, logging y codificación
│   ├── security/       # Gestión de cookies y flujo 2FA
│   ├── util/           # Helpers de sesión, mensajes, paginación y vistas
│   └── constants/      # Constantes compartidas entre servlets y JSP
├── src/main/webapp/
│   ├── public/         # JSP públicas (home, login, registro, catálogo, recuperación)
│   ├── private/        # JSP privadas (dashboard, perfiles, gestión CRUD)
│   ├── common/         # Header/footer reutilizables
│   ├── css/            # Estilos propios
│   └── WEB-INF/
│       ├── web.xml     # Declaración de filtros y configuración web
│       └── classes/
│           ├── config.properties   # Propiedades de BD, correo e imágenes
│           ├── log4j2.properties   # Configuración de logging
│           └── i18n/               # Bundles de mensajes (es, en, fr)
└── README.md
```

## 🛠️ Tecnologías
- **Java 8** + **Jakarta Servlet/JSP 5** (Tomcat 10).
- **JSP + JSTL** como tecnología de vistas.
- **Log4j2** para trazas (API/Core/Web).
- **MySQL** como base de datos (vía `RentExpres-middleware`).
- **c3p0**, **Commons Email**, **Gson**, **Jasypt** y **BCrypt** suministrados por el middleware.

## ⚙️ Configuración necesaria
1. **Dependencia middleware**: instalar/deployar `RentExpres-middleware` 1.0.0 en el repositorio Maven indicado en el `pom.xml`.
2. **Base de datos**: crear el esquema `rentexpres`, importar tablas y datos del proyecto `RentExpres-bd` y configurar el
   `DataSource` (por ejemplo en `context.xml`).
3. **Propiedades de aplicación**: editar `src/main/webapp/WEB-INF/classes/config.properties` con credenciales de BD, SMTP y
   rutas locales para imágenes.
4. **Correo electrónico**: completar `mail.*` para poder enviar códigos 2FA y recuperación de contraseña.
5. **Logging**: ajustar `src/main/webapp/WEB-INF/classes/log4j2.properties` para el entorno (console/file appenders).

## ▶️ Construcción y despliegue
```bash
mvn clean package
```
- El WAR `target/rentexpress-web.war` puede desplegarse en Tomcat 10+.
- Endpoints relevantes tras desplegar en `http://localhost:8080/rentexpress-web`:
  - `/public/index` – portada pública gestionada por `PublicHomeServlet`.
  - `/public/VehicleServlet` – catálogo con filtros, paginación y carrito temporal.
  - `/public/users/register` – alta de clientes con envío de código 2FA.
  - `/public/security/recovery` – flujo de recuperación de contraseña.
  - `/private/dashboard` – acceso privado tras autenticación y verificación.

## ✅ Convenciones de desarrollo
- Validaciones y mensajes de error en los servlets; las JSP sólo muestran `errors`, `flashSuccess` o `flashError`.
- Nada de `System.out.println`: toda la salida va por Log4j2.
- Cierre y limpieza de sesión controlados con `SessionManager` y `CookieManager`.
- Internacionalización vía bundles `i18n.Messages*` y etiquetas `<fmt:message/>` en la vista.
# rentexpress-web
