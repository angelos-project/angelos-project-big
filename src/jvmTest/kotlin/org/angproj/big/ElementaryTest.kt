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
import java.math.BigInteger as JavaBigInteger

class ElementaryTest {

    /**
     * Those are values that give indifferences compared to Java BigInteger
     * The old BigInt reproduces these errors.
     *
     * Vx: 00a77c6825bf87b2f5
     * Jx: 00a77c6825bf87b2f5
     * Kx: 00a77c6825bf87b2f5
     * Rj: 00cf10f1cb
     * Rk: 00
     *
     * Vx: 00a54c5d82096297ea
     * Jx: 00a54c5d82096297ea
     * Kx: 00a54c5d82096297ea
     * Rj: 00cdb59c83
     * Rk: 00
     *
     * Vx: 009d37b67a9fbaaae61d1795a1b93b6029
     * Jx: 009d37b67a9fbaaae61d1795a1b93b6029
     * Kx: 009d37b67a9fbaaae61d1795a1b93b6029
     * Rj: 00c89e4aa04535e14e
     * Rk: 00c89e4a9fe0e9f980
     *
     * Vx: 00d7498c81a0c0d057
     * Jx: 00d7498c81a0c0d057
     * Kx: 00d7498c81a0c0d057
     * Rj: 00eac34397
     * Rk: 00
     * */

    /**
     * Partial error found
     * */
    //@Test
    fun testSqrt() {
        Combinator.numberGenerator(0..64) { x ->
            val xBi2 = bigIntOf(x)
            val xJbi = JavaBigInteger(x)

            Debugger.assertContentEquals(
                x,
                xJbi,
                xBi2,
                xJbi.sqrt(),
                xBi2.sqrt() // <- Emulation
            )
        }
    }
}