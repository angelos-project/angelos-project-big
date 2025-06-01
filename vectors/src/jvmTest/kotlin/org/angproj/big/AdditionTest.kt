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

import org.angproj.aux.io.toBinary
import org.angproj.aux.io.toByteArray
import org.angproj.aux.mem.BufMgr
import org.angproj.aux.sec.SecureRandom
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertSame
import java.math.BigInteger as JavaBigInteger

class AdditionTest {
    /**
     * Generally fuzzes and validates that "public fun BigInt.add(value: BigMath<*>): BigInt" works
     * under all normal conditions. No special cases to test is currently known.
     * */
    @Test
    fun testAdd() = withLogic {
        Combinator.numberGenerator(-64..64) { x ->
            val xBi2 = bigIntOf(x.toBinary())
            val xJbi = JavaBigInteger(x)
            Combinator.innerNumberGenerator(-64..64) { y ->
                val yBi2 = bigIntOf(y)
                val yJbi = JavaBigInteger(y)

                Debugger.assertContentEquals(
                    x, y,
                    xJbi, yJbi,
                    xBi2, yBi2,
                    xJbi.add(yJbi),
                    xBi2.add(yBi2), // <- Emulation
                )
            }
        }
    }

    /**
     * Kotlin specific mimic of extension used for Java BigInteger.
     * */
    @Test
    fun testPlus() = withLogic {
        Combinator.numberGenerator(-64..64) { x ->
            val xBi2 = bigIntOf(x.toBinary())
            val xJbi = JavaBigInteger(x)
            Combinator.innerNumberGenerator(-64..64) { y ->
                val yBi2 = bigIntOf(y)
                val yJbi = JavaBigInteger(y)

                Debugger.assertContentEquals(
                    x, y,
                    xJbi, yJbi,
                    xBi2, yBi2,
                    xJbi.add(yJbi),
                    xBi2 + yBi2, // <- Kotlin specific
                )
            }
        }
    }

    /**
     * Kotlin specific mimic of extension used for Java BigInteger.
     * */
    @Test
    fun testInc() = withLogic {
        Combinator.numberGenerator(-64..64) { x ->
            var xBi2 = bigIntOf(x.toBinary())
            val xJbi = JavaBigInteger(x)

            Debugger.assertContentEquals(
                x,
                xJbi,
                xBi2,
                xJbi.add(JavaBigInteger.ONE),
                ++xBi2, // <- Kotlin specific
            )
        }
    }

    /**
     * Validates that zero + value returns the value, and is the same using Java BigInteger.
     * */
    @Test
    fun testZeroWithValue() = withLogic {
        val x = BufMgr.bin(13).apply{ SecureRandom.read(this) }.toByteArray()
        val xBi2 = bigIntOf(x)
        val xJbi = JavaBigInteger(x)

        assertSame(BigInt.zero.add(xBi2), xBi2)
        assertContentEquals(
            BigInt.zero.add(xBi2).toByteArray(),
            JavaBigInteger.ZERO.add(xJbi).toByteArray()
        )
    }

    /**
     * Validates that value + zero returns the value, and is the same using Java BigInteger.
     * */
    @Test
    fun testValueWithZero() = withLogic {
        val x = BufMgr.bin(13).apply{ SecureRandom.read(this) }
        val xBi2 = bigIntOf(x)
        val xJbi = JavaBigInteger(x.toByteArray())

        assertSame(xBi2.add(BigInt.zero), xBi2)
        assertContentEquals(
            xBi2.add(BigInt.zero).toByteArray(),
            xJbi.add(JavaBigInteger.ZERO).toByteArray()
        )
    }
}