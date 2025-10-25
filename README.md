# RentExpress Web Middleware Guidelines

Antes de implementar nuevos servlets, servicios u otros componentes dentro del proyecto **rentexpress-web**, revisa primero el contenido del middleware empaquetado en [`src/main/webapp/WEB-INF/lib/RentExpres.jar`](src/main/webapp/WEB-INF/lib/RentExpres.jar).

1. Extrae o lista los recursos existentes (por ejemplo con `jar tf src/main/webapp/WEB-INF/lib/RentExpres.jar`).
2. Comprueba si ya existen los *services*, *DAOs* u otras clases que necesitas reutilizar.
3. Si el JAR expone la clase en formato `.java`, abre el archivo y revisa cómo funcionan exactamente los métodos implicados.
4. Verifica que el método que deseas usar realmente existe en el middleware; si no existe, detén la implementación hasta definir una alternativa con el equipo.
5. Si el método existe, replica fielmente su comportamiento en tu servlet o componente para mantener la coherencia funcional.

Seguir este flujo asegura que las funcionalidades nuevas aprovechen la base ya implementada en el middleware y que la información mostrada en la aplicación esté alineada con los servicios disponibles.
