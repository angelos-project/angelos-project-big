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

class NotTest {

    @Test
    fun testNot() = withLogic {
        val xBi2 = BigInt.createRandomBigInt(256)
        val notX = xBi2.not()

        assertEquals(xBi2.bitLength, notX.bitLength)
        assertEquals(xBi2.sigNum, notX.sigNum.negate())

        assertEquals(xBi2, notX.not())
    }

    @Test
    fun testInv() = withLogic {
        val xBi2 = BigInt.createRandomBigInt(256)
        val invX = xBi2.inv()

        assertEquals(xBi2.not(), invX)

        assertEquals(xBi2, invX.inv())
    }
}