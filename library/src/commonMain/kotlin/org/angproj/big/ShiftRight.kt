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
 *      Per Bothner
 *      Josh Bloch
 *      Michael McCloskey
 *      Alan Eliasen
 *      Timothy Buktu
 *
 * Contributors:
 *      Kristoffer Paulsson - Port to Kotlin and adaption to Angelos Project
 */
package org.angproj.big

import org.angproj.aux.io.TypeBits
import org.angproj.big.newbig.ExportImportBigInt


public infix fun BigInt.shr(n: Int): BigInt = shiftRight(n)

public fun BigInt.shiftRight(n: Int): BigInt = when {
    sigNum.isZero() -> BigInt.zero
    n > 0 -> BigInt.innerShiftRight(n, this)
    n == 0 -> this
    else -> BigInt.innerShiftLeft(-n, this)
}


public fun BigInt.Companion.innerShiftRight(n: Int, x: BigInt): BigInt {
    val nInts = n ushr 5
    val nBits = n and 0x1f
    val mag = x.mag
    val magLen: Int = mag.size
    var newMag: IntArray

    if (nInts >= magLen) return (if (x.sigNum.isNonNegative()) zero else minusOne)

    val newMagLen = magLen - nInts
    val newMagLast = newMagLen - 1
    val nBitsInv = 32 - nBits
    if (nBits == 0) {
        newMag = x.mag.copyOf(newMagLen)
    } else {
        val highBits: Int = mag[0] ushr nBits
        val extra = if(highBits != 0) 1 else 0
        newMag = IntArray(newMagLast + extra)
        if(extra == 1) newMag[0] = highBits
        var idx = newMagLast
        (newMag.lastIndex downTo extra).forEach {
            newMag[it] = (mag[idx--] ushr nBits) or (mag[idx] shl nBitsInv)
        }
    }

    if (x.sigNum.isNegative()) {
        var onesLost = false
        var i = magLen - 1
        val j = newMagLen
        while (i >= j && !onesLost) { onesLost = mag[i--] != 0 }
        if (!onesLost && nBits != 0) onesLost = (mag[newMagLast] shl nBitsInv != 0)
        if (onesLost) {
            onesLost = false
            // DO NOT USE newMagLen OR newMagLast BELOW HERE
            var k = newMag.lastIndex
            while (k >= 0 && !onesLost) { newMag[k] += 1; onesLost = newMag[k--] != 0 }
            if (!onesLost) newMag = IntArray(newMag.size + 1).also { it[0] = 1 }
        }
    }

    return ExportImportBigInt.internalOf(newMag, x.sigNum)
}


public fun BigInt.shiftRight0(n: Int): BigInt = when {
    sigNum.isZero() -> BigInt.zero
    n > 0 -> shiftRightBits0(n)
    n == 0 -> this
    else -> BigInt.raw<Unit>(BigInt.innerShiftLeftBits(mag, -n), sigNum)
}

internal fun BigInt.shiftRightBits0(count: Int): BigInt = withLogic {
    val bigShift: Int = count.floorDiv(TypeBits.int)
    val tinyShift: Int = count.mod(TypeBits.int)
    val tinyShiftOpposite = TypeBits.int - tinyShift

    if (bigShift >= mag.size) return@withLogic if (sigNum.isNonNegative()) BigInt.zero else BigInt.minusOne

    val result = when(tinyShift) {
        0 -> mag.copyOf(mag.size - bigShift)
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
        if (onesLost) (result.lastIndex downTo 0).indexOfFirst {
                result[it] += 1
                result[it] != 0
            }.takeIf { it == -1 }?.let {
                return@withLogic BigInt.raw<Unit>((intArrayOf(1) + IntArray(result.size)), sigNum) }
    }

    return@withLogic BigInt.raw<Unit>(result, sigNumZeroAdjust(result, sigNum))
}