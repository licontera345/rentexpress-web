package com.pinguela.rentexpressweb.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.junit.Test;

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
}
