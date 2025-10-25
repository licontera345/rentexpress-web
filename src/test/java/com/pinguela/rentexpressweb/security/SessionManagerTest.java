package com.pinguela.rentexpressweb.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class SessionManagerTest {

        @Test
        public void logoutShouldInvalidateExistingSession() {
                HttpServletRequest request = mock(HttpServletRequest.class);
                HttpSession session = mock(HttpSession.class);

                when(request.getSession(false)).thenReturn(session);

                SessionManager.logout(request);

                verify(request).getSession(false);
                verify(session).invalidate();
        }

        @Test
        public void logoutShouldNotInvalidateWhenNoSessionExists() {
                HttpServletRequest request = mock(HttpServletRequest.class);

                when(request.getSession(false)).thenReturn(null);

                SessionManager.logout(request);

                verify(request).getSession(false);
                verifyNoMoreInteractions(request);
        }

        @Test
        public void setAttributeShouldStoreValueInSession() {
                HttpServletRequest request = mock(HttpServletRequest.class);
                HttpSession session = mock(HttpSession.class);

                when(request.getSession()).thenReturn(session);

                SessionManager.setAttribute(request, "user", "value");

                verify(request).getSession();
                verify(session).setAttribute("user", "value");
                verifyNoMoreInteractions(session);
        }

        @Test
        public void getAttributeShouldReturnValueWhenSessionExists() {
                HttpServletRequest request = mock(HttpServletRequest.class);
                HttpSession session = mock(HttpSession.class);

                when(request.getSession(false)).thenReturn(session);
                when(session.getAttribute("user")).thenReturn("value");

                Object result = SessionManager.getAttribute(request, "user");

                assertEquals("value", result);
                verify(request).getSession(false);
                verify(session).getAttribute("user");
                verifyNoMoreInteractions(session);
        }

        @Test
        public void getAttributeShouldReturnNullWhenNoSessionExists() {
                HttpServletRequest request = mock(HttpServletRequest.class);

                when(request.getSession(false)).thenReturn(null);

                Object result = SessionManager.getAttribute(request, "user");

                assertNull(result);
                verify(request).getSession(false);
                verify(request, never()).getSession();
                verifyNoMoreInteractions(request);
        }

        @Test
        public void removeAttributeShouldRemoveValueWhenSessionExists() {
                HttpServletRequest request = mock(HttpServletRequest.class);
                HttpSession session = mock(HttpSession.class);

                when(request.getSession(false)).thenReturn(session);

                SessionManager.removeAttribute(request, "user");

                verify(request).getSession(false);
                verify(session).removeAttribute("user");
                verifyNoMoreInteractions(session);
        }

        @Test
        public void removeAttributeShouldNotCreateSessionWhenNoneExists() {
                HttpServletRequest request = mock(HttpServletRequest.class);

                when(request.getSession(false)).thenReturn(null);

                SessionManager.removeAttribute(request, "user");

                verify(request).getSession(false);
                verify(request, never()).getSession();
                verifyNoMoreInteractions(request);
        }
}
