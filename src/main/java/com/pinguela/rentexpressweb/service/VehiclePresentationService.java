package com.pinguela.rentexpressweb.service;

import java.io.File;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.pinguela.rentexpres.exception.RentexpresException;
import com.pinguela.rentexpres.model.HeadquartersDTO;
import com.pinguela.rentexpres.model.Results;
import com.pinguela.rentexpres.model.VehicleCategoryDTO;
import com.pinguela.rentexpres.model.VehicleCriteria;
import com.pinguela.rentexpres.model.VehicleDTO;
import com.pinguela.rentexpres.model.VehicleStatusDTO;
import com.pinguela.rentexpres.service.FileService;
import com.pinguela.rentexpres.service.VehicleCategoryService;
import com.pinguela.rentexpres.service.VehicleService;
import com.pinguela.rentexpres.service.VehicleStatusService;

/**
 * Aggregates the catalog lookups and enrichment needed to render vehicle listings throughout the site.
 */
public class VehiclePresentationService {

        private final VehicleService vehicleService;
        private final FileService fileService;
        private final VehicleCategoryService vehicleCategoryService;
        private final VehicleStatusService vehicleStatusService;
        private final HeadquartersLookupService headquartersLookupService;

        public VehiclePresentationService(VehicleService vehicleService, FileService fileService,
                        VehicleCategoryService vehicleCategoryService, VehicleStatusService vehicleStatusService,
                        HeadquartersLookupService headquartersLookupService) {
                this.vehicleService = vehicleService;
                this.fileService = fileService;
                this.vehicleCategoryService = vehicleCategoryService;
                this.vehicleStatusService = vehicleStatusService;
                this.headquartersLookupService = headquartersLookupService;
        }

        public HomeVehiclesData loadHomeVehicles(String language, int pageSize) {
                VehicleCriteria criteria = new VehicleCriteria();
                criteria.setPageNumber(1);
                criteria.setPageSize(Integer.valueOf(pageSize));
                criteria.setOrderBy("created_at");
                criteria.setOrderDir("DESC");

                try {
                        Results<VehicleDTO> results = vehicleService.findByCriteria(criteria);
                        List<VehicleDTO> vehicles = extractVehicles(results);

                        Map<Integer, VehicleCategoryDTO> categories = indexVehicleCategories(loadCategories(language));
                        Map<Integer, VehicleStatusDTO> statuses = indexVehicleStatuses(loadStatuses(language));
                        Map<Integer, HeadquartersDTO> headquarters = indexHeadquarters(headquartersLookupService.findAll());

                        enrichVehicles(vehicles, categories, statuses, headquarters);
                        Map<Integer, Boolean> vehicleImages = buildVehicleImages(vehicles);

                        return new HomeVehiclesData(vehicles, vehicleImages, false);
                } catch (RentexpresException e) {
                        return new HomeVehiclesData(Collections.emptyList(), Collections.emptyMap(), true);
                }
        }

        public VehicleListData loadVehicleList(VehicleCriteria criteria, String language) {
                List<VehicleCategoryDTO> categories = loadCategories(language);
                List<VehicleStatusDTO> statuses = loadStatuses(language);

                Results<VehicleDTO> results = new Results<>();
                results.setPage(criteria.getSafePage());
                results.setPageSize(criteria.getSafePageSize());
                results.normalize();

                List<VehicleDTO> vehicles = Collections.emptyList();
                Map<Integer, Boolean> vehicleImages = Collections.emptyMap();
                boolean hasErrors = false;

                try {
                        Results<VehicleDTO> fetched = vehicleService.findByCriteria(criteria);
                        if (fetched != null) {
                                results = fetched;
                                results.setPage(criteria.getSafePage());
                                results.setPageSize(criteria.getSafePageSize());
                                results.normalize();
                        }

                        vehicles = results.getResults();
                        if (vehicles == null) {
                                vehicles = Collections.emptyList();
                        }

                        Map<Integer, VehicleCategoryDTO> categoryIndex = indexVehicleCategories(categories);
                        Map<Integer, VehicleStatusDTO> statusIndex = indexVehicleStatuses(statuses);
                        Map<Integer, HeadquartersDTO> headquartersIndex = indexHeadquarters(headquartersLookupService.findAll());

                        enrichVehicles(vehicles, categoryIndex, statusIndex, headquartersIndex);
                        vehicleImages = buildVehicleImages(vehicles);
                } catch (RentexpresException e) {
                        hasErrors = true;
                        results = new Results<>();
                        results.setPage(criteria.getSafePage());
                        results.setPageSize(criteria.getSafePageSize());
                        results.normalize();
                        vehicles = Collections.emptyList();
                        vehicleImages = Collections.emptyMap();
                }

                return new VehicleListData(results, vehicles, vehicleImages, categories, statuses, hasErrors);
        }

        private List<VehicleDTO> extractVehicles(Results<VehicleDTO> results) {
                if (results == null || results.getResults() == null) {
                        return Collections.emptyList();
                }
                return results.getResults();
        }

        private List<VehicleCategoryDTO> loadCategories(String language) {
                try {
                        List<VehicleCategoryDTO> categories = vehicleCategoryService.findAll(language);
                        return categories != null ? categories : Collections.emptyList();
                } catch (RentexpresException e) {
                        return Collections.emptyList();
                }
        }

        private List<VehicleStatusDTO> loadStatuses(String language) {
                try {
                        List<VehicleStatusDTO> statuses = vehicleStatusService.findAll(language);
                        return statuses != null ? statuses : Collections.emptyList();
                } catch (RentexpresException e) {
                        return Collections.emptyList();
                }
        }

