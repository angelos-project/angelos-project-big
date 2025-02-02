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

import org.angproj.aux.io.TypeBits

public infix fun BigInt.shr(n: Int): BigInt = shiftRight(n)

public fun BigInt.shiftRight(n: Int): BigInt = when {
    sigNum.isZero() -> BigInt.zero
    n > 0 -> asMutableBigInt().shiftRightBits(n)
    n == 0 -> this
    else -> BigInt(BigInt.innerShiftLeftBits(mag, -n).toList(), sigNum)
}

internal fun MutableBigInt.shiftRightBits(count: Int): BigInt = withLogic {
    val bigShift: Int = count.floorDiv(TypeBits.int)
    val tinyShift: Int = count.mod(TypeBits.int)
    val tinyShiftOpposite = TypeBits.int - tinyShift

    if (bigShift >= mag.size) return@withLogic if (sigNum.isNonNegative()) BigInt.zero else BigInt.minusOne

    val result = when(tinyShift) {
        0 -> mag.toIntArray().copyOf(mag.size - bigShift)
        else -> {
            val highBits = mag[0] ushr tinyShift
            val extra = if(highBits == 0) 0 else 1
            val remove = if (extra == 1) 0 else 1
            val result = when (highBits) {
                0 -> IntArray(mag.lastIndex - bigShift)
                else -> IntArray(mag.size - bigShift).also { it[0] = highBits }
            }
            (mag.lastIndex - bigShift - remove downTo extra).forEach {
                val idx = it + remove
                result[it] = mag[idx] ushr tinyShift or (mag[idx - 1] shl tinyShiftOpposite)
            }
            result
        }
    }

    if (sigNum.isNegative()) {
        var onesLost = (mag.lastIndex downTo mag.size - bigShift).all { mag[it] == 0 }.not()
        if (!onesLost && tinyShift != 0) onesLost = mag[mag.size - bigShift - 1] shl TypeBits.int - tinyShift != 0
        if (onesLost) {
            (result.lastIndex downTo 0).indexOfFirst {
                result[it] += 1
                result[it] != 0
            }.takeIf { it == -1 }?.let {
                return@withLogic BigInt((intArrayOf(1) + IntArray(result.size)).toList(), sigNum) }
        }
    }

    return@withLogic BigInt(result.toList(), sigNumZeroAdjust(result, sigNum))
}