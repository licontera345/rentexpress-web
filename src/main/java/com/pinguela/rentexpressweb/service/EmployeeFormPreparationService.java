package com.pinguela.rentexpressweb.service;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import com.pinguela.rentexpres.exception.RentexpresException;
import com.pinguela.rentexpres.model.CityDTO;
import com.pinguela.rentexpres.model.HeadquartersDTO;
import com.pinguela.rentexpres.model.ProvinceDTO;
import com.pinguela.rentexpres.model.RoleDTO;
import com.pinguela.rentexpres.service.CityService;
import com.pinguela.rentexpres.service.ProvinceService;
import com.pinguela.rentexpres.service.RoleService;

/**
 * Provides the catalog data needed to render the public employee form.
 */
public class EmployeeFormPreparationService {

        private final RoleService roleService;
        private final ProvinceService provinceService;
        private final CityService cityService;
        private final HeadquartersLookupService headquartersLookupService;

        public EmployeeFormPreparationService(RoleService roleService, ProvinceService provinceService,
                        CityService cityService, HeadquartersLookupService headquartersLookupService) {
                this.roleService = roleService;
                this.provinceService = provinceService;
                this.cityService = cityService;
                this.headquartersLookupService = headquartersLookupService;
        }

        public EmployeeFormData loadFormData() throws RentexpresException {
                List<RoleDTO> roles = loadRoles();
                List<HeadquartersDTO> headquarters = sortHeadquarters(headquartersLookupService.findAll());
                List<ProvinceDTO> provinces = loadProvinces();
                Map<Integer, List<CityDTO>> citiesByProvince = loadCitiesByProvince();

                return new EmployeeFormData(roles, headquarters, provinces, citiesByProvince);
        }

        private List<RoleDTO> loadRoles() throws RentexpresException {
                List<RoleDTO> roles = roleService.findAll();
                if (roles == null) {
                        return Collections.emptyList();
                }

                List<RoleDTO> sorted = new ArrayList<>(roles);
                Comparator<RoleDTO> comparator = Comparator.comparing(role -> safeString(role.getRoleName()),
                                stringCollator());
                Collections.sort(sorted, comparator);
                return sorted;
        }

        private List<ProvinceDTO> loadProvinces() throws RentexpresException {
                List<ProvinceDTO> provinces = provinceService.findAll();
                if (provinces == null) {
                        return Collections.emptyList();
                }

                List<ProvinceDTO> sorted = new ArrayList<>(provinces);
                Comparator<ProvinceDTO> comparator = Comparator.comparing(province -> safeString(province.getProvinceName()),
                                stringCollator());
                Collections.sort(sorted, comparator);
                return sorted;
        }

        private Map<Integer, List<CityDTO>> loadCitiesByProvince() throws RentexpresException {
                List<CityDTO> cities = cityService.findAll();
                if (cities == null || cities.isEmpty()) {
                                return Collections.emptyMap();
                }

                Map<Integer, List<CityDTO>> grouped = new LinkedHashMap<>();
                for (CityDTO city : cities) {
                        if (city == null || city.getProvinceId() == null) {
                                continue;
                        }
                        grouped.computeIfAbsent(city.getProvinceId(), key -> new ArrayList<>()).add(city);
                }

                Comparator<CityDTO> comparator = Comparator.comparing(city -> safeString(city.getCityName()),
                                stringCollator());
                for (List<CityDTO> cityList : grouped.values()) {
                        cityList.sort(comparator);
                }

                return grouped;
        }

        private Collator stringCollator() {
                Collator collator = Collator.getInstance(Locale.getDefault());
                collator.setStrength(Collator.PRIMARY);
                return collator;
        }

        private List<HeadquartersDTO> sortHeadquarters(List<HeadquartersDTO> headquarters) {
                if (headquarters == null) {
                        return Collections.emptyList();
                }

                List<HeadquartersDTO> sorted = new ArrayList<>(headquarters);
                Comparator<HeadquartersDTO> comparator = Comparator.comparing(h -> safeString(h.getName()),
                                stringCollator());
                Collections.sort(sorted, comparator);
                return sorted;
        }

        private String safeString(String value) {
                return value != null ? value : "";
        }
}
