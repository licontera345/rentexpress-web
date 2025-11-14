package com.pinguela.rentexpressweb.controller;

import java.io.IOException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.pinguela.rentexpres.exception.DataException;
import com.pinguela.rentexpres.exception.RentexpresException;
import com.pinguela.rentexpres.model.HeadquartersDTO;
import com.pinguela.rentexpres.model.Results;
import com.pinguela.rentexpres.model.VehicleCategoryDTO;
import com.pinguela.rentexpres.model.VehicleCriteria;
import com.pinguela.rentexpres.model.VehicleDTO;
import com.pinguela.rentexpres.model.VehicleStatusDTO;
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
import com.pinguela.rentexpressweb.util.MessageResolver;
import com.pinguela.rentexpressweb.util.PaginationUtils;
import com.pinguela.rentexpressweb.util.RequestHelper;
import com.pinguela.rentexpressweb.util.SessionManager;
import com.pinguela.rentexpressweb.util.Views;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/private/VehicleServlet")
public class PrivateVehicleServlet extends AbstractManagementServlet {

    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = LogManager.getLogger(PrivateVehicleServlet.class);
    private static final String GENERIC_ERROR_KEY = "vehicle.manage.flash.error";
    private static final String DELETE_CONSTRAINT_ERROR_KEY = "vehicle.manage.flash.delete.constraint";
    private static final int DEFAULT_PAGE_SIZE = 10;

    private final VehicleService vehicleService;
    private final VehicleCategoryService categoryService;
    private final VehicleStatusService statusService;
    private final HeadquartersService headquartersService;

    /*
     * Construye el servlet utilizando las implementaciones estándar de los
     * servicios implicados en la gestión de vehículos.
     */
    public PrivateVehicleServlet() {
        this(new VehicleServiceImpl(), new VehicleCategoryServiceImpl(), new VehicleStatusServiceImpl(),
                new HeadquartersServiceImpl());
    }

    /*
     * Permite inyectar servicios personalizados y configura las acciones CRUD que
     * la clase base debe reconocer.
     */
    PrivateVehicleServlet(VehicleService vehicleService, VehicleCategoryService categoryService,
            VehicleStatusService statusService, HeadquartersService headquartersService) {
        super(AppConstants.CREATE_VEHICLE, AppConstants.UPDATE_VEHICLE, AppConstants.DELETE_VEHICLE);
        this.vehicleService = vehicleService;
        this.categoryService = categoryService;
        this.statusService = statusService;
        this.headquartersService = headquartersService;
    }

    @Override
    /*
     * Obtiene el identificador del vehículo a partir de los parámetros de la
     * petición.
     */
    protected Integer resolveEntityId(HttpServletRequest request) {
        return RequestHelper.parseInteger(param(request, VehicleConstants.PARAM_VEHICLE_ID));
    }

