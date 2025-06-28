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
 * Acknowledgement of algorithm:
 *      Josh Bloch
 *      Michael McCloskey
 *      Alan Eliasen
 *      Timothy Buktu
 *
 * Contributors:
 *      Kristoffer Paulsson - Port to Kotlin and adaption to Angelos Project
 */
package org.angproj.big

import org.angproj.sec.util.TypeSize
import kotlin.math.max

/**
 * Clears the bit at the specified [pos] in this [BigInt].
 *
 * @param pos The position of the bit to clear.
 * @return A new [BigInt] with the specified bit cleared.
 */
public fun BigInt.clearBit(pos: Int): BigInt = BigInt.innerClearBit(this.mag, this.sigNum, pos).valueOf()


public fun BigInt.Companion.innerClearBit(x: IntArray, xSig: BigSigned, pos: Int): IntArray {
    ensureThat<BigMathException>(pos >= 0) { "Can not flip an imaginary bit at a negative position." }

    val bigCnt = pos.floorDiv(TypeSize.intBits)
    val result = IntArray(max(x.intLength(xSig), (pos + 1).floorDiv(TypeSize.intBits) + 1))
    val xnz = x.firstNonzero()

    result.indices.forEach { result.intSet(it, x.intGetComp(it, xSig, xnz)) }
    result.intSet(bigCnt, result.intGet(bigCnt) and (1 shl (pos and 31)).inv())

    return result
}