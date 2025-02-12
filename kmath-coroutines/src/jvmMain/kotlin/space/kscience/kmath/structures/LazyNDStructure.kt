/*
 * Copyright 2018-2021 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.structures

import kotlinx.coroutines.*
import space.kscience.kmath.coroutines.Math
import space.kscience.kmath.nd.DefaultStrides
import space.kscience.kmath.nd.NDStructure

public class LazyNDStructure<T>(
    public val scope: CoroutineScope,
    public override val shape: IntArray,
    public val function: suspend (IntArray) -> T
) : NDStructure<T> {
    private val cache: MutableMap<IntArray, Deferred<T>> = hashMapOf()

    public fun deferred(index: IntArray): Deferred<T> = cache.getOrPut(index) {
        scope.async(context = Dispatchers.Math) { function(index) }
    }

    public suspend fun await(index: IntArray): T = deferred(index).await()
    public override operator fun get(index: IntArray): T = runBlocking { deferred(index).await() }

    public override fun elements(): Sequence<Pair<IntArray, T>> {
        val strides = DefaultStrides(shape)
        val res = runBlocking { strides.indices().toList().map { index -> index to await(index) } }
        return res.asSequence()
    }

    public override fun equals(other: Any?): Boolean {
        return NDStructure.contentEquals(this, other as? NDStructure<*> ?: return false)
    }

    public override fun hashCode(): Int {
        var result = scope.hashCode()
        result = 31 * result + shape.contentHashCode()
        result = 31 * result + function.hashCode()
        result = 31 * result + cache.hashCode()
        return result
    }
}

public fun <T> NDStructure<T>.deferred(index: IntArray): Deferred<T> =
    if (this is LazyNDStructure<T>) deferred(index) else CompletableDeferred(get(index))

public suspend fun <T> NDStructure<T>.await(index: IntArray): T =
    if (this is LazyNDStructure<T>) await(index) else get(index)

/**
 * PENDING would benefit from KEEP-176
 */
public inline fun <T, R> NDStructure<T>.mapAsyncIndexed(
    scope: CoroutineScope,
    crossinline function: suspend (T, index: IntArray) -> R
): LazyNDStructure<R> = LazyNDStructure(scope, shape) { index -> function(get(index), index) }

public inline fun <T, R> NDStructure<T>.mapAsync(
    scope: CoroutineScope,
    crossinline function: suspend (T) -> R
): LazyNDStructure<R> = LazyNDStructure(scope, shape) { index -> function(get(index)) }
