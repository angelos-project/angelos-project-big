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

class SubtractionTest {

    @Test
    fun testSubtract() = withLogic {
        val large = BigInt.createRandomBigInt(256)
        val small = large.dec()

        // Validate that subtraction works
        assertEquals(large.subtract(small), BigInt.one)

        // Validate that subtracting zero returns the original value
        assertEquals(large.subtract(BigInt.zero), large)

        // Validate that subtracting itself returns zero
        assertEquals(large.subtract(large), BigInt.zero)

        // Validate that subtracting a larger number results in a negative value
        assertTrue{large.subtract(large.inc()) < BigInt.zero}
    }

    /**
     * Kotlin specific mimic of extension used for Java BigInteger.
     * */
    @Test
    fun testDec() = withLogic{
        val large = BigInt.createRandomBigInt(256)

        // Validate that decrementing works
        assertEquals(large.dec(), large.subtract(BigInt.one))

        // Validate that decrementing zero returns negative one
        assertEquals(BigInt.zero.dec(), BigInt.minusOne)

        // Validate that decrementing one returns zero
        assertEquals(BigInt.one.dec(), BigInt.zero)
    }

    /**
     * Validates that the minuend set to 0 is validated without a hiccup.
     * */
    @Test
    fun testFirstIfZero() = withLogic {
        // Validate that subtracting from zero returns the negated value
        val xBi2 = BigInt.createRandomBigInt(256)
        assertEquals(BigInt.zero.subtract(xBi2), xBi2.negate())

        // Validate that subtracting zero from zero returns zero
        assertEquals(BigInt.zero.subtract(BigInt.zero), BigInt.zero)
    }

    /**
     * Validates that the subtrahend set to 0 is validated without a hiccup.
     * */
    @Test
    fun testSecondIfZero() = withLogic {
        // Validate that subtracting zero from any value returns the original value
        val xBi2 = BigInt.createRandomBigInt(256)
        assertEquals(xBi2.subtract(BigInt.zero), xBi2)
    }
}