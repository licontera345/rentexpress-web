package com.pinguela.rentexpressweb.service.employee;

import com.pinguela.rentexpres.model.EmployeeDTO;
import com.pinguela.rentexpres.model.HeadquartersDTO;
import com.pinguela.rentexpres.model.RoleDTO;
import com.pinguela.rentexpres.service.EmployeeService;
import com.pinguela.rentexpres.service.HeadquartersService;
import com.pinguela.rentexpres.service.RoleService;
import com.pinguela.rentexpres.service.impl.EmployeeServiceImpl;
import com.pinguela.rentexpres.service.impl.HeadquartersServiceImpl;
import com.pinguela.rentexpres.service.impl.RoleServiceImpl;
import com.pinguela.rentexpressweb.constants.EmployeeConstants;
import com.pinguela.rentexpressweb.util.ControllerUtils;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Implementación por defecto del servicio de directorio de empleados.
 */
public class EmployeeDirectoryServiceImpl implements EmployeeDirectoryService {

    private static final Logger LOGGER = LogManager.getLogger(EmployeeDirectoryServiceImpl.class);

    private final EmployeeService employeeService;
    private final RoleService roleService;
    private final HeadquartersService headquartersService;

    public EmployeeDirectoryServiceImpl() {
        this(new EmployeeServiceImpl(), new RoleServiceImpl(), new HeadquartersServiceImpl());
    }

    public EmployeeDirectoryServiceImpl(EmployeeService employeeService, RoleService roleService,
            HeadquartersService headquartersService) {
        this.employeeService = employeeService;
        this.roleService = roleService;
        this.headquartersService = headquartersService;
    }

    @Override
    public EmployeeDirectoryView prepareDirectory(EmployeeDirectoryFilter filter) {
        List<EmployeeDTO> employees = loadEmployees();
        List<RoleDTO> roles = loadRoles();
        List<HeadquartersDTO> headquarters = loadHeadquarters();

        List<EmployeeDTO> filteredEmployees = applyFilters(employees, filter);
        Map<String, Object> summary = buildSummary(employees, filteredEmployees);

        Map<Integer, String> roleNames = mapRoles(roles);
        Map<Integer, String> headquartersNames = mapHeadquarters(headquarters);

        return new EmployeeDirectoryView(filteredEmployees, roleNames, headquartersNames, summary);
    }

    private List<EmployeeDTO> loadEmployees() {
        try {
            List<EmployeeDTO> list = employeeService.findAll();
            if (list == null) {
                return Collections.emptyList();
            }
            return list;
        } catch (Exception ex) {
            LOGGER.error("No se pudieron recuperar los empleados", ex);
            return Collections.emptyList();
        }
    }

    private List<RoleDTO> loadRoles() {
        try {
            List<RoleDTO> roles = roleService.findAll();
            if (roles == null) {
                return Collections.emptyList();
            }
            return roles;
        } catch (Exception ex) {
            LOGGER.warn("No se pudieron recuperar los roles disponibles", ex);
            return Collections.emptyList();
        }
    }

    private List<HeadquartersDTO> loadHeadquarters() {
        try {
            List<HeadquartersDTO> headquarters = headquartersService.findAll();
            if (headquarters == null) {
                return Collections.emptyList();
            }
            return headquarters;
        } catch (Exception ex) {
            LOGGER.error("No se pudieron recuperar las sedes", ex);
            return Collections.emptyList();
        }
    }

    private List<EmployeeDTO> applyFilters(List<EmployeeDTO> employees, EmployeeDirectoryFilter filter) {
        if (employees == null || employees.isEmpty()) {
            return Collections.emptyList();
        }
        List<EmployeeDTO> filtered = new ArrayList<EmployeeDTO>();
        String normalizedSearch = ControllerUtils.normalizeLowerCase(filter.getSearch());
        Integer headquartersId = filter.getHeadquartersId();
        Boolean active = filter.getActive();

        for (int i = 0; i < employees.size(); i++) {
            EmployeeDTO employee = employees.get(i);
            if (employee == null) {
                continue;
            }
            if (!matchesSearch(employee, normalizedSearch)) {
                continue;
            }
            if (!matchesHeadquarters(employee, headquartersId)) {
                continue;
            }
            if (!matchesActive(employee, active)) {
                continue;
            }
            filtered.add(employee);
        }
        return filtered;
    }

