/*
 * Copyright 2018-2021 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.real

import space.kscience.kmath.linear.Point
import space.kscience.kmath.misc.UnstableKMathAPI
import space.kscience.kmath.operations.Norm
import space.kscience.kmath.structures.Buffer
import space.kscience.kmath.structures.MutableBuffer.Companion.real
import space.kscience.kmath.structures.asBuffer
import space.kscience.kmath.structures.asIterable
import space.kscience.kmath.structures.indices
import kotlin.math.pow
import kotlin.math.sqrt

public typealias RealVector = Point<Double>

public object VectorL2Norm : Norm<Point<out Number>, Double> {
    override fun norm(arg: Point<out Number>): Double = sqrt(arg.asIterable().sumByDouble(Number::toDouble))
}

public operator fun Buffer.Companion.invoke(vararg doubles: Double): RealVector = doubles.asBuffer()

/**
 * Fill the vector of given [size] with given [value]
 */
@UnstableKMathAPI
public fun Buffer.Companion.same(size: Int, value: Number): RealVector = real(size) { value.toDouble() }

// Transformation methods

public inline fun RealVector.map(transform: (Double) -> Double): RealVector =
    real(size) { transform(get(it)) }

public inline fun RealVector.mapIndexed(transform: (index: Int, value: Double) -> Double): RealVector =
    real(size) { transform(it, get(it)) }

public operator fun RealVector.plus(other: RealVector): RealVector {
    require(size == other.size) { "Vector size $size expected but ${other.size} found" }
    return mapIndexed { index, value -> value + other[index] }
}

public operator fun RealVector.plus(number: Number): RealVector = map { it + number.toDouble() }

public operator fun Number.plus(vector: RealVector): RealVector = vector + this

public operator fun RealVector.unaryMinus(): Buffer<Double> = map { -it }

public operator fun RealVector.minus(other: RealVector): RealVector {
    require(size == other.size) { "Vector size $size expected but ${other.size} found" }
    return mapIndexed { index, value -> value - other[index] }
}

public operator fun RealVector.minus(number: Number): RealVector = map { it - number.toDouble() }

public operator fun Number.minus(vector: RealVector): RealVector = vector.map { toDouble() - it }

public operator fun RealVector.times(other: RealVector): RealVector {
    require(size == other.size) { "Vector size $size expected but ${other.size} found" }
    return mapIndexed { index, value -> value * other[index] }
}

public operator fun RealVector.times(number: Number): RealVector = map { it * number.toDouble() }

public operator fun Number.times(vector: RealVector): RealVector = vector * this

public operator fun RealVector.div(other: RealVector): RealVector {
    require(size == other.size) { "Vector size $size expected but ${other.size} found" }
    return mapIndexed { index, value -> value / other[index] }
}

public operator fun RealVector.div(number: Number): RealVector = map { it / number.toDouble() }

public operator fun Number.div(vector: RealVector): RealVector = vector.map { toDouble() / it }

//extended operations

public fun RealVector.pow(p: Double): RealVector = map { it.pow(p) }

public fun RealVector.pow(p: Int): RealVector = map { it.pow(p) }

public fun exp(vector: RealVector): RealVector = vector.map { kotlin.math.exp(it) }

public fun sqrt(vector: RealVector): RealVector = vector.map { kotlin.math.sqrt(it) }

public fun RealVector.square(): RealVector = map { it.pow(2) }

public fun sin(vector: RealVector): RealVector = vector.map { kotlin.math.sin(it) }

public fun cos(vector: RealVector): RealVector = vector.map { kotlin.math.cos(it) }

public fun tan(vector: RealVector): RealVector = vector.map { kotlin.math.tan(it) }

public fun ln(vector: RealVector): RealVector = vector.map { kotlin.math.ln(it) }

public fun log10(vector: RealVector): RealVector = vector.map { kotlin.math.log10(it) }

// reductions methods

public fun RealVector.sum(): Double {
    var res = 0.0
    for (i in indices) {
        res += get(i)
    }
    return res
}