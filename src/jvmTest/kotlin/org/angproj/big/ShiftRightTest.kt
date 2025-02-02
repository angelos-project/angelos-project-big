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

import org.angproj.aux.io.binOf
import org.angproj.aux.io.toBinary
import org.angproj.aux.io.toByteArray
import org.angproj.aux.sec.SecureRandom
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertSame
import java.math.BigInteger as JavaBigInteger

class ShiftRightTest {
    /**
     * Generally fuzzes and validates that "public fun BigInt.shiftRight(n: Int): BigInt" works
     * under all normal conditions.
     * */
    @Test
    fun testShiftRight() {
        Combinator.numberGenerator(-64..64) { x ->
            val xBi2 = bigIntOf(x.toBinary())
            val xJbi = JavaBigInteger(x)
            (-64 until 64).forEach {
                val s = it

                Debugger.assertContentEquals(
                    x, s,
                    xJbi,
                    xBi2,
                    xJbi.shiftRight(s),
                    xBi2.shiftRight(s) // <- Emulation
                )
            }
        }
    }

    /**
     * Kotlin specific mimic of extension used for Java BigInteger.
     * */
    @Test
    fun testShr() {
        Combinator.numberGenerator(-64..64) { x ->
            val xBi2 = bigIntOf(x.toBinary())
            val xJbi = JavaBigInteger(x)
            (-64 until 64).forEach {
                val s = it

                Debugger.assertContentEquals(
                    x, s,
                    xJbi,
                    xBi2,
                    xJbi.shiftRight(s),
                    xBi2 shr s // <- Kotlin specific
                )
            }
        }
    }

    /**
     * Validates that position set to 0 is validated without a hiccup.
     * */
    @Test
    fun testPosIfZero() {
        val x = binOf(64).apply{ SecureRandom.read(this) }
        val xBi2 = bigIntOf(x)
        val xJbi = JavaBigInteger(x.toByteArray())

        assertSame(xBi2.shiftRight(0), xBi2)
        assertContentEquals(
            xBi2.shiftRight(0).toByteArray(),
            xJbi.shiftRight(0).toByteArray()
        )
    }

    /**
     * Validates that magnitude set to 0 is validated without a hiccup.
     * */
    @Test
    fun testMagnitudeIfZero() {
        assertSame(BigInt.zero.shiftRight(53), BigInt.zero)
        assertContentEquals(
            BigInt.zero.shiftRight(53).toByteArray(),
            JavaBigInteger.ZERO.shiftRight(53).toByteArray()
        )
    }
}