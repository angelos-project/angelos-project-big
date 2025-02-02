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

public operator fun BigInt.compareTo(other: BigMath<*>): Int = compareSpecial(other).state

public fun BigInt.compareSpecial(other: BigMath<*>): BigCompare = when {
    sigNum.state > other.sigNum.state -> BigCompare.GREATER
    sigNum.state < other.sigNum.state -> BigCompare.LESSER
    sigNum == BigSigned.POSITIVE -> BigInt.innerCompareMagnitude(this, other)
    sigNum == BigSigned.NEGATIVE -> BigInt.innerCompareMagnitude(other, this)
    else -> BigCompare.EQUAL
}

internal fun BigInt.Companion.innerCompareMagnitude(left: BigMath<*>, right: BigMath<*>): BigCompare = when {
    left.mag.size < right.mag.size -> BigCompare.LESSER
    left.mag.size > right.mag.size -> BigCompare.GREATER
    else -> {
        left.mag.indices.forEach { idx ->
            val xNum = left.mag[idx] // Should NOT use getL()
            val yNum = right.mag[idx]
            if (xNum != yNum) return@innerCompareMagnitude if (xNum xor -0x80000000 < yNum xor -0x80000000
            ) BigCompare.LESSER else BigCompare.GREATER
        }
        BigCompare.EQUAL
    }
}

public operator fun BigInt.unaryMinus(): BigInt = negate()

public fun BigInt.negate(): BigInt = BigInt(mag, sigNum.negate())

public fun BigInt.abs(): BigInt = when (sigNum) {
    BigSigned.NEGATIVE -> negate()
    else -> this
}

public fun BigInt.min(value: BigInt): BigInt = if(this < value) this else value

public fun BigInt.max(value: BigInt): BigInt = if(this > value) this else value