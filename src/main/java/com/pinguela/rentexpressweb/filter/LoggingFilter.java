package com.pinguela.rentexpressweb.filter;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.pinguela.rentexpres.model.EmployeeDTO;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

/**
 * Filtro de trazas (auditoría) para RentExpress. Registra cada petición con
 * fecha, método, ruta y empleado autenticado.
 */
@WebFilter("/*")
public class LoggingFilter implements Filter {

	/**
	 * @see Filter#init(FilterConfig)
	 */
	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		System.out.println("[LoggingFilter] Inicializado correctamente");
	}

	/**
	 * @see Filter#doFilter(ServletRequest, ServletResponse, FilterChain)
	 */
	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {

		HttpServletRequest req = (HttpServletRequest) request;
		String uri = req.getRequestURI();
		String method = req.getMethod();
		HttpSession session = req.getSession(false);
                EmployeeDTO employee = (session != null) ? (EmployeeDTO) session.getAttribute("employee") : null;
                String employeeStr = (employee != null) ? employee.getEmployeeName() : "anónimo";
		String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());

		chain.doFilter(request, response);
	}

	/**
	 * @see Filter#destroy()
	 */
	@Override
	public void destroy() {
		System.out.println("[LoggingFilter] Finalizado");
	}
}
