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

public operator fun BigInt.plus(other: BigInt): BigInt = add(other)

public operator fun BigInt.inc(): BigInt = add(BigInt.one)

public fun BigInt.add(value: BigMath<*>): BigInt = when {
    sigNum.isZero() -> value.toBigInt()
    value.sigNum.isZero() -> this
    else -> {
        val out = biggerFirst(this, value) { big, little ->
            return@biggerFirst BigInt.innerAdd(big, little)
        }
        ofIntArray(out.mag.toIntArray())
    }
}

internal fun BigInt.Companion.innerAdd(x: BigMath<*>, y: BigMath<*>): MutableBigInt = withLogic {
    val result = MutableBigInt.emptyMutableBigIntOf(IntArray(x.mag.size + 1))
    var carry: Long = 0

    result.mag.indices.forEach { idx ->
        carry += getIdxL(x, idx) + getIdxL(y, idx)
        setIdxL(result.mag, idx, carry)
        carry = carry ushr TypeBits.int
    }
    return@withLogic result
}