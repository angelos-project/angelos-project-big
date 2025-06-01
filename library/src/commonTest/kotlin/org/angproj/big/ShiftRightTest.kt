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
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertSame

class ShiftRightTest {

    @Test
    fun testShiftRight() = withLogic {
        val number = BigInt.createRandomBigInt(256)

        // Validate that shiftRight works
        assertEquals(number.shiftRight(1), number shr 1)
        assertEquals(number.shiftRight(0), number)
        assertEquals(number.shiftRight(-1), number.shiftLeft(1))
    }

    /**
     * Kotlin specific mimic of extension used for Java BigInteger.
     * */
    @Test
    fun testShr() = withLogic {
        val number = BigInt.createRandomBigInt(256)

        // Validate that shr works
        assertEquals(number shr 1, number.shiftRight(1))
        assertEquals(number shr 0, number)
        assertEquals(number shr -1, number.shiftLeft(1))

        // Validate that shr works with SecureRandom
        val secureNumber = BigInt.createRandomBigInt(256)
        assertEquals(secureNumber shr 1, secureNumber.shiftRight(1))
    }

    /**
     * Validates that position set to 0 is validated without a hiccup.
     * */
    @Test
    fun testPosIfZero() = withLogic {
        val number = BigInt.createRandomBigInt(256)

        assertSame(number, number.shiftRight(0))
        assertContentEquals(number.toByteArray(), number.shiftRight(0).toByteArray())
    }

    /**
     * Validates that magnitude set to 0 is validated without a hiccup.
     * */
    @Test
    fun testMagnitudeIfZero() = withLogic {
        assertSame(BigInt.zero, BigInt.zero.shiftRight(53))
        assertContentEquals(BigInt.zero.toByteArray(), BigInt.zero.shiftRight(53).toByteArray())
    }
}