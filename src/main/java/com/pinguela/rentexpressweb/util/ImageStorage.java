package com.pinguela.rentexpressweb.util;

import com.pinguela.rentexpressweb.constants.MediaConstants;
import jakarta.servlet.ServletContext;
import jakarta.servlet.http.Part;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Locale;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Utilidad para almacenar y recuperar imágenes en el sistema de ficheros del
 * contenedor.
 */
public final class ImageStorage {

    private static final Logger LOGGER = LogManager.getLogger(ImageStorage.class);

    private ImageStorage() {
    }

    public static File resolveImage(ServletContext context, String entityType, String identifier) {
        if (context == null || entityType == null || identifier == null) {
            return null;
        }
        File entityDirectory = new File(resolveBaseDirectory(context), normalize(entityType));
        if (!entityDirectory.exists() || !entityDirectory.isDirectory()) {
            return null;
        }
        File[] matches = entityDirectory.listFiles(new PrefixFileFilter(identifier));
        if (matches == null || matches.length == 0) {
            return null;
        }
        return matches[0];
    }

    public static String store(ServletContext context, Part part, String entityType, String identifier) throws IOException {
        if (context == null || part == null || entityType == null || identifier == null) {
            return null;
        }
        String normalizedEntity = normalize(entityType);
        if (!MediaConstants.isAllowedEntity(normalizedEntity)) {
            throw new IOException("Tipo de entidad no permitido: " + entityType);
        }
        String extension = extractExtension(part.getSubmittedFileName());
        if (extension == null || !MediaConstants.isAllowedExtension(extension)) {
            extension = "jpg";
        }
        File baseDir = resolveBaseDirectory(context);
        File entityDir = new File(baseDir, normalizedEntity);
        if (!entityDir.exists()) {
            if (!entityDir.mkdirs()) {
                throw new IOException("No se pudo crear el directorio para " + normalizedEntity);
            }
        }
        purgePreviousFiles(entityDir, identifier);

        File target = new File(entityDir, identifier + "." + extension);
        InputStream input = null;
        OutputStream output = null;
        try {
            input = part.getInputStream();
            output = new FileOutputStream(target);
            byte[] buffer = new byte[MediaConstants.BUFFER_SIZE];
            int read = input.read(buffer);
            while (read != -1) {
                output.write(buffer, 0, read);
                read = input.read(buffer);
            }
        } finally {
            if (output != null) {
                try {
                    output.close();
                } catch (IOException closeEx) {
                    LOGGER.error("Error cerrando la salida al almacenar la imagen", closeEx);
                }
            }
            if (input != null) {
                try {
                    input.close();
                } catch (IOException closeEx) {
                    LOGGER.error("Error cerrando la entrada al almacenar la imagen", closeEx);
                }
            }
        }
        return MediaConstants.MEDIA_BASE_PATH + "/" + normalizedEntity + "/" + target.getName();
    }

    public static void copyToResponse(File source, OutputStream output) throws IOException {
        if (source == null || output == null) {
            return;
        }
        InputStream input = null;
        try {
            input = new FileInputStream(source);
            byte[] buffer = new byte[MediaConstants.BUFFER_SIZE];
            int read = input.read(buffer);
            while (read != -1) {
                output.write(buffer, 0, read);
                read = input.read(buffer);
            }
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException closeEx) {
                    LOGGER.error("Error cerrando la lectura de la imagen", closeEx);
                }
            }
        }
    }

    private static File resolveBaseDirectory(ServletContext context) {
        String realPath = context.getRealPath(MediaConstants.MEDIA_BASE_PATH);
        File baseDir;
        if (realPath != null) {
            baseDir = new File(realPath);
        } else {
            String tempDir = System.getProperty("java.io.tmpdir");
            baseDir = new File(tempDir, "rentexpress-media");
        }
        if (!baseDir.exists()) {
            baseDir.mkdirs();
        }
        return baseDir;
    }

    private static void purgePreviousFiles(File directory, String identifier) {
        File[] matches = directory.listFiles(new PrefixFileFilter(identifier));
        if (matches == null) {
            return;
        }
        for (int i = 0; i < matches.length; i++) {
            File candidate = matches[i];
            if (candidate != null && candidate.isFile()) {
                if (!candidate.delete()) {
                    LOGGER.warn("No se pudo eliminar la imagen anterior {}", candidate.getAbsolutePath());
                }
            }
        }
    }

    private static String extractExtension(String fileName) {
        if (fileName == null) {
            return null;
        }
        String sanitized = fileName.trim();
        int lastDot = sanitized.lastIndexOf('.');
        if (lastDot == -1 || lastDot == sanitized.length() - 1) {
            return null;
        }
        return sanitized.substring(lastDot + 1).toLowerCase(Locale.ROOT);
    }

    private static String normalize(String value) {
        if (value == null) {
            return null;
        }
        return value.trim().toLowerCase(Locale.ROOT);
    }

    private static final class PrefixFileFilter implements FilenameFilter {

        private final String identifier;

        private PrefixFileFilter(String identifier) {
            this.identifier = identifier;
        }

        public boolean accept(File dir, String name) {
            if (identifier == null || name == null) {
                return false;
            }
            if (!name.toLowerCase(Locale.ROOT).startsWith(identifier.toLowerCase(Locale.ROOT) + ".")) {
                return false;
            }
            int lastDot = name.lastIndexOf('.');
            if (lastDot == -1) {
                return false;
            }
            String extension = name.substring(lastDot + 1);
            return MediaConstants.isAllowedExtension(extension);
        }
    }
}
