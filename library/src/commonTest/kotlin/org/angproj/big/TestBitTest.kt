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

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.test.assertFailsWith

class TestBitTest {

    @Test
    fun testTestBit() {
        val number1 = BigInt.one.shiftLeft(192).dec()
        repeat(191) {
            assertEquals(true, number1.testBit(it))
        }

        val number2 = BigInt.one.shiftLeft(192)
        repeat(191) {
            assertEquals(false, number2.testBit(it))
        }
    }

    /**
     * Validates that a position beyond the magnitude is properly handled with modulus.
     * */
    @Test
    fun testPosBeyondMag() = withLogic {
        val number = BigInt.one.shiftLeft(128)

        assertTrue(number.mag.size * 32 < 200)
        assertEquals(false, number.testBit(200))
    }

    /**
     * Validates that a BigMathException is thrown if a negative position is given, and mimics Java.
     * */
    @Test
    fun testNegPos(): Unit = withLogic {
        val number = BigInt.createRandomBigInt(192)

        assertFailsWith<BigMathException> { number.testBit(-100) }
    }
}