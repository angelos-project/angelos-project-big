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
 *      Josh Bloch - earlier implementation
 *      Michael McCloskey - earlier implementation
 *      Alan Eliasen - earlier implementation
 *      Timothy Buktu - earlier implementation
 *      Kristoffer Paulsson - adaption to Angelos Project
 */
package org.angproj.big

import org.angproj.aux.io.TypeBits

public infix fun BigInt.shl(n: Int): BigInt = shiftLeft(n)

public fun BigInt.shiftLeft(n: Int): BigInt = when {
    sigNum.isZero() -> BigInt.zero
    n > 0 -> BigInt(BigInt.innerShiftLeftBits(mag, n), sigNum)
    n == 0 -> this
    else -> shiftRightBits(-n)
}

internal fun BigInt.Companion.innerShiftLeftBits(mag: IntArray, count: Int): IntArray {
    val bigShift = count.floorDiv(TypeBits.int)
    val tinyShift = count.mod(TypeBits.int)
    val tinyShiftOpposite = TypeBits.int - tinyShift

    return when(tinyShift) {
        0 -> IntArray(mag.size + bigShift).also {
            mag.copyInto(it, 0, 0, mag.size) }
        else -> {
            val extra = if (mag[0].countLeadingZeroBits() <= tinyShift) 1 else 0
            val result = IntArray(mag.size + bigShift + extra)

            (result.size - bigShift until result.size).forEach { result[it] = 0 }
            if (extra == 1) result[0] = mag.first() ushr tinyShiftOpposite
            (0 until mag.lastIndex).forEach {
                result[it + extra] = (mag[it] shl tinyShift) or (mag[it + 1] ushr tinyShiftOpposite)
            }
            result[result.lastIndex - bigShift] = mag.last() shl tinyShift
            result
        }
    }
}