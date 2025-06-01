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
import org.angproj.aux.io.toByteArray
import org.angproj.aux.mem.BufMgr
import org.angproj.aux.num.bigIntOf
import org.angproj.aux.sec.SecureRandom
import org.angproj.aux.util.floorMod
import org.mockito.Mockito
import kotlin.math.absoluteValue
import kotlin.test.*
import java.math.BigInteger as JavaBigInteger

class TestBitTest {

    fun bitGenBi(bitLen: Int): BigInt = when(bitLen < 0) {
        true -> BigInt.createRandomInRange(BigInt.minusOne.shiftLeft(bitLen), BigInt.minusOne.shiftLeft(bitLen-1))
        else -> BigInt.createRandomInRange(BigInt.one.shiftLeft(bitLen-1), BigInt.one.shiftLeft(bitLen))
    }

    /**
     * Generally fuzzes and validates that "public fun BigInt.testBit(pos: Int): Boolean" works
     * under all normal conditions.
     * */
    @Test
    fun testTestBit() = repeat(1) {
        val first = bitGenBi(SecureRandom.readInt().mod(64*8))
        val second = SecureRandom.readInt().mod(64*8).absoluteValue

        val mockFirst = Mockito.mock(BigInt::class.java)
        Mockito.`when`(mockFirst.sigNum).thenReturn(first.sigNum)
        Mockito.`when`(mockFirst.mag).thenReturn(first.mag)

        try {
            val r1 = mockFirst.testBit(second)
            val r2 = JavaBigInteger(first.toByteArray()).testBit(second)

            assertEquals(r1, r2)
        } catch (e: java.lang.AssertionError) {
            throw e
        }
    }

    /**
     * Validates that a position beyond the magnitude is properly handled with modulus.
     * */
    @Test
    fun testPosBeyondMag() = withLogic {
        val x = BufMgr.bin(13).apply{ SecureRandom.read(this) }
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
    fun testNegPos(): Unit = withLogic {
        val x = BufMgr.bin(23).apply{ SecureRandom.read(this) }
        val xBi2 = bigIntOf(x)
        val xJbi = JavaBigInteger(x.toByteArray())

        assertFailsWith<BigMathException> { xBi2.testBit(-100) }
        assertFailsWith<ArithmeticException> { xJbi.testBit(-100) }
    }
}