package space.kscience.kmath.chains

import space.kscience.kmath.structures.DoubleBuffer

/**
 * Chunked, specialized chain for double values, which supports blocking [nextBlocking] operation
 */
public interface BlockingDoubleChain : BlockingBufferChain<Double> {

    /**
     * Returns an [DoubleArray] chunk of [size] values of [next].
     */
    public override fun nextBufferBlocking(size: Int): DoubleBuffer

    override suspend fun fork(): BlockingDoubleChain

    public companion object
}

public fun BlockingDoubleChain.map(transform: (Double) -> Double): BlockingDoubleChain = object : BlockingDoubleChain {
    override fun nextBufferBlocking(size: Int): DoubleBuffer {
        val block = this@map.nextBufferBlocking(size)
        return DoubleBuffer(size) { transform(block[it]) }
    }

    override suspend fun fork(): BlockingDoubleChain = this@map.fork().map(transform)
}