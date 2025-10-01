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
    fun testEqualSameRef() {
        val number = BigInt.createEntropyBigInt(256)
        assertTrue(number.equals(number))
    }

    @Test
    fun testWrongObjType() {
        val number = BigInt.createEntropyBigInt(256)
        val stub = DummyStub()
        assertFalse(number.equals(stub))
    }

    @Test
    fun testDiffSigNum() {
        val number = BigInt.createEntropyBigInt(256)
        assertFalse(number.equals(number.negate()))
    }

    @Test
    fun testWrongMagLength() {
        val number = BigInt.createEntropyBigInt(256)
        assertFalse(number.equals(number.shiftRight(32)))
    }

    @Test
    fun testCopyDiffObj() {
        val number = BigInt.createEntropyBigInt(256)
        val x = number.toByteArray()
        assertTrue(number.equals(bigIntOf(x)))
    }

    @Test
    fun testCopyDiffValue() {
        val number = BigInt.createEntropyBigInt(256)
        assertFalse(number.equals(number.add(BigInt.one)))
    }
}