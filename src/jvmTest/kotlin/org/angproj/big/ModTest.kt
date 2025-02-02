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

import org.angproj.aux.io.binOf
import org.angproj.aux.io.toBinary
import org.angproj.aux.io.toByteArray
import org.angproj.aux.sec.SecureRandom
import java.lang.ArithmeticException
import java.math.BigInteger
import kotlin.test.Test
import kotlin.test.assertFailsWith
import java.math.BigInteger as JavaBigInteger

class ModTest {
    /**
     * Generally fuzzes and validates that "public fun BigInt.mod(value: BigInt): BigInt" works
     * under all normal conditions.
     * */
    @Test
    fun testMod() {
        Combinator.numberGenerator(-64..64) { x ->
            val xBi2 = bigIntOf(x.toBinary())
            val xJbi = BigInteger(x)
            Combinator.innerNumberGenerator(1..64) { y ->
                val yBi2 = bigIntOf(y.toBinary())
                val yJbi = BigInteger(y)
                if(yJbi.equals(JavaBigInteger.ZERO)) return@innerNumberGenerator

                Debugger.assertContentEquals(
                    x, y,
                    xJbi, yJbi,
                    xBi2, yBi2,
                    xJbi.mod(yJbi),
                    xBi2.mod(yBi2) // <- Emulation
                )
            }
        }
    }

    /**
     * Validates that BigMathException is thrown if the modulus is zero or negative, likewise as Java BigInteger.
     * */
    @Test
    fun testModulusNotPositive() {
        val x = binOf(64).apply{ SecureRandom.read(this) }
        val xBi2 = bigIntOf(x)
        val xJbi = JavaBigInteger(x.toByteArray())

        assertFailsWith<BigMathException> { xBi2.mod(BigInt.zero) }
        assertFailsWith<ArithmeticException> { xJbi.mod(JavaBigInteger.ZERO) }
    }
}