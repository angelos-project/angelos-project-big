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

import org.angproj.aux.io.toBinary
import org.angproj.aux.io.toByteArray
import org.angproj.aux.mem.BufMgr
import org.angproj.aux.sec.SecureRandom
import java.math.BigInteger
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertSame
import java.math.BigInteger as JavaBigInteger

class DivisionTest {

    @OptIn(ExperimentalStdlibApi::class)
    @Test
    fun testWeirdInput1(): Unit = withLogic {
        val d1 = "ff000000ffffffffffffffffffffffffffffffffffffffffffffffffff0000000000000000000000002000000000000000000000000000000000000000000000".hexToByteArray()
        val d2 = "0000000800000000".hexToByteArray()
        val r1 = bigIntOf(d1).divideAndRemainder(bigIntOf(d2))
        val r2 = BigInteger(d1).divideAndRemainder(BigInteger(d2))

        r1.apply {
            println(first.toByteArray().toHexString())
            first.printDebug()
            println(second.toByteArray().toHexString())
            second.printDebug()
        }
        r2.apply {
            println(this[0].toByteArray().toHexString())
            println(this[1].toByteArray().toHexString())
        }

        assertContentEquals(r1.first.toByteArray(), r2[0].toByteArray())
        assertContentEquals(r1.second.toByteArray(), r2[1].toByteArray())
    }

    @OptIn(ExperimentalStdlibApi::class)
    @Test
    fun testWeirdInput2(): Unit = withLogic {
        val d1 = "000000ffffffffffffffffffffffffffffffffff26ffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff26ffffffffffffffffffffff".hexToByteArray()
        val d2 = "fffffbfbfbfbfbfbfbfbfbfbfbfbfbfbfbfbfbfbfbff".hexToByteArray()
        val r1 = bigIntOf(d1).divideAndRemainder(bigIntOf(d2))
        val r2 = BigInteger(d1).divideAndRemainder(BigInteger(d2))

        r1.apply {
            first.printDebug()
            second.printDebug()
        }
        r2.apply {
            println(this[0].toByteArray().toHexString())
            println(this[1].toByteArray().toHexString())
        }

        assertContentEquals(r1.first.toByteArray(), r2[0].toByteArray())
        assertContentEquals(r1.second.toByteArray(), r2[1].toByteArray())
    }

    @OptIn(ExperimentalStdlibApi::class)
    @Test
    fun testWeirdInput3(): Unit = withLogic {
        val d1 = "fffffffdfdfdfdfdfdfdfdfdfdfdfdfdfdfdfdfdfdfdfdfdfdfdfdfdfdfdfdfdfdfdfdfdfdfdfdfdfdfdfdfdfdfdfdfdfdfdfdfdfdfdfdfdfdfdfdfdfdfdfdfd".hexToByteArray()
        val d2 = "fdfdfdfdfdfdfdfdfdfdfdfdfdfdfdfdfdfdfdfdfdfdfdfdfdfdfdfdfdfdfdfdfdfdfdfdfd".hexToByteArray()
        val r1 = bigIntOf(d1).divideAndRemainder(bigIntOf(d2))
        val r2 = BigInteger(d1).divideAndRemainder(BigInteger(d2))

        r1.apply {
            first.printDebug()
            second.printDebug()
        }
        r2.apply {
            println(this[0].toByteArray().toHexString())
            println(this[1].toByteArray().toHexString())
        }

        assertContentEquals(r1.first.toByteArray(), r2[0].toByteArray())
        assertContentEquals(r1.second.toByteArray(), r2[1].toByteArray())
    }

    @OptIn(ExperimentalStdlibApi::class)
    @Test
    fun testWeirdInput4(): Unit = withLogic {
        val d1 = "ffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffa0a0a0a0a0a0ffffffffffffffffffffffffffffffffffffffffffffffffffff".hexToByteArray()
        val d2 = "ffffffa0a0a0a0a0a0ffffffffffffffffffffffffffffffffffffffffffffff".hexToByteArray()
        val r1 = bigIntOf(d1).divideAndRemainder(bigIntOf(d2))
        val r2 = BigInteger(d1).divideAndRemainder(BigInteger(d2))

        r1.apply {
            first.printDebug()
            second.printDebug()
        }
        r2.apply {
            println(this[0].toByteArray().toHexString())
            println(this[0].signum())
            println(this[1].toByteArray().toHexString())
            println(this[1].signum())
        }

        assertContentEquals(r1.first.toByteArray(), r2[0].toByteArray())
        assertContentEquals(r1.second.toByteArray(), r2[1].toByteArray())
    }

