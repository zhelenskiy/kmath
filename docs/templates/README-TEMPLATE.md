[![JetBrains Research](https://jb.gg/badges/research.svg)](https://confluence.jetbrains.com/display/ALL/JetBrains+on+GitHub)
[![DOI](https://zenodo.org/badge/129486382.svg)](https://zenodo.org/badge/latestdoi/129486382)

![Gradle build](https://github.com/mipt-npm/kmath/workflows/Gradle%20build/badge.svg)

[![Maven Central](https://img.shields.io/maven-central/v/space.kscience/kmath-core.svg?label=Maven%20Central)](https://search.maven.org/search?q=g:%22space.kscience%22%20AND%20a:%22kmath-core%22)

# KMath

Could be pronounced as `key-math`. The **K**otlin **Math**ematics library was initially intended as a Kotlin-based analog to
Python's NumPy library. Later we found that kotlin is much more flexible language and allows superior architecture 
designs. In contrast to `numpy` and `scipy` it is modular and has a lightweight core. The `numpy`-like experience could 
be achieved with [kmath-for-real](/kmath-for-real) extension module.

## Publications and talks

* [A conceptual article about context-oriented design](https://proandroiddev.com/an-introduction-context-oriented-programming-in-kotlin-2e79d316b0a2)
* [Another article about context-oriented design](https://proandroiddev.com/diving-deeper-into-context-oriented-programming-in-kotlin-3ecb4ec38814)
* [ACAT 2019 conference paper](https://aip.scitation.org/doi/abs/10.1063/1.5130103)

# Goal

* Provide a flexible and powerful API to work with mathematics abstractions in Kotlin-multiplatform (JVM, JS and Native). 
* Provide basic multiplatform implementations for those abstractions (without significant performance optimization).
* Provide bindings and wrappers with those abstractions for popular optimized platform libraries.

## Non-goals

* Be like NumPy. It was the idea at the beginning, but we decided that we can do better in terms of API.
* Provide the best performance out of the box. We have specialized libraries for that. Need only API wrappers for them.
* Cover all cases as immediately and in one bundle. We will modularize everything and add new features gradually.
* Provide specialized behavior in the core. API is made generic on purpose, so one needs to specialize for types, like 
for `Double` in the core. For that we will have specialization modules like `kmath-for-real`, which will give better
experience for those, who want to work with specific types.

## Features and stability

KMath is a modular library. Different modules provide different features with different API stability guarantees. All core modules are released with the same version, but with different API change policy. The features are described in module definitions below. The module stability could have following levels:

* **PROTOTYPE**. On this level there are no compatibility guarantees. All methods and classes form those modules could break any moment. You can still use it, but be sure to fix the specific version.
* **EXPERIMENTAL**. The general API is decided, but some changes could be made. Volatile API is marked with `@UnstableKmathAPI` or other stability warning annotations.
* **DEVELOPMENT**. API breaking genrally follows semantic versioning ideology. There could be changes in minor versions, but not in patch versions. API is protected with [binary-compatibility-validator](https://github.com/Kotlin/binary-compatibility-validator) tool.
* **STABLE**. The API stabilized. Breaking changes are allowed only in major releases.

<!--Current feature list is [here](/docs/features.md)-->


<!--* **Array-like structures** Full support of many-dimensional array-like structures -->
<!--including mixed arithmetic operations and function operations over arrays and numbers (with the added benefit of static type checking).-->

<!--* **Histograms** Fast multi-dimensional histograms.-->

<!--* **Streaming** Streaming operations on mathematical objects and objects buffers.-->

<!--* **Type-safe dimensions** Type-safe dimensions for matrix operations.-->

<!--* **Commons-math wrapper** It is planned to gradually wrap most parts of -->
<!--[Apache commons-math](http://commons.apache.org/proper/commons-math/) library in Kotlin code and maybe rewrite some -->
<!--parts to better suit the Kotlin programming paradigm, however there is no established roadmap for that. Feel free to -->
<!--submit a feature request if you want something to be implemented first.-->
<!--                           -->
<!--## Planned features-->

<!--* **Messaging** A mathematical notation to support multi-language and multi-node communication for mathematical tasks.-->

<!--* **Array statistics** -->

<!--* **Integration** Univariate and multivariate integration framework.-->

<!--* **Probability and distributions**-->

<!--* **Fitting** Non-linear curve fitting facilities-->

## Modules

$modules

## Multi-platform support

KMath is developed as a multi-platform library, which means that most of the interfaces are declared in the 
[common source sets](/kmath-core/src/commonMain) and implemented there wherever it is possible. In some cases, features 
are delegated to platform-specific implementations even if they could be provided in the common module for performance 
reasons. Currently, the Kotlin/JVM is the primary platform, however Kotlin/Native and Kotlin/JS contributions and 
feedback are also welcome.

## Performance

Calculation performance is one of major goals of KMath in the future, but in some cases it is impossible to achieve 
both performance and flexibility. 

We expect to focus on creating convenient universal API first and then work on increasing performance for specific 
cases. We expect the worst KMath benchmarks will perform better than native Python, but worse than optimized 
native/SciPy (mostly due to boxing operations on primitive numbers). The best performance of optimized parts could be 
better than SciPy.

### Repositories

Release and development artifacts are accessible from mipt-npm [Space](https://www.jetbrains.com/space/) repository `https://maven.pkg.jetbrains.space/mipt-npm/p/sci/maven` (see documentation of
[Kotlin Multiplatform](https://kotlinlang.org/docs/reference/multiplatform.html) for more details). The repository could be reached through [repo.kotlin.link](https://repo.kotlin.link) proxy:

```kotlin
repositories {
    maven("https://repo.kotlin.link")
}

dependencies {
    api("${group}:kmath-core:$version")
    // api("kscience.kmath:kmath-core-jvm:$version") for jvm-specific version
}
```

Gradle `6.0+` is required for multiplatform artifacts.

## Contributing

The project requires a lot of additional work. The most important thing we need is a feedback about what features are 
required the most. Feel free to create feature requests. We are also welcome to code contributions, 
especially in issues marked with 
[waiting for a hero](https://github.com/mipt-npm/kmath/labels/waiting%20for%20a%20hero) label.
