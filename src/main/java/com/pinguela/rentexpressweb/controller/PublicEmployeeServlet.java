package com.pinguela.rentexpressweb.controller;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import com.pinguela.rentexpres.dao.HeadquartersDAO;
import com.pinguela.rentexpres.dao.impl.HeadquartersDAOImpl;
import com.pinguela.rentexpres.exception.RentexpresException;
import com.pinguela.rentexpres.model.EmployeeDTO;
import com.pinguela.rentexpres.model.HeadquartersDTO;
import com.pinguela.rentexpres.model.RoleDTO;
import com.pinguela.rentexpres.service.EmployeeService;
import com.pinguela.rentexpres.service.FileService;
import com.pinguela.rentexpres.service.ProvinceService;
import com.pinguela.rentexpres.service.RoleService;
import com.pinguela.rentexpres.service.CityService;
import com.pinguela.rentexpres.service.VehicleService;
import com.pinguela.rentexpres.service.VehicleCategoryService;
import com.pinguela.rentexpres.service.VehicleStatusService;
import com.pinguela.rentexpres.service.impl.CityServiceImpl;
import com.pinguela.rentexpres.service.impl.EmployeeServiceImpl;
import com.pinguela.rentexpres.service.impl.FileServiceImpl;
import com.pinguela.rentexpres.service.impl.ProvinceServiceImpl;
import com.pinguela.rentexpres.service.impl.RoleServiceImpl;
import com.pinguela.rentexpres.service.impl.VehicleCategoryServiceImpl;
import com.pinguela.rentexpres.service.impl.VehicleServiceImpl;
import com.pinguela.rentexpres.service.impl.VehicleStatusServiceImpl;
import com.pinguela.rentexpressweb.security.SessionManager;
import com.pinguela.rentexpressweb.service.EmployeeFormData;
import com.pinguela.rentexpressweb.service.EmployeeFormPreparationService;
import com.pinguela.rentexpressweb.service.HeadquartersLookupService;
import com.pinguela.rentexpressweb.service.VehiclePresentationService;
import com.pinguela.rentexpressweb.service.VehiclePresentationService.HomeVehiclesData;
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
        private final RoleService roleService;
        private final VehiclePresentationService vehiclePresentationService;
        private final HeadquartersLookupService headquartersLookupService;
        private final EmployeeFormPreparationService employeeFormPreparationService;

        public PublicEmployeeServlet() {
                this(new EmployeeServiceImpl(), new FileServiceImpl(), new VehicleServiceImpl(), new ProvinceServiceImpl(),
                                new CityServiceImpl(), new RoleServiceImpl(), new HeadquartersDAOImpl(),
                                new VehicleCategoryServiceImpl(), new VehicleStatusServiceImpl());
        }

        PublicEmployeeServlet(EmployeeService employeeService, FileService fileService, VehicleService vehicleService,
                        ProvinceService provinceService, CityService cityService, RoleService roleService,
                        HeadquartersDAO headquartersDAO, VehicleCategoryService vehicleCategoryService,
                        VehicleStatusService vehicleStatusService) {
                this.employeeService = employeeService;
                this.fileService = fileService;
                this.roleService = roleService;
                this.headquartersLookupService = new HeadquartersLookupService(headquartersDAO);
                this.vehiclePresentationService = new VehiclePresentationService(vehicleService, fileService,
                                vehicleCategoryService, vehicleStatusService, this.headquartersLookupService);
                this.employeeFormPreparationService = new EmployeeFormPreparationService(roleService, provinceService,
                                cityService, this.headquartersLookupService);
        }

        @Override
        protected void doGet(HttpServletRequest request, HttpServletResponse response)
                        throws ServletException, IOException {

                String action = request.getParameter("action");
                String destination = Views.INDEX;

                try {
                        if ("logout".equals(action)) {
                                SessionManager.logout(request);
                                destination = Views.LOGIN;

                        } else if ("detail".equals(action)) {
                                int id = Integer.parseInt(request.getParameter("id"));
                                EmployeeDTO employee = employeeService.findById(id);
                                enrichEmployee(employee);
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
                                loadEmployeeFormData(request, null);

                        } else if ("edit".equals(action)) {
                                int id = Integer.parseInt(request.getParameter("id"));
                                EmployeeDTO employee = employeeService.findById(id);
                                enrichEmployee(employee);
                                request.setAttribute("employee", employee);
                                loadEmployeeFormData(request, employee);
                                destination = Views.EMPLOYEE_FORM;

                        } else if ("deactivate".equals(action)) {
                                int id = Integer.parseInt(request.getParameter("id"));
                                EmployeeDTO employee = employeeService.findById(id);

                                if (employee != null) {
                                        updateActiveStatus(employee, false);
                                        employeeService.update(employee);
                                }

                                response.sendRedirect(request.getContextPath() + "/public/EmployeeServlet?action=list");
                                return;

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

                                EmployeeDTO employee = employeeService.autenticar(username, password);

                                if (employee != null && Boolean.TRUE.equals(employee.getActiveStatus())) {
                                        HttpSession session = request.getSession();
                                        session.setAttribute("employee", employee);

                                        if ("yes".equals(remember)) {
                                                Cookie cookieUser = new Cookie("rememberUser", username);
                                                cookieUser.setMaxAge(60 * 60 * 24 * 7);
                                                cookieUser.setPath(request.getContextPath());
                                                response.addCookie(cookieUser);

                                                Cookie cookiePassword = new Cookie("rememberPassword", password);
                                                cookiePassword.setMaxAge(60 * 60 * 24 * 7);
                                                cookiePassword.setPath(request.getContextPath());
                                                response.addCookie(cookiePassword);
                                        } else {
                                                Cookie cookieUser = new Cookie("rememberUser", "");
                                                cookieUser.setMaxAge(0);
                                                cookieUser.setPath(request.getContextPath());
                                                response.addCookie(cookieUser);

                                                Cookie cookiePassword = new Cookie("rememberPassword", "");
                                                cookiePassword.setMaxAge(0);
                                                cookiePassword.setPath(request.getContextPath());
                                                response.addCookie(cookiePassword);
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
                                String firstName = request.getParameter("firstName");
                                String lastName1 = request.getParameter("lastName1");
                                String lastName2 = request.getParameter("lastName2");
                                String phone = request.getParameter("phone");
                                Integer roleId = parseInteger(request.getParameter("roleId"));
                                Integer headquartersId = parseInteger(request.getParameter("headquartersId"));
                                Integer provinceId = parseInteger(request.getParameter("provinceId"));
                                Integer cityId = parseInteger(request.getParameter("cityId"));
                                boolean active = request.getParameter("activeStatus") != null;

                                EmployeeDTO newEmployee = new EmployeeDTO();
                                newEmployee.setEmployeeName(username);
                                newEmployee.setEmail(email);
                                newEmployee.setPassword(password);
                                newEmployee.setFirstName(firstName);
                                newEmployee.setLastName1(lastName1);
                                newEmployee.setLastName2(lastName2);
                                newEmployee.setPhone(phone);
                                newEmployee.setRoleId(roleId);
                                newEmployee.setHeadquartersId(headquartersId);
                                updateActiveStatus(newEmployee, active);

                                boolean created = employeeService.create(newEmployee);
                                if (created) {
                                        response.sendRedirect(request.getContextPath() + "/public/EmployeeServlet?action=list");
                                        return;
                                }

                                request.setAttribute("employee", newEmployee);
                                request.setAttribute("selectedProvinceId", provinceId);
                                request.setAttribute("selectedCityId", cityId);
                                loadEmployeeFormData(request, newEmployee);
                                request.setAttribute("formErrorMessageKey", "employee.form.saveError");
                                destination = Views.EMPLOYEE_FORM;

                        } else if ("update".equals(action)) {
                                int id = Integer.parseInt(request.getParameter("id"));
                                String username = request.getParameter("username");
                                String email = request.getParameter("email");
                                String password = request.getParameter("password");
                                String firstName = request.getParameter("firstName");
                                String lastName1 = request.getParameter("lastName1");
                                String lastName2 = request.getParameter("lastName2");
                                String phone = request.getParameter("phone");
                                Integer roleId = parseInteger(request.getParameter("roleId"));
                                Integer headquartersId = parseInteger(request.getParameter("headquartersId"));
                                Integer provinceId = parseInteger(request.getParameter("provinceId"));
                                Integer cityId = parseInteger(request.getParameter("cityId"));
                                boolean active = request.getParameter("activeStatus") != null;

                                EmployeeDTO employee = employeeService.findById(id);
                                employee.setEmployeeName(username);
                                employee.setEmail(email);
                                employee.setFirstName(firstName);
                                employee.setLastName1(lastName1);
                                employee.setLastName2(lastName2);
                                employee.setPhone(phone);
                                employee.setRoleId(roleId);
                                employee.setHeadquartersId(headquartersId);
                                employee.setPassword((password != null && !password.isBlank()) ? password : null);
                                updateActiveStatus(employee, active);

                                boolean updated = employeeService.update(employee);
                                if (updated) {
                                        response.sendRedirect(request.getContextPath() + "/public/EmployeeServlet?action=list");
                                        return;
                                }

                                enrichEmployee(employee);
                                request.setAttribute("employee", employee);
                                request.setAttribute("selectedProvinceId", provinceId);
                                request.setAttribute("selectedCityId", cityId);
                                loadEmployeeFormData(request, employee);
                                request.setAttribute("formErrorMessageKey", "employee.form.updateError");
                                destination = Views.EMPLOYEE_FORM;
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
                String language = resolveLanguage(request);
                HomeVehiclesData data = vehiclePresentationService.loadHomeVehicles(language, 4);

                request.setAttribute("featuredVehicles", data.getVehicles());
                request.setAttribute("featuredVehicleImages", data.getVehicleImages());
                request.setAttribute("featuredVehiclesError", Boolean.valueOf(data.hasErrors()));
        }

        private String resolveLanguage(HttpServletRequest request) {
                HttpSession session = request.getSession(false);
                if (session != null) {
                        Object localeAttr = session.getAttribute("locale");
                        if (localeAttr instanceof Locale) {
                                Locale locale = (Locale) localeAttr;
                                if (locale.getLanguage() != null && !locale.getLanguage().isEmpty()) {
                                        return locale.getLanguage();
                                }
                        }
                }
                return Locale.getDefault().getLanguage();
        }

        private void updateActiveStatus(EmployeeDTO employee, boolean active) {
                if (employee != null) {
                        employee.setActiveStatus(Boolean.valueOf(active));
                }
        }

        private void enrichEmployee(EmployeeDTO employee) throws RentexpresException {
                if (employee == null) {
                        return;
                }

                if (employee.getRoleId() != null) {
                        RoleDTO role = roleService.findById(employee.getRoleId());
                        employee.setRole(role);
                }

                if (employee.getHeadquartersId() != null) {
                        HeadquartersDTO headquarters = headquartersLookupService.findById(employee.getHeadquartersId());
                        employee.setHeadquarters(headquarters);
                }
        }

        private void loadEmployeeFormData(HttpServletRequest request, EmployeeDTO employee) throws RentexpresException {
                EmployeeFormData formData = employeeFormPreparationService.loadFormData();

                request.setAttribute("roles", new ArrayList<>(formData.getRoles()));
                request.setAttribute("headquartersList", new ArrayList<>(formData.getHeadquarters()));
                request.setAttribute("provinces", new ArrayList<>(formData.getProvinces()));
                request.setAttribute("citiesByProvince", formData.getCitiesByProvince());

                Integer selectedProvinceId = (Integer) request.getAttribute("selectedProvinceId");
                Integer selectedCityId = (Integer) request.getAttribute("selectedCityId");
                Integer selectedHeadquartersId = null;

                if (employee != null) {
                        selectedHeadquartersId = employee.getHeadquartersId();
                }

                if (selectedHeadquartersId == null) {
                        selectedHeadquartersId = parseInteger(request.getParameter("headquartersId"));
                }

                if (selectedProvinceId == null) {
                        selectedProvinceId = parseInteger(request.getParameter("provinceId"));
                }

                if (selectedCityId == null) {
                        selectedCityId = parseInteger(request.getParameter("cityId"));
                }

                if (selectedProvinceId == null && selectedHeadquartersId != null) {
                        HeadquartersDTO selectedHeadquarters = formData.getHeadquartersById(selectedHeadquartersId);
                        if (selectedHeadquarters != null) {
                                if (selectedHeadquarters.getProvince() != null) {
                                        selectedProvinceId = selectedHeadquarters.getProvince().getProvinceId();
                                }
                                if (selectedHeadquarters.getCity() != null) {
                                        selectedCityId = selectedHeadquarters.getCity().getCityId();
                                }
                        }
                }

                request.setAttribute("selectedProvinceId", selectedProvinceId);
                request.setAttribute("selectedCityId", selectedCityId);
                request.setAttribute("selectedHeadquartersId", selectedHeadquartersId);
        }

        private Integer parseInteger(String value) {
                if (value == null || value.isBlank()) {
                        return null;
                }
                try {
                        return Integer.valueOf(value.trim());
                } catch (NumberFormatException e) {
                        return null;
                }
        }
}
