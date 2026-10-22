package com.argus.shared.error;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import java.util.regex.Pattern;
import org.junit.jupiter.api.Test;

class GuardTest {

    @Test
    void notNull_returns_the_value() {
        assertThat(Guard.notNull("x", "name")).isEqualTo("x");
    }

    @Test
    void notNull_rejects_null_and_names_the_field() {
        assertThatThrownBy(() -> Guard.notNull(null, "runId"))
                .isInstanceOf(InvalidInputException.class)
                .hasMessageContaining("runId")
                .extracting(e -> ((InvalidInputException) e).field()).isEqualTo("runId");
    }

    @Test
    void notBlank_rejects_null_empty_and_blank() {
        assertThatThrownBy(() -> Guard.notBlank(null, "f")).isInstanceOf(InvalidInputException.class);
        assertThatThrownBy(() -> Guard.notBlank("", "f")).isInstanceOf(InvalidInputException.class);
        assertThatThrownBy(() -> Guard.notBlank("   ", "f")).isInstanceOf(InvalidInputException.class);
        assertThat(Guard.notBlank("a", "f")).isEqualTo("a");
    }

    @Test
    void notEmpty_rejects_empty_collections() {
        assertThatThrownBy(() -> Guard.notEmpty(List.of(), "items")).isInstanceOf(InvalidInputException.class);
        assertThat(Guard.notEmpty(List.of(1), "items")).containsExactly(1);
    }

    @Test
    void inRange_accepts_bounds_and_rejects_outside_nan_and_infinity() {
        assertThat(Guard.inRange(0, 0, 1, "x")).isZero();
        assertThat(Guard.inRange(1, 0, 1, "x")).isEqualTo(1);
        assertThatThrownBy(() -> Guard.inRange(-0.01, 0, 1, "x")).isInstanceOf(InvalidInputException.class);
        assertThatThrownBy(() -> Guard.inRange(1.01, 0, 1, "x")).isInstanceOf(InvalidInputException.class);
        assertThatThrownBy(() -> Guard.inRange(Double.NaN, 0, 1, "x")).isInstanceOf(InvalidInputException.class);
        assertThatThrownBy(() -> Guard.inRange(Double.POSITIVE_INFINITY, 0, 1, "x"))
                .isInstanceOf(InvalidInputException.class);
    }

    @Test
    void positive_and_nonNegative() {
        assertThat(Guard.positive(1, "n")).isEqualTo(1);
        assertThatThrownBy(() -> Guard.positive(0, "n")).isInstanceOf(InvalidInputException.class);
        assertThat(Guard.nonNegative(0, "n")).isZero();
        assertThatThrownBy(() -> Guard.nonNegative(-1, "n")).isInstanceOf(InvalidInputException.class);
    }

    @Test
    void matches_checks_the_whole_value() {
        Pattern digits = Pattern.compile("\\d+");
        assertThat(Guard.matches("123", digits, "n", "chiffres")).isEqualTo("123");
        assertThatThrownBy(() -> Guard.matches("12a", digits, "n", "chiffres"))
                .isInstanceOf(InvalidInputException.class)
                .hasMessageContaining("chiffres");
    }

    @Test
    void that_throws_with_the_given_message() {
        Guard.that(true, "jamais lu");
        assertThatThrownBy(() -> Guard.that(false, "condition violée"))
                .isInstanceOf(InvalidInputException.class)
                .hasMessage("condition violée");
    }
}
