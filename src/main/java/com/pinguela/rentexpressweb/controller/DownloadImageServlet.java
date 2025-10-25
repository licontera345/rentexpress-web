package com.pinguela.rentexpressweb.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;

import com.pinguela.rentexpres.exception.RentexpresException;
import com.pinguela.rentexpres.service.FileService;
import com.pinguela.rentexpres.service.impl.FileServiceImpl;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/DownloadImageServlet")
public class DownloadImageServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private final FileService fileService = new FileServiceImpl();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {

        final String idStr = request.getParameter("id");
        if (idStr == null) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Falta parámetro id");
            return;
        }

        final Integer idUsuario;
        try {
            idUsuario = Integer.valueOf(idStr);
        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "id inválido");
            return;
        }

        final File f;
        try {
            f = fileService.getImageByEmployeeId(idUsuario);
        } catch (RentexpresException e) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "No se pudo recuperar la imagen");
            return;
        }

        if (f == null) {
            response.setStatus(HttpServletResponse.SC_NO_CONTENT);
            return;
        }

        if (!f.exists() || !f.isFile()) {
            response.setStatus(HttpServletResponse.SC_NO_CONTENT);
            return;
        }

        String mime = Files.probeContentType(f.toPath());
        if (mime == null)
            mime = "application/octet-stream";
        response.setContentType(mime);
        response.setContentLengthLong(f.length());

        try (FileInputStream in = new FileInputStream(f); OutputStream out = response.getOutputStream()) {
            byte[] buf = new byte[8192];
            int r;
            while ((r = in.read(buf)) != -1) {
                out.write(buf, 0, r);
            }
            out.flush();
        }
    }
}