    /**
     * Generally fuzzes and validates that "public fun BigInt.divideAndRemainder(
     *     value: BigInt
     * ): Pair<BigInt, BigInt>" works
     * under all normal conditions. No special cases to test is currently known.
     * */
    @Test
    fun testDivideAndRemainder() = withLogic {
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
                    false
                )
            }
        }
    }

    /**
     * Generally fuzzes and validates that "public fun BigInt.divide(value: BigInt): BigInt" works
     * under all normal conditions. No special cases to test is currently known.
     * */
    @Test
    fun testDivide() = withLogic {
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
    fun testDiv() = withLogic {
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
    fun testRemainder() = withLogic {
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
    fun testRem() = withLogic {
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
    fun testDivisorIfZero(): Unit = withLogic {
        val x = BufMgr.bin(64).apply{ SecureRandom.read(this) }
        val xBi2 = bigIntOf(x)
        val xJbi = JavaBigInteger(x.toByteArray())

        assertFailsWith<BigMathException> { xBi2.divideAndRemainder(BigInt.zero) }
        assertFailsWith<ArithmeticException> { xJbi.divideAndRemainder(JavaBigInteger.ZERO) }
    }

    /**
     * Validates that division with divisor set as 1 is validated without a hiccup.
     * */
    @Test
    fun testDivisorIfOne() = withLogic {
        val x = BufMgr.bin(64).apply{ SecureRandom.read(this) }
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
    fun testDividendIfZero() = withLogic {
        val x = BufMgr.bin(64).apply{ SecureRandom.read(this) }
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
    fun testDividendIfLesser() = withLogic {
        val x = BufMgr.bin(64).apply{ SecureRandom.read(this) }
        val y = BufMgr.bin(63).apply{ SecureRandom.read(this) }
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
    fun testDividendIfEquals() = withLogic {
        val x = BufMgr.bin(64).apply{ SecureRandom.read(this) }
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
    fun testDividendIfNegated() = withLogic {
        val x = BufMgr.bin(64).apply{ SecureRandom.read(this) }
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

    /*@OptIn(ExperimentalStdlibApi::class)
    @Test
    fun testDivideAndRemainder2() {
        var xc = 0
        var yc = 0
        Combinator.numberGenerator(-64..64) { x ->
            val xBi2 = internalOf(x)
            val xJbi = JavaBigInteger(x)
            xc++
            //if(xJbi.equals(JavaBigInteger.ZERO)) return@numberGenerator
            Combinator.innerNumberGenerator(-64..64) { y ->
                yc++
                println("$xc, $yc")
                println("X: " + x.toHexString())
                println("Y: " + y.toHexString())
                val yBi2 = internalOf(y)
                val yJbi = JavaBigInteger(y)
                //if(yJbi.equals(JavaBigInteger.ZERO)) return@innerNumberGenerator

                val r2 = xJbi.divideAndRemainder(yJbi)
                val r1 = xBi2.divideAndRemainder(yBi2)

                println("Compare Quotient")
                assertContentEquals(r2[0].toByteArray(), toByteArray(r1.first.mag, r1.first.sigNum))
                println("Compare Remainder")
                assertContentEquals(r2[1].toByteArray(), toByteArray(r1.second.mag, r1.second.sigNum))

            }
        }
    }*/

    /*@Test
    fun testLoadExport2() {
        Combinator.numberGenerator(-128..128) { x ->
            val xBi2 = internalOf(x)
            val xJbi = JavaBigInteger(x)
            assertContentEquals(xJbi.toByteArray(), toByteArray(xBi2.mag, xBi2.sigNum))
        }
    }*/
}