package com.pinguela.rentexpressweb.constants;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

/**
 * Constantes relacionadas con la gestión de ficheros multimedia.
 */
public final class MediaConstants {

    private MediaConstants() {
    }

    public static final String MEDIA_BASE_PATH = "/media";
    public static final String PARAM_ENTITY = "entity";
    public static final String PARAM_ENTITY_ID = "entityId";
    public static final String PARAM_IMAGE_FILE = "image";
    public static final String PARAM_REDIRECT = "redirect";
    public static final String ATTR_IMAGE_PATH = "imagePath";
    public static final String ATTR_IMAGE_ERRORS = "imageErrors";

    public static final String VALUE_ENTITY_USER = "user";
    public static final String VALUE_ENTITY_EMPLOYEE = "employee";
    public static final String VALUE_ENTITY_VEHICLE = "vehicle";

    public static final int MAX_IMAGE_SIZE_BYTES = 2 * 1024 * 1024; // 2 MB
    public static final int BUFFER_SIZE = 8192;

    private static final Set<String> ALLOWED_ENTITIES = Collections.unmodifiableSet(new HashSet<String>(Arrays.asList(
            VALUE_ENTITY_USER,
            VALUE_ENTITY_EMPLOYEE,
            VALUE_ENTITY_VEHICLE)));

    private static final Set<String> ALLOWED_EXTENSIONS = Collections
            .unmodifiableSet(new HashSet<String>(Arrays.asList("jpg", "jpeg", "png", "gif", "webp")));

    public static boolean isAllowedEntity(String entity) {
        if (entity == null) {
            return false;
        }
        return ALLOWED_ENTITIES.contains(entity.trim().toLowerCase(Locale.ROOT));
    }

    public static boolean isAllowedExtension(String extension) {
        if (extension == null) {
            return false;
        }
        return ALLOWED_EXTENSIONS.contains(extension.trim().toLowerCase(Locale.ROOT));
    }
}
