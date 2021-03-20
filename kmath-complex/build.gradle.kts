import ru.mipt.npm.gradle.Maturity

plugins {
    id("ru.mipt.npm.gradle.mpp")
    id("ru.mipt.npm.gradle.native")
}

kotlin.sourceSets {
    all {
        languageSettings.useExperimentalAnnotation("kscience.kmath.misc.UnstableKMathAPI")
    }

    commonMain {
        dependencies {
            api(project(":kmath-core"))
        }
    }
}

readme {
    description = "Complex numbers and quaternions."
    maturity = Maturity.PROTOTYPE
    propertyByTemplate("artifact", rootProject.file("docs/templates/ARTIFACT-TEMPLATE.md"))

    feature(
        id = "complex",
        description = "Complex Numbers",
        ref = "src/commonMain/kotlin/space/kscience/kmath/complex/Complex.kt"
    )

    feature(
        id = "quaternion",
        description = "Quaternions",
        ref = "src/commonMain/kotlin/space/kscience/kmath/complex/Quaternion.kt"
    )
}
