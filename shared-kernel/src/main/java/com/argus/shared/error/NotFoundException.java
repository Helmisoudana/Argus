package com.argus.shared.error;

/** Une ressource demandée n'existe pas (run, projet, endpoint, rapport...). */
public class NotFoundException extends argusException {

    private static final long serialVersionUID = 1L;

    private final String resourceType;
    private final String resourceId;

    public NotFoundException(String resourceType, String resourceId) {
        super(ErrorCode.NOT_FOUND, "%s introuvable : %s".formatted(resourceType, resourceId));
        this.resourceType = resourceType;
        this.resourceId = resourceId;
    }

    /** Type métier de la ressource, ex. {@code "Run"}. */
    public String resourceType() {
        return resourceType;
    }

    /** Identifiant recherché, sous forme de texte. */
    public String resourceId() {
        return resourceId;
    }
}
