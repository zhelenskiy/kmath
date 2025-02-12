/*
 * Copyright 2018-2021 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package kaceince.kmath.real

import space.kscience.kmath.linear.LinearSpace
import space.kscience.kmath.linear.asMatrix
import space.kscience.kmath.linear.transpose
import space.kscience.kmath.real.plus
import space.kscience.kmath.structures.RealBuffer
import kotlin.test.Test
import kotlin.test.assertEquals

internal class RealVectorTest {
    @Test
    fun testSum() {
        val vector1 = RealBuffer(5) { it.toDouble() }
        val vector2 = RealBuffer(5) { 5 - it.toDouble() }
        val sum = vector1 + vector2
        assertEquals(5.0, sum[2])
    }

    @Test
    fun testVectorToMatrix() {
        val vector = RealBuffer(5) { it.toDouble() }
        val matrix = vector.asMatrix()
        assertEquals(4.0, matrix[4, 0])
    }

    @Test
    fun testDot() {
        val vector1 = RealBuffer(5) { it.toDouble() }
        val vector2 = RealBuffer(5) { 5 - it.toDouble() }
        val matrix1 = vector1.asMatrix()
        val matrix2 = vector2.asMatrix().transpose()
        val product = LinearSpace.real.run { matrix1 dot matrix2 }
        assertEquals(5.0, product[1, 0])
        assertEquals(6.0, product[2, 2])
    }
}
