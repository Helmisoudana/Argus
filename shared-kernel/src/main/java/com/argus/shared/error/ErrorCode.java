package com.argus.shared.error;

import java.io.Serializable;
import java.util.regex.Pattern;

/**
 * Code d'erreur stable et lisible par une machine (logs, API, rapport).
 *
 * <p>Format : segments en MAJUSCULES séparés par des points, ex. {@code API_DISCOVERY.SPEC_NOT_FOUND}.
 * Par convention, le premier segment est le module émetteur. Le kernel ne définit que les
 * codes génériques ; chaque module déclare ses propres constantes dans son propre domaine
 * (ce qui évite que le kernel grossisse à chaque nouvelle erreur).
 */
public record ErrorCode(String value) implements Serializable {

    private static final long serialVersionUID = 1L;

    // Doit être déclaré AVANT les constantes ci-dessous (ordre d'initialisation statique).
    private static final Pattern FORMAT = Pattern.compile("^[A-Z][A-Z0-9_]*(\\.[A-Z][A-Z0-9_]*)*$");

    public static final ErrorCode INVALID_INPUT = new ErrorCode("SHARED.INVALID_INPUT");
    public static final ErrorCode NOT_FOUND = new ErrorCode("SHARED.NOT_FOUND");
    public static final ErrorCode EXTERNAL_SYSTEM = new ErrorCode("SHARED.EXTERNAL_SYSTEM");

    public ErrorCode {
        Guard.matches(value, FORMAT, "ErrorCode", "MODULE.CODE_EN_MAJUSCULES");
    }

    /** Fabrique un code à partir du module émetteur et d'un code local : {@code of("repair_loop", "stagnation")}. */
    public static ErrorCode of(String module, String code) {
        Guard.notBlank(module, "module");
        Guard.notBlank(code, "code");
        return new ErrorCode(module.trim().toUpperCase().replace('-', '_') + "."
                + code.trim().toUpperCase().replace('-', '_'));
    }

    @Override
    public String toString() {
        return value;
    }
}
