package com.argus.shared.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.argus.shared.error.InvalidInputException;
import org.junit.jupiter.api.Test;

class HttpMethodTest {

    @Test
    void parse_ignores_case_and_surrounding_spaces() {
        assertThat(HttpMethod.parse(" post ")).isEqualTo(HttpMethod.POST);
        assertThat(HttpMethod.parse("delete")).isEqualTo(HttpMethod.DELETE);
    }

    @Test
    void parse_rejects_unknown_or_blank() {
        assertThatThrownBy(() -> HttpMethod.parse("FETCH")).isInstanceOf(InvalidInputException.class);
        assertThatThrownBy(() -> HttpMethod.parse(" ")).isInstanceOf(InvalidInputException.class);
        assertThatThrownBy(() -> HttpMethod.parse(null)).isInstanceOf(InvalidInputException.class);
    }
}
