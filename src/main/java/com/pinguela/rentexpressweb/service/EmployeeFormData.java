package com.pinguela.rentexpressweb.service;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.pinguela.rentexpres.model.CityDTO;
import com.pinguela.rentexpres.model.HeadquartersDTO;
import com.pinguela.rentexpres.model.ProvinceDTO;
import com.pinguela.rentexpres.model.RoleDTO;

/**
 * Immutable view model with the catalog information required by the employee form.
 */
public class EmployeeFormData {

        private final List<RoleDTO> roles;
        private final List<HeadquartersDTO> headquarters;
        private final List<ProvinceDTO> provinces;
        private final Map<Integer, List<CityDTO>> citiesByProvince;
        private final Map<Integer, HeadquartersDTO> headquartersById;

        public EmployeeFormData(List<RoleDTO> roles, List<HeadquartersDTO> headquarters, List<ProvinceDTO> provinces,
                        Map<Integer, List<CityDTO>> citiesByProvince) {
                this.roles = roles != null ? Collections.unmodifiableList(roles) : Collections.emptyList();
                this.headquarters = headquarters != null ? Collections.unmodifiableList(headquarters) : Collections.emptyList();
                this.provinces = provinces != null ? Collections.unmodifiableList(provinces) : Collections.emptyList();
                this.citiesByProvince = citiesByProvince != null ? Collections.unmodifiableMap(citiesByProvince)
                                : Collections.emptyMap();
                this.headquartersById = indexHeadquarters(this.headquarters);
        }

        public List<RoleDTO> getRoles() {
                return roles;
        }

        public List<HeadquartersDTO> getHeadquarters() {
                return headquarters;
        }

        public List<ProvinceDTO> getProvinces() {
                return provinces;
        }

        public Map<Integer, List<CityDTO>> getCitiesByProvince() {
                return citiesByProvince;
        }

        public HeadquartersDTO getHeadquartersById(Integer headquartersId) {
                if (headquartersId == null) {
                        return null;
                }
                return headquartersById.get(headquartersId);
        }

        private Map<Integer, HeadquartersDTO> indexHeadquarters(List<HeadquartersDTO> headquartersList) {
                if (headquartersList == null || headquartersList.isEmpty()) {
                        return Collections.emptyMap();
                }

                Map<Integer, HeadquartersDTO> index = new LinkedHashMap<>();
                for (HeadquartersDTO headquartersDTO : headquartersList) {
                        if (headquartersDTO != null && headquartersDTO.getHeadquartersId() != null) {
                                index.put(headquartersDTO.getHeadquartersId(), headquartersDTO);
                        }
                }
                return Collections.unmodifiableMap(index);
        }
}
