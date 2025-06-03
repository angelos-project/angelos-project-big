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
import kotlin.test.assertFailsWith
import kotlin.test.assertSame
import java.math.BigInteger as JavaBigInteger

class PowTest {
    /**
     * Generally fuzzes and validates that "public fun BigInt.pow(exponent: Int): BigInt" works
     * under all normal conditions.
     * */
    @Test
    fun testPow() {
        Combinator.numberGenerator(-64..64) { x ->
            val xBi2 = bigIntOf(x)
            val xJbi = JavaBigInteger(x)
            (0..8).forEach { y ->

                Debugger.assertContentEquals(
                    x,
                    xJbi,
                    xBi2,
                    xJbi.pow(y),
                    xBi2.pow(y)  // <- Emulation
                )
            }
        }
    }

    /**
     * Validates that BigMathException is thrown if the exponent is negative, likewise as Java BigInteger.
     * */
    @Test
    fun testExponentIfNegative() {
        val x = BufMgr.bin(64).apply{ SecureRandom.read(this) }.toByteArray()
        val xBi2 = bigIntOf(x)
        val xJbi = JavaBigInteger(x)

        assertFailsWith<BigMathException> { xBi2.pow(-1) }
        assertFailsWith<ArithmeticException> { xJbi.pow(-1) }
    }

    /**
     * Validates that exponent set as 0 is validated without a hiccup.
     * */
    @Test
    fun testExponentIfZero() {
        val x = BufMgr.bin(64).apply{ SecureRandom.read(this) }.toByteArray()
        val xBi2 = bigIntOf(x)
        val xJbi = JavaBigInteger(x)

        val rBi2 = xBi2.pow(0)
        val rJbi = xJbi.pow(0)

        assertSame(rBi2, BigInt.one)
        assertContentEquals(rBi2.toByteArray(), rJbi.toByteArray())
    }

    /**
     * Validates that exponent set as 1 is validated without a hiccup.
     * */
    @Test
    fun testExponentIfOne() {
        val x = BufMgr.bin(64).apply{ SecureRandom.read(this) }.toByteArray()
        val xBi2 = bigIntOf(x)
        val xJbi = JavaBigInteger(x)

        val rBi2 = xBi2.pow(1)
        val rJbi = xJbi.pow(1)

        assertSame(rBi2, xBi2)
        assertContentEquals(rBi2.toByteArray(), rJbi.toByteArray())
    }

    /**
     * Validates that coefficient set as 0 is validated without a hiccup.
     * */
    @Test
    fun testCoefficientIfZero() {
        val rBi2 = BigInt.zero.pow(7)
        val rJbi = JavaBigInteger.ZERO.pow(7)

        assertSame(rBi2, BigInt.zero)
        assertContentEquals(rBi2.toByteArray(), rJbi.toByteArray())
    }
}