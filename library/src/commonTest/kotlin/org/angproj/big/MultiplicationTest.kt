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

class MultiplicationTest {

    @Test
    fun testMultiply() = withLogic {
        val number = BigInt.createRandomBigInt(192)

        assertEquals(number + number, number.multiply(BigInt.two))
    }

    /**
     * Kotlin specific mimic of extension used for Java BigInteger.
     * */
    @Test
    fun testTimes() = withLogic {
        val number = BigInt.createRandomBigInt(192)

        assertEquals(number + number, number * BigInt.two)
    }

    /**
     * Validates that first factor set to 0 is validated without a hiccup.
     * */
    @Test
    fun testFirstIfZero() = withLogic {
        val number = BigInt.createRandomBigInt(192)

        assertSame(BigInt.zero, BigInt.zero.multiply(number))
        assertContentEquals(BigInt.zero.toByteArray(), BigInt.zero.multiply(number).toByteArray())
    }

    /**
     * Validates that second factor set to 0 is validated without a hiccup.
     * */
    @Test
    fun testSecondIfZero() = withLogic {
        val number = BigInt.createRandomBigInt(192)

        assertSame(BigInt.zero, number.multiply(BigInt.zero))
        assertContentEquals(BigInt.zero.toByteArray(), number.multiply(BigInt.zero).toByteArray())
    }
}