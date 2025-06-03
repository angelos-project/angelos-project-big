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

class ShiftLeftTest {

    @Test
    fun testShiftLeft() {
        val number = BigInt.createRandomBigInt(256)

        // Validate that shiftLeft works
        assertEquals(number.shiftLeft(1), number shl 1)
        assertEquals(number.shiftLeft(0), number)
        assertEquals(number.shiftLeft(-1), number.shiftRight(1))
    }

    /**
     * Kotlin specific mimic of extension used for Java BigInteger.
     * */
    @Test
    fun testShl() {
        val number = BigInt.createRandomBigInt(256)

        // Validate that shl works
        assertEquals(number shl 1, number.shiftLeft(1))
        assertEquals(number shl 0, number)
        assertEquals(number shl -1, number.shiftRight(1))

        // Validate that shl works with SecureRandom
        val secureNumber = BigInt.createRandomBigInt(256)
        assertEquals(secureNumber shl 1, secureNumber.shiftLeft(1))
    }

    /**
     * Validates that position set to 0 is validated without a hiccup.
     * */
    @Test
    fun testPosIfZero() {
        val number = BigInt.createRandomBigInt(256)

        assertSame(number, number.shiftLeft(0))
        assertContentEquals(number.toByteArray(), number.shiftLeft(0).toByteArray())
    }

    /**
     * Validates that magnitude set to 0 is validated without a hiccup.
     * */
    @Test
    fun testMagnitudeIfZero() {
        assertSame(BigInt.zero, BigInt.zero.shiftLeft(53))
        assertContentEquals(BigInt.zero.toByteArray(),BigInt.zero.shiftLeft(53).toByteArray())
    }
}