package com.pinguela.rentexpressweb.util;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Date;

/**
 * Entrada de actividad mostrada en el dashboard privado.
 */
public final class ActivityEntry implements Serializable {

    private static final long serialVersionUID = 1L;

    private final String messageKey;
    private final String icon;
    private final Date timestamp;
    private final Object[] messageArguments;

    ActivityEntry(String messageKey, String icon, Date timestamp, Object[] messageArguments) {
        this.messageKey = messageKey;
        this.icon = icon;
        this.timestamp = timestamp != null ? new Date(timestamp.getTime()) : null;
        if (messageArguments == null || messageArguments.length == 0) {
            this.messageArguments = new Object[0];
        } else {
            this.messageArguments = Arrays.copyOf(messageArguments, messageArguments.length);
        }
    }

    public String getMessageKey() {
        return messageKey;
    }

    public String getIcon() {
        return icon;
    }

    public Date getTimestamp() {
        return timestamp != null ? new Date(timestamp.getTime()) : null;
    }

    public Object[] getMessageArguments() {
        return Arrays.copyOf(messageArguments, messageArguments.length);
    }
}
