# rentexpress-web
mi appweb
# RentExpress Web Middleware Guidelines


siempre revisar el archivo que esta a

**Comentarios estándar de los servlets**

Nunca elimines los comentarios generados automáticamente por Maven en cada servlet (por ejemplo, las anotaciones de `@see`). Esos comentarios forman parte del estándar del proyecto y deben mantenerse siempre.

Before implementing new servlets, services, or other components within the **rentexpress-web** project, first inspect the middleware bundled in [`src/main/webapp/WEB-INF/lib/RentExpres.jar`](src/main/webapp/WEB-INF/lib/RentExpres.jar).

1. Extract or list the existing resources (for example, with `jar tf src/main/webapp/WEB-INF/lib/RentExpres.jar`).
2. Check whether the services, DAOs, or other classes you need already exist and can be reused.
3. If the JAR exposes the class as `.java`, open the file and review exactly how the relevant methods behave.
4. Confirm that the method you plan to use is actually provided by the middleware; if it is not, pause the implementation and align on an alternative with the team.
5. When the method exists, mirror its behavior accurately in your servlet or component so the application stays functionally consistent.

Following this workflow ensures that new features build upon the middleware that is already implemented and that the information shown in the application stays aligned with the available services.


Key project notes:

The goal is to build a web application for car reservations and rentals. The application will deliver core capabilities such as CRUD for entities, user profile management, internationalization, security (public/private split), form validation, session management, and the business logic required for reservations.

Technologies in use:

Frontend: JSP, HTML, CSS, Bootstrap

Backend: Java Servlets (Jakarta EE)

Database: MySQL

Server: Apache Tomcat 10

Build: Maven

Business Logic: Reservation and Rental
This is the primary action within the application.

Reservation vs. Rental flow:

Reservation: The user prepays to secure a vehicle for a future date.

Rental: When the reservation’s start date arrives, it automatically becomes an active “Rental” until the return date.

Mandatory flow: Every rental must originate from a reservation, even if both happen on the same day.

State and date management:

The user selects a vehicle and a date range (start and end).

The system checks the vehicle’s availability for those dates.

If available, the reservation is created and the vehicle status changes to Reserved.

When the reservation start date arrives, the status automatically updates to Rented.

When the rental end date is reached, the status reverts to Available.

Availability rules:

A vehicle that is “Reserved” or “Rented” cannot be selected by another user.

Even if unavailable vehicles appear in the catalog, they cannot be reserved or rented.

Session-based “Reservation” notice:

If a user selects a vehicle (adds it to the “cart”) but does not complete payment, that vehicle is stored temporarily in the session (HttpSession) but not in the database.

Show a notification to the user (for example: “You have a car on hold. Please complete your purchase.”).

This temporary hold only exists while the user’s session is active; logging out clears it.

Security: Authentication and authorization
The system is divided into a public area (visible to everyone) and a private area (requires login).

Authentication (login):

Logging in is required to reserve or rent a vehicle.

If an anonymous user tries to access these features, redirect them to the login or registration page.

“Remember Me”: Use cookies (via `setMaxAge`) to save the username and password when requested, allowing the login form to be prefilled on future visits.

Email and 2FA: Send welcome emails. For password recovery (and optionally 2FA), send an email containing a random 6-digit code that remains valid for a short period (e.g., 1 minute).

Registration and role assignment flow:
* The first time a person creates an account in the system, they are always registered as a **Customer**. No other role can be chosen during self-registration, so the Customer role must be set as the default for new accounts.
* Employees do **not** register through the public flow. Their accounts are pre-created by the organization, and they simply sign in with the credentials provided to them.
* After logging in, both customers and employees must select the headquarters (branch) they are operating from. This branch selection drives the structured search experience by limiting the available vehicles to those assigned to the chosen headquarters.
* Whenever the user changes headquarters, the catalog and filters must refresh so only vehicles at that location appear in the results.
* The navigation bar greets the authenticated person using the `{0}` placeholder from the i18n bundles and exposes the branch selector used to update the session-wide headquarters filter.

