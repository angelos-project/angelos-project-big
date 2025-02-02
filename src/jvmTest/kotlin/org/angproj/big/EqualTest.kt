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

import org.angproj.aux.io.binOf
import org.angproj.aux.io.toBinary
import org.angproj.aux.io.toByteArray
import org.angproj.aux.sec.SecureRandom
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.test.assertFalse
import java.math.BigInteger as JavaBigInteger

class DummyStub

/**
 * Confirms that the equals(other: Any?): Boolean method returns the same on Java BigInteger and BigInt.
 * */
class EqualTest {
    @Test
    fun testEqualSameRef() {
        val x = binOf(43).apply{ SecureRandom.read(this) }
        val xBi2 = bigIntOf(x)
        val xJbi = JavaBigInteger(x.toByteArray())

        assertEquals(xBi2.equals(xBi2), xJbi.equals(xJbi))
        assertTrue(xBi2.equals(xBi2))
    }

    @Test
    fun testWrongObjType() {
        val x = binOf(43).apply{ SecureRandom.read(this) }
        val xBi2 = bigIntOf(x)
        val xJbi = JavaBigInteger(x.toByteArray())
        val idiot = DummyStub()

        assertEquals(xBi2.equals(idiot), xJbi.equals(idiot))
        assertFalse(xBi2.equals(idiot))
    }

    @Test
    fun testDiffSigNum() {
        val x = binOf(43).apply{ SecureRandom.read(this) }
        val xBi2 = bigIntOf(x)
        val xJbi = JavaBigInteger(x.toByteArray())

        assertEquals(xBi2.equals(xBi2.negate()), xJbi.equals(xJbi.negate()))
        assertFalse(xBi2.equals(xBi2.negate()))
    }

    @Test
    fun testWrongMagLength() {
        val x = binOf(43).apply{ SecureRandom.read(this) }
        val xBi2 = bigIntOf(x)
        val xJbi = JavaBigInteger(x.toByteArray())

        assertEquals(xBi2.equals(xBi2.shiftRight(32)), xJbi.equals(xJbi.shiftRight(32)))
        assertFalse(xBi2.equals(xBi2.shiftRight(32)))
    }

    @Test
    fun testCopyDiffObj() {
        val x = binOf(43).apply{ SecureRandom.read(this) }.toByteArray()
        val xBi2 = bigIntOf(x.toBinary())
        val xJbi = JavaBigInteger(x)

        assertEquals(xBi2.equals(bigIntOf(x.toBinary())), xJbi.equals(JavaBigInteger(x)))
        assertTrue(xBi2.equals(bigIntOf(x)))
    }

    @Test
    fun testCopyDiffValue() {
        val x = binOf(43).apply{ SecureRandom.read(this) }
        val xBi2 = bigIntOf(x)
        val xJbi = JavaBigInteger(x.toByteArray())

        assertEquals(xBi2.equals(xBi2.add(BigInt.one)), xJbi.equals(xJbi.add(JavaBigInteger.ONE)))
        assertFalse(xBi2.equals(xBi2.add(BigInt.one)))
    }
}