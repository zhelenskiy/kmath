/*
 * Copyright 2018-2021 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.benchmarks

import kotlinx.benchmark.Benchmark
import kotlinx.benchmark.Blackhole
import kotlinx.benchmark.Scope
import kotlinx.benchmark.State
import org.jetbrains.bio.viktor.F64Array
import space.kscience.kmath.nd.NDAlgebra
import space.kscience.kmath.nd.NDStructure
import space.kscience.kmath.nd.auto
import space.kscience.kmath.nd.real
import space.kscience.kmath.operations.RealField
import space.kscience.kmath.viktor.ViktorNDField

@State(Scope.Benchmark)
internal class ViktorBenchmark {
    @Benchmark
    fun automaticFieldAddition(blackhole: Blackhole) {
        with(autoField) {
            var res: NDStructure<Double> = one
            repeat(n) { res += 1.0 }
            blackhole.consume(res)
        }
    }

    @Benchmark
    fun realFieldAddition(blackhole: Blackhole) {
        with(realField) {
            var res: NDStructure<Double> = one
            repeat(n) { res += 1.0 }
            blackhole.consume(res)
        }
    }

    @Benchmark
    fun viktorFieldAddition(blackhole: Blackhole) {
        with(viktorField) {
            var res = one
            repeat(n) { res += 1.0 }
            blackhole.consume(res)
        }
    }

    @Benchmark
    fun rawViktor(blackhole: Blackhole) {
        val one = F64Array.full(init = 1.0, shape = intArrayOf(dim, dim))
        var res = one
        repeat(n) { res = res + one }
        blackhole.consume(res)
    }

    private companion object {
        private const val dim = 1000
        private const val n = 100

        // automatically build context most suited for given type.
        private val autoField = NDAlgebra.auto(RealField, dim, dim)
        private val realField = NDAlgebra.real(dim, dim)
        private val viktorField = ViktorNDField(dim, dim)
    }
}
