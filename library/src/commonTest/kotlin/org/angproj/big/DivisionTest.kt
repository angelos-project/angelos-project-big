/**
 * Copyright (c) 2023-2024 by Kristoffer Paulsson <kristoffer.paulsson@talenten.se>.
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

class DivisionTest {

    /**
     * Generally fuzzes and validates that "public fun BigInt.divideAndRemainder(
     *     value: BigInt
     * ): Pair<BigInt, BigInt>" works
     * under all normal conditions. No special cases to test is currently known.
     * */
    @Test
    fun testDivideAndRemainder() = withLogic {
    }

    /**
     * Generally fuzzes and validates that "public fun BigInt.divide(value: BigInt): BigInt" works
     * under all normal conditions. No special cases to test is currently known.
     * */
    @Test
    fun testDivide() = withLogic {
    }

    /**
     * Kotlin specific mimic of extension used for Java BigInteger.
     * */
    @Test
    fun testDiv() = withLogic {
    }

    /**
     * Generally fuzzes and validates that "public fun BigInt.remainder(value: BigInt): BigInt" works
     * under all normal conditions. No special cases to test is currently known.
     * */
    @Test
    fun testRemainder() = withLogic {
    }

    /**
     * Kotlin specific mimic of extension used for Java BigInteger.
     * */
    @Test
    fun testRem() = withLogic {
    }
}