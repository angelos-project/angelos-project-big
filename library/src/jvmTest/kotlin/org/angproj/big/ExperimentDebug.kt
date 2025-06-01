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

import junit.framework.TestCase.assertNotNull
import org.angproj.aux.util.BinHex
import org.mockito.Mockito
import kotlin.math.absoluteValue
import kotlin.random.Random
import kotlin.math.max
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertFailsWith
import java.math.BigInteger as JavaBigInteger
import java.math.BigInteger

class ExperimentDebug {

    fun bitGen(negative: Boolean, length: Int): ByteArray {
        val pattern: Byte = 127
        return ByteArray(length) { pattern }.also { it[0] = if(negative) -1 else 1 }
    }

    fun bitGenBi(negative: Boolean, length: Int): BigInt = when(negative) {
        true -> BigInt.createRandomInRange(BigInt.minusOne.shiftLeft(length * 8), BigInt.minusOne.shiftLeft(length*8-1))
        else -> BigInt.createRandomInRange(BigInt.one.shiftLeft(length * 8-1), BigInt.one.shiftLeft(length*8))
    }

    // Should be fixed
    /**
     * This test recognizes that BigInt can predict its export size properly.
     * */
    @Test
    fun testToSize_asReference(): Unit = withLogic {
        Combinator.numberGenerator(-64..64) { x ->
            val xBi2 = bigIntOf(x)
            val xJbi = JavaBigInteger(x)

            Debugger.assertEquals(
                x,
                xJbi,
                xBi2,
                getByteSize(xBi2),
                xBi2.toByteArray().size,
            )
        }
    }

    // Should be fixed
    /**
     * This test recognizes that BigInt can predict its export size properly.
     * */
    @Test
    fun testToSize_forceError(): Unit = withLogic {
        val x = byteArrayOf(0xff.toByte(), 0xff.toByte(), 0xff.toByte())
        val xBi2 = bigIntOf(x)
        val xJbi = JavaBigInteger(x)

        Debugger.assertEquals(
            x,
            xJbi,
            xBi2,
            getByteSize(xBi2),
            xBi2.toByteArray().size,
        )
    }

    @Test
    fun doAction_doesSomething() = withLogic {
        val num1 = BigInt.createRandomBigInt(128)
        val num2 = BigInt.createRandomBigInt(120)

        val mockDividend = Mockito.mock(BigInt::class.java)
        Mockito.`when`(mockDividend.sigNum).thenReturn(num1.sigNum)
        Mockito.`when`(mockDividend.mag).thenReturn(num1.mag)

        val mockDivisor = Mockito.mock(BigInt::class.java)
        Mockito.`when`(mockDivisor.sigNum).thenReturn(num2.sigNum)
        Mockito.`when`(mockDivisor.mag).thenReturn(num2.mag)

        val r2 = mockDividend.divideAndRemainder(mockDivisor)
        val r1 = JavaBigInteger(num1.toByteArray()).divideAndRemainder(JavaBigInteger(num2.toByteArray()))


        assertContentEquals(r1[0].toByteArray(), r2.first.toByteArray())
        assertContentEquals(r1[1].toByteArray(), r2.second.toByteArray())
    }

    @Test
    fun fixTrix(): Unit = withLogic {
        val dividend = byteArrayOf(-95, -95, -95, -95, -95, -95, 0, 0, 0, 0, 0, -127, -111, -1, 0, 0, -1, 0, 0, -1, -1, -1, -1, -1, -1, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85)
        val divisor = byteArrayOf(85, 85, 85, 85, -93, 0, 46, -93)

        println(BinHex.encodeToHex(dividend))
        println(BinHex.encodeToHex(divisor))

        //*
        val r1 = JavaBigInteger(dividend).divideAndRemainder(JavaBigInteger(divisor))
        println(BinHex.encodeToHex(r1[0].toByteArray()))
        println(BinHex.encodeToHex(r1[1].toByteArray()))
        // */

        val r2 = bigIntOf(dividend).divideAndRemainder(bigIntOf(divisor))
        println(BinHex.encodeToHex(r2.first.toByteArray()))
        println(BinHex.encodeToHex(r2.second.toByteArray()))
    }

