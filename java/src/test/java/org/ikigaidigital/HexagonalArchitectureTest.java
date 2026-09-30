package org.ikigaidigital;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;
import org.junit.jupiter.api.Test;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;
import static com.tngtech.archunit.library.dependencies.SlicesRuleDefinition.slices;

/** The layering the README asks for, enforced by the build instead of by convention. */
class HexagonalArchitectureTest {

    private static final String[] CORE = {"org.ikigaidigital", "org.ikigaidigital.domain..", "org.ikigaidigital.application.."};

    private final JavaClasses productionClasses = new ClassFileImporter()
            .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS)
            .importPackages("org.ikigaidigital");

    @Test
    void should_keepCoreFreeOfFrameworks_whenAnyClassIsAdded() {
        // The Spring entry point shares the root package with the given classes so that component scanning covers everything
        noClasses().that().resideInAnyPackage(CORE)
                .and().doNotHaveFullyQualifiedName(TimeDepositApplication.class.getName())
                .should().dependOnClassesThat().resideInAnyPackage(
                        "org.springframework..", "jakarta..", "com.fasterxml..", "org.hibernate..")
                .check(productionClasses);
    }

    @Test
    void should_keepCoreIndependentOfAdaptersAndConfig_whenAnyClassIsAdded() {
        noClasses().that().resideInAnyPackage(CORE)
                .should().dependOnClassesThat().resideInAnyPackage(
                        "org.ikigaidigital.adapter..", "org.ikigaidigital.config..")
                .check(productionClasses);
    }

    @Test
    void should_keepPortsAsInterfaces_whenAnyPortIsAdded() {
        classes().that().resideInAPackage("org.ikigaidigital.application.port..")
                .should().beInterfaces()
                .check(productionClasses);
    }

    @Test
    void should_keepInboundAdapterOnUseCasePorts_whenAnyControllerIsAdded() {
        noClasses().that().resideInAPackage("org.ikigaidigital.adapter.in..")
                .should().dependOnClassesThat().resideInAPackage("org.ikigaidigital.application.service..")
                .check(productionClasses);
    }

    @Test
    void should_keepTopLevelPackagesFreeOfCycles_whenAnyDependencyIsAdded() {
        slices().matching("org.ikigaidigital.(*)..")
                .should().beFreeOfCycles()
                .check(productionClasses);
    }

    @Test
    void should_keepInboundAdapterIndependentOfOutboundAdapter_whenAnyClassIsAdded() {
        noClasses().that().resideInAPackage("org.ikigaidigital.adapter.in..")
                .should().dependOnClassesThat().resideInAPackage("org.ikigaidigital.adapter.out..")
                .check(productionClasses);
    }
}
