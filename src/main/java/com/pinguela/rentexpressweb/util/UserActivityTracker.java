package com.pinguela.rentexpressweb.util;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import com.pinguela.rentexpressweb.constants.UserConstants;
import com.pinguela.rentexpressweb.security.SessionManager;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

/**
 * Utilidad para mantener un pequeño historial de actividad del usuario en la sesión.
 */
public final class UserActivityTracker {

    private static final int MAX_ACTIVITY_ITEMS = 10;

    private UserActivityTracker() {
    }

    public static void record(HttpServletRequest request, String messageKey, Object... messageArguments) {
        record(request, messageKey, null, messageArguments);
    }

    public static void record(HttpServletRequest request, String messageKey, String icon,
            Object... messageArguments) {
        if (request == null || messageKey == null) {
            return;
        }

        HttpSession session = request.getSession();
        @SuppressWarnings("unchecked")
        List<ActivityEntry> stored = (List<ActivityEntry>) session
                .getAttribute(UserConstants.ATTR_ACTIVITY_LOG);
        if (stored == null) {
            stored = new LinkedList<ActivityEntry>();
            session.setAttribute(UserConstants.ATTR_ACTIVITY_LOG, stored);
        }

        ActivityEntry entry = new ActivityEntry(messageKey, icon, new Date(), messageArguments);
        stored.add(0, entry);
        while (stored.size() > MAX_ACTIVITY_ITEMS) {
            stored.remove(stored.size() - 1);
        }
    }

    public static List<ActivityEntry> getRecentActivities(HttpServletRequest request) {
        if (request == null) {
            return new ArrayList<ActivityEntry>();
        }
        HttpSession session = SessionManager.getSession(request);
        if (session == null) {
            return new ArrayList<ActivityEntry>();
        }
        @SuppressWarnings("unchecked")
        List<ActivityEntry> stored = (List<ActivityEntry>) session
                .getAttribute(UserConstants.ATTR_ACTIVITY_LOG);
        if (stored == null || stored.isEmpty()) {
            return new ArrayList<ActivityEntry>();
        }
        return new ArrayList<ActivityEntry>(stored);
    }

    public static final class ActivityEntry {
        private final String messageKey;
        private final String icon;
        private final Date timestamp;
        private final Object[] messageArguments;

        private ActivityEntry(String messageKey, String icon, Date timestamp, Object[] messageArguments) {
            this.messageKey = messageKey;
            this.icon = icon;
            this.timestamp = timestamp;
            if (messageArguments == null || messageArguments.length == 0) {
                this.messageArguments = new Object[0];
            } else {
                this.messageArguments = new Object[messageArguments.length];
                System.arraycopy(messageArguments, 0, this.messageArguments, 0, messageArguments.length);
            }
        }

        public String getMessageKey() {
            return messageKey;
        }

        public String getIcon() {
            return icon;
        }

        public Date getTimestamp() {
            return timestamp;
        }

        public Object[] getMessageArguments() {
            Object[] copy = new Object[messageArguments.length];
            System.arraycopy(messageArguments, 0, copy, 0, messageArguments.length);
            return copy;
        }
    }
}
