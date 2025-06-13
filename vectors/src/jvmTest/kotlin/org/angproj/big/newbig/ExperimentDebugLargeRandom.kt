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
import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertFailsWith



/**
 * It seems like the bit size between short and long cannot be less than 32 bits.
 * Some bug with the magnitude array?
 * */
class ExperimentDebugLargeRandom {

    private val loops = 1_000_000

    private lateinit var shortPositive: BigInt
    private lateinit var shortNegative: BigInt
    private lateinit var longPositive: BigInt
    private lateinit var longNegative: BigInt

    private fun generateRandomBigInts(){
        //val long = (128..256).random()
        //val short = (96..long).random()
        val long = 128
        val short = 96
        shortPositive = internalOf(Random.nextBytes(short / 8)).abs()
        shortNegative = internalOf(Random.nextBytes(short / 8)).abs().negate()
        longPositive = internalOf(Random.nextBytes(long / 8)).abs()
        longNegative = internalOf(Random.nextBytes(long / 8)).abs().negate()
    }

    init {
        generateRandomBigInts()
    }

    fun generateShortPositiveBigInt(): BigInt { return shortPositive }
    fun generateShortPositiveBigInteger(): BigInteger {
        val bigInt = generateShortPositiveBigInt()
        return BigInteger(toByteArray(bigInt.mag, bigInt.sigNum))
    }
    fun generateLongPositiveBigInt(): BigInt { return longPositive }
    fun generateLongPositiveBigInteger(): BigInteger {
        val bigInt = generateLongPositiveBigInt()
        return BigInteger(toByteArray(bigInt.mag, bigInt.sigNum))
    }
    fun generateShortNegativeBigInt(): BigInt { return shortNegative }
    fun generateShortNegativeBigInteger(): BigInteger {
        val bigInt = generateShortNegativeBigInt()
        return BigInteger(toByteArray(bigInt.mag, bigInt.sigNum))
    }
    fun generateLongNegativeBigInt(): BigInt { return longNegative }
    fun generateLongNegativeBigInteger(): BigInteger {
        val bigInt = generateLongNegativeBigInt()
        return BigInteger(toByteArray(bigInt.mag, bigInt.sigNum))
    }
    fun generateZeroBigInt(): BigInt { return BigInt.zero }
    fun generateZeroBigInteger(): BigInteger { return BigInteger.ZERO }

    @Test
    fun testIsSame() {
        val bigInt = generateShortPositiveBigInt()
        val bigInteger = generateShortPositiveBigInteger()

        assertContentEquals(bigInteger.toByteArray(), toByteArray(bigInt.mag, bigInt.sigNum))
    }

    /**
     * 1024 / 128 = 8
     * */
    @Test
    fun testLongPosDivShortPos(): Unit = repeat(loops) {
        generateRandomBigInts()

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
    fun testLongPosDivShortNeg(): Unit = repeat(loops) {
        generateRandomBigInts()

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
    fun testLongNegDivShortPos(): Unit = repeat(loops) {
        generateRandomBigInts()

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
    fun testLongNegDivShortNeg(): Unit = repeat(loops) {
        generateRandomBigInts()

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
    fun testShortPosDivLongPos(): Unit = repeat(loops) {
        generateRandomBigInts()

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
    fun testShortNegDivLongPos(): Unit = repeat(loops) {
        generateRandomBigInts()

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
    fun testShortPosDivLongNeg(): Unit = repeat(loops) {
        generateRandomBigInts()

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
    fun testShortNegDivLongNeg(): Unit = repeat(loops) {
        generateRandomBigInts()

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
    fun testZeroDivShortPos(): Unit = repeat(loops) {
        generateRandomBigInts()

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
    fun testZeroDivShortNeg(): Unit = repeat(loops) {
        generateRandomBigInts()

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
    fun testShortPosDivZero(): Unit = repeat(loops) {
        generateRandomBigInts()

        val bigInt = generateShortPositiveBigInt()
        val bigInteger = generateShortPositiveBigInteger()
        val zeroBigInt = generateZeroBigInt()
        val zeroBigInteger = generateZeroBigInteger()

        assertFailsWith<BigMathException> { bigInt.divideAndRemainder(zeroBigInt) }
        assertFailsWith<ArithmeticException> { bigInteger.divideAndRemainder(zeroBigInteger) }
    }

    /**
     * -128 / 0 = 0
     * */
    @Test
    fun testShortNegDivZero(): Unit = repeat(loops) {
        generateRandomBigInts()

        val bigInt = generateShortNegativeBigInt()
        val bigInteger = generateShortNegativeBigInteger()
        val zeroBigInt = generateZeroBigInt()
        val zeroBigInteger = generateZeroBigInteger()

        assertFailsWith<BigMathException> { bigInt.divideAndRemainder(zeroBigInt) }
        assertFailsWith<ArithmeticException> { bigInteger.divideAndRemainder(zeroBigInteger) }
    }

    /**
     * 0 / 0 = 0
     * */
    @Test
    fun testZeroDivZero(): Unit = repeat(loops) {
        generateRandomBigInts()

        val bigInt = generateZeroBigInt()
        val bigInteger = generateZeroBigInteger()

        assertFailsWith<BigMathException> { bigInt.divideAndRemainder(bigInt) }
        assertFailsWith<ArithmeticException> { bigInteger.divideAndRemainder(bigInteger) }
    }

}