Authorization (roles and filters):

Implement an `AuthFilter` to control URL access.

Roles: Define clear roles (for example, Customer and Employee). Only Customers can reserve or rent.

Filter logic:

The filter intercepts the request and obtains the requested URL.

It retrieves the user session (creating one for an “Anonymous” user if needed).

Call an authorization method that checks whether the session user’s role is permitted to access that URL (comparing against a list of role-authorized URLs).

If permitted, execute `chain.doFilter(request, response)`.

If not permitted, redirect the user (for example, to the login page or an access denied page).

Form validation and error handling
Validation will be centralized in the Servlets.

The validation servlet receives the form parameters (for example, `request.getParameter("username")`).

Validate each field according to the business rules.

When errors occur, store them in a `Map<String, String>` (for example, `errors.put("username", "Username is required.")`).

If errors exist (the map is not empty):

Store the error map in the request (`request.setAttribute("error", errors)`).

Forward back to the JSP form.

If there are no errors (the map is empty):

Continue with the regular flow (for example, save to the database, redirect to `success.jsp`).

Displaying errors in JSP (using JSTL): Within the JSP form, check for the `error` attribute and iterate over the messages.

Java

<c:if test="${not empty error}">
    <div class="error-messages">
        <c:forEach var="entry" items="${error.entrySet()}">
            <div class="error-message">${entry.value}</div>
            <%-- For internationalization, you could use a key such as:
                 <fmt:message key="${entry.key}_error" />
            --%>
        </c:forEach>
    </div>
</c:if>
Internationalization (i18n)
Users can switch the language even if they are not logged in.

Language selection priority (logic within `LanguageServlet`):

Cookie: Check whether the client sends a cookie named `locale`. If present, use that language and store it in the session.

Browser header: If there is no cookie, read the `Accept-Language` header (`request.getHeader("Accept-Language")`).

Matching: Compare the browser’s ordered list of preferred languages against the application’s supported languages (for example, `en`, `es`, `fr`) using `Locale.lookup`.

Setting: If a supported match is found, store it in the session.

Default: If no match is found, store the default language (`en`) in the session. The team aligned on `en` as the canonical fallback locale.

Persistence: When a user explicitly selects a language, save it to a cookie with an expiration date for future visits.

Additional features and practices
CRUD and views: Implement CRUD operations (create, read, update, delete) for the primary entities (for example, vehicles and products), including images. JSPs should render these entities and their photos.

Profile editing: Users can update their profile information, including uploading an avatar photo.

File management: The system handles uploading and downloading images (vehicle photos, avatars).

Search and pagination: Implement structured search (with filters) and pagination for entity listings.

Avoid literals (“magic strings”): Request parameter names, session attributes, and similar values should not be hard-coded. Declare them as `private static final String` constants at the start of the servlet class.

Cache control: Configure HTTP headers in servlets (`Cache-Control: no-cache, no-store`, `Expires: 0`) to prevent the browser from caching dynamic or private pages.

## Arquitectura y patrón MVC con JSP/Servlets

Sigue una separación estricta entre capas: las JSP actúan únicamente como vistas y toda la lógica reside en Servlets, servicios o DAOs. Usa el Servlet como punto de entrada para cada petición: valida parámetros, invoca la capa de servicio/DAO y deja los datos listos como atributos (`request.setAttribute`). Termina el flujo con un `RequestDispatcher.forward` hacia la JSP correspondiente, donde se renderiza con JSTL y Expression Language. Evita los *scriptlets* (`<% ... %>`) salvo para casos muy puntuales en los que no exista alternativa declarativa.

Aunque las JSP permiten directivas y scripting, prioriza siempre EL/JSTL y clases Java externas. Esta disciplina mantiene el código más limpio, permite testear la lógica con facilidad y evita mezclar responsabilidades.

## Sesiones y cookies