    @Test
    fun fixTrix2(): Unit = withLogic {
        //val dividend = byteArrayOf(-1, -1, -56, -1, -1, -1, -1, -1, -37, 42, 44, 44, 108, 108, 98, 108)
        //val divisor = byteArrayOf(10, 10, 42, 60, 42)
        val dividend =byteArrayOf(-33, 64, 64, 64, 64, 64, 0, -1, -1, -1, -1, -80, -80, -80, -80, -80)
        val divisor = byteArrayOf(-80, -54, -33, 0, -54)

        println("INPUT")
        println(BinHex.encodeToHex(dividend))
        println(BinHex.encodeToHex(divisor))


        println("JAVA")
        val r1 = JavaBigInteger(dividend).divideAndRemainder(JavaBigInteger(divisor))
        println(BinHex.encodeToHex(r1[0].toByteArray()))
        println(BinHex.encodeToHex(r1[1].toByteArray()))
        // */

        println("KOTLIN")
        val r2 = bigIntOf(dividend).divideAndRemainder(bigIntOf(divisor))
        println(BinHex.encodeToHex(r2.first.toByteArray()))
        println(BinHex.encodeToHex(r2.second.toByteArray()))

        bigIntOf(dividend).printDebug("DIVIDEND")
        bigIntOf(divisor).printDebug("DIVISOR")

        val mockBigIntDividend = Mockito.mock(BigInt::class.java)
        Mockito.`when`(mockBigIntDividend.mag).thenReturn(intArrayOf(14080, 0, 617993171, -1819042412))
        Mockito.`when`(mockBigIntDividend.sigNum).thenReturn(BigSigned.NEGATIVE)

        val mockBigIntDivisor = Mockito.mock(BigInt::class.java)
        Mockito.`when`(mockBigIntDivisor.mag).thenReturn(intArrayOf(10, 170540074))
        Mockito.`when`(mockBigIntDivisor.sigNum).thenReturn(BigSigned.POSITIVE)

        val mockR = mockBigIntDividend.divideAndRemainder(mockBigIntDivisor)

        assertNotNull(mockR)
    }

    @Test // -1,  1  OK       DIFF
    fun justTest(): Unit = withLogic {
        repeat(256) { x ->
            repeat(256) { y ->
                val dividend = x.toByte().toLong()
                val divisor = y.toByte().toLong()
                if(divisor.toInt() == 0) return@repeat

                val r1 = JavaBigInteger.valueOf(dividend).divideAndRemainder(JavaBigInteger.valueOf(divisor))
                //val q = (dividend / divisor).toInt()
                //val r = (dividend - q * divisor).toInt()
                val r2 = bigIntOf(dividend).divideAndRemainder(bigIntOf(divisor))

                try {
                    assertContentEquals(r1[0].toByteArray(), r2.first.toByteArray())
                    assertContentEquals(r1[1].toByteArray(), r2.second.toByteArray())
                    //assertEquals(r1[0].toLong(), q.toLong())
                    //assertEquals(r1[1].toLong(), r.toLong())
                    println("$dividend / $divisor")
                } catch (_: AssertionError) {
                    println("Dividend: $dividend")
                    println("Divisor: $divisor")
                    println("Java: Q " + r1[0].toLong() + " / R " + r1[1].toLong())
                    println("Kotlin: Q " + r2.first.toLong() + " / R " + r2.second.toLong())
                    println()
                }
            }
        }
    }

    @Test
    fun justTest2(): Unit = withLogic {
        val dividend = -120L
        val divisor = 119L

        val r1 = JavaBigInteger.valueOf(dividend).divideAndRemainder(JavaBigInteger.valueOf(divisor))
        val r2 = bigIntOf(dividend).divideAndRemainder(bigIntOf(divisor))

        try {
            assertContentEquals(r1[0].toByteArray(), r2.first.toByteArray())
            assertContentEquals(r1[1].toByteArray(), r2.second.toByteArray())
        } catch (_: AssertionError) {
            println("Dividend: $dividend")
            println("Divisor: $divisor")
            println("Java: Q " + r1[0].toLong() + " / R " + r1[1].toLong())
            println("Kotlin: Q " + r2.first.toLong() + " / R " + r2.second.toLong())
        }
    }

    fun randPosLong(): Long = max(Random.nextLong().absoluteValue, 1)
    fun randNegLong(): Long = -randPosLong()
    fun randPos(): Long = max(Random.nextInt().absoluteValue, 1).toLong()
    fun randNeg(): Long = -randPos().toLong()
    fun zeroInt(): Long = 0L

    // 2, 1, 0, -1, -2
    // 2 / 2
    // 2 / 1
    // 2 / 0
    // 2 / -1
    // 2 / -2
    // 1 / 2
    // 1 / 1
    // 1 / 0
    // 1 / -1
    // 1 / -2
    // 0 / 2
    // 0 / 1
    // 0 / 0
    // 0 / -1
    // 0 / -2
    // -1 / 2
    // -1 / 1
    // -1 / 0
    // -1 / -1
    // -1 / -2
    // -2 / 2
    // -2 / 1
    // -2 / 0
    // -2 / -1
    // -2 / -2

