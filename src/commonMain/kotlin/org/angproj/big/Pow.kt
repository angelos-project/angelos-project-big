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

public fun BigInt.pow(exponent: Int): BigInt = when {
    exponent < 0 -> error { BigMathException("Exponent can not be negative") }
    exponent == 0 -> BigInt.one
    exponent == 1 -> this
    sigNum.isZero() -> this
    else -> {
        val size = (bitCount * exponent shr 5) + 2 * mag.size
        check(size < Int.MAX_VALUE) { BigMathException("Exponent is so large so there is to little memory left.") }
        BigInt.innerPower(this, exponent)
    }
}

internal fun BigInt.Companion.innerPower(base: BigInt, exponent: Int): BigInt = withLogic {
    var rest = exponent
    var total = BigInt.zero
    while(rest > 1) {
        var square = base
        val pow2 = bitSizeForInt(rest)
        rest = (rest and (1 shl pow2-1).inv())
        (0 until pow2-1).forEach { _ -> square = square.multiply(square) }
        total = when(total.sigNum.isZero()) {
            false -> (total.multiply(square) )
            else -> square
        }
    }
    if(rest == 1) total = (total * base)
    return@withLogic total
}