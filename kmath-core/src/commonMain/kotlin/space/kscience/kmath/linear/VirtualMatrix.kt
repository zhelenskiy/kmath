/*
 * Copyright 2018-2021 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.linear

import space.kscience.kmath.nd.NDStructure

/**
 * The matrix where each element is evaluated each time when is being accessed.
 *
 * @property generator the function that provides elements.
 */
public class VirtualMatrix<T : Any>(
    override val rowNum: Int,
    override val colNum: Int,
    public val generator: (i: Int, j: Int) -> T,
) : Matrix<T> {

    override val shape: IntArray get() = intArrayOf(rowNum, colNum)

    override operator fun get(i: Int, j: Int): T = generator(i, j)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is NDStructure<*>) return false
        return NDStructure.contentEquals(this, other)
    }

    override fun hashCode(): Int {
        var result = rowNum
        result = 31 * result + colNum
        result = 31 * result + generator.hashCode()
        return result
    }
}
