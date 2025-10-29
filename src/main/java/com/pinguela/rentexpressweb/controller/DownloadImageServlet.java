package com.pinguela.rentexpressweb.controller;

import com.pinguela.rentexpressweb.constants.MediaConstants;
import com.pinguela.rentexpressweb.util.ImageStorage;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Permite descargar imágenes almacenadas en el servidor como adjuntos.
 */
@WebServlet("/app/images/download")
public class DownloadImageServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private static final Logger LOGGER = LogManager.getLogger(DownloadImageServlet.class);

    public DownloadImageServlet() {
        super();
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String entity = normalize(request.getParameter(MediaConstants.PARAM_ENTITY));
        String entityId = normalize(request.getParameter(MediaConstants.PARAM_ENTITY_ID));
        if (!MediaConstants.isAllowedEntity(entity) || entityId == null || entityId.isEmpty()) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        File image = ImageStorage.resolveImage(getServletContext(), entity, entityId);
        if (image == null || !image.exists()) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        String mimeType = getServletContext().getMimeType(image.getName());
        if (mimeType == null) {
            mimeType = "application/octet-stream";
        }
        response.setContentType(mimeType);
        response.setContentLengthLong(image.length());
        response.setHeader("Cache-Control", "no-store");
        response.setHeader("Pragma", "no-cache");
        response.setDateHeader("Expires", 0);

        String encodedName = URLEncoder.encode(image.getName(), StandardCharsets.UTF_8.name());
        response.setHeader("Content-Disposition", "attachment; filename=\"" + encodedName + "\"");

        OutputStream output = null;
        try {
            output = response.getOutputStream();
            ImageStorage.copyToResponse(image, output);
        } catch (IOException ex) {
            LOGGER.error("Error enviando la imagen {}", image.getAbsolutePath(), ex);
            throw ex;
        } finally {
            if (output != null) {
                try {
                    output.flush();
                } catch (IOException ex) {
                    LOGGER.error("Error liberando el flujo de salida", ex);
                }
            }
        }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request, response);
    }

    private String normalize(String value) {
        if (value == null) {
            return null;
        }
        return value.trim().toLowerCase(Locale.ROOT);
    }
}
