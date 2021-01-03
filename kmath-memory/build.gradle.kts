plugins {
    id("ru.mipt.npm.mpp")
    id("ru.mipt.npm.native")
}

tasks.jvmTest {
    jvmArgs("--add-modules", "jdk.incubator.foreign")
}
