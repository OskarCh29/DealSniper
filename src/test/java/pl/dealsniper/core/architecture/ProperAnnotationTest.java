/* (C) 2025 */
package pl.dealsniper.core.architecture;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.fields;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.methods;
import static pl.dealsniper.core.util.ArchUnitNaming.CONTROLLER_LAYER;
import static pl.dealsniper.core.util.ArchUnitNaming.REPOSITORY_IMPL_LAYER;
import static pl.dealsniper.core.util.ArchUnitNaming.SERVICE_LAYER;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@AnalyzeClasses(packages = "pl.dealsniper.core", importOptions = ImportOption.DoNotIncludeTests.class)
public class ProperAnnotationTest {

    @ArchTest
    static final ArchRule PROPER_DEPENDENCY_INJECTION = fields().should().notBeAnnotatedWith(Autowired.class);

    @ArchTest
    static final ArchRule REPOSITORY_INTERFACE_NOT_ANNOTATED =
            classes().that().resideInAPackage("..repository").should().notBeAnnotatedWith(Repository.class);

    @ArchTest
    static final ArchRule PROPER_ANNOTATION_WITHOUT_COMPONENT = classes()
            .that()
            .resideInAnyPackage(CONTROLLER_LAYER, SERVICE_LAYER, REPOSITORY_IMPL_LAYER)
            .should()
            .notBeAnnotatedWith(Component.class)
            .because("These layers should use proper annotation @Controller, @Service, @Repository");

    @ArchTest
    static final ArchRule PROPER_CONTROLLER_ANNOTATION = methods()
            .that()
            .areDeclaredInClassesThat()
            .areAnnotatedWith(RestController.class)
            .should()
            .beAnnotatedWith(RequestMapping.class)
            .orShould()
            .beAnnotatedWith(GetMapping.class)
            .orShould()
            .beAnnotatedWith(PostMapping.class)
            .orShould()
            .beAnnotatedWith(PutMapping.class)
            .orShould()
            .beAnnotatedWith(DeleteMapping.class)
            .orShould()
            .beAnnotatedWith(PatchMapping.class)
            .allowEmptyShould(true);
}
