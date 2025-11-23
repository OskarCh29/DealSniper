/* (C) 2025 */
package pl.dealsniper.core.architecture;

import static pl.dealsniper.core.mock.ArchUnitNaming.CONTROLLER_LAYER;
import static pl.dealsniper.core.mock.ArchUnitNaming.DTO_LAYER;
import static pl.dealsniper.core.mock.ArchUnitNaming.MAPPER_LAYER;
import static pl.dealsniper.core.mock.ArchUnitNaming.MODEL_LAYER;
import static pl.dealsniper.core.mock.ArchUnitNaming.REPOSITORY_IMPL_LAYER;
import static pl.dealsniper.core.mock.ArchUnitNaming.REPOSITORY_LAYER;
import static pl.dealsniper.core.mock.ArchUnitNaming.SCHEDULER_LAYER;
import static pl.dealsniper.core.mock.ArchUnitNaming.SCRAPER_LAYER;
import static pl.dealsniper.core.mock.ArchUnitNaming.SERVICE_LAYER;

import com.tngtech.archunit.base.DescribedPredicate;
import com.tngtech.archunit.core.domain.JavaClass;
import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;
import com.tngtech.archunit.library.Architectures;

@AnalyzeClasses(packages = "pl.dealsniper", importOptions = ImportOption.DoNotIncludeTests.class)
public class LayerArchitectureTest {

    @ArchTest
    static final ArchRule LAYER_ARCHITECTURE_RULE = Architectures.layeredArchitecture()
            .consideringOnlyDependenciesInLayers()
            .layer("Controller")
            .definedBy(CONTROLLER_LAYER)
            .layer("Mapper")
            .definedBy(MAPPER_LAYER)
            .layer("DTO")
            .definedBy(DTO_LAYER)
            .layer("Model")
            .definedBy(MODEL_LAYER)
            .layer("Repository")
            .definedBy(REPOSITORY_LAYER)
            .layer("RepositoryImpl")
            .definedBy(REPOSITORY_IMPL_LAYER)
            .layer("Service")
            .definedBy(SERVICE_LAYER)
            .layer("Scraper")
            .definedBy(SCRAPER_LAYER)
            .layer("Scheduler")
            .definedBy(SCHEDULER_LAYER)
            .whereLayer("Controller")
            .mayNotBeAccessedByAnyLayer()
            .whereLayer("Controller")
            .mayOnlyAccessLayers("Service", "Mapper", "DTO")
            .whereLayer("Service")
            .mayOnlyBeAccessedByLayers("Controller")
            .whereLayer("Service")
            .mayOnlyAccessLayers("Model", "Repository", "Mapper", "Scraper", "Scheduler", "DTO")
            .whereLayer("Mapper")
            .mayOnlyBeAccessedByLayers("Model", "DTO", "RepositoryImpl", "Service", "Controller")
            .whereLayer("Model")
            .mayOnlyBeAccessedByLayers("Service", "RepositoryImpl", "Mapper", "Repository")
            .whereLayer("Repository")
            .mayOnlyBeAccessedByLayers("Service")
            .whereLayer("RepositoryImpl")
            .mayOnlyAccessLayers("Model", "Mapper", "DTO", "Repository")
            .whereLayer("Scraper")
            .mayOnlyBeAccessedByLayers("Service", "Model")
            .whereLayer("Scraper")
            .mayOnlyAccessLayers("Model")
            .whereLayer("Scheduler")
            .mayOnlyAccessLayers("Service", "Model", "Scraper")
            .ignoreDependency(DescribedPredicate.alwaysTrue(), JavaClass.Predicates.simpleNameContaining("Deal"))
            .because("ArchUnit made generic types restrict so with above it is avoided");
}
