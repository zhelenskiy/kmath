package space.kscience.kmath.complex

import space.kscience.kmath.operations.*
import kotlin.math.PI

///**
// * This complex's conjugate.
// */
//public val Complex.conjugate: Complex
//    get() = Complex(re, -im)
//
///**
// * This complex's reciprocal.
// */
//public val Complex.reciprocal: Complex
//    get() {
//        val scale = re * re + im * im
//        return Complex(re / scale, -im / scale)
//    }
//
///**
// * Absolute value of complex number.
// */
//public val Complex.r: Double
//    get() = sqrt(re * re + im * im)
//
///**
// * An angle between vector represented by complex number and X axis.
// */
//public val Complex.theta: Double
//    get() = atan(im / re)
//
private const val PI_DIV_2: Double = PI / 2.0


//
//    /**
//     * Adds complex number to real one.
//     *
//     * @receiver the addend.
//     * @param c the augend.
//     * @return the sum.
//     */
//    public operator fun Double.plus(c: Complex): Complex = add(this.toComplex(), c)
//
//    /**
//     * Subtracts complex number from real one.
//     *
//     * @receiver the minuend.
//     * @param c the subtrahend.
//     * @return the difference.
//     */
//    public operator fun Double.minus(c: Complex): Complex = add(this.toComplex(), -c)
//
//    /**
//     * Adds real number to complex one.
//     *
//     * @receiver the addend.
//     * @param d the augend.
//     * @return the sum.
//     */
//    public operator fun Complex.plus(d: Double): Complex = d + this
//
//    /**
//     * Subtracts real number from complex one.
//     *
//     * @receiver the minuend.
//     * @param d the subtrahend.
//     * @return the difference.
//     */
//    public operator fun Complex.minus(d: Double): Complex = add(this, -d.toComplex())
//
//    /**
//     * Multiplies real number by complex one.
//     *
//     * @receiver the multiplier.
//     * @param c the multiplicand.
//     * @receiver the product.
//     */
//    public operator fun Double.times(c: Complex): Complex = Complex(c.re * this, c.im * this)
//
//    public override fun norm(arg: Complex): Complex = sqrt(arg.conjugate * arg)
//
//    public override fun bindSymbol(value: String): Complex =
//        if (value == "i") i else super<ExtendedField>.bindSymbol(value)
//}
//
///**
// * Creates a complex number with real part equal to this real.
// *
// * @receiver the real part.
// * @return the new complex number.
// */
//public fun Number.toComplex(): Complex = Complex(this)
//
///**
// * Creates a new buffer of complex numbers with the specified [size], where each element is calculated by calling the
// * specified [init] function.
// */
//public inline fun Buffer.Companion.complex(size: Int, init: (Int) -> Complex): Buffer<Complex> =
//    MemoryBuffer.create(Complex, size, init)
//
///**
// * Creates a new buffer of complex numbers with the specified [size], where each element is calculated by calling the
// * specified [init] function.
// */
//public inline fun MutableBuffer.Companion.complex(size: Int, init: (Int) -> Complex): MutableBuffer<Complex> =
//    MutableMemoryBuffer.create(Complex, size, init)

/**
 * Represents generic complex value consisting of real and imaginary part.
 *
 * @param T the type of value's components.
 * @property re the real component.
 * @property im the imaginary component.
 */
public data class Complex<T : Comparable<T>>(val re: T, val im: T) {
    public override fun toString(): String = "($re + i*$im)"
}

/**
 * The algebra of [Complex].
 */
public open class ComplexAlgebra<T>(public open val algebra: NumericAlgebra<T>) :
    NumericAlgebra<Complex<T>> where T : Comparable<T> {
    /**
     * The imaginary unit.
     */
    public val i: Complex<T> by lazy {
        algebra { Complex(number(0.0), number(1.0)) }
    }

    public override fun number(value: Number): Complex<T> =
        algebra { Complex(algebra.number(value), algebra.number(0.0)) }

    public override fun bindSymbol(value: String): Complex<T> = if (value == "i") i else super.bindSymbol(value)
}

/**
 * The group of [Complex].
 */
