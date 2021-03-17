import ru.mipt.npm.gradle.Maturity

plugins {
    id("ru.mipt.npm.gradle.jvm")
}

dependencies {
    implementation(project(":kmath-core"))
}

readme {
    maturity = Maturity.PROTOTYPE
}
