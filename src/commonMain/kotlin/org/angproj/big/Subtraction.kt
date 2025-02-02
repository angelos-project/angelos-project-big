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

public operator fun BigInt.dec(): BigInt = subtract(BigInt.one)

public operator fun BigInt.minus(other: BigInt): BigInt = this.subtract(other)

public fun BigInt.subtract(value: BigMath<*>): BigInt = when {
    value.sigNum.isZero() -> this
    sigNum.isZero() -> value.toBigInt().negate()
    else -> {
        val out = BigInt.innerSubtract(this, value)
        ofIntArray(out.mag.toIntArray())
    }
}

internal fun BigInt.Companion.innerSubtract(x: BigMath<*>, y: BigMath<*>): MutableBigInt = withLogic{
    val result = MutableBigInt.emptyMutableBigIntOf(maxOfArrays(x.mag, y.mag))
    var carry = 0

    result.mag.indices.forEach { idr ->
        var yNum = getIdx(y, idr) + carry
        val xNum = getIdx(x, idr)
        carry = if (yNum xor -0x80000000 < carry xor -0x80000000) 1 else 0
        yNum = xNum - yNum
        carry += if (yNum xor -0x80000000 > xNum xor -0x80000000) 1 else 0
        result.mag.revSet(idr, yNum)
    }
    return@withLogic result
}