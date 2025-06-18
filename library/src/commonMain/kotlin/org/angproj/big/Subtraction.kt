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

import kotlin.math.max


/**
 * Decrements this BigInt by one.
 *
 * @return a new BigInt that is one less than this BigInt.
 */
public operator fun BigInt.dec(): BigInt = subtract(BigInt.one)

/**
 * Subtracts the specified BigInt from this BigInt.
 *
 * @param other the BigInt to subtract from this BigInt.
 * @return a new BigInt that is the result of subtracting the specified BigInt from this BigInt.
 */
public operator fun BigInt.minus(other: BigInt): BigInt = this.subtract(other)

/**
 * Subtracts the specified BigInt from this BigInt.
 *
 * @param value the BigInt to subtract from this BigInt.
 * @return a new BigInt that is the result of subtracting the specified BigInt from this BigInt.
 */
public fun BigInt.subtract(value: BigInt): BigInt = when {
    value.sigNum.isZero() -> this
    sigNum.isZero() -> value.negate()
    else -> ExportImportBigInt.internalOf(BigInt.innerSubtract(this.mag, this.sigNum, value.mag, value.sigNum))
}

public fun BigInt.Companion.innerSubtract(
    x: IntArray, xSig: BigSigned, y: IntArray, ySig: BigSigned
): IntArray {
    val xnz = x.firstNonzero()
    val ynz = y.firstNonzero()
    val result = IntArray(max(x.size, y.size) + 1)
    var carry = 0

    result.indices.forEach { idr ->
        var yNum = y.intGetComp(idr, ySig, ynz) + carry
        val xNum = x.intGetComp(idr, xSig, xnz)
        carry = if (yNum xor -0x80000000 < carry xor -0x80000000) 1 else 0
        yNum = xNum - yNum
        carry += if (yNum xor -0x80000000 > xNum xor -0x80000000) 1 else 0
        result.intSet(idr, yNum)
    }

    return result
}