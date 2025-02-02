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
 * Contributors:
 *      Kristoffer Paulsson - initial implementation
 */
package org.angproj.big

import org.angproj.aux.io.TypeBits

public operator fun BigInt.times(other: BigInt): BigInt = multiply(other)

public fun BigInt.multiply(value: BigInt): BigInt = when {
    sigNum.isZero() || value.sigNum.isZero() -> BigInt.zero
    else -> biggerFirst(this, value) { big, little ->
        val negative = big.sigNum.isNegative().let { if (little.sigNum.isNegative()) !it else it }
        val product = BigInt.innerMultiply(
            big.abs(),
            little.abs()
        )
        val result = BigInt(product.mag.copyOf(), BigSigned.POSITIVE)
        return@biggerFirst if (negative) result.negate() else result
    }
}

internal fun BigInt.Companion.innerMultiply(x: BigInt, y: BigInt): BigInt = withLogic {
    val result = emptyBigIntOf(IntArray(x.mag.size + y.mag.size))

    result.mag.revSet(x.mag.size, innerMultiply1(result, x, getIdx(y, 0)))
    (1 until y.mag.size).forEach { idy ->
        val num = getIdxL(y, idy)
        var carry: Long = 0
        x.mag.indices.forEach { idx ->
            carry += getIdxL(x, idx) * num + getIdxL(result, idy + idx)
            setIdxL(result.mag, idy + idx, carry)
            carry = carry ushr TypeBits.int
        }
        setIdxL(result.mag, idy + x.mag.size, carry)
    }
    return@withLogic result
}

internal fun BigInt.Companion.innerMultiply1(result: BigInt, x: BigInt, y: Int): Int = withLogic{
    val first = y.getL()
    var carry: Long = 0
    x.mag.indices.forEach { idx ->
        carry += getIdxL(x, idx) * first
        setIdxL(result.mag, idx, carry)
        carry = carry ushr TypeBits.int
    }
    return@withLogic carry.toInt()
}