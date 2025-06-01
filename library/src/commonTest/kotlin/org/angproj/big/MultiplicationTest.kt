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

class MultiplicationTest {

    @Test
    fun testMultiply() = withLogic {
        val xBi2 = BigInt.createRandomBigInt(256)
        val yBi2 = BigInt.createRandomBigInt(256)

        // Validate that multiplication works
        assertEquals(xBi2.multiply(yBi2), xBi2 * yBi2)

        // Validate that multiplication with zero returns zero
        assertEquals(xBi2.multiply(BigInt.zero), BigInt.zero)
        assertEquals(BigInt.zero.multiply(yBi2), BigInt.zero)

        // Validate that multiplication with one returns the original value
        assertEquals(xBi2.multiply(BigInt.one), xBi2)
        assertEquals(BigInt.one.multiply(yBi2), yBi2)
    }
}