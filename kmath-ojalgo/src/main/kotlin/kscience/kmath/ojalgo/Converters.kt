package kscience.kmath.ojalgo

import kscience.kmath.operations.Complex
import org.ojalgo.scalar.ComplexNumber
import org.ojalgo.scalar.RationalNumber

public fun Double.asOjalgoRational(): RationalNumber = RationalNumber.rational(this)
public fun Complex.asOjalgoComplex(): ComplexNumber = ComplexNumber.of(re, im)
public fun RationalNumber.asKMathReal(): Double = doubleValue()
public fun ComplexNumber.asKMathComplex(): Complex = Complex(real, imaginary)
