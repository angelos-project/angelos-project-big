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


class AndTest {

    @Test
    fun testAnd() {
        val large = BigInt.one.shiftLeft(256).dec()
        val small = BigInt.one.shiftLeft(128).dec()

        // Validate that AND operation works correctly
        assertEquals(large and small, large and small)
        assertEquals(small and large, large and small)

        // Validate that AND with zero returns zero
        assertEquals(large and BigInt.zero, BigInt.zero)
        assertEquals(BigInt.zero and small, BigInt.zero)

        // Validate that AND with one returns the original value
        assertEquals(large and BigInt.one, large and BigInt.one)
        assertEquals(BigInt.one and small, small and BigInt.one)

        // Validate that AND with itself returns the original value
        assertEquals(large and large, large)
        assertEquals(small and small, small)

        // Validate that AND with random values works
        val random1 = BigInt.createRandomBigInt(256)
        val random2 = BigInt.createRandomBigInt(256)
        val result = random1 and random2
        assertContentEquals(result.toByteArray(), (random1 and random2).toByteArray())
    }

    /**
     * Kotlin specific mimic of extension used for Java BigInteger.
     * */
    @Test
    fun testAndInfix() {
        val large = BigInt.one.shiftLeft(256).dec()
        val small = BigInt.one.shiftLeft(128).dec()

        // Validate that infix AND operation works correctly
        assertEquals(large and small, large and small)
        assertEquals(small and large, large and small)

        // Validate that infix AND with zero returns zero
        assertEquals(large and BigInt.zero, BigInt.zero)
        assertEquals(BigInt.zero and small, BigInt.zero)

        // Validate that infix AND with one returns the original value
        assertEquals(large and BigInt.one, large and BigInt.one)
        assertEquals(BigInt.one and small, small and BigInt.one)

        // Validate that infix AND with itself returns the original value
        assertEquals(large and large, large)
        assertEquals(small and small, small)
    }

    @Test
    fun testAndWithAndNotIncludingNotUsingRandom() {
        val random1 = BigInt.createRandomBigInt(256)
        val random2 = BigInt.createRandomBigInt(256)

        // Validate that AND with AND NOT operation works correctly
        val andResult = random1 and random2
        val andNotResult = andResult andNot random2
        assertContentEquals(andNotResult.toByteArray(), (random1 and random2).andNot(random2).toByteArray())
    }
}