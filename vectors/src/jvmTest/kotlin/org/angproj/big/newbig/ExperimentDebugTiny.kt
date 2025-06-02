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
package org.angproj.big.newbig

import org.angproj.big.BigMathException
import java.math.BigInteger
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertFailsWith


class ExperimentDebugTiny {

    fun generateShortPositiveBigInt(): BigInt { return internalOf(intArrayOf(128)) }
    fun generateShortPositiveBigInteger(): BigInteger { return BigInteger.valueOf(128) }
    fun generateLongPositiveBigInt(): BigInt { return internalOf(intArrayOf(1024)) }
    fun generateLongPositiveBigInteger(): BigInteger { return BigInteger.valueOf(1024) }
    fun generateShortNegativeBigInt(): BigInt { return internalOf(intArrayOf(-128)) }
    fun generateShortNegativeBigInteger(): BigInteger { return BigInteger.valueOf(-128) }
    fun generateLongNegativeBigInt(): BigInt { return internalOf(intArrayOf(-1024)) }
    fun generateLongNegativeBigInteger(): BigInteger { return BigInteger.valueOf(-1024) }
    fun generateZeroBigInt(): BigInt { return BigInt.zero }
    fun generateZeroBigInteger(): BigInteger { return BigInteger.ZERO }

    /**
     * 1024 / 128 = 8
     * */
    @Test
    fun testLongPosDivShortPos() {
        val bigInt = generateLongPositiveBigInt()
        val bigInteger = generateLongPositiveBigInteger()
        val shortPosBigInt = generateShortPositiveBigInt()
        val shortPosBigInteger = generateShortPositiveBigInteger()

        val result1 = bigInt.divideAndRemainder(shortPosBigInt)
        val result2 = bigInteger.divideAndRemainder(shortPosBigInteger)

        assertContentEquals(
            toByteArray(result1.first.mag, result1.first.sigNum),
            result2[0].toByteArray(),
        )
        assertContentEquals(
            toByteArray(result1.second.mag, result1.second.sigNum),
            result2[1].toByteArray(),
        )
    }

    /**
     * 1024 / -128 = -8
     * */
    @Test
    fun testLongPosDivShortNeg() {
        val bigInt = generateLongPositiveBigInt()
        val bigInteger = generateLongPositiveBigInteger()
        val shortNegBigInt = generateShortNegativeBigInt()
        val shortNegBigInteger = generateShortNegativeBigInteger()

        val result1 = bigInt.divideAndRemainder(shortNegBigInt)
        val result2 = bigInteger.divideAndRemainder(shortNegBigInteger)

        assertContentEquals(
            toByteArray(result1.first.mag, result1.first.sigNum),
            result2[0].toByteArray(),
        )
        assertContentEquals(
            toByteArray(result1.second.mag, result1.second.sigNum),
            result2[1].toByteArray(),
        )
    }

    /**
     * -1024 / 128 = -8
     * */
    @Test
    fun testLongNegDivShortPos() {
        val bigInt = generateLongNegativeBigInt()
        val bigInteger = generateLongNegativeBigInteger()
        val shortPosBigInt = generateShortPositiveBigInt()
        val shortPosBigInteger = generateShortPositiveBigInteger()

        val result1 = bigInt.divideAndRemainder(shortPosBigInt)
        val result2 = bigInteger.divideAndRemainder(shortPosBigInteger)

        assertContentEquals(
            toByteArray(result1.first.mag, result1.first.sigNum),
            result2[0].toByteArray(),
        )
        assertContentEquals(
            toByteArray(result1.second.mag, result1.second.sigNum),
            result2[1].toByteArray(),
        )
    }

    /**
     * -1024 / -128 = 8
     * */
    @Test
    fun testLongNegDivShortNeg() {
        val bigInt = generateLongNegativeBigInt()
        val bigInteger = generateLongNegativeBigInteger()
        val shortNegBigInt = generateShortNegativeBigInt()
        val shortNegBigInteger = generateShortNegativeBigInteger()

        val result1 = bigInt.divideAndRemainder(shortNegBigInt)
        val result2 = bigInteger.divideAndRemainder(shortNegBigInteger)

        assertContentEquals(
            toByteArray(result1.first.mag, result1.first.sigNum),
            result2[0].toByteArray(),
        )
        assertContentEquals(
            toByteArray(result1.second.mag, result1.second.sigNum),
            result2[1].toByteArray(),
        )
    }

    /**
     * 128 / 1024 = 0
     * */
    @Test
    fun testShortPosDivLongPos() {
        val bigInt = generateShortPositiveBigInt()
        val bigInteger = generateShortPositiveBigInteger()
        val longPosBigInt = generateLongPositiveBigInt()
        val longPosBigInteger = generateLongPositiveBigInteger()

        val result1 = bigInt.divideAndRemainder(longPosBigInt)
        val result2 = bigInteger.divideAndRemainder(longPosBigInteger)

        assertContentEquals(
            toByteArray(result1.first.mag, result1.first.sigNum),
            result2[0].toByteArray(),
        )
        assertContentEquals(
            toByteArray(result1.second.mag, result1.second.sigNum),
            result2[1].toByteArray(),
        )
    }

