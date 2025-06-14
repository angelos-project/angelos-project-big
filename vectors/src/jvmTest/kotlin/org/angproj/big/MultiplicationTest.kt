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

import org.angproj.sec.util.securelyRandomize
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertSame
import java.math.BigInteger as JavaBigInteger

class MultiplicationTest {
    /**
     * Generally fuzzes and validates that "public fun BigInt.multiply(value: BigMath<*>): BigInt" works
     * under all normal conditions.
     * */
    @Test
    fun testMultiply() {
        Combinator.numberGenerator(-64..64) { x ->
            val xBi2 = bigIntOf(x)
            val xJbi = JavaBigInteger(x)
            Combinator.numberGenerator(-64..64) { y ->
                val yBi2 = bigIntOf(y)
                val yJbi = JavaBigInteger(y)

                Debugger.assertContentEquals(
                    x, y,
                    xJbi, yJbi,
                    xBi2, yBi2,
                    xJbi.multiply(yJbi),
                    xBi2.multiply(yBi2), // <- Emulation
                )
            }
        }
    }

    /**
     * Kotlin specific mimic of extension used for Java BigInteger.
     * */
    @Test
    fun testTimes() {
        Combinator.numberGenerator(-64..64) { x ->
            val xBi2 = bigIntOf(x)
            val xJbi = JavaBigInteger(x)
            Combinator.innerNumberGenerator(-64..64) { y ->
                val yBi2 = bigIntOf(y)
                val yJbi = JavaBigInteger(y)

                Debugger.assertContentEquals(
                    x, y,
                    xJbi, yJbi,
                    xBi2, yBi2,
                    xJbi.multiply(yJbi),
                    xBi2 * yBi2, // <- Kotlin specific
                )
            }
        }
    }

    /**
     * Validates that first factor set to 0 is validated without a hiccup.
     * */
    @Test
    fun testFirstIfZero() {
        val y = ByteArray(64).also { it.securelyRandomize() }
        val xBi2 = BigInt.zero
        val xJbi = JavaBigInteger.ZERO

        val yBi2 = bigIntOf(y)
        val yJbi = JavaBigInteger(y)

        val rBi2 = yBi2.multiply(xBi2)
        val rJbi = yJbi.multiply(xJbi)

        assertSame(rBi2, BigInt.zero)
        assertContentEquals(rBi2.toByteArray(), rJbi.toByteArray())
    }

    /**
     * Validates that second factor set to 0 is validated without a hiccup.
     * */
    @Test
    fun testSecondIfZero() {
        val y = ByteArray(64).also { it.securelyRandomize() }
        val xBi2 = bigIntOf(y)
        val xJbi = JavaBigInteger(y)

        val yBi2 = BigInt.zero
        val yJbi = JavaBigInteger.ZERO

        val rBi2 = yBi2.multiply(xBi2)
        val rJbi = yJbi.multiply(xJbi)

        assertSame(rBi2, BigInt.zero)
        assertContentEquals(rBi2.toByteArray(), rJbi.toByteArray())
    }
}