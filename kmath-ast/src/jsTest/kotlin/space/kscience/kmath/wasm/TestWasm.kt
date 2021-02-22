package space.kscience.kmath.wasm

import space.kscience.kmath.ast.mstInExtendedField
import space.kscience.kmath.ast.mstInRing
import space.kscience.kmath.expressions.invoke
import space.kscience.kmath.operations.IntRing
import space.kscience.kmath.operations.RealField
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
            .mstInExtendedField { bindSymbol("y") + bindSymbol("x").pow(10) }
            .compile()("x" to 2.0, "y" to 100000000.0)

        assertEquals(100001024.0, res)
    }

    @Test
    fun powFunction() {
        val expr = RealField.mstInExtendedField { bindSymbol("x").pow(1.0 / 6.0) }.compile()
        assertEquals(0.9730585187140817, expr("x" to 0.8488554755054833))
    }
}
