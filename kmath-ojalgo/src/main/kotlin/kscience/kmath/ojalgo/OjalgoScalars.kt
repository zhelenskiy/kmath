package kscience.kmath.ojalgo

import kscience.kmath.operations.Complex
import kscience.kmath.operations.Field
import org.ojalgo.scalar.*

public val OjalgoMoneyField: Field<Scalar<Money>> = OjalgoField<Scalar<Money>, Money>(Money.ZERO, Money.ONE)

public val OjalgoComplexField: Field<Complex> =
    OjalgoField<Scalar<ComplexNumber>, ComplexNumber>(ComplexNumber.ZERO, ComplexNumber.ONE).convert(
        { Complex(it.get().real, it.get().imaginary) },
        { ComplexNumber.of(it.re, it.im) })

public val OjalgoRationalField: Field<Double> =
    OjalgoField<Scalar<RationalNumber>, RationalNumber>(
        RationalNumber.ZERO,
        RationalNumber.ONE
    ).convert(Scalar<RationalNumber>::doubleValue, RationalNumber::valueOf)

// TODO
public val OjalgoQuaternionField: Field<Scalar<Quaternion>> =
    OjalgoField<Scalar<Quaternion>, Quaternion>(Quaternion.ZERO, Quaternion.ONE)

public val OjalgoPrimitiveField: Field<Double> =
    OjalgoField<Scalar<Double>, Double>(PrimitiveScalar.ZERO, PrimitiveScalar.ONE).convert(
        Scalar<Double>::doubleValue,
        PrimitiveScalar::valueOf
    )