public open class ComplexGroup<T, A>(public override val algebra: A) : ComplexAlgebra<T>(algebra),
    Group<Complex<T>> where A : NumericAlgebra<T>, A : Group<T>, T : Comparable<T> {
    public override val zero: Complex<T> by lazy {
        algebra { Complex(zero, zero) }
    }

    public override fun add(a: Complex<T>, b: Complex<T>): Complex<T> = algebra { Complex(a.re + b.re, a.im + b.im) }
    public override fun Complex<T>.unaryMinus(): Complex<T> = algebra { Complex(-re, -im) }
}

/**
 * The ring of [Complex].
 */
public open class ComplexRing<T, A>(public override val algebra: A) : ComplexGroup<T, A>(algebra),
    Ring<Complex<T>> where A : NumericAlgebra<T>, A : Ring<T>, T : Comparable<T> {
    public override val one: Complex<T> by lazy {
        algebra { Complex(one, one) }
    }

    public override fun multiply(a: Complex<T>, b: Complex<T>): Complex<T> =
        algebra { Complex(a.re * b.re - a.im * b.im, a.re * b.im + a.im * b.re) }
}

/**
 * The field of [Complex].
 */
public open class ComplexField<T, A>(public override val algebra: A) : ComplexRing<T, A>(algebra),
    Field<Complex<T>> where A : Field<T>, T : Comparable<T> {
    public override fun divide(a: Complex<T>, b: Complex<T>): Complex<T> = algebra {
        when {
            abs(b.im) < abs(b.re) -> {
                val wr = b.im / b.re
                val wd = b.re + wr * b.im

                if (wd != wd || wd == 0.0)
                    throw ArithmeticException("Division by zero or infinity")
                else
                    Complex((a.re + a.im * wr) / wd, (a.im - a.re * wr) / wd)
            }

            b.im == 0.0 -> throw ArithmeticException("Division by zero")

            else -> {
                val wr = b.re / b.im
                val wd = b.im + wr * b.re

                if (wd != wd || wd == 0.0)
                    throw ArithmeticException("Division by zero or infinity")
                else
                    Complex((a.re * wr + a.im) / wd, (a.im * wr - a.re) / wd)
            }
        }
    }

    public override fun number(value: Number): Complex<T> = super<ComplexRing>.number(value)

    public override fun scale(a: Complex<T>, value: Double): Complex<T> =
        algebra { Complex(a.re * value, a.im * value) }

    public override operator fun Complex<T>.div(k: Number): Complex<T> =
        algebra { Complex(re / k.toDouble(), im / k.toDouble()) }
}

/**
 * The extended field of Complex.
 */
public class ComplexExtendedField<T, A>(public override val algebra: A) : ComplexField<T, A>(algebra),
    ExtendedField<Complex<T>>, Norm<Complex<T>, T> where T : Comparable<T>, A : ExtendedField<T> {

    public override fun sin(arg: Complex<T>): Complex<T> = i * (exp(-i * arg) - exp(i * arg)) / 2.0
    public override fun cos(arg: Complex<T>): Complex<T> = (exp(-i * arg) + exp(i * arg)) / 2.0

    public override fun tan(arg: Complex<T>): Complex<T> {
        val e1 = exp(-i * arg)
        val e2 = exp(i * arg)
        return i * (e1 - e2) / (e1 + e2)
    }

    public override fun asin(arg: Complex<T>): Complex<T> = -i * ln(sqrt(one - (arg * arg)) + i * arg)
    public override fun acos(arg: Complex<T>): Complex<T> = number(PI_DIV_2) + i * ln(sqrt(one - (arg * arg)) + i * arg)

    public override fun atan(arg: Complex<T>): Complex<T> = algebra {
        val iArg = i * arg
        return i * (ln(this@ComplexExtendedField.one - iArg) - ln(this@ComplexExtendedField.one + iArg)) / 2
    }

    public override fun power(arg: Complex<T>, pow: Number): Complex<T> = algebra {
        if (arg.im == 0.0)
            Complex(arg.re.pow(pow.toDouble()), algebra.zero)
        else
            exp(pow * ln(arg))
    }

    public override fun exp(arg: Complex<T>): Complex<T> =
        Complex(algebra.exp(arg.re), algebra.zero) * Complex(algebra.cos(arg.im), algebra.sin(arg.im))

    public override fun ln(arg: Complex<T>): Complex<T> = algebra { Complex(ln(norm(arg)), atan(arg.im / arg.re)) }
    public override fun norm(arg: Complex<T>): T = algebra { sqrt(arg.re * arg.re + arg.im * arg.im) }
}