    @Test
    fun justTest4() {
        (5 downTo -5).forEach { dividend ->
            (5 downTo -5).forEach { divisor ->
                if(divisor == 0) println("$dividend / $divisor = Undefined")
                else println("$dividend / $divisor = ${dividend / divisor}, ${dividend % divisor}")
            }
        }
    }

    @Test
    fun justTest5() {
        intArrayOf(-5, -1, 0, 1, 5).forEach { dividend ->
            intArrayOf(-5, -1, 1, 5).forEach { divisor ->
                if(divisor == 0) println("$dividend / $divisor = Undefined")
                else println("$dividend / $divisor = ${dividend / divisor}, ${dividend % divisor}")
            }
        }
    }

    @Test
    fun justTest6() {
        intArrayOf(1, -1).forEach { dividend ->
            (5 downTo -5).forEach { divisor ->
                if(divisor == 0) println("$dividend / $divisor = Undefined")
                else println("$dividend / $divisor = ${dividend / divisor}, ${dividend % divisor}")
            }
        }
    }

    @Test //  1,  1  OK       OK
    fun testPosDivPos(): Unit = withLogicR(1_000_000) {
        val dividend = randPos().toLong()
        val divisor = randPos().toLong()

        val r1 = JavaBigInteger.valueOf(dividend).divideAndRemainder(JavaBigInteger.valueOf(divisor))
        val r2 = bigIntOf(dividend).divideAndRemainder(bigIntOf(divisor))

        assertContentEquals(r1[0].toByteArray(), r2.first.toByteArray())
        assertContentEquals(r1[1].toByteArray(), r2.second.toByteArray())
    }

    @Test //  1, -1  OK       OK
    fun testPosDivNeg(): Unit = withLogicR(1_000_000) {
        val dividend = randPos().toLong()
        val divisor = randNeg().toLong()

        val r1 = JavaBigInteger.valueOf(dividend).divideAndRemainder(JavaBigInteger.valueOf(divisor))
        val r2 = bigIntOf(dividend).divideAndRemainder(bigIntOf(divisor))

        assertContentEquals(r1[0].toByteArray(), r2.first.toByteArray())
        assertContentEquals(r1[1].toByteArray(), r2.second.toByteArray())
    }

    @Test // -1,  1  OK       DIFF
    fun testNegDivPos(): Unit = withLogicR(1_000_000) {
        val dividend = randNeg().toLong()
        val divisor = randPos().toLong()

        val r1 = JavaBigInteger.valueOf(dividend).divideAndRemainder(JavaBigInteger.valueOf(divisor))
        val r2 = bigIntOf(dividend).divideAndRemainder(bigIntOf(divisor))

        assertContentEquals(r1[0].toByteArray(), r2.first.toByteArray())
        assertContentEquals(r1[1].toByteArray(), r2.second.toByteArray())
    }

    @Test // -1, -1  OK       OK
    fun testNegDivNeg(): Unit = withLogicR(1_000_000) {
        val dividend = randNeg().toLong()
        val divisor = randNeg().toLong()

        val r1 = JavaBigInteger.valueOf(dividend).divideAndRemainder(JavaBigInteger.valueOf(divisor))
        val r2 = bigIntOf(dividend).divideAndRemainder(bigIntOf(divisor))

        assertContentEquals(r1[0].toByteArray(), r2.first.toByteArray())
        assertContentEquals(r1[1].toByteArray(), r2.second.toByteArray())
    }

    @Test //  1,  0  ERR      ERR
    fun testPosDivZero(): Unit = withLogicR(1_000_000) {
        val dividend = randPos().toLong()
        val divisor = zeroInt().toLong()

        assertFailsWith<ArithmeticException> {
            val r1 = JavaBigInteger.valueOf(dividend).divideAndRemainder(JavaBigInteger.valueOf(divisor))
        }
        assertFailsWith<BigMathException> {
            val r2 = bigIntOf(dividend).divideAndRemainder(bigIntOf(divisor))
        }
    }

    @Test //  0,  1  OK       OK
    fun testZeroDivPos(): Unit = withLogicR(1_000_000) {
        val dividend = zeroInt().toLong()
        val divisor = randPos().toLong()

        val r1 = JavaBigInteger.valueOf(dividend).divideAndRemainder(JavaBigInteger.valueOf(divisor))
        val r2 = bigIntOf(dividend).divideAndRemainder(bigIntOf(divisor))

        assertContentEquals(r1[0].toByteArray(), r2.first.toByteArray())
        assertContentEquals(r1[1].toByteArray(), r2.second.toByteArray())
    }

