package com.argus.shared.architecture;

import static org.assertj.core.api.Assertions.assertThat;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;
import java.util.stream.StreamSupport;
import org.junit.jupiter.api.Test;

/** SK-6 : le kernel reste petit. Au-delà de 40 types, c'est un signal d'alerte de conception. */
class KernelSizeTest {

    private static final int MAX_TYPES = 40;

    @Test
    void kernel_stays_under_the_size_cap() {
        JavaClasses classes = new ClassFileImporter()
                .withImportOption(new ImportOption.DoNotIncludeTests())
                .importPackages("com.argus.shared");

        long types = StreamSupport.stream(classes.spliterator(), false)
                .filter(c -> c.getEnclosingClass().isEmpty())
                .filter(c -> !"package-info".equals(c.getSimpleName()))
                .count();

        assertThat(types)
                .as("SK-6 : le shared-kernel compte %d types (maximum %d)", types, MAX_TYPES)
                .isLessThanOrEqualTo(MAX_TYPES);
    }
}