    @Override
    /*
     * Carga el listado de vehículos aplicando filtros, paginación y datos de
     * referencia para la vista.
     */
    protected void loadList(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            int pageNumber = PaginationUtils.resolvePage(request);
            int pageSize = PaginationUtils.resolvePageSize(request, AppConstants.ATTR_PAGE_SIZE, DEFAULT_PAGE_SIZE);

            VehicleCriteria criteria = buildCriteria(request, pageNumber, pageSize);

            Results<VehicleDTO> results = vehicleService.findByCriteria(criteria);
            PaginationUtils.apply(request, results, pageNumber, pageSize, VehicleConstants.ATTR_VEHICLES);

            loadReferenceData(request);

            request.setAttribute("filterSearch", param(request, VehicleConstants.PARAM_SEARCH));
            request.setAttribute("filterCategoryId", param(request, VehicleConstants.PARAM_CATEGORY));
            request.setAttribute("filterStatusId", param(request, VehicleConstants.PARAM_STATUS_ID));
            request.setAttribute("filterHeadquartersId", param(request, VehicleConstants.PARAM_HEADQUARTERS_ID));
            request.setAttribute("filterLicensePlate", param(request, VehicleConstants.PARAM_LICENSE_PLATE));
            request.setAttribute("filterPriceMin", param(request, VehicleConstants.PARAM_MIN_PRICE));
            request.setAttribute("filterPriceMax", param(request, VehicleConstants.PARAM_MAX_PRICE));
            request.setAttribute("filterYearFrom", param(request, VehicleConstants.PARAM_MIN_YEAR));
            request.setAttribute("filterYearTo", param(request, VehicleConstants.PARAM_MAX_YEAR));

            transferFlashAttributes(request);
            forward(request, response, Views.PRIVATE_VEHICLE_LIST);
        } catch (RentexpresException | DataException ex) {
            LOGGER.error("Unable to load vehicles", ex);
            SessionManager.set(request, AppConstants.ATTR_FLASH_ERROR,
                    MessageResolver.getMessage(request, GENERIC_ERROR_KEY));
            transferFlashAttributes(request);
            forward(request, response, Views.PRIVATE_VEHICLE_LIST);
        }
    }

    @Override
    /*
     * Prepara el formulario de alta o edición de vehículos incluyendo los catálogos
     * necesarios y gestionando redirecciones ante incidencias.
     */
    protected void showForm(HttpServletRequest request, HttpServletResponse response, Integer vehicleId)
            throws ServletException, IOException {
        try {
            VehicleDTO vehicle = vehicleId != null ? vehicleService.findById(vehicleId) : new VehicleDTO();
            loadReferenceData(request);

            request.setAttribute(VehicleConstants.ATTR_VEHICLE, vehicle);
            transferFlashAttributes(request);

            forward(request, response, Views.PRIVATE_VEHICLE_FORM);
        } catch (RentexpresException | DataException ex) {
            LOGGER.error("Unable to load vehicle form", ex);
            SessionManager.set(request, AppConstants.ATTR_FLASH_ERROR,
                    MessageResolver.getMessage(request, GENERIC_ERROR_KEY));
            response.sendRedirect(request.getContextPath() + "/private/VehicleServlet");
        }
    }

    @Override
    /*
     * Atiende la creación de un nuevo vehículo reutilizando la lógica común de
     * persistencia.
     */
    protected void handleCreate(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processPersist(request, response, true);
    }

    @Override
    /*
     * Gestiona la actualización de un vehículo existente apoyándose en la lógica
     * compartida de procesamiento.
     */
    protected void handleUpdate(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processPersist(request, response, false);
    }

    /*
     * Gestiona la operación de alta o modificación delegando completamente la
     * validación en el middleware.
     */
    private void processPersist(HttpServletRequest request, HttpServletResponse response, boolean creating)
            throws ServletException, IOException {
        VehicleDTO vehicle = buildVehicle(request);

        String successKey = creating ? "vehicle.manage.flash.created" : "vehicle.manage.flash.updated";

        try {
            boolean success = creating ? vehicleService.create(vehicle) : vehicleService.update(vehicle);
            if (success) {
                SessionManager.set(request, AppConstants.ATTR_FLASH_SUCCESS,
                        MessageResolver.getMessage(request, successKey));
            } else {
                SessionManager.set(request, AppConstants.ATTR_FLASH_ERROR,
                        MessageResolver.getMessage(request, GENERIC_ERROR_KEY));
            }
        } catch (RentexpresException ex) {
            String logAction = creating ? "create" : "update";
            LOGGER.error("Unable to {} vehicle", logAction, ex);
            SessionManager.set(request, AppConstants.ATTR_FLASH_ERROR,
                    MessageResolver.getMessage(request, GENERIC_ERROR_KEY));
        }
        response.sendRedirect(request.getContextPath() + "/private/VehicleServlet");
    }

    @Override
    /*
     * Realiza el borrado lógico del vehículo indicado, informando del resultado al
     * usuario.
     */
    protected void handleDelete(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Integer vehicleId = RequestHelper.parseInteger(param(request, VehicleConstants.PARAM_VEHICLE_ID));

        try {
            if (vehicleId != null && vehicleService.delete(vehicleId)) {
                SessionManager.set(request, AppConstants.ATTR_FLASH_SUCCESS,
                        MessageResolver.getMessage(request, "vehicle.manage.flash.deleted"));
            } else {
                SessionManager.set(request, AppConstants.ATTR_FLASH_ERROR,
                        MessageResolver.getMessage(request, GENERIC_ERROR_KEY));
            }
        } catch (RentexpresException ex) {
            LOGGER.error("Unable to delete vehicle", ex);
            String messageKey = isConstraintViolation(ex) ? DELETE_CONSTRAINT_ERROR_KEY : GENERIC_ERROR_KEY;
            SessionManager.set(request, AppConstants.ATTR_FLASH_ERROR,
                    MessageResolver.getMessage(request, messageKey));
        }
        response.sendRedirect(request.getContextPath() + "/private/VehicleServlet");
    }

    private boolean isConstraintViolation(Throwable throwable) {
        Throwable cause = throwable;
        while (cause != null) {
            if (cause instanceof SQLIntegrityConstraintViolationException) {
                return true;
            }
            cause = cause.getCause();
        }
        return false;
    }

    /*
     * Construye el criterio de búsqueda a partir de los parámetros recibidos.
     */
    private VehicleCriteria buildCriteria(HttpServletRequest request, int pageNumber, int pageSize) {
        VehicleCriteria criteria = new VehicleCriteria();
        criteria.setPageNumber(pageNumber);
        criteria.setPageSize(pageSize);
        String brand = param(request, VehicleConstants.PARAM_SEARCH);
        if (brand == null) {
            brand = param(request, VehicleConstants.PARAM_BRAND);
        }
        criteria.setBrand(brand);
        criteria.setCategoryId(RequestHelper.parseInteger(param(request, VehicleConstants.PARAM_CATEGORY)));
        criteria.setVehicleStatusId(RequestHelper.parseInteger(param(request, VehicleConstants.PARAM_STATUS_ID)));
        criteria.setCurrentHeadquartersId(
                RequestHelper.parseInteger(param(request, VehicleConstants.PARAM_HEADQUARTERS_ID)));
        criteria.setLicensePlate(param(request, VehicleConstants.PARAM_LICENSE_PLATE));
        criteria.setDailyPriceMin(RequestHelper.parseBigDecimal(param(request, VehicleConstants.PARAM_MIN_PRICE)));
        criteria.setDailyPriceMax(RequestHelper.parseBigDecimal(param(request, VehicleConstants.PARAM_MAX_PRICE)));
        criteria.setManufactureYearFrom(RequestHelper.parseInteger(param(request, VehicleConstants.PARAM_MIN_YEAR)));
        criteria.setManufactureYearTo(RequestHelper.parseInteger(param(request, VehicleConstants.PARAM_MAX_YEAR)));
        return criteria;
    }

    private VehicleDTO buildVehicle(HttpServletRequest request) {
        VehicleDTO vehicle = new VehicleDTO();
        vehicle.setVehicleId(RequestHelper.parseInteger(param(request, VehicleConstants.PARAM_VEHICLE_ID)));
        vehicle.setBrand(param(request, VehicleConstants.PARAM_BRAND));
        vehicle.setModel(param(request, VehicleConstants.PARAM_MODEL));
        vehicle.setManufactureYear(RequestHelper.parseInteger(param(request, VehicleConstants.PARAM_MANUFACTURE_YEAR)));
        vehicle.setDailyPrice(RequestHelper.parseBigDecimal(param(request, VehicleConstants.PARAM_DAILY_PRICE)));
        vehicle.setLicensePlate(param(request, VehicleConstants.PARAM_LICENSE_PLATE));
        vehicle.setVinNumber(param(request, VehicleConstants.PARAM_VIN));
        vehicle.setCurrentMileage(RequestHelper.parseInteger(param(request, VehicleConstants.PARAM_CURRENT_MILEAGE)));
        vehicle.setVehicleStatusId(RequestHelper.parseInteger(param(request, VehicleConstants.PARAM_STATUS_ID)));
        vehicle.setCategoryId(RequestHelper.parseInteger(param(request, VehicleConstants.PARAM_CATEGORY)));
        vehicle.setCurrentHeadquartersId(RequestHelper.parseInteger(param(request, VehicleConstants.PARAM_HEADQUARTERS_ID)));
        return vehicle;
    }

    private void loadReferenceData(HttpServletRequest request) throws RentexpresException, DataException {
        String language = resolveLanguage(request);
        List<VehicleCategoryDTO> categories = categoryService.findAll(language);
        List<VehicleStatusDTO> statuses = statusService.findAll(language);
        List<HeadquartersDTO> headquarters = headquartersService.findAll();

        request.setAttribute(VehicleConstants.ATTR_CATEGORIES, categories);
        request.setAttribute(VehicleConstants.ATTR_STATUSES, statuses);
        request.setAttribute(AppConstants.ATTR_HEADQUARTERS, headquarters);
    }

    @Override
    /*
     * Devuelve el logger específico de este servlet para cumplir con la jerarquía
     * de controladores.
     */
    protected Logger getLogger() {
        return LOGGER;
    }

}
