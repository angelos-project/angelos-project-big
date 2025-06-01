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

import org.angproj.big.newbig.ExportImportBigInt


public operator fun BigInt.compareTo(other: BigInt): Int = compareSpecial(other).state

public fun BigInt.compareSpecial(other: BigInt): BigCompare = when {
    sigNum.state > other.sigNum.state -> BigCompare.GREATER
    sigNum.state < other.sigNum.state -> BigCompare.LESSER
    sigNum == BigSigned.POSITIVE -> BigInt.innerCompareMagnitude(this.mag, other.mag)
    sigNum == BigSigned.NEGATIVE -> BigInt.innerCompareMagnitude(other.mag, this.mag)
    else -> BigCompare.EQUAL
}

public fun BigInt.Companion.innerCompareMagnitude(left: IntArray, right: IntArray): BigCompare = when {
    left.size < right.size -> BigCompare.LESSER
    left.size > right.size -> BigCompare.GREATER
    else -> {
        left.indices.forEach { idx ->
            val xNum = left[idx]
            val yNum = right[idx]
            if (xNum != yNum) return when {
                xNum xor -0x80000000 < yNum xor -0x80000000 -> BigCompare.LESSER
                else -> BigCompare.GREATER
            }
        }
        BigCompare.EQUAL
    }
}


internal fun BigInt.Companion.innerCompareMagnitude0(left: BigInt, right: BigInt): BigCompare = when {
    left.mag.size < right.mag.size -> BigCompare.LESSER
    left.mag.size > right.mag.size -> BigCompare.GREATER
    else -> {
        left.mag.indices.forEach { idx ->
            val xNum = left.mag[idx] // Should NOT use getL()
            val yNum = right.mag[idx]
            if (xNum != yNum) return@innerCompareMagnitude0 if (xNum xor -0x80000000 < yNum xor -0x80000000
            ) BigCompare.LESSER else BigCompare.GREATER
        }
        BigCompare.EQUAL
    }
}

public operator fun BigInt.unaryMinus(): BigInt = negate()

public fun BigInt.negate0(): BigInt = BigInt.raw<Unit>(mag, sigNum.negate())
public fun BigInt.negate(): BigInt = ExportImportBigInt.internalOf(mag, sigNum.negate())


public fun BigInt.abs(): BigInt = when (sigNum) {
    BigSigned.NEGATIVE -> negate()
    else -> this
}

public fun BigInt.min(value: BigInt): BigInt = when {
    this < value -> this
    else -> value
}

public fun BigInt.max(value: BigInt): BigInt = when {
    this > value -> this
    else -> value
}
