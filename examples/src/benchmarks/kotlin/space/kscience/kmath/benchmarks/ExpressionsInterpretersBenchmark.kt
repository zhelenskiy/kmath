package space.kscience.kmath.benchmarks

import kotlinx.benchmark.Benchmark
import kotlinx.benchmark.Blackhole
import kotlinx.benchmark.Scope
import kotlinx.benchmark.State
import space.kscience.kmath.asm.compile
import space.kscience.kmath.ast.mstInField
import space.kscience.kmath.expressions.Expression
import space.kscience.kmath.expressions.expressionInExtendedField
import space.kscience.kmath.expressions.invoke
import space.kscience.kmath.expressions.symbol
import space.kscience.kmath.operations.DoubleField
import space.kscience.kmath.operations.bindSymbol
import kotlin.random.Random

@State(Scope.Benchmark)
internal class ExpressionsInterpretersBenchmark {
    private companion object {
        private val algebra: ExtendedField<Double> = DoubleField
        private const val times: Int = 1_000_000

        private val functional: Expression<Double> = algebra.expressionInExtendedField {
            symbol("x") * const(2.0) + const(2.0) / symbol("x") - const(16.0) / sin(symbol("x"))
        }

        private val mst: Expression<Double> = algebra.mstInExtendedField {
            symbol("x") * 2.0 + 2.0 / symbol("x") - 16.0 / sin(symbol("x"))
        }

        private val asm: Expression<Double> = mst.compile()

        private val raw: Expression<Double> = run {
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

        blackhole.consume(sum)
    }
}
