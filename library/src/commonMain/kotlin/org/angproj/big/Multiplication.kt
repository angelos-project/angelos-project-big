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

import org.angproj.sec.util.TypeSize

/**
 * Multiplies two BigInt values together.
 *
 * @param other the BigInt to multiply with this BigInt.
 * @return a new BigInt that is the product of this BigInt and the specified BigInt.
 */
public operator fun BigInt.times(other: BigInt): BigInt = multiply(other)

/**
 * Multiplies two BigInt values together.
 *
 * @param value the BigInt to multiply with this BigInt.
 * @return a new BigInt that is the product of this BigInt and the specified BigInt.
 */
public fun BigInt.multiply(value: BigInt): BigInt = when {
    sigNum.isZero() || value.sigNum.isZero() -> BigInt.zero
    else -> ExportImportBigInt.internalOf(
        ExportImportBigInt.trustedStripLeadingZeroInts(BigInt.innerMultiply(this.mag, value.mag)),
        if (this.sigNum == value.sigNum) BigSigned.POSITIVE else BigSigned.NEGATIVE
    )
}

public fun BigInt.Companion.innerMultiply(x: IntArray, y: IntArray): IntArray {
    val xRev = x.rev(0)
    val yRev = y.rev(0)

    val z = BigInt.innerMultiplication(x, y)
    (xRev - 1 downTo 0).forEach { i ->
        var carry: Long = 0
        (yRev downTo 0).forEach { j ->
            val k = j + i + 1
            (y[j].longMask() * x[i].longMask() + z[k].longMask() + carry).also {
                z[k] = it.toInt()
                carry = it ushr TypeSize.intBits
            }
        }
        z[i] = carry.toInt()
    }

    return z
}

public fun BigInt.Companion.innerMultiplication(x: IntArray, y: IntArray): IntArray {
    val xRev = x.rev(0)

    val z = IntArray(x.size + y.size)
    val k = 1 + xRev
    var carry: Long = 0
    (y.lastIndex downTo 0).forEach { j ->
        (y[j].longMask() * x[xRev].longMask() + carry).also {
            z[j + k] = it.toInt()
            carry = it ushr TypeSize.intBits
        }
    }
    z[xRev] = carry.toInt()

    return z
}