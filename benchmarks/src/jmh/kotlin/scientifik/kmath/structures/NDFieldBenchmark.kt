package scientifik.kmath.structures

import org.openjdk.jmh.annotations.Benchmark
import scientifik.kmath.operations.RealField

open class NDFieldBenchmark {

    @Benchmark
    fun autoFieldAdd() {
        bufferedField.run {
            var res: NDBuffer<Double> = one
            repeat(n) {
                res += one
            }
        }
    }

    @Benchmark
    fun autoElementAdd() {
        var res = genericField.one
        repeat(n) {
            res += 1.0
        }
    }

    @Benchmark
    fun specializedFieldAdd() {
        specializedField.run {
            var res: NDBuffer<Double> = one
            repeat(n) {
                res += 1.0
            }
        }
    }


    @Benchmark
    fun boxingFieldAdd() {
        genericField.run {
            var res: NDBuffer<Double> = one
            repeat(n) {
                res += one
            }
        }
    }

    companion object {
        val dim = 1000
        val n = 100

        val bufferedField = NDField.auto(RealField, dim, dim)
        val specializedField = NDField.real(dim, dim)
        val genericField = NDField.buffered(intArrayOf(dim, dim), RealField)
    }
}