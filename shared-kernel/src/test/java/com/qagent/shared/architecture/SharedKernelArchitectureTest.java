package com.argus.shared.architecture;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

import com.tngtech.archunit.core.domain.JavaClass;
import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.lang.ArchCondition;
import com.tngtech.archunit.lang.ArchRule;
import com.tngtech.archunit.lang.ConditionEvents;
import com.tngtech.archunit.lang.SimpleConditionEvent;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;

/**
 * Applique automatiquement les règles SK-1 à SK-5 et SK-8 du README.
 * Le build échoue si l'une d'elles est violée.
 */
@AnalyzeClasses(packages = "com.argus.shared", importOptions = ImportOption.DoNotIncludeTests.class)
class SharedKernelArchitectureTest {

    /** SK-1 : JDK uniquement. */
    @ArchTest
    static final ArchRule sk1_only_jdk_and_kernel_packages =
            classes().should().onlyDependOnClassesThat()
                    .resideInAnyPackage("com.argus.shared..", "java..");

    /** SK-1 / SK-8 : aucun framework, aucune annotation de sérialisation ou de persistance. */
    @ArchTest
    static final ArchRule sk8_no_framework =
            noClasses().should().dependOnClassesThat().resideInAnyPackage(
                    "org.springframework..", "jakarta..", "javax..", "com.fasterxml..", "lombok..", "org.slf4j..");

    /** Couches internes : error est la plus basse, event est la plus haute. */
    @ArchTest
    static final ArchRule error_is_the_lowest_layer =
            noClasses().that().resideInAPackage("com.argus.shared.error..")
                    .should().dependOnClassesThat().resideInAnyPackage(
                            "com.argus.shared.id..", "com.argus.shared.model..", "com.argus.shared.event..");

    @ArchTest
    static final ArchRule model_does_not_depend_on_id_or_event =
            noClasses().that().resideInAPackage("com.argus.shared.model..")
                    .should().dependOnClassesThat().resideInAnyPackage(
                            "com.argus.shared.id..", "com.argus.shared.event..");

    @ArchTest
    static final ArchRule id_does_not_depend_on_event =
            noClasses().that().resideInAPackage("com.argus.shared.id..")
                    .should().dependOnClassesThat().resideInAPackage("com.argus.shared.event..");

    /** SK-2 : les types de valeur (id, model) sont des records ou des enums. */
    @ArchTest
    static final ArchRule sk2_value_types_are_records_or_enums =
            classes().that().resideInAnyPackage("com.argus.shared.id..", "com.argus.shared.model..")
                    .and().areNotInterfaces()
                    .should(beRecordOrEnum());

    private static ArchCondition<JavaClass> beRecordOrEnum() {
        return new ArchCondition<>("be a record or an enum") {
            @Override
            public void check(JavaClass javaClass, ConditionEvents events) {
                boolean ok = "package-info".equals(javaClass.getSimpleName())
                        || javaClass.isEnum()
                        || javaClass.isAssignableTo(Record.class);
                events.add(new SimpleConditionEvent(javaClass, ok,
                        javaClass.getName() + (ok ? " is" : " is not") + " a record or an enum"));
            }
        };
    }
}