    private boolean matchesSearch(EmployeeDTO employee, String normalizedSearch) {
        if (normalizedSearch == null || normalizedSearch.isEmpty()) {
            return true;
        }
        if (contains(employee.getFirstName(), normalizedSearch)) {
            return true;
        }
        if (contains(employee.getLastName1(), normalizedSearch)) {
            return true;
        }
        if (contains(employee.getLastName2(), normalizedSearch)) {
            return true;
        }
        if (contains(employee.getEmail(), normalizedSearch)) {
            return true;
        }
        if (contains(employee.getEmployeeName(), normalizedSearch)) {
            return true;
        }
        return false;
    }

    private boolean contains(String value, String search) {
        return value != null && value.toLowerCase(Locale.ROOT).contains(search);
    }

    private boolean matchesHeadquarters(EmployeeDTO employee, Integer headquartersId) {
        if (headquartersId == null) {
            return true;
        }
        return Objects.equals(employee.getHeadquartersId(), headquartersId);
    }

    private boolean matchesActive(EmployeeDTO employee, Boolean active) {
        if (active == null) {
            return true;
        }
        Boolean status = employee.getActiveStatus();
        if (status == null) {
            return !active.booleanValue();
        }
        return status.booleanValue() == active.booleanValue();
    }

    private Map<Integer, String> mapRoles(List<RoleDTO> roles) {
        Map<Integer, String> map = new LinkedHashMap<Integer, String>();
        if (roles == null) {
            return map;
        }
        for (int i = 0; i < roles.size(); i++) {
            RoleDTO role = roles.get(i);
            if (role != null && role.getRoleId() != null) {
                map.put(role.getRoleId(), role.getRoleName());
            }
        }
        return map;
    }

    private Map<Integer, String> mapHeadquarters(List<HeadquartersDTO> headquarters) {
        Map<Integer, String> map = new LinkedHashMap<Integer, String>();
        if (headquarters == null) {
            return map;
        }
        for (int i = 0; i < headquarters.size(); i++) {
            HeadquartersDTO dto = headquarters.get(i);
            if (dto != null && dto.getId() != null) {
                map.put(dto.getId(), dto.getName());
            }
        }
        return map;
    }

    private Map<String, Object> buildSummary(List<EmployeeDTO> all, List<EmployeeDTO> filtered) {
        Map<String, Object> summary = new LinkedHashMap<String, Object>();
        int activeCount = 0;
        int inactiveCount = 0;
        Set<Integer> headquarters = new HashSet<Integer>();

        int total = all == null ? 0 : all.size();
        int filteredSize = filtered == null ? 0 : filtered.size();

        if (all != null) {
            for (int i = 0; i < all.size(); i++) {
                EmployeeDTO employee = all.get(i);
                if (employee == null) {
                    continue;
                }
                if (Boolean.TRUE.equals(employee.getActiveStatus())) {
                    activeCount++;
                } else {
                    inactiveCount++;
                }
                if (employee.getHeadquartersId() != null) {
                    headquarters.add(employee.getHeadquartersId());
                }
            }
        }

        summary.put(EmployeeConstants.SUMMARY_TOTAL, Integer.valueOf(total));
        summary.put(EmployeeConstants.SUMMARY_FILTERED, Integer.valueOf(filteredSize));
        summary.put(EmployeeConstants.SUMMARY_ACTIVE, Integer.valueOf(activeCount));
        summary.put(EmployeeConstants.SUMMARY_INACTIVE, Integer.valueOf(inactiveCount));
        summary.put(EmployeeConstants.SUMMARY_HEADQUARTERS, Integer.valueOf(headquarters.size()));
        return summary;
    }
}
