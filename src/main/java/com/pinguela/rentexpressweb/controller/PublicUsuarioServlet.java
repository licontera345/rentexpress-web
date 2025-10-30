package com.pinguela.rentexpressweb.controller;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import com.pinguela.rentexpres.model.UsuarioDTO;
import com.pinguela.rentexpres.service.UsuarioService;
import com.pinguela.rentexpres.service.impl.UsuarioServiceImpl;
import com.pinguela.rentexpressweb.util.SessionManager;
import com.pinguela.rentexpressweb.util.Views;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * Servlet implementation class UsuarioServlet
 */
@WebServlet("/public/UsuarioServlet")
public class PublicUsuarioServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private UsuarioService usuarioService = new UsuarioServiceImpl();

	/**
	 * Default constructor.
	 */
	public PublicUsuarioServlet() {
		usuarioService = new UsuarioServiceImpl();

	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		// 🔸 Restaurar idioma desde Cookie si existe
		Cookie[] cookies = request.getCookies();
		if (cookies != null) {
			for (Cookie c : cookies) {
				if ("locale".equals(c.getName())) {
					request.getSession().setAttribute("locale", new Locale(c.getValue()));
				}
			}
		}

		String action = request.getParameter("action");
		String destination = Views.INDEX;

		try {
			if ("changeLocale".equals(action)) {
				// 🔸 Cambiar idioma y guardarlo en Cookie
				String idioma = request.getParameter("lenguage");
				if (idioma != null && !idioma.trim().isEmpty()) {
					Locale locale = new Locale(idioma);
					request.getSession().setAttribute("locale", locale);

					Cookie cookie = new Cookie("locale", idioma);
					cookie.setMaxAge(60 * 60 * 24 * 30); // 30 días
					cookie.setPath(request.getContextPath());
					response.addCookie(cookie);
				}
				destination = Views.INDEX;

			} else if ("logout".equals(action)) {
				SessionManager.logout(request);
				destination = Views.LOGIN;

			} else if ("detail".equals(action)) {
				int id = Integer.parseInt(request.getParameter("id"));
				UsuarioDTO usuario = usuarioService.findById(id);
				request.setAttribute("usuario", usuario);
				destination = Views.USUARIO_DETAIL;

			} else if ("list".equals(action)) {
				List<UsuarioDTO> usuarios = usuarioService.findAll();
				request.setAttribute("usuarios", usuarios);
				destination = Views.USUARIO_LIST;

			} else if ("create".equals(action)) {
				destination = Views.USUARIO_FORM;

			} else if ("edit".equals(action)) {
				int id = Integer.parseInt(request.getParameter("id"));
				UsuarioDTO usuario = usuarioService.findById(id);
				request.setAttribute("usuario", usuario);
				destination = Views.USUARIO_FORM;

			} else if ("delete".equals(action)) {
				int id = Integer.parseInt(request.getParameter("id"));
				usuarioService.delete(null, id); // no modificamos DAO/Service
				destination = "/public/UsuarioServlet?action=list";
			}

		} catch (Exception e) {
			e.printStackTrace();
			destination = Views.ERROR;
		}

		request.getRequestDispatcher(destination).forward(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		String action = request.getParameter("action");
		String destination = Views.INDEX;

		try {
			if ("login".equals(action)) {
				String username = request.getParameter("username");
				String password = request.getParameter("password");
				String remember = request.getParameter("remember");

				UsuarioDTO usuario = usuarioService.autenticar(username, password);

				if (usuario != null) {
					HttpSession session = request.getSession();
					session.setAttribute("usuario", usuario);

					// 🔸 Guardar Cookie si se seleccionó "Recordar usuario"
					if ("yes".equals(remember)) {
						Cookie cookieUser = new Cookie("rememberUser", username);
						cookieUser.setMaxAge(60 * 60 * 24 * 7); // 7 días
						cookieUser.setPath(request.getContextPath());
						response.addCookie(cookieUser);
					} else {
						// Si no marcó recordar, eliminar Cookie previa si existe
						Cookie cookieUser = new Cookie("rememberUser", "");
						cookieUser.setMaxAge(0);
						cookieUser.setPath(request.getContextPath());
						response.addCookie(cookieUser);
					}

					// 🔸 Configurar idioma por defecto si no hay Cookie
					Locale locale = (Locale) session.getAttribute("locale");
					if (locale == null) {
						session.setAttribute("locale", new Locale("es"));
					}

					destination = Views.INDEX;
				} else {
					request.setAttribute("error", "Usuario o contraseña incorrectos");
					destination = Views.LOGIN;
				}

			} else if ("save".equals(action)) {
				String nombre = request.getParameter("nombreUsuario");
				String email = request.getParameter("email");

				UsuarioDTO nuevo = new UsuarioDTO();
				nuevo.setNombreUsuario(nombre);
				nuevo.setEmail(email);

				usuarioService.create(null);
				destination = "/public/UsuarioServlet?action=list";

			} else if ("update".equals(action)) {
				int id = Integer.parseInt(request.getParameter("id"));
				String nombre = request.getParameter("nombreUsuario");
				String email = request.getParameter("email");

				UsuarioDTO usuario = usuarioService.findById(id);
				usuario.setNombreUsuario(nombre);
				usuario.setEmail(email);

				usuarioService.update(null);
				destination = "/public/UsuarioServlet?action=list";
			}

		} catch (Exception e) {
			e.printStackTrace();
			destination = Views.ERROR;
		}

		request.getRequestDispatcher(destination).forward(request, response);
	}
}