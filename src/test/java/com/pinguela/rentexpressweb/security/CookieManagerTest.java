package com.pinguela.rentexpressweb.security;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class CookieManagerTest {

    private CookieManager cookieManager;

    @Before
    public void setUp() {
        cookieManager = new CookieManager();
    }

    @Test
    public void testSetCookieConfiguresCookieCorrectly() {
        HttpServletResponse response = mock(HttpServletResponse.class);
        ArgumentCaptor<Cookie> cookieCaptor = ArgumentCaptor.forClass(Cookie.class);

        cookieManager.setCookie(response, "session", "abc123", 3600, "/app");

        verify(response).addCookie(cookieCaptor.capture());
        Cookie cookie = cookieCaptor.getValue();

        assertEquals("session", cookie.getName());
        assertEquals("abc123", cookie.getValue());
        assertEquals(3600, cookie.getMaxAge());
        assertEquals("/app", cookie.getPath());
    }

    @Test
    public void testRemoveCookieConfiguresCookieDeletion() {
        HttpServletResponse response = mock(HttpServletResponse.class);
        ArgumentCaptor<Cookie> cookieCaptor = ArgumentCaptor.forClass(Cookie.class);

        boolean removed = cookieManager.removeCookie(response, "session", "/app");

        assertTrue(removed);
        verify(response).addCookie(cookieCaptor.capture());
        Cookie cookie = cookieCaptor.getValue();

        assertEquals("session", cookie.getName());
        assertEquals("", cookie.getValue());
        assertEquals(0, cookie.getMaxAge());
        assertEquals("/app", cookie.getPath());
    }

    @Test
    public void testGetCookieValueReturnsNullWhenCookieMissing() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getCookies()).thenReturn(null);

        String value = cookieManager.getCookieValue(request, "nonexistent");

        assertNull(value);
    }

    @Test
    public void testGetCookieValueReturnsCookieValueWhenPresent() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        Cookie targetCookie = new Cookie("session", "abc123");
        Cookie[] cookies = new Cookie[]{new Cookie("other", "value"), targetCookie};
        when(request.getCookies()).thenReturn(cookies);

        String value = cookieManager.getCookieValue(request, "session");

        assertEquals("abc123", value);
    }
}
