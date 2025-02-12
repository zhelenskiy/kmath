/*
 * Copyright 2018-2021 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.expressions

import space.kscience.kmath.operations.Algebra
import kotlin.jvm.JvmName
import kotlin.properties.ReadOnlyProperty

/**
 * A marker interface for a symbol. A symbol mus have an identity
 */
public interface Symbol {
    /**
     * Identity object for the symbol. Two symbols with the same identity are considered to be the same symbol.
     */
    public val identity: String
}

/**
 * A [Symbol] with a [String] identity
 */
public inline class StringSymbol(override val identity: String) : Symbol {
    override fun toString(): String = identity
}

/**
 * An elementary function that could be invoked on a map of arguments.
 *
 * @param T the type this expression takes as argument and returns.
 */
public fun interface Expression<T> {
    /**
     * Calls this expression from arguments.
     *
     * @param arguments the map of arguments.
     * @return the value.
     */
    public operator fun invoke(arguments: Map<Symbol, T>): T
}

/**
 * Calls this expression without providing any arguments.
 *
 * @return a value.
 */
public operator fun <T> Expression<T>.invoke(): T = invoke(emptyMap())

/**
 * Calls this expression from arguments.
 *
 * @param pairs the pairs of arguments to values.
 * @return a value.
 */
@JvmName("callBySymbol")
public operator fun <T> Expression<T>.invoke(vararg pairs: Pair<Symbol, T>): T = invoke(mapOf(*pairs))

/**
 * Calls this expression from arguments.
 *
 * @param pairs the pairs of arguments' names to values.
 * @return a value.
 */
@JvmName("callByString")
public operator fun <T> Expression<T>.invoke(vararg pairs: Pair<String, T>): T =
    invoke(mapOf(*pairs).mapKeys { StringSymbol(it.key) })


/**
 * A context for expression construction
 *
 * @param T type of the constants for the expression
 * @param E type of the actual expression state
 */
public interface ExpressionAlgebra<in T, E> : Algebra<E> {
    /**
     * Bind a given [Symbol] to this context variable and produce context-specific object. Return null if symbol could not be bound in current context.
     */
    public fun bindSymbolOrNull(symbol: Symbol): E?

    /**
     * Bind a string to a context using [StringSymbol]
     */
    override fun bindSymbol(value: String): E = bindSymbol(StringSymbol(value))

    /**
     * A constant expression which does not depend on arguments
     */
    public fun const(value: T): E
}

/**
 * Bind a given [Symbol] to this context variable and produce context-specific object.
 */
public fun <T, E> ExpressionAlgebra<T, E>.bindSymbol(symbol: Symbol): E =
    bindSymbolOrNull(symbol) ?: error("Symbol $symbol could not be bound to $this")

/**
 * A delegate to create a symbol with a string identity in this scope
 */
public val symbol: ReadOnlyProperty<Any?, Symbol> = ReadOnlyProperty { _, property ->
    StringSymbol(property.name)
}

/**
 * Bind a symbol by name inside the [ExpressionAlgebra]
 */
public fun <T, E> ExpressionAlgebra<T, E>.binding(): ReadOnlyProperty<Any?, E> = ReadOnlyProperty { _, property ->
    bindSymbol(StringSymbol(property.name)) ?: error("A variable with name ${property.name} does not exist")
}
