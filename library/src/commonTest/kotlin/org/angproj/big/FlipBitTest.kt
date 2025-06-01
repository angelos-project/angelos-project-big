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
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue


class FlipBitTest {

    @Test
    fun testFlipBit() = withLogic {
        val number1 = BigInt.one.shiftLeft(192).dec()
        repeat(191) {
            assertEquals(false, number1.flipBit(it).testBit(it))
        }

        val number2 = BigInt.one.shiftLeft(192)
        repeat(191) {
            assertEquals(true, number2.flipBit(it).testBit(it))
        }
    }

    /**
     * Validates that a position beyond the magnitude is properly handled with modulus.
     * */
    @Test
    fun testPosBeyondMag() = withLogic {
        val number = BigInt.one.shiftLeft(128)

        assertTrue(number.mag.size * 32 < 200)
        assertEquals(true, number.flipBit(200).testBit(200))
    }

    /**
     * Validates that a BigMathException is thrown if a negative position is given, and mimics Java.
     * */
    @Test
    fun testNegPos(): Unit = withLogic {
        val number = BigInt.one.shiftLeft(192)

        assertFailsWith<BigMathException> { number.flipBit(-100) }
    }
}