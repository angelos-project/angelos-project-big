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

import org.angproj.big.newbig.ExportImportBigInt


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