plugins { id("scientifik.mpp") }
kotlin.sourceSets.all { languageSettings.useExperimentalAnnotation("kotlin.contracts.ExperimentalContracts") }
tasks.jvmTest.get().jvmArgs("--add-modules", "jdk.incubator.foreign")
