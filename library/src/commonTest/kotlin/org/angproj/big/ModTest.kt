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
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class ModTest {

    @Test
    fun testMod() {
        val large = BigInt.createRandomBigInt(256)
        val small = BigInt.createRandomBigInt(128)

        // Ensure that the modulus is less than the dividend
        assertTrue{ large > small }

        // Validate that the modulus is correct
        assertTrue { large.mod(small) < small }
        assertTrue { large.mod(small) >= BigInt.zero }
    }

    /**
     * Validates that BigMathException is thrown if the modulus is zero or negative, likewise as Java BigInteger.
     * */
    @Test
    fun testModulusNotPositive() {
        val large = BigInt.createRandomBigInt(256)

        // Validate that modulus zero throws an exception
        assertFailsWith<BigMathException> {
            large.mod(BigInt.zero)
        }

        // Validate that modulus negative throws an exception
        assertFailsWith<BigMathException> {
            large.mod(BigInt.minusOne)
        }
    }
}