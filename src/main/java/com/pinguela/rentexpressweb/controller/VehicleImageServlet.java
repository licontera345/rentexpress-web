package com.pinguela.rentexpressweb.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.util.List;

import com.pinguela.rentexpres.exception.RentexpresException;
import com.pinguela.rentexpres.service.FileService;
import com.pinguela.rentexpres.service.impl.FileServiceImpl;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/public/vehicle-image")
public class VehicleImageServlet extends HttpServlet {

        private static final long serialVersionUID = 1L;

        private final FileService fileService = new FileServiceImpl();

        @Override
        protected void doGet(HttpServletRequest request, HttpServletResponse response)
                        throws ServletException, IOException {

                final String vehicleIdParam = request.getParameter("vehicleId");
                if (vehicleIdParam == null) {
                        response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing vehicleId parameter");
                        return;
                }

                final Integer vehicleId;
                try {
                        vehicleId = Integer.valueOf(vehicleIdParam);
                } catch (NumberFormatException e) {
                        response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid vehicleId parameter");
                        return;
                }

                try {
                        List<File> images = fileService.getImagesByVehicleId(vehicleId);

                        if (images == null || images.isEmpty()) {
                                response.setStatus(HttpServletResponse.SC_NO_CONTENT);
                                return;
                        }

                        File image = images.get(0);
                        if (!image.exists() || !image.isFile()) {
                                response.setStatus(HttpServletResponse.SC_NO_CONTENT);
                                return;
                        }

                        String mimeType = Files.probeContentType(image.toPath());
                        if (mimeType == null) {
                                mimeType = "application/octet-stream";
                        }

                        response.setContentType(mimeType);
                        response.setContentLengthLong(image.length());

                        try (FileInputStream inputStream = new FileInputStream(image);
                                        OutputStream outputStream = response.getOutputStream()) {
                                byte[] buffer = new byte[8192];
                                int bytesRead;
                                while ((bytesRead = inputStream.read(buffer)) != -1) {
                                        outputStream.write(buffer, 0, bytesRead);
                                }
                                outputStream.flush();
                        }
                } catch (RentexpresException e) {
                        response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                }
        }
}
