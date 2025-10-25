package com.pinguela.rentexpressweb.controller;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import com.pinguela.rentexpres.model.EmployeeDTO;
import com.pinguela.rentexpres.model.Results;
import com.pinguela.rentexpres.model.VehicleCriteria;
import com.pinguela.rentexpres.model.VehicleDTO;
import com.pinguela.rentexpres.service.EmployeeService;
import com.pinguela.rentexpres.service.FileService;
import com.pinguela.rentexpres.service.VehicleService;
import com.pinguela.rentexpres.service.impl.EmployeeServiceImpl;
import com.pinguela.rentexpres.service.impl.FileServiceImpl;
import com.pinguela.rentexpres.service.impl.VehicleServiceImpl;
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
 * Servlet for public Employee operations (login, list, view, etc.)
 */
@WebServlet("/public/EmployeeServlet")
public class PublicEmployeeServlet extends HttpServlet {

        private static final long serialVersionUID = 1L;

        private final EmployeeService employeeService;
        private final FileService fileService;
        private final VehicleService vehicleService;

        public PublicEmployeeServlet() {
                this.employeeService = new EmployeeServiceImpl();
                this.fileService = new FileServiceImpl();
                this.vehicleService = new VehicleServiceImpl();
        }

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		// Restore locale from cookie if exists
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
				String language = request.getParameter("language");
				if (language != null && !language.trim().isEmpty()) {
					Locale locale = new Locale(language);
					request.getSession().setAttribute("locale", locale);

					Cookie cookie = new Cookie("locale", language);
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
				EmployeeDTO employee = employeeService.findById(id);
				request.setAttribute("employee", employee);

				File image = fileService.getImageByEmployeeId(id);
				request.setAttribute("hasImage", image != null);
				request.setAttribute("image", image);

				destination = Views.EMPLOYEE_DETAIL;

			} else if ("list".equals(action)) {
				List<EmployeeDTO> employees = employeeService.findAll();
				request.setAttribute("employees", employees);
				destination = Views.EMPLOYEE_LIST;

			} else if ("create".equals(action)) {
				destination = Views.EMPLOYEE_FORM;

			} else if ("edit".equals(action)) {
				int id = Integer.parseInt(request.getParameter("id"));
				EmployeeDTO employee = employeeService.findById(id);
				request.setAttribute("employee", employee);
				destination = Views.EMPLOYEE_FORM;

			} else if ("deactivate".equals(action)) {
				int id = Integer.parseInt(request.getParameter("id"));
				employeeService.deactivate(id);
				destination = "/public/EmployeeServlet?action=list";

			} else if ("index".equals(action) || action == null) {
				destination = Views.INDEX;
			}

		} catch (Exception e) {
			e.printStackTrace();
			destination = Views.ERROR;
		}

                if (Views.INDEX.equals(destination)) {
                        loadHomeData(request);
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

				EmployeeDTO employee = employeeService.authenticate(username, password);

				if (employee != null && employee.getActiveStatus() == 1) {
					HttpSession session = request.getSession();
					session.setAttribute("employee", employee);

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
                                        request.setAttribute("error", "Invalid credentials or inactive account");
                                        destination = Views.LOGIN;
                                }

			} else if ("save".equals(action)) {
				String username = request.getParameter("username");
				String email = request.getParameter("email");
				String password = request.getParameter("password");

				EmployeeDTO newEmployee = new EmployeeDTO();
				newEmployee.setEmployeeName(username);
				newEmployee.setEmail(email);
				newEmployee.setPassword(password);
				newEmployee.setActiveStatus(1);

				employeeService.create(newEmployee);
				destination = "/public/EmployeeServlet?action=list";

			} else if ("update".equals(action)) {
				int id = Integer.parseInt(request.getParameter("id"));
				String username = request.getParameter("username");
				String email = request.getParameter("email");

				EmployeeDTO employee = employeeService.findById(id);
				employee.setEmployeeName(username);
				employee.setEmail(email);

				employeeService.update(employee);
				destination = "/public/EmployeeServlet?action=list";
			}

                } catch (Exception e) {
                        e.printStackTrace();
                        destination = Views.ERROR;
                }

                if (Views.INDEX.equals(destination)) {
                        loadHomeData(request);
                }

                request.getRequestDispatcher(destination).forward(request, response);
        }

        private void loadHomeData(HttpServletRequest request) {
                try {
                        VehicleCriteria criteria = new VehicleCriteria();
                        criteria.setPageNumber(1);
                        criteria.setPageSize(Integer.valueOf(4));
                        criteria.setOrderBy("created_at");
                        criteria.setOrderDir("DESC");

                        Results<VehicleDTO> results = vehicleService.findByCriteria(criteria);
                        List<VehicleDTO> vehicles = (results != null && results.getResults() != null)
                                        ? results.getResults()
                                        : Collections.emptyList();

                        Map<Integer, Boolean> vehicleImages = new HashMap<>();
                        for (VehicleDTO vehicle : vehicles) {
                                if (vehicle == null || vehicle.getVehicleId() == null) {
                                        continue;
                                }

                                List<File> images = fileService.getImagesByVehicleId(vehicle.getVehicleId());
                                if (images != null && !images.isEmpty()) {
                                        vehicleImages.put(vehicle.getVehicleId(), Boolean.TRUE);
                                }
                        }

                        request.setAttribute("featuredVehicles", vehicles);
                        request.setAttribute("featuredVehicleImages", vehicleImages);
                } catch (Exception e) {
                        e.printStackTrace();
                        request.setAttribute("featuredVehicles", Collections.emptyList());
                        request.setAttribute("featuredVehicleImages", Collections.emptyMap());
                        request.setAttribute("featuredVehiclesError", Boolean.TRUE);
                }
        }
}
