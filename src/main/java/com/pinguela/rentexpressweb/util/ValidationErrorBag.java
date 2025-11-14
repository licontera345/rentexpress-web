package com.pinguela.rentexpressweb.util;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Contenedor ligero de errores de validación que permite acumular mensajes por
 * campo sin utilizar mapas y acceder directamente al texto localizado asociado
 * a cada identificador.
 */
public final class ValidationErrorBag implements Serializable {

    private static final long serialVersionUID = 1L;

    private final List<ValidationError> errors = new ArrayList<>();

    /*
     * Añade un nuevo error indicando el campo asociado (puede ser nulo) y el
     * mensaje localizado que se debe mostrar.
     */
    public void add(String field, String message) {
        errors.add(new ValidationError(field, message));
    }

    /*
     * Indica si no existe ningún error registrado en el contenedor.
     */
    public boolean isEmpty() {
        return errors.isEmpty();
    }

    /*
     * Devuelve una vista inmodificable de todos los errores para facilitar la
     * iteración desde la capa de presentación.
     */
    public List<ValidationError> getAll() {
        return Collections.unmodifiableList(errors);
    }

    /*
     * Busca el primer error asociado al campo indicado y devuelve únicamente su
     * mensaje localizado. Si no se encuentra coincidencia se devuelve null.
     */
    public String getMessage(String field) {
        if (field == null) {
            return null;
        }
        for (ValidationError error : errors) {
            if (error.getField() != null && error.getField().equals(field)) {
                return error.getMessage();
            }
        }
        return null;
    }

    /*
     * Permite comprobar si existe algún error asociado al campo indicado.
     */
    public boolean hasError(String field) {
        if (field == null) {
            return false;
        }
        for (ValidationError error : errors) {
            if (error.getField() != null && error.getField().equals(field)) {
                return true;
            }
        }
        return false;
    }

}