    /**
     * -128 / 1024 = 0
     * */
    @Test
    fun testShortNegDivLongPos() {
        val bigInt = generateShortNegativeBigInt()
        val bigInteger = generateShortNegativeBigInteger()
        val longPosBigInt = generateLongPositiveBigInt()
        val longPosBigInteger = generateLongPositiveBigInteger()

        val result1 = bigInt.divideAndRemainder(longPosBigInt)
        val result2 = bigInteger.divideAndRemainder(longPosBigInteger)

        assertContentEquals(
            toByteArray(result1.first.mag, result1.first.sigNum),
            result2[0].toByteArray(),
        )
        assertContentEquals(
            toByteArray(result1.second.mag, result1.second.sigNum),
            result2[1].toByteArray(),
        )
    }

    /**
     * 128 / -1024 = 0
     * */
    @Test
    fun testShortPosDivLongNeg() {
        val bigInt = generateShortPositiveBigInt()
        val bigInteger = generateShortPositiveBigInteger()
        val longNegBigInt = generateLongNegativeBigInt()
        val longNegBigInteger = generateLongNegativeBigInteger()

        val result1 = bigInt.divideAndRemainder(longNegBigInt)
        val result2 = bigInteger.divideAndRemainder(longNegBigInteger)

        assertContentEquals(
            toByteArray(result1.first.mag, result1.first.sigNum),
            result2[0].toByteArray(),
        )
        assertContentEquals(
            toByteArray(result1.second.mag, result1.second.sigNum),
            result2[1].toByteArray(),
        )
    }

    /**
     * -128 / -1024 = 0
     * */
    @Test
    fun testShortNegDivLongNeg() {
        val bigInt = generateShortNegativeBigInt()
        val bigInteger = generateShortNegativeBigInteger()
        val longNegBigInt = generateLongNegativeBigInt()
        val longNegBigInteger = generateLongNegativeBigInteger()

        val result1 = bigInt.divideAndRemainder(longNegBigInt)
        val result2 = bigInteger.divideAndRemainder(longNegBigInteger)

        assertContentEquals(
            toByteArray(result1.first.mag, result1.first.sigNum),
            result2[0].toByteArray(),
        )
        assertContentEquals(
            toByteArray(result1.second.mag, result1.second.sigNum),
            result2[1].toByteArray(),
        )
    }

    /**
     * 0 / 128 = 0
     * */
    @Test
    fun testZeroDivShortPos() {
        val bigInt = generateZeroBigInt()
        val bigInteger = generateZeroBigInteger()
        val shortPosBigInt = generateShortPositiveBigInt()
        val shortPosBigInteger = generateShortPositiveBigInteger()

        val result1 = bigInt.divideAndRemainder(shortPosBigInt)
        val result2 = bigInteger.divideAndRemainder(shortPosBigInteger)

        assertContentEquals(
            toByteArray(result1.first.mag, result1.first.sigNum),
            result2[0].toByteArray(),
        )
        assertContentEquals(
            toByteArray(result1.second.mag, result1.second.sigNum),
            result2[1].toByteArray(),
        )
    }

    /**
     * 0 / -128 = 0
     * */
    @Test
    fun testZeroDivShortNeg() {
        val bigInt = generateZeroBigInt()
        val bigInteger = generateZeroBigInteger()
        val shortNegBigInt = generateShortNegativeBigInt()
        val shortNegBigInteger = generateShortNegativeBigInteger()

        val result1 = bigInt.divideAndRemainder(shortNegBigInt)
        val result2 = bigInteger.divideAndRemainder(shortNegBigInteger)

        assertContentEquals(
            toByteArray(result1.first.mag, result1.first.sigNum),
            result2[0].toByteArray(),
        )
        assertContentEquals(
            toByteArray(result1.second.mag, result1.second.sigNum),
            result2[1].toByteArray(),
        )
    }

    /**
     * 128 / 0 = 0
     * */
    @Test
    fun testShortPosDivZero() {
        val bigInt = generateShortPositiveBigInt()
        val bigInteger = generateShortPositiveBigInteger()
        val zeroBigInt = generateZeroBigInt()
        val zeroBigInteger = generateZeroBigInteger()

        assertFailsWith<BigMathException> { val result1 = bigInt.divideAndRemainder(zeroBigInt) }
        assertFailsWith<ArithmeticException> { val result2 = bigInteger.divideAndRemainder(zeroBigInteger) }
    }

    /**
     * -128 / 0 = 0
     * */
    @Test
    fun testShortNegDivZero() {
        val bigInt = generateShortNegativeBigInt()
        val bigInteger = generateShortNegativeBigInteger()
        val zeroBigInt = generateZeroBigInt()
        val zeroBigInteger = generateZeroBigInteger()

        assertFailsWith<BigMathException> { val result1 = bigInt.divideAndRemainder(zeroBigInt) }
        assertFailsWith<ArithmeticException> { val result2 = bigInteger.divideAndRemainder(zeroBigInteger) }
    }

    /**
     * 0 / 0 = 0
     * */
    @Test
    fun testZeroDivZero() {
        val bigInt = generateZeroBigInt()
        val bigInteger = generateZeroBigInteger()

        assertFailsWith<BigMathException> { val result1 = bigInt.divideAndRemainder(bigInt) }
        assertFailsWith<ArithmeticException> { val result2 = bigInteger.divideAndRemainder(bigInteger) }
    }

}