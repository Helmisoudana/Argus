package com.argus.shared.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.argus.shared.error.InvalidInputException;
import org.junit.jupiter.api.Test;

class QualifiedNameTest {

    @Test
    void exposes_simple_name_and_package() {
        QualifiedName name = QualifiedName.of("com.acme.order.OrderService");
        assertThat(name.simpleName()).isEqualTo("OrderService");
        assertThat(name.packageName()).contains("com.acme.order");
        assertThat(name).hasToString("com.acme.order.OrderService");
    }

    @Test
    void default_package_has_no_package_name() {
        QualifiedName name = QualifiedName.of("Foo");
        assertThat(name.simpleName()).isEqualTo("Foo");
        assertThat(name.packageName()).isEmpty();
    }

    @Test
    void accepts_dollar_and_underscore() {
        assertThat(QualifiedName.of("com.acme.Outer$Inner").simpleName()).isEqualTo("Outer$Inner");
        assertThat(QualifiedName.of("com.acme._Internal").simpleName()).isEqualTo("_Internal");
    }

    @Test
    void rejects_malformed_names() {
        for (String bad : new String[] {"", " ", "com..Foo", ".Foo", "com.Foo.", "1abc", "com.acme.Foo Bar", "com-acme.Foo"}) {
            assertThatThrownBy(() -> QualifiedName.of(bad))
                    .as("« %s » doit être refusé", bad)
                    .isInstanceOf(InvalidInputException.class);
        }
    }
}
