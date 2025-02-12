/*
 * Copyright 2018-2021 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.nd

import space.kscience.kmath.misc.UnstableKMathAPI
import space.kscience.kmath.structures.Buffer
import space.kscience.kmath.structures.VirtualBuffer
import kotlin.reflect.KClass

/**
 * A structure that is guaranteed to be two-dimensional.
 *
 * @param T the type of items.
 */
public interface Structure2D<T> : NDStructure<T> {
    /**
     * The number of rows in this structure.
     */
    public val rowNum: Int

    /**
     * The number of columns in this structure.
     */
    public val colNum: Int

    public override val shape: IntArray get() = intArrayOf(rowNum, colNum)

    /**
     * The buffer of rows of this structure. It gets elements from the structure dynamically.
     */
    public val rows: List<Buffer<T>>
        get() = List(rowNum) { i -> VirtualBuffer(colNum) { j -> get(i, j) } }

    /**
     * The buffer of columns of this structure. It gets elements from the structure dynamically.
     */
    public val columns: List<Buffer<T>>
        get() = List(colNum) { j -> VirtualBuffer(rowNum) { i -> get(i, j) } }

    /**
     * Retrieves an element from the structure by two indices.
     *
     * @param i the first index.
     * @param j the second index.
     * @return an element.
     */
    public operator fun get(i: Int, j: Int): T

    override operator fun get(index: IntArray): T {
        require(index.size == 2) { "Index dimension mismatch. Expected 2 but found ${index.size}" }
        return get(index[0], index[1])
    }

    override fun elements(): Sequence<Pair<IntArray, T>> = sequence {
        for (i in 0 until rowNum)
            for (j in 0 until colNum) yield(intArrayOf(i, j) to get(i, j))
    }

    public companion object
}

/**
 * A 2D wrapper for nd-structure
 */
private class Structure2DWrapper<T>(val structure: NDStructure<T>) : Structure2D<T> {
    override val shape: IntArray get() = structure.shape

    override val rowNum: Int get() = shape[0]
    override val colNum: Int get() = shape[1]

    override operator fun get(i: Int, j: Int): T = structure[i, j]

    @UnstableKMathAPI
    override fun <F : Any> getFeature(type: KClass<F>): F? = structure.getFeature(type)

    override fun elements(): Sequence<Pair<IntArray, T>> = structure.elements()

    override fun equals(other: Any?): Boolean = structure == other

    override fun hashCode(): Int = structure.hashCode()
}

/**
 * Represent a [NDStructure] as [Structure1D]. Throw error in case of dimension mismatch
 */
public fun <T> NDStructure<T>.as2D(): Structure2D<T> = this as? Structure2D<T> ?: when (shape.size) {
    2 -> Structure2DWrapper(this)
    else -> error("Can't create 2d-structure from ${shape.size}d-structure")
}

/**
 * Expose inner [NDStructure] if possible
 */
internal fun <T> Structure2D<T>.unwrap(): NDStructure<T> =
    if (this is Structure2DWrapper) structure
    else this