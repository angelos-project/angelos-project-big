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

import org.angproj.aux.io.binOf
import org.angproj.aux.io.toBinary
import org.angproj.aux.io.toByteArray
import org.angproj.aux.sec.SecureRandom
import java.lang.ArithmeticException
import kotlin.test.*
import java.math.BigInteger as JavaBigInteger

class DivisionTest {
    /**
     * Generally fuzzes and validates that "public fun BigInt.divideAndRemainder(
     *     value: BigInt
     * ): Pair<BigInt, BigInt>" works
     * under all normal conditions. No special cases to test is currently known.
     * */
    @Test
    fun testDivideAndRemainder() {
        Combinator.numberGenerator(-64..64) { x ->
            val xBi2 = bigIntOf(x.toBinary())
            val xJbi = JavaBigInteger(x)
            Combinator.innerNumberGenerator(-64..64) { y ->
                val yBi2 = bigIntOf(y.toBinary())
                val yJbi = JavaBigInteger(y)
                if(yJbi.equals(JavaBigInteger.ZERO)) return@innerNumberGenerator

                Debugger.assertContentEqualsDouble(
                    x, y,
                    xJbi, yJbi,
                    xBi2, yBi2,
                    xJbi.divideAndRemainder(yJbi),
                    xBi2.divideAndRemainder(yBi2), // <- Emulation
                    true
                )
            }
        }
    }

    /**
     * Generally fuzzes and validates that "public fun BigInt.divide(value: BigInt): BigInt" works
     * under all normal conditions. No special cases to test is currently known.
     * */
    @Test
    fun testDivide() {
        Combinator.numberGenerator(-64..64) { x ->
            val xBi2 = bigIntOf(x.toBinary())
            val xJbi = JavaBigInteger(x)
            Combinator.innerNumberGenerator(-64..64) { y ->
                val yBi2 = bigIntOf(y.toBinary())
                val yJbi = JavaBigInteger(y)
                if(yJbi.equals(JavaBigInteger.ZERO)) return@innerNumberGenerator

                Debugger.assertContentEquals(
                    x, y,
                    xJbi, yJbi,
                    xBi2, yBi2,
                    xJbi.divide(yJbi),
                    xBi2.divide(yBi2), // <- Emulation
                )
            }
        }
    }

    /**
     * Kotlin specific mimic of extension used for Java BigInteger.
     * */
    @Test
    fun testDiv() {
        Combinator.numberGenerator(-64..64) { x ->
            val xBi2 = bigIntOf(x.toBinary())
            val xJbi = JavaBigInteger(x)
            Combinator.innerNumberGenerator(-64..64) { y ->
                val yBi2 = bigIntOf(y.toBinary())
                val yJbi = JavaBigInteger(y)
                if(yJbi.equals(JavaBigInteger.ZERO)) return@innerNumberGenerator

                Debugger.assertContentEquals(
                    x, y,
                    xJbi, yJbi,
                    xBi2, yBi2,
                    xJbi.div(yJbi),
                    xBi2 / yBi2, // <- Kotlin specific
                )
            }
        }
    }

    /**
     * Generally fuzzes and validates that "public fun BigInt.remainder(value: BigInt): BigInt" works
     * under all normal conditions. No special cases to test is currently known.
     * */
    @Test
    fun testRemainder() {
        Combinator.numberGenerator(-64..64) { x ->
            val xBi2 = bigIntOf(x.toBinary())
            val xJbi = JavaBigInteger(x)
            Combinator.innerNumberGenerator(-64..64) { y ->
                val yBi2 = bigIntOf(y.toBinary())
                val yJbi = JavaBigInteger(y)
                if(yJbi.equals(JavaBigInteger.ZERO)) return@innerNumberGenerator

                Debugger.assertContentEquals(
                    x, y,
                    xJbi, yJbi,
                    xBi2, yBi2,
                    xJbi.remainder(yJbi),
                    xBi2.remainder(yBi2), // <- Emulation
                )
            }
        }
    }

    /**
     * Kotlin specific mimic of extension used for Java BigInteger.
     * */
    @Test
    fun testRem() {
        Combinator.numberGenerator(-64..64) { x ->
            val xBi2 = bigIntOf(x.toBinary())
            val xJbi = JavaBigInteger(x)
            Combinator.innerNumberGenerator(-64..64) { y ->
                val yBi2 = bigIntOf(y.toBinary())
                val yJbi = JavaBigInteger(y)
                if(yJbi.equals(JavaBigInteger.ZERO)) return@innerNumberGenerator

                Debugger.assertContentEquals(
                    x, y,
                    xJbi, yJbi,
                    xBi2, yBi2,
                    xJbi.rem(yJbi),
                    xBi2 % yBi2, // <- Kotlin specific
                )
            }
        }
    }

