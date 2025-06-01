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
import kotlin.test.assertFailsWith

class PowTest {

    @Test
    fun testPow() = withLogic {
    }

    /**
     * Validates that BigMathException is thrown if the exponent is negative, likewise as Java BigInteger.
     * */
    @Test
    fun testExponentIfNegative(): Unit = withLogic {
    }

    /**
     * Validates that exponent set as 0 is validated without a hiccup.
     * */
    @Test
    fun testExponentIfZero() = withLogic {
    }

    /**
     * Validates that exponent set as 1 is validated without a hiccup.
     * */
    @Test
    fun testExponentIfOne() = withLogic {
        val xBi2 = BigInt.createRandomBigInt(256)

        // Validate that exponentiation with one exponent returns the base
        assertEquals(xBi2, xBi2.pow(1))

        // Validate that exponentiation with zero base returns zero
        assertEquals(BigInt.zero, BigInt.zero.pow(1))
    }

    /**
     * Validates that coefficient set as 0 is validated without a hiccup.
     * */
    @Test
    fun testCoefficientIfZero() = withLogic {

    }
}