HTTP es sin estado, por lo que dependemos de `HttpSession` para conservar datos como autenticación ligera, carritos o preferencias. El contenedor gestiona la cookie `JSESSIONID`, que identifica la sesión del cliente. Métodos clave: `request.getSession()`, `session.setAttribute(...)`, `session.getAttribute(...)`, `session.invalidate()` y `session.setMaxInactiveInterval(...)`.

Para seguridad básica, fuerza que el identificador de sesión viaje solo por cookie y bajo HTTPS, reduciendo riesgos de fijación o robo de sesión. Las preferencias simples (idioma, ciudad, última sede visitada) pueden persistirse mediante cookies propias (`response.addCookie`). En JSP consume esas cookies con `${cookie.miCookie.value}` y evita los *scriptlets*.

## Seguridad imprescindible

### Autenticación

Nunca guardes contraseñas en texto plano. Usa BCrypt (dependencias recomendadas: `jBCrypt` o `at.favre.lib:bcrypt`) con un *cost* razonable para generar hashes y validarlos.

### Autorización

Protege las zonas privadas implementando filtros (`javax.servlet.Filter` con `@WebFilter`). Los filtros interceptan las URLs sensibles, verifican la sesión y el rol y, si falta autorización, redirigen a la página de login.

### Confidencialidad e integridad

Ejecuta el sitio bajo HTTPS para que credenciales y cookies no viajen en claro. Es un requisito mínimo en 2025.

## Capa de datos

Selecciona la estrategia de persistencia que mejor encaje con el proyecto:

* **Opción JDBC.** Configura un `DataSource` vía JNDI en el contenedor, inyéctalo en tus DAOs y cierra los recursos en bloques `finally` (o `try-with-resources`). Implementa paginación (`LIMIT offset, pageSize`) desde el DAO cuando trabajes con listados grandes.
* **Opción JPA/Hibernate.** Declara las dependencias (`hibernate-entitymanager`, `mysql-connector-java`), organiza capas (Servlet/Controller → Service → DAO/JPA) y maneja las transacciones con `@Transactional` o `UserTransaction`. Usa JPQL y `@NamedQuery` para consultas expresivas. Recuerda que `hibernate.hbm2ddl.auto` solo es aceptable en entornos de desarrollo; en producción aplica scripts versionados.

## Validación de formularios y mensajes

Centraliza la validación en el servidor con Bean Validation (JSR 303) sobre tus modelos o *backing beans*. Cuando un formulario falle, agrupa los errores en un `Map<String, String>`, guárdalos en la request y reenvía a la JSP. En la vista, itera con JSTL para mostrar mensajes y reutiliza EL para rellenar los valores introducidos.

## Internacionalización (i18n)

Mantén bundles de mensajes y permite al usuario seleccionar idioma. Persiste la elección en sesión y cookie para visitas posteriores. El mismo enfoque descrito en los temas de JSF se traslada a JSP+JSTL usando las etiquetas `<fmt:message>` y `<fmt:setBundle>`.

## Subida y descarga de ficheros

Para cargas, habilita soporte multipart (`@MultipartConfig`) en tus Servlets y procesa los ficheros con `request.getPart(...)`. Decide si guardas el binario o solo su ruta. Para descargas, configura el tipo de contenido correcto y transmite el flujo desde el almacenamiento.

## Utilidades clave

* **Logs y depuración.** Asegúrate de tener logging activo y revisa la consola o el manager del contenedor durante las pruebas.
* **Contenedores vs. servidores completos.** Tomcat proporciona JSP/Servlets; si necesitas características completas de Jakarta EE (JTA, EJB, etc.) evalúa WildFly o GlassFish.

## Checklist de “lo mínimo decente” que no puede faltar

* Tomcat 10 configurado y proyecto Maven WAR sin errores.
* JSP con JSTL/EL, cero lógica gorda en la vista.
* Control de sesión correcto con JSESSIONID, logout que invalida sesión.
* Filtro de autorización para URLs privadas y redirección a login.
* Contraseñas con BCrypt y sitio por HTTPS.
* DataSource JNDI y DAOs limpios (JDBC o JPA bien encapsulados).
* Paginación en listados grandes desde el DAO.
* i18n con bundles y selector de idioma.
* Manejo de archivos si tu app lo necesita.
* Scripts de BD o `hbm2ddl` solo en desarrollo, jamás a lo loco en producción.

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

