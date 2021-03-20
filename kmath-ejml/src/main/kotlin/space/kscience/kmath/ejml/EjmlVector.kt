package space.kscience.kmath.ejml

import org.ejml.simple.SimpleMatrix
import space.kscience.kmath.linear.Point

/**
 * Represents point over EJML [SimpleMatrix].
 *
 * @property origin the underlying [SimpleMatrix].
 * @author Iaroslav Postovalov
 */
public inline class EjmlVector internal constructor(public val origin: SimpleMatrix) : Point<Double> {
    public override val size: Int
        get() = origin.numRows()

    init {
        require(origin.numCols() == 1) { "Only single column matrices are allowed" }
    }

    public override operator fun get(index: Int): Double = origin[index]

    public override operator fun iterator(): Iterator<Double> = object : Iterator<Double> {
        private var cursor: Int = 0

        override fun next(): Double {
            cursor += 1
            return origin[cursor - 1]
        }

        override fun hasNext(): Boolean = cursor < origin.numCols() * origin.numRows()
    }

    public override fun toString(): String = "EjmlVector(origin=$origin)"
}
