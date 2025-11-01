package com.pinguela.rentexpressweb.controller;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.pinguela.rentexpres.exception.RentexpresException;
import com.pinguela.rentexpres.model.EmployeeDTO;
import com.pinguela.rentexpres.model.UserDTO;
import com.pinguela.rentexpres.service.EmployeeService;
import com.pinguela.rentexpres.service.UserService;
import com.pinguela.rentexpres.service.impl.EmployeeServiceImpl;
import com.pinguela.rentexpres.service.impl.UserServiceImpl;
import com.pinguela.rentexpressweb.constants.AppConstants;
import com.pinguela.rentexpressweb.constants.UserConstants;
import com.pinguela.rentexpressweb.util.MessageResolver;
import com.pinguela.rentexpressweb.util.SessionManager;
import com.pinguela.rentexpressweb.util.Views;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
@WebServlet("/app/users/private")
public class PrivateProfileServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final String KEY_FLASH_SUCCESS = "user.profile.flash.success";
    private static final String KEY_ERROR_FULL_NAME_REQUIRED = "error.validation.fullNameRequired";
    private static final Logger LOGGER = LogManager.getLogger(PrivateProfileServlet.class);
    private transient UserService userService;
    private transient EmployeeService employeeService;
    @Override
    public void init() throws ServletException {
        super.init();
        this.userService = new UserServiceImpl();
        this.employeeService = new EmployeeServiceImpl();
    }
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        UserDTO currentUser = (UserDTO) SessionManager.get(request, AppConstants.ATTR_CURRENT_USER);
        EmployeeDTO currentEmployee = (EmployeeDTO) SessionManager.get(request, AppConstants.ATTR_CURRENT_EMPLOYEE);
        if (currentUser == null && currentEmployee == null) {
            response.sendRedirect(request.getContextPath() + Views.PUBLIC_LOGIN);
            return;
        }
        try {
            if (currentUser != null) {
                UserDTO reloaded = userService.findById(currentUser.getUserId());
                if (reloaded != null) {
                    SessionManager.set(request, AppConstants.ATTR_CURRENT_USER, reloaded);
                    currentUser = reloaded;
                } else {
                    request.setAttribute(AppConstants.ATTR_FLASH_ERROR,
                            MessageResolver.getMessage(request, "user.profile.loadError"));
                }
            } else if (currentEmployee != null) {
                EmployeeDTO reloaded = employeeService.findById(currentEmployee.getEmployeeId());
                if (reloaded != null) {
                    SessionManager.set(request, AppConstants.ATTR_CURRENT_EMPLOYEE, reloaded);
                    currentEmployee = reloaded;
                } else {
                    request.setAttribute(AppConstants.ATTR_FLASH_ERROR,
                            MessageResolver.getMessage(request, "employee.profile.loadError"));
                }
            }
        } catch (RentexpresException ex) {
            LOGGER.error("Error loading private profile information", ex);
            request.setAttribute(AppConstants.ATTR_FLASH_ERROR, ex.getMessage());
        }
        Map<String, Object> account = buildAccount(currentUser, currentEmployee, null, null);
        request.setAttribute("account", account);
        SessionManager.set(request, UserConstants.ATTR_PROFILE_DATA, account);
        request.getRequestDispatcher(Views.PRIVATE_USER_PROFILE).forward(request, response);
    }
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        UserDTO currentUser = (UserDTO) SessionManager.get(request, AppConstants.ATTR_CURRENT_USER);
        EmployeeDTO currentEmployee = (EmployeeDTO) SessionManager.get(request, AppConstants.ATTR_CURRENT_EMPLOYEE);
        if (currentUser == null && currentEmployee == null) {
            response.sendRedirect(request.getContextPath() + Views.PUBLIC_LOGIN);
            return;
        }
        Map<String, String> errors = new LinkedHashMap<String, String>();
        String fullName = normalize(request.getParameter(UserConstants.PARAM_FULL_NAME));
        String phone = normalize(request.getParameter(UserConstants.PARAM_PHONE));
        if (fullName == null) {
            errors.put(UserConstants.PARAM_FULL_NAME,
                    MessageResolver.getMessage(request, KEY_ERROR_FULL_NAME_REQUIRED));
        }
        if (!errors.isEmpty()) {
            request.setAttribute(UserConstants.ATTR_PROFILE_ERRORS, errors);
            request.setAttribute("account", buildAccount(currentUser, currentEmployee, fullName, phone));
            request.getRequestDispatcher(Views.PRIVATE_USER_PROFILE).forward(request, response);
            return;
        }
        try {
            if (updateAccount(request, currentUser, currentEmployee, fullName, phone)) {
                SessionManager.set(request, AppConstants.ATTR_FLASH_SUCCESS,
                        MessageResolver.getMessage(request, KEY_FLASH_SUCCESS));
                response.sendRedirect(request.getContextPath() + Views.PRIVATE_USER_PROFILE);
                return;
            }
            String errorKey = currentUser != null ? "user.profile.loadError" : "employee.profile.loadError";
            request.setAttribute(AppConstants.ATTR_FLASH_ERROR, MessageResolver.getMessage(request, errorKey));
        } catch (RentexpresException ex) {
            LOGGER.error("Error updating private profile", ex);
            request.setAttribute(AppConstants.ATTR_FLASH_ERROR, ex.getMessage());
        }
        request.setAttribute("account", buildAccount(currentUser, currentEmployee, fullName, phone));
        request.getRequestDispatcher(Views.PRIVATE_USER_PROFILE).forward(request, response);
    }
    private String normalize(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }
    private boolean updateAccount(HttpServletRequest request, UserDTO currentUser, EmployeeDTO currentEmployee,
            String fullName, String phone) throws RentexpresException {
        if (currentUser != null) {
            UserDTO updated = new UserDTO();
            updated.setUserId(currentUser.getUserId());
            updated.setFirstName(fullName);
            updated.setLastName1(currentUser.getLastName1());
            updated.setLastName2(currentUser.getLastName2());
            updated.setPhone(phone);
            updated.setEmail(currentUser.getEmail());
            userService.update(updated);
            UserDTO refreshed = userService.findById(currentUser.getUserId());
            if (refreshed == null) {
                return false;
            }
            SessionManager.set(request, AppConstants.ATTR_CURRENT_USER, refreshed);
            SessionManager.set(request, UserConstants.ATTR_PROFILE_DATA, buildAccount(refreshed, null, null, null));
            return true;
        }
        if (currentEmployee != null) {
            EmployeeDTO updatedEmployee = new EmployeeDTO();
            updatedEmployee.setEmployeeId(currentEmployee.getEmployeeId());
            updatedEmployee.setEmployeeName(fullName);
            updatedEmployee.setFirstName(fullName);
            updatedEmployee.setPhone(phone);
            updatedEmployee.setEmail(currentEmployee.getEmail());
            employeeService.update(updatedEmployee);
            EmployeeDTO refreshed = employeeService.findById(currentEmployee.getEmployeeId());
            if (refreshed == null) {
                return false;
            }
            SessionManager.set(request, AppConstants.ATTR_CURRENT_EMPLOYEE, refreshed);
            SessionManager.set(request, UserConstants.ATTR_PROFILE_DATA, buildAccount(null, refreshed, null, null));
            return true;
        }
        return false;
    }
    private Map<String, Object> buildAccount(UserDTO user, EmployeeDTO employee, String fullName, String phone) {
        Map<String, Object> account = new LinkedHashMap<String, Object>();
        if (user != null) {
            account.put("id", user.getUserId());
            account.put("email", user.getEmail());
            account.put("fullName", resolveFullName(fullName, user.getFirstName(), user.getLastName1(), user.getLastName2()));
            account.put("phone", phone != null ? phone : user.getPhone());
        } else if (employee != null) {
            account.put("id", employee.getEmployeeId());
            account.put("email", employee.getEmail());
            account.put("fullName",
                    fullName != null ? fullName : (employee.getEmployeeName() != null ? employee.getEmployeeName() : employee.getFirstName()));
            account.put("phone", phone != null ? phone : employee.getPhone());
        }
        account.put("avatarPath", null);
        return account;
    }
    private String resolveFullName(String provided, String firstName, String lastName1, String lastName2) {
        if (provided != null) {
            return provided;
        }
        StringBuilder builder = new StringBuilder();
        if (firstName != null && !firstName.isEmpty()) {
            builder.append(firstName.trim());
        }
        if (lastName1 != null && !lastName1.isEmpty()) {
            if (builder.length() > 0) {
                builder.append(' ');
            }
            builder.append(lastName1.trim());
        }
        if (lastName2 != null && !lastName2.isEmpty()) {
            if (builder.length() > 0) {
                builder.append(' ');
            }
            builder.append(lastName2.trim());
        }
        return builder.length() > 0 ? builder.toString() : null;
    }
}
