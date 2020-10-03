package kscience.kmath.ojalgo

import kscience.kmath.linear.DeterminantFeature
import kscience.kmath.linear.FeaturedMatrix
import kscience.kmath.linear.MatrixFeature
import kscience.kmath.linear.Point
import kscience.kmath.structures.NDStructure
import org.ojalgo.array.RationalArray
import org.ojalgo.matrix.RationalMatrix
import org.ojalgo.scalar.RationalNumber

public class OjalgoRationalMatrix(public val origin: RationalMatrix, features: Set<MatrixFeature>?) :
    FeaturedMatrix<RationalNumber> {
    public override val features: Set<MatrixFeature> =
        features.orEmpty() union setOf(object : DeterminantFeature<RationalNumber> {
            override val determinant: RationalNumber
                get() = origin.determinant.get()
        })

    public override fun suggestFeature(vararg features: MatrixFeature): OjalgoRationalMatrix =
        OjalgoRationalMatrix(origin, this.features + features)

    public override fun get(i: Int, j: Int): RationalNumber = origin[i.toLong(), j.toLong()]

    public override fun equals(other: Any?): Boolean {
        if (other is OjalgoRationalMatrix) return origin == other.origin
        return NDStructure.equals(this, other as? NDStructure<*> ?: return false)
    }

    override fun hashCode(): Int {
        var result = origin.hashCode()
        result = 31 * result + features.hashCode()
        return result
    }

    public override fun toString(): String = "OjalgoRationalMatrix(origin=$origin, features=$features)"
}

public class OjalgoRationalVector(public val origin: RationalArray) : Point<RationalNumber> {
    override val size: Int
        get() = origin.size()

    public override fun get(index: Int): RationalNumber = origin[index.toLong()]
    public override fun iterator(): Iterator<RationalNumber> = origin.data.iterator()
    public override fun toString(): String = "OjalgoRationalVector(origin=$origin)"
}
