package com.pinguela.rentexpressweb.controller;

import com.pinguela.rentexpres.service.HeadquartersService;
import com.pinguela.rentexpres.service.VehicleCategoryService;
import com.pinguela.rentexpres.service.VehicleService;
import com.pinguela.rentexpres.service.VehicleStatusService;
import com.pinguela.rentexpres.service.impl.HeadquartersServiceImpl;
import com.pinguela.rentexpres.service.impl.VehicleCategoryServiceImpl;
import com.pinguela.rentexpres.service.impl.VehicleServiceImpl;
import com.pinguela.rentexpres.service.impl.VehicleStatusServiceImpl;
import com.pinguela.rentexpressweb.constants.AppConstants;
import com.pinguela.rentexpressweb.constants.VehicleConstants;
import com.pinguela.rentexpressweb.controller.support.PublicVehiclePageBuilder;
import com.pinguela.rentexpressweb.controller.support.PublicVehiclePageData;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

/**
 * Servlet encargado de mostrar el catálogo público de vehículos con filtros básicos.
 */
@WebServlet("/public/vehicles")
public class PublicVehicleServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private final VehicleService vehicleService = new VehicleServiceImpl();
    private final VehicleCategoryService categoryService = new VehicleCategoryServiceImpl();
    private final VehicleStatusService statusService = new VehicleStatusServiceImpl();
    private final HeadquartersService headquartersService = new HeadquartersServiceImpl();

    private transient PublicVehiclePageBuilder pageBuilder;

    public PublicVehicleServlet() {
        super();
    }

    @Override
    public void init() throws ServletException {
        super.init();
        this.pageBuilder = new PublicVehiclePageBuilder(vehicleService, categoryService, statusService, headquartersService);
    }

    /**
     * @see HttpServlet#doGet(HttpServletRequest, HttpServletResponse)
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setAttribute(AppConstants.ATTR_PAGE_TITLE, "Catálogo de vehículos");

        PublicVehiclePageData pageData = pageBuilder.build(request);

        request.setAttribute(VehicleConstants.ATTR_FILTERS, pageData.getFilters());
        request.setAttribute(VehicleConstants.ATTR_FILTER_ERRORS, pageData.getFilterErrors());
        request.setAttribute(VehicleConstants.ATTR_PARAM_NAMES, pageData.getParamNames());
        request.setAttribute(VehicleConstants.ATTR_SORT_VALUES, pageData.getSortValues());
        request.setAttribute(VehicleConstants.ATTR_AVAILABLE_CATEGORIES, pageData.getCategories());
        request.setAttribute(VehicleConstants.ATTR_CATEGORY_NAMES, pageData.getCategoryNames());
        request.setAttribute(VehicleConstants.ATTR_HEADQUARTERS, pageData.getHeadquarters());
        request.setAttribute(VehicleConstants.ATTR_HEADQUARTERS_NAMES, pageData.getHeadquartersNames());
        request.setAttribute(VehicleConstants.ATTR_AVAILABLE_STATUSES, pageData.getStatuses());
        request.setAttribute(VehicleConstants.ATTR_STATUS_NAMES, pageData.getStatusNames());
        request.setAttribute(VehicleConstants.ATTR_PAGE_SIZES, pageData.getPageSizeOptions());
        request.setAttribute(VehicleConstants.ATTR_RESULTS, pageData.getResults());
        request.setAttribute(VehicleConstants.ATTR_VEHICLES, pageData.getVehicles());
        request.setAttribute(VehicleConstants.ATTR_TOTAL_RESULTS, pageData.getTotalResults());
        request.setAttribute(VehicleConstants.ATTR_RESULTS_FROM_ROW, pageData.getFromRow());
        request.setAttribute(VehicleConstants.ATTR_RESULTS_TO_ROW, pageData.getToRow());

        request.getRequestDispatcher("/public/vehicle/catalog.jsp").forward(request, response);
    }

    /**
     * @see HttpServlet#doPost(HttpServletRequest, HttpServletResponse)
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request, response);
    }
}
