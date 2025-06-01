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
import kotlin.test.assertFailsWith
import kotlin.test.assertSame
import kotlin.test.assertContentEquals

class PowTest {

    @Test
    fun testPow() = withLogic {
        val number = BigInt.createRandomBigInt(192)

        assertEquals(number * number, number.pow(2))
    }

    /**
     * Validates that BigMathException is thrown if the exponent is negative, likewise as Java BigInteger.
     * */
    @Test
    fun testExponentIfNegative(): Unit = withLogic {
        val number = BigInt.createRandomBigInt(192)

        assertFailsWith<BigMathException> { number.pow(-1) }
    }

    /**
     * Validates that exponent set as 0 is validated without a hiccup.
     * */
    @Test
    fun testExponentIfZero() = withLogic {
        val number = BigInt.createRandomBigInt(192)

        assertSame(BigInt.one, number.pow(0))
        assertContentEquals(BigInt.one.toByteArray(), number.pow(0).toByteArray())
    }

    /**
     * Validates that exponent set as 1 is validated without a hiccup.
     * */
    @Test
    fun testExponentIfOne() = withLogic {
        val number = BigInt.createRandomBigInt(192)

        assertSame(number, number.pow(1))
        assertContentEquals(number.toByteArray(), number.pow(1).toByteArray())
    }

    /**
     * Validates that coefficient set as 0 is validated without a hiccup.
     * */
    @Test
    fun testCoefficientIfZero() = withLogic {
        val number = BigInt.zero.pow(7)

        assertSame(number, BigInt.zero)
        assertContentEquals(number.toByteArray(), BigInt.zero.toByteArray())
    }
}