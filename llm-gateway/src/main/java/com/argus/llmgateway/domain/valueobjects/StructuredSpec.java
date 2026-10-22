package com.argus.llmgateway.domain.valueobjects;
import com.argus.llmgateway.domain.exceptions.InvalidLlmRequestError;

public record StructuredSpec(Format format, String language) {

    public enum Format { JSON, CODE_BLOCK }

    public StructuredSpec {
        if (format == null) throw new InvalidLlmRequestError("format", "obligatoire");
        if (format == Format.CODE_BLOCK && (language == null || language.isBlank()))
            throw new InvalidLlmRequestError("language", "obligatoire pour un bloc de code");
    }

    public static StructuredSpec json() {
        return new StructuredSpec(Format.JSON, null);
    }

    public static StructuredSpec code(String language) {
        return new StructuredSpec(Format.CODE_BLOCK, language);
    }
}