package com.argus.shared.model;

import com.argus.shared.error.Guard;
import com.argus.shared.error.InvalidInputException;

/** Méthode HTTP d'un endpoint. */
public enum HttpMethod {
    GET, POST, PUT, PATCH, DELETE, HEAD, OPTIONS;

    /** Lecture tolérante (casse et espaces ignorés) : {@code parse(" post ")} donne {@link #POST}. */
    public static HttpMethod parse(String text) {
        Guard.notBlank(text, "HttpMethod");
        try {
            return valueOf(text.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw InvalidInputException.forField("HttpMethod", "méthode inconnue : \"" + text + "\"");
        }
    }
}
