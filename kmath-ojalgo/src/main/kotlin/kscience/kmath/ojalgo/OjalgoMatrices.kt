package kscience.kmath.ojalgo

import kscience.kmath.linear.FeaturedMatrix
import kscience.kmath.linear.MatrixFeature
import org.ojalgo.matrix.RationalMatrix
import org.ojalgo.scalar.RationalNumber

class OjalgoRationalMatrix(val origin: RationalMatrix, features: Set<MatrixFeature>?) : FeaturedMatrix<RationalNumber> {
    override val features: Set<MatrixFeature> = features.orEmpty()

    override fun suggestFeature(vararg features: MatrixFeature): OjalgoRationalMatrix =
        OjalgoRationalMatrix(origin, this.features + features)

    override fun get(i: Int, j: Int): RationalNumber = origin.get(i.toLong(), j.toLong())

    override fun equals(other: Any?): Boolean {
        TODO()
    }

    override fun hashCode(): Int {
        TODO()
    }
}
