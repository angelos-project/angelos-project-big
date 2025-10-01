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

class XorTest {

    @Test
    fun testXor() {
        val large = BigInt.one.shiftLeft(256).dec()
        val small = BigInt.one.shiftLeft(128).dec()

        // Validate that XOR operation works correctly
        assertEquals(large xor small, large xor small)
        assertEquals(small xor large, large xor small)

        // Validate that XOR with zero returns the original value
        assertEquals(large xor BigInt.zero, large)
        assertEquals(BigInt.zero xor small, small)

        // Validate that XOR with one returns the original value
        assertEquals(large xor BigInt.one, large xor BigInt.one)
        assertEquals(BigInt.one xor small, small xor BigInt.one)

        // Validate that XOR with itself returns zero
        assertEquals(large xor large, BigInt.zero)
        assertEquals(small xor small, BigInt.zero)

        // Validate that XOR with random values works
        val random1 = BigInt.createEntropyBigInt(256)
        val random2 = BigInt.createEntropyBigInt(256)
        val result = random1 xor random2
        assertContentEquals(result.toByteArray(), (random1 xor random2).toByteArray())
    }
    /**
     * Kotlin specific mimic of extension used for Java BigInteger.
     * */
    @Test
    fun testXorInfix() {
        val large = BigInt.one.shiftLeft(256).dec()
        val small = BigInt.one.shiftLeft(128).dec()

        // Validate that infix XOR operation works correctly
        assertEquals(large xor small, large xor small)
        assertEquals(small xor large, large xor small)

        // Validate that infix XOR with zero returns the original value
        assertEquals(large xor BigInt.zero, large)
        assertEquals(BigInt.zero xor small, small)

        // Validate that infix XOR with one returns the original value
        assertEquals(large xor BigInt.one, large xor BigInt.one)
        assertEquals(BigInt.one xor small, small xor BigInt.one)

        // Validate that infix XOR with itself returns zero
        assertEquals(large xor large, BigInt.zero)
        assertEquals(small xor small, BigInt.zero)

        // Validate that infix XOR with random values works
        val random1 = BigInt.createEntropyBigInt(256)
        val random2 = BigInt.createEntropyBigInt(256)
        val result = random1 xor random2
        assertContentEquals(result.toByteArray(), (random1 xor random2).toByteArray())
    }
}