/*
 * Copyright 2018-2021 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.benchmarks

import kotlinx.benchmark.Benchmark
import kotlinx.benchmark.Blackhole
import kotlinx.benchmark.Scope
import kotlinx.benchmark.State
import space.kscience.kmath.asm.compile
import space.kscience.kmath.ast.mstInField
import space.kscience.kmath.expressions.Expression
import space.kscience.kmath.expressions.expressionInField
import space.kscience.kmath.expressions.invoke
import space.kscience.kmath.expressions.symbol
import space.kscience.kmath.operations.RealField
import space.kscience.kmath.operations.bindSymbol
import kotlin.random.Random

@State(Scope.Benchmark)
internal class ExpressionsInterpretersBenchmark {
    @Benchmark
    fun functionalExpression(blackhole: Blackhole) {
        val expr = algebra.expressionInField {
            val x = bindSymbol(x)
            x * const(2.0) + const(2.0) / x - const(16.0)
        }

        invokeAndSum(expr, blackhole)
    }

    @Benchmark
    fun mstExpression(blackhole: Blackhole) {
        val expr = algebra.mstInField {
            val x = bindSymbol(x)
            x * 2.0 + number(2.0) / x - 16.0
        }

        invokeAndSum(expr, blackhole)
    }

    @Benchmark
    fun asmExpression(blackhole: Blackhole) {
        val expr = algebra.mstInField {
            val x = bindSymbol(x)
            x * 2.0 + number(2.0) / x - 16.0
        }.compile()

        invokeAndSum(expr, blackhole)
    }

    @Benchmark
    fun rawExpression(blackhole: Blackhole) {
        val expr = Expression<Double> { args ->
            val x = args.getValue(x)
            x * 2.0 + 2.0 / x - 16.0
        }

        invokeAndSum(expr, blackhole)
    }

    private fun invokeAndSum(expr: Expression<Double>, blackhole: Blackhole) {
        val random = Random(0)
        var sum = 0.0

        repeat(1000000) {
            sum += expr(x to random.nextDouble())
        }

        blackhole.consume(sum)
    }

    private companion object {
        private val algebra = RealField
        private val x by symbol
    }
}
