package com.pinguela.rentexpressweb.controller;

import com.pinguela.rentexpressweb.constants.AppConstants;
import com.pinguela.rentexpressweb.constants.MediaConstants;
import com.pinguela.rentexpressweb.constants.SecurityConstants;
import com.pinguela.rentexpressweb.security.SessionManager;
import com.pinguela.rentexpressweb.util.ImageStorage;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Centraliza la subida y descarga de imágenes bajo el paquete de seguridad.
 */
@WebServlet({ UploadServlet.UPLOAD_PATH, UploadServlet.DOWNLOAD_PATH })
@MultipartConfig
public class UploadServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    static final String UPLOAD_PATH = "/app/images/upload";
    static final String DOWNLOAD_PATH = "/app/images/download";

    private static final Logger LOGGER = LogManager.getLogger(UploadServlet.class);

    public UploadServlet() {
        super();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (DOWNLOAD_PATH.equals(request.getServletPath())) {
            handleDownload(request, response);
        } else {
            response.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (UPLOAD_PATH.equals(request.getServletPath())) {
            handleUpload(request, response);
        } else if (DOWNLOAD_PATH.equals(request.getServletPath())) {
            handleDownload(request, response);
        } else {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    private void handleUpload(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Map<String, String> errors = new LinkedHashMap<String, String>();
        String entity = sanitizeEntity(request.getParameter(MediaConstants.PARAM_ENTITY));
        String entityId = trimToNull(request.getParameter(MediaConstants.PARAM_ENTITY_ID));

        if (!MediaConstants.isAllowedEntity(entity)) {
            errors.put(MediaConstants.PARAM_ENTITY, "Entidad de imagen no reconocida.");
        }
        if (entityId == null || entityId.isEmpty()) {
            errors.put(MediaConstants.PARAM_ENTITY_ID, "Identificador no válido.");
        }

        Part filePart = null;
        try {
            filePart = request.getPart(MediaConstants.PARAM_IMAGE_FILE);
        } catch (IllegalStateException ex) {
            LOGGER.error("El fichero excede el límite permitido", ex);
            errors.put(MediaConstants.PARAM_IMAGE_FILE, "El fichero es demasiado grande.");
        }

        if (filePart == null || filePart.getSize() == 0) {
            errors.put(MediaConstants.PARAM_IMAGE_FILE, "Selecciona un fichero para subir.");
        } else if (filePart.getSize() > MediaConstants.MAX_IMAGE_SIZE_BYTES) {
            errors.put(MediaConstants.PARAM_IMAGE_FILE, "La imagen supera el tamaño permitido (2MB).");
        }

        if (!errors.isEmpty()) {
            notifyFailure(request, errors);
            response.sendRedirect(resolveRedirect(request));
            return;
        }

        try {
            String stored = ImageStorage.store(getServletContext(), filePart, entity, entityId);
            SessionManager.setAttribute(request, MediaConstants.ATTR_IMAGE_PATH, stored);
            SessionManager.setAttribute(request, AppConstants.ATTR_FLASH_SUCCESS,
                    "Imagen almacenada correctamente.");
        } catch (IOException ex) {
            LOGGER.error("Error almacenando imagen para {}:{}", entity, entityId, ex);
            Map<String, String> failure = new LinkedHashMap<String, String>();
            failure.put(MediaConstants.PARAM_IMAGE_FILE, "No se pudo guardar la imagen en el servidor.");
            notifyFailure(request, failure);
        }

        response.sendRedirect(resolveRedirect(request));
    }

    private void handleDownload(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
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

    private void notifyFailure(HttpServletRequest request, Map<String, String> errors) {
        StringBuilder builder = new StringBuilder();
        for (Map.Entry<String, String> entry : errors.entrySet()) {
            if (builder.length() > 0) {
                builder.append(' ');
            }
            builder.append(entry.getValue());
        }
        SessionManager.setAttribute(request, AppConstants.ATTR_FLASH_ERROR, builder.toString());
        SessionManager.setAttribute(request, MediaConstants.ATTR_IMAGE_ERRORS, errors);
    }

    private String resolveRedirect(HttpServletRequest request) {
        String target = request.getParameter(MediaConstants.PARAM_REDIRECT);
        if (target != null && !target.trim().isEmpty()) {
            return target;
        }
        String referer = request.getHeader("Referer");
        if (referer != null && !referer.trim().isEmpty()) {
            return referer;
        }
        return request.getContextPath() + SecurityConstants.HOME_ENDPOINT;
    }

    private String sanitizeEntity(String value) {
        if (value == null) {
            return null;
        }
        return value.trim().toLowerCase(Locale.ROOT);
    }

    private String normalize(String value) {
        if (value == null) {
            return null;
        }
        return value.trim().toLowerCase(Locale.ROOT);
    }

    private String trimToNull(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }
}
