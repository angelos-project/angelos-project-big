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
import org.angproj.aux.util.floorMod
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue
import java.math.BigInteger as JavaBigInteger


class FlipBitTest {
    /**
     * Generally fuzzes and validates that "public fun BigInt.flipBit(pos: Int): BigInt" works
     * under all normal conditions.
     * */
    @Test
    fun testFlipBit() {
        Combinator.numberGenerator(-64..64) { x ->
            val xBi2 = bigIntOf(x)
            val xJbi = JavaBigInteger(x)
            if(xBi2.sigNum.isZero()) return@numberGenerator
            (0..64).forEach {
                val pos = it.floorMod(xBi2.bitLength)

                Debugger.assertContentEquals(
                    x, pos,
                    xJbi,
                    xBi2,
                    xJbi.flipBit(pos),
                    xBi2.flipBit(pos) // <- Emulation
                )
            }
        }
    }

    /**
     * Validates that a position beyond the magnitude is properly handled with modulus.
     * */
    @Test
    fun testPosBeyondMag() {
        val x = BufMgr.bin(13).apply{ SecureRandom.read(this) }.toByteArray()
        val xBi2 = bigIntOf(x)
        val xJbi = JavaBigInteger(x)

        assertTrue(xBi2.mag.size * 32 < 200)
        assertContentEquals(
            xBi2.flipBit(200).toByteArray(),
            xJbi.flipBit(200).toByteArray()
        )
    }

    /**
     * Validates that a BigMathException is thrown if a negative position is given, and mimics Java.
     * */
    @Test
    fun testNegPos() {
        val x = BufMgr.bin(23).apply{ SecureRandom.read(this) }.toByteArray()
        val xBi2 = bigIntOf(x)
        val xJbi = JavaBigInteger(x)

        assertFailsWith<BigMathException> { xBi2.flipBit(-100) }
        assertFailsWith<ArithmeticException> { xJbi.flipBit(-100) }
    }
}