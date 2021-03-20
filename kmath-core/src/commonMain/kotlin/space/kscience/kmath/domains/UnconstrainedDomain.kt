/* 
 * Copyright 2015 Alexander Nozik.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package space.kscience.kmath.domains

import space.kscience.kmath.linear.Point
import space.kscience.kmath.misc.UnstableKMathAPI

@UnstableKMathAPI
public class UnconstrainedDomain(public override val dimension: Int) : DoubleDomain {
    public override operator fun contains(point: Point<Double>): Boolean = true

    public override fun getLowerBound(num: Int): Double = Double.NEGATIVE_INFINITY

    public override fun getUpperBound(num: Int): Double = Double.POSITIVE_INFINITY

    public override fun volume(): Double = Double.POSITIVE_INFINITY
}
