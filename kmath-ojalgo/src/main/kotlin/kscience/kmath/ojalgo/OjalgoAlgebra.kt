package kscience.kmath.ojalgo

import kscience.kmath.operations.Field
import kscience.kmath.operations.Ring
import kscience.kmath.operations.Space
import org.ojalgo.algebra.Operation
import org.ojalgo.algebra.ScalarOperation

public open class OjalgoSpace<T, N>(override val zero: T) : Space<T>
        where N : Comparable<N>, T : Operation.Addition<T>, T : ScalarOperation.Multiplication<T, N> {
    override fun add(a: T, b: T): T = a.add(b)
    override fun multiply(a: T, k: Number): T = a.multiply(k.toDouble())
}

public open class OjalgoRing<T, N>(zero: T, override val one: T) : OjalgoSpace<T, N>(zero), Ring<T>
        where N : Comparable<N>,
              T : Operation.Addition<T>,
              T : ScalarOperation.Multiplication<T, N>,
              T : Operation.Multiplication<T> {
    override fun multiply(a: T, b: T): T = a.multiply(b)
}

public open class OjalgoField<T, N>(zero: T, one: T) : OjalgoRing<T, N>(zero, one), Field<T>
        where N : Comparable<N>,
              T : Operation.Addition<T>,
              T : ScalarOperation.Multiplication<T, N>,
              T : Operation.Multiplication<T>,
              T : Operation.Division<T> {
    override fun divide(a: T, b: T): T = a.divide(b)
}

internal inline fun <T, R> Field<T>.convert(crossinline tToR: (T) -> R, crossinline rToT: (R) -> T): Field<R> =
    object : Field<R> {
        override val zero: R
            get() = tToR(this@convert.zero)

        override val one: R
            get() = tToR(this@convert.one)

        override fun add(a: R, b: R): R = tToR(this@convert.add(rToT(a), rToT(b)))
        override fun multiply(a: R, k: Number): R = tToR(this@convert.multiply(rToT(a), k))
        override fun multiply(a: R, b: R): R = tToR(this@convert.multiply(rToT(a), rToT(b)))
        override fun divide(a: R, b: R): R = tToR(this@convert.divide(rToT(a), rToT(b)))
    }