    @Test //  0,  0  ERR      ERR
    fun testZeroDivZero(): Unit = withLogicR(1_000_000) {
        val dividend = zeroInt().toLong()
        val divisor = zeroInt().toLong()

        assertFailsWith<ArithmeticException> {
            val r1 = JavaBigInteger.valueOf(dividend).divideAndRemainder(JavaBigInteger.valueOf(divisor))
        }
        assertFailsWith<BigMathException> {
            val r2 = bigIntOf(dividend).divideAndRemainder(bigIntOf(divisor))
        }
    }

    @Test // -1,  0  ERR      ERR
    fun testNegDivZero(): Unit = withLogicR(1_000_000) {
        val dividend = randNeg().toLong()
        val divisor = zeroInt().toLong()

        assertFailsWith<ArithmeticException> {
            val r1 = JavaBigInteger.valueOf(dividend).divideAndRemainder(JavaBigInteger.valueOf(divisor))
        }
        assertFailsWith<BigMathException> {
            val r2 = bigIntOf(dividend).divideAndRemainder(bigIntOf(divisor))
        }
    }

    @Test //  0, -1  OK       OK
    fun testZeroDivNeg(): Unit = withLogicR(1_000_000) {
        val dividend = zeroInt().toLong()
        val divisor = randNeg().toLong()

        val r1 = JavaBigInteger.valueOf(dividend).divideAndRemainder(JavaBigInteger.valueOf(divisor))
        val r2 = bigIntOf(dividend).divideAndRemainder(bigIntOf(divisor))

        assertContentEquals(r1[0].toByteArray(), r2.first.toByteArray())
        assertContentEquals(r1[1].toByteArray(), r2.second.toByteArray())
    }

    // #####################################

    @Test //  1,  1  OK       OK
    fun testPosDivPosLong(): Unit = withLogicR(1) {
        val dividend = BigInt.createRandomInRange(BigInt.one.shiftLeft(63), BigInt.one.shiftLeft(64))
        val divisor = BigInt.createRandomInRange(BigInt.one.shiftLeft(31), BigInt.one.shiftLeft(32))

        val mockDividend = Mockito.mock(BigInt::class.java)
        Mockito.`when`(mockDividend.sigNum).thenReturn(dividend.sigNum)
        Mockito.`when`(mockDividend.mag).thenReturn(dividend.mag)

        val mockDivisor = Mockito.mock(BigInt::class.java)
        Mockito.`when`(mockDivisor.sigNum).thenReturn(divisor.sigNum)
        Mockito.`when`(mockDivisor.mag).thenReturn(divisor.mag)

        val r2 = mockDividend.divideAndRemainder(mockDivisor)
        val r1 = JavaBigInteger(dividend.toByteArray()).divideAndRemainder(JavaBigInteger(divisor.toByteArray()))

        assertContentEquals(r1[0].toByteArray(), r2.first.toByteArray())
        assertContentEquals(r1[1].toByteArray(), r2.second.toByteArray())
    }

    @Test //  1, -1  OK       OK
    fun testPosDivNegLong(): Unit = withLogicR(1) {
        val dividend = BigInt.createRandomInRange(BigInt.one.shiftLeft(63), BigInt.one.shiftLeft(64))
        val divisor = BigInt.createRandomInRange(BigInt.minusOne.shiftLeft(64), BigInt.minusOne.shiftLeft(63))

        val mockDividend = Mockito.mock(BigInt::class.java)
        Mockito.`when`(mockDividend.sigNum).thenReturn(dividend.sigNum)
        Mockito.`when`(mockDividend.mag).thenReturn(dividend.mag)

        val mockDivisor = Mockito.mock(BigInt::class.java)
        Mockito.`when`(mockDivisor.sigNum).thenReturn(divisor.sigNum)
        Mockito.`when`(mockDivisor.mag).thenReturn(divisor.mag)

        val r2 = mockDividend.divideAndRemainder(mockDivisor)
        val r1 = JavaBigInteger(dividend.toByteArray()).divideAndRemainder(JavaBigInteger(divisor.toByteArray()))

        assertContentEquals(r1[0].toByteArray(), r2.first.toByteArray())
        assertContentEquals(r1[1].toByteArray(), r2.second.toByteArray())
    }

