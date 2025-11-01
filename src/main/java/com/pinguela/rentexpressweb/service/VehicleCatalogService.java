package com.pinguela.rentexpressweb.service;

import com.pinguela.rentexpres.exception.RentexpresException;
import com.pinguela.rentexpres.model.VehicleCriteria;
import com.pinguela.rentexpres.model.VehicleDTO;
import com.pinguela.rentexpres.model.Results;
import com.pinguela.rentexpres.service.impl.VehicleServiceImpl;

/**
 * Servicio específico para el catálogo público que añade soporte
 * a la paginación opcional a partir de los parámetros recibidos.
 */
public class VehicleCatalogService extends VehicleServiceImpl {

    public Results<VehicleDTO> findByCriteria(VehicleCriteria criteria, Integer page, Integer size)
            throws RentexpresException {
        VehicleCriteria effectiveCriteria = criteria != null ? criteria : new VehicleCriteria();

        if (page != null && page.intValue() > 0) {
            effectiveCriteria.setPageNumber(page);
        }
        if (size != null && size.intValue() > 0) {
            effectiveCriteria.setPageSize(size);
        }

        return super.findByCriteria(effectiveCriteria);
    }
}
