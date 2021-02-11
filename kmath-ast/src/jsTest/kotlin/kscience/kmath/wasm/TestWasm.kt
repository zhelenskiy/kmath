package kscience.kmath.wasm

import kscience.kmath.ast.mstInExtendedField
import kscience.kmath.ast.mstInRing
import kscience.kmath.expressions.invoke
import kscience.kmath.operations.IntRing
import kscience.kmath.operations.RealField
import kotlin.test.Test
import kotlin.test.assertEquals

internal class TestWasm {
    @Test
    fun int() {
        val res = IntRing.mstInRing { number(100000000) + number(10000000) }.compile()()
        assertEquals(110000000, res)
    }

    @Test
    fun real() {
        val res = RealField.mstInExtendedField { number(100000000) + number(2).pow(10) }.compile()()
        assertEquals(100001024.0, res)
    }

    @Test
    fun argsPassing() {
        val res = RealField
            .mstInExtendedField { symbol("y") + symbol("x").pow(10) }
            .compile()("x" to 2.0, "y" to 100000000.0)

        assertEquals(100001024.0, res)
    }

    @Test
    fun powFunction() {
        val expr = RealField.mstInExtendedField { symbol("x").pow(1.0 / 6.0) }.compile()
        assertEquals(0.9730585187140817, expr("x" to 0.8488554755054833))
    }
}
