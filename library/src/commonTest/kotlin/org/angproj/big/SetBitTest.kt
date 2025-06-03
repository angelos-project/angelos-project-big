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

class SetBitTest {

    @Test
    fun testSetBit() {
        val number = BigInt.one.shiftLeft(192)

        repeat(191) { i ->
            assertEquals(true, number.setBit(i).testBit(i))
        }
    }

    /**
     * Validates that a position beyond the magnitude is properly handled with modulus.
     * */
    @Test
    fun testPosBeyondMag() {
        val number = BigInt.one.shiftLeft(128)

        assertTrue(number.mag.size * 32 < 200)
        assertEquals(true, number.setBit(200).testBit(200))
    }

    /**
     * Validates that a BigMathException is thrown if a negative position is given, and mimics Java.
     * */
    @Test
    fun testNegPos(): Unit {
        val number = BigInt.one.shiftLeft(192)

        assertFailsWith<BigMathException> { number.setBit(-100) }
    }
}