algunas cosas a analisar :
sevlet de validacion

error extends map<string ...>

get
put 

request.setAtribute...



<fmt: me...${error.field}
map

------------------------
welcomeServlet
public usuario servlet
private usuario servlet
languageServlet
imageServlet
-------------------
no literal en el codigo parameter mejor en private 

------------------
una notificacion para decirle que tiene un coche en reserva pero que aun no ha pagado
cuando el usuario elige un coche a reservar o alquilar se guarda en la session pero no en la bd hasta que la compre 
esa notificiacion y reserva del coche "" entre comillas solo funcionara mientras el usuario este en la seccion cuando slaga ya no esta

--------------------------
lenguage servlet

1/ comprobar a ver si cliente manda cookie de locale 

si la manda no hago nada si lo tiene en la session o lo meto en la session

 si no la manda comprueba extraigo de la cabeserqa accect lenguage revviso la cabezera de la lista ordenada de los locales preferidos
   (request.getheader"accect.lenguage") del navegador
   
 para cada uno de ellos busco si lo tengo 
 
  si lo tengo soportado lo meto en la session
  
  si no encuentro ninguno de la lista de preferido le meto en la session el por defecto
-------------------
correo de bienbenida
2fA con numero aleatorio por correo y correo de recuperacion de contrasena con un numero aleatorio ambos con 6 digitos y un periodo de 1 minuto de validez
-------------------
validacion de formularios
1/ servlet de validacion que recibe los parametros del formulario
2/ valida los campos y si hay errores los mete en un map<string,string> donde la clave es el nombre del campo y el valor el mensaje de error
3/ si hay errores los mete en la request como atributo y redirige al formulario
4/ si no hay errores continua con el proceso normal

-------------------
validacion en jsp
<%-- en el formulario jsp --%>
<c:if test="${not empty error}">
    <div class="error-messages">
        <c:forEach var="entry" items="${error.entrySet()}">
            <div class="error-message">${entry.value}</div>
        </c:forEach>
    </div>
    </c:if>
    <%-- en el servlet de validacion --%>
    Map<String, String> errors = new HashMap<>();
    // validar campos
    if (errors.isEmpty()) {
        // continuar proceso normal
    } else {
        request.setAttribute("error", errors);
        request.getRequestDispatcher("/form.jsp").forward(request, response);
    }
    -------------------
    <fmt:message key="${error.field}"/>
    -------------------
USO de las cookies para guardar la preferencia de idioma del usuario
1/ cuando el usuario selecciona un idioma se guarda en una cookie
2/ en el servlet de lenguaje se comprueba si existe la cookie y se establece el idioma correspondiente	
3/ si no existe la cookie se utiliza el idioma por defecto del navegador
4/ en cada solicitud se lee la cookie para establecer el idioma adecuado
5/ la cookie tiene una fecha de expiracion para que no se guarde indefinidamente
-------------------
// ejemplo de servlet de validacion
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
            // Continue with normal processing
            response.sendRedirect("success.jsp");
        } else {
            request.setAttribute("error", errors);
            request.getRequestDispatcher("/form.jsp").forward(request, response);
        }
    }
}
// ejemplo de servlet de lenguaje
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
// ejemplo de notificacion de coche en reserva
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
// ejemplo de envio de correo de bienvenida
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
ap:message key="${error.field}"/>
    -------------------
    <fmt:message key="${error.field}"/>
    -------------------    
    
    
uso de cookie para guardar la contrasena y usuario de un login
1/ cuando el usuario selecciona "remember me" se guarda en una cookie
2/ en el servlet de login se comprueba si existe la cookie y se rellena el formulario de login con los datos correspondientes
3/ si no existe la cookie el formulario de login se muestra vacio
4/ la cookie tiene una fecha de expiracion para que no se guarde indefinidamente
-------------------
// ejemplo de servlet de login con "remember me"
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
            userCookie.setMaxAge(7 * 24 * 60 * 60); // 1 week
            passCookie.setMaxAge(7 * 24 * 60 * 60); // 1 week
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

