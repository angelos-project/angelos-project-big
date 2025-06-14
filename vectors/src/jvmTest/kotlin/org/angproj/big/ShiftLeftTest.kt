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

class ShiftLeftTest {
    /**
     * Generally fuzzes and validates that "public fun BigInt.shiftLeft(n: Int): BigInt" works
     * under all normal conditions.
     * */
    @Test
    fun testShiftLeft() {
        Combinator.numberGenerator(-64..64) { x ->
            val xBi2 = bigIntOf(x)
            val xJbi = JavaBigInteger(x)
            (-64 until 64).forEach {
                val s = it

                Debugger.assertContentEquals(
                    x, s,
                    xJbi,
                    xBi2,
                    xJbi.shiftLeft(s),
                    xBi2.shiftLeft(s) // <- Emulation
                )
            }
        }
    }

    /**
     * Kotlin specific mimic of extension used for Java BigInteger.
     * */
    @Test
    fun testShl() {
        Combinator.numberGenerator(-64..64) { x ->
            val xBi2 = bigIntOf(x)
            val xJbi = JavaBigInteger(x)
            (-64 until 64).forEach {
                val s = it

                Debugger.assertContentEquals(
                    x, s,
                    xJbi,
                    xBi2,
                    xJbi.shiftLeft(s),
                    xBi2 shl s // <- Kotlin specific
                )
            }
        }
    }

    /**
     * Validates that position set to 0 is validated without a hiccup.
     * */
    @Test
    fun testPosIfZero() {
        val x = ByteArray(64).also { it.securelyRandomize() }
        val xBi2 = bigIntOf(x)
        val xJbi = JavaBigInteger(x)

        assertSame(xBi2.shiftLeft(0), xBi2)
        assertContentEquals(
            xBi2.shiftLeft(0).toByteArray(),
            xJbi.shiftLeft(0).toByteArray()
        )
    }

    /**
     * Validates that magnitude set to 0 is validated without a hiccup.
     * */
    @Test
    fun testMagnitudeIfZero() {
        assertSame(BigInt.zero.shiftLeft(53), BigInt.zero)
        assertContentEquals(
            BigInt.zero.shiftLeft(53).toByteArray(),
            JavaBigInteger.ZERO.shiftLeft(53).toByteArray()
        )
    }
}