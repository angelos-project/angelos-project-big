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

class SquareRootTest {

    @Test
    fun testSqrt_zero() {
        val x = BigInt.zero
        assertEquals(BigInt.zero, x.sqrt())
    }

    @Test
    fun testSqrt_one() {
        val x = BigInt.one
        assertEquals(BigInt.one, x.sqrt())
    }

    @Test
    fun testSqrt_perfectSquare() {
        val x = bigIntOf(144)
        assertEquals(bigIntOf(12), x.sqrt())
    }

    @Test
    fun testSqrt_nonPerfectSquare() {
        val x = bigIntOf(20)
        assertEquals(bigIntOf(4), x.sqrt())
    }

    @Test
    fun testSqrtAndRemainder_perfectSquare() {
        val x = bigIntOf(49)
        val (sqrt, rem) = x.sqrtAndRemainder()
        assertEquals(bigIntOf(7), sqrt)
        assertEquals(BigInt.zero, rem)
    }

    @Test
    fun testSqrtAndRemainder_nonPerfectSquare() {
        val x = bigIntOf(50)
        val (sqrt, rem) = x.sqrtAndRemainder()
        assertEquals(bigIntOf(7), sqrt)
        assertEquals(bigIntOf(1), rem)
    }

    /*@OptIn(ExperimentalStdlibApi::class)
    @Test
    fun testSqrt_largeNumber_hex() {
        val x = bigIntOf("16BCC41E9000000000".hexToByteArray()) // 100000000000000000000 in hex
        val expected = bigIntOf("2540BE400".hexToByteArray())    // 10000000000 in hex
        assertEquals(expected, x.sqrt())
    }*/

    @Test
    fun testSqrt_negative_throws() {
        val x = bigIntOf(-1)
        assertFailsWith<BigMathException> { x.sqrt() }
    }
}