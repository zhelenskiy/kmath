/*
 * Copyright 2018-2021 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.viktor

import org.jetbrains.bio.viktor.F64Array
import space.kscience.kmath.misc.UnstableKMathAPI
import space.kscience.kmath.nd.*
import space.kscience.kmath.operations.ExtendedField
import space.kscience.kmath.operations.NumbersAddOperations
import space.kscience.kmath.operations.RealField
import space.kscience.kmath.operations.ScaleOperations

@Suppress("OVERRIDE_BY_INLINE", "NOTHING_TO_INLINE")
public inline class ViktorNDStructure(public val f64Buffer: F64Array) : MutableNDStructure<Double> {
    public override val shape: IntArray get() = f64Buffer.shape

    public override inline fun get(index: IntArray): Double = f64Buffer.get(*index)

    public override inline fun set(index: IntArray, value: Double) {
        f64Buffer.set(*index, value = value)
    }

    public override fun elements(): Sequence<Pair<IntArray, Double>> =
        DefaultStrides(shape).indices().map { it to get(it) }
}

public fun F64Array.asStructure(): ViktorNDStructure = ViktorNDStructure(this)

@OptIn(UnstableKMathAPI::class)
@Suppress("OVERRIDE_BY_INLINE", "NOTHING_TO_INLINE")
public class ViktorNDField(public override val shape: IntArray) : NDField<Double, RealField>,
    NumbersAddOperations<NDStructure<Double>>, ExtendedField<NDStructure<Double>>,
    ScaleOperations<NDStructure<Double>> {

    public val NDStructure<Double>.f64Buffer: F64Array
        get() = when {
            !shape.contentEquals(this@ViktorNDField.shape) -> throw ShapeMismatchException(
                this@ViktorNDField.shape,
                shape
            )
            this is ViktorNDStructure && this.f64Buffer.shape.contentEquals(this@ViktorNDField.shape) -> this.f64Buffer
            else -> produce { this@f64Buffer[it] }.f64Buffer
        }

    public override val zero: ViktorNDStructure by lazy { F64Array.full(init = 0.0, shape = shape).asStructure() }

    public override val one: ViktorNDStructure by lazy { F64Array.full(init = 1.0, shape = shape).asStructure() }

    private val strides: Strides = DefaultStrides(shape)

    public override val elementContext: RealField get() = RealField

    public override fun produce(initializer: RealField.(IntArray) -> Double): ViktorNDStructure =
        F64Array(*shape).apply {
            this@ViktorNDField.strides.indices().forEach { index ->
                set(value = RealField.initializer(index), indices = index)
            }
        }.asStructure()

    override fun NDStructure<Double>.unaryMinus(): NDStructure<Double> = -1 * this

    public override fun NDStructure<Double>.map(transform: RealField.(Double) -> Double): ViktorNDStructure =
        F64Array(*this@ViktorNDField.shape).apply {
            this@ViktorNDField.strides.indices().forEach { index ->
                set(value = RealField.transform(this@map[index]), indices = index)
            }
        }.asStructure()

    public override fun NDStructure<Double>.mapIndexed(
        transform: RealField.(index: IntArray, Double) -> Double,
    ): ViktorNDStructure = F64Array(*this@ViktorNDField.shape).apply {
        this@ViktorNDField.strides.indices().forEach { index ->
            set(value = RealField.transform(index, this@mapIndexed[index]), indices = index)
        }
    }.asStructure()

    public override fun combine(
        a: NDStructure<Double>,
        b: NDStructure<Double>,
        transform: RealField.(Double, Double) -> Double,
    ): ViktorNDStructure = F64Array(*shape).apply {
        this@ViktorNDField.strides.indices().forEach { index ->
            set(value = RealField.transform(a[index], b[index]), indices = index)
        }
    }.asStructure()

    public override fun add(a: NDStructure<Double>, b: NDStructure<Double>): ViktorNDStructure =
        (a.f64Buffer + b.f64Buffer).asStructure()

    public override fun scale(a: NDStructure<Double>, value: Double): ViktorNDStructure =
        (a.f64Buffer * value.toDouble()).asStructure()

    public override inline fun NDStructure<Double>.plus(b: NDStructure<Double>): ViktorNDStructure =
        (f64Buffer + b.f64Buffer).asStructure()

    public override inline fun NDStructure<Double>.minus(b: NDStructure<Double>): ViktorNDStructure =
        (f64Buffer - b.f64Buffer).asStructure()

    public override inline fun NDStructure<Double>.times(k: Number): ViktorNDStructure =
        (f64Buffer * k.toDouble()).asStructure()

    public override inline fun NDStructure<Double>.plus(arg: Double): ViktorNDStructure =
        (f64Buffer.plus(arg)).asStructure()

    override fun number(value: Number): ViktorNDStructure =
        F64Array.full(init = value.toDouble(), shape = shape).asStructure()

    override fun sin(arg: NDStructure<Double>): ViktorNDStructure = arg.map { sin(it) }

    override fun cos(arg: NDStructure<Double>): ViktorNDStructure = arg.map { cos(it) }

    override fun asin(arg: NDStructure<Double>): ViktorNDStructure = arg.map { asin(it) }

    override fun acos(arg: NDStructure<Double>): ViktorNDStructure = arg.map { acos(it) }

    override fun atan(arg: NDStructure<Double>): ViktorNDStructure = arg.map { atan(it) }

    override fun power(arg: NDStructure<Double>, pow: Number): ViktorNDStructure = arg.map { it.pow(pow) }

    override fun exp(arg: NDStructure<Double>): ViktorNDStructure = arg.f64Buffer.exp().asStructure()

    override fun ln(arg: NDStructure<Double>): ViktorNDStructure = arg.f64Buffer.log().asStructure()
}

public fun ViktorNDField(vararg shape: Int): ViktorNDField = ViktorNDField(shape)