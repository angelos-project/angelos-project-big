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
import kotlin.test.Test
import java.math.BigInteger as JavaBigInteger

class NotTest {
    /**
     * Generally fuzzes and validates that "public fun BigInt.not(): BigInt" works
     * under all normal conditions. No special cases to test is currently known.
     * */
    @Test
    fun testNot() = withLogic {
        Combinator.numberGenerator(-64..64) { x ->
            val xBi2 = bigIntOf(x.toBinary())
            val xJbi = JavaBigInteger(x)

            Debugger.assertContentEquals(
                x,
                xJbi,
                xBi2,
                xJbi.not(),
                xBi2.not() // <- Emulation
            )
        }
    }

    /**
     * Kotlin specific mimic of extension used for Java BigInteger.
     * */
    @Test
    fun testInv() = withLogic {
        Combinator.numberGenerator(-64..64) { x ->
            val xBi2 = bigIntOf(x.toBinary())
            val xJbi = JavaBigInteger(x)

            Debugger.assertContentEquals(
                x,
                xJbi,
                xBi2,
                xJbi.not(),
                xBi2.inv() // <- Kotlin specific
            )
        }
    }
}