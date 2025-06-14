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

import org.angproj.sec.util.floorMod
import kotlin.test.Test
import kotlin.test.assertTrue

class EssentialTest {
    @Test
    fun testCompareTo() {
        val large = BigInt.createRandomBigInt(256)
        val small = large.dec()

        assertTrue { large > small }
        assertTrue { small < large }
        assertTrue { large >= small }
        assertTrue { small <= large }
        assertTrue { large.compareTo(small) > 0 }
        assertTrue { small.compareTo(large) < 0 }
    }

    /**
     * Generally fuzzes and validates that "public fun BigInt.negate(): BigInt" works
     * under all normal conditions.
     * */
    @Test
    fun testNegate() {
        assertTrue { BigInt.createRandomBigInt(256).abs().negate() < BigInt.zero }
    }

    /**
     * Kotlin specific mimic of extension used for Java BigInteger.
     * */
    @Test
    fun testUnaryMinus() {
        assertTrue { BigInt.createRandomBigInt(256).abs().unaryMinus() < BigInt.zero }
    }

    /**
     * Generally fuzzes and validates that "public fun BigInt.abs(): BigInt" works
     * under all normal conditions.
     * */
    @Test
    fun testAbs() {
        assertTrue { BigInt.createRandomBigInt(256).negate().abs() > BigInt.zero }
    }

    /**
     * Generally fuzzes and validates that "public fun BigInt.min(value: BigInt): BigInt" works
     * under all normal conditions.
     * */
    @Test
    fun testMin() {
        val large = BigInt.createRandomBigInt(256)
        val small = large.dec()

        assertTrue { large.min(small) == small }
        assertTrue { small.min(large) == small }
    }

    /**
     * Generally fuzzes and validates that "public fun BigInt.max(value: BigInt): BigInt" works
     * under all normal conditions.
     * */
    @Test
    fun testMax() {
        val small = BigInt.createRandomBigInt(256)
        val large = small.inc()

        assertTrue { large.max(small) == large }
        assertTrue { small.max(large) == large }
    }
}