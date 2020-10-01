package kscience.kmath.ojalgo

import org.ojalgo.scalar.*

public val OjalgoMoney = OjalgoField<Scalar<Money>, Money>(Money.ZERO, Money.ONE)

public val OjalgoComplexNumber =
    OjalgoField<Scalar<ComplexNumber>, ComplexNumber>(ComplexNumber.ZERO, ComplexNumber.ONE)

public val OjalgoRationalNumber =
    OjalgoField<Scalar<RationalNumber>, RationalNumber>(RationalNumber.ZERO, RationalNumber.ONE)

public val OjalgoQuaternion = OjalgoField<Scalar<Quaternion>, Quaternion>(Quaternion.ZERO, Quaternion.ONE)

public val OjalgoPrimitiveScalar = OjalgoField<Scalar<Double>, Double>(PrimitiveScalar.ZERO, PrimitiveScalar.ONE)
