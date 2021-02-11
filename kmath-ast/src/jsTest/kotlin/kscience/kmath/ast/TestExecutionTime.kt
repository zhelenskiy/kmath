package kscience.kmath.ast

import kscience.kmath.expressions.Expression
import kscience.kmath.expressions.StringSymbol
import kscience.kmath.expressions.invoke
import kscience.kmath.operations.RealField
import kotlin.math.pow
import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.time.measureTime
import kscience.kmath.estree.compile as estreeCompile
import kscience.kmath.wasm.compile as wasmCompile

internal class TestExecutionTime {
    @Test
    fun testExecutionTime() {
        val times = 500_000
        var sum = 0.0

        val basic = RealField.mstInExtendedField {
            symbol("x").pow(1.0 / 6.0)
        }

        println("MST")
        var rng = Random(0)
        measureTime { repeat(times) { sum += basic("x" to rng.nextDouble()) } }.also(::println)
        val reference = sum

        var e = basic.wasmCompile()
        println("WASM Compiled")
        rng = Random(0)
        sum = 0.0
        measureTime { repeat(times) { sum += e("x" to rng.nextDouble()) } }.also(::println)
        assertEquals(reference, sum)

        e = basic.estreeCompile()
        println("ESTree Compiled")
        rng = Random(0)
        sum = 0.0
        measureTime { repeat(times) { sum += e("x" to rng.nextDouble()) } }.also(::println)
        assertEquals(reference, sum)

        e = Expression { args ->
            args.getValue(StringSymbol("x")).pow(1.0 / 6.0)
        }

        println("JS Math")
        rng = Random(0)
        sum = 0.0
        measureTime { repeat(times) { sum += e("x" to rng.nextDouble()) } }.also(::println)
        assertEquals(reference, sum)
    }
}