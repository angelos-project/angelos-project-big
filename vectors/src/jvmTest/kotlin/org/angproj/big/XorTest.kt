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

import org.angproj.sec.SecureRandom
import org.mockito.Mockito
import kotlin.test.Test
import kotlin.test.assertContentEquals
import java.math.BigInteger as JavaBigInteger

class XorTest {

    fun bitGen(negative: Boolean, length: Int): ByteArray {
        val pattern: Byte = 127
        return ByteArray(length) { pattern }.also { it[0] = if(negative) -1 else 1 }
    }

    fun bitGenBi(bitLen: Int): BigInt = when(bitLen < 0) {
        true -> BigInt.createRandomInRange(BigInt.minusOne.shiftLeft(bitLen), BigInt.minusOne.shiftLeft(bitLen-1))
        else -> BigInt.createRandomInRange(BigInt.one.shiftLeft(bitLen-1), BigInt.one.shiftLeft(bitLen))
    }

    /**
     * Generally fuzzes and validates that "public infix fun BigInt.xor(value: BigMath<*>): BigInt" works
     * under all normal conditions. No special cases to test is currently known.
     * */
    @Test
    fun testXor() = repeat(1) {
        val first = bitGenBi(SecureRandom.readInt().mod(64*8))
        val second = bitGenBi(SecureRandom.readInt().mod(64*8))

        val mockFirst = Mockito.mock(BigInt::class.java)
        Mockito.`when`(mockFirst.sigNum).thenReturn(first.sigNum)
        Mockito.`when`(mockFirst.mag).thenReturn(first.mag)

        val mockSecond = Mockito.mock(BigInt::class.java)
        Mockito.`when`(mockSecond.sigNum).thenReturn(second.sigNum)
        Mockito.`when`(mockSecond.mag).thenReturn(second.mag)

        try {
            val r1 = mockFirst.xor(mockSecond)
            val r2 = JavaBigInteger(first.toByteArray()).xor(JavaBigInteger(second.toByteArray()))

            assertContentEquals(r1.toByteArray(), r2.toByteArray())
        } catch (e: java.lang.AssertionError) {
            println("First: [${first.toByteArray().joinToString(", ")}]")
            println("Second: [${second.toByteArray().joinToString(", ")}]")
            throw e
        }
    }
    /**
     * Kotlin specific mimic of extension used for Java BigInteger.
     * */
    @Test
    fun testXorInfix() {
        Combinator.numberGenerator(-64..64) { x ->
            val xBi2 = bigIntOf(x)
            val xJbi = JavaBigInteger(x)
            Combinator.numberGenerator(-64..64) { y ->
                val yBi2 = bigIntOf(y)
                val yJbi = JavaBigInteger(y)

                Debugger.assertContentEquals(
                    x, y,
                    xJbi, yJbi,
                    xBi2, yBi2,
                    xJbi.xor(yJbi),
                    xBi2 xor yBi2 // <- Kotlin specific
                )
            }
        }
    }
}