    @Test // -1,  1  OK       DIFF
    fun testNegDivPosLong(): Unit = withLogicR(1) {
        val dividend = BigInt.createRandomInRange(BigInt.minusOne.shiftLeft(64), BigInt.minusOne.shiftLeft(63))
        val divisor = BigInt.createRandomInRange(BigInt.one.shiftLeft(31), BigInt.one.shiftLeft(32))

        val mockDividend = Mockito.mock(BigInt::class.java)
        Mockito.`when`(mockDividend.sigNum).thenReturn(dividend.sigNum)
        Mockito.`when`(mockDividend.mag).thenReturn(dividend.mag)

        val mockDivisor = Mockito.mock(BigInt::class.java)
        Mockito.`when`(mockDivisor.sigNum).thenReturn(divisor.sigNum)
        Mockito.`when`(mockDivisor.mag).thenReturn(divisor.mag)

        val r2 = mockDividend.divideAndRemainder(mockDivisor)
        val r1 = JavaBigInteger(dividend.toByteArray()).divideAndRemainder(JavaBigInteger(divisor.toByteArray()))

        assertContentEquals(r1[0].toByteArray(), r2.first.toByteArray())
        assertContentEquals(r1[1].toByteArray(), r2.second.toByteArray())
    }

    @Test // -1, -1  OK       OK
    fun testNegDivNegLong(): Unit = withLogicR(1) {
        val dividend = BigInt.createRandomInRange(BigInt.minusOne.shiftLeft(64), BigInt.minusOne.shiftLeft(63))
        val divisor = BigInt.createRandomInRange(BigInt.minusOne.shiftLeft(32), BigInt.minusOne.shiftLeft(31))

        val mockDividend = Mockito.mock(BigInt::class.java)
        Mockito.`when`(mockDividend.sigNum).thenReturn(dividend.sigNum)
        Mockito.`when`(mockDividend.mag).thenReturn(dividend.mag)

        val mockDivisor = Mockito.mock(BigInt::class.java)
        Mockito.`when`(mockDivisor.sigNum).thenReturn(divisor.sigNum)
        Mockito.`when`(mockDivisor.mag).thenReturn(divisor.mag)

        val r2 = mockDividend.divideAndRemainder(mockDivisor)
        val r1 = JavaBigInteger(dividend.toByteArray()).divideAndRemainder(JavaBigInteger(divisor.toByteArray()))

        assertContentEquals(r1[0].toByteArray(), r2.first.toByteArray())
        assertContentEquals(r1[1].toByteArray(), r2.second.toByteArray())
    }

    @Test //  1,  0  ERR      ERR
    fun testPosDivZeroLong(): Unit = withLogicR(1) {
        val dividend = BigInt.createRandomInRange(BigInt.one.shiftLeft(31), BigInt.one.shiftLeft(32))
        val divisor = BigInt.zero

        val mockDividend = Mockito.mock(BigInt::class.java)
        Mockito.`when`(mockDividend.sigNum).thenReturn(dividend.sigNum)
        Mockito.`when`(mockDividend.mag).thenReturn(dividend.mag)

        val mockDivisor = Mockito.mock(BigInt::class.java)
        Mockito.`when`(mockDivisor.sigNum).thenReturn(divisor.sigNum)
        Mockito.`when`(mockDivisor.mag).thenReturn(divisor.mag)

        assertFailsWith<ArithmeticException> {
            val r1 = JavaBigInteger(dividend.toByteArray()).divideAndRemainder(JavaBigInteger(divisor.toByteArray()))
        }
        assertFailsWith<BigMathException> {
            val r2 = mockDividend.divideAndRemainder(mockDivisor)
        }
    }

    @Test // -1,  0  ERR      ERR
    fun testNegDivZeroLong(): Unit = withLogicR(1) {
        val dividend = BigInt.createRandomInRange(BigInt.minusOne.shiftLeft(32), BigInt.minusOne.shiftLeft(31))
        val divisor = BigInt.zero

        val mockDividend = Mockito.mock(BigInt::class.java)
        Mockito.`when`(mockDividend.sigNum).thenReturn(dividend.sigNum)
        Mockito.`when`(mockDividend.mag).thenReturn(dividend.mag)

        val mockDivisor = Mockito.mock(BigInt::class.java)
        Mockito.`when`(mockDivisor.sigNum).thenReturn(divisor.sigNum)
        Mockito.`when`(mockDivisor.mag).thenReturn(divisor.mag)

        assertFailsWith<ArithmeticException> {
            val r1 = JavaBigInteger(dividend.toByteArray()).divideAndRemainder(JavaBigInteger(divisor.toByteArray()))
        }
        assertFailsWith<BigMathException> {
            val r2 = mockDividend.divideAndRemainder(mockDivisor)
        }
    }


    // #####################################################

