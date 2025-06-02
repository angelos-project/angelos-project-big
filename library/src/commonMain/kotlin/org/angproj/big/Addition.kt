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

public fun BigInt.Companion.innerAdd(
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