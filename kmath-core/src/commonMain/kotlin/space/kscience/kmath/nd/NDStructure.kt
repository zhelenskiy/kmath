/*
 * Copyright 2018-2021 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.nd

import space.kscience.kmath.misc.UnstableKMathAPI
import space.kscience.kmath.structures.Buffer
import space.kscience.kmath.structures.BufferFactory
import space.kscience.kmath.structures.MutableBuffer
import space.kscience.kmath.structures.asSequence
import kotlin.jvm.JvmName
import kotlin.native.concurrent.ThreadLocal
import kotlin.reflect.KClass

/**
 * Represents n-dimensional structure, i.e. multidimensional container of items of the same type and size. The number
 * of dimensions and items in an array is defined by its shape, which is a sequence of non-negative integers that
 * specify the sizes of each dimension.
 *
 * @param T the type of items.
 */
public interface NDStructure<T> {
    /**
     * The shape of structure, i.e. non-empty sequence of non-negative integers that specify sizes of dimensions of
     * this structure.
     */
    public val shape: IntArray

    /**
     * The count of dimensions in this structure. It should be equal to size of [shape].
     */
    public val dimension: Int get() = shape.size

    /**
     * Returns the value at the specified indices.
     *
     * @param index the indices.
     * @return the value.
     */
    public operator fun get(index: IntArray): T

    /**
     * Returns the sequence of all the elements associated by their indices.
     *
     * @return the lazy sequence of pairs of indices to values.
     */
    public fun elements(): Sequence<Pair<IntArray, T>>

    //force override equality and hash code
    public override fun equals(other: Any?): Boolean
    public override fun hashCode(): Int

    /**
     * Feature is some additional strucure information which allows to access it special properties or hints.
     * If the feature is not present, null is returned.
     */
    @UnstableKMathAPI
    public fun <F : Any> getFeature(type: KClass<F>): F? = null

    public companion object {
        /**
         * Indicates whether some [NDStructure] is equal to another one.
         */
        public fun contentEquals(st1: NDStructure<*>, st2: NDStructure<*>): Boolean {
            if (st1 === st2) return true

            // fast comparison of buffers if possible
            if (st1 is NDBuffer && st2 is NDBuffer && st1.strides == st2.strides)
                return st1.buffer.contentEquals(st2.buffer)

            //element by element comparison if it could not be avoided
            return st1.elements().all { (index, value) -> value == st2[index] }
        }

        /**
         * Creates a NDStructure with explicit buffer factory.
         *
         * Strides should be reused if possible.
         */
        public fun <T> buffered(
            strides: Strides,
            bufferFactory: BufferFactory<T> = Buffer.Companion::boxing,
            initializer: (IntArray) -> T,
        ): NDBuffer<T> = NDBuffer(strides, bufferFactory(strides.linearSize) { i -> initializer(strides.index(i)) })

        /**
         * Inline create NDStructure with non-boxing buffer implementation if it is possible
         */
        public inline fun <reified T : Any> auto(
            strides: Strides,
            crossinline initializer: (IntArray) -> T,
        ): NDBuffer<T> = NDBuffer(strides, Buffer.auto(strides.linearSize) { i -> initializer(strides.index(i)) })

        public inline fun <T : Any> auto(
            type: KClass<T>,
            strides: Strides,
            crossinline initializer: (IntArray) -> T,
        ): NDBuffer<T> = NDBuffer(strides, Buffer.auto(type, strides.linearSize) { i -> initializer(strides.index(i)) })

        public fun <T> buffered(
            shape: IntArray,
            bufferFactory: BufferFactory<T> = Buffer.Companion::boxing,
            initializer: (IntArray) -> T,
        ): NDBuffer<T> = buffered(DefaultStrides(shape), bufferFactory, initializer)

        public inline fun <reified T : Any> auto(
            shape: IntArray,
            crossinline initializer: (IntArray) -> T,
        ): NDBuffer<T> = auto(DefaultStrides(shape), initializer)

        @JvmName("autoVarArg")
        public inline fun <reified T : Any> auto(
            vararg shape: Int,
            crossinline initializer: (IntArray) -> T,
        ): NDBuffer<T> =
            auto(DefaultStrides(shape), initializer)

        public inline fun <T : Any> auto(
            type: KClass<T>,
            vararg shape: Int,
            crossinline initializer: (IntArray) -> T,
        ): NDBuffer<T> = auto(type, DefaultStrides(shape), initializer)
    }
}

/**
 * Returns the value at the specified indices.
 *
 * @param index the indices.
 * @return the value.
 */
public operator fun <T> NDStructure<T>.get(vararg index: Int): T = get(index)

@UnstableKMathAPI
public inline fun <reified T : Any> NDStructure<*>.getFeature(): T? = getFeature(T::class)

/**
 * Represents mutable [NDStructure].
 */
public interface MutableNDStructure<T> : NDStructure<T> {
    /**
     * Inserts an item at the specified indices.
     *
     * @param index the indices.
     * @param value the value.
     */
    public operator fun set(index: IntArray, value: T)
}

/**
 * Transform a structure element-by element in place.
 */
public inline fun <T> MutableNDStructure<T>.mapInPlace(action: (IntArray, T) -> T): Unit =
    elements().forEach { (index, oldValue) -> this[index] = action(index, oldValue) }

/**
 * A way to convert ND index to linear one and back.
 */
public interface Strides {
    /**
     * Shape of NDStructure
     */
    public val shape: IntArray

