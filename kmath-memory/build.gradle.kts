plugins {
    id("ru.mipt.npm.gradle.mpp")
    id("ru.mipt.npm.gradle.native")
}

readme {
    description = """
        An API and basic implementation for arranging objects in a continous memory block.
    """.trimIndent()
    maturity = ru.mipt.npm.gradle.Maturity.DEVELOPMENT
}

tasks.jvmTest {
    jvmArgs("--add-modules", "jdk.incubator.foreign")
}
