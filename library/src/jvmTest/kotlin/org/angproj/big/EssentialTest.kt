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

class EssentialTest {
    @Test
    fun testCompareTo() = withLogic {
        Combinator.numberGenerator(-64..64) { x ->
            val xBi2 = bigIntOf(x.toBinary())
            val xJbi = JavaBigInteger(x)
            Combinator.innerNumberGenerator(-64..64) { y ->
                val yBi2 = bigIntOf(y.toBinary())
                val yJbi = JavaBigInteger(y)

                Debugger.assertEquals(
                    x, y,
                    xJbi, yJbi,
                    xBi2, yBi2,
                    xJbi.compareTo(yJbi),
                    xBi2.compareSpecial(yBi2).state,
                )
            }
        }
    }

    /**
     * Generally fuzzes and validates that "public fun BigInt.negate(): BigInt" works
     * under all normal conditions.
     * */
    @Test
    fun testNegate() = withLogic {
        Combinator.numberGenerator(-64..64) { x ->
            val xBi2 = bigIntOf(x.toBinary())
            val xJbi = JavaBigInteger(x)

            Debugger.assertContentEquals(
                x,
                xJbi,
                xBi2,
                xJbi.negate(),
                xBi2.negate() // <- Emulation
            )
        }
    }

    /**
     * Kotlin specific mimic of extension used for Java BigInteger.
     * */
    @Test
    fun testUnaryMinus() = withLogic {
        Combinator.numberGenerator(-64..64) { x ->
            val xBi2 = bigIntOf(x.toBinary())
            val xJbi = JavaBigInteger(x)

            Debugger.assertContentEquals(
                x,
                xJbi,
                xBi2,
                xJbi.negate(),
                -xBi2 // <- Kotlin specific
            )
        }
    }

    /**
     * Generally fuzzes and validates that "public fun BigInt.abs(): BigInt" works
     * under all normal conditions.
     * */
    @Test
    fun testAbs() = withLogic {
        Combinator.numberGenerator(-64..64) { x ->
            val xBi2 = bigIntOf(x.toBinary())
            val xJbi = JavaBigInteger(x)

            Debugger.assertContentEquals(
                x,
                xJbi,
                xBi2,
                xJbi.abs(),
                xBi2.abs() // <- Emulation
            )
        }
    }

    /**
     * Generally fuzzes and validates that "public fun BigInt.min(value: BigInt): BigInt" works
     * under all normal conditions.
     * */
    @Test
    fun testMin() = withLogic {
        Combinator.numberGenerator(-64..64) { x ->
            val xBi2 = bigIntOf(x.toBinary())
            val xJbi = JavaBigInteger(x)
            Combinator.innerNumberGenerator(-64..64) { y ->
                val yBi2 = bigIntOf(y.toBinary())
                val yJbi = JavaBigInteger(y)

                Debugger.assertContentEquals(
                    x, y,
                    xJbi, yJbi,
                    xBi2, yBi2,
                    xJbi.min(yJbi),
                    xBi2.min(yBi2), // <- Emulation
                )
            }
        }
    }

    /**
     * Generally fuzzes and validates that "public fun BigInt.max(value: BigInt): BigInt" works
     * under all normal conditions.
     * */
    @Test
    fun testMax() = withLogic {
        Combinator.numberGenerator(-64..64) { x ->
            val xBi2 = bigIntOf(x.toBinary())
            val xJbi = JavaBigInteger(x)
            Combinator.innerNumberGenerator(-64..64) { y ->
                val yBi2 = bigIntOf(y.toBinary())
                val yJbi = JavaBigInteger(y)

                Debugger.assertContentEquals(
                    x, y,
                    xJbi, yJbi,
                    xBi2, yBi2,
                    xJbi.max(yJbi),
                    xBi2.max(yBi2), // <- Emulation
                )
            }
        }
    }
}