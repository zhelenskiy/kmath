/*
 * Copyright 2018-2021 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.ast

import space.kscience.kmath.asm.compile
import space.kscience.kmath.expressions.derivative
import space.kscience.kmath.expressions.invoke
import space.kscience.kmath.expressions.symbol
import space.kscience.kmath.kotlingrad.differentiable
import space.kscience.kmath.operations.RealField

/**
 * In this example, x^2-4*x-44 function is differentiated with Kotlin∇, and the autodiff result is compared with
 * valid derivative.
 */
fun main() {
    val x by symbol

    val actualDerivative = MstExpression(RealField, "x^2-4*x-44".parseMath())
        .differentiable()
        .derivative(x)
        .compile()

    val expectedDerivative = MstExpression(RealField, "2*x-4".parseMath()).compile()
    assert(actualDerivative("x" to 123.0) == expectedDerivative("x" to 123.0))
}
