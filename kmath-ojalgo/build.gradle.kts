plugins {
    id("ru.mipt.npm.jvm")
}

dependencies {
    api("org.ojalgo:ojalgo:48.3.0")
    api(project(":kmath-core"))
}
