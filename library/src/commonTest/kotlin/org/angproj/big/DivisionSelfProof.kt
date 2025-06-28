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
import kotlin.test.assertFailsWith
import kotlin.test.assertEquals


/**
 * It seems like the bit size between short and long cannot be less than 32 bits.
 * Some bug with the magnitude array?
 * */
class DivisionSelfProof {

    val longBits = 192
    val shortBits = 112

    private val loops = 1_000_000


    fun proof(dividend: BigInt, divisor: BigInt) {
        val result = dividend.divideAndRemainder(divisor)

        val quotient = result.first
        val remainder = result.second

        assertEquals(dividend, divisor.times(quotient).plus(remainder))
    }

    /**
     * 1024 / 128 = 8
     * */
    @Test
    fun testLongPosDivShortPos(): Unit = repeat(loops) {
        val bigIntLongPos = BigInt.createRandomBigInt(longBits).abs()
        val bigIntShortPos = BigInt.createRandomBigInt(shortBits)

        val dividend = bigIntLongPos
        val divisor = bigIntShortPos

        proof(dividend, divisor)
    }

    /**
     * 1024 / -128 = -8
     * */
    @Test
    fun testLongPosDivShortNeg(): Unit = repeat(loops) {
        val bigIntLongPos = BigInt.createRandomBigInt(longBits).abs()
        val bigIntShortNeg = BigInt.createRandomBigInt(shortBits).abs().negate()

        val dividend = bigIntLongPos
        val divisor = bigIntShortNeg

        proof(dividend, divisor)
    }

    /**
     * -1024 / 128 = -8
     * */
    @Test
    fun testLongNegDivShortPos(): Unit = repeat(loops) {
        val bigIntLongNeg = BigInt.createRandomBigInt(longBits).abs().negate()
        val bigIntShortPos = BigInt.createRandomBigInt(shortBits).abs()

        val dividend = bigIntLongNeg
        val divisor = bigIntShortPos

        proof(dividend, divisor)
    }

    /**
     * -1024 / -128 = 8
     * */
    @Test
    fun testLongNegDivShortNeg(): Unit = repeat(loops) {
        val bigIntLongNeg = BigInt.createRandomBigInt(longBits).abs().negate()
        val bigIntShortNeg = BigInt.createRandomBigInt(shortBits).abs().negate()

        val dividend = bigIntLongNeg
        val divisor = bigIntShortNeg

        proof(dividend, divisor)
    }

    /**
     * 128 / 1024 = 0
     * */
    @Test
    fun testShortPosDivLongPos(): Unit = repeat(loops) {
        val bigIntShortPos = BigInt.createRandomBigInt(shortBits).abs()
        val bigIntLongPos = BigInt.createRandomBigInt(longBits).abs()

        val dividend = bigIntShortPos
        val divisor = bigIntLongPos

        proof(dividend, divisor)
    }

    /**
     * -128 / 1024 = 0
     * */
    @Test
    fun testShortNegDivLongPos(): Unit = repeat(loops) {
        val bigIntShortNeg = BigInt.createRandomBigInt(shortBits).abs().negate()
        val bigIntLongPos = BigInt.createRandomBigInt(longBits).abs()

        val dividend = bigIntShortNeg
        val divisor = bigIntLongPos

        proof(dividend, divisor)
    }

    /**
     * 128 / -1024 = 0
     * */
    @Test
    fun testShortPosDivLongNeg(): Unit = repeat(loops) {
        val bigIntShortPos = BigInt.createRandomBigInt(shortBits).abs()
        val bigIntLongNeg = BigInt.createRandomBigInt(longBits).abs().negate()

        val dividend = bigIntShortPos
        val divisor = bigIntLongNeg

        proof(dividend, divisor)
    }

    /**
     * -128 / -1024 = 0
     * */
    @Test
    fun testShortNegDivLongNeg(): Unit = repeat(loops) {
        val bigIntShortNeg = BigInt.createRandomBigInt(shortBits).abs().negate()
        val bigIntLongNeg = BigInt.createRandomBigInt(longBits).abs().negate()

        val dividend = bigIntShortNeg
        val divisor = bigIntLongNeg

        proof(dividend, divisor)
    }

    /**
     * 0 / 128 = 0
     * */
    @Test
    fun testZeroDivShortPos(): Unit = repeat(loops) {
        val bigIntZero = BigInt.zero
        val bigIntShortPos = BigInt.createRandomBigInt(shortBits).abs()

        val dividend = bigIntZero
        val divisor = bigIntShortPos

        proof(dividend, divisor)
    }

    /**
     * 0 / -128 = 0
     * */
    @Test
    fun testZeroDivShortNeg(): Unit = repeat(loops) {
        val bigIntZero = BigInt.zero
        val bigIntShortNeg = BigInt.createRandomBigInt(shortBits).abs().negate()

        val dividend = bigIntZero
        val divisor = bigIntShortNeg

        proof(dividend, divisor)
    }

    /**
     * 128 / 0 = 0
     * */
    @Test
    fun testShortPosDivZero(): Unit = repeat(loops) {
        val bigIntShortPos = BigInt.createRandomBigInt(shortBits).abs()
        val bigIntZero = BigInt.zero

        val dividend = bigIntShortPos
        val divisor = bigIntZero

        assertFailsWith<BigMathException> { dividend.divideAndRemainder(divisor) }
    }

    /**
     * -128 / 0 = 0
     * */
    @Test
    fun testShortNegDivZero(): Unit = repeat(loops) {
        val bigIntShortNeg = BigInt.createRandomBigInt(shortBits).abs().negate()
        val bigIntZero = BigInt.zero

        val dividend = bigIntShortNeg
        val divisor = bigIntZero

        assertFailsWith<BigMathException> { dividend.divideAndRemainder(divisor) }
    }

    /**
     * 0 / 0 = 0
     * */
    @Test
    fun testZeroDivZero() {
        assertFailsWith<BigMathException> { BigInt.zero.divideAndRemainder(BigInt.zero) }
    }

}