        private Map<Integer, VehicleCategoryDTO> indexVehicleCategories(List<VehicleCategoryDTO> categories) {
                if (categories == null || categories.isEmpty()) {
                        return Collections.emptyMap();
                }

                Map<Integer, VehicleCategoryDTO> map = new HashMap<>();
                for (VehicleCategoryDTO category : categories) {
                        if (category != null && category.getCategoryId() != null) {
                                map.put(category.getCategoryId(), category);
                        }
                }
                return map;
        }

        private Map<Integer, VehicleStatusDTO> indexVehicleStatuses(List<VehicleStatusDTO> statuses) {
                if (statuses == null || statuses.isEmpty()) {
                        return Collections.emptyMap();
                }

                Map<Integer, VehicleStatusDTO> map = new HashMap<>();
                for (VehicleStatusDTO status : statuses) {
                        if (status != null && status.getVehicleStatusId() != null) {
                                map.put(status.getVehicleStatusId(), status);
                        }
                }
                return map;
        }

        private Map<Integer, HeadquartersDTO> indexHeadquarters(List<HeadquartersDTO> headquarters) {
                if (headquarters == null || headquarters.isEmpty()) {
                        return Collections.emptyMap();
                }

                Map<Integer, HeadquartersDTO> map = new HashMap<>();
                for (HeadquartersDTO item : headquarters) {
                        if (item != null && item.getHeadquartersId() != null) {
                                map.put(item.getHeadquartersId(), item);
                        }
                }
                return map;
        }

        private void enrichVehicles(List<VehicleDTO> vehicles, Map<Integer, VehicleCategoryDTO> categoryMap,
                        Map<Integer, VehicleStatusDTO> statusMap, Map<Integer, HeadquartersDTO> headquartersMap) {
                if (vehicles == null || vehicles.isEmpty()) {
                        return;
                }

                for (VehicleDTO vehicle : vehicles) {
                        if (vehicle == null) {
                                continue;
                        }

                        if (vehicle.getCategoryId() != null && vehicle.getVehicleCategory() == null) {
                                VehicleCategoryDTO category = categoryMap.get(vehicle.getCategoryId());
                                if (category != null) {
                                        vehicle.setVehicleCategory(category);
                                }
                        }

                        if (vehicle.getVehicleStatusId() != null && vehicle.getVehicleStatus() == null) {
                                VehicleStatusDTO status = statusMap.get(vehicle.getVehicleStatusId());
                                if (status != null) {
                                        vehicle.setVehicleStatus(status);
                                }
                        }

                        if (vehicle.getCurrentHeadquartersId() != null && vehicle.getCurrentHeadquarters() == null) {
                                HeadquartersDTO headquarters = headquartersMap.get(vehicle.getCurrentHeadquartersId());
                                if (headquarters != null) {
                                        vehicle.setCurrentHeadquarters(headquarters);
                                }
                        }
                }
        }

        private Map<Integer, Boolean> buildVehicleImages(List<VehicleDTO> vehicles) throws RentexpresException {
                if (vehicles == null || vehicles.isEmpty()) {
                        return Collections.emptyMap();
                }

                Map<Integer, Boolean> images = new HashMap<>();
                for (VehicleDTO vehicle : vehicles) {
                        if (vehicle == null || vehicle.getVehicleId() == null) {
                                continue;
                        }

                        List<File> files = fileService.getImagesByVehicleId(vehicle.getVehicleId());
                        if (files != null && !files.isEmpty()) {
                                images.put(vehicle.getVehicleId(), Boolean.TRUE);
                        }
                }
                return images;
        }

        public static class HomeVehiclesData {

                private final List<VehicleDTO> vehicles;
                private final Map<Integer, Boolean> vehicleImages;
                private final boolean hasErrors;

                public HomeVehiclesData(List<VehicleDTO> vehicles, Map<Integer, Boolean> vehicleImages, boolean hasErrors) {
                        this.vehicles = vehicles != null ? vehicles : Collections.emptyList();
                        this.vehicleImages = vehicleImages != null ? vehicleImages : Collections.emptyMap();
                        this.hasErrors = hasErrors;
                }

                public List<VehicleDTO> getVehicles() {
                        return vehicles;
                }

                public Map<Integer, Boolean> getVehicleImages() {
                        return vehicleImages;
                }

                public boolean hasErrors() {
                        return hasErrors;
                }
        }

        public static class VehicleListData {

                private final Results<VehicleDTO> results;
                private final List<VehicleDTO> vehicles;
                private final Map<Integer, Boolean> vehicleImages;
                private final List<VehicleCategoryDTO> categories;
                private final List<VehicleStatusDTO> statuses;
                private final boolean hasErrors;

                public VehicleListData(Results<VehicleDTO> results, List<VehicleDTO> vehicles,
                                Map<Integer, Boolean> vehicleImages, List<VehicleCategoryDTO> categories,
                                List<VehicleStatusDTO> statuses, boolean hasErrors) {
                        this.results = results != null ? results : new Results<>();
                        this.vehicles = vehicles != null ? vehicles : Collections.emptyList();
                        this.vehicleImages = vehicleImages != null ? vehicleImages : Collections.emptyMap();
                        this.categories = categories != null ? categories : Collections.emptyList();
                        this.statuses = statuses != null ? statuses : Collections.emptyList();
                        this.hasErrors = hasErrors;
                }

                public Results<VehicleDTO> getResults() {
                        return results;
                }

                public List<VehicleDTO> getVehicles() {
                        return vehicles;
                }

                public Map<Integer, Boolean> getVehicleImages() {
                        return vehicleImages;
                }

                public List<VehicleCategoryDTO> getCategories() {
                        return categories;
                }

                public List<VehicleStatusDTO> getStatuses() {
                        return statuses;
                }

                public boolean hasErrors() {
                        return hasErrors;
                }
        }
}