    @Test //  1,  1  OK       OK
    fun testPosLDivPosSBytes(): Unit = withLogicR(1) {
        val length = Random.nextInt().absoluteValue.mod(20) + 8
        val dividend = bitGenBi(false, length)
        val divisor = bitGenBi(false, length - 4)

        val mockDividend = Mockito.mock(BigInt::class.java)
        Mockito.`when`(mockDividend.sigNum).thenReturn(dividend.sigNum)
        Mockito.`when`(mockDividend.mag).thenReturn(dividend.mag)

        val mockDivisor = Mockito.mock(BigInt::class.java)
        Mockito.`when`(mockDivisor.sigNum).thenReturn(divisor.sigNum)
        Mockito.`when`(mockDivisor.mag).thenReturn(divisor.mag)

        try {
            val r1 = mockDividend.divideAndRemainder(mockDivisor)
            val r2 = JavaBigInteger(dividend.toByteArray()).divideAndRemainder(JavaBigInteger(divisor.toByteArray()))

            //println("apa")
            assertContentEquals(r2[0].toByteArray(), r1.first.toByteArray())
            assertContentEquals(r2[1].toByteArray(), r1.second.toByteArray())
        } catch (e: java.lang.AssertionError) {
            println("Dividend: [${dividend.toByteArray().joinToString(", ")}]")
            println("Divisor: [${divisor.toByteArray().joinToString(", ")}]")
            throw e
        }
    }

    @Test //  1,  1  OK       OK
    fun testPosSDivPosLBytes(): Unit = withLogicR(1) {
        val length = Random.nextInt().absoluteValue.mod(60) + 8
        val dividend = bitGenBi(false, length - 4)
        val divisor = bitGenBi(false, length)

        val mockDividend = Mockito.mock(BigInt::class.java)
        Mockito.`when`(mockDividend.sigNum).thenReturn(dividend.sigNum)
        Mockito.`when`(mockDividend.mag).thenReturn(dividend.mag)

        val mockDivisor = Mockito.mock(BigInt::class.java)
        Mockito.`when`(mockDivisor.sigNum).thenReturn(divisor.sigNum)
        Mockito.`when`(mockDivisor.mag).thenReturn(divisor.mag)

        try {
            val r1 = mockDividend.divideAndRemainder(mockDivisor)
            val r2 = JavaBigInteger(dividend.toByteArray()).divideAndRemainder(JavaBigInteger(divisor.toByteArray()))

            //println("apa")
            assertContentEquals(r2[0].toByteArray(), r1.first.toByteArray())
            assertContentEquals(r2[1].toByteArray(), r1.second.toByteArray())
        } catch (e: java.lang.AssertionError) {
            println("Dividend: [${dividend.toByteArray().joinToString(", ")}]")
            println("Divisor: [${divisor.toByteArray().joinToString(", ")}]")
            throw e
        }
    }

    @Test //  1, -1  OK       OK
    fun testPosLDivNegSBytes(): Unit = withLogicR(1) {
        val length = Random.nextInt().absoluteValue.mod(60) + 8
        val dividend = bitGenBi(false, length)
        val divisor = bitGenBi(true, length - 4)

        val mockDividend = Mockito.mock(BigInt::class.java)
        Mockito.`when`(mockDividend.sigNum).thenReturn(dividend.sigNum)
        Mockito.`when`(mockDividend.mag).thenReturn(dividend.mag)

        val mockDivisor = Mockito.mock(BigInt::class.java)
        Mockito.`when`(mockDivisor.sigNum).thenReturn(divisor.sigNum)
        Mockito.`when`(mockDivisor.mag).thenReturn(divisor.mag)

        try {
            val r1 = mockDividend.divideAndRemainder(mockDivisor)
            val r2 = JavaBigInteger(dividend.toByteArray()).divideAndRemainder(JavaBigInteger(divisor.toByteArray()))

            //println("apa")
            assertContentEquals(r2[0].toByteArray(), r1.first.toByteArray())
            assertContentEquals(r2[1].toByteArray(), r1.second.toByteArray())
        } catch (e: java.lang.AssertionError) {
            println("Dividend: [${dividend.toByteArray().joinToString(", ")}]")
            println("Divisor: [${divisor.toByteArray().joinToString(", ")}]")
            /*println("Quot-Java: [${r1[0].toByteArray().joinToString(", ")}]")
            println("Quot-Kotl: [${r2.first.toByteArray().joinToString(", ")}]")
            println("Rem-Java: [${r1[1].toByteArray().joinToString(", ")}]")
            println("Rem-Kotl: [${r2.second.toByteArray().joinToString(", ")}]")*/
            throw e
        }
    }