    /**
     * Validates that BigMathException is thrown if the divisor is zero, likewise as Java BigInteger.
     * */
    @Test
    fun testDivisorIfZero() {
        val x = binOf(64).apply{ SecureRandom.read(this) }
        val xBi2 = bigIntOf(x)
        val xJbi = JavaBigInteger(x.toByteArray())

        assertFailsWith<BigMathException> { xBi2.divideAndRemainder(BigInt.zero) }
        assertFailsWith<ArithmeticException> { xJbi.divideAndRemainder(JavaBigInteger.ZERO) }
    }

    /**
     * Validates that division with divisor set as 1 is validated without a hiccup.
     * */
    @Test
    fun testDivisorIfOne() {
        val x = binOf(64).apply{ SecureRandom.read(this) }
        val xBi2 = bigIntOf(x)
        val xJbi = JavaBigInteger(x.toByteArray())

        val rBi2 = xBi2.divideAndRemainder(BigInt.one)
        val rJbi = xJbi.divideAndRemainder(JavaBigInteger.ONE)

        assertSame(rBi2.first, xBi2)
        assertSame(rBi2.second, BigInt.zero)

        assertContentEquals(rBi2.first.toByteArray(), rJbi[0].toByteArray())
        assertContentEquals(rBi2.second.toByteArray(), rJbi[1].toByteArray())
    }

    /**
     * Validates that division with dividend set as 0 is validated without a hiccup.
     * */
    @Test
    fun testDividendIfZero() {
        val x = binOf(64).apply{ SecureRandom.read(this) }
        val xBi2 = bigIntOf(x)
        val xJbi = JavaBigInteger(x.toByteArray())

        val rBi2 = BigInt.zero.divideAndRemainder(xBi2)
        val rJbi = JavaBigInteger.ZERO.divideAndRemainder(xJbi)

        assertSame(rBi2.first, BigInt.zero)
        assertSame(rBi2.second, BigInt.zero)

        assertContentEquals(rBi2.first.toByteArray(), rJbi[0].toByteArray())
        assertContentEquals(rBi2.second.toByteArray(), rJbi[1].toByteArray())
    }

    /**
     * Validates that division with dividend smaller than divisor is validated without a hiccup.
     * */
    @Test
    fun testDividendIfLesser() {
        val x = binOf(64).apply{ SecureRandom.read(this) }
        val y = binOf(63).apply{ SecureRandom.read(this) }
        val xBi2 = bigIntOf(x)
        val xJbi = JavaBigInteger(x.toByteArray())

        val yBi2 = bigIntOf(y)
        val yJbi = JavaBigInteger(y.toByteArray())

        val rBi2 = yBi2.divideAndRemainder(xBi2)
        val rJbi = yJbi.divideAndRemainder(xJbi)

        assertSame(rBi2.first, BigInt.zero)
        assertSame(rBi2.second, yBi2)

        assertContentEquals(rBi2.first.toByteArray(), rJbi[0].toByteArray())
        assertContentEquals(rBi2.second.toByteArray(), rJbi[1].toByteArray())
    }

    /**
     * Validates that division with dividend equal to the divisor is validated without a hiccup.
     * */
    @Test
    fun testDividendIfEquals() {
        val x = binOf(64).apply{ SecureRandom.read(this) }
        val xBi2 = bigIntOf(x)
        val xJbi = JavaBigInteger(x.toByteArray())

        val yBi2 = bigIntOf(x)
        val yJbi = JavaBigInteger(x.toByteArray())

        val rBi2 = xBi2.divideAndRemainder(yBi2)
        val rJbi = xJbi.divideAndRemainder(yJbi)

        assertSame(rBi2.first, BigInt.one)
        assertSame(rBi2.second, BigInt.zero)

        assertContentEquals(rBi2.first.toByteArray(), rJbi[0].toByteArray())
        assertContentEquals(rBi2.second.toByteArray(), rJbi[1].toByteArray())
    }

    /**
     * Validates that division with dividend negated to the divisor is validated without a hiccup.
     * */
    @Test
    fun testDividendIfNegated() {
        val x = binOf(64).apply{ SecureRandom.read(this) }
        val xBi2 = bigIntOf(x)
        val xJbi = JavaBigInteger(x.toByteArray())

        val yBi2 = bigIntOf(x).negate()
        val yJbi = JavaBigInteger(x.toByteArray()).negate()

        val rBi2 = xBi2.divideAndRemainder(yBi2)
        val rJbi = xJbi.divideAndRemainder(yJbi)

        assertSame(rBi2.first, BigInt.minusOne)
        assertSame(rBi2.second, BigInt.zero)

        assertContentEquals(rBi2.first.toByteArray(), rJbi[0].toByteArray())
        assertContentEquals(rBi2.second.toByteArray(), rJbi[1].toByteArray())
    }
}