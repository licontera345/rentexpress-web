package com.pinguela.rentexpressweb.service.employee;

import com.pinguela.rentexpressweb.constants.EmployeeConstants;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Filtros aplicables al directorio de empleados.
 */
public class EmployeeDirectoryFilter {

    private final String search;
    private final Integer headquartersId;
    private final Boolean active;
    private final Map<String, String> rawFilters;

    private EmployeeDirectoryFilter(Builder builder) {
        this.search = builder.search;
        this.headquartersId = builder.headquartersId;
        this.active = builder.active;
        this.rawFilters = Collections.unmodifiableMap(new LinkedHashMap<String, String>(builder.rawFilters));
    }

    public String getSearch() {
        return search;
    }

    public Integer getHeadquartersId() {
        return headquartersId;
    }

    public Boolean getActive() {
        return active;
    }

    public Map<String, String> getRawFilters() {
        return rawFilters;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String search;
        private Integer headquartersId;
        private Boolean active;
        private final Map<String, String> rawFilters = new LinkedHashMap<String, String>();

        public Builder search(String value) {
            this.search = value;
            this.rawFilters.put(EmployeeConstants.PARAM_SEARCH, value);
            return this;
        }

        public Builder headquartersId(Integer value, String raw) {
            this.headquartersId = value;
            this.rawFilters.put(EmployeeConstants.PARAM_HEADQUARTERS, raw);
            return this;
        }

        public Builder active(Boolean value, String raw) {
            this.active = value;
            this.rawFilters.put(EmployeeConstants.PARAM_ACTIVE, raw);
            return this;
        }

        public EmployeeDirectoryFilter build() {
            if (!rawFilters.containsKey(EmployeeConstants.PARAM_SEARCH)) {
                rawFilters.put(EmployeeConstants.PARAM_SEARCH, null);
            }
            if (!rawFilters.containsKey(EmployeeConstants.PARAM_HEADQUARTERS)) {
                rawFilters.put(EmployeeConstants.PARAM_HEADQUARTERS, null);
            }
            if (!rawFilters.containsKey(EmployeeConstants.PARAM_ACTIVE)) {
                rawFilters.put(EmployeeConstants.PARAM_ACTIVE, EmployeeConstants.VALUE_ALL);
            }
            return new EmployeeDirectoryFilter(this);
        }
    }
}
