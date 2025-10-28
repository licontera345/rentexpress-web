package com.pinguela.rentexpressweb.controller;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import com.pinguela.rentexpres.model.UserDTO;
import com.pinguela.rentexpres.service.FileService;
import com.pinguela.rentexpres.service.UserService;
import com.pinguela.rentexpres.service.impl.FileServiceImpl;
import com.pinguela.rentexpres.service.impl.UserServiceImpl;
import com.pinguela.rentexpressweb.security.SessionManager;
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

	private UserService usuarioService = new UserServiceImpl();
	private FileService fileService = new FileServiceImpl();

	public PublicUsuarioServlet() {
		usuarioService = new UserServiceImpl();
		fileService = new FileServiceImpl();
	}

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		// Restaurar idioma desde Cookie si existe
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
				String idioma = request.getParameter("lenguage");
				if (idioma != null && !idioma.trim().isEmpty()) {
					Locale locale = new Locale(idioma);
					request.getSession().setAttribute("locale", locale);

					Cookie cookie = new Cookie("locale", idioma);
					cookie.setMaxAge(60 * 60 * 24 * 30);
					cookie.setPath(request.getContextPath());
					response.addCookie(cookie);
				}
				destination = Views.INDEX;

			} else if ("logout".equals(action)) {
				SessionManager.logout(request);
				destination = Views.LOGIN;

			} else if ("detail".equals(action)) {
				int id = Integer.parseInt(request.getParameter("id"));
				UserDTO usuario = usuarioService.findById(id);
				request.setAttribute("usuario", usuario);

				List<String> imagenes = (List<String>) fileService.getImageByUserId(id);

				request.setAttribute("tieneImagen", imagenes != null && !imagenes.isEmpty());
				request.setAttribute("imagenes", imagenes);

				destination = Views.USUARIO_DETAIL;

			} else if ("list".equals(action)) {
				List<UserDTO> usuarios = usuarioService.findAll();
				request.setAttribute("usuarios", usuarios);
				destination = Views.USUARIO_LIST;

			} else if ("create".equals(action)) {
				destination = Views.USUARIO_FORM;

			} else if ("edit".equals(action)) {
				int id = Integer.parseInt(request.getParameter("id"));
				UserDTO usuario = usuarioService.findById(id);
				request.setAttribute("usuario", usuario);
				destination = Views.USUARIO_FORM;

			} else if ("delete".equals(action)) {
				usuarioService.delete(null);
				destination = "/public/UsuarioServlet?action=list";

			} else if ("index".equals(action) || action == null) {
				destination = Views.INDEX;
			}

		} catch (Exception e) {
			e.printStackTrace();
			destination = Views.ERROR;
		}

		request.getRequestDispatcher(destination).forward(request, response);
	}

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

				UserDTO usuario = usuarioService.authenticate(username, password);

				if (usuario != null) {
					HttpSession session = request.getSession();
					session.setAttribute("usuario", usuario);

					if ("yes".equals(remember)) {
						Cookie cookieUser = new Cookie("rememberUser", username);
						cookieUser.setMaxAge(60 * 60 * 24 * 7);
						cookieUser.setPath(request.getContextPath());
						response.addCookie(cookieUser);
					} else {
						Cookie cookieUser = new Cookie("rememberUser", "");
						cookieUser.setMaxAge(0);
						cookieUser.setPath(request.getContextPath());
						response.addCookie(cookieUser);
					}

					Locale locale = (Locale) session.getAttribute("locale");
					if (locale == null)
						session.setAttribute("locale", new Locale("es"));

					destination = Views.INDEX;
				} else {
					request.setAttribute("error", "Usuario o contraseña incorrectos");
					destination = Views.LOGIN;
				}

			} else if ("save".equals(action)) {
				String nombre = request.getParameter("nombreUsuario");
				String email = request.getParameter("email");

				UserDTO nuevo = new UserDTO();
				nuevo.setUsername(nombre);
				nuevo.setEmail(email);

				usuarioService.create(null);
				destination = "/public/UsuarioServlet?action=list";

			} else if ("update".equals(action)) {
				int id = Integer.parseInt(request.getParameter("id"));
				String nombre = request.getParameter("nombreUsuario");
				String email = request.getParameter("email");

				UserDTO usuario = usuarioService.findById(id);
				usuario.setUsername(nombre);
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
