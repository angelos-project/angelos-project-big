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
 *
 * Contributors:
 *      Kristoffer Paulsson - Port to Kotlin and adaption to Angelos Project
 */
package org.angproj.big

import org.angproj.aux.io.TypeBits
import org.angproj.big.newbig.*


public operator fun BigInt.plus(other: BigInt): BigInt = add(other)

public operator fun BigInt.inc(): BigInt = add(BigInt.one)

public fun BigInt.add(value: BigInt): BigInt = when {
    sigNum.isZero() -> value
    value.sigNum.isZero() -> this
    else -> biggerFirst(this, value) { big, little ->
        ExportImportBigInt.internalOf(
            BigInt.innerAdd(
                big.mag, big.sigNum,
                little.mag, little.sigNum
            )
        )
    }
}

internal fun BigInt.Companion.innerAdd(
    x: IntArray, xSig: BigSigned, y: IntArray, ySig: BigSigned
): IntArray {
    val xnz = x.firstNonzero()
    val ynz = y.firstNonzero()
    val result = IntArray(x.size + 1)
    var carry: Long = 0

    result.indices.forEach { idx ->
        carry +=
            x.intGetComp(idx, xSig, xnz).longMask() +
                    y.intGetComp(idx, ySig, ynz).longMask()
        result.longSet(idx, carry)
        carry = carry ushr TypeBits.int
    }

    return result
}

internal fun BigInt.Companion.innerAdd1(
    x: IntArray, xSig: BigSigned, y: IntArray, ySig: BigSigned
): IntArray = withLogic {
    val xnz = x.firstNonzero()
    val ynz = y.firstNonzero()
    val result = IntArray(x.size + 1)
    var carry: Long = 0

    result.indices.forEach { idx ->
        carry += (
                LoadAndSaveBigInt.getIntNew(idx, x, xSig, xnz).toLong() and LONG_MASK) + (
                LoadAndSaveBigInt.getIntNew(idx, y, ySig, ynz).toLong() and LONG_MASK)
        setIdxL(result, idx, carry)
        carry = carry ushr TypeBits.int
    }

    return@withLogic result
}

public fun BigInt.add0(value: BigInt): BigInt = when {
    sigNum.isZero() -> value
    value.sigNum.isZero() -> this
    else -> {
        val out = biggerFirst(this, value) { big, little ->
            return@biggerFirst BigInt.innerAdd0(big, little)
        }
        fromIntArray(out.mag)
    }
}

internal fun BigInt.Companion.innerAdd0(x: BigInt, y: BigInt): BigInt = withLogic {
    val result = emptyBigIntOf(IntArray(x.mag.size + 1))
    var carry: Long = 0

    result.mag.indices.forEach { idx ->
        carry += getIdxL(x, idx) + getIdxL(y, idx)
        setIdxL(result.mag, idx, carry)
        carry = carry ushr TypeBits.int
    }

    return@withLogic result
}

/*private fun rightShift(n: Int, value: IntArray) {
    if (intLen === 0) return
    val nInts = n ushr 5
    val nBits = n and 0x1F
    this.intLen -= nInts
    if (nBits == 0) return
    val bitsInHighWord: Int = LoadAndSaveBigInt.bitLengthForInt(value[offset])
    if (nBits >= bitsInHighWord) {
        this.primitiveLeftShift(32 - nBits)
        this.intLen--
    } else {
        primitiveRightShift(nBits)
    }
}*/