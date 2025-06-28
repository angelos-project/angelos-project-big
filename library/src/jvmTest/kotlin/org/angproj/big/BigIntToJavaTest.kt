package org.angproj.big

import java.lang.NumberFormatException
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

import java.math.BigInteger as JavaBigInteger

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
class BigIntToJavaTest {

    /**
     * This test recognizes that BigInt and Java BigInteger interprets a ByteArray of some random values
     * the same when importing from the said ByteArray and exporting to a new ByteArray.
     * */
    @Test
    fun testByteArray() {
        val randomBytes = ByteArray(33) { it.toByte() }

        val bigInt = bigIntOf(randomBytes)
        val exportedBytes = bigInt.toByteArray()

        val javaBigInteger = JavaBigInteger(randomBytes)
        val javaExportedBytes = javaBigInteger.toByteArray()

        assertContentEquals(exportedBytes, javaExportedBytes, "Exported bytes do not match the Java BigInteger bytes")
    }

    /**
     * This test recognizes that BigInt and Java BigInteger interprets a Long random value
     * the same way when importing and then exporting to a new ByteArray.
     * */
    @Test
    fun testLong() {
        val someValue = 1234567890123456789L
        val bigInt = bigIntOf(someValue)
        val exportedValue = bigInt.toLong()

        assertEquals(someValue, exportedValue)
    }

    /**
     * This test recognizes that BigInt and Java BigInteger interprets a ByteArray of some random values
     * the same when exporting toInt.
     * */
    @Test
    fun testToInt() {
        val someValue: Int = 123456789
        val bigInt = bigIntOf(someValue.toLong())
        val exportedValue = bigInt.toInt()

        assertEquals(someValue, exportedValue)
    }

    @Test
    fun testToIntNeg() {
        val someValue: Int = -123456789
        val bigInt = bigIntOf(someValue.toLong())
        val exportedValue = bigInt.toInt()

        assertEquals(someValue, exportedValue)
    }

    @Test
    fun testToIntZero() {
        val someValue: Int = 0
        val bigInt = bigIntOf(someValue.toLong())
        val exportedValue = bigInt.toInt()

        assertEquals(someValue, exportedValue)
    }


    /**
     * This test recognizes that BigInt and Java BigInteger interprets a ByteArray of some random values
     * the same when exporting toLong.
     * */
    @Test
    fun testToLong() {
        val someValue = byteArrayOf(0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08)

        val bigInt = bigIntOf(someValue)
        val exportedValue = bigInt.toLong()

        val javaBigInteger = JavaBigInteger(someValue)
        val javaExportedValue = javaBigInteger.toLong()

        assertEquals(exportedValue, javaExportedValue, "Exported long value does not match the original value")
    }

    /**
     * This test recognizes that BigInt and Java BigInteger calculates
     * the sigNum of the same underlying value similarly.
     * */
    @Test
    fun testSigNum() {
        // TODO("Go deeper into the sigNum calculation and compare the results more thoroughly")
        val randomBytes = ByteArray(33) { it.toByte() }
        val bigInt = bigIntOf(randomBytes)
        val exportedValue = bigInt.sigNum.state

        val javaBigInteger = JavaBigInteger(randomBytes)
        val javaExportedValue = javaBigInteger.signum()

        assertEquals(exportedValue, javaExportedValue)
    }

    /**
     * This test recognizes that BigInt and Java BigInteger calculates
     * the bitLength of the same underlying value similarly.
     * */
    @Test
    fun testBitLength() {
        val randomBytes = ByteArray(33) { it.toByte() }
        val bigInt = bigIntOf(randomBytes)
        val exportedValue = bigInt.bitLength

        val javaBigInteger = JavaBigInteger(randomBytes)
        val javaExportedValue = javaBigInteger.bitLength()

        assertEquals(exportedValue, javaExportedValue, "Bit length does not match between BigInt and Java BigInteger")
    }

    /**
     * This test recognizes that BigInt and Java BigInteger calculates
     * the bitCount of the same underlying value similarly.
     * */
    @Test
    fun testBitCount() {
        val randomBytes = ByteArray(33) { it.toByte() }
        val bigInt = bigIntOf(randomBytes)
        val exportedValue = bigInt.bitCount

        val javaBigInteger = JavaBigInteger(randomBytes)
        val javaExportedValue = javaBigInteger.bitCount()

        assertEquals(exportedValue, javaExportedValue, "Bit count does not match between BigInt and Java BigInteger")
    }

    @Test
    fun testBitCountNeg() {
        val randomBytes = ByteArray(33) { it.toByte() }
        val bigInt = bigIntOf(randomBytes).negate()
        val exportedValue = bigInt.bitCount

        val javaBigInteger = JavaBigInteger(randomBytes).negate()
        val javaExportedValue = javaBigInteger.bitCount()

        assertEquals(exportedValue, javaExportedValue, "Bit count does not match between BigInt and Java BigInteger")
    }

    /**
     * This test certifies that both BigInt and Java BigInteger
     * throws an exception similarly in response to a zero-length ByteArray.
     * */
    @Test
    fun testEmptyByteArray() {
        val emptyByteArray = byteArrayOf()
        assertFailsWith<BigMathException> { bigIntOf(emptyByteArray) }
        assertFailsWith<NumberFormatException> { JavaBigInteger(emptyByteArray) }
    }

    /**
     *
     * */
    @Test
    fun testHashCode() {
        val randomBytes = ByteArray(33) { it.toByte() }
        val bigInt = bigIntOf(randomBytes)
        val exportedValue = bigInt.hashCode()

        val javaBigInteger = JavaBigInteger(randomBytes)
        val javaExportedValue = javaBigInteger.hashCode()

        assertEquals(exportedValue, javaExportedValue, "Bit count does not match between BigInt and Java BigInteger")
    }
}