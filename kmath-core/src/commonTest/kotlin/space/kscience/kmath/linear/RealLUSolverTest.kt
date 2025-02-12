/*
 * Copyright 2018-2021 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.linear

import space.kscience.kmath.misc.UnstableKMathAPI
import kotlin.test.Test
import kotlin.test.assertEquals

@UnstableKMathAPI
class RealLUSolverTest {

    @Test
    fun testInvertOne() {
        val matrix = LinearSpace.real.one(2, 2)
        val inverted = LinearSpace.real.inverseWithLup(matrix)
        assertEquals(matrix, inverted)
    }

    @Test
    fun testDecomposition() {
        LinearSpace.real.run {
            val matrix = matrix(2, 2)(
                3.0, 1.0,
                2.0, 3.0
            )

            val lup = lup(matrix)

            //Check determinant
            assertEquals(7.0, lup.determinant)

            assertEquals(lup.p dot matrix, lup.l dot lup.u)
        }
    }

    @Test
    fun testInvert() {
        val matrix = LinearSpace.real.matrix(2, 2)(
            3.0, 1.0,
            1.0, 3.0
        )

        val inverted = LinearSpace.real.inverseWithLup(matrix)

        val expected = LinearSpace.real.matrix(2, 2)(
            0.375, -0.125,
            -0.125, 0.375
        )

        assertEquals(expected, inverted)
    }
}
