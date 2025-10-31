package com.pinguela.rentexpressweb.service.employee;

/**
 * Servicio encargado de preparar los datos necesarios para el directorio de empleados.
 */
public interface EmployeeDirectoryService {

    EmployeeDirectoryView prepareDirectory(EmployeeDirectoryFilter filter);
}
