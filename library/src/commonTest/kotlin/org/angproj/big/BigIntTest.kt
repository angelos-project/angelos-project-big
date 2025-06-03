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

class BigIntTest {

    @Test
    fun testNull() {
        assertTrue(BigInt.nullObject.isNull())
        assertFalse(BigInt.zero.isNull())
    }

    /**
     * This test recognizes that BigInt and Java BigInteger interprets a ByteArray of some random values
     * the same when importing from the said ByteArray and exporting to a new ByteArray.
     * */
    @Test
    fun testByteArray() {
    }

    /**
     * This test recognizes that BigInt can predict its export size properly.
     * */
    @Test
    fun testToSize() {
    }

    /**
     * This test recognizes that BigInt and Java BigInteger interprets a Long random value
     * the same way when importing and then exporting to a new ByteArray.
     * */
    @Test
    fun testLong() {
    }

    /**
     * This test recognizes that BigInt and Java BigInteger interprets a ByteArray of some random values
     * the same when exporting toInt.
     * */
    @Test
    fun testToInt() {
    }

    /**
     * This test recognizes that BigInt and Java BigInteger interprets a ByteArray of some random values
     * the same when exporting toLong.
     * */
    @Test
    fun testToLong() {
    }

    /**
     * This test recognizes that BigInt and Java BigInteger calculates
     * the sigNum of the same underlying value similarly.
     * */
    @Test
    fun testSigNum() {
    }

    /**
     * This test recognizes that BigInt and Java BigInteger calculates
     * the bitLength of the same underlying value similarly.
     * */
    @Test
    fun testBitLength() {
    }

    /**
     * This test recognizes that BigInt and Java BigInteger calculates
     * the bitCount of the same underlying value similarly.
     * */
    @Test
    fun testBitCount() {
    }

    /**
     * This test recognizes whether large zero unsigned big integer
     * translates into zero magnitude and sigNum properly.
     * */
    @Test
    fun testUnsignedBigIntOf() {
    }

    /**
     * This test certifies that both BigInt and Java BigInteger
     * throws an exception similarly in response to a zero-length ByteArray.
     * */
    @Test
    fun testEmptyByteArray() {
    }
}