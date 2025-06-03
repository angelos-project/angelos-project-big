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

import org.angproj.aux.io.toByteArray
import org.angproj.aux.mem.BufMgr
import org.angproj.aux.sec.SecureRandom
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertSame
import java.math.BigInteger as JavaBigInteger

class SubtractionTest {
    /**
     * Generally fuzzes and validates that "public fun BigInt.subtract(value: BigMath<*>): BigInt" works
     * under all normal conditions.
     * */
    @Test
    fun testSubtract() {
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
                    xJbi.subtract(yJbi),
                    xBi2.subtract(yBi2), // <- Emulation
                )
            }
        }
    }

    /**
     * Kotlin specific mimic of extension used for Java BigInteger.
     * */
    @Test
    fun testMinus() {
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
                    xJbi.subtract(yJbi),
                    xBi2 - yBi2, // <- Kotlin specific
                )
            }
        }
    }

    /**
     * Kotlin specific mimic of extension used for Java BigInteger.
     * */
    @Test
    fun testDec() {
        Combinator.numberGenerator(-64..64) { x ->
            var xBi2 = bigIntOf(x)
            val xJbi = JavaBigInteger(x)

            Debugger.assertContentEquals(
                x,
                xJbi,
                xBi2,
                xJbi.subtract(JavaBigInteger.ONE),
                --xBi2, // <- Kotlin specific
            )
        }
    }

    /**
     * Validates that the minuend set to 0 is validated without a hiccup.
     * */
    @Test
    fun testFirstIfZero() {
        val y = BufMgr.bin(64).apply{ SecureRandom.read(this) }.toByteArray()
        val yBi2 = bigIntOf(y)
        val yJbi = JavaBigInteger(y)

        val rBi2 = BigInt.zero.subtract(yBi2)
        val rJbi = JavaBigInteger.ZERO.subtract(yJbi)

        assertContentEquals(rBi2.toByteArray(), yBi2.negate().toByteArray())
        assertContentEquals(rBi2.toByteArray(), rJbi.toByteArray())
    }

    /**
     * Validates that the subtrahend set to 0 is validated without a hiccup.
     * */
    @Test
    fun testSecondIfZero() {
        val x = BufMgr.bin(64).apply{ SecureRandom.read(this) }.toByteArray()
        val xBi2 = bigIntOf(x)
        val xJbi = JavaBigInteger(x)

        val rBi2 = xBi2.subtract(BigInt.zero)
        val rJbi = xJbi.subtract(JavaBigInteger.ZERO)

        assertSame(rBi2, xBi2)
        assertContentEquals(rBi2.toByteArray(), rJbi.toByteArray())
    }
}