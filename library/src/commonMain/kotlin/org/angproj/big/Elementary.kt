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

import kotlin.math.ceil
import kotlin.math.sqrt

/**
 * Not ready to be used
 * Check unittest for a brief explanation.
 * */
internal fun BigInt.sqrt(): BigInt = when {
    sigNum.isNegative() -> error("Can not calculate the square root of a negative value.")
    sigNum.isZero() -> BigInt.zero
    bitLength <= Long.SIZE_BITS -> fromLong(sqrt(toLong().toDouble()).toLong())
    else -> BigInt.squareRoot(this)
}

internal fun BigInt.Companion.squareRoot(value: BigInt): BigInt {
    var dividend = value
    var root = BigInt.zero
    var mask = bitMask(dividend.bitLength)
    val size = when(value.bitLength > 127) {
        true -> 63
        else -> 31
    }

    while(dividend.bitLength >= size) {
        var shift = dividend.bitLength - size
        if (shift % 2 == 1) shift++
        mask = mask.shiftRight(size)
        val sqrt = fromLong(ceil(sqrt(dividend.shiftRight(shift).toLong().toDouble())).toLong())
        root += sqrt.shiftLeft(shift / 2 - 1) * BigInt.two
        dividend = dividend.and(mask)
    }

    while(true) {
        val corr = value.divide(root).add(root).shiftRight(1)
        if (corr.compareSpecial(root).isGreaterOrEqual()) break
        root = corr
    }
    return root
}

internal fun BigInt.Companion.bitMask(bitCount: Int): BigInt {
    return when {
        bitCount <= 0 -> BigInt.zero
        bitCount == 1 -> BigInt.one
        else -> BigInt.one.shiftLeft(bitCount).add(BigInt.minusOne)
    }
}