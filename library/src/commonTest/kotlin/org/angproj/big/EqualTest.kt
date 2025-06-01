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
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class DummyStub

class EqualTest {
    @Test
    fun testEqualSameRef() = withLogic {
        val xBi2 = BigInt.createRandomBigInt(256)
        assertTrue(xBi2.equals(xBi2))
    }

    @Test
    fun testWrongObjType() = withLogic {
        val xBi2 = BigInt.createRandomBigInt(256)
        val stub = DummyStub()
        assertFalse(xBi2.equals(stub))
    }

    @Test
    fun testDiffSigNum() = withLogic {
        val xBi2 = BigInt.createRandomBigInt(256)
        assertFalse(xBi2.equals(xBi2.negate()))
    }

    @Test
    fun testWrongMagLength() = withLogic {
        val xBi2 = BigInt.createRandomBigInt(256)
        assertFalse(xBi2.equals(xBi2.shiftRight(32)))
    }

    @Test
    fun testCopyDiffObj() = withLogic {
        val xBi2 = BigInt.createRandomBigInt(256)
        val x = xBi2.toByteArray()
        assertTrue(xBi2.equals(bigIntOf(x)))
    }

    @Test
    fun testCopyDiffValue() = withLogic {
        val xBi2 = BigInt.createRandomBigInt(256)
        assertFalse(xBi2.equals(xBi2.add(BigInt.one)))
    }
}