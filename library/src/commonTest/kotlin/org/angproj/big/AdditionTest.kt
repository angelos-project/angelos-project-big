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
import kotlin.test.assertTrue

class AdditionTest {

    @Test
    fun testAdd() = withLogic {
        val value1 = BigInt.createRandomBigInt(192)
        val value2 = BigInt.createRandomBigInt(193)

        val result = value1.add(value2)
        assertEquals(result - value1, value2)
        assertEquals(result - value2, value1)
    }

    /**
     * Kotlin specific mimic of extension used for Java BigInteger.
     * */
    @Test
    fun testPlus() = withLogic {
        val value1 = BigInt.createRandomBigInt(192)
        val value2 = BigInt.createRandomBigInt(193)

        val result = value1 + value2
        assertEquals(result - value1, value2)
        assertEquals(result - value2, value1)
    }

    /**
     * Kotlin specific mimic of extension used for Java BigInteger.
     * */
    @Test
    fun testInc() = withLogic {
        val value = BigInt.createRandomBigInt(192)

        val result = value.inc()
        assertEquals(result - value, BigInt.one)
        assertTrue { result > value }
    }

    /**
     * Validates that zero + value returns the value, and is the same using Java BigInteger.
     * */
    @Test
    fun testZeroWithValue() = withLogic {
        val number = BigInt.createRandomBigInt(192)

        assertSame(number, BigInt.zero.add(number))
        assertContentEquals(number.toByteArray(), BigInt.zero.add(number).toByteArray())
    }

    /**
     * Validates that value + zero returns the value, and is the same using Java BigInteger.
     * */
    @Test
    fun testValueWithZero() = withLogic {
        val number = BigInt.createRandomBigInt(192)

        assertSame(number, number.add(BigInt.zero))
        assertContentEquals(number.toByteArray(), number.add(BigInt.zero).toByteArray())
    }
}