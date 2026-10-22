package com.argus.shared.id;

import com.argus.shared.error.Guard;
import com.argus.shared.error.InvalidInputException;
import com.argus.shared.model.HttpMethod;
import java.util.regex.Pattern;

/**
 * Identifiant d'un endpoint : méthode HTTP + chemin normalisé, ex. {@code GET /orders/{id}}.
 *
 * <p>Normalisation : espaces retirés aux extrémités, barre oblique initiale ajoutée si absente,
 * barre oblique finale retirée (sauf pour la racine). La casse du chemin est conservée.
 * Les paramètres de chemin restent sous forme de gabarit ({@code {id}}). Pas de query string.
 */
public record EndpointId(HttpMethod method, String path) implements Identifier<String> {

    private static final Pattern VALID_PATH = Pattern.compile("^/[^\\s?#]*$");

    public EndpointId {
        Guard.notNull(method, "method");
        Guard.notBlank(path, "path");
        path = normalize(path);
        Guard.matches(path, VALID_PATH, "path", "chemin sans espace, '?' ni '#', ex. /orders/{id}");
    }

    /** Lit « GET /orders/{id} ». */
    public static EndpointId parse(String text) {
        Guard.notBlank(text, "EndpointId");
        String[] parts = text.trim().split("\\s+", 2);
        if (parts.length != 2) {
            throw InvalidInputException.forField("EndpointId", "attendu « METHODE /chemin », reçu : \"" + text + "\"");
        }
        return new EndpointId(HttpMethod.parse(parts[0]), parts[1]);
    }

    private static String normalize(String raw) {
        String p = raw.trim();
        if (!p.startsWith("/")) {
            p = "/" + p;
        }
        while (p.length() > 1 && p.endsWith("/")) {
            p = p.substring(0, p.length() - 1);
        }
        return p;
    }

    /** Forme texte canonique, ex. {@code GET /orders/{id}}. */
    @Override
    public String value() {
        return method + " " + path;
    }

    @Override
    public String toString() {
        return value();
    }
}
