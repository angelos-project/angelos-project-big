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

class ShiftRightTest {
    /**
     * Generally fuzzes and validates that "public fun BigInt.shiftRight(n: Int): BigInt" works
     * under all normal conditions.
     * */
    @Test
    fun testShiftRight() = withLogic {
        val xBi2 = BigInt.createRandomBigInt(256)

        // Validate that shiftRight works
        assertEquals(xBi2.shiftRight(1), xBi2 shr 1)
        assertEquals(xBi2.shiftRight(0), xBi2)
        assertEquals(xBi2.shiftRight(-1), xBi2.shiftLeft(1))
    }

    /**
     * Kotlin specific mimic of extension used for Java BigInteger.
     * */
    @Test
    fun testShr() = withLogic {
    }

    /**
     * Validates that position set to 0 is validated without a hiccup.
     * */
    @Test
    fun testPosIfZero() = withLogic {
    }

    /**
     * Validates that magnitude set to 0 is validated without a hiccup.
     * */
    @Test
    fun testMagnitudeIfZero() = withLogic {
    }
}