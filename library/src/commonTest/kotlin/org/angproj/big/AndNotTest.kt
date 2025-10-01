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

class AndNotTest {

    @Test
    fun testAndNot() {
        val large = BigInt.one.shiftLeft(256).dec()
        val small = BigInt.one.shiftLeft(128).dec()

        // Validate that AND NOT operation works correctly
        assertEquals(large andNot small, large andNot small)
        assertEquals(small andNot large, small andNot large)

        // Validate that AND NOT with zero returns the original value
        assertEquals(large andNot BigInt.zero, large)
        assertEquals(BigInt.zero andNot small, BigInt.zero)

        // Validate that AND NOT with itself returns zero
        assertEquals(large andNot large, BigInt.zero)
        assertEquals(small andNot small, BigInt.zero)

        // Validate that AND NOT with random values works
        val random1 = BigInt.createEntropyBigInt(256)
        val random2 = BigInt.createEntropyBigInt(256)
        val result = random1 andNot random2
        assertContentEquals(result.toByteArray(), (random1 andNot random2).toByteArray())
    }

    @Test
    fun testAndNotWithNotIncludingAndUsingRandom() {
        // Validate that AND NOT with random values works
        val random1 = BigInt.createEntropyBigInt(256)
        val random2 = BigInt.createEntropyBigInt(256)
        val result = random1 andNot random2.not()
        assertContentEquals(result.toByteArray(), (random1 andNot random2.not()).toByteArray())
    }
}