    @Test //  1, -1  OK       OK
    fun testPosSDivNegLBytes(): Unit = withLogicR(1) {
        val length = Random.nextInt().absoluteValue.mod(60) + 8
        val dividend = bitGenBi(false, length-4)
        val divisor = bitGenBi(true, length)

        val mockDividend = Mockito.mock(BigInt::class.java)
        Mockito.`when`(mockDividend.sigNum).thenReturn(dividend.sigNum)
        Mockito.`when`(mockDividend.mag).thenReturn(dividend.mag)

        val mockDivisor = Mockito.mock(BigInt::class.java)
        Mockito.`when`(mockDivisor.sigNum).thenReturn(divisor.sigNum)
        Mockito.`when`(mockDivisor.mag).thenReturn(divisor.mag)

        try {
            val r1 = mockDividend.divideAndRemainder(mockDivisor)
            val r2 = JavaBigInteger(dividend.toByteArray()).divideAndRemainder(JavaBigInteger(divisor.toByteArray()))

            //println("apa")
            assertContentEquals(r2[0].toByteArray(), r1.first.toByteArray())
            assertContentEquals(r2[1].toByteArray(), r1.second.toByteArray())
        } catch (e: java.lang.AssertionError) {
            println("Dividend: [${dividend.toByteArray().joinToString(", ")}]")
            println("Divisor: [${divisor.toByteArray().joinToString(", ")}]")
            /*println("Quot-Java: [${r1[0].toByteArray().joinToString(", ")}]")
            println("Quot-Kotl: [${r2.first.toByteArray().joinToString(", ")}]")
            println("Rem-Java: [${r1[1].toByteArray().joinToString(", ")}]")
            println("Rem-Kotl: [${r2.second.toByteArray().joinToString(", ")}]")*/
            throw e
        }
    }

    @Test // -1,  1  OK       DIFF // mulSub using lowerHalf
    fun testNegLDivPosSBytes(): Unit = withLogicR(1) {
        val length = Random.nextInt().absoluteValue.mod(60) + 8
        val dividend = bitGenBi(true, length)
        val divisor = bitGenBi(false, length - 4)

        val mockDividend = Mockito.mock(BigInt::class.java)
        Mockito.`when`(mockDividend.sigNum).thenReturn(dividend.sigNum)
        Mockito.`when`(mockDividend.mag).thenReturn(dividend.mag)

        val mockDivisor = Mockito.mock(BigInt::class.java)
        Mockito.`when`(mockDivisor.sigNum).thenReturn(divisor.sigNum)
        Mockito.`when`(mockDivisor.mag).thenReturn(divisor.mag)

        try {
            val r1 = mockDividend.divideAndRemainder(mockDivisor)
            val r2 = JavaBigInteger(dividend.toByteArray()).divideAndRemainder(JavaBigInteger(divisor.toByteArray()))

            //println("apa")
            assertContentEquals(r2[0].toByteArray(), r1.first.toByteArray())
            assertContentEquals(r2[1].toByteArray(), r1.second.toByteArray())
        } catch (e: java.lang.AssertionError) {
            println("Dividend: [${dividend.toByteArray().joinToString(", ")}]")
            println("Divisor: [${divisor.toByteArray().joinToString(", ")}]")
            /*println("Quot-Java: [${r1[0].toByteArray().joinToString(", ")}]")
            println("Quot-Kotl: [${r2.first.toByteArray().joinToString(", ")}]")
            println("Rem-Java: [${r1[1].toByteArray().joinToString(", ")}]")
            println("Rem-Kotl: [${r2.second.toByteArray().joinToString(", ")}]")*/
            throw e
        }
    }

    @Test // -1,  1  OK       DIFF
    fun testNegSDivPosLBytes(): Unit = withLogicR(1) {
        val length = Random.nextInt().absoluteValue.mod(60) + 8
        val dividend = bitGenBi(true, length - 4)
        val divisor = bitGenBi(false, length)

        val mockDividend = Mockito.mock(BigInt::class.java)
        Mockito.`when`(mockDividend.sigNum).thenReturn(dividend.sigNum)
        Mockito.`when`(mockDividend.mag).thenReturn(dividend.mag)

        val mockDivisor = Mockito.mock(BigInt::class.java)
        Mockito.`when`(mockDivisor.sigNum).thenReturn(divisor.sigNum)
        Mockito.`when`(mockDivisor.mag).thenReturn(divisor.mag)

        try {
            val r1 = mockDividend.divideAndRemainder(mockDivisor)
            val r2 = JavaBigInteger(dividend.toByteArray()).divideAndRemainder(JavaBigInteger(divisor.toByteArray()))

            //println("apa")
            assertContentEquals(r2[0].toByteArray(), r1.first.toByteArray())
            assertContentEquals(r2[1].toByteArray(), r1.second.toByteArray())
        } catch (e: java.lang.AssertionError) {
            println("Dividend: [${dividend.toByteArray().joinToString(", ")}]")
            println("Divisor: [${divisor.toByteArray().joinToString(", ")}]")
            /*println("Quot-Java: [${r1[0].toByteArray().joinToString(", ")}]")
            println("Quot-Kotl: [${r2.first.toByteArray().joinToString(", ")}]")
            println("Rem-Java: [${r1[1].toByteArray().joinToString(", ")}]")
            println("Rem-Kotl: [${r2.second.toByteArray().joinToString(", ")}]")*/
            throw e
        }
    }