    /**
     * Array strides
     */
    public val strides: List<Int>

    /**
     * Get linear index from multidimensional index
     */
    public fun offset(index: IntArray): Int

    /**
     * Get multidimensional from linear
     */
    public fun index(offset: Int): IntArray

    /**
     * The size of linear buffer to accommodate all elements of ND-structure corresponding to strides
     */
    public val linearSize: Int

    // TODO introduce a fast way to calculate index of the next element?

    /**
     * Iterate over ND indices in a natural order
     */
    public fun indices(): Sequence<IntArray> = (0 until linearSize).asSequence().map {
        index(it)
    }
}

/**
 * Simple implementation of [Strides].
 */
public class DefaultStrides private constructor(override val shape: IntArray) : Strides {
    override val linearSize: Int
        get() = strides[shape.size]

    /**
     * Strides for memory access
     */
    override val strides: List<Int> by lazy {
        sequence {
            var current = 1
            yield(1)

            shape.forEach {
                current *= it
                yield(current)
            }
        }.toList()
    }

    override fun offset(index: IntArray): Int = index.mapIndexed { i, value ->
        if (value < 0 || value >= shape[i]) throw IndexOutOfBoundsException("Index $value out of shape bounds: (0,${this.shape[i]})")
        value * strides[i]
    }.sum()

    override fun index(offset: Int): IntArray {
        val res = IntArray(shape.size)
        var current = offset
        var strideIndex = strides.size - 2

        while (strideIndex >= 0) {
            res[strideIndex] = (current / strides[strideIndex])
            current %= strides[strideIndex]
            strideIndex--
        }

        return res
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is DefaultStrides) return false
        if (!shape.contentEquals(other.shape)) return false
        return true
    }

    override fun hashCode(): Int = shape.contentHashCode()

    @ThreadLocal
    public companion object {
        private val defaultStridesCache = HashMap<IntArray, Strides>()

        /**
         * Cached builder for default strides
         */
        public operator fun invoke(shape: IntArray): Strides =
            defaultStridesCache.getOrPut(shape) { DefaultStrides(shape) }
    }
}

/**
 * Represents [NDStructure] over [Buffer].
 *
 * @param T the type of items.
 * @param strides The strides to access elements of [Buffer] by linear indices.
 * @param buffer The underlying buffer.
 */
public open class NDBuffer<T>(
    public val strides: Strides,
    buffer: Buffer<T>,
) : NDStructure<T> {

    init {
        if (strides.linearSize != buffer.size) {
            error("Expected buffer side of ${strides.linearSize}, but found ${buffer.size}")
        }
    }

    public open val buffer: Buffer<T> = buffer

    override operator fun get(index: IntArray): T = buffer[strides.offset(index)]

    override val shape: IntArray get() = strides.shape

    override fun elements(): Sequence<Pair<IntArray, T>> = strides.indices().map {
        it to this[it]
    }

    override fun equals(other: Any?): Boolean {
        return NDStructure.contentEquals(this, other as? NDStructure<*> ?: return false)
    }

    override fun hashCode(): Int {
        var result = strides.hashCode()
        result = 31 * result + buffer.hashCode()
        return result
    }

    override fun toString(): String {
        val bufferRepr: String = when (shape.size) {
            1 -> buffer.asSequence().joinToString(prefix = "[", postfix = "]", separator = ", ")
            2 -> (0 until shape[0]).joinToString(prefix = "[", postfix = "]", separator = ", ") { i ->
                (0 until shape[1]).joinToString(prefix = "[", postfix = "]", separator = ", ") { j ->
                    val offset = strides.offset(intArrayOf(i, j))
                    buffer[offset].toString()
                }
            }
            else -> "..."
        }
        return "NDBuffer(shape=${shape.contentToString()}, buffer=$bufferRepr)"
    }
}

/**
 * Transform structure to a new structure using provided [BufferFactory] and optimizing if argument is [NDBuffer]
 */
public inline fun <T, reified R : Any> NDStructure<T>.mapToBuffer(
    factory: BufferFactory<R> = Buffer.Companion::auto,
    crossinline transform: (T) -> R,
): NDBuffer<R> {
    return if (this is NDBuffer<T>)
        NDBuffer(this.strides, factory.invoke(strides.linearSize) { transform(buffer[it]) })
    else {
        val strides = DefaultStrides(shape)
        NDBuffer(strides, factory.invoke(strides.linearSize) { transform(get(strides.index(it))) })
    }
}

/**
 * Mutable ND buffer based on linear [MutableBuffer].
 */
public class MutableNDBuffer<T>(
    strides: Strides,
    buffer: MutableBuffer<T>,
) : NDBuffer<T>(strides, buffer), MutableNDStructure<T> {

    init {
        require(strides.linearSize == buffer.size) {
            "Expected buffer side of ${strides.linearSize}, but found ${buffer.size}"
        }
    }

    override val buffer: MutableBuffer<T> = super.buffer as MutableBuffer<T>

    override operator fun set(index: IntArray, value: T): Unit = buffer.set(strides.offset(index), value)
}

public inline fun <reified T : Any> NDStructure<T>.combine(
    struct: NDStructure<T>,
    crossinline block: (T, T) -> T,
): NDStructure<T> {
    require(shape.contentEquals(struct.shape)) { "Shape mismatch in structure combination" }
    return NDStructure.auto(shape) { block(this[it], struct[it]) }
}
