package com.argus.llmgateway.domain.services;

import com.argus.llmgateway.domain.exceptions.InvalidLlmResponseError;
import com.argus.llmgateway.domain.valueobjects.StructuredSpec;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** Extrait un JSON ou un bloc de code d'une réponse brute du LLM. */
public final class StructuredOutputParser {

    private static final Pattern ANY_FENCE =
            Pattern.compile("```[\\w+-]*[ \\t]*\\R(.*?)```", Pattern.DOTALL);

    public String parse(String text, StructuredSpec spec) {
        Objects.requireNonNull(spec, "spec");
        if (text == null || text.isBlank()) {
            throw new InvalidLlmResponseError("réponse vide");
        }
        return switch (spec.format()) {
            case JSON -> extractJson(text);
            case CODE_BLOCK -> extractCode(text, spec.language());
        };
    }

    private String extractJson(String text) {
        int brace = text.indexOf('{');
        int bracket = text.indexOf('[');
        int start = brace < 0 ? bracket : (bracket < 0 ? brace : Math.min(brace, bracket));
        int end = Math.max(text.lastIndexOf('}'), text.lastIndexOf(']'));
        if (start < 0 || end <= start) {
            throw new InvalidLlmResponseError("JSON introuvable");
        }
        return text.substring(start, end + 1).strip();
    }

    private String extractCode(String text, String language) {
        Pattern typed = Pattern.compile(
                "```" + Pattern.quote(language) + "[ \\t]*\\R(.*?)```",
                Pattern.DOTALL | Pattern.CASE_INSENSITIVE);
        Matcher m = typed.matcher(text);
        if (m.find()) {
            return m.group(1).strip();
        }
        Matcher any = ANY_FENCE.matcher(text);
        if (any.find()) {
            return any.group(1).strip();
        }
        throw new InvalidLlmResponseError("bloc de code introuvable");
    }
}