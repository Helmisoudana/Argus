package com.argus.shared.error;

/** Une valeur fournie ne respecte pas un invariant (nulle, vide, hors bornes, mal formée). */
public class InvalidInputException extends argusException {

    private static final long serialVersionUID = 1L;

    private final String field;

    public InvalidInputException(String message) {
        this(null, message);
    }

    public InvalidInputException(String field, String message) {
        super(ErrorCode.INVALID_INPUT, message);
        this.field = field;
    }

    /** Fabrique un message uniforme : « champ : raison ». */
    public static InvalidInputException forField(String field, String reason) {
        return new InvalidInputException(field, field + " : " + reason);
    }

    /** Nom du champ fautif, ou {@code null} si l'erreur ne concerne pas un champ précis. */
    public String field() {
        return field;
    }
}
