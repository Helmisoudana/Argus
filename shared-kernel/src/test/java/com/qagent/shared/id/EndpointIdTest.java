package com.argus.shared.id;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.argus.shared.error.InvalidInputException;
import com.argus.shared.model.HttpMethod;
import org.junit.jupiter.api.Test;

class EndpointIdTest {

    @Test
    void parse_reads_method_and_path() {
        EndpointId id = EndpointId.parse("GET /orders/{id}");
        assertThat(id.method()).isEqualTo(HttpMethod.GET);
        assertThat(id.path()).isEqualTo("/orders/{id}");
        assertThat(id.value()).isEqualTo("GET /orders/{id}");
        assertThat(id).hasToString("GET /orders/{id}");
    }

    @Test
    void path_is_normalized() {
        assertThat(EndpointId.parse("get orders/{id}/").value()).isEqualTo("GET /orders/{id}");
        assertThat(new EndpointId(HttpMethod.GET, "/orders//".trim()).path()).isEqualTo("/orders");
        assertThat(new EndpointId(HttpMethod.GET, "/").path()).isEqualTo("/");
    }

    @Test
    void normalized_forms_are_equal() {
        assertThat(new EndpointId(HttpMethod.POST, "/a/")).isEqualTo(new EndpointId(HttpMethod.POST, "/a"));
    }

    @Test
    void path_case_is_preserved() {
        assertThat(new EndpointId(HttpMethod.GET, "/Orders").path()).isEqualTo("/Orders");
    }

    @Test
    void rejects_query_string_fragment_and_inner_spaces() {
        assertThatThrownBy(() -> new EndpointId(HttpMethod.GET, "/a?b=1")).isInstanceOf(InvalidInputException.class);
        assertThatThrownBy(() -> new EndpointId(HttpMethod.GET, "/a#x")).isInstanceOf(InvalidInputException.class);
        assertThatThrownBy(() -> new EndpointId(HttpMethod.GET, "/a b")).isInstanceOf(InvalidInputException.class);
    }

    @Test
    void rejects_missing_parts() {
        assertThatThrownBy(() -> EndpointId.parse("GET")).isInstanceOf(InvalidInputException.class);
        assertThatThrownBy(() -> new EndpointId(null, "/a")).isInstanceOf(InvalidInputException.class);
        assertThatThrownBy(() -> new EndpointId(HttpMethod.GET, " ")).isInstanceOf(InvalidInputException.class);
    }
}
