package com.pinguela.rentexpressweb.controller;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import com.pinguela.rentexpressweb.util.Views;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * Servlet dedicated to handling language resolution and user locale changes.
 */
@WebServlet({"/public/LanguageServlet", "/public/language"})
public class LanguageServlet extends HttpServlet {

        private static final long serialVersionUID = 1L;

        private static final List<Locale> SUPPORTED_LOCALES = Arrays.asList(
                        Locale.forLanguageTag("es"),
                        Locale.forLanguageTag("en"),
                        Locale.forLanguageTag("fr"));

        private static final Locale DEFAULT_LOCALE = Locale.forLanguageTag("es");

        @Override
        protected void doGet(HttpServletRequest request, HttpServletResponse response)
                        throws ServletException, IOException {

                HttpSession session = request.getSession();
                String action = request.getParameter("action");

                if ("changeLocale".equals(action)) {
                        handleLocaleChange(request, response, session);
                        redirectToPreviousPage(request, response);
                        return;
                }

                Locale currentLocale = (Locale) session.getAttribute("locale");
                if (currentLocale == null) {
                        Locale detectedLocale = resolveLocaleFromCookie(request);
                        if (detectedLocale == null) {
                                detectedLocale = resolveLocaleFromHeader(request);
                        }
                        session.setAttribute("locale", detectedLocale != null ? detectedLocale : DEFAULT_LOCALE);
                }

                if (!isIncludeRequest(request)) {
                        redirectToPreviousPage(request, response);
                }
        }

        private void handleLocaleChange(HttpServletRequest request, HttpServletResponse response, HttpSession session) {
                String language = request.getParameter("language");
                if (language == null || language.trim().isEmpty()) {
                        return;
                }

                Locale requestedLocale = Locale.forLanguageTag(language);
                String requestedLanguage = requestedLocale.getLanguage();
                if (requestedLanguage == null || requestedLanguage.isEmpty()) {
                        return;
                }

                Locale matchedLocale = SUPPORTED_LOCALES.stream()
                                .filter(locale -> locale.getLanguage().equalsIgnoreCase(requestedLanguage))
                                .findFirst()
                                .orElse(null);

                if (matchedLocale == null) {
                        return;
                }

                session.setAttribute("locale", matchedLocale);

                Cookie cookie = new Cookie("locale", matchedLocale.getLanguage());
                cookie.setMaxAge(60 * 60 * 24 * 30);
                String contextPath = request.getContextPath();
                cookie.setPath(contextPath == null || contextPath.isEmpty() ? "/" : contextPath);
                response.addCookie(cookie);
        }

        private Locale resolveLocaleFromCookie(HttpServletRequest request) {
                Cookie[] cookies = request.getCookies();
                if (cookies == null) {
                        return null;
                }

                for (Cookie cookie : cookies) {
                        if ("locale".equals(cookie.getName())) {
                                Locale locale = Locale.forLanguageTag(cookie.getValue());
                                if (SUPPORTED_LOCALES.contains(locale)) {
                                        return locale;
                                }
                                break;
                        }
                }

                return null;
        }

        private Locale resolveLocaleFromHeader(HttpServletRequest request) {
                String acceptLanguage = request.getHeader("Accept-Language");
                if (acceptLanguage == null || acceptLanguage.isEmpty()) {
                        return DEFAULT_LOCALE;
                }

                try {
                        List<Locale.LanguageRange> languageRanges = Locale.LanguageRange.parse(acceptLanguage);
                        Locale matchedLocale = Locale.lookup(languageRanges, SUPPORTED_LOCALES);
                        return matchedLocale != null ? matchedLocale : DEFAULT_LOCALE;
                } catch (IllegalArgumentException ex) {
                        return DEFAULT_LOCALE;
                }
        }

        private void redirectToPreviousPage(HttpServletRequest request, HttpServletResponse response) throws IOException {
                if (isIncludeRequest(request)) {
                        return;
                }

                String redirect = request.getParameter("redirect");
                if (redirect != null && !redirect.isEmpty()) {
                        response.sendRedirect(redirect);
                        return;
                }

                String referer = request.getHeader("Referer");
                if (referer != null && !referer.isEmpty()) {
                        response.sendRedirect(referer);
                } else {
                        response.sendRedirect(request.getContextPath() + Views.INDEX);
                }
        }

        private boolean isIncludeRequest(HttpServletRequest request) {
                return request.getAttribute(RequestDispatcher.INCLUDE_REQUEST_URI) != null;
        }
}
