/**
 * Copyright (c) 2023-2024 by Kristoffer Paulsson <kristoffer.paulsson@talenten.se>.
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
import kotlin.test.assertFailsWith

class DivisionTest {

    @Test
    fun testDivEquals() {
        BigInt.one.divideAndRemainder(BigInt.minusOne)
        BigInt.one.divideAndRemainder(BigInt.one)
    }

    /**
     * Generally fuzzes and validates that "public fun BigInt.divide(value: BigInt): BigInt" works
     * under all normal conditions. No special cases to test is currently known.
     * */
    @Test
    fun testDivide() {
        BigInt.one.divide(BigInt.minusOne)
    }

    /**
     * Kotlin specific mimic of extension used for Java BigInteger.
     * */
    @Test
    fun testDiv() {
        BigInt.one.div(BigInt.minusOne)

    }

    /**
     * Generally fuzzes and validates that "public fun BigInt.remainder(value: BigInt): BigInt" works
     * under all normal conditions. No special cases to test is currently known.
     * */
    @Test
    fun testRemainder() {
        BigInt.one.remainder(BigInt.minusOne)

    }

    /**
     * Kotlin specific mimic of extension used for Java BigInteger.
     * */
    @Test
    fun testRem() {
        BigInt.one.rem(BigInt.minusOne)

    }

    val loops = 1

    fun proof(dividend: BigInt, divisor: BigInt) {
        if(divisor != BigInt.zero) {
            val result = dividend.divideAndRemainder(divisor)

            val quotient = result.first
            val remainder = result.second

            assertEquals(dividend, divisor.times(quotient).plus(remainder))
        } else {
            assertFailsWith<BigMathException> { dividend.divideAndRemainder(divisor) }
        }
    }

    // 32x17

    /**
     * 1024 / 128 = 8
     * */
    @Test
    fun testLongPosDivShortPos32x17(): Unit = repeat(loops) {
        val bigIntLongPos = BigInt.createRandomBigInt(32).abs()
        val bigIntShortPos = BigInt.createRandomBigInt(17)

        val dividend = bigIntLongPos
        val divisor = bigIntShortPos

        proof(dividend, divisor)
    }

    /**
     * 1024 / -128 = -8
     * */
    @Test
    fun testLongPosDivShortNeg32x17(): Unit = repeat(loops) {
        val bigIntLongPos = BigInt.createRandomBigInt(32).abs()
        val bigIntShortNeg = BigInt.createRandomBigInt(17).abs().negate()

        val dividend = bigIntLongPos
        val divisor = bigIntShortNeg

        proof(dividend, divisor)
    }

    /**
     * -1024 / 128 = -8
     * */
    @Test
    fun testLongNegDivShortPos32x17(): Unit = repeat(loops) {
        val bigIntLongNeg = BigInt.createRandomBigInt(32).abs().negate()
        val bigIntShortPos = BigInt.createRandomBigInt(17).abs()

        val dividend = bigIntLongNeg
        val divisor = bigIntShortPos

        proof(dividend, divisor)
    }

    /**
     * -1024 / -128 = 8
     * */
    @Test
    fun testLongNegDivShortNeg32x17(): Unit = repeat(loops) {
        val bigIntLongNeg = BigInt.createRandomBigInt(32).abs().negate()
        val bigIntShortNeg = BigInt.createRandomBigInt(17).abs().negate()

        val dividend = bigIntLongNeg
        val divisor = bigIntShortNeg

        proof(dividend, divisor)
    }

    /**
     * 128 / 1024 = 0
     * */
    @Test
    fun testShortPosDivLongPos32x17(): Unit = repeat(loops) {
        val bigIntShortPos = BigInt.createRandomBigInt(17).abs()
        val bigIntLongPos = BigInt.createRandomBigInt(32).abs()

        val dividend = bigIntShortPos
        val divisor = bigIntLongPos

        proof(dividend, divisor)
    }

    /**
     * -128 / 1024 = 0
     * */
    @Test
    fun testShortNegDivLongPos32x17(): Unit = repeat(loops) {
        val bigIntShortNeg = BigInt.createRandomBigInt(17).abs().negate()
        val bigIntLongPos = BigInt.createRandomBigInt(32).abs()

        val dividend = bigIntShortNeg
        val divisor = bigIntLongPos

        proof(dividend, divisor)
    }

    /**
     * 128 / -1024 = 0
     * */
    @Test
    fun testShortPosDivLongNeg32x17(): Unit = repeat(loops) {
        val bigIntShortPos = BigInt.createRandomBigInt(17).abs()
        val bigIntLongNeg = BigInt.createRandomBigInt(32).abs().negate()

        val dividend = bigIntShortPos
        val divisor = bigIntLongNeg

        proof(dividend, divisor)
    }

    /**
     * -128 / -1024 = 0
     * */
    @Test
    fun testShortNegDivLongNeg32x17(): Unit = repeat(loops) {
        val bigIntShortNeg = BigInt.createRandomBigInt(17).abs().negate()
        val bigIntLongNeg = BigInt.createRandomBigInt(32).abs().negate()

        val dividend = bigIntShortNeg
        val divisor = bigIntLongNeg

        proof(dividend, divisor)
    }

    /**
     * 0 / 128 = 0
     * */
    @Test
    fun testZeroDivShortPos32x17(): Unit = repeat(loops) {
        val bigIntZero = BigInt.zero
        val bigIntShortPos = BigInt.createRandomBigInt(17).abs()

        val dividend = bigIntZero
        val divisor = bigIntShortPos

        proof(dividend, divisor)
    }

    /**
     * 0 / -128 = 0
     * */
    @Test
    fun testZeroDivShortNeg32x17(): Unit = repeat(loops) {
        val bigIntZero = BigInt.zero
        val bigIntShortNeg = BigInt.createRandomBigInt(17).abs().negate()

        val dividend = bigIntZero
        val divisor = bigIntShortNeg

        proof(dividend, divisor)
    }

    /**
     * 128 / 0 = 0
     * */
    @Test
    fun testShortPosDivZero32x17(): Unit = repeat(loops) {
        val bigIntShortPos = BigInt.createRandomBigInt(17).abs()
        val bigIntZero = BigInt.zero

        val dividend = bigIntShortPos
        val divisor = bigIntZero

        proof(dividend, divisor)
    }

    /**
     * -128 / 0 = 0
     * */
    @Test
    fun testShortNegDivZero32x17(): Unit = repeat(loops) {
        val bigIntShortNeg = BigInt.createRandomBigInt(17).abs().negate()
        val bigIntZero = BigInt.zero

        val dividend = bigIntShortNeg
        val divisor = bigIntZero

        proof(dividend, divisor)
    }

    // 125x31

    /**
     * 1024 / 128 = 8
     * */
    @Test
    fun testLongPosDivShortPos125x31(): Unit = repeat(loops) {
        val bigIntLongPos = BigInt.createRandomBigInt(125).abs()
        val bigIntShortPos = BigInt.createRandomBigInt(31)

        val dividend = bigIntLongPos
        val divisor = bigIntShortPos

        proof(dividend, divisor)
    }

    /**
     * 1024 / -128 = -8
     * */
    @Test
    fun testLongPosDivShortNeg125x31(): Unit = repeat(loops) {
        val bigIntLongPos = BigInt.createRandomBigInt(125).abs()
        val bigIntShortNeg = BigInt.createRandomBigInt(31).abs().negate()

        val dividend = bigIntLongPos
        val divisor = bigIntShortNeg

        proof(dividend, divisor)
    }

    /**
     * -1024 / 128 = -8
     * */
    @Test
    fun testLongNegDivShortPos125x31(): Unit = repeat(loops) {
        val bigIntLongNeg = BigInt.createRandomBigInt(125).abs().negate()
        val bigIntShortPos = BigInt.createRandomBigInt(31).abs()

        val dividend = bigIntLongNeg
        val divisor = bigIntShortPos

        proof(dividend, divisor)
    }

    /**
     * -1024 / -128 = 8
     * */
    @Test
    fun testLongNegDivShortNeg125x31(): Unit = repeat(loops) {
        val bigIntLongNeg = BigInt.createRandomBigInt(125).abs().negate()
        val bigIntShortNeg = BigInt.createRandomBigInt(31).abs().negate()

        val dividend = bigIntLongNeg
        val divisor = bigIntShortNeg

        proof(dividend, divisor)
    }

    /**
     * 128 / 1024 = 0
     * */
    @Test
    fun testShortPosDivLongPos125x31(): Unit = repeat(loops) {
        val bigIntShortPos = BigInt.createRandomBigInt(31).abs()
        val bigIntLongPos = BigInt.createRandomBigInt(125).abs()

        val dividend = bigIntShortPos
        val divisor = bigIntLongPos

        proof(dividend, divisor)
    }

    /**
     * -128 / 1024 = 0
     * */
    @Test
    fun testShortNegDivLongPos125x31(): Unit = repeat(loops) {
        val bigIntShortNeg = BigInt.createRandomBigInt(31).abs().negate()
        val bigIntLongPos = BigInt.createRandomBigInt(125).abs()

        val dividend = bigIntShortNeg
        val divisor = bigIntLongPos

        proof(dividend, divisor)
    }

    /**
     * 128 / -1024 = 0
     * */
    @Test
    fun testShortPosDivLongNeg125x31(): Unit = repeat(loops) {
        val bigIntShortPos = BigInt.createRandomBigInt(31).abs()
        val bigIntLongNeg = BigInt.createRandomBigInt(125).abs().negate()

        val dividend = bigIntShortPos
        val divisor = bigIntLongNeg

        proof(dividend, divisor)
    }

    /**
     * -128 / -1024 = 0
     * */
    @Test
    fun testShortNegDivLongNeg125x31(): Unit = repeat(loops) {
        val bigIntShortNeg = BigInt.createRandomBigInt(31).abs().negate()
        val bigIntLongNeg = BigInt.createRandomBigInt(125).abs().negate()

        val dividend = bigIntShortNeg
        val divisor = bigIntLongNeg

        proof(dividend, divisor)
    }

    /**
     * 0 / 128 = 0
     * */
    @Test
    fun testZeroDivShortPos125x31(): Unit = repeat(loops) {
        val bigIntZero = BigInt.zero
        val bigIntShortPos = BigInt.createRandomBigInt(31).abs()

        val dividend = bigIntZero
        val divisor = bigIntShortPos

        proof(dividend, divisor)
    }

    /**
     * 0 / -128 = 0
     * */
    @Test
    fun testZeroDivShortNeg125x31(): Unit = repeat(loops) {
        val bigIntZero = BigInt.zero
        val bigIntShortNeg = BigInt.createRandomBigInt(31).abs().negate()

        val dividend = bigIntZero
        val divisor = bigIntShortNeg

        proof(dividend, divisor)
    }

    /**
     * 128 / 0 = 0
     * */
    @Test
    fun testShortPosDivZero125x31(): Unit = repeat(loops) {
        val bigIntShortPos = BigInt.createRandomBigInt(31).abs()
        val bigIntZero = BigInt.zero

        val dividend = bigIntShortPos
        val divisor = bigIntZero

        proof(dividend, divisor)
    }

    /**
     * -128 / 0 = 0
     * */
    @Test
    fun testShortNegDivZero125x31(): Unit = repeat(loops) {
        val bigIntShortNeg = BigInt.createRandomBigInt(31).abs().negate()
        val bigIntZero = BigInt.zero

        val dividend = bigIntShortNeg
        val divisor = bigIntZero

        proof(dividend, divisor)
    }

    // 255x191

    /**
     * 1024 / 128 = 8
     * */
    @Test
    fun testLongPosDivShortPos255x191(): Unit = repeat(loops) {
        val bigIntLongPos = BigInt.createRandomBigInt(255).abs()
        val bigIntShortPos = BigInt.createRandomBigInt(191)

        val dividend = bigIntLongPos
        val divisor = bigIntShortPos

        proof(dividend, divisor)
    }

    /**
     * 1024 / -128 = -8
     * */
    @Test
    fun testLongPosDivShortNeg255x191(): Unit = repeat(loops) {
        val bigIntLongPos = BigInt.createRandomBigInt(255).abs()
        val bigIntShortNeg = BigInt.createRandomBigInt(191).abs().negate()

        val dividend = bigIntLongPos
        val divisor = bigIntShortNeg

        proof(dividend, divisor)
    }

    /**
     * -1024 / 128 = -8
     * */
    @Test
    fun testLongNegDivShortPos255x191(): Unit = repeat(loops) {
        val bigIntLongNeg = BigInt.createRandomBigInt(255).abs().negate()
        val bigIntShortPos = BigInt.createRandomBigInt(191).abs()

        val dividend = bigIntLongNeg
        val divisor = bigIntShortPos

        proof(dividend, divisor)
    }

    /**
     * -1024 / -128 = 8
     * */
    @Test
    fun testLongNegDivShortNeg255x191(): Unit = repeat(loops) {
        val bigIntLongNeg = BigInt.createRandomBigInt(255).abs().negate()
        val bigIntShortNeg = BigInt.createRandomBigInt(191).abs().negate()

        val dividend = bigIntLongNeg
        val divisor = bigIntShortNeg

        proof(dividend, divisor)
    }

    /**
     * 128 / 1024 = 0
     * */
    @Test
    fun testShortPosDivLongPos255x191(): Unit = repeat(loops) {
        val bigIntShortPos = BigInt.createRandomBigInt(191).abs()
        val bigIntLongPos = BigInt.createRandomBigInt(255).abs()

        val dividend = bigIntShortPos
        val divisor = bigIntLongPos

        proof(dividend, divisor)
    }

    /**
     * -128 / 1024 = 0
     * */
    @Test
    fun testShortNegDivLongPos255x191(): Unit = repeat(loops) {
        val bigIntShortNeg = BigInt.createRandomBigInt(191).abs().negate()
        val bigIntLongPos = BigInt.createRandomBigInt(255).abs()

        val dividend = bigIntShortNeg
        val divisor = bigIntLongPos

        proof(dividend, divisor)
    }

    /**
     * 128 / -1024 = 0
     * */
    @Test
    fun testShortPosDivLongNeg255x191(): Unit = repeat(loops) {
        val bigIntShortPos = BigInt.createRandomBigInt(191).abs()
        val bigIntLongNeg = BigInt.createRandomBigInt(255).abs().negate()

        val dividend = bigIntShortPos
        val divisor = bigIntLongNeg

        proof(dividend, divisor)
    }

    /**
     * -128 / -1024 = 0
     * */
    @Test
    fun testShortNegDivLongNeg255x191(): Unit = repeat(loops) {
        val bigIntShortNeg = BigInt.createRandomBigInt(191).abs().negate()
        val bigIntLongNeg = BigInt.createRandomBigInt(255).abs().negate()

        val dividend = bigIntShortNeg
        val divisor = bigIntLongNeg

        proof(dividend, divisor)
    }

    /**
     * 0 / 128 = 0
     * */
    @Test
    fun testZeroDivShortPos255x191(): Unit = repeat(loops) {
        val bigIntZero = BigInt.zero
        val bigIntShortPos = BigInt.createRandomBigInt(191).abs()

        val dividend = bigIntZero
        val divisor = bigIntShortPos

        proof(dividend, divisor)
    }

    /**
     * 0 / -128 = 0
     * */
    @Test
    fun testZeroDivShortNeg255x191(): Unit = repeat(loops) {
        val bigIntZero = BigInt.zero
        val bigIntShortNeg = BigInt.createRandomBigInt(191).abs().negate()

        val dividend = bigIntZero
        val divisor = bigIntShortNeg

        proof(dividend, divisor)
    }

    /**
     * 128 / 0 = 0
     * */
    @Test
    fun testShortPosDivZero255x191(): Unit = repeat(loops) {
        val bigIntShortPos = BigInt.createRandomBigInt(191).abs()
        val bigIntZero = BigInt.zero

        val dividend = bigIntShortPos
        val divisor = bigIntZero

        proof(dividend, divisor)
    }

    /**
     * -128 / 0 = 0
     * */
    @Test
    fun testShortNegDivZero255x191(): Unit = repeat(loops) {
        val bigIntShortNeg = BigInt.createRandomBigInt(191).abs().negate()
        val bigIntZero = BigInt.zero

        val dividend = bigIntShortNeg
        val divisor = bigIntZero

        proof(dividend, divisor)
    }

    /**
     * Generally fuzzes and validates that "public fun BigInt.divideAndRemainder(
     *     value: BigInt
     * ): Pair<BigInt, BigInt>" works
     * under all normal conditions. No special cases to test is currently known.
     * */
    @Test
    fun testDivideAndRemainder() {
    }
}