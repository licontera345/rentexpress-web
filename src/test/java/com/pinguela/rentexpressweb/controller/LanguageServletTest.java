package com.pinguela.rentexpressweb.controller;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Locale;

import org.junit.Test;
import org.mockito.ArgumentCaptor;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

public class LanguageServletTest {

        @Test
        public void changeLocaleShouldAcceptLanguageTagWithRegion() throws Exception {
                LanguageServlet servlet = new LanguageServlet();

                HttpServletRequest request = mock(HttpServletRequest.class);
                HttpServletResponse response = mock(HttpServletResponse.class);
                HttpSession session = mock(HttpSession.class);

                when(request.getSession()).thenReturn(session);
                when(request.getParameter("action")).thenReturn("changeLocale");
                when(request.getParameter("language")).thenReturn("es-ES");
                when(request.getContextPath()).thenReturn("/rentexpress");
                when(request.getParameter("redirect")).thenReturn(null);
                when(request.getHeader("Referer")).thenReturn(null);

                servlet.doGet(request, response);

                ArgumentCaptor<Locale> localeCaptor = ArgumentCaptor.forClass(Locale.class);
                verify(session).setAttribute(eq("locale"), localeCaptor.capture());
                assertEquals("es", localeCaptor.getValue().getLanguage());

                ArgumentCaptor<Cookie> cookieCaptor = ArgumentCaptor.forClass(Cookie.class);
                verify(response).addCookie(cookieCaptor.capture());
                Cookie cookie = cookieCaptor.getValue();
                assertEquals("locale", cookie.getName());
                assertEquals("es", cookie.getValue());

                verify(response).sendRedirect("/rentexpress/public/index.jsp");
        }
}
