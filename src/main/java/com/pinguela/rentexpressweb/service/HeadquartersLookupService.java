package com.pinguela.rentexpressweb.service;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

import com.pinguela.rentexpres.dao.HeadquartersDAO;
import com.pinguela.rentexpres.exception.DataException;
import com.pinguela.rentexpres.exception.RentexpresException;
import com.pinguela.rentexpres.model.HeadquartersDTO;
import com.pinguela.rentexpres.util.JDBCUtils;

/**
 * Service wrapper that centralizes JDBC-backed HeadquartersDAO operations used by the web layer.
 */
public class HeadquartersLookupService {

        private final HeadquartersDAO headquartersDAO;

        public HeadquartersLookupService(HeadquartersDAO headquartersDAO) {
                this.headquartersDAO = headquartersDAO;
        }

        public List<HeadquartersDTO> findAll() throws RentexpresException {
                Connection connection = null;
                try {
                        connection = JDBCUtils.getConnection();
                        JDBCUtils.beginTransaction(connection);

                        List<HeadquartersDTO> headquarters = headquartersDAO.findAll(connection);

                        JDBCUtils.commitTransaction(connection);
                        return headquarters != null ? headquarters : Collections.emptyList();
                } catch (SQLException | DataException e) {
                        JDBCUtils.rollbackTransaction(connection);
                        throw new RentexpresException("Error fetching headquarters list", e);
                } finally {
                        JDBCUtils.close(connection);
                }
        }

        public HeadquartersDTO findById(Integer headquartersId) throws RentexpresException {
                if (headquartersId == null) {
                        return null;
                }

                Connection connection = null;
                try {
                        connection = JDBCUtils.getConnection();
                        JDBCUtils.beginTransaction(connection);

                        HeadquartersDTO headquarters = headquartersDAO.findById(connection, headquartersId);

                        JDBCUtils.commitTransaction(connection);
                        return headquarters;
                } catch (SQLException | DataException e) {
                        JDBCUtils.rollbackTransaction(connection);
                        throw new RentexpresException("Error fetching headquarters by id", e);
                } finally {
                        JDBCUtils.close(connection);
                }
        }
}
