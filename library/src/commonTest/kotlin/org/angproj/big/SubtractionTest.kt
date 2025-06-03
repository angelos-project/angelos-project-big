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

import kotlin.test.*

class SubtractionTest {

    @Test
    fun testSubtract() {
        val value1 = BigInt.createRandomBigInt(192)
        val value2 = BigInt.createRandomBigInt(193)

        val result = value1.subtract(value2)
        assertEquals(result + value2, value1)
        assertEquals(value1 - value2, result)
        assertTrue { result < value1 }
    }

    /**
     * Kotlin specific mimic of extension used for Java BigInteger.
     * */
    @Test
    fun testMinus() {
        val value1 = BigInt.createRandomBigInt(192)
        val value2 = BigInt.createRandomBigInt(193)

        val result = value1 - value2
        assertEquals(result + value2, value1)
        assertEquals(value1.subtract(value2), result)
        assertTrue { result < value1 }
    }

    /**
     * Kotlin specific mimic of extension used for Java BigInteger.
     * */
    @Test
    fun testDec() {
        val value = BigInt.createRandomBigInt(192)

        val result = value.dec()
        assertEquals(result + BigInt.one, value)
        assertTrue { result < value }
    }

    /**
     * Validates that the minuend set to 0 is validated without a hiccup.
     * */
    @Test
    fun testFirstIfZero() {
        val number = BigInt.createRandomBigInt(192)

        assertContentEquals(number.negate().toByteArray(), BigInt.zero.subtract(number).toByteArray())
    }

    /**
     * Validates that the subtrahend set to 0 is validated without a hiccup.
     * */
    @Test
    fun testSecondIfZero() {
        val number = BigInt.createRandomBigInt(192)

        assertSame(number, number.subtract(BigInt.zero))
        assertContentEquals(number.toByteArray(), number.subtract(BigInt.zero).toByteArray())
    }
}