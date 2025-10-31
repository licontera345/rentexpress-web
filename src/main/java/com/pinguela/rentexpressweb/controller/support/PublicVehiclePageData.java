package com.pinguela.rentexpressweb.controller.support;

import com.pinguela.rentexpres.model.HeadquartersDTO;
import com.pinguela.rentexpres.model.Results;
import com.pinguela.rentexpres.model.VehicleCategoryDTO;
import com.pinguela.rentexpres.model.VehicleDTO;
import com.pinguela.rentexpres.model.VehicleStatusDTO;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Representa los datos necesarios para renderizar la página pública de vehículos.
 */
public class PublicVehiclePageData {

    private final Map<String, String> filters;
    private final List<String> filterErrors;
    private final Map<String, String> paramNames;
    private final Map<String, String> sortValues;
    private final List<VehicleCategoryDTO> categories;
    private final Map<Integer, String> categoryNames;
    private final List<HeadquartersDTO> headquarters;
    private final Map<Integer, String> headquartersNames;
    private final List<VehicleStatusDTO> statuses;
    private final Map<Integer, String> statusNames;
    private final List<Integer> pageSizeOptions;
    private final Results<VehicleDTO> results;
    private final List<VehicleDTO> vehicles;
    private final Integer totalResults;
    private final Integer fromRow;
    private final Integer toRow;

    public PublicVehiclePageData(
            Map<String, String> filters,
            List<String> filterErrors,
            Map<String, String> paramNames,
            Map<String, String> sortValues,
            List<VehicleCategoryDTO> categories,
            Map<Integer, String> categoryNames,
            List<HeadquartersDTO> headquarters,
            Map<Integer, String> headquartersNames,
            List<VehicleStatusDTO> statuses,
            Map<Integer, String> statusNames,
            List<Integer> pageSizeOptions,
            Results<VehicleDTO> results,
            List<VehicleDTO> vehicles,
            Integer totalResults,
            Integer fromRow,
            Integer toRow) {
        this.filters = filters != null ? filters : Collections.emptyMap();
        this.filterErrors = filterErrors != null ? filterErrors : Collections.emptyList();
        this.paramNames = paramNames != null ? paramNames : Collections.emptyMap();
        this.sortValues = sortValues != null ? sortValues : Collections.emptyMap();
        this.categories = categories != null ? categories : Collections.emptyList();
        this.categoryNames = categoryNames != null ? categoryNames : Collections.emptyMap();
        this.headquarters = headquarters != null ? headquarters : Collections.emptyList();
        this.headquartersNames = headquartersNames != null ? headquartersNames : Collections.emptyMap();
        this.statuses = statuses != null ? statuses : Collections.emptyList();
        this.statusNames = statusNames != null ? statusNames : Collections.emptyMap();
        this.pageSizeOptions = pageSizeOptions != null ? pageSizeOptions : Collections.emptyList();
        this.results = results;
        this.vehicles = vehicles != null ? vehicles : Collections.emptyList();
        this.totalResults = totalResults;
        this.fromRow = fromRow;
        this.toRow = toRow;
    }

    public Map<String, String> getFilters() {
        return filters;
    }

    public List<String> getFilterErrors() {
        return filterErrors;
    }

    public Map<String, String> getParamNames() {
        return paramNames;
    }

    public Map<String, String> getSortValues() {
        return sortValues;
    }

    public List<VehicleCategoryDTO> getCategories() {
        return categories;
    }

    public Map<Integer, String> getCategoryNames() {
        return categoryNames;
    }

    public List<HeadquartersDTO> getHeadquarters() {
        return headquarters;
    }

    public Map<Integer, String> getHeadquartersNames() {
        return headquartersNames;
    }

    public List<VehicleStatusDTO> getStatuses() {
        return statuses;
    }

    public Map<Integer, String> getStatusNames() {
        return statusNames;
    }

    public List<Integer> getPageSizeOptions() {
        return pageSizeOptions;
    }

    public Results<VehicleDTO> getResults() {
        return results;
    }

    public List<VehicleDTO> getVehicles() {
        return vehicles;
    }

    public Integer getTotalResults() {
        return totalResults;
    }

    public Integer getFromRow() {
        return fromRow;
    }

    public Integer getToRow() {
        return toRow;
    }
}
