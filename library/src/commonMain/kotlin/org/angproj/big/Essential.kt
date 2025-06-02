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

/**
 * Compares this BigInt with another BigInt.
 *
 * @param other the BigInt to compare with.
 * @return a negative integer, zero, or a positive integer as this BigInt is less than, equal to, or greater than the specified BigInt.
 */
public operator fun BigInt.compareTo(other: BigInt): Int = compareSpecial(other).state

/**
 * Compares this BigInt with another BigInt.
 *
 * @param other the BigInt to compare with.
 * @return a [BigCompare] indicating whether this BigInt is less than, equal to, or greater than the specified BigInt.
 */
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

/**
 * Returns a new BigInt that is the negation of this BigInt.
 *
 * If this BigInt is positive, it returns a new BigInt with the same magnitude but negative sign.
 * If it is negative, it returns a new BigInt with the same magnitude but positive sign.
 * If it is zero, it returns zero.
 *
 * @return a new BigInt that is the negation of this BigInt.
 */
public operator fun BigInt.unaryMinus(): BigInt = negate()

/**
 * Returns a new BigInt that is the negation of this BigInt.
 *
 * If this BigInt is positive, it returns a new BigInt with the same magnitude but negative sign.
 * If it is negative, it returns a new BigInt with the same magnitude but positive sign.
 * If it is zero, it returns zero.
 *
 * @return a new BigInt that is the negation of this BigInt.
 */
public fun BigInt.negate(): BigInt = ExportImportBigInt.internalOf(mag, sigNum.negate())

/**
 * Returns the absolute value of this BigInt.
 *
 * If this BigInt is negative, it returns a new BigInt with the same magnitude but positive sign.
 * If it is already positive or zero, it returns this BigInt unchanged.
 *
 * @return the absolute value of this BigInt.
 */
public fun BigInt.abs(): BigInt = when (sigNum) {
    BigSigned.NEGATIVE -> negate()
    else -> this
}

/**
 * Returns the minimum of this BigInt and the specified BigInt.
 *
 * @param value the BigInt to compare with.
 * @return the smaller of this BigInt and the specified BigInt.
 */
public fun BigInt.min(value: BigInt): BigInt = when {
    this < value -> this
    else -> value
}

/**
 * Returns the maximum of this BigInt and the specified BigInt.
 *
 * @param value the BigInt to compare with.
 * @return the larger of this BigInt and the specified BigInt.
 */
public fun BigInt.max(value: BigInt): BigInt = when {
    this > value -> this
    else -> value
}
