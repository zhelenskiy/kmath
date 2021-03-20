package space.kscience.kmath.testutils

import space.kscience.kmath.operations.Ring
import space.kscience.kmath.operations.ScaleOperations
import space.kscience.kmath.operations.invoke
import kotlin.test.assertEquals

internal open class RingVerifier<T, A>(algebra: A, a: T, b: T, c: T, x: Number) :
    SpaceVerifier<T, A>(algebra, a, b, c, x) where A : Ring<T>, A : ScaleOperations<T> {

    override fun verify() {
        super.verify()

        algebra {
            assertEquals(a + b, b + a, "Addition in $algebra is not commutative.")
            assertEquals(a * b * c, a * (b * c), "Multiplication in $algebra is not associative.")
            assertEquals(c * (a + b), (c * a) + (c * b), "Multiplication in $algebra is not distributive.")
            assertEquals(a * one, one * a, "$one in $algebra is not a neutral multiplication element.")
            assertEquals(a, one * a, "$one in $algebra is not a neutral multiplication element.")
            assertEquals(a, a * one, "$one in $algebra is not a neutral multiplication element.")
            assertEquals(a, one * a, "$one in $algebra is not a neutral multiplication element.")
            assertEquals(a, a * one * one, "Multiplication by $one in $algebra is not idempotent.")
            assertEquals(a, a * one * one * one, "Multiplication by $one in $algebra is not idempotent.")
            assertEquals(a, a * one * one * one * one, "Multiplication by $one in $algebra is not idempotent.")
            assertEquals(zero, a * zero, "Multiplication by $zero in $algebra doesn't give $zero.")
            assertEquals(zero, zero * a, "Multiplication by $zero in $algebra doesn't give $zero.")
            assertEquals(a * zero, a * zero, "Multiplication by $zero in $algebra doesn't give $zero.")
        }
    }
}
