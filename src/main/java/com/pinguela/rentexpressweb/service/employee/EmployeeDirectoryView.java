package com.pinguela.rentexpressweb.service.employee;

import com.pinguela.rentexpres.model.EmployeeDTO;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Resultado preparado para la vista del directorio de empleados.
 */
public class EmployeeDirectoryView {

    private final List<EmployeeDTO> employees;
    private final Map<Integer, String> roleNames;
    private final Map<Integer, String> headquartersNames;
    private final Map<String, Object> summary;

    public EmployeeDirectoryView(List<EmployeeDTO> employees, Map<Integer, String> roleNames,
            Map<Integer, String> headquartersNames, Map<String, Object> summary) {
        this.employees = employees == null ? Collections.<EmployeeDTO>emptyList() : employees;
        this.roleNames = roleNames == null ? Collections.<Integer, String>emptyMap()
                : Collections.unmodifiableMap(new LinkedHashMap<Integer, String>(roleNames));
        this.headquartersNames = headquartersNames == null ? Collections.<Integer, String>emptyMap()
                : Collections.unmodifiableMap(new LinkedHashMap<Integer, String>(headquartersNames));
        this.summary = summary == null ? Collections.<String, Object>emptyMap()
                : Collections.unmodifiableMap(new LinkedHashMap<String, Object>(summary));
    }

    public List<EmployeeDTO> getEmployees() {
        return employees;
    }

    public Map<Integer, String> getRoleNames() {
        return roleNames;
    }

    public Map<Integer, String> getHeadquartersNames() {
        return headquartersNames;
    }

    public Map<String, Object> getSummary() {
        return summary;
    }
}
