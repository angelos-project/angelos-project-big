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

import org.angproj.aux.io.TypeBits
import org.angproj.big.newbig.ExportImportBigInt
import org.angproj.big.newbig.ShiftArithm


public infix fun BigInt.shl(n: Int): BigInt = shiftLeft(n)

public fun BigInt.shiftLeft(n: Int): BigInt = when {
    sigNum.isZero() -> BigInt.zero
    n > 0 -> BigInt.innerShiftLeft(n, this)
    n == 0 -> this
    else -> BigInt.innerShiftRight(-n, this)
}


public fun BigInt.Companion.innerShiftLeft(n: Int, x: BigInt): BigInt {
    val nInts = n ushr 5
    val nBits = n and 0x1f
    val mag = x.mag
    val magLen = mag.size
    val newMag: IntArray

    if (nBits == 0) {
        newMag = IntArray(magLen + nInts)
        mag.copyInto(newMag, 0, 0, magLen)
    } else {
        val nBitsRev = 32 - nBits
        val highBits = mag[0] ushr nBitsRev
        val extra = if(highBits != 0) 1 else 0
        newMag = IntArray(magLen + nInts + extra)
        if(extra == 1) newMag[0] = highBits
        val magLast = mag.lastIndex
        (0 until magLast).forEach {
            newMag[it + extra] = (mag[it] shl nBits) or (mag[it+1] ushr nBitsRev)
        }
        newMag[magLast + extra] = mag[magLast] shl nBits
    }

    return ExportImportBigInt.internalOf(newMag, x.sigNum)
}


public fun BigInt.shiftLeft0(n: Int): BigInt = when {
    sigNum.isZero() -> BigInt.zero
    n > 0 -> BigInt.raw<Unit>(BigInt.innerShiftLeftBits(mag, n), sigNum)
    n == 0 -> this
    else -> shiftRightBits0(-n)
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