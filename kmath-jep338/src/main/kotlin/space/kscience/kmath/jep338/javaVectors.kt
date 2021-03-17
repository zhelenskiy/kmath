package space.kscience.kmath.jep338

import jdk.incubator.vector.DoubleVector
import jdk.incubator.vector.VectorSpecies
import space.kscience.kmath.structures.Buffer
import space.kscience.kmath.structures.asSequence

/**
 * Implements
 */
public class JRealVector(public val handle: DoubleVector) : Buffer<Double> {
    public override val size: Int
        get() = handle.length()

    public override fun get(index: Int): Double = handle.lane(index)

    public override fun iterator(): Iterator<Double> = object : AbstractIterator<Double>() {
        private var i = 0

        override fun computeNext() {
            if (i == size)
                done()
            else {
                setNext(get(i))
                i++
            }
        }
    }

    public override fun contentEquals(other: Buffer<*>): Boolean {
        if (other is JRealVector) return other.handle == handle
        return super.contentEquals(other)
    }
}
