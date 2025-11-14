package com.pinguela.rentexpressweb.controller;

import java.io.IOException;

import com.pinguela.rentexpressweb.constants.AppConstants;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Template servlet for private CRUD screens that only orchestrate the flow
 * between the view layer and the middleware services. Concrete servlets just
 * need to provide the specific list/form handling while this base class takes
 * care of resolving the requested action (list, create, update, delete) and the
 * common authentication/encoding concerns.
 */
public abstract class AbstractManagementServlet extends BasePrivateServlet {

    private static final long serialVersionUID = 1L;

    private enum CrudAction {
        CREATE, UPDATE, DELETE, LIST
    }

    private final String createAction;
    private final String updateAction;
    private final String deleteAction;

    /*
     * Configura las acciones CRUD personalizadas que gestionará el servlet hijo
     * para que la clase base pueda interpretar el flujo solicitado en cada
     * petición.
     */
    protected AbstractManagementServlet(String createAction, String updateAction, String deleteAction) {
        this.createAction = createAction;
        this.updateAction = updateAction;
        this.deleteAction = deleteAction;
    }

    @Override
    /*
     * Atiende las peticiones GET inicializando la codificación, comprobando la
     * autenticación y delegando en la acción correspondiente (listado o carga de
     * formulario).
     */
    protected final void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        configureEncoding(request, response);
        if (!ensureAuthenticated(request, response)) {
            return;
        }

        CrudAction action = resolveAction(param(request, AppConstants.ACTION));
        if (CrudAction.CREATE.equals(action)) {
            showForm(request, response, null);
            return;
        }
        if (CrudAction.UPDATE.equals(action)) {
            showForm(request, response, resolveEntityId(request));
            return;
        }

        loadList(request, response);
    }

    @Override
    /*
     * Gestiona las peticiones POST tras validar la sesión, dirigiendo la
     * operación al método específico de creación, actualización, borrado lógico o
     * recarga del listado.
     */
    protected final void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        configureEncoding(request, response);
        if (!ensureAuthenticated(request, response)) {
            return;
        }

        CrudAction action = resolveAction(param(request, AppConstants.ACTION));
        switch (action) {
        case CREATE:
            handleCreate(request, response);
            break;
        case UPDATE:
            handleUpdate(request, response);
            break;
        case DELETE:
            handleDelete(request, response);
            break;
        case LIST:
        default:
            loadList(request, response);
            break;
        }
    }

    /*
     * Traduce el parámetro de acción recibido en la petición a uno de los valores
     * del enumerado interno para unificar el tratamiento de las operaciones CRUD.
     */
    private CrudAction resolveAction(String actionParam) {
        if (createAction != null && createAction.equals(actionParam)) {
            return CrudAction.CREATE;
        }
        if (updateAction != null && updateAction.equals(actionParam)) {
            return CrudAction.UPDATE;
        }
        if (deleteAction != null && deleteAction.equals(actionParam)) {
            return CrudAction.DELETE;
        }
        return CrudAction.LIST;
    }

    /*
     * Extrae el identificador de la entidad que se desea modificar o eliminar a
     * partir de los parámetros de la petición.
     */
    protected abstract Integer resolveEntityId(HttpServletRequest request);

    /*
     * Carga los datos necesarios para mostrar el listado principal del recurso
     * gestionado por el servlet.
     */
    protected abstract void loadList(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException;

    /*
     * Prepara la información necesaria para renderizar el formulario de alta o
     * edición, incluyendo la entidad existente cuando procede.
     */
    protected abstract void showForm(HttpServletRequest request, HttpServletResponse response, Integer entityId)
            throws ServletException, IOException;

    /*
     * Ejecuta la lógica de creación de una nueva entidad gestionando la
     * validación y la comunicación con el middleware.
     */
    protected abstract void handleCreate(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException;

    /*
     * Orquesta la actualización de una entidad existente delegando en los
     * servicios y controlando los posibles errores de validación.
     */
    protected abstract void handleUpdate(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException;

    /*
     * Coordina el borrado lógico de la entidad solicitada asegurando la correcta
     * respuesta al usuario.
     */
    protected abstract void handleDelete(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException;
}
