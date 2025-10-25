package com.pinguela.rentexpressweb.filter;

import java.io.IOException;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * Servlet Filter implementation class AuthFilter
 */
@WebFilter("/private/*")
public class AuthFilter extends HttpFilter implements Filter {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpFilter#HttpFilter()
	 */
	public AuthFilter() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see Filter#doFilter(ServletRequest, ServletResponse, FilterChain)
	 */
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {

		HttpServletRequest req = (HttpServletRequest) request;
		HttpServletResponse res = (HttpServletResponse) response;

		String uri = req.getRequestURI();
		HttpSession session = req.getSession(false);

		boolean usuarioLogueado = (session != null && session.getAttribute("usuario") != null);

		// Rutas públicas
		boolean esPublica = uri.contains("/public/") || uri.endsWith("login.jsp") || uri.endsWith("UsuarioServlet")
				&& req.getParameter("action") != null
				&& (req.getParameter("action").equals("login") || req.getParameter("action").equals("changeLocale"));

		// Recursos estáticos (CSS, JS, imágenes)
		boolean esRecursoEstatico = uri.contains("/css/") || uri.contains("/js/") || uri.contains("/images/");

		// Lógica de acceso
		if (usuarioLogueado || esPublica || esRecursoEstatico) {
			chain.doFilter(request, response);
		} else {
			// Si intenta acceder a algo privado sin login → redirige al login
			res.sendRedirect(req.getContextPath() + "/public/usuario/login.jsp");
		}
	}

	/**
	 * @see Filter#destroy()
	 */
	public void destroy() {
		// TODO Auto-generated method stub
	}

	/**
	 * @see Filter#init(FilterConfig)
	 */
	public void init(FilterConfig fConfig) throws ServletException {
		// TODO Auto-generated method stub
	}

}
