/*
 * Copyright 2018-2021 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.nd

import space.kscience.kmath.misc.UnstableKMathAPI
import space.kscience.kmath.operations.ExtendedField
import space.kscience.kmath.operations.NumbersAddOperations
import space.kscience.kmath.operations.RealField
import space.kscience.kmath.operations.ScaleOperations
import space.kscience.kmath.structures.RealBuffer
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

@OptIn(UnstableKMathAPI::class)
public class RealNDField(
    shape: IntArray,
) : BufferedNDField<Double, RealField>(shape, RealField, ::RealBuffer),
    NumbersAddOperations<NDStructure<Double>>,
    ScaleOperations<NDStructure<Double>>,
    ExtendedField<NDStructure<Double>> {

    override val zero: NDBuffer<Double> by lazy { produce { zero } }
    override val one: NDBuffer<Double> by lazy { produce { one } }

    override fun number(value: Number): NDBuffer<Double> {
        val d = value.toDouble() // minimize conversions
        return produce { d }
    }

    override val NDStructure<Double>.buffer: RealBuffer
        get() = when {
            !shape.contentEquals(this@RealNDField.shape) -> throw ShapeMismatchException(
                this@RealNDField.shape,
                shape
            )
            this is NDBuffer && this.strides == this@RealNDField.strides -> this.buffer as RealBuffer
            else -> RealBuffer(strides.linearSize) { offset -> get(strides.index(offset)) }
        }

    @Suppress("OVERRIDE_BY_INLINE")
    override inline fun NDStructure<Double>.map(
        transform: RealField.(Double) -> Double,
    ): NDBuffer<Double> {
        val buffer = RealBuffer(strides.linearSize) { offset -> RealField.transform(buffer.array[offset]) }
        return NDBuffer(strides, buffer)
    }

    @Suppress("OVERRIDE_BY_INLINE")
    override inline fun produce(initializer: RealField.(IntArray) -> Double): NDBuffer<Double> {
        val array = DoubleArray(strides.linearSize) { offset ->
            val index = strides.index(offset)
            RealField.initializer(index)
        }
        return NDBuffer(strides, RealBuffer(array))
    }

    @Suppress("OVERRIDE_BY_INLINE")
    override inline fun NDStructure<Double>.mapIndexed(
        transform: RealField.(index: IntArray, Double) -> Double,
    ): NDBuffer<Double> = NDBuffer(
        strides,
        buffer = RealBuffer(strides.linearSize) { offset ->
            RealField.transform(
                strides.index(offset),
                buffer.array[offset]
            )
        })

    @Suppress("OVERRIDE_BY_INLINE")
    override inline fun combine(
        a: NDStructure<Double>,
        b: NDStructure<Double>,
        transform: RealField.(Double, Double) -> Double,
    ): NDBuffer<Double> {
        val buffer = RealBuffer(strides.linearSize) { offset ->
            RealField.transform(a.buffer.array[offset], b.buffer.array[offset])
        }
        return NDBuffer(strides, buffer)
    }

    override fun scale(a: NDStructure<Double>, value: Double): NDStructure<Double> = a.map { it * value }

    override fun power(arg: NDStructure<Double>, pow: Number): NDBuffer<Double> = arg.map { power(it, pow) }

    override fun exp(arg: NDStructure<Double>): NDBuffer<Double> = arg.map { exp(it) }

    override fun ln(arg: NDStructure<Double>): NDBuffer<Double> = arg.map { ln(it) }

    override fun sin(arg: NDStructure<Double>): NDBuffer<Double> = arg.map { sin(it) }
    override fun cos(arg: NDStructure<Double>): NDBuffer<Double> = arg.map { cos(it) }
    override fun tan(arg: NDStructure<Double>): NDBuffer<Double> = arg.map { tan(it) }
    override fun asin(arg: NDStructure<Double>): NDBuffer<Double> = arg.map { asin(it) }
    override fun acos(arg: NDStructure<Double>): NDBuffer<Double> = arg.map { acos(it) }
    override fun atan(arg: NDStructure<Double>): NDBuffer<Double> = arg.map { atan(it) }

    override fun sinh(arg: NDStructure<Double>): NDBuffer<Double> = arg.map { sinh(it) }
    override fun cosh(arg: NDStructure<Double>): NDBuffer<Double> = arg.map { cosh(it) }
    override fun tanh(arg: NDStructure<Double>): NDBuffer<Double> = arg.map { tanh(it) }
    override fun asinh(arg: NDStructure<Double>): NDBuffer<Double> = arg.map { asinh(it) }
    override fun acosh(arg: NDStructure<Double>): NDBuffer<Double> = arg.map { acosh(it) }
    override fun atanh(arg: NDStructure<Double>): NDBuffer<Double> = arg.map { atanh(it) }
}

public fun NDAlgebra.Companion.real(vararg shape: Int): RealNDField = RealNDField(shape)

/**
 * Produce a context for n-dimensional operations inside this real field
 */
public inline fun <R> RealField.nd(vararg shape: Int, action: RealNDField.() -> R): R {
    contract { callsInPlace(action, InvocationKind.EXACTLY_ONCE) }
    return RealNDField(shape).run(action)
}