-------------------
no literal en el codigo parameter mejor en private
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
-------------------
uso de los headers para controlar la cache
1/ en el servlet se establecen los headers para controlar la cache
2/ se puede establecer el header "Cache-Control" para definir las politicas de cache
3/ se puede establecer el header "Expires" para definir la fecha de expiracion de la cache
-------------------
uso de los header para idioma 
1/ en el servlet se lee el header "Accept-Language" para determinar el idioma preferido del usuario
2/ se puede utilizar este valor para establecer el locale en la session o en la aplicacion
3/ se puede utilizar este valor para mostrar contenido en el idioma preferido del usuario+
-------------------
// ejemplo de servlet con control de cache
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
// ejemplo de servlet que utiliza el header Accept-Language
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
            request.getSession().setAttribute("locale", "en"); // default locale
        }

        response.sendRedirect("dashboard.jsp");
    }
}
-------------------
Sí Rol CRUD adecuado de una entidad de modelo de datos con fotos
1/ Crear una entidad de modelo de datos con atributos incluyendo fotos
2/ Implementar el CRUD (Crear, Leer, Actualizar, Eliminar) para la entidad
3/ Utilizar un servlet para manejar las operaciones CRUD
4/ Utilizar JSP para mostrar las fotos y los datos de la entidad
-------------------
// ejemplo de entidad de modelo de datos con fotos
public class Product {
    private int id;
    private String name;
    private String description;
    private double price;
    private String file;

