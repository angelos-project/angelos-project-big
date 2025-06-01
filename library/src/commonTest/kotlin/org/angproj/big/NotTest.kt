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

class NotTest {

    @Test
    fun testNot() = withLogic {
        val large = BigInt.one.shiftLeft(256).dec()
        val small = BigInt.one.shiftLeft(128).dec()

        // Validate that NOT operation works correctly
        assertEquals(large.not(), large.not())
        assertEquals(small.not(), small.not())

        // Validate that NOT of itself returns zero
        assertEquals(large.not().not(), large)
        assertEquals(small.not().not(), small)

        // Validate that NOT with random values works
        val random1 = BigInt.createRandomBigInt(256)
        val result = random1.not()
        assertContentEquals(result.toByteArray(), random1.not().toByteArray())
    }

    /**
     * Kotlin specific mimic of extension used for Java BigInteger.
     * */
    @Test
    fun testInv() = withLogic {
        val large = BigInt.one.shiftLeft(256).dec()
        val small = BigInt.one.shiftLeft(128).dec()

        // Validate that inv operation works correctly
        assertEquals(large.inv(), large.inv())
        assertEquals(small.inv(), small.inv())

        // Validate that inv of itself returns zero
        assertEquals(large.inv().inv(), large)
        assertEquals(small.inv().inv(), small)

        // Validate that inv with random values works
        val random1 = BigInt.createRandomBigInt(256)
        val result = random1.inv()
        assertContentEquals(result.toByteArray(), random1.inv().toByteArray())
    }

    @Test
    fun testNotWithAndIncludingAndNotUsingRandom() = withLogic {
        val random1 = BigInt.createRandomBigInt(256)
        val random2 = BigInt.createRandomBigInt(256)

        // Validate that NOT with AND operation works correctly
        val andResult = random1 and random2
        val notAndResult = andResult.not()
        assertContentEquals(notAndResult.toByteArray(), (random1 and random2).not().toByteArray())

        // Validate that NOT with AND NOT operation works correctly
        val andNotResult = random1 andNot random2
        val notAndNotResult = andNotResult.not()
        assertContentEquals(notAndNotResult.toByteArray(), (random1 andNot random2).not().toByteArray())
    }
}