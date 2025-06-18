/**
 * Copyright (c) 2023-2025 by Kristoffer Paulsson <kristoffer.paulsson@talenten.se>.
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
 *      Kristoffer Paulsson - adaption to Angelos Project
 */
package org.angproj.big

/**
 * Tests whether the bit at the specified position is set.
 *
 * @param pos the position of the bit to test, where 0 is the least significant bit.
 * @return `true` if the bit at the specified position is set, `false` otherwise.
 * @throws BigMathException if `pos` is negative.
 * */
public fun BigInt.testBit(pos: Int): Boolean = BigInt.innerTestBit(this.mag, this.sigNum, pos)

public fun BigInt.Companion.innerTestBit(x: IntArray, xSig: BigSigned, pos: Int): Boolean {
    require(pos >= 0) { throw BigMathException("Can not flip an imaginary bit at a negative position.") }

    val xnz = x.firstNonzero()
    return x.intGetComp(pos.floorDiv(32), xSig, xnz) and (1 shl (pos and 31)) != 0
}