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
import kotlin.test.assertContentEquals

class OrTest {

    @Test
    fun testOr() = withLogic {
        val large = BigInt.one.shiftLeft(256).dec()
        val small = BigInt.one.shiftLeft(128).dec()

        // Validate that OR operation works correctly
        assertEquals(large or small, large or small)
        assertEquals(small or large, large or small)

        // Validate that OR with zero returns the original value
        assertEquals(large or BigInt.zero, large)
        assertEquals(BigInt.zero or small, small)

        // Validate that OR with one returns the original value
        assertEquals(large or BigInt.one, large or BigInt.one)
        assertEquals(BigInt.one or small, small or BigInt.one)

        // Validate that OR with itself returns the original value
        assertEquals(large or large, large)
        assertEquals(small or small, small)

        // Validate that OR with random values works
        val random1 = BigInt.createRandomBigInt(256)
        val random2 = BigInt.createRandomBigInt(256)
        val result = random1 or random2
        assertContentEquals(result.toByteArray(), (random1 or random2).toByteArray())
    }

    /**
     * Kotlin specific mimic of extension used for Java BigInteger.
     * */
    @Test
    fun testOrInfix() = withLogic {
        val large = BigInt.one.shiftLeft(256).dec()
        val small = BigInt.one.shiftLeft(128).dec()

        // Validate that infix OR operation works correctly
        assertEquals(large or small, large or small)
        assertEquals(small or large, large or small)
    }
}