    // Getters and setters
}
// ejemplo de servlet para manejar operaciones CRUD
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
// ejemplo de JSP para mostrar fotos y datos de la entidad
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
-------------------
busqueda extructurada de una entidad con paginacion
1/ Crear una entidad de modelo de datos
2/ Implementar la busqueda estructurada con filtros
3/ Implementar la paginacion de los resultados
4/ Utilizar un servlet para manejar la busqueda y paginacion
5/ Utilizar JSP para mostrar los resultados de la busqueda con paginacion
-------------------
Edición de perfil con foto avatar
1/ Crear una entidad de usuario con atributos incluyendo foto avatar
2/ Implementar la funcionalidad de edición de perfil
3/ Utilizar un servlet para manejar la edición de perfil
4/ Utilizar JSP para mostrar el formulario de edición de perfil con foto avatar
-------------------
Internacionalización con cookies
Filtro de idioma y cambio de idioma
Vistas de al menos una entidad (la misma que el crud)
Datos de al menos una entidad
1/ Implementar la internacionalización utilizando cookies para guardar la preferencia de idioma del usuario
2/ Crear un filtro para manejar el cambio de idioma
3/ Utilizar JSP para mostrar las vistas de la entidad en diferentes idiomas
4/ Utilizar un servlet para manejar los datos de la entidad
-------------------
Securización: parte privada / parte pública
1/ Implementar la securización de la aplicación dividiendo en parte privada y parte pública
2/ Utilizar filtros para controlar el acceso a la parte privada
3/ Utilizar JSP para mostrar las vistas de la parte pública y privada
4/ Utilizar un servlet para manejar las operaciones en la parte privada
-------------------
Validación y gestión de errores: formularios y excepciones
1/ Implementar la validación de formularios en el servlet
2/ Gestionar los errores y excepciones de manera adecuada
3/ Utilizar JSP para mostrar los mensajes de error en los formularios
4/ Utilizar un servlet para manejar las excepciones y redirigir a páginas de error
-------------------
Gestión de sesiones y cookies: login, preferencias de usuario
1/ Implementar la gestión de sesiones para el login de usuarios
2/ Utilizar cookies para guardar las preferencias de usuario
3/ Utilizar JSP para mostrar las preferencias de usuario
4/ Utilizar un servlet para manejar el login y las preferencias de usuario
-------------------
Gestión de archivos: subida y descarga de img
1/ Implementar la funcionalidad de subida y descarga de img
2/ Utilizar un servlet para manejar la subida y descarga de img
3/ Utilizar JSP para mostrar el formulario de subida de img y los enlaces de descarga
-------------------
Acción de mi negocio reserva/alquiler de coches
1/ Implementar la funcionalidad de reserva y alquiler de coches
2/ Utilizar un servlet para manejar las reservas y alquileres de coches
3/ Utilizar JSP para mostrar el formulario de reserva y alquiler de coches
4/ Gestionar el estado de las reservas y alquileres en la base de datos
-------------------
Tecnologías utilizadas
Frontend: JSP, HTML, CSS, Bootstrap
Backend: Java Servlets (Jakarta EE)  
Base de datos: MySQL  
Servidor: Apache Tomcat 10  
Herramienta de compilación: Maven
------------------
a tener en cuenta:
aunque no inicie seccion uede cambiar el idioma
podra ver los coches pero no reservar ni alquilar si quiere reservar o alquilar debe iniciar sesion yo le mando una 
pantalla de login o registro para continuar al precionar el boton reservar o alquilar
-------------------
la diferencia entre reservar y alquilar: 
reserva es cuando el usuario aparta un coche pera una fecha futura pero ya lo paga en el momento de la reserva y cuando llega la fecha de la reserva establecida
pasa a ser un alquiler hasta la fecha de devolucion.
la gestion de las fechas de reserva y alquiler es importante para que no haya conflictos.
para gestionar las fechas de reserva y alquiler lo haremos de la siguiente manera:
con metodos en el servlet que gestionan las reservas y alquileres
que automaticamente cambian el estado del coche segun las fechas y hora establecidas
antes de llegar al alquiler debemos pasar por la reserva aunque lo quiera alquilar directamente.
lo gestionaremos de la manera: 
1/ el usuario selecciona un coche y una fecha de inicio y fin
2/ el sistema comprueba si el coche esta disponible en esas fechas
3/ si el coche esta disponible se crea una reserva y se cambia el estado del coche a reservado
4/ cuando llega la fecha de inicio de la reserva se cambia el estado del coche a alquilado
5/ cuando llega la fecha de fin del alquiler se cambia el estado del coche a disponible
-------------------
no debemos alquilar sin antes reservar 
un coche reservado no puede ser reservado por otro usuario
un coche alquilado no puede ser reservado ni alquilado por otro usuario
aunque lo muestro y dejo ver sus caracteristicas no se puede reservar ni alquilar si su estado es cualquier otro que no sea disponible
-------------------
Resumen de la aplicación web de reserva/alquiler de coches
1/ Crear una aplicación web para la reserva y alquiler de coches
2/ Implementar las funcionalidades de CRUD, búsqueda estructurada, edición de perfil, internacionalización, securización, validación, gestión de sesiones y cookies, gestión de archivos y acción de negocio
3/ Utilizar servlets y JSP para manejar las operaciones y mostrar las vistas
4/ Utilizar una base de datos para almacenar los datos de la aplicación
-------------------
importante: 
hay que autenticar al usuario antes de permitirle reservar o alquilar un coche 
si no esta autenticado lo redirijo a la pagina de login o registro

hay que autenticarte despues de login

autrizacion: solo los usuarios con rol de cliente pueden reservar o alquilar coches;
solo los empleados 
lo hacemos con un controlador


//authfilter
//obtrnrmos la url que deseo acceder
//obtengo a sesion sino crear una nueva 
//se no hay session hago uno anonimo
//chamada a metodo de autorizacion 
//chain.dofilter request response 

//inicio de deicion de filtrado
if else
//


----------------
contrullo la la autorizacion de cada rol
una lista de url autorizadas 



----------
recorro y verifico se la autorizaciony el rol con la url
//url enviado como parametro_url






-------------------

no hacer nunca: 
frond-controller
todo apunta al mismo controlador
no necesitas muchos is sino clases 
le llegan las peteciones y el saca los parametros que le llegan y el saca una clase de comando y hace dispacher


***************************








  




