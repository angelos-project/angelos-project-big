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

class ShiftLeftTest {

    @Test
    fun testShiftLeft() = withLogic {
        val xBi2 = BigInt.createRandomBigInt(256)

        // Validate that shift left works
        assertEquals(xBi2.shiftLeft(1), xBi2 shl 1)
        assertEquals(xBi2.shiftLeft(0), xBi2 shl 0)
        assertEquals(xBi2.shiftLeft(10), xBi2 shl 10)

        // Validate that shift left with zero returns the original value
        assertEquals(xBi2.shiftLeft(0), xBi2)
        assertEquals(BigInt.zero.shiftLeft(10), BigInt.zero)
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