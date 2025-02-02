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
import org.angproj.aux.util.floorMod
import java.lang.ArithmeticException
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue
import java.math.BigInteger as JavaBigInteger

class TestBitTest {
    /**
     * Generally fuzzes and validates that "public fun BigInt.testBit(pos: Int): Boolean" works
     * under all normal conditions.
     * */
    @Test
    fun testTestBit() {
        Combinator.numberGenerator(-64..64) { x ->
            val xBi2 = bigIntOf(x.toBinary())
            val xJbi = JavaBigInteger(x)
            if(xBi2.sigNum.isZero()) return@numberGenerator
            (0..64).forEach {
                val pos = it.floorMod(xBi2.bitLength)

                Debugger.assertEquals(
                    x, pos,
                    xJbi,
                    xBi2,
                    xJbi.testBit(pos),
                    xBi2.testBit(pos) // <- Emulation
                )
            }
        }
    }

    /**
     * Validates that a position beyond the magnitude is properly handled with modulus.
     * */
    @Test
    fun testPosBeyondMag() {
        val x = binOf(13).apply{ SecureRandom.read(this) }
        val xBi2 = bigIntOf(x)
        val xJbi = JavaBigInteger(x.toByteArray())

        assertTrue(xBi2.mag.size * 32 < 200)
        assertEquals(
            xBi2.testBit(200),
            xJbi.testBit(200)
        )
    }

    /**
     * Validates that a BigMathException is thrown if a negative position is given, and mimics Java.
     * */
    @Test
    fun testNegPos() {
        val x = binOf(23).apply{ SecureRandom.read(this) }
        val xBi2 = bigIntOf(x)
        val xJbi = JavaBigInteger(x.toByteArray())

        assertFailsWith<BigMathException> { xBi2.testBit(-100) }
        assertFailsWith<ArithmeticException> { xJbi.testBit(-100) }
    }
}