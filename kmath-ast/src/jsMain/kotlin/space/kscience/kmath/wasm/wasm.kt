package space.kscience.kmath.wasm

import space.kscience.kmath.ast.MST
import space.kscience.kmath.ast.MstExpression
import space.kscience.kmath.expressions.Expression
import space.kscience.kmath.operations.DoubleField
import space.kscience.kmath.operations.IntRing
import space.kscience.kmath.wasm.internal.IntWasmBuilder
import space.kscience.kmath.wasm.internal.RealWasmBuilder

/**
 * Compiles an [MST] to WASM in the context of reals.
 *
 * @author Iaroslav Postovalov.
 */
public fun DoubleField.expression(mst: MST): Expression<Double> =
    RealWasmBuilder(mst).instance

/**
 * Compiles an [MST] to WASM in the context of integers.
 *
 * @author Iaroslav Postovalov.
 */
public fun IntRing.expression(mst: MST): Expression<Int> =
    IntWasmBuilder(mst).instance

/**
 * Optimizes performance of an [MstExpression] using WASM codegen in the context of reals.
 *
 * @author Iaroslav Postovalov.
 */
public fun MstExpression<Double, DoubleField>.compile(): Expression<Double> =
    RealWasmBuilder(mst).instance

/**
 * Optimizes performance of an [MstExpression] using WASM codegen in the context of integers.
 *
 * @author Iaroslav Postovalov.
 */
public fun MstExpression<Int, IntRing>.compile(): Expression<Int> =
    IntWasmBuilder(mst).instance
