/* (C) 2025 */
package pl.dealsniper.core.architecture;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static pl.dealsniper.core.mock.ArchUnitNaming.*;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

@AnalyzeClasses(
        packages = "pl.dealsniper.core",
        importOptions = {ImportOption.DoNotIncludeTests.class})
public class NamingConventionTest {

    @ArchTest
    static final ArchRule CONTROLLER_NOT_SUFFIXED =
            classes().that().resideInAPackage(CONTROLLER_LAYER).should().haveSimpleNameEndingWith("Controller");

    @ArchTest
    static final ArchRule SERVICES_NOT_SUFFIXED = classes()
            .that()
            .resideInAPackage(SERVICE_LAYER)
            .should()
            .haveSimpleNameEndingWith("Service")
            .orShould()
            .haveSimpleNameEndingWith("Orchestrator");

    @ArchTest
    static final ArchRule REPOSITORY_NOT_SUFFIXED = classes()
            .that()
            .resideInAPackage(REPOSITORY_LAYER)
            .or()
            .resideInAPackage(REPOSITORY_IMPL_LAYER)
            .should()
            .haveSimpleNameEndingWith("Repository")
            .orShould()
            .haveSimpleNameEndingWith("RepositoryImpl");

    @ArchTest
    static final ArchRule REPOSITORY_IMPL_NOT_SUFFIXED = classes()
            .that()
            .resideInAPackage(REPOSITORY_IMPL_LAYER)
            .should()
            .haveSimpleNameEndingWith("RepositoryImpl");

    @ArchTest
    static final ArchRule MAPPERS_NOT_SUFFIXED = classes()
            .that()
            .resideInAPackage(MAPPER_LAYER)
            .and()
            .areTopLevelClasses()
            .should()
            .haveSimpleNameEndingWith("Mapper")
            .orShould()
            .haveSimpleNameEndingWith("MapperImpl");

    @ArchTest
    static final ArchRule DTO_REQUEST_NOT_SUFFIXED = classes()
            .that()
            .resideInAPackage(DTO_REQUEST_LAYER)
            .and()
            .areTopLevelClasses()
            .and()
            .areNotEnums()
            .and()
            .areNotInterfaces()
            .should()
            .haveSimpleNameEndingWith("Request")
            .andShould()
            .beRecords();

    @ArchTest
    static final ArchRule DTO_RESPONSE_NOT_SUFFIXED = classes()
            .that()
            .resideInAPackage(DTO_RESPONSE_LAYER)
            .and()
            .areTopLevelClasses()
            .should()
            .haveSimpleNameEndingWith("Response")
            .andShould()
            .beRecords();

    @ArchTest
    static final ArchRule EXCEPTIONS_NOT_SUFFIXED = classes()
            .that()
            .resideInAPackage("..exception..")
            .and()
            .areNotEnums()
            .should()
            .haveSimpleNameEndingWith("Exception")
            .orShould()
            .haveSimpleName("GlobalExceptionHandler");

    @ArchTest
    static final ArchRule UTIL_NOT_SUFFIXED = classes()
            .that()
            .resideInAPackage(UTIL_LAYER)
            .and()
            .areTopLevelClasses()
            .should()
            .haveSimpleNameEndingWith("Util");
}
