package com.pinguela.rentexpressweb.controller;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import com.pinguela.rentexpres.exception.RentexpresException;
import com.pinguela.rentexpres.service.FileService;
import com.pinguela.rentexpres.service.impl.FileServiceImpl;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;

@WebServlet("/UploadImageServlet")
@MultipartConfig(maxFileSize = 5 * 1024 * 1024, // 5 MB por fichero
                maxRequestSize = 10 * 1024 * 1024) // 10 MB por request
public class UploadImageServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private final FileService fileService = new FileServiceImpl();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {

        request.setCharacterEncoding("UTF-8");

        final String idStr = request.getParameter("employeeId");
        if (idStr == null) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Falta employeeId");
            return;
        }

        final Integer employeeId;
        try {
            employeeId = Integer.valueOf(idStr);
        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "employeeId inválido");
            return;
        }

        Part part = request.getPart("imagen");
        if (part == null || part.getSize() == 0) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Falta imagen");
            return;
        }

        String contentType = part.getContentType();
        if (contentType == null
                || !(contentType.equalsIgnoreCase("image/jpeg") || contentType.equalsIgnoreCase("image/png"))) {
            response.sendError(HttpServletResponse.SC_UNSUPPORTED_MEDIA_TYPE, "Sólo JPG o PNG");
            return;
        }

        File tmp = File.createTempFile("upload_", ".bin");
        try (InputStream in = part.getInputStream(); FileOutputStream out = new FileOutputStream(tmp)) {
            byte[] buf = new byte[8192];
            int r;
            while ((r = in.read(buf)) != -1) {
                out.write(buf, 0, r);
            }
            out.flush();

            fileService.uploadImageByEmployeeId(tmp, employeeId);
        } catch (RentexpresException e) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "No se pudo guardar la imagen");
            return;
        } finally {
            // Limpieza del temporal
            if (!tmp.delete()) {
                tmp.deleteOnExit();
            }
        }

        // Volver al detalle
        response.sendRedirect(request.getContextPath() + "/public/EmployeeServlet?action=detail&id=" + employeeId);
    }
}
