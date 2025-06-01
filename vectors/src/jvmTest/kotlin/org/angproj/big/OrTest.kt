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

class OrTest {
    /**
     * Generally fuzzes and validates that "public infix fun BigInt.or(value: BigMath<*>): BigInt" works
     * under all normal conditions. No special cases to test is currently known.
     * */
    @Test
    fun testOr() = withLogic {
        Combinator.numberGenerator(-64..64) { x ->
            val xBi2 = bigIntOf(x.toBinary())
            val xJbi = JavaBigInteger(x)
            Combinator.numberGenerator(-64..64) { y ->
                val yBi2 = bigIntOf(y.toBinary())
                val yJbi = JavaBigInteger(y)

                Debugger.assertContentEquals(
                    x, y,
                    xJbi, yJbi,
                    xBi2, yBi2,
                    xJbi.or(yJbi),
                    xBi2.or(yBi2) // <- Emulation
                )
            }
        }
    }

    /**
     * Kotlin specific mimic of extension used for Java BigInteger.
     * */
    @Test
    fun testOrInfix() = withLogic {
        Combinator.numberGenerator(-64..64) { x ->
            val xBi2 = bigIntOf(x.toBinary())
            val xJbi = JavaBigInteger(x)
            Combinator.numberGenerator(-64..64) { y ->
                val yBi2 = bigIntOf(y.toBinary())
                val yJbi = JavaBigInteger(y)

                Debugger.assertContentEquals(
                    x, y,
                    xJbi, yJbi,
                    xBi2, yBi2,
                    xJbi.or(yJbi),
                    xBi2 or yBi2 // <- Kotlin specific
                )
            }
        }
    }
}