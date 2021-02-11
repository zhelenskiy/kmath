package kscience.kmath.benchmarks

import kscience.kmath.asm.compile
import kscience.kmath.ast.mstInExtendedField
import kscience.kmath.expressions.Expression
import kscience.kmath.expressions.expressionInExtendedField
import kscience.kmath.expressions.invoke
import kscience.kmath.expressions.symbol
import kscience.kmath.operations.ExtendedField
import kscience.kmath.operations.RealField
import org.openjdk.jmh.annotations.Benchmark
import org.openjdk.jmh.annotations.Scope
import org.openjdk.jmh.annotations.State
import kotlin.random.Random

@State(Scope.Benchmark)
internal class ExpressionsInterpretersBenchmark {
    private companion object {
        private val algebra: ExtendedField<Double> = RealField
        private const val times = 1_000_000

        val functional = algebra.expressionInExtendedField {
            symbol("x") * const(2.0) + const(2.0) / symbol("x") - const(16.0) / sin(symbol("x"))
        }

        val mst = algebra.mstInExtendedField {
            symbol("x") * 2.0 + 2.0 / symbol("x") - 16.0 / sin(symbol("x"))
        }

        val asm = mst.compile()

        val raw = run {
            val x by symbol

            Expression<Double> { args ->
                args.getValue(x) * 2.0 + 2.0 / args.getValue(x) - 16.0 / kotlin.math.sin(args.getValue(x))
            }
        }
    }

    @Benchmark
    fun functionalExpression() = invokeAndSum(functional)

    @Benchmark
    fun mstExpression() = invokeAndSum(mst)

    @Benchmark
    fun asmExpression() = invokeAndSum(asm)

    @Benchmark
    fun rawExpression() = invokeAndSum(raw)

    private fun invokeAndSum(expr: Expression<Double>) {
        val random = Random(0)
        var sum = 0.0

        repeat(times) {
            sum += expr("x" to random.nextDouble())
        }
    }
}