    @Test // -1, -1  OK       OK
    fun testNegLDivNegSBytes(): Unit = withLogicR(1) {
        val length = Random.nextInt().absoluteValue.mod(60) + 8
        val dividend = bitGenBi(true, length)
        val divisor = bitGenBi(true, length - 4)

        val mockDividend = Mockito.mock(BigInt::class.java)
        Mockito.`when`(mockDividend.sigNum).thenReturn(dividend.sigNum)
        Mockito.`when`(mockDividend.mag).thenReturn(dividend.mag)

        val mockDivisor = Mockito.mock(BigInt::class.java)
        Mockito.`when`(mockDivisor.sigNum).thenReturn(divisor.sigNum)
        Mockito.`when`(mockDivisor.mag).thenReturn(divisor.mag)

        try {
            val r1 = mockDividend.divideAndRemainder(mockDivisor)
            val r2 = JavaBigInteger(dividend.toByteArray()).divideAndRemainder(JavaBigInteger(divisor.toByteArray()))

            //println("apa")
            assertContentEquals(r2[0].toByteArray(), r1.first.toByteArray())
            assertContentEquals(r2[1].toByteArray(), r1.second.toByteArray())
        } catch (e: java.lang.AssertionError) {
            println("Dividend: [${dividend.toByteArray().joinToString(", ")}]")
            println("Divisor: [${divisor.toByteArray().joinToString(", ")}]")
            /*println("Quot-Java: [${r1[0].toByteArray().joinToString(", ")}]")
            println("Quot-Kotl: [${r2.first.toByteArray().joinToString(", ")}]")
            println("Rem-Java: [${r1[1].toByteArray().joinToString(", ")}]")
            println("Rem-Kotl: [${r2.second.toByteArray().joinToString(", ")}]")*/
            throw e
        }
    }

    @Test // -1, -1  OK       OK
    fun testNegSDivNegLBytes(): Unit = withLogicR(1_000_000) {
        val length = Random.nextInt().absoluteValue.mod(60) + 8
        val dividend = bitGen(true, length - 4)
        val divisor = bitGen(true, length)

        try {
            val r1 = JavaBigInteger(dividend).divideAndRemainder(JavaBigInteger(divisor))
            val r2 = bigIntOf(dividend).divideAndRemainder(bigIntOf(divisor))

            assertContentEquals(r1[0].toByteArray(), r2.first.toByteArray())
            assertContentEquals(r1[1].toByteArray(), r2.second.toByteArray())
        } catch (e: java.lang.AssertionError) {
            println("Dividend: [${dividend.joinToString(", ")}] (${dividend.size})")
            println("Divisor: [${divisor.joinToString(", ")}] (${divisor.size})")
            throw e
        }
    }

    @Test //  1,  0  ERR      ERR
    fun testPosDivZeroBytes(): Unit = withLogicR(1_000_000) {
        val dividend = randPosLong().toLong()
        val divisor = zeroInt().toLong()

        assertFailsWith<ArithmeticException> {
            val r1 = JavaBigInteger.valueOf(dividend).divideAndRemainder(JavaBigInteger.valueOf(divisor))
        }
        assertFailsWith<BigMathException> {
            val r2 = bigIntOf(dividend).divideAndRemainder(bigIntOf(divisor))
        }
    }

    @Test // -1,  0  ERR      ERR
    fun testNegDivZeroBytes(): Unit = withLogicR(1_000_000) {
        val dividend = randNegLong().toLong()
        val divisor = zeroInt().toLong()

        assertFailsWith<ArithmeticException> {
            val r1 = JavaBigInteger.valueOf(dividend).divideAndRemainder(JavaBigInteger.valueOf(divisor))
        }
        assertFailsWith<BigMathException> {
            val r2 = bigIntOf(dividend).divideAndRemainder(bigIntOf(divisor))
        }
    }
}