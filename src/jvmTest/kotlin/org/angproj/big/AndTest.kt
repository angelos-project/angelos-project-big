/**
 * Copyright (c) 2024 by Kristoffer Paulsson <kristoffer.paulsson@talenten.se>.
 *
 * This software is available under the terms of the MIT license. Parts are licensed
 * under different terms if stated. The legal terms are attached to the LICENSE file
 * and are made available on:
 *
 *      https://opensource.org/licenses/MIT
 *
 * SPDX-License-Identifier: MIT
 *
 * Contributors:
 *      Kristoffer Paulsson - initial implementation
 */
package org.angproj.big

import org.angproj.aux.io.toBinary
import kotlin.test.Test
import java.math.BigInteger as JavaBigInteger

class AndTest {
    /**
     * Generally fuzzes and validates that "public infix fun BigInt.and(value: BigMath<*>): BigInt" works
     * under all normal conditions. No special cases to test is currently known.
     * */
    @Test
    fun testAnd() {
        Combinator.numberGenerator(-64..64) { x ->
            val xBi2 = bigIntOf(x.toBinary())
            val xJbi = JavaBigInteger(x)
            Combinator.numberGenerator(-64..64) { y ->
                val yBi2 = bigIntOf(y)
                val yJbi = JavaBigInteger(y)

                Debugger.assertContentEquals(
                    x, y,
                    xJbi, yJbi,
                    xBi2, yBi2,
                    xJbi.and(yJbi),
                    xBi2.and(yBi2), // <- Emulation
                )
            }
        }
    }

    /**
     * Kotlin specific mimic of extension used for Java BigInteger.
     * */
    @Test
    fun testAndInfix() {
        Combinator.numberGenerator(-64..64) { x ->
            val xBi2 = bigIntOf(x.toBinary())
            val xJbi = JavaBigInteger(x)
            Combinator.numberGenerator(-64..64) { y ->
                val yBi2 = bigIntOf(y)
                val yJbi = JavaBigInteger(y)

                Debugger.assertContentEquals(
                    x, y,
                    xJbi, yJbi,
                    xBi2, yBi2,
                    xJbi.and(yJbi),
                    xBi2 and yBi2, // <- Kotlin specific
                )
            }
        }
    }
}