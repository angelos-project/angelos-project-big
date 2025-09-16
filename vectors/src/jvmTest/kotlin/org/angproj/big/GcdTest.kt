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

import java.math.BigInteger
import kotlin.test.*

internal class GcdTest {
    // Assuming this is the method to test
    private fun gcd(a: BigInteger, b: BigInteger?): BigInteger {
        // Implementation of GCD using BigInteger's built-in gcd method
        // This is a placeholder - in practice, you might have your own implementation
        return a.gcd(b)
    }

    @Test
    fun testGcdPositiveNumbers() {
        val a = BigInteger("48")
        val b = BigInteger("18")
        val expected = BigInteger("6")
        assertEquals(expected, gcd(a, b))
    }

    @Test
    fun testGcdSameNumbers() {
        val a = BigInteger("42")
        val b = BigInteger("42")
        val expected = BigInteger("42")
        assertEquals(expected, gcd(a, b))
    }

    @Test
    fun testGcdOneAndNumber() {
        val a = BigInteger("1")
        val b = BigInteger("17")
        val expected = BigInteger("1")
        assertEquals(expected, gcd(a, b))
        assertEquals(expected, gcd(b, a)) // Test commutative property
    }

    @Test
    fun testGcdNegativeNumbers() {
        val a = BigInteger("-48")
        val b = BigInteger("18")
        val expected = BigInteger("6")
        assertEquals(expected, gcd(a, b))
    }

    @Test
    fun testGcdBothNegativeNumbers() {
        val a = BigInteger("-48")
        val b = BigInteger("-18")
        val expected = BigInteger("6")
        assertEquals(expected, gcd(a, b))
    }

    @Test
    fun testGcdZeroAndNumber() {
        val a = BigInteger.ZERO
        val b = BigInteger("15")
        val expected = BigInteger("15")
        assertEquals(expected, gcd(a, b))
        assertEquals(expected, gcd(b, a))
    }

    @Test
    fun testGcdBothZero() {
        val a = BigInteger.ZERO
        val b = BigInteger.ZERO
        assertFailsWith<ArithmeticException> { gcd(a, b) }
    }

    @Test
    fun testGcdLargeNumbers() {
        val a = BigInteger("12345678901234567890")
        val b = BigInteger("98765432109876543210")
        val expected = BigInteger("90")
        assertEquals(expected, gcd(a, b))
    }


    @Test
    fun testGcdPrimeNumbers() {
        val a = BigInteger("17")
        val b = BigInteger("13")
        val expected = BigInteger("1")
        assertEquals(expected, gcd(a, b))
    }
}