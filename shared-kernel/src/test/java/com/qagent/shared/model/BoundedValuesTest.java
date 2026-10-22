package com.argus.shared.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.argus.shared.error.InvalidInputException;
import org.junit.jupiter.api.Test;

class BoundedValuesTest {

    @Test
    void percentage_accepts_0_to_100() {
        assertThat(Percentage.of(0)).isEqualTo(Percentage.ZERO);
        assertThat(Percentage.of(100)).isEqualTo(Percentage.FULL);
        assertThat(Percentage.of(80.5).value()).isEqualTo(80.5);
    }

    @Test
    void percentage_rejects_out_of_range_and_nan() {
        assertThatThrownBy(() -> Percentage.of(-0.1)).isInstanceOf(InvalidInputException.class);
        assertThatThrownBy(() -> Percentage.of(100.1)).isInstanceOf(InvalidInputException.class);
        assertThatThrownBy(() -> Percentage.of(Double.NaN)).isInstanceOf(InvalidInputException.class);
    }

    @Test
    void confidence_accepts_0_to_1() {
        assertThat(Confidence.of(0)).isEqualTo(Confidence.NONE);
        assertThat(Confidence.of(1)).isEqualTo(Confidence.CERTAIN);
        assertThat(Confidence.of(0.7).value()).isEqualTo(0.7);
    }

    @Test
    void confidence_rejects_out_of_range() {
        assertThatThrownBy(() -> Confidence.of(-0.01)).isInstanceOf(InvalidInputException.class);
        assertThatThrownBy(() -> Confidence.of(1.01)).isInstanceOf(InvalidInputException.class);
    }

    @Test
    void sourceLocation_is_one_based_and_prints_file_line_column() {
        assertThat(new SourceLocation("OrderService.java", 12, 5)).hasToString("OrderService.java:12:5");
        assertThatThrownBy(() -> new SourceLocation("A.java", 0, 1)).isInstanceOf(InvalidInputException.class);
        assertThatThrownBy(() -> new SourceLocation("A.java", 1, 0)).isInstanceOf(InvalidInputException.class);
        assertThatThrownBy(() -> new SourceLocation(" ", 1, 1)).isInstanceOf(InvalidInputException.class);
    }

    @Test
    void enums_expose_the_expected_vocabulary() {
        assertThat(TestLevel.values()).containsExactly(
                TestLevel.UNIT, TestLevel.INTEGRATION, TestLevel.API, TestLevel.CONTRACT);
        assertThat(FailureCategory.values()).containsExactly(
                FailureCategory.TEST_ERROR, FailureCategory.ENVIRONMENT_OR_FLAKY, FailureCategory.SUSPECTED_APP_BUG);
        assertThat(TestStatus.values()).hasSize(7);
    }
}
