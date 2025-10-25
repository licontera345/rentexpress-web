# RentExpress Web Middleware Guidelines

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

Default: If no match is found, store the default language (for example, `en`) in the session.

Persistence: When a user explicitly selects a language, save it to a cookie with an expiration date for future visits.

Additional features and practices
CRUD and views: Implement CRUD operations (create, read, update, delete) for the primary entities (for example, vehicles and products), including images. JSPs should render these entities and their photos.

Profile editing: Users can update their profile information, including uploading an avatar photo.

File management: The system handles uploading and downloading images (vehicle photos, avatars).

Search and pagination: Implement structured search (with filters) and pagination for entity listings.

Avoid literals (“magic strings”): Request parameter names, session attributes, and similar values should not be hard-coded. Declare them as `private static final String` constants at the start of the servlet class.

Cache control: Configure HTTP headers in servlets (`Cache-Control: no-cache, no-store`, `Expires: 0`) to prevent the browser from caching dynamic or private pages.

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
