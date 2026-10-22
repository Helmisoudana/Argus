package com.argus.shared.error;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;

class ErrorCodeAndExceptionsTest {

    @Test
    void errorCode_accepts_module_dot_code_in_uppercase() {
        assertThat(new ErrorCode("API_DISCOVERY.SPEC_NOT_FOUND").value()).isEqualTo("API_DISCOVERY.SPEC_NOT_FOUND");
    }

    @Test
    void errorCode_rejects_bad_formats() {
        assertThatThrownBy(() -> new ErrorCode("minuscule")).isInstanceOf(InvalidInputException.class);
        assertThatThrownBy(() -> new ErrorCode("A..B")).isInstanceOf(InvalidInputException.class);
        assertThatThrownBy(() -> new ErrorCode("")).isInstanceOf(InvalidInputException.class);
    }

    @Test
    void errorCode_of_normalizes_module_and_code() {
        assertThat(ErrorCode.of("repair-loop", "stagnation").value()).isEqualTo("REPAIR_LOOP.STAGNATION");
    }

    @Test
    void generic_codes_are_in_the_shared_namespace() {
        assertThat(ErrorCode.INVALID_INPUT.value()).isEqualTo("SHARED.INVALID_INPUT");
        assertThat(ErrorCode.NOT_FOUND.value()).isEqualTo("SHARED.NOT_FOUND");
        assertThat(ErrorCode.EXTERNAL_SYSTEM.value()).isEqualTo("SHARED.EXTERNAL_SYSTEM");
    }

    @Test
    void notFound_carries_type_id_and_code() {
        NotFoundException e = new NotFoundException("Run", "42");
        assertThat(e.code()).isEqualTo(ErrorCode.NOT_FOUND);
        assertThat(e.resourceType()).isEqualTo("Run");
        assertThat(e.resourceId()).isEqualTo("42");
        assertThat(e).hasMessage("Run introuvable : 42");
    }

    @Test
    void externalSystem_keeps_the_system_and_the_cause() {
        RuntimeException cause = new RuntimeException("boom");
        ExternalSystemException e = new ExternalSystemException("git", "clone impossible", cause);
        assertThat(e.code()).isEqualTo(ErrorCode.EXTERNAL_SYSTEM);
        assertThat(e.system()).isEqualTo("git");
        assertThat(e).hasMessage("[git] clone impossible").hasCause(cause);
    }

    @Test
    void invalidInput_forField_builds_a_uniform_message() {
        InvalidInputException e = InvalidInputException.forField("line", "doit être positif");
        assertThat(e.code()).isEqualTo(ErrorCode.INVALID_INPUT);
        assertThat(e.field()).isEqualTo("line");
        assertThat(e).hasMessage("line : doit être positif");
    }

    @Test
    void all_business_errors_share_the_same_root() {
        assertThat(new NotFoundException("A", "1")).isInstanceOf(argusException.class);
        assertThat(new ExternalSystemException("a", "b")).isInstanceOf(argusException.class);
        assertThat(new InvalidInputException("m")).isInstanceOf(argusException.class);
    }
}
