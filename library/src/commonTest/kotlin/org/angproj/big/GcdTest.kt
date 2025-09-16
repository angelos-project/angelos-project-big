/**
 * Copyright (c) 2025 by Kristoffer Paulsson <kristoffer.paulsson@talenten.se>.
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

class GcdTest {

    @Test
    fun testGcdPositiveNumbers() {
        val a = bigIntOf(48)
        val b = bigIntOf(18)
        val expected = bigIntOf(6)
        assertEquals(expected, a.gcd(b))
    }

    @Test
    fun testGcdSameNumbers() {
        val a = bigIntOf(42)
        val b = bigIntOf(42)
        val expected = bigIntOf(42)
        assertEquals(expected, a.gcd(b))
    }

    @Test
    fun testGcdOneAndNumber() {
        val a = bigIntOf(1)
        val b = bigIntOf(17)
        val expected = bigIntOf(1)
        assertEquals(expected, a.gcd(b))
        assertEquals(expected, b.gcd(a)) // Test commutative property
    }

    @Test
    fun testGcdNegativeNumbers() {
        val a = bigIntOf(-48)
        val b = bigIntOf(18)
        val expected = bigIntOf(6)
        assertEquals(expected, a.gcd(b))
    }

    @Test
    fun testGcdBothNegativeNumbers() {
        val a = bigIntOf(-48)
        val b = bigIntOf(-18)
        val expected = bigIntOf(6)
        assertEquals(expected, a.gcd(b))
    }

    @Test
    fun testGcdZeroAndNumber() {
        val a = BigInt.zero
        val b = bigIntOf(15)
        val expected = bigIntOf(15)
        assertEquals(expected, a.gcd(b))
        assertEquals(expected, b.gcd(a))
    }

    @Test
    fun testGcdBothZero() {
        val a = BigInt.zero
        val b = BigInt.zero
        val expected = BigInt.zero
        assertEquals(expected, a.gcd(b))
    }

    @Test
    fun testGcdPrimeNumbers() {
        val a = bigIntOf(17)
        val b = bigIntOf(13)
        val expected = bigIntOf(1)
        assertEquals(expected, a.gcd(b))
    }
}