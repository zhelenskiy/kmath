package space.kscience.kmath.real

import space.kscience.kmath.nd.BufferND
import space.kscience.kmath.operations.DoubleField
import space.kscience.kmath.structures.DoubleBuffer

/**
 * Map one [BufferND] using function without indices.
 */
public inline fun BufferND<Double>.mapInline(crossinline transform: DoubleField.(Double) -> Double): BufferND<Double> {
    val array = DoubleArray(strides.linearSize) { offset -> DoubleField.transform(buffer[offset]) }
    return BufferND(strides, DoubleBuffer(array))
}

/**
 * Element by element application of any operation on elements to the whole array. Just like in numpy.
 */
public operator fun Function1<Double, Double>.invoke(elementND: BufferND<Double>): BufferND<Double> =
    elementND.mapInline { this@invoke(it) }

/* plus and minus */

/**
 * Summation operation for [BufferND] and single element
 */
public operator fun BufferND<Double>.plus(arg: Double): BufferND<Double> = mapInline { it + arg }

/**
 * Subtraction operation between [BufferND] and single element
 */
public operator fun BufferND<Double>.minus(arg: Double): BufferND<Double> = mapInline { it - arg }