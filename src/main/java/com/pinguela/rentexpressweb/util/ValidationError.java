package com.pinguela.rentexpressweb.util;

import java.io.Serializable;

/**
 * Representa un error de validación asociado a un campo concreto del formulario.
 * Se almacena el identificador del campo y el mensaje localizado para poder
 * mostrarlo en la vista sin depender de estructuras basadas en mapas.
 */
public final class ValidationError implements Serializable {

    private static final long serialVersionUID = 1L;

    private final String field;
    private final String message;

    /*
     * Crea una nueva instancia indicando el campo afectado y el mensaje que se
     * deberá mostrar al usuario.
     */
    public ValidationError(String field, String message) {
        this.field = field;
        this.message = message;
    }

    /*
     * Devuelve el identificador del campo asociado al error. Puede ser nulo cuando
     * el mensaje no corresponde a un campo específico.
     */
    public String getField() {
        return field;
    }

    /*
     * Recupera el mensaje localizado que describe el problema detectado.
     */
    public String getMessage() {
        return message